package com.tianpingpai.core;

import android.os.Handler;
import android.os.Looper;

import com.tianpingpai.model.Model;
import com.tianpingpai.utils.SingletonFactory;

import java.util.HashSet;

public class ModelManager <K,E>{

	private static ModelManager<ModelEvent,Model> sInstance;

	public static ModelManager<ModelEvent,Model> getModelInstance(){
//		return SingletonFactory.getInstance(ModelManager.class);
        if(sInstance == null){
            sInstance = new ModelManager<>();
        }
        return sInstance;
	}

	private static Handler sHandler = new Handler(Looper.getMainLooper());
	
	private HashSet<ModelStatusListener<K, E>> listeners = new HashSet<>() ;
	
	public void registerListener(ModelStatusListener<K, E> l){
		listeners.add(l);
	}
	
	public void unregisterListener(ModelStatusListener<K, E> l){
		listeners.remove(l);
	}
	
	public HashSet<ModelStatusListener<K, E>> getListener(){
		return listeners;
	}
	
	public void notifyEvent(final K event,final E model){
		final HashSet<ModelStatusListener<K, E>> copy = new HashSet<>(listeners);
		if(Thread.currentThread() == Looper.getMainLooper().getThread()){
			for(ModelStatusListener<K, E> l:copy){
				l.onModelEvent(event, model);
			}
		}else{
			sHandler.post(new Runnable() {
				@Override
				public void run() {
					for(ModelStatusListener<K, E> l:copy){
						l.onModelEvent(event, model);
					}
				}
			});
		}
	}
}
