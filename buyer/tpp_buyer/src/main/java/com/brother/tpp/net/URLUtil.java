package com.brother.tpp.net;

import com.tianpingpai.buyer.model.URLApi;

/**
 * 网络请求常量类
 * @author Administrator
 *
 */
public class URLUtil {
	/**
	 *  买家统计   请求接口（GET）
	 */
	public static final String  GET_BUYER_STATISTICS = URLApi.getBaseUrl() + "/api/statistics/getBuyerStatistics.json";
	/**
	 *  商品侧滑信息接口   请求接口（GET）
	 */
	public static final String  GET_SHOP_INFO = URLApi.getBaseUrl() + "/api/shopinfo/getShopDetail.json";
	/**
	 *  店铺 是否已经收藏 shop_type shop_id
	 */
	public static final String  SHOP_IS_FAVORITE = URLApi.getBaseUrl() + "/api/favorite/getShopIsFavorite.json";
	/**
	 *  店铺筛选的  分类列表
	 */
	public static final String  PROD_FIRST_DATA_LIST = URLApi.getBaseUrl() + "/api/category/getProdFirstDataList.json";
	/**
	 * 用户登录接口
	 */
	public static final String LOGIN_URL = URLApi.getBaseUrl()+"/api/user/login.json";
	/**
	 * 注册接口
	 */
	public static final String REGISTER_URL = URLApi.getBaseUrl()+"/api/user/register.json";
	/**
	 * 获取验证码
	 */
	public static final String VALIDATE_URL = URLApi.getBaseUrl()+"/api/verify/sendVerifyCode.json";
	/**
	 * 找回密码
	 */
	public static final String FORGET_URL = URLApi.getBaseUrl()+"/api/user/resetPwd.json";
	/**
	 * 个人中心的详情 
	 */
	public static final String OWNDES_URL = URLApi.getBaseUrl()+"/api/user/getBuyUserDetailInfo.json";
	/**
	 * 地区获取接口
	 */
	public static final String ADDRESS_URL = URLApi.getBaseUrl()+"/api/area/getAreaDataList.json";
	/**
	 * 添加用户收货地址
	 */
	public static final String ADDADDRESS_URL = URLApi.getBaseUrl()+"/api/user/addUserAddr.json";
	/**
	 * 编辑用户收货地址
	 */
	public static final String EDITORADDRESS_URL = URLApi.getBaseUrl()+"/api/user/updateUserAddr.json";
	/**
	 * 删除用户地址
	 */
	public static final String DELETEADDRESS_URL = URLApi.getBaseUrl()+"/api/user/delUserAddr.json";
	/**
	 * 个人中心的订单列表的地址
	 */
	public static final String GET_ORDER_FORM_URL = URLApi.getBaseUrl()+"/api/order/getOrderListInfo.json";
	/**
	 * 订单详情的地址
	 */
	public static final String GETORDERFORMDES_URL = URLApi.getBaseUrl()+"/api/order/getOrderDetailInfo.json";
	/**
	 * 店铺列表  ShopFragment,ShopcategoryAct调用
	 */
	public static final String GETSHOPS_URL = URLApi.getBaseUrl()+"/api/user/getRecommendSaler.json";
	/**
	 * 获取商品3级全部分类信息接口   店铺详情
	 *   ShopDetailAct调用 
	 */
	public static final String GET_SALE_USER_PROD_LIST_INFO_URL = URLApi.getBaseUrl()+"/api/prod/getSaleUserProdListInfo.json";
	/**
	 *  综合 店铺详情  获取商品3级全部分类信息接口
	 *   ShopMultiAct调用 
	 */
	public static final String GET_MULTI_PROD_LIST_INFO_URL = URLApi.getBaseUrl()+"/api/shop/getMultiProdListInfo.json";

	/**
	 * 校验商品是否限购
	 */
	public static final String GENERATE_CHECK_SHOP_CART_URL = URLApi.getBaseUrl()+"/api/sales/shopcart_check";

	/**
	 * 搜索接口
	 */
	public static final String SEARCH_URL = URLApi.getBaseUrl() + "/api/search/searchProd.json";
	
	/**
	 * 消息接口
	 */
	public static final String GETPUSHMESSAGE_URL = URLApi.getBaseUrl()+"/api/msg/getMsgInfo.json";
	/**
	 * 订单跟踪
	 */
	public static final String GET_ORDER_FORM_TRACE_URL =  URLApi.getBaseUrl()+"/api/order/getOrderTraceInfo.json";
	
	/**
	 * 注册终端
	 */
	public static final String REGISTERTERMINAL_URL =  URLApi.getBaseUrl()+"/api/user/registTerminal.json";
	
	/**
	 *  修改订单状态
	 */
	public static final String ACCEPTORTHER_URL = URLApi.getBaseUrl()+"/api/order/updateOrderStatus.json";
	
	/**
	 *  post增加店铺收藏 sale_user_id 卖家id(int)
	 */
	public static final String ADD_FAVORITE_SALER_URL = URLApi.getBaseUrl()+"/api/favorite/addFavoriteSaler.json";
	/**
	 *  post删除店铺收藏 id收藏id (long)
	 */
	public static final String DEL_FAVORITE_SALER_URL = URLApi.getBaseUrl()+"/api/favorite/delFavoriteSaler.json";
	/**
	 *  get查询店铺收藏 page (int) page_size(int)
	 */
	public static final String GET_BUY_FAVORITE_LIST_INFO_URL = URLApi.getBaseUrl()+"/api/favorite/getBuyFavoriteListInfo.json";
	/**
	 *  get 某个店铺的常用菜单列表 saler_id 可分页 (不传店铺id即全部常用)
	 */
	public static final String COMMON_USE_URL = URLApi.getBaseUrl() + "/api/prod/commonuse.json";
	/**
	 *  post增加 常用店铺sale_id  卖家id(int) 和 prod_id
	 */
	public static final String ADD_COMMONUSE_URL = URLApi.getBaseUrl()+"/api/prod/addCommonuse.json";
	/**
	 *  post删除店铺常用  saler_id 和 prod_id 
	 */
	public static final String DEL_COMMONUSE_URL = URLApi.getBaseUrl()+"/api/prod/delCommonuse.json";
	
	/**
	 *  post 增加店铺评论  saler_id 和 user_id 
	 */
	public static final String ADD_COMMENT_URL = URLApi.getBaseUrl()+"/api/comment/add";
	/**
	 *  get 店铺评论 列表  saler_id 和 grade 评论等级 
	 */
	public static final String COMMENT_LIST_URL = URLApi.getBaseUrl()+"/api/comment/list";

	/**请求成功	0 */
    public static final int DATA_STATE_SUCCESS = 0;

	/**
	 * post 删除收藏店铺或商品 salerId 和type 和 prodId
	 */
	public static final String DEL_STORE_OR_PROD_URL = URLApi.getBaseUrl()+"/api/favorite/delete";

	/**
	 * 生成订单接口
	 */
	public static final String ORDERADD_URL = URLApi.getBaseUrl()+"/api/order/add";

}
