package com.brother.tpp.vo;

import java.io.Serializable;

/** 买家统计  各个分类
 * @author lwr
 *
 */
public class Statistic_category implements Serializable {

	private static final long serialVersionUID = 1L;
	private String category_name;
	private int category_id;
	private float mny;
	/**
	 *  百分比
	 */
	private double percent;
	@Override
	public String toString() {
		return "Statistic_category [category_name=" + category_name
				+ ", category_id=" + category_id + ", mny=" + mny
				+ ", percent=" + percent + "]";
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public float getMny() {
		return mny;
	}
	public void setMny(float mny) {
		this.mny = mny;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	
}
