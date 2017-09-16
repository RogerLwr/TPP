package com.tianpingpai.seller.manager;

import android.util.Log;
import android.util.Pair;

import com.tianpingpai.core.ModelManager;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.seller.dao.ShopRemarkDao;
import com.tianpingpai.seller.dao.ShoppingCartDao;
import com.tianpingpai.seller.model.ShopRemark;
import com.tianpingpai.utils.SingletonFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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
		if(product.getCartStatus() < ProductModel.STATUS_IN_CART){
			product.setCartStatus(ProductModel.STATUS_MARKED);//TODO
		}
		if (product.getProductNum() == 0) {
			ShoppingCartDao.getInstance().delete(product);
		} else {
			ShoppingCartDao.getInstance().save(product);
		}
		Log.e("xx","product num:" + product.getProductNum());
		notifyEvent(ShoppingCartEvent.OnItemsChange, product);
	}

	public void addToShoppingCart(Collection<ProductModel> products) {
		for (ProductModel product : products) {
			product.setCartStatus(ProductModel.STATUS_IN_CART);
			if (product.getProductNum() == 0) {
				if (ShoppingCartDao.getInstance().contains(product)) {
					ShoppingCartDao.getInstance().delete(product);
				}
			} else {
				ShoppingCartDao.getInstance().save(product);
				Log.e("xx","saving:" + product);
				Log.e("xx","saving:" + product + product.getId() + "," + product.getCategoryId() + "," + product.getProductNum());
			}
		}
		notifyEvent(ShoppingCartEvent.OnItemsChange,null);
	}
	
	public void clearShoppingCart(){
		ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> items = ShoppingCartDao.getInstance().getInOrderItems();
		for(Pair<SellerModel,ArrayList<ProductModel>> p:items){
//			shopRemarkDao.deleteBySellerId(p.first.getId());
		}
		ShoppingCartDao.getInstance().clearShoppingCart();
		
		notifyEvent(ShoppingCartEvent.OnItemsChange, null);
	}
	
	public double getTotalPrice(){
		return ShoppingCartDao.getInstance().getTotalPrice(ProductModel.STATUS_IN_ORDER);
	}
	
	public double getTotalPricForInTimeOrders(){
        double sum = 0;
        if(inTimeOrders != null){
            for(ProductModel pm:inTimeOrders){
                sum += pm.getCouponPrice() * pm.getProductNum();
            }
        }
        return sum;
//		return ShoppingCartDao.getInstance().getTotalPrice(ProductModel.STATUS_IN_ORDER_IN_TIME);
	}
	
	
	public void syncProductNumberFromDb(Collection<ProductModel> pms,boolean isMultiShop) {
		ShoppingCartDao.getInstance().syncProductStateFromDb(pms, isMultiShop);
	}
	
	public void syncRemarksFromDb(Collection<ProductModel> pms){
		ShoppingCartDao.getInstance().syncRemarksFromDb(pms);
	}

	public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getShoppingCartItems() {
		return ShoppingCartDao.getInstance().getShoppingCartItems();
	}
	
	public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getInOrderItems() {
		return ShoppingCartDao.getInstance().getInOrderItems();
	}
	
	public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getInTimeOrderItems() {
//		return ShoppingCartDao.getInstance().getInTimeOrderItems();
//	    return inTimeOrders;

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

	public void delete(Collection<ProductModel> selection) {
		for(ProductModel pm:selection){
			ShoppingCartDao.getInstance().delete(pm);
		}
		notifyEvent(ShoppingCartEvent.OnItemsChange, null);
	}

	public void save(Collection<ProductModel> productList) {
		ShoppingCartDao.getInstance().save(productList);
	}
	
	public void save(ProductModel p){
		ShoppingCartDao.getInstance().save(p);
	}
	
	public ShopRemark getRemarkForSeller(long sellerId){
		return shopRemarkDao.getBySellerId(sellerId);
	}

	public void saveRemark(ShopRemark remark){
		shopRemarkDao.save(remark);
	}

	private ShopRemarkDao shopRemarkDao = new ShopRemarkDao();

	public void clearInTimeOrder() {
        this.inTimeOrders = null;
		ShoppingCartDao.getInstance().clearInTimeOrder();
	}

    public void clearRemarks() {
        shopRemarkDao.clear();
    }
}
