package com.yourl.service.userstore;

import com.yourl.service.urlstore.IUrlStoreService;

/**
 * Created by Svirinstel on 12.05.17.
 */
public interface IUserStoreService {
    IUrlStoreService findUrlStoreServiceByUser(String userID);

    String createUser();

}
