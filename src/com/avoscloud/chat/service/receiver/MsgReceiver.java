package com.avoscloud.chat.service.receiver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.base.init.MyApplication;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVMessageReceiver;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.Group;
import com.avos.avoscloud.Session;
import com.avos.avoscloud.SessionManager;
import com.avoscloud.chat.service.listener.MsgListener;
import com.avoscloud.chat.service.listener.StatusListener;
import com.avoscloud.chat.service.listener.UserSessionListener;
import com.avoscloud.chat.util.Logger;

/**
 * Created by lzw on 14-8-7.
 */

public class MsgReceiver extends AVMessageReceiver {
	private static String TAG = "MsgReceiver";
	public static StatusListener statusListener;
	static String RETRY_ACTION = "com.avoscloud.chat.RETRY_CONNECT";
	public static Set<String> onlineIds = new HashSet<String>();
	public static Set<MsgListener> msgListeners = new HashSet<MsgListener>();
	private static boolean sessionPaused = true;
	private static boolean isOffLine = true;
	public static UserSessionListener sessionListener  ;
	
	@Override
	public void onSessionOpen(Context context, Session session) {
		if(MyApplication.CURRENT_USER!=null){
			Logger.d("onSessionOpen");
			System.out.println("用户成功登录上实时聊天服务器了");
			try {
				sessionPaused = false;
	//			MyApplication.CURRENT_USER = AVUser.getCurrentUser();
				//加入群组
		        Group group = session.getGroup(MyApplication.GROUP_ID);
		        Log.e(TAG, "group_id = "+group.getGroupId());
		        Log.e(TAG, "onSessionOpen user id -->"+MyApplication.CURRENT_USER.getObjectId());
		        group.join();
				
			} catch (Exception e) {
				Log.e(TAG, "Open Session Error "+e.toString());
			}
		}
		
	}
	
	@Override
	public void onSessionClose(Context context, Session session) {
		Logger.d("onSessionClose");
		//List<String> peersId = session.getOnlinePeers() ;
		openSession(MyApplication.CURRENT_USER);
		/*for(String id : peersId){
			if(id.equals(MyApplication.CURRENT_USER.getObjectId())){
				System.out.println("onSessionClose........" + MyApplication.CURRENT_USER.getObjectId());
			}
		}*/
		
		//MsgReceiver.setSessionPaused(true);
		System.out.println("onSessionClose........");
	}
	@Override
	public void onSessionPaused(Context context, Session session) {
		try {
			sessionPaused = true;
			openSession(MyApplication.CURRENT_USER);
			MyApplication.ctx.sendBroadcast(new Intent(RETRY_ACTION));
			System.out.println("用户Session失效了....");
		} catch (Exception e) {
			
		}
	
	}	

	@Override
	public void onSessionResumed(Context context, Session session) {
		sessionPaused = false;
		System.out.println("用户Session恢复了....");
	}

	public static boolean isSessionPaused() {
		return sessionPaused ;
	}

	public static void setSessionPaused(boolean sessionPaused) {
		MsgReceiver.sessionPaused = sessionPaused;
	}
	

	public static Session getSession(AVUser user) {
		try {
			Session session = SessionManager.getInstance(MyApplication.CURRENT_USER.getObjectId());
			return session ;
			//session.setSignatureFactory(new SignatureFactory());
		} catch (Exception e) {
			return null ;
		}
		
		
		
		
	}
	
	public static void closeSession(AVUser user) {
		try {
			if(MyApplication.CURRENT_USER!=null){
				Session session = getSession(user);
				session.close();
			}
		} catch (Exception e) {
			
		}
	}

