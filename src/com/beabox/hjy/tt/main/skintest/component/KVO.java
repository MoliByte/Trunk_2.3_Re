package com.beabox.hjy.tt.main.skintest.component;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangjilue on 14-3-24.
 */
public class KVO {
	public static interface Observer {
		void onEvent(String event, Object ... args);
	}
	private Map<String, List<Observer>> events = new HashMap<String, List<Observer>>();

	/**
	 * 注册事件监听器
	 * @param event 事件名称
	 * @param observer 观察者
	 */
	public void registerObserver(String event, Observer observer){
		List<Observer> observers = events.get(event);
		if(observers == null){
			observers = new ArrayList<Observer>();
			events.put(event, observers);
		}

		if(!observers.contains(observer)){
			observers.add(observer);
		}
	}

	/**
	 * 触发事件
	 * @param event 事件名称
	 * @param args 参数列表
	 */
	public void fire(String event, Object ... args){
		List<Observer> observers = events.get(event);
		if(observers != null){
			for(int i = 0; i < observers.size(); i ++){
				Observer observer = observers.get(i);
				if(observer!=null && args != null && event != null){
					observer.onEvent(event,args);
				}else{
					continue ;
				}
				
			}
		}
	}

	/**
	 * 移除观察者
	 * @param event 事件名
	 * @param observer 要移除的观察者
	 */
	public void removeObserver(String event, Observer observer){
		List<Observer> observers = events.get(event);
		if(observers != null){
			observers.remove(observer);
		}
	}

}
