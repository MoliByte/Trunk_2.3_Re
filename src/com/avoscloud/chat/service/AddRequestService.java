package com.avoscloud.chat.service;

import java.util.List;

import com.app.base.init.MyApplication;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avoscloud.chat.avobject.AddRequest;
import com.avoscloud.chat.base.C;

/**
 * Created by lzw on 14-9-27.
 */
public class AddRequestService {
  public static int countAddRequests() throws AVException {
    AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    q.whereEqualTo(AddRequest.TO_USER, AVUser.getCurrentUser());
    try {
      return q.count();
    } catch (AVException e) {
      if (e.getCode() == AVException.CACHE_MISS) {
        return 0;
      } else {
        throw e;
      }
    }
  }

  public static List<AddRequest> findAddRequests() throws AVException {
    AVUser user = AVUser.getCurrentUser();
    AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
    q.include(AddRequest.FROM_USER);
    q.whereEqualTo(AddRequest.TO_USER, user);
    q.orderByDescending(C.CREATED_AT);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    return q.find();
  }

  public static boolean hasAddRequest() throws AVException {
    PreferenceMap preferenceMap = PreferenceMap.getMyPrefDao(MyApplication.ctx);
    int addRequestN = preferenceMap.getAddRequestN();
    int requestN = countAddRequests();
    if (requestN > addRequestN) {
      return true;
    } else {
      return false;
    }
  }
}
