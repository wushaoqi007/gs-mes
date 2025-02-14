package com.greenstone.mes.mail.external;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.mail.external.MailHttpClient;
import com.greenstone.mes.mail.infrastructure.config.MailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailHttpClientImpl implements MailHttpClient {

    private final MailConfig mailConfig;

    @Override
    public String get(String path) {
        HttpRequest get = HttpUtil.createGet(path);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Api-Key", mailConfig.getServer().getApikey());
        get.addHeaders(headers);
        HttpResponse response = get.execute();
        return response.body();
    }

    @Override
    public String post(String path, String body) {
        HttpRequest post = HttpUtil.createPost(path);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Api-Key", mailConfig.getServer().getApikey());
        post.addHeaders(headers);
        post.body(body);
        HttpResponse response = post.execute();
        return response.body();
    }

    @Override
    public String post(String path, Object body) {
        return post(path, JSON.toJSONString(body));
    }

}
