package com.bekaku.api.spring.service;

import com.google.gson.JsonObject;

import java.util.HashMap;

public interface RestClientService {
    <T> T post(String url, Object body, HashMap<String, String> headers, Class<T> responseType);

    <T> T get(String url, HashMap<String, String> headers, Class<T> responseType);

    <T> T delete(String url, HashMap<String, String> headers, Class<T> responseType);
}
