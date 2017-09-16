package com.tianpingpai.buyer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.tools.Tools;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.utils.TextUtils;
import com.tianpingpai.viewController.ProdDetailViewController;

@SuppressWarnings("unused")
public class ProductAdapter extends ModelAdapter<ProductModel> {

    private Activity activity;
    public ActionSheet as;
    public ProdDetailViewController pdvc;

    public void setSearch(boolean search) {
        isSearch = search;
    }

    private boolean isSearch = false;

    @Override
    protected com.tianpingpai.ui.ModelAdapter.ViewHolder<ProductModel> onCreateViewHolder(
            LayoutInflater inflater) {
        return new ProductViewHolder(inflater);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity a) {
        this.activity = a;
    }

    public void syncWithDb() {
        //TODO
        if (getModels() != null) {
            ShoppingCartManager.getInstance().syncProductNumberFromDb(getModels(), false);
            notifyDataSetChanged();
        }
    }

    class ProductViewHolder implements ViewHolder<ProductModel> {
        private View view;
        @Binding(id = R.id.thumbnail_image_view)
        private ImageView thumbnailImageView;
        @Binding(id = R.id.name_text_view,format = "{{prod_name}}")
        private TextView nameTextView;
        @Binding(id = R.id.price_text_view,format = "￥{{coupon_price}}")
        private TextView priceTextView;
        @Binding(id = R.id.unit_text_view,format = "/{{unit}}")
        private TextView unitTV;
        @Binding(id = R.id.desc_text_view,format = "描述:{{description}}")
        private TextView descTextView;
        @Binding(id = R.id.number_text_view)
        private TextView numberTextView;
        @Binding(id = R.id.sell_number_text_view,format = "已售出{{sale_num}}({{unit}})")
        private TextView sellNumberTextView;
        @Binding(id = R.id.minus_button)
        private View minusButton;
        private ProductModel mModel;

        private Binder binder = new Binder();

        @OnClick(R.id.number_text_view)
        private OnClickListener numberTextListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                View vv = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_number, null);
                final Dialog d = new AlertDialog.Builder(getActivity()).setView(vv).create();
                final EditText numberEditText = (EditText) vv.findViewById(R.id.number_edit_text);
                if (mModel.getProductNum() != 0) {
                    numberEditText.setText(String.valueOf(mModel.getProductNum()));
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
                        setProductNum(num, true);
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
        };

        long submitButtonLastClick = 0; // 字段
        private OnClickListener onClickShowLineChartListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - submitButtonLastClick < 2000) {
                    return;
                }
                submitButtonLastClick = System.currentTimeMillis();
                as = new ActionSheet();
                as.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);// 设置为屏幕高度
                pdvc = new ProdDetailViewController();
                pdvc.setShowButtonContainer(isSearch);
                pdvc.setProdAdapter(ProductAdapter.this);
                pdvc.setActivity((FragmentActivity) activity);
                pdvc.setProdId(mModel.getId());
                pdvc.setModel(mModel);
                as.setViewController(pdvc);
                as.show();
            }
        };
//        public ProdDetailViewController pdvc;

        public ProductViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_product, null);
            binder.bindView(this, view);
            view.findViewById(R.id.content_container).setOnClickListener(onClickShowLineChartListener);
            thumbnailImageView.setOnClickListener(onClickShowLineChartListener);
            nameTextView.setGravity(Gravity.CENTER);

            final View addButtonView = view.findViewById(R.id.add_button);
            addButtonView.setOnClickListener(new OnClickListener() {
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

            view.findViewById(R.id.minus_button).setOnClickListener(new OnClickListener() {
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
            mModel.setProductNum(num);
            if (notify) {
                ShoppingCartManager.getInstance().mark(mModel);
            }

            if (num > 0) {
                minusButton.setVisibility(View.VISIBLE);
            } else {
                minusButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void setModel(ProductModel model) {
            this.mModel = model;
            long start = System.currentTimeMillis();
            binder.bindData(model);
            Log.e("xx", "used:" + (System.currentTimeMillis() - start));
            if (TextUtils.isEmpty(model.getImageUrl())) {
                this.thumbnailImageView.setVisibility(View.GONE);
            } else {
                this.thumbnailImageView.setVisibility(View.VISIBLE);
                ImageLoader.load(model.getImageUrl(), this.thumbnailImageView, R.drawable.img_loading, R.drawable.img_loading);
            }
            if (TextUtils.isEmpty(model.getDescription())) {
                descTextView.setVisibility(View.GONE);
            } else {
                descTextView.setVisibility(View.VISIBLE);
            }
            setProductNum(model.getProductNum(), false);
        }

        @Override
        public View getView() {
            return view;
        }
    }

}
