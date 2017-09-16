package com.tianpingpai.model;

public class URLApi {
	
	public static final boolean debugMode = true;

	private static boolean isDebug(){
		return debugMode;
	}
	
	public static String getBaseUrl(){
		if(isDebug()){
			return "http://test.tianpingpai.com";
		}
		return "http://api.tianpingpai.com";
	}
	public static final String  PROD_FIRST_DATA_LIST = getBaseUrl() + "/api/category/getProdFirstDataList.json";

	public static final String ADDRESS_URL = URLApi.getBaseUrl()+"/api/area/getAreaDataList.json";
	public static final class User{
		public static String login(){
			return getBaseUrl() + "/crm/marketer/login";
		}
		
		public static String getInfo(){
			return getBaseUrl() + "/crm/marketer/getMarketerInfo";
		}
		
		public static String update(){
			return getBaseUrl() + "/crm/marketer/updateMarketerInfo";
		}

		public static String getSubordinates() {
			return getBaseUrl() + "/crm/marketer/getSubMarketer";
		}
		
		public static String getContacts(){
			return getBaseUrl() + "/crm/marketer/getOtherMarketer";
		}

		public static String getStatistics() {
			return getBaseUrl() + "/crm/order/statistic";
		}
	}
	
	public static final class Customer{

		public static final String register =  getBaseUrl()+"/api/saler/register";
		public static String check(){
			return getBaseUrl() + "/crm/customer/check";
		}
		
		public static String add(){
			return getBaseUrl() + "/crm/customer/add";
		}
		
		public static String updateInfo(){
			return getBaseUrl() + "/crm/customer/updateCustomerInfo";
		}
		
		public static String getMyCustomers(){
			return getBaseUrl() + "/crm/customer/getMyCustomer";
		}

		public static String getQueryCustomer(){
			return getBaseUrl() + "/crm/customer/queryByCondition";
		}

		public static String approve(){
			return getBaseUrl() + "/crm/customer/approve";
		}
		
		public static String getComments(){
			return getBaseUrl() + "/crm/customer/getCusComment";
		}

		public static String addComment() {
			return getBaseUrl() + "/crm/customer/addComment";
		}
		
		public static String getSaleNumber(){
			return getBaseUrl() + "/crm/customer/detail";
		}
		
		public static String delete(){
			return getBaseUrl() + "/crm/customer/delCustomer";
		}
	}
	
	public static final class Order{
		public static String list(){
			return getBaseUrl() + "/crm/order/list";
		}
		
		public static String getDetail(){
			return getBaseUrl() + "/crm/order/detail";
		}
	}
	
	public static final class Market{
		public static String list(){
			return getBaseUrl() + "/api/market/list";
		}
	}

	//部门统计的借口
	public static String getDepartmentstatistal(){
		return "/crm/app/customer/purchase_department";
	}
	//个人统计的借口
	public static String getMinestatistal(){
		return "/crm/app/customer/purchase";
	}

	
}
