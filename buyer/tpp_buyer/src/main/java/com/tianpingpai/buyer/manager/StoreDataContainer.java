package com.tianpingpai.buyer.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.util.Pair;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.model.LayeredProduct;
import com.tianpingpai.buyer.model.LayeredProduct.FirstCategory;
import com.tianpingpai.buyer.model.LayeredProduct.SecondCategory;
import com.tianpingpai.buyer.model.ShopInfoParser;
import com.tianpingpai.buyer.model.StoreModel;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.parser.NoPageListParser;
import com.tianpingpai.buyer.parser.ShopProductsParser;
import com.tianpingpai.buyer.ui.StoreCommonUseViewController;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.tools.TLog;

import java.util.ArrayList;
import java.util.HashMap;

public class StoreDataContainer {

    public static final String KEY_STORE_TYPE = "type";
    public static final String KEY_STORE_ID = "shop_id";
    public static final String KEY_SHOP_NAME = "shop_name";
    public static final int STORE_TYPE_NORMAL = 1;
    public static final int STORE_TYPE_MULTI = 2;
    public static final int STORE_TYPE_MULTI_WITH_ID = 3;

    private int storeId;
    private int catId;
    private int type;
    private String shopName;

    StoreCommonUseViewController storeCommonUseViewController;

    public void setStoreCommonUseViewController(StoreCommonUseViewController storeCommonUseViewController) {
        this.storeCommonUseViewController = storeCommonUseViewController;
    }

    private ModelManager<ModelEvent, LayeredProduct> productManager = new ModelManager<>();
    private ModelManager<ModelEvent, StoreModel> storeManager = new ModelManager<>();
    private ModelManager<Integer,ModelResult<?>> notValidatedManager = new ModelManager<>();

    public ModelManager<Integer,ModelResult<?>> getNotValidatedManager(){
        return notValidatedManager;
    }

    private LayeredProduct layeredProduct;
    private StoreModel storeModel;

    public ModelManager<ModelEvent, LayeredProduct> getProductManager() {
        return productManager;
    }

    public void setProductManager(
            ModelManager<ModelEvent, LayeredProduct> productManager) {
        this.productManager = productManager;
    }

    public ModelManager<ModelEvent, StoreModel> getStoreManager() {
        return storeManager;
    }

    public void setStoreManager(ModelManager<ModelEvent, StoreModel> storeManager) {
        this.storeManager = storeManager;
    }

    public LayeredProduct getLayeredProduct() {
        return layeredProduct;
    }

    public void setLayeredProduct(LayeredProduct layeredProduct) {
        this.layeredProduct = layeredProduct;
    }

    public StoreModel getStoreModel() {
        return storeModel;
    }

    public void setStoreModel(StoreModel storeModel) {
        this.storeModel = storeModel;
    }

    Intent intent = new Intent();

    public void parseParams(Intent intent) {
        storeId = intent.getIntExtra("shop_id", -1);
        if (storeId == -1) {
            storeId = intent.getIntExtra("id", -1);
        }
        if (storeId == -1) {
            storeId = intent.getIntExtra("user_id", -1);// from bar code?
        }

        catId = intent.getIntExtra("category_id", -1);
        type = intent.getIntExtra(KEY_STORE_TYPE, STORE_TYPE_NORMAL);

        shopName = intent.getStringExtra(KEY_SHOP_NAME);
    }

