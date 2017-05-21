package com.yourl.service.userstore;

import com.yourl.service.urlstore.IUrlStoreService;
import com.yourl.service.urlstore.InMemoryUrlStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Svirinstel on 12.05.17.
 */
@Service
@EnableScheduling
@PropertySource("classpath:application.properties")
public class InMemoryUserStoreService implements IUserStoreService {

    private Map<String, IUrlStoreService> urlStoreByUserMap = new ConcurrentHashMap<>();

    @Value("${url.id.length:10}")
    private int urlIdLength;

    @Value("${url.lifetime.seconds:3600}")
    private int urlLifetime;

    @Override
    public IUrlStoreService findUrlStoreServiceByUser(String userID) {
        return urlStoreByUserMap.get(userID);
    }

    @Override
    public String createUser() {
        String userId;

        do {
            userId = UUID.randomUUID().toString();
        } while (urlStoreByUserMap.containsKey(userId));

        urlStoreByUserMap.put(userId, new InMemoryUrlStoreService(urlIdLength));
        return userId;
    }

    @Scheduled(fixedDelay = 5000)
    public void deleteOldUrls(){
        for (Map.Entry<String, IUrlStoreService> urlStoreByUser : urlStoreByUserMap.entrySet()) {
            if (!urlStoreByUser.getValue().checkURLExperation(urlLifetime)) {
                System.out.println("I've just removed user " + urlStoreByUser.getKey());
                urlStoreByUserMap.remove(urlStoreByUser.getKey());
            }
        }
    }

}
