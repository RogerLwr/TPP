package com.tianpingpai.seller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.tianpingpai.db.BaseDao;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.utils.SingletonFactory;

import java.util.ArrayList;
import java.util.Collection;

public class ShoppingCartDao extends BaseDao<ProductModel> {

    public static ShoppingCartDao getInstance() {
        return SingletonFactory.getInstance(ShoppingCartDao.class);
    }

    public void syncRemarksFromDb(Collection<ProductModel> pms) {
        SQLiteDatabase db = getDb();

        String sql = "SELECT [_id],[remark] FROM ["
                + getTableName()
                + "] WHERE [productId] = ? AND [categoryId] = ? AND [sellerId] = ?";
        for (ProductModel pm : pms) {
            Cursor c = db.rawQuery(
                    sql,
                    new String[]{pm.getId() + "", pm.getCategoryId() + "",
                            pm.getSellerId() + ""});
            if (c.moveToFirst()) {
                pm.setDbId(c.getInt(0));
                pm.setComment(c.getString(1));
            }
            c.close();
        }
    }

    public void syncProductStateFromDb(Collection<ProductModel> pms,
                                       boolean isMultiShop) {
        SQLiteDatabase db = getDb();
        isMultiShop = false;

        String columnName = isMultiShop ? "multiShopId" : "sellerId";
        String sql = "SELECT [productNumber],[_id],[status] ,[remark]FROM ["
                + getTableName()
                + "] WHERE [productId] = ? AND [categoryId] = ? AND ["
                + columnName + "] = ?";
        for (ProductModel pm : pms) {
            Cursor c = db.rawQuery(
                    sql,
                    new String[]{
                            pm.getId() + "",
                            pm.getCategoryId() + "",
                            isMultiShop ? pm.getMultiShopId() + "" : pm
                                    .getSellerId() + ""});
            if (c.moveToFirst()) {
                pm.setProductNum(c.getInt(0));
                pm.setDbId(c.getInt(1));
                pm.setCartStatus(c.getInt(2));
                pm.setComment(c.getString(3));
            }
            c.close();
        }
        // db.close();
    }

