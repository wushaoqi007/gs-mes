package com.greenstone.mes.common.security.feign;

import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.domain.R;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static feign.Util.UTF_8;
import static feign.Util.checkNotNull;

/**
 * @author gu_renkai
 * @date 2022/12/1 15:45
 */
@Slf4j
@Configuration
public class DecoderConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Decoder feignDecoder(ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        return new OptionalDecoder(new NormalDecoder(new SpringDecoder(messageConverters, customizers)));
    }

    public static class NormalDecoder implements Decoder {

        private final Decoder decoder;

        public NormalDecoder(Decoder decoder) {
            this.decoder = decoder;
        }

        @Override
        public Object decode(Response response, Type type) throws IOException, FeignException {
            if (type instanceof ParameterizedType pType && pType.getRawType() == R.class) {
                return this.decoder.decode(response, type);
            } else {
                Response.Body body = response.body();
                String bodyStr = Util.toString(body.asReader(UTF_8));
                R<?> r = JSONObject.parseObject(bodyStr, R.class);
                if (r == null) {
                    return JSONObject.parseObject(bodyStr, type);
                }
                if (r.getCode() == 500) {
                    throw new RuntimeException(response.request().url() + " " + r.getMsg());
                }
                if (r.getCode() == 0) {
                    return JSONObject.parseObject(bodyStr, type);
                }

                if (type == String.class) {
                    Response.Body newBody = new ByteArrayBody(((String) r.getData()).getBytes(StandardCharsets.UTF_8));
                    Response.Builder builder = response.toBuilder().body(newBody);
                    return this.decoder.decode(builder.build(), type);
                } else {
                    Response.Body newBody =
                            new ByteArrayBody(JSONObject.toJSONString(r.getData()).getBytes(StandardCharsets.UTF_8));
                    Response.Builder builder = response.toBuilder().body(newBody);
                    return this.decoder.decode(builder.build(), type);
                }
            }
        }
    }

    private static final class ByteArrayBody implements Response.Body {

        private final byte[] data;

        public ByteArrayBody(byte[] data) {
            this.data = data;
        }

        private static Response.Body orNull(byte[] data) {
            if (data == null) {
                return null;
            }
            return new ByteArrayBody(data);
        }

        private static Response.Body orNull(String text, Charset charset) {
            if (text == null) {
                return null;
            }
            checkNotNull(charset, "charset");
            return new ByteArrayBody(text.getBytes(charset));
        }

        @Override
        public Integer length() {
            return data.length;
        }

        @Override
        public boolean isRepeatable() {
            return true;
        }

        @Override
        public InputStream asInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        @SuppressWarnings("deprecation")
        @Override
        public Reader asReader() throws IOException {
            return new InputStreamReader(asInputStream(), UTF_8);
        }

        @Override
        public Reader asReader(Charset charset) throws IOException {
            checkNotNull(charset, "charset should not be null");
            return new InputStreamReader(asInputStream(), charset);
        }

        @Override
        public void close() throws IOException {
        }

    }

    public static void main(String[] args) {
        R<String> r1 = new R<>();
        Type genericSuperclass = r1.getClass().getGenericSuperclass();
        System.out.println(genericSuperclass.getClass());
    }

}
