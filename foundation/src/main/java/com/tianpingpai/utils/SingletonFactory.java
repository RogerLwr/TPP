package com.tianpingpai.utils;

import java.util.HashMap;

public class SingletonFactory {
	private static HashMap<Class<?>,Object> instanceMap = new HashMap<Class<?>, Object>();
	@SuppressWarnings("unchecked")
	public synchronized static <T> T getInstance(Class<T> clazz){
		
		T instance = (T) instanceMap.get(clazz);
		if(instance == null){
			try {
				instance = clazz.newInstance();
				instanceMap.put(clazz, instance);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
}
