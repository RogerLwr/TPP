package com.tianpingpai.buyer.model;

import android.util.Log;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.model.StoreModel.Notification;
import com.tianpingpai.http.HttpRequest.Parser;
import com.tianpingpai.model.CommentModel;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShopInfoParser implements
		Parser<ModelResult<StoreModel>, String> {

	@Override
	public ModelResult<StoreModel> parse(String is) {
		JSONObject response;
		try {
			response = new JSONObject(is);
			ModelResult<StoreModel> mr = new ModelResult<>();
			int statusCode = response.getInt("statusCode");
			mr.setCode(statusCode);
			mr.setDesc(response.getString("statusDesc"));
			JSONObject obj = response.optJSONObject("result");
			Log.e("xx", "statusCode" + is);
			if (statusCode == URLUtil.DATA_STATE_SUCCESS && obj != null) {

				StoreModel shopInfoSliding = new StoreModel();
				shopInfoSliding.setName(TextUtils.optString(obj,"shop_name"));
				shopInfoSliding.setPhone(TextUtils.optString(obj,"phone"));
				shopInfoSliding.setId(obj.optInt("shop_id"));
				shopInfoSliding.setCategoryDesc(TextUtils.optString(obj,"category_desc"));
				shopInfoSliding.setType(obj.optInt("shop_type"));
				shopInfoSliding.setAddress(TextUtils.optString(obj,"shop_addr"));
				shopInfoSliding.setFavoriteNumer(obj.optInt("favorite_num"));
				shopInfoSliding.setCommentNumber(obj.optInt("comment_num"));
				shopInfoSliding.setGoodCommentNumber(obj.optInt("good_comment"));
				shopInfoSliding.setOrderNumber(obj.optInt("order_num"));
				shopInfoSliding.setRating((float) obj.optDouble("score"));
				shopInfoSliding.setImage(TextUtils.optString(obj,"shop_img"));
//				shopInfoSliding.setRelated_num(obj.optInt("related_num"));
				shopInfoSliding.setShopDesc(TextUtils.optString(obj,"shop_desc"));
				shopInfoSliding.setMinAmount(obj.optInt("minAmount"));
				shopInfoSliding.setFreight(obj.optInt("freight"));
				shopInfoSliding.setStartingPrice(obj.optInt("startingPrice"));
				mr.setModel(shopInfoSliding);
				// related shops

				JSONArray notificationArray = obj.optJSONArray("notifications");
				if (notificationArray != null) {
					ArrayList<Notification> notificationList = new ArrayList<>();
					for (int i = 0; i < notificationArray.length(); i++) {
						JSONObject nobj = notificationArray.optJSONObject(i);
						if (nobj != null) {
							Notification notification = new Notification();
							notification.setContent(TextUtils.optString(nobj,"content"));
							notificationList.add(notification);
						}
					}
					shopInfoSliding.setNotifications(notificationList);
				}
				JSONArray relatedArray = obj.optJSONArray("include_shops");
				if (relatedArray != null) {
					ArrayList<StoreModel> shopList = new ArrayList<>();
					for (int i = 0; i < relatedArray.length(); i++) {
						JSONObject robj = relatedArray.optJSONObject(i);
						if (robj != null) {
							StoreModel shop = new StoreModel();
							shop.setAddress(TextUtils.optString(robj,"shop_addr"));
							shop.setId(robj.optInt("shop_id"));
							shop.setRating(robj.optDouble("score"));
							shop.setType(robj.optInt("shop_type"));
							shop.setName(TextUtils.optString(robj,"shop_name"));
							shop.setCategoryDesc(TextUtils.optString(robj,"category_desc"));
							shopList.add(shop);
						}
					}
					shopInfoSliding.setSubStores(shopList);
				}

				JSONArray recentCommentArray = obj.optJSONArray("recent_comment");
				if (recentCommentArray != null){
					ArrayList<CommentModel> commentList = new ArrayList<>();
					for(int i = 0;i < recentCommentArray.length();i++){
						JSONObject cmobj = recentCommentArray.optJSONObject(i);
						if(cmobj != null){
							CommentModel cm = new CommentModel();
							cm.setId(cmobj.optInt("id"));
							cm.setUserId(cmobj.optInt("user_id"));
							cm.setSalerId(cmobj.optInt("order_id"));
							cm.setUsername(TextUtils.optString(cmobj,"username"));
							cm.setContent(TextUtils.optString(cmobj,"content"));
							cm.setGrade(cmobj.optInt("grade"));
							cm.setSpeed(cmobj.optInt("speed"));
							cm.setService(cmobj.optInt("service"));
							cm.setReplyUserId(cmobj.optInt("reply_user_id"));
							cm.setReplyUserName(TextUtils.optString(cmobj,"reply_username"));
							cm.setCreateTime(TextUtils.optString(cmobj,"createdTime"));
							cm.setStatus(cmobj.optInt("status"));
							cm.setScore(cmobj.getInt("score"));
							commentList.add(cm);
						}
					}
					shopInfoSliding.setRecentComments(commentList);
				}

				JSONArray promotionArray = obj.optJSONArray("promotions");
				if(promotionArray != null){
					ArrayList<Promotion> promotions = new ArrayList<>();
					for(int i = 0; i<promotionArray.length(); i++){

						JSONObject pObj = promotionArray.optJSONObject(i);
						if(pObj != null){
							Promotion promotion = new Promotion();
							String label = TextUtils.optString(pObj,"label");
							Log.e("xx", "118----------------label="+label);
							promotion.setLabel(label);
							promotion.setTitle(pObj.getString("title"));
							promotion.setBgcolor(pObj.getString("bgcolor"));
							promotions.add(promotion);
						}

					}
					shopInfoSliding.setPromotions(promotions);
				}

			}
			return mr;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
