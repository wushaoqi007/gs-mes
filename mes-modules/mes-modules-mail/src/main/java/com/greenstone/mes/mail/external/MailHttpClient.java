package com.greenstone.mes.mail.external;

public interface MailHttpClient {

    String get(String path);

    String post(String path, String body);

    String post(String path, Object body);

}
