package com.yourl.service.urlstore;

public interface IUrlStoreService {
    String findUrlById(String id);

    String storeURL(String url);

}
