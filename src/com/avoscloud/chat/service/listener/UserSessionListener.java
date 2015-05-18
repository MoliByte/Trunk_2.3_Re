package com.avoscloud.chat.service.listener;

import android.content.Context;

import com.avos.avoscloud.Session;


public interface UserSessionListener {
  public void sessionIsOpen(Context context, Session session);
  public void sessionIsPause(Context context, Session session);
  public void sessionIsResume(Context context, Session session);
}
