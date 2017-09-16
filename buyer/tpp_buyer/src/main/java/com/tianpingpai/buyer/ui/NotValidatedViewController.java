package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Layout;

@SuppressWarnings("unused")
@ActionBar(hidden = true)
@Layout(id = R.layout.dialog_not_validated)
public class NotValidatedViewController extends BaseViewController {
    @Binding(id = R.id.msg_text_view)
    private TextView msgTextView;
    @Binding(id = R.id.cancel_button)
    private TextView cancelButton;

    private String message;
    private String cancelButtonText;

    public void setMessage(String msg){
        this.message = msg;
    }

    public void setCancelButtonText(String text){
        this.cancelButtonText = text;
    }

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    private ActionSheet actionSheet;

    public void setFinishActivityOnExit(boolean finishActivityOnExit) {
        this.finishActivityOnExit = finishActivityOnExit;
    }

    private boolean finishActivityOnExit = true;

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        String title = "我们的客服人员会尽快为您审核\n您也可以拨打客服电话进行快速审核";
        msgTextView.setText(title);
        if(message != null){
            msgTextView.setText(message);
        }
        if(cancelButtonText != null){
            cancelButton.setText(cancelButtonText);
        }
        setTitle("");
    }

    public void setPositiveButtonListener(View.OnClickListener positiveButtonListener) {
        this.positiveButtonListener = positiveButtonListener;
    }

    @OnClick(R.id.positive_button)
    private View.OnClickListener positiveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + ContextProvider.getContext().getString(R.string.service_line)));
            actionSheet.dismiss();
            getActivity().startActivity(intent);
            if(finishActivityOnExit){
                getActivity().finish();
            }
        }
    };

    @OnClick(R.id.cancel_button)
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
            if(finishActivityOnExit){
                getActivity().finish();
            }
        }
    };
}
