package com.tianpingpai.core;

public interface ModelStatusListener<K,E> {
	void onModelEvent(K event, E model);
}