    public double getTotalPrice(int cartStatus) {
        SQLiteDatabase db = getDb();
        double totalPrice = 0;
        String sql = "SELECT SUM([productNumber] * [couponPrice]) AS [totalPrice] FROM ["
                + getTableName() + "] WHERE [cartStatus] = " + cartStatus + ";";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            totalPrice = cursor.getDouble(0);
        }
        cursor.close();
        // db.close();
        return totalPrice;
    }

    public void clearShoppingCart() {
        SQLiteDatabase db = getDb();
        db.execSQL("DELETE FROM [" + getTableName() + "] WHERE [cartStatus]="
                + ProductModel.STATUS_IN_ORDER + ";");
        // db.close();
        Log.e("xx", "clearing table");
    }

    public void clearInTimeOrder() {
        SQLiteDatabase db = getDb();
        db.execSQL("DELETE FROM [" + getTableName() + "] WHERE [cartStatus]="
                + ProductModel.STATUS_IN_ORDER_IN_TIME + ";");
        // db.close();
        Log.e("xx", "clearing in time table");
    }

    public void resetShoppingCartStatus() {
        SQLiteDatabase db = getDb();
        ContentValues cv = new ContentValues();
        cv.put("cartStatus", ProductModel.STATUS_IN_CART);
        db.update(getTableName(), cv, "[cartStatus] = "
                + ProductModel.STATUS_IN_ORDER, null);

        db.delete(getTableName(), "[cartStatus] = "
                + ProductModel.STATUS_IN_ORDER_IN_TIME, null);
        // db.close();
    }

    public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getShoppingCartItems() {
        SQLiteDatabase db = getDb();
        ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> cartItems = new ArrayList<>();
        Cursor storeCursor = db.rawQuery(
                "SELECT DISTINCT [sellerId],[shopName] FROM [" + getTableName()
                        + "] WHERE [cartStatus] <="
                        + ProductModel.STATUS_IN_CART + " ;", null);
        while (storeCursor.moveToNext()) {
            SellerModel seller = new SellerModel();
            int sellerId = storeCursor.getInt(0);
            seller.setId(sellerId);
            seller.setDisplayName(storeCursor.getString(1));
            Cursor productCursor = db.rawQuery(
                    "SELECT * FROM [" + getTableName()
                            + "] WHERE [sellerId] = ? AND [cartStatus] = ?;",
                    new String[]{String.valueOf(sellerId),
                            String.valueOf(ProductModel.STATUS_IN_CART)});
            ArrayList<ProductModel> products = new ArrayList<>();
            while (productCursor.moveToNext()) {
                ProductModel product = new ProductModel();
                fillFields(product, productCursor);
                products.add(product);
            }
            cartItems.add(new Pair<>(
                    seller, products));
            productCursor.close();
        }
        storeCursor.close();
        // db.close();
        return cartItems;
    }

    // ------------------------------------

    @Override
    protected Pair<String, String> getPrimaryKeyAndValue(ProductModel e) {
        return new Pair<>("_id", "" + e.getDbId());
    }

    @Override
    protected String getTableName() {
        return "shopping_cart_table";
    }

    @Override
    protected String getDatabaseFileName() {
        return "buyer.db";
    }

    @Override
    protected int getVersion() {
        return 3;
    }

    @Override
    protected void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        StringBuilder sf = new StringBuilder();
        sf.append("Create  TABLE [" + getTableName() + "](");
        sf.append("		  [_id] INTEGER PRIMARY KEY AUTOINCREMENT"); // PRIMARY KEY
        sf.append("      ,[productId] INTEGER");
        sf.append("      ,[name] TEXT");
        sf.append("      ,[cartStatus] INTEGER");
        sf.append("      ,[sellerId] INTEGER");
        sf.append("      ,[multiShopId] TEXT");
        sf.append("      ,[shopName] TEXT");
        sf.append("      ,[productNumber] INTEGER");
        sf.append("      ,[price] FLOAT");
        sf.append("      ,[couponPrice] FLOAT");
        sf.append("      ,[unit] TEXT");
        sf.append("      ,[categoryId] INTEGER");
        sf.append("      ,[categoryName] TEXT");
        sf.append("		 ,[shopDisplayName] TEXT");
        sf.append("      ,[status] INTEGER");
        sf.append("      ,[remark] TEXT");
        sf.append("      ,[productImage] TEXT");
        sf.append(");");
        db.execSQL(sf.toString());

    }

    @Override
    protected void fillContentValues(ContentValues cv, ProductModel e) {
        cv.put("productId", e.getId());
        cv.put("name", e.getName());
        // cv.put("cartStatus", e.getS);
        cv.put("sellerId", e.getSellerId());
        cv.put("categoryId", e.getCategoryId());
        cv.put("multiShopId", e.getMultiShopId());
        cv.put("shopName", e.getSellerName());
        cv.put("productNumber", e.getProductNum());
        cv.put("couponPrice", e.getCouponPrice());
        // cv.put("totalPrice", e.getPrice() * e.getProductNum());
        if (!TextUtils.isEmpty(e.getComment())) {
            cv.put("remark", e.getComment());// TODO
        }

        cv.put("cartStatus", e.getCartStatus());
        cv.put("unit", e.getUnit());
    }

    @Override
    protected void fillFields(ProductModel e, Cursor c) {
        super.fillFields(e, c);
        e.setDbId(c.getInt(c.getColumnIndex("_id")));
        e.setName(c.getString(c.getColumnIndex("name")));
        e.setId(c.getLong(c.getColumnIndex("productId")));
        e.setCartStatus(c.getInt(c.getColumnIndex("cartStatus")));
        e.setSellerId(c.getInt(c.getColumnIndex("sellerId")));
        e.setCategoryId(c.getInt(c.getColumnIndex("categoryId")));
        e.setProductNum(c.getInt(c.getColumnIndex("productNumber")));
        e.setCouponPrice(c.getDouble(c.getColumnIndex("couponPrice")));
        e.setComment(c.getString(c.getColumnIndex("remark")));
        e.setUnit(c.getString(c.getColumnIndex("unit")));
        e.setSellerName(c.getString(c.getColumnIndex("shopName")));
    }


    @Override
    public boolean contains(ProductModel e) {
        SQLiteDatabase db = getDb();
        Cursor c = db.rawQuery("SELECT [_id] FROM [" + getTableName()
                + "] WHERE [sellerId] = ? AND [productId] = ?;", new String[]{
                String.valueOf(e.getSellerId()), String.valueOf(e.getId())});
        boolean contains = c.moveToFirst();
        Log.e("xx", "" + c.getCount());
        c.close();
        // db.close();
        return contains;
    }

    @Override
    public boolean delete(ProductModel e) {
        SQLiteDatabase db = getDb();
        String id = String.valueOf(e.getId());
        String catId = String.valueOf(e.getCategoryId());
        String sellerId = String.valueOf(e.getSellerId());

        String sql = "DELETE FROM ["
                + getTableName()
                + "] WHERE [productId] = ? AND [categoryId] = ? AND [sellerId] = ? ;";

        String[] args = new String[]{id, catId, sellerId};

        db.execSQL(sql, args);
        // db.close();
        return super.delete(e);
    }

    @Override
    protected boolean update(ProductModel e) {
        SQLiteDatabase db = getDb();
        String id = String.valueOf(e.getId());
        String catId = String.valueOf(e.getCategoryId());
        String sellerId = String.valueOf(e.getSellerId());
        ContentValues cv = new ContentValues();
        fillContentValues(cv, e);
        db.update(getTableName(), cv,
                "[productId] = ? AND [categoryId] = ? AND [sellerId] = ?",
                new String[]{id, catId, sellerId});
        // db.close();
        return true;
    }

    public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getInOrderItems() {
        SQLiteDatabase db = getDb();
        ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> cartItems = new ArrayList<>();
        Cursor storeCursor = db.rawQuery(
                "SELECT DISTINCT [sellerId],[shopName] FROM [" + getTableName()
                        + "] WHERE [cartStatus] ="
                        + ProductModel.STATUS_IN_ORDER + " ;", null);
        while (storeCursor.moveToNext()) {
            SellerModel seller = new SellerModel();
            int sellerId = storeCursor.getInt(0);
            seller.setId(sellerId);
            seller.setDisplayName(storeCursor.getString(1));
            Cursor productCursor = db.rawQuery(
                    "SELECT * FROM [" + getTableName()
                            + "] WHERE [sellerId] = ? AND [cartStatus] = ?;",
                    new String[]{String.valueOf(sellerId),
                            String.valueOf(ProductModel.STATUS_IN_ORDER)});
            ArrayList<ProductModel> products = new ArrayList<>();
            while (productCursor.moveToNext()) {
                ProductModel product = new ProductModel();
                fillFields(product, productCursor);
                products.add(product);
            }
            cartItems.add(new Pair<>(
                    seller, products));
            productCursor.close();
        }
        storeCursor.close();
        // db.close();
        return cartItems;
    }

    public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getInTimeOrderItems() {
        SQLiteDatabase db = getDb();
        ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> cartItems = new ArrayList<>();
        Cursor storeCursor = db.rawQuery(
                "SELECT DISTINCT [sellerId],[shopName] FROM [" + getTableName()
                        + "] WHERE [cartStatus] ="
                        + ProductModel.STATUS_IN_ORDER_IN_TIME + " ;", null);
        while (storeCursor.moveToNext()) {
            SellerModel seller = new SellerModel();
            int sellerId = storeCursor.getInt(0);
            seller.setId(sellerId);
            seller.setDisplayName(storeCursor.getString(1));
            Cursor productCursor = db.rawQuery(
                    "SELECT * FROM [" + getTableName()
                            + "] WHERE [sellerId] = ? AND [cartStatus] = ?;",
                    new String[]{String.valueOf(sellerId),
                            String.valueOf(ProductModel.STATUS_IN_ORDER_IN_TIME)});
            ArrayList<ProductModel> products = new ArrayList<>();
            while (productCursor.moveToNext()) {
                ProductModel product = new ProductModel();
                fillFields(product, productCursor);
                products.add(product);
            }
            cartItems.add(new Pair<>(seller, products));
            productCursor.close();
        }
        storeCursor.close();
        // db.close();
        return cartItems;
    }
}