    private ResultListener<ModelResult<LayeredProduct>> listener = new ResultListener<ModelResult<LayeredProduct>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<LayeredProduct>> request,
                             ModelResult<LayeredProduct> data) {
            if (data.isSuccess() && data.getModel() != null) {
                HashMap<Long, Pair<ArrayList<ProductModel>, Integer>> virtualTable = new HashMap<>();
                ArrayList<ProductModel> entities = new ArrayList<>();

                layeredProduct = data.getModel();
                layeredProduct.setStoreId(storeId);
                layeredProduct.setStoreType(type);
                long start = System.currentTimeMillis();
                for (FirstCategory fc : layeredProduct.getCategories()) {
                    if (fc.getId() < 0) {
                        int size = fc.getProducts() == null ? 0 : fc.getProducts().size();
                        for (int i = 0; i < size; i++) {
                            ProductModel p = fc.getProducts().get(i);
                            virtualTable.put(p.getId(), new Pair<>(fc.getProducts(), i));
                        }
                    } else {
                        ArrayList<SecondCategory> secondCats = fc.getSecondCategories();
                        if (secondCats != null) {
                            for (SecondCategory sc : secondCats) {
                                for (int i = 0; i < sc.getProducts().size(); i++) {
                                    ProductModel p = sc.getProducts().get(i);
                                    entities.add(p);
                                }
                            }
                        }
                    }
                }
                Log.e("xx","forloop:" + (System.currentTimeMillis() - start));

                for (ProductModel p : entities) {
                    Pair<ArrayList<ProductModel>, Integer> value = virtualTable.get(p.getId());
                    if (value != null) {
                        value.first.set(value.second, p);
                    }
                }
                Log.e("xx","pair:" + (System.currentTimeMillis() - start));
                ShoppingCartManager.getInstance().syncProductNumberFromDb(data.getModel().getProducts(), isMultiShop() || isMultiShopWithId());
                Log.e("xx", "sync:" + (System.currentTimeMillis() - start));
                productManager.notifyEvent(ModelEvent.OnModelGet, layeredProduct);
                storeManager.notifyEvent(ModelEvent.OnModelGet, storeModel);
                Log.e("xx", "notify:" + (System.currentTimeMillis() - start));

            } else {
                productManager.notifyEvent(ModelEvent.OnModelGetFailed, layeredProduct);
                storeManager.notifyEvent(ModelEvent.OnModelGetFailed, storeModel);
            }
        }
    };

    public void loadProducts() {
        Log.e("xx", "sid:" + storeId + ":type=" + type);
        String mUrl = ContextProvider.getBaseURL() + "/api/shop/getMultiShopProdList.json";

        String url = isMultiShop() ? mUrl
                : URLUtil.GET_SALE_USER_PROD_LIST_INFO_URL;

        HttpRequest<ModelResult<LayeredProduct>> req = new HttpRequest<>(
                url, listener);

        if (isMultiShop()) {
            if (storeId != CommonUtil.DEFAULT_ARG) {
                req.addParam("shop_id", storeId + "");
            }
        } else {
            if (storeId != CommonUtil.DEFAULT_ARG) {
                req.addParam("user_id", storeId + "");
            }
        }

        if (shopName != null) {
            req.setAttachment(shopName);
        }
        // int catId = getActivity().getIntent().getIntExtra("category_id", -1);

        if (catId != -1) {
            req.addParam("category_id", catId + "");
        }

//        if (isMultiShop() || type == STORE_TYPE_MULTI_WITH_ID) {
//            MultiShopParser parser = new MultiShopParser();
//            req.setParser(parser);
//        } else {
//            ShopProductsParser parser = new ShopProductsParser();
//            req.setParser(parser);
//            parser.setShopName(shopName);
//        }
        ShopProductsParser parser = new ShopProductsParser();
        req.setParser(parser);
        parser.setShopName(shopName);

        req.addParam("version", getVersion(ContextProvider.getContext()));
        if (isMultiShop()) {
            MarketModel market = MarketManager.getInstance().getCurrentMarket();
            if (market != null) {
                req.addParam("market_id", market.getId() + "");
            }
        }

        req.setErrorListener(errorListener);
        VolleyDispatcher.getInstance().dispatch(req);

    }

    public boolean isMultiShopWithId() {
        return type == STORE_TYPE_MULTI_WITH_ID;
    }

    public boolean isMultiShop() {
        return type == STORE_TYPE_MULTI;
    }

    public void loadStoreInfo() {
        HttpRequest<ModelResult<StoreModel>> req = new HttpRequest<>(
                URLUtil.GET_SHOP_INFO, new ResultListener<ModelResult<StoreModel>>() {
            @Override
            public void onResult(
                    HttpRequest<ModelResult<StoreModel>> request,
                    ModelResult<StoreModel> data) {
                if (data.isSuccess()) {
                    storeModel = data.getModel();
                    Log.e("xx", "storeId:" + storeId + "||" + storeModel.getId());
                    if (storeModel.getId() != 0) {
                        storeId = storeModel.getId();
                    }
                    type = storeModel.getType();

                    intent.putExtra("shop_id", storeId);
                    intent.putExtra(StoreCommonUseViewController.KEY_STORE_TYPE, 1);
                    storeCommonUseViewController.parseParams(intent);

                    TLog.e("xx", "storeId="+storeId+", type="+type);

                    loadProducts();
//                    loadRelatedStores();
                } else if(data.getCode() == 5){
                    notValidatedManager.notifyEvent(5,null);
                }else{
                    storeManager.notifyEvent(ModelEvent.OnModelGetFailed, storeModel);
                    productManager.notifyEvent(ModelEvent.OnModelGetFailed, layeredProduct);
                }
            }
        });
        if (storeId != -1) {
            req.addParam("shop_id", storeId + "");
        }

        if (catId != -1) {
            req.addParam("category_id", catId + "");
        }

        if (isMultiShop() || isMultiShopWithId()) {
            req.addParam("shop_type", "2");
        } else {
            req.addParam("shop_type", "1");
        }

        req.setErrorListener(errorListener);
        MarketModel market = MarketManager.getInstance().getCurrentMarket();
        if (market != null) {
            req.addParam("market_id", market.getId() + "");
        }

        ShopInfoParser parser = new ShopInfoParser();
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
    }

    public static String getVersion(Context context) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.4.0";
    }

    ArrayList<StoreModel> stores;
    private ResultListener<ModelResult<ArrayList<StoreModel>>> relatedStoreListener = new ResultListener<ModelResult<ArrayList<StoreModel>>>() {
        @Override
        public void onResult(
                HttpRequest<ModelResult<ArrayList<StoreModel>>> request,
                ModelResult<ArrayList<StoreModel>> data) {
            Log.e("xx", "ss" + data.getModel());
            stores = data.getModel();
            storeManager.notifyEvent(ModelEvent.OnModelsGet, null);
        }
    };

    private HttpRequest.ErrorListener errorListener = new HttpRequest.ErrorListener() {
        @Override
        public void onError(HttpRequest<?> request, HttpError eror) {
            storeManager.notifyEvent(ModelEvent.OnNetworkError, null);
            productManager.notifyEvent(ModelEvent.OnNetworkError, null);
        }
    };

    public ArrayList<StoreModel> getRelatedStores() {
        return stores;
    }

    public void loadRelatedStores() {
        String url = URLApi.getBaseUrl() + "/api/shopinfo/" + "getRelatedShops.json";
        HttpRequest<ModelResult<ArrayList<StoreModel>>> req = new HttpRequest<>(url, relatedStoreListener);
        req.addParam("shop_id", storeId + "");
        if (catId != -1) {
            req.addParam("category_id", catId + "");
        }
        if (isMultiShop() || isMultiShopWithId()) {
            req.addParam("shop_type", "2");
        } else {
            req.addParam("shop_type", "1");
        }
        MarketModel market = MarketManager.getInstance().getCurrentMarket();
        if (market != null) {
            req.addParam("market_id", market.getId() + "");
        }
        NoPageListParser<StoreModel> p = new NoPageListParser<>(StoreModel.class);
        req.setParser(p);
        VolleyDispatcher.getInstance().dispatch(req);

    }

    public void loadData() {
        loadStoreInfo();
    }

    public int getStoreId() {
        return storeId;
    }

    public CharSequence getStoreName() {
        return shopName;
    }
}