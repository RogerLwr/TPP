package com.tianpingpai.seller.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

import java.text.ParseException;
import java.util.Date;

@SuppressWarnings("unused")
public class GroupShoppingAdapter extends ModelAdapter<Model> {
    private class GroupShoppingViewHolder implements ViewHolder<Model> {
        private View view;

        @Binding(id = R.id.sale_name_text_view, format = "{{sale_name}}")
        private TextView saleNameTextView;
        @Binding(id = R.id.name_text_view, format = "{{group_name}}")
        private TextView nameTextView;

        @Binding(id = R.id.group_price_text_view, format = "{{group_price}}")
        private TextView groupPriceTextView;
        @Binding(id = R.id.price_text_view, format = "{{price}}")
        private TextView priceTextView;
        @Binding(id = R.id.status_text_view, format = "{{_status}}")
        private TextView statusTextView;
        @Binding(id = R.id.thumbnail_image_view)
        private ImageView thumbnailImageView;

        @Binding(id = R.id.info_text_view)
        private TextView infoTextView;

        @Binding(id = R.id.group_num_text_view)
        private TextView numTextView;

        private Binder binder = new Binder();

        private GroupShoppingViewHolder(View v) {
            this.view = v;
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model model) {

            int status = model.getInt("status");
            numTextView.setVisibility(View.VISIBLE);
            infoTextView.setTextColor(infoTextView.getResources().getColor(R.color.gray_66));
            numTextView.setTextColor(infoTextView.getResources().getColor(R.color.gray_66));
            String statusStr = "组团中";
            if(status == 0){
                statusStr = "未开始";
                String dateStr = "";
                try {
                    Date date = CommonUtil.Date.sdf_yyyy_MM_dd_HH_mm_ss.parse(model.getString("start_time"));
                    dateStr = CommonUtil.Date.sdf_yyyy_MM_dd_HH.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                infoTextView.setText( dateStr + "开抢");
                infoTextView.setTextColor(infoTextView.getResources().getColor(R.color.red_ff6));
                numTextView.setVisibility(View.GONE);
            }else if(status == 1){
                statusStr = "组团中";
                SpannableStringBuilder ssb = new SpannableStringBuilder("已售出" + (model.getInt("num")-model.getInt("left_num")) );
                ForegroundColorSpan span=new ForegroundColorSpan(Color.parseColor("#0cc486"));
                ssb.setSpan(span, 3, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                infoTextView.setText(ssb);
                SpannableStringBuilder ssbNum = new SpannableStringBuilder("售出"+ model.getInt("num"));
                ForegroundColorSpan spanNum = new ForegroundColorSpan(Color.parseColor("#0cc486"));
                ssbNum.setSpan(span, 2, ssbNum.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssbNum.append("成团");
                numTextView.setText(ssbNum);
            }else {
                SpannableStringBuilder ssb = new SpannableStringBuilder("已售出" + (model.getInt("num")-model.getInt("left_num")) );
                ForegroundColorSpan span=new ForegroundColorSpan(Color.parseColor("#0cc486"));
                ssb.setSpan(span, 3, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                infoTextView.setText(ssb);
                SpannableStringBuilder ssbNum = new SpannableStringBuilder("剩余"+ "0");
                ForegroundColorSpan spanNum = new ForegroundColorSpan(Color.parseColor("#0cc486"));
                ssbNum.setSpan(span, 2, ssbNum.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                numTextView.setText(ssbNum);
                numTextView.setVisibility(View.GONE);
                if(status == 2){
                    statusStr = "团购中";
                }
                if(status == 3){
                    statusStr = "团购结束";
                }
            }

            model.set("_status", statusStr);
            binder.bindData(model);
            ImageLoader.load(model.getString("thum_img"), thumbnailImageView);
            priceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        }

        @Override
        public View getView() {
            return this.view;
        }
    }


    @SuppressLint("InflateParams")
    @Override
    protected ViewHolder<Model> onCreateViewHolder(
            LayoutInflater inflater) {
        return new GroupShoppingViewHolder(inflater.inflate(R.layout.item_group_shopping, null));
    }
}
