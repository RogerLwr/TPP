package com.tianpingpai.seller.tools;

import java.text.SimpleDateFormat;

/**
 * 通用的常量
 * @author Administrator
 *
 */
@SuppressWarnings("unused")
public class CommonUtil {

	public static class Date{
		public static final SimpleDateFormat sdf_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public static final SimpleDateFormat sdf_yyyy_MM_dd_HH = new SimpleDateFormat("yyyy年MM月dd日HH点");
		public static final SimpleDateFormat sdf_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
		public static final SimpleDateFormat sdf_yyyy_MM = new SimpleDateFormat("yyyy年MM月");
	}

	public static class Sku{
		/**
		 * 1 是单选 2是 多选 3是 填空
		 */
		public static final int attrMode1singleSelect = 1;
		public static final int attrMode2multiSelect = 2;
		public static final int attrMode3fillBlank = 3;
	}

	public static class ShopStatus {
		/**
		 * 店铺营业 1   店铺休息 2
		 */
		public static final int shopBusiness = 1;
		public static final int shopRest = 2;
	}

	public static int width = 130;
	public static int height = 130;

	/*public static class SalerInfo{
		*//**
		 * 供货商等级 为0 (个人中心 显示 我的积分模块)
		 *//*
		public static final int SALER_GRADE_0 = 0;
		*//**
		 * 供货商等级 为1 (个人中心 不显示 我的积分模块)
		 *//*
		public static final int SALER_GRADE_1 = 1;
	}*/

	public static class Coupon{
		public static final int NOT_USE = 1;
		public static final int USED = 2;
		/**
		 * 优惠券状态 已过期 3
		 */
		public static final int OUT_DATE = 3;
	}

	/**
	 *  数据为空 的错误
	 */
	public static final int DATA_IS_EMPTY = 101;
	
	/**
	 *  默认参数 -1
	 */
	public static final int DEFAULT_ARG = -1;
	/**
	 *  公告界面的 更新标志
	 */
	public static final int TYPE_UPDATE_NOTICE = 2;

	/**
	 *  handler msg.what 的标志   请求失败 网络延时
	 */
	public static final int MSG_WHAT_DATA_OUT_TIME = 2;
	/**
	 * 针对买家待卖家确认；针对卖家未接受
	 */
	public static final int NOTACCEPT_SELLER = 1;
	/**
	 * 针对买家待发货；针对卖家未发货
	 */
	public static final int NOT_DELEVIERGOODS = 2;
	/**
	 * 针对买家已收货；针对卖家已完成
	 */
	public static final int COMPLETE = 4;
	
	public static final String ORDER_STATUS = "3,4,7,8,9";
	
	/**
	 * 卖家
	 */
	public static final int SELLER = 0;
	/**
	 * 买家
	 */
//	public static final int BUYER = 1;
	/**
	 * 分页条数
	 */
	public static final int PAGESIZE = 10;
	
	public static final String APP_TO_LOGIN = "com.brother.tpp.broadcast.toLogin";
}
