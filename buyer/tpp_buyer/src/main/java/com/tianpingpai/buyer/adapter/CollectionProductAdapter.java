package com.tianpingpai.buyer.adapter;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.buyer.ui.CollectionProductViewController;
import com.tianpingpai.buyer.ui.StoreViewController;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.TextUtils;
import com.tianpingpai.viewController.ProdDetailViewController;


@SuppressWarnings("unused")
public class CollectionProductAdapter extends ModelAdapter<Model> {

    private Activity activity;
    public ActionSheet as;
    public ProdDetailViewController pdvc;
    public void setActivity(Activity activity){
        this.activity = activity;
    }

    private CollectionProductViewController collectionProductViewController;
    public void setCollectionProductViewController(CollectionProductViewController collectionProductViewController){
        this.collectionProductViewController = collectionProductViewController;
    }
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CollectionProductViewHolder(inflater);
    }

    class CollectionProductViewHolder implements ViewHolder<Model>{

        private Model mModel;
        private View view;
        private Binder binder = new Binder();
        @Binding(id = R.id.store_button)
        private View topView ;
        @Binding(id = R.id.store_title_text_view,format = "{{salerName}}")
        private TextView storeTitleTextView;
        @Binding(id = R.id.sale_number_text_view,format = "促销商品({{salesProdNum}})")
        private TextView saleNumberTextView;

        @Binding(id = R.id.thumbnail_image_view)
        private ImageView thumbnailImageView;
        @Binding(id = R.id.name_text_view,format = "{{prodName}}")
        private TextView nameTextView;
        @Binding(id = R.id.price_text_view)
        private TextView priceTextView;
        @Binding(id = R.id.original_price_text_view)
        private TextView originalPriceTextView;
        @Binding(id = R.id.desc_text_view,format = "描述:{{description}}")
        private TextView descTextView;
        @Binding(id = R.id.number_text_view)
        private TextView numberTextView;
        @Binding(id = R.id.minus_button)
        private View minusButton;
        @Binding(id = R.id.countdown_text_view)
        private TextView countdownTextView;
        @Binding(id = R.id.off_shelves)
        private TextView offShelves;
        @Binding(id = R.id.standard_text_view)
        private TextView standardTextView;
        @Binding(id = R.id.limit_text_view)
        private TextView limitTextView;
        @Binding(id = R.id.stock_text_view)
        private TextView stockTextView;
        @Binding(id = R.id.salesNum_text_view)
        private TextView salesNumTextView;
        @Binding(id = R.id.sale_inf_container)
        private View saleInfContainer;
        @Binding(id = R.id.sale_mark_view)
        private View saleMarkView;
        @Binding(id = R.id.add_container)
        private View addContainer;

        private int greenColor = ContextProvider.getContext().getResources().getColor(R.color.green_0cc486);


        public CollectionProductViewHolder(LayoutInflater inflater){
            view = inflater.inflate(R.layout.item_collection_product_out, null);
            binder.bindView(this, view);

            view.setOnClickListener(productDetailButton);
            view.setOnLongClickListener(productLongClickListener);
            nameTextView.setGravity(Gravity.CENTER);

            final View addButtonView = view.findViewById(R.id.add_button);
            addButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = 1;
                    try {
                        num = Integer.parseInt(numberTextView.getText().toString());
                        num++;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    num = Math.max(1, num);
                    setProductNum(num, true);

                }
            });

            view.findViewById(R.id.minus_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = 0;
                    try {
                        num = Integer.parseInt(numberTextView.getText().toString());
                        num--;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    num = Math.max(0, num);
                    setProductNum(num, true);
                }
            });
        }

        private void setProductNum(int num, boolean notify) {
            if (num == 0) {
                numberTextView.setText("");
            } else {
                numberTextView.setText(String.valueOf(num));
            }
            mModel.set("prod_num",num);
            if (notify) {
                ShoppingCartManager.getInstance().mark(null);//TODO
            }

            if (num > 0) {
                minusButton.setVisibility(View.VISIBLE);
                numberTextView.setVisibility(View.VISIBLE);
            } else {
                minusButton.setVisibility(View.INVISIBLE);
                numberTextView.setVisibility(View.INVISIBLE);
            }
        }


        @Override
        public View getView() {
            return view;
        }

        @Override
        public void setModel(Model model) {
            this.mModel = model;
            binder.bindData(model);
            descTextView.setText(TextUtils.getVlaue(model.getString("prod_quality"))+TextUtils.getVlaue(model.getString("prod_attr"))+TextUtils.getVlaue(model.getString("description")));
            priceTextView.setText("￥"+model.getDouble("couponPrice")+"/"+model.getString("unit"));
            numberTextView.setText(""+mModel.getInt("prod_num"));
            boolean isSale = model.getBoolean("salesProd");
            if(isSale){
                saleMarkView.setVisibility(View.VISIBLE);
                priceTextView.setText("￥"+model.getDouble("salesPrice")+"/"+model.getString("unit"));
                originalPriceTextView.setText("￥"+model.getDouble("originPrice")+"/"+model.getString("unit"));
                originalPriceTextView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
                originalPriceTextView.setVisibility(View.VISIBLE);
                String days = model.getString("days");
                String hours = model.getString("hours");
                String minutes = model.getString("minutes");
                String [] ss = new String[]{"剩余:","天","时","分"};
                String time = ss[0]+days+ss[1]+hours+ss[2]+minutes+ss[3];
                SpannableStringBuilder timeSSB = new SpannableStringBuilder(time);
                timeSSB.setSpan(new ForegroundColorSpan(Color.RED),ss[0].length(),ss[0].length()+days.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                timeSSB.setSpan(new ForegroundColorSpan(Color.RED),ss[0].length()+days.length()+ss[1].length(),ss[0].length()+days.length()+ss[1].length()+hours.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                timeSSB.setSpan(new ForegroundColorSpan(Color.RED),ss[0].length()+days.length()+ss[1].length()+hours.length()+ss[2].length(),ss[0].length()+days.length()+ss[1].length()+hours.length()+ss[2].length()+minutes.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                countdownTextView.setText(timeSSB);
                countdownTextView.setVisibility(View.VISIBLE);

                String stand = model.getString("stand_weight");
                if(!TextUtils.isEmpty(stand)){
                    standardTextView.setText(stand);
                    standardTextView.setVisibility(View.VISIBLE);
                }else{
                    standardTextView.setVisibility(View.GONE);
                }
                String limitNum = ""+model.getInt("limitNum");
                if(!TextUtils.isEmpty(limitNum)){
                    String newLimitNum = "限购"+limitNum+model.getString("unit");
                    SpannableStringBuilder newLimitNumSSB = new SpannableStringBuilder(newLimitNum);
                    newLimitNumSSB.setSpan(new ForegroundColorSpan(greenColor),2,2+limitNum.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    limitTextView.setText(newLimitNumSSB);
                    limitTextView.setVisibility(View.VISIBLE);
                }else{
                    limitTextView.setVisibility(View.GONE);
                }
                String stock = ""+model.getInt("stock");
                if(!TextUtils.isEmpty(stock)){
                    String newStock = "库存"+stock+model.getString("unit");
                    SpannableStringBuilder stockSSB = new SpannableStringBuilder(newStock);
                    stockSSB.setSpan(new ForegroundColorSpan(greenColor),2,2+stock.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    stockTextView.setText(stockSSB);
                }
                String salesNum = ""+model.getInt("salesNum");
                if(!TextUtils.isEmpty(salesNum)){
                    String newSalesNum = "已售"+salesNum+model.getString("unit");
                    SpannableStringBuilder salesNumSSB = new SpannableStringBuilder(newSalesNum);
                    salesNumSSB.setSpan(new ForegroundColorSpan(greenColor),2,2+salesNum.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    salesNumTextView.setText(salesNumSSB);
                }
                saleInfContainer.setVisibility(View.VISIBLE);

            }else{
                saleMarkView.setVisibility(View.GONE);
                originalPriceTextView.setVisibility(View.GONE);
                countdownTextView.setVisibility(View.GONE);
                saleInfContainer.setVisibility(View.GONE);
            }
            int status = model.getInt("status");
            if(status == 0){
                offShelves.setVisibility(View.VISIBLE);
                offShelves.setText("已下架");
                addContainer.setVisibility(View.INVISIBLE);
            }
            else if(model.getInt("status") == 1){
                offShelves.setVisibility(View.GONE);
                addContainer.setVisibility(View.VISIBLE);
            }else if(model.getInt("status") == 2){
                offShelves.setVisibility(View.VISIBLE);
                offShelves.setText("该宝贝已失效");
                addContainer.setVisibility(View.INVISIBLE);
            }else{
                offShelves.setVisibility(View.GONE);
                addContainer.setVisibility(View.VISIBLE);
            }
            if(model.getBoolean("show_top")){
                topView.setVisibility(View.VISIBLE);
            }else {
                topView.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(model.getString("img"))) {
                this.thumbnailImageView.setImageResource(R.drawable.img_loading_90_70);
                this.thumbnailImageView.setVisibility(View.VISIBLE);
            } else {
                this.thumbnailImageView.setVisibility(View.VISIBLE);
                ImageLoader.load(model.getString("img")+ URLApi.getImageTP(90,70), this.thumbnailImageView, R.drawable.img_loading_90_70, R.drawable.img_loading_90_70);
            }
            if (TextUtils.isEmpty(model.getString("description"))) {
                descTextView.setVisibility(View.GONE);
            } else {
                descTextView.setVisibility(View.VISIBLE);
            }
            int num = model.getInt("prod_num");
            if (num > 0) {
                minusButton.setVisibility(View.VISIBLE);
                numberTextView.setVisibility(View.VISIBLE);
            } else {
                minusButton.setVisibility(View.INVISIBLE);
                numberTextView.setVisibility(View.INVISIBLE);
            }
            setProductNum(mModel.getInt("prod_num"), false);
        }
        @OnClick(R.id.store_button)
        public View.OnClickListener storeButton = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, StoreViewController.class);

                intent.putExtra(StoreDataContainer.KEY_STORE_TYPE,
                        StoreDataContainer.STORE_TYPE_NORMAL);
                intent.putExtra("shop_id", mModel.getInt("salerId"));
                intent.putExtra(StoreDataContainer.KEY_SHOP_NAME, mModel.getString("salerName"));
                activity.startActivity(intent);


            }
        };

        long submitButtonLastClick = 0;
        private View.OnClickListener productDetailButton = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int status = mModel.getInt("status");
                switch (status){
                    case 0:
                        Toast.makeText(activity,"该商品已下架!",Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        openProductDetail();
                        break;
                    case 2:
//                        showDelete("该商品已不存在,是否删除?",false);
                        Toast.makeText(activity,"该商品已不存在!",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        private void openProductDetail(){
            if (System.currentTimeMillis() - submitButtonLastClick < 2000) {
                return;
            }
            submitButtonLastClick = System.currentTimeMillis();
            as = new ActionSheet();
            as.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);// 设置为屏幕高度
            pdvc = new ProdDetailViewController();
            pdvc.setCollectionProductAdapter(CollectionProductAdapter.this);
            pdvc.setActivity((FragmentActivity) activity);
            pdvc.setProdId(mModel.getInt("prodId"));
            ProductModel p = new ProductModel();
            p.setProductNum(mModel.getInt("prod_num"));
            pdvc.setModel(p);
            pdvc.setCollectModel(mModel);
            as.setViewController(pdvc);
            as.show();

        }

        private void showDelete(String message,boolean hide){
            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setActionSheet(new ActionSheet());
            dialog.setMessage(message);
            dialog.setCancelButtonHidden(hide);
            dialog.setActivity((FragmentActivity) activity);
            dialog.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    deleteProduct();
                }
            });
            dialog.setTitle("确定删除该商品吗?");
            dialog.show();
        }

        private void deleteProduct(){
            String url = URLUtil.DEL_STORE_OR_PROD_URL;

            UserModel user = UserManager.getInstance().getCurrentUser();
            HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,new HttpRequest.ResultListener<ModelResult<Model>>(){
                @Override
                public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
                    if(data.isSuccess()){
                        collectionProductViewController.hideSubmitting();
                        getModels().remove(mModel);
                        notifyDataSetChanged();
                        collectionProductViewController.refreshLayoutControl.triggerRefreshDelayed();
                    }
                }
            });
            req.setMethod(HttpRequest.POST);
            req.addParam("salerId", mModel.getInt("salerId") + "");
            req.addParam("type","2");
            req.addParam("prodId", mModel.getInt("prodId") + "");
            GenericModelParser parser = new GenericModelParser();
            req.setParser(parser);
            if(user != null){
                req.addParam("accessToken", user.getAccessToken());
            }
            req.setErrorListener(new CommonErrorHandler(collectionProductViewController));
            VolleyDispatcher.getInstance().dispatch(req);
            collectionProductViewController.showSubmitting();
        }

        private View.OnLongClickListener productLongClickListener = new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                showDelete("",false);
                return true;
            }
        };

    }
}
