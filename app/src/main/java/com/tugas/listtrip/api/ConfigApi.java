package com.tugas.listtrip.api;

public class ConfigApi {

    private static String BASE_URL_API = "https://sorjservice.online/listtrip/api/";

    public static ApiEndPoints getApiService(){
        return RetrofitClient.getClient(BASE_URL_API).create(ApiEndPoints.class);
    }
}
