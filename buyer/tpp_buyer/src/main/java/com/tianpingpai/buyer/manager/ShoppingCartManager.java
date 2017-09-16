package com.tianpingpai.buyer.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.util.Pair;

import com.tianpingpai.buyer.db.ShopRemarkDao;
import com.tianpingpai.buyer.db.ShoppingCartDao;
import com.tianpingpai.buyer.model.ShopRemark;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.utils.SingletonFactory;

public class ShoppingCartManager extends
		ModelManager<ShoppingCartEvent, ProductModel> {

	public static ShoppingCartManager getInstance() {
		return SingletonFactory.getInstance(ShoppingCartManager.class);
	}

	private Collection<ProductModel> inTimeOrders;

	public void setInTimeOrders(Collection<ProductModel> models){
		this.inTimeOrders = models;
	}

	public void mark(ProductModel product) {
		notifyEvent(ShoppingCartEvent.OnProductNumberChange,null);
	}

	public void addToShoppingCart(Collection<ProductModel> products) {
		String url = ContextProvider.getBaseURL() + "/api/shoppingCart/add";
		ArrayList<Model> selection = new ArrayList<>();
		HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, new HttpRequest.ResultListener<ModelResult<Void>>() {
			@Override
			public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
				if(data.isSuccess()){
					notifyEvent(ShoppingCartEvent.OnSuccess,null);
				}else{
					notifyEvent(ShoppingCartEvent.OnFailure,null);
				}
			}
		});
		for(ProductModel pm:products){
			if(pm.getProductNum() <= 0){
				continue;
			}
			Model model = new Model();
			model.set("prod_id",pm.getId());
			model.set("num",pm.getProductNum());
			selection.add(model);
		}
		String prods = Model.valueString(selection);
		req.addParam("prods",prods);
		req.setMethod(HttpRequest.POST);
		req.setParser(new ModelParser<>(Void.class));
		req.setErrorListener(syncErrorListener);
		VolleyDispatcher.getInstance().dispatch(req);
		notifyEvent(ShoppingCartEvent.OnSync,null);
	}
	
	public void clearShoppingCart(){
		ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> items = ShoppingCartDao.getInstance().getInOrderItems();
		for(Pair<SellerModel ,ArrayList<ProductModel>> p:items){
			shopRemarkDao.deleteBySellerId(p.first.getId());
		}
		ShoppingCartDao.getInstance().clearShoppingCart();
		notifyEvent(ShoppingCartEvent.OnItemsChange, null);
	}
	
	public void syncProductNumberFromDb(Collection<ProductModel> pms,boolean isMultiShop) {
//		ShoppingCartDao.getInstance().syncProductStateFromDb(pms, isMultiShop);
	}
	
	public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getShoppingCartItems() {
		return ShoppingCartDao.getInstance().getShoppingCartItems();
	}
	
	public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getInOrderItems() {
//		return ShoppingCartDao.getInstance().getInOrderItems();
		return inOrderItems;
	}

	ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> inOrderItems;
	public void setInOrderItems(ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> items){
		inOrderItems = items;
	}


	public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getInTimeOrderItems() {
        HashMap<SellerModel,ArrayList<ProductModel>> map = new HashMap<>();
        ArrayList<Pair<SellerModel,ArrayList<ProductModel>>> orders = new ArrayList<>();
        for(ProductModel pm:inTimeOrders){
            SellerModel sm = new SellerModel();
            sm.setId(pm.getSellerId());
            sm.setDisplayName(pm.getSellerName());
            ArrayList<ProductModel> products = map.get(sm);
            if(products == null){
                products = new ArrayList<>();
            }
            products.add(pm);
            map.put(sm,products);
        }

        for(SellerModel sm:map.keySet()){
            orders.add(new Pair<>(sm,map.get(sm)));
        }
        return orders;
    }
	
	public void resetShoppingCartStatus(){
		ShoppingCartDao.getInstance().resetShoppingCartStatus();
	}

	public void delete(final Collection<ProductModel> selection) {
		String url = ContextProvider.getBaseURL() + "/api/shoppingCart/delete";
		HttpRequest<ModelResult<ModelResult<Void>>> req = new HttpRequest<>(url, new HttpRequest.ResultListener<ModelResult<ModelResult<Void>>>() {
			@Override
			public void onResult(HttpRequest<ModelResult<ModelResult<Void>>> request, ModelResult<ModelResult<Void>> data) {
				if(data.isSuccess()){
					notifyEvent(ShoppingCartEvent.OnDeleteSuccess,null);
				}else{
					notifyEvent(ShoppingCartEvent.OnFailure,null);
				}
			}
		});
		StringBuilder sb = new StringBuilder();
		for(ProductModel pm:selection){
			sb.append(pm.getId());
			sb.append(",");
		}
		req.addParam("prod_ids",sb.toString());
		req.setMethod(HttpRequest.POST);
		req.setParser(new ModelParser<>(Void.class));
		req.setErrorListener(syncErrorListener);
		VolleyDispatcher.getInstance().dispatch(req);
		notifyEvent(ShoppingCartEvent.OnSync,null);
	}

	public void saveRemark(final ShopRemark remark){
		String url = ContextProvider.getBaseURL() + "/api/shoppingCart/remarks";
		HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, new HttpRequest.ResultListener<ModelResult<Void>>() {
			@Override
			public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
				if(data.isSuccess()){
					remark.getSellerModel().setRemark(remark.getRemark());
					notifyEvent(ShoppingCartEvent.OnSuccess,null);
				}else{
					notifyEvent(ShoppingCartEvent.OnFailure,null);
				}
			}
		});
		req.setMethod(HttpRequest.POST);
		req.addParam("saler_id",String.valueOf(remark.getSellerId()));
		req.addParam("content",remark.getRemark());
		req.setParser(new ModelParser<>(Void.class));
		req.setErrorListener(syncErrorListener);
		VolleyDispatcher.getInstance().dispatch(req);
		notifyEvent(ShoppingCartEvent.OnSync,null);
	}
	
	private ShopRemarkDao shopRemarkDao = new ShopRemarkDao();

	public void updateNumber(final ProductModel p, final int productNumber){
		String url = ContextProvider.getBaseURL() + "/api/shoppingCart/add";
		ArrayList<Model> selection = new ArrayList<>();
		HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, new HttpRequest.ResultListener<ModelResult<Void>>() {
			@Override
			public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
				p.setProductNum(productNumber);
				if(data.isSuccess()){
					notifyEvent(ShoppingCartEvent.OnSuccess,null);
				}else{
					notifyEvent(ShoppingCartEvent.OnFailure,null);
				}
			}
		});
			Model model = new Model();
			model.set("prod_id",p.getId());
			model.set("num",productNumber);
			selection.add(model);
		String prods = Model.valueString(selection);
		req.addParam("prods",prods);
		req.setMethod(HttpRequest.POST);
		req.setParser(new ModelParser<>(Void.class));
		req.setErrorListener(syncErrorListener);
		VolleyDispatcher.getInstance().dispatch(req);
		notifyEvent(ShoppingCartEvent.OnSync,null);

	}

	private HttpRequest.ErrorListener syncErrorListener = new HttpRequest.ErrorListener() {
		@Override
		public void onError(HttpRequest<?> request, HttpError error) {
			notifyEvent(ShoppingCartEvent.OnFailure,null);
		}
	};
}
