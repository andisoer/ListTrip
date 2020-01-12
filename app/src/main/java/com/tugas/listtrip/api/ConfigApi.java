package com.tugas.listtrip.api;

public class ConfigApi {

    private static String BASE_URL_API = "http://192.168.43.22/listtrip/";

    public static ApiEndPoints getApiService(){
        return RetrofitClient.getClient(BASE_URL_API).create(ApiEndPoints.class);
    }
}
