package com.avoscloud.chat.service.listener;

import android.content.Context;

import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.Group;


public interface MsgReceiverListener {
  public void onMessageReceiver(Context context, Group group, AVMessage message);
}