	public static void openSession(AVUser user) {
		try {
			Log.e(TAG, "open session user id -->"+user.getObjectId());
			if(MyApplication.CURRENT_USER != null){
				Session session = getSession(MyApplication.CURRENT_USER);
				List<String> peerIds = new ArrayList<String>();
				peerIds.add(user.getObjectId());
				session.open(peerIds);
			}
			//session.watchPeers(peerIds);
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void onPeersWatched(Context context, Session session,
			List<String> peerIds) {
		try {
			if (peerIds.size() > 0) {
				// throw new IllegalStateException("the size of watched peers isn't 1");
				Logger.d("watched " + peerIds);
				// session.open(peerIds);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	@Override
	public void onPeersUnwatched(Context context, Session session,
			List<String> peerIds) {
		Logger.d("unwatch " + peerIds);
	}

	@Override
	public void onMessage(final Context context, Session session,
			AVMessage avMsg) {
		Logger.d("onMessage");
		System.out.println( "onMessage....");
		try {
//			if(!session.isOpen()){
//				openSession(AVUser.getCurrentUser());
//			}
			// ChatUtils.logAVMessage(avMsg);
			// ChatService.onMessage(context, avMsg, msgListeners, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Override
	public void onMessageSent(Context context, Session session, AVMessage avMsg) {
		Logger.d("onMessageSent");
		System.out.println( "onMessageSent....");
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		// ChatUtils.logAVMessage(avMsg);
		// ChatService.onMessageSent(avMsg, msgListeners, null);
	}

	@Override
	public void onMessageDelivered(Context context, Session session,
			AVMessage msg) {
		Logger.d("onMessageDelivered");
		System.out.println( "onMessageDelivered....");
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		// ChatUtils.logAVMessage(msg);
		// ChatService.onMessageDelivered(msg, msgListeners);
	}

	@Override
	public void onMessageFailure(Context context, Session session,
			AVMessage avMsg) {
		Logger.d("onMessageFailure");
		System.out.println( "onMessageFailure....");
		try {
			
			// ChatUtils.logAVMessage(avMsg);
			// ChatService.onMessageFailure(avMsg, msgListeners, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Override
	public void onStatusOnline(Context context, Session session,
			List<String> peerIds) {
		Logger.d("onStatusOnline " + peerIds);
		isOffLine = false ;
//		System.out.println( "onStatusOnline...." + peerIds);
//		onlineIds.addAll(peerIds);
//		if (statusListener != null) {
//			statusListener.onStatusOnline(new ArrayList<String>(onlineIds));
//		}
	}

	@Override/***一些watchPeerIds 关注的对象离线状态监听 ***/
	public void onStatusOffline(Context context, Session session,
			List<String> strings) {
		Logger.d("onStatusOff " + strings);
		isOffLine = true ;
		if(!session.isOpen()){
			//openSession(AVUser.getCurrentUser());
		}
//		System.out.println( "onStatusOff...." + strings);
//		onlineIds.removeAll(strings);
//		if (statusListener != null) {
//			statusListener.onStatusOnline(new ArrayList<String>(onlineIds));
//		}
	}

	/*@Override
	public void onSessionClose(Context context, Session session) {
		log.e("session is close ", "session is close ");
		List<String> peerIds = new ArrayList<String>();
		peerIds.add(AVUser.getCurrentUser().getObjectId() );
		session.open(peerIds);
	}*/
	
	

	@Override
	public void onError(Context context, Session session, Throwable throwable) {
		// Utils.toast(context, throwable.getMessage());
		throwable.printStackTrace();
		// ChatService.onMessageError(throwable, msgListeners);
		try {
			System.out.println( "onError...." + throwable.toString());
			if(!session.isOpen()){
				//openSession(AVUser.getCurrentUser());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public static void registerStatusListener(StatusListener listener) {
		statusListener = listener;
	}

	public static void unregisterSatutsListener() {
		statusListener = null;
	}

	public static void addMsgListener(MsgListener listener) {
		msgListeners.add(listener);
	}

	public static void removeMsgListener(MsgListener listener) {
		msgListeners.remove(listener);
	}
	
	public static void addSessionListener(UserSessionListener listener) {
		sessionListener = listener ;
	}

	public static List<String> getOnlineIds() {
		return new ArrayList<String>(onlineIds);
	}

	

}
