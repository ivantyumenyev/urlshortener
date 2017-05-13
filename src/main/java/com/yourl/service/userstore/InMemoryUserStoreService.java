package com.yourl.service.userstore;

import com.yourl.service.urlstore.IUrlStoreService;
import com.yourl.service.urlstore.InMemoryUrlStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Svirinstel on 12.05.17.
 */
@Service
public class InMemoryUserStoreService implements IUserStoreService {

    private Map<String, IUrlStoreService> urlStoreByUserMap = new ConcurrentHashMap<>();

    @Override
    public IUrlStoreService findUrlStoreServiceByUser(String userID) {
        return urlStoreByUserMap.get(userID);
    }

    @Override
    public String createUser() {
        String userID = UUID.randomUUID().toString();
        urlStoreByUserMap.put(userID, new InMemoryUrlStoreService());
        return userID;
    }

}
