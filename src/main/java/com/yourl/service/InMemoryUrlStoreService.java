package com.yourl.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUrlStoreService implements IUrlStoreService{
    private Map<String, String> urlByIdMap = new ConcurrentHashMap<>();

    @Override
    public String findUrlById(String id) {
        return urlByIdMap.get(id);
    }

    @Override
    public void storeUrl(String id, String url) {
        urlByIdMap.put(id, url);
    }
}
