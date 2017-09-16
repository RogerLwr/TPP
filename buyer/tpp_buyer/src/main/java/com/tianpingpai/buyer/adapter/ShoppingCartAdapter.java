package com.tianpingpai.buyer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.model.ShopRemark;
import com.tianpingpai.buyer.ui.InputRemarkViewController;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.buyer.ui.ShoppingCartViewController;
import com.tianpingpai.buyer.ui.StoreViewController;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.tools.Tools;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ShoppingCartAdapter extends
        ModelAdapter<Pair<SellerModel, ArrayList<ProductModel>>> {

    public interface OnProductSelectionChangeListener {
        void onProductSelectionChange();
    }

    @Override
    public void setModels(
            ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> models) {

        super.setModels(models);
        selectionSet.clear();
        selectionSellerSet.clear();

    }

    private OnProductSelectionChangeListener selectionListener;

    public void setOnProductSelectionChangeListener(
            OnProductSelectionChangeListener l) {
        this.selectionListener = l;
    }

    private OnCheckedChangeListener checkChangedListener;

    public void setCheckChangedListener(
            OnCheckedChangeListener checkChangedListener) {
        this.checkChangedListener = checkChangedListener;
    }

    public void setShoppingCartViewController(ShoppingCartViewController shoppingCartViewController) {
        this.shoppingCartViewController = shoppingCartViewController;
    }

    private ShoppingCartViewController shoppingCartViewController;

    public void selectAll() {
        for (int i = 0; i < getCount(); i++) {
            Pair<SellerModel, ArrayList<ProductModel>> item = getItem(i);

            // TODO abstract
            SellerModel seller = item.first;
            boolean isValid = false;
            double priceSum = 0;

            for (ProductModel pm : item.second) {
                selectionSet.add(pm);
                priceSum = pm.getCouponPrice() * pm.getProductNum();
            }
            if (priceSum >= seller.getMinAmount()) {
                isValid = true;
            } else {
                if (seller.getFreight() > 0) {
                    isValid = true;
                }
            }

            for (final ProductModel p : item.second) {
                validationSet.put(p, isValid);
            }
        }
        if (checkChangedListener != null) {
            checkChangedListener.onCheckedChanged(null, true);
        }
        selectionListener.onProductSelectionChange();
        notifyDataSetChanged();
    }

    public void removeAll() {
        selectionSet.clear();
        selectionSellerSet.clear();
        if (checkChangedListener != null) {
            checkChangedListener.onCheckedChanged(null, false);
        }
        selectionListener.onProductSelectionChange();
        notifyDataSetChanged();
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private Activity activity;

    @Override
    protected com.tianpingpai.ui.ModelAdapter.ViewHolder<Pair<SellerModel, ArrayList<ProductModel>>> onCreateViewHolder(
            LayoutInflater inflater) {
        return new CartItemViewHolder(inflater);
    }

    private class CartItemViewHolder implements
            ViewHolder<Pair<SellerModel, ArrayList<ProductModel>>> {
        private View view;

        @Binding(id = R.id.store_name_text_view)
        private TextView storeNameTextView;
        @Binding(id = R.id.products_container)
        private ViewGroup productsContainer;
        @Binding(id = R.id.price_sum_text_view)
        private TextView priceSumTextView;
        @Binding(id = R.id.freight_text_view)
        private TextView freightTextView;
        @Binding(id = R.id.freight_status_text_view)
        private TextView freightStatusTextView;
        @Binding(id = R.id.total_sum_text_view)
        private TextView totalSumTextView;
        @Binding(id = R.id.remark_text_view)
        private TextView remarkTextView;
        @Binding(id = R.id.remark_hint_text_view)
        private TextView remarkHintTextView;
        @Binding(id = R.id.price_container)
        private View priceContainer;
        @Binding(id = R.id.store_full_status)
        private TextView storeFullStatus;
        @Binding(id =R.id.arrow_status)
        private ImageView arrowStatusView;

        private Binder binder = new Binder();

        private GroupSelectionListener groupSelectionListener = new GroupSelectionListener();

        {
            groupSelectionListener.holder = this;
        }


        private int sellerId;
        private SellerModel seller;
        private ArrayList<ProductModel> products;

        private void setRemark(String text) {
            remarkTextView.setText(text);
            if (!TextUtils.isEmpty(text)) {
                remarkHintTextView.setVisibility(View.GONE);
            } else {
                remarkHintTextView.setVisibility(View.VISIBLE);
            }
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.remark_container)
        private OnClickListener orderRemarkListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = remarkTextView.getText().toString();
                ActionSheet actionSheet;
                if(shoppingCartViewController.getMainFragment() != null){
                    actionSheet = shoppingCartViewController.getMainFragment().getActionSheet(true);
                }else{
                    actionSheet = shoppingCartViewController.getActionSheet(true);
                }

                actionSheet.setHeight(shoppingCartViewController.getContentHeight());
                InputRemarkViewController inputRemarkViewController = new InputRemarkViewController();
                inputRemarkViewController.setActivity(shoppingCartViewController.getActivity());
                inputRemarkViewController.setOnRemarkConfirmListener(new InputRemarkViewController.OnRemarkConfirmListener() {
                    @Override
                    public void onRemarkConfirm(String text) {
                        ShopRemark remark = new ShopRemark();
                        remark.setSellerModel(seller);
                        remark.setSellerId(seller.getId());
                        remark.setRemark(text);
                        ShoppingCartManager.getInstance().saveRemark(
                                remark);
                        setRemark(text);
                    }
                });
                inputRemarkViewController.setText(text);
                actionSheet.setViewController(inputRemarkViewController);
                actionSheet.show();
            }
        };

        public CartItemViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_shopping_cart, null);
            binder.bindView(this, view);
            view.findViewById(R.id.to_store_button).setOnClickListener(
                    toStoreButtonListener);
            groupSelectionListener.setCategoryCheckBox((CompoundButton) view
                    .findViewById(R.id.store_check_box));
        }

        @Override
        public void setModel(Pair<SellerModel, ArrayList<ProductModel>> model) {
            SellerModel seller = model.first;
            products = model.second;
            this.seller = seller;
            sellerId = (int) seller.getId();
            storeNameTextView.setText(seller.getDisplayName());

            ArrayList<ProductModel> products = model.second;
            productsContainer.removeAllViews();
            groupSelectionListener.subBoxes.clear();

            for (int i = 0; i < products.size(); i++) {
                final ProductModel p = products.get(i);
                View v = LayoutInflater.from(ContextProvider.getContext())
                        .inflate(R.layout.item_shopping_cart_product, null);
                productsContainer.addView(v);
                if (i < products.size() - 1) {
                    View dividerLine = LayoutInflater.from(ContextProvider.getContext())
                            .inflate(R.layout.item_shopping_cart_product_divider, null);
                    productsContainer.addView(dividerLine);
                }
                TextView nameTextView = (TextView) v
                        .findViewById(R.id.name_text_view);
                final TextView numberTextView = (TextView) v
                        .findViewById(R.id.number_text_view);
                TextView priceTextView = (TextView) v
                        .findViewById(R.id.price_text_view);
                final CompoundButton productCheckBox = (CompoundButton) v
                        .findViewById(R.id.product_check_box);
                productCheckBox.setTag(p);
                groupSelectionListener.addSubCheckBox(productCheckBox);
                productCheckBox.setChecked(selectionSet.contains(p));

                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productCheckBox.setChecked(!productCheckBox.isChecked());
                    }
                });

                v.findViewById(R.id.increase_button).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int productNumber = 0;
                                try {
                                    productNumber = Integer
                                            .parseInt(numberTextView.getText()
                                                    .toString());
                                } catch (NumberFormatException ee) {
                                    ee.printStackTrace();//TODO report
                                }
                                productNumber++;
