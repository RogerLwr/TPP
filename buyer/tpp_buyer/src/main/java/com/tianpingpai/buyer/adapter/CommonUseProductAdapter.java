package com.tianpingpai.buyer.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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
import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.utils.AnimationTool;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.viewController.ProdDetailViewController;

@SuppressWarnings("unused")
public class CommonUseProductAdapter extends ModelAdapter<ProductModel> {

    public ActionSheet as;

    public interface ProductNumberChangeListener {
        void onProductNumberChange(ProductModel pm);
    }


    private FragmentActivity a;

    public void setActivity(FragmentActivity a) {
        this.a = a;
    }

    private AnimationTool animationTool = new AnimationTool();
    private ImageView moveImageView;
    private View buyCartView;
    public void setBuyCartView(View buyCartView){
        this.buyCartView = buyCartView;
    }

    private ProductNumberChangeListener listener;

    public void setProductNumberChangeListener(ProductNumberChangeListener l) {
        this.listener = l;
    }

    @Override
    protected com.tianpingpai.ui.ModelAdapter.ViewHolder<ProductModel> onCreateViewHolder(
            LayoutInflater inflater) {
        return new ProductViewHolder(inflater);
    }

    class ProductViewHolder implements ViewHolder<ProductModel> {
        View view;

        @Binding(id = R.id.thumbnail_image_view)
        private ImageView thumbnailImageView;
        @Binding(id = R.id.name_text_view,format = "{{prod_name}}")
        private TextView nameTextView;
        @Binding(id = R.id.price_text_view,format = "¥{{coupon_price|money}}")
        private TextView priceTextView;
        @Binding(id = R.id.unit_text_view,format = "/{{unit}}")
        private TextView unitTV;
        @Binding(id = R.id.desc_text_view,format = "描述: {{description}}")
        private TextView descTextView;
        @Binding(id = R.id.number_text_view,format = "{{prod_num}}")
        private TextView numberTextView;
        @Binding(id = R.id.sell_number_text_view,format = "已出售{{sale_num}}{{unit}}")
        private TextView sellNumberTextView;
        @Binding(id = R.id.minus_button)
        private View minusButton;
        private ProductModel mModel;

        private Binder binder = new Binder();

        private OnClickListener numberTextListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // InputNumberDialog d = new InputNumberDialog();
                View vv = LayoutInflater.from(a).inflate(
                        R.layout.dialog_input_number, null);
                final Dialog d = new AlertDialog.Builder(a).setView(vv)
                        .create();
                final EditText numberEditText = (EditText) vv
                        .findViewById(R.id.number_edit_text);
                if (mModel.getProductNum() != 0) {
                    numberEditText.setText(String.valueOf(mModel.getProductNum()));
                }
                vv.findViewById(R.id.decrease_button).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int num = 0;
                                try {
                                    num = Integer.parseInt(numberEditText
                                            .getText().toString());
                                    num--;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                num = Math.max(0, num);
                                numberEditText.setText(String.valueOf(num));
                            }
                        });
                vv.findViewById(R.id.increase_button).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int num = 1;
                                try {
                                    num = Integer.parseInt(numberEditText
                                            .getText().toString());
                                    num++;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                num = Math.max(1, num);
                                numberEditText.setText(String.valueOf(num));
                            }
                        });

                vv.findViewById(R.id.okay_button).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int num = 0;
                                if (!TextUtils.isEmpty(numberEditText.getText())) {
                                    try {
                                        num = Integer.parseInt(numberEditText
                                                .getText().toString());
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();// TODO
                                        num = 0;
                                    }
                                }
                                mModel.setProductNum(num);
                                numberTextView.setText(String.valueOf(num));
                                if (num == 0) {
                                    minusButton.setVisibility(View.INVISIBLE);
                                } else {
                                    minusButton.setVisibility(View.VISIBLE);
                                }
                                if (listener != null) {
                                    listener.onProductNumberChange(mModel);
                                }
                                d.dismiss();
                            }
                        });

                vv.findViewById(R.id.cancel_button).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                            }
                        });
                d.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                lp.copyFrom(d.getWindow().getAttributes());

                lp.width = DimensionUtil.dip2px(248);
                lp.height = 1000;
                // lp.x=-170;
                // lp.y=100;
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
                if (pdvc == null) {
                    pdvc = new ProdDetailViewController();
                }
                pdvc.setCommonUseProductAdapter(CommonUseProductAdapter.this);
                pdvc.setProductNumberChangeListener(listener);
                pdvc.setActivity(a);
                pdvc.setProdId(mModel.getId());
                pdvc.setModel(mModel);
                as.setViewController(pdvc);
                as.show();
            }
        };
        private ProdDetailViewController pdvc;

        public ProductViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_product, null);
            binder.bindView(this, view);
            view.findViewById(R.id.content_container).setOnClickListener(onClickShowLineChartListener);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//					((StoreCommonUseViewController) fragment).showDeleteDialog(mModel);//TODO
                    return true;
                }
            });
            thumbnailImageView.setOnClickListener(onClickShowLineChartListener);
            nameTextView.setGravity(Gravity.CENTER);
            nameTextView.setTextColor(Color.parseColor("#666666"));
            numberTextView.setOnClickListener(numberTextListener);

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
                    mModel.setProductNum(num);
                    numberTextView.setText(String.valueOf(num));
                    if (listener != null) {
                        listener.onProductNumberChange(mModel);
                    }

                    if (num == 0) {
                        minusButton.setVisibility(View.INVISIBLE);
                    } else {
                        minusButton.setVisibility(View.VISIBLE);
                        numberTextView.setVisibility(View.VISIBLE);
                    }

//                    int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
//                    addButtonView.getLocationInWindow(start_location);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
//                    int[] stop_location = new int[2];
//                    buyCartView.getLocationInWindow(stop_location);
//                    moveImageView = new ImageView(a);
//                    moveImageView.setImageResource(R.drawable.selector_add_button);
//                    animationTool.setAnim(moveImageView, start_location, stop_location, a);

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
                    mModel.setProductNum(num);
                    numberTextView.setText(String.valueOf(num));
                    if (listener != null) {
                        listener.onProductNumberChange(mModel);
                    }

                    if (num == 0) {
                        minusButton.setVisibility(View.INVISIBLE);
                        numberTextView.setVisibility(View.INVISIBLE);
                    } else {
                        minusButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        public void setModel(ProductModel model) {
            this.mModel = model;
            binder.bindData(model);

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
            if (model.getProductNum() == 0) {
                numberTextView.setText("");
                minusButton.setVisibility(View.INVISIBLE);
            } else {
                minusButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }

    public void remove(ProductModel product) {
        getModels().remove(product);
        notifyDataSetChanged();
    }
}
