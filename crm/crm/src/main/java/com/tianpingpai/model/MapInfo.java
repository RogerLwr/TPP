package com.tianpingpai.model;

import com.tianpingpai.crm.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapInfo implements Serializable {
	private static final long serialVersionUID = -758459502806858414L;
	/**
	 * 精度
	 */
	private double latitude;
	/**
	 * 纬度
	 */
	private double longitude;
	/**
	 * 图片ID，真实项目中可能是图片路径
	 */
	private int imgId;
	/**
	 * 商家名称
	 */
	private String name;
	/**
	 * 距离
	 */
	private String distance;
	/**
	 * 赞数量
	 */
	private int zan;

	private String stratEndInfo = "";

	public String getStratEndInfo() {
		return stratEndInfo;
	}

	public void setStratEndInfo(String stratEndInfo) {
		this.stratEndInfo = stratEndInfo;
	}

	public static List<MapInfo> mapInfos = new ArrayList<MapInfo>();

	static {
		mapInfos.add(new MapInfo(34.242652, 108.971171, R.drawable.ic_150922_mark_pop, "英伦贵族小旅馆",
				"距离209米", 1456));
		mapInfos.add(new MapInfo(34.242952, 108.972171, R.drawable.ic_150922_mark_pop, "沙井国际洗浴会所",
				"距离897米", 456));
		mapInfos.add(new MapInfo(34.242852, 108.973171, R.drawable.ic_150922_mark_pop, "五环服装城",
				"距离249米", 1456));
		mapInfos.add(new MapInfo(34.242152, 108.971971, R.drawable.ic_150922_mark_pop, "老米家泡馍小炒",
				"距离679米", 1456));
	}

	public MapInfo() {
	}

	public MapInfo(double latitude, double longitude, int imgId, String name,
				   String distance, int zan) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.imgId = imgId;
		this.name = name;
		this.distance = distance;
		this.zan = zan;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

	@Override
	public String toString() {
		return "MapInfo{" +
				"latitude=" + latitude +
				", longitude=" + longitude +
				", imgId=" + imgId +
				", name='" + name + '\'' +
				", distance='" + distance + '\'' +
				", zan=" + zan +
				'}';
	}
}