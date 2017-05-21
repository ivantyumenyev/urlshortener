package com.yourl.service.urlstore;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUrlStoreService implements IUrlStoreService{
    private BiMap<String, String> urlByIdMap = HashBiMap.create();
    private Map<String, Integer> idCallsMap = new ConcurrentHashMap<>();
    private Map<String, LocalDateTime> idCreateDateMap = new ConcurrentHashMap<>();

    private static final String ALPHA_NUM = "0123456789abcdefghijklmnopqrstuvwxyz";

    private int idLength = 6;

    public InMemoryUrlStoreService(int idLength) {
        this.idLength = idLength;
    }

    @Override
    public String findUrlById(String id) {
        String url = urlByIdMap.get(id);

        if (url != null) {
            addCall(id);
            return url;
        } else {
            return "";
        }
    }

    @Override
    public String storeURL(String url) {

        String urlId = urlByIdMap.inverse().get(url);

        if (urlId == null) {
            do {
                urlId = generateUrlId();
            } while (urlByIdMap.containsKey(urlId));

        }

        urlByIdMap.put(urlId, url);
        idCreateDateMap.put(urlId, LocalDateTime.now());
        return urlId;

    }

    @Override
    public boolean checkURLExperation(int lifeTime) {
        for (Map.Entry<String, LocalDateTime> idCreateDate : idCreateDateMap.entrySet()) {
            if (ChronoUnit.SECONDS.between(idCreateDate.getValue(), LocalDateTime.now()) > lifeTime) {
                System.out.println("I've just removed URL ID " + idCreateDate.getKey());
                urlByIdMap.remove(idCreateDate.getKey());
                idCallsMap.remove(idCreateDate.getKey());
                idCreateDateMap.remove(idCreateDate.getKey());
            }

        }

        return !idCreateDateMap.isEmpty();

    }

    private void addCall(String id) {
        Integer calls = idCallsMap.get(id);
        if (calls != null){
            idCallsMap.put(id, ++calls);
            System.out.println(calls);
        } else {
            idCallsMap.put(id, 1);
            System.out.println("1");
        }

    }

    private String generateUrlId() {

        StringBuffer sb = new StringBuffer(idLength);
        for (int i = 0; i < idLength; i++) {
            int ndx = (int)(Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }
        return sb.toString();
    }

}
