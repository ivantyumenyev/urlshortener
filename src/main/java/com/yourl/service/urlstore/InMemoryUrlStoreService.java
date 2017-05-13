package com.yourl.service.urlstore;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUrlStoreService implements IUrlStoreService{
    private Map<String, String> urlByIdMap = new ConcurrentHashMap<>();

    private static final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int ID_LENGTH = 6;

    @Override
    public String findUrlById(String id) {
        return urlByIdMap.get(id);
    }

    @Override
    public String storeURL(String url) {

        for (Map.Entry<String, String> entry : urlByIdMap.entrySet()){
            if (entry.getValue().equals(url)) {
                return entry.getKey();
            }
        }

        String urlID = generateUrlID();
        urlByIdMap.put(urlID, url);
        return urlID;
    }

    private String generateUrlID() {
        StringBuffer sb = new StringBuffer(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int ndx = (int)(Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }
        return sb.toString();
    }

}
