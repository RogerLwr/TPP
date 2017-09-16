package com.tianpingpai.buyer.ui;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.PriceFormat;

@SuppressWarnings("unused")
@Layout(id = R.layout.dialog_confirm)
public class ConfirmOrderDialog extends BaseViewController {
    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    private ActionSheet actionSheet;

    public void setPrice(double price) {
        this.price = price;
    }

    private double price;

    @Binding(id = R.id.msg_text_view)
    private TextView msgTextView;

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        String title = "确认提交订单？总金额:" + PriceFormat.format(price);
        msgTextView.setText(title);
        Toolbar toolbar = (Toolbar) setActionBarLayout(R.layout.ab_title_white).findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        setTitle("提交订单");
    }

    public void setPositiveButtonListener(View.OnClickListener positiveButtonListener) {
        this.positiveButtonListener = positiveButtonListener;
    }

    @OnClick(R.id.positive_button)
    private View.OnClickListener positiveButtonListener;

    @OnClick(R.id.cancel_button)
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
        }
    };
}
