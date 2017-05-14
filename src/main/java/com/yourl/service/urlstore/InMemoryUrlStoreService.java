package com.yourl.service.urlstore;



import com.google.common.collect.MapMaker;
import com.yourl.service.urlstore.dto.ShortUrl;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUrlStoreService implements IUrlStoreService{
    private Map<String, ShortUrl> urlByIdMap = new ConcurrentHashMap<>();

    private static final String ALPHA_NUM = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int ID_LENGTH = 6;

    @Override
    public String findUrlById(String id) {
        ShortUrl shortUrl = urlByIdMap.get(id);
        if (shortUrl != null) {
            shortUrl.addCall();
            return shortUrl.getUrl();
        } else {
            return "";
        }
    }

    @Override
    public String storeURL(String url) {

        for (Map.Entry<String, ShortUrl> entry : urlByIdMap.entrySet()){
            if (entry.getValue().getUrl().equals(url)) {
                return entry.getKey();
            }
        }

        String urlID = generateUrlID();
        urlByIdMap.put(urlID, new ShortUrl(url));
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