//                                numberTextView.setText(productNumber + "");
                                ShoppingCartManager.getInstance().updateNumber(p,productNumber);
                            }
                        });

                v.findViewById(R.id.decrease_button).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int productNumber = 0;
                                try {
                                    productNumber = Integer
                                            .parseInt(numberTextView.getText()
                                                    .toString());
                                } catch (NumberFormatException ee) {
                                    ee.printStackTrace();//TODO
                                }
                                if (productNumber <= 1) {
                                    return;
                                }
                                productNumber--;
//                                numberTextView.setText(String.valueOf(productNumber));
                                ShoppingCartManager.getInstance().updateNumber(p,productNumber);
                            }
                        });
                v.findViewById(R.id.number_text_view).setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                });
//                OnClickListener numberTextListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View vv = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_number, null);
                        final Dialog d = new AlertDialog.Builder(getActivity()).setView(vv).create();
                        final EditText numberEditText = (EditText) vv.findViewById(R.id.number_edit_text);
                        if (p.getProductNum() != 0) {
                            numberEditText.setText(String.valueOf(p.getProductNum()));
                        }

                        vv.findViewById(R.id.decrease_button).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int num = 0;
                                try {
                                    num = Integer.parseInt(numberEditText.getText().toString());
                                    num--;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                num = Math.max(0, num);
                                numberEditText.setText(String.valueOf(num));
                            }
                        });

                        vv.findViewById(R.id.increase_button).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int num = 1;
                                try {
                                    num = Integer.parseInt(numberEditText.getText().toString());
                                    num++;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                num = Math.max(1, num);
                                numberEditText.setText(String.valueOf(num));
                            }
                        });

                        vv.findViewById(R.id.okay_button).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int num = 0;
                                if (!TextUtils.isEmpty(numberEditText.getText())) {
                                    try {
                                        num = Integer.parseInt(numberEditText.getText()
                                                .toString());
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();// TODO
                                        num = 0;
                                    }
                                }
