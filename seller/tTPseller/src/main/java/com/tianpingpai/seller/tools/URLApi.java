package com.tianpingpai.seller.tools;

public class URLApi {
    public static final boolean IS_DEBUG = true;
    public static final boolean REPLACE_ACCESS_TOKEN = false; //替换accessToken
//>>>>>>> 正式环境 控制 是否添加 AccessToken

    public static String getBaseUrl() {

        if (IS_DEBUG) {
            return "http://test.tianpingpai.com";
//            return "http://release.tianpingpai.com";
        } else
            return "http://api.tianpingpai.com";
    }

    public static String getWebBaseUrl(){
        if (IS_DEBUG) {
            return "http://test.tianpingpai.com";
//            return "http://release.tianpingpai.com";
        } else
            return "http://m.tianpingpai.com";
    }

    /**
     * 一级供货商 的接口
     */
    public static final class SaleOrder {
        public static final String ORDER_LIST_INFO = getBaseUrl() + "/api/salerorder/getOrderListInfo.json";
        public static final String ORDER_DETAIL_INFO = getBaseUrl() + "/api/salerorder/getOrderDetailInfo.json";
        public static final String DEL_ORDER = getBaseUrl() + "/api/salerorder/delOrder.json";
        public static final String UPDATE_ORDER_STATUS = getBaseUrl() + "/api/salerorder/updateOrderStatus.json";
        public static final String CHG_ORDER_MNY = getBaseUrl() + "/api/salerorder/chgOrderMny.json"; //订单改价
    }
    /**
     * 获取订单列表信息
     */
    public static final String GET_ORDER_LIST_URL = getBaseUrl() + "/api/order/getOrderListInfo.json";
    /**
     * 获取订单详情信息
     */
    public static final String GET_ORDER_DETAIL_URL = getBaseUrl() + "/api/order/getOrderDetailInfo.json";
    /**
     * 拒绝订单
     */
    public static final String REFUSE_ORDER_URL = getBaseUrl() + "/api/order/delOrder.json";
    /**
     * 接收订单
     */
    public static final String ACCEPT_ORDER_URL = getBaseUrl() + "/api/order/updateOrderStatus.json";

    /**
     * 商家绑定提现账号接口Get
     */
    public static final String CASH_ACCOUNTS = getBaseUrl() + "/api/cash/accounts";
    /**
     * 申请提现接口Post
     */
    public static final String APPLY = getBaseUrl() + "/api/cash/apply";
    /**
     * 商家注册接口
     */
    public static final String REGISTER_URL = getBaseUrl() + "/api/saler/register";
    /**
     * post 更新商家信息
     */
    public static final String SALER_UPDATE = getBaseUrl() + "/api/saler/update";
    /**
     * 分类列表
     */
    public static final String PROD_FIRST_DATA_LIST = getBaseUrl() + "/api/category/getProdFirstDataList.json";
    /**
     * get 店铺评论 列表  saler_id 和 grade 评论等级
     */
    public static final String COMMENT_LIST_URL = getBaseUrl() + "/api/comment/list";
    /**
     * get 商家统计 接口  saler_id 商家id 年 year 月 month
     */
    public static final String STATISTIC = getBaseUrl() + "/api/saler/statistic";
    /**
     * Post 更新通知接口  id 商家通知id content beginTime endTime
     */
    public static final String NOTIFICATIONS_UPDATE = getBaseUrl() + "/api/saler/notifications/update";
    /**
     * Get 删除通知接口  id 商家通知id
     */
    public static final String NOTIFICATIONS_DEL = getBaseUrl() + "/api/saler/notifications/delete";
    /**
     * Post添加通知 content beginTime endTime
     */
    public static final String NOTIFICATIONS_ADD = getBaseUrl() + "/api/saler/notifications/add";
    /**
     * 全部通知
     */
    public static final String NOTIFICATIONS_ALL = getBaseUrl() + "/api/saler/notifications/all";
    /**
     * 用户登录接口
     */
    public static final String LOGIN_URL = getBaseUrl() + "/api/user/login.json";
    /**
     * 找回密码
     */
    public static final String FORGET_URL = getBaseUrl() + "/api/user/resetPwd.json";
    /**
     * 找回密码
     */
    public static final String VALIDATE_URL = getBaseUrl() + "/api/verify/sendVerifyCode.json";

    /**
     * 获取商品3级全部分类信息接口(包括上下架)
     * %d是通配符 可以替换为int型 ManageGoodsFragmentLwr调用 ?user_id=%d
     */
    public static final String GET_PROD_LIST_INFO_URL = getBaseUrl() + "/api/prod/getProdListInfo.json";

    /**
     * post 批量修改 商品价格  list<prod4Json> 用 fastjson 生成 String
     */
    public static final String UPDATE_PROD_PRICE_URL = getBaseUrl() + "/api/prod/updateProdPrice.json";
    /**
     * post 删除 商品
     */
    public static final String DELETE_PROD_URL = getBaseUrl() + "/api/prod/delSalerProd.json";

    /**
     * 消息列表
     */
    public static final String MESSAGE_LIST_URL = getBaseUrl() + "/api/msg/getMsgInfo.json";

    /**
     * 注册终端
     */
    public static final String REGISTER_TERMINAL_URL = getBaseUrl() + "/api/user/registTerminal.json";
}
