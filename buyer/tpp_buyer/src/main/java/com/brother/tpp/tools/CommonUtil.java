package com.brother.tpp.tools;

/**
 *
 */
public class CommonUtil {

	public static class Coupon{
		public static final int NOT_USE = 1;
		public static final int USED = 2;
		/**
		 * 优惠券状态 已过期 3
		 */
		public static final int OUT_DATE = 3;
	}
	
	/**
	 *  值回传 请求码, 增加常用列表的值回传
	 */
	public static final int REQUST_CODE_COMMON_USE_ADD = 101;

	
	/**
	 *  排序  -1默认排序， 1发货速度， 2服务态度 ， 3商品质量， 4推荐排序
	 */
	public static final int SORT_DEFAULT = -1;
	public static final int SORT_SPEED = 1;
	public static final int SORT_SERVIECE = 2;
	public static final int SORT_QUALITY = 3;
	public static final int SORT_RECOMMEND = 4;
	
	/**
	 *  综合店铺  收藏 类型(2维码)  即 调用 不带商圈id的接口
	 */
	public static final int TYPE_COLLECT_MULTI_SHOP = 2;
	/**
	 *  intent或fragment 获取 的 默认参数 
	 */
	public static final int DEFAULT_ARG = -1;
	/**
	 * 加入购物车 店铺进入购物车传到fragment的参数 将 数据库中 当前店铺 is_market = 0 改为 1 标记为加入购物车
	 */
	public static final int SHOP_TO_CAR = 101;
	/**
	 * 未付款(待付款)
	 */
	public static final int NOT_PAY_MONEY = 0;
	/**
	 * (待发货订单)待卖家确认(针对买家)；针对卖家未接受
	 */
	public static final int NOT_ACCEPT_SELLER = 1;
	/**
	 * 待发货订单(针对买家)；针对卖家未发货
	 */
	public static final int NOT_DELEVIER_GOODS = 2;
	/**
	 * 待收货(针对买家)；已发货(针对卖家)
	 */
	public static final int WAIT_ACCEPT_GOODS = 3;
	/**
	 * 已收货(待评价)(针对买家)；已完成(针对卖家)
	 */
	public static final int COMPLETE_WAIT_COMMENT = 4;



	/***********************************友盟数据统计页面设置的常量 ***********************************/
	/**
	 * 帮助 页面
	 */
	public static final String HELP_PAGE_UMENG = "Buyer_Help";

	/***********************************友盟数据统计页面设置的常量 ***********************************/

	public static final String COMPLETED_ACTION = "com.tianpingpai.buyer.broadcast.COMPLETEDACTION";
	public static final int PAGESIZE = 10;
}