//                                setProductNum(num, true);
                                if(num == 0){
                                    num = 1;
                                }
                                ShoppingCartManager.getInstance().updateNumber(p,num);
                                d.dismiss();

                            }
                        });

                        vv.findViewById(R.id.cancel_button).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                            }
                        });
                        d.show();
                        numberEditText.setSelectAllOnFocus(true);
                        Tools.softInput();

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                        lp.copyFrom(d.getWindow().getAttributes());


                        lp.width = DimensionUtil.dip2px(248);
                        lp.height = 900;
                        d.getWindow().setAttributes(lp);

                    }
                });

//				Log.w("xx", "389-------------"+p);
                nameTextView.setText(p.getName());
                numberTextView.setText(String.valueOf(p.getProductNum()));
                String unit = com.tianpingpai.utils.TextUtils.isEmpty(p.getUnit()) ? "" : p.getUnit();
                String priceText = "￥"
                        + PriceFormat.format(p.getCouponPrice()) + "/"
                        + unit;
                priceTextView.setText(priceText);
                View lineView = new View(ContextProvider.getContext());
                lineView.setBackgroundColor(Color.WHITE);
                LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, 8);
                layoutParams.setMargins(50, 0, 0, 0);
                productsContainer.addView(lineView, layoutParams);

                setRemark(seller.getRemark());
            }
            updatePriceSum();
        }

        @Override
        public View getView() {
            return view;
        }

        private SpannableStringBuilder formatPrice(String text, double price) {
            SpannableStringBuilder style = new SpannableStringBuilder(text
                    + ":￥" + PriceFormat.format(price));
            style.setSpan(new ForegroundColorSpan(ContextProvider.getContext()
                            .getResources().getColor(R.color.tx_666)), 0, 3,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(ContextProvider.getContext()
                            .getResources().getColor(R.color.orange_f6)), 3,
                    style.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return style;
        }

        private void updatePriceSum() {
            double priceSum = 0;

            for (final ProductModel p : products) {
                if (selectionSet.contains(p)) {
                    priceSum += p.getCouponPrice() * p.getProductNum();
                    selectionSellerSet.add(seller);
                }
            }

            SpannableStringBuilder style = formatPrice("金额", priceSum);
            priceSumTextView.setText(style);

            double freight = seller.getFreight();
            boolean isValid;
            double total = priceSum;
            String freightStatus = PriceFormat.format(seller.getMinAmount())
                    + "元免运费";
            if (priceSum >= seller.getMinAmount()) {
                if (((int) seller.getMinAmount()) == 0) {
                    freightStatusTextView.setText("0元起送");
                } else {
                    freightStatusTextView
                            .setText(freightStatus);
                }

                freight = 0;
                isValid = true;
                priceContainer.setBackgroundResource(android.R.color.transparent);
            } else {
                if (seller.getFreight() > 0) {
                    freightStatusTextView
                            .setText(freightStatus);
                    total += seller.getFreight();
                    isValid = true;
                    priceContainer.setBackgroundResource(android.R.color.transparent);
                } else {
                    freightStatus = PriceFormat.format(seller.getMinAmount()) + "起送";
                    freightStatusTextView.setText(freightStatus);
                    priceContainer.setBackgroundResource(R.color.shopping_cart_store_invalid);
                    isValid = false;
                }
            }

            freightStatusTextView.setText("满"+PriceFormat.format(seller.getStartingPrice())+"起送");//ky
            style = formatPrice("运费", freight);
            freightTextView.setText(style);

            for (final ProductModel p : products) {
                if (selectionSet.contains(p)) {
                    validationSet.put(p, isValid);
                } else {
                    validationSet.remove(p);
                }
            }

            style = formatPrice("合计", total);
            totalSumTextView.setText(style);
            if(priceSum >= seller.getStartingPrice()){
                storeFullStatus.setVisibility(View.INVISIBLE);
                sellerSet.put(seller,true);
                freightStatusTextView.setText("满"+PriceFormat.format(seller.getMinAmount())+"免运费");
            }else{
                storeFullStatus.setVisibility(View.VISIBLE);
                if(priceSum == 0){
                    sellerSet.put(seller,true);
                }else {
                    sellerSet.put(seller,false);
                }
            }

        }

        private OnClickListener toStoreButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT,
                        StoreViewController.class);
                intent.putExtra(StoreDataContainer.KEY_STORE_TYPE,
                        StoreDataContainer.STORE_TYPE_NORMAL);
                intent.putExtra(StoreDataContainer.KEY_STORE_ID, sellerId);
                intent.putExtra(StoreDataContainer.KEY_SHOP_NAME,
                        seller.getSaleName());
                getActivity().startActivity(intent);
            }
        };

        boolean show = false;
        @OnClick(R.id.arrow_container)
        private View.OnClickListener showOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(show){
                    show = false;
                    priceSumTextView.setVisibility(View.GONE);
                    freightTextView.setVisibility(View.GONE);
                    arrowStatusView.setImageResource(R.drawable.arrow_down);
                }else {
                    show = true;
                    priceSumTextView.setVisibility(View.VISIBLE);
                    freightTextView.setVisibility(View.VISIBLE);
                    arrowStatusView.setImageResource(R.drawable.arrow_up);
                }
            }
        };
    }

    private HashSet<ProductModel> selectionSet = new HashSet<>();
    private HashSet<SellerModel> selectionSellerSet = new HashSet<>();

    private class GroupSelectionListener implements OnCheckedChangeListener {
        private CompoundButton categoryBox;
        private CartItemViewHolder holder;
        private ArrayList<CompoundButton> subBoxes = new ArrayList<>();

        public void setCategoryCheckBox(CompoundButton cb) {
            this.categoryBox = cb;
            this.categoryBox.setOnCheckedChangeListener(this);
        }

        public void addSubCheckBox(CompoundButton cb) {
            this.subBoxes.add(cb);
            cb.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            Log.e("xx", "onCheckedChanged" + isChecked);

            if (!isChecked) {
                if (selectionSet.isEmpty() && checkChangedListener != null) {
                    checkChangedListener.onCheckedChanged(null, false);
                }
            }
            if (buttonView == categoryBox) {
                for (CompoundButton cb : subBoxes) {
                    cb.setChecked(isChecked);
                }
            } else {

                ProductModel pm = (ProductModel) buttonView.getTag();
                if (isChecked) {
                    selectionSet.add(pm);
                } else {
                    selectionSet.remove(pm);
                }

                boolean allSame = true;
                for (CompoundButton cb : subBoxes) {
                    if (cb.isChecked() != isChecked) {
                        allSame = false;
                        break;
                    }
                }
                if (allSame) {
                    categoryBox.setChecked(isChecked);
                }
                holder.updatePriceSum();
                selectionListener.onProductSelectionChange();
            }
        }
    }

    public HashSet<ProductModel> getSelectedProducts() {
        return selectionSet;
    }

    public ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> getSelectionStores() {
        ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> stores = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            Pair<SellerModel, ArrayList<ProductModel>> p = getItem(i);
            ArrayList<ProductModel> products = new ArrayList<>();
            for (ProductModel pm : p.second) {
                if (selectionSet.contains(pm)) {
                    Log.e("xx", "stores:" + pm.getName());
                    products.add(pm);
                }
            }
            if (!products.isEmpty()) {
                stores.add(new Pair<>(
                        p.first, products));
            }
        }
        return stores;
    }

    private HashMap<ProductModel, Boolean> validationSet = new HashMap<>();

    public boolean isAllValid() {
        boolean allValid = true;
        for (ProductModel p : selectionSet) {
            allValid = allValid & validationSet.get(p);
        }
        return allValid;
    }

    private HashMap<SellerModel,Boolean> sellerSet = new HashMap<>();

    public boolean isCheckOutSeller(){
        boolean b = true;
        for (SellerModel s : selectionSellerSet){
            b = b & sellerSet.get(s);
        }
        return b;
    }
}
