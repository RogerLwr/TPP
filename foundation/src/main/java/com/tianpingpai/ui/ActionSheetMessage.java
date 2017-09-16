package com.tianpingpai.ui;

import android.view.View;
import android.widget.TextView;

import com.tianpingpai.foundation.R;

public class ActionSheetMessage extends BaseViewController {


    private ActionSheet actionSheet;

    private CharSequence mTitle;

    public boolean isShow() {
        return isShow;
    }

    private boolean isShow ;

    public void setMessage(CharSequence message) {
        this.message = message;
    }

    private CharSequence message;
    private boolean cancelButtonHidden;
    private CharSequence positiveButtonText;

    public void setActionSheet(ActionSheet as){
        this.actionSheet = as;
        actionSheet.setViewController(this);
    }

    public void dismiss(){
        isShow = false;
        actionSheet.dismiss();
    }

    public void show(){
        isShow = true;
        actionSheet.show();
    }

    {
        setLayoutId(R.layout.message_action_sheet);
    }

    public void setPositiveButtonText(CharSequence text){
        this.positiveButtonText = text;
    }

    public void setCancelButtonHidden(boolean hidden){
        this.cancelButtonHidden = hidden ;
    }

    public void setTitle(CharSequence title){
        this.mTitle = title;
    }

    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(negativeButtonListener != null){
                negativeButtonListener.onClick(v);
            }
            actionSheet.dismiss();
        }
    };

    private View.OnClickListener positiveButtonListener;
    private View.OnClickListener negativeButtonListener;

    public void setPositiveButtonListener(View.OnClickListener l){
        this.positiveButtonListener = l;
    }
    public void setNegativeButtonListener(View.OnClickListener l){
        this.negativeButtonListener = l;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        hideActionBar();
        View cancelButton = rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);
        TextView positiveButton = (TextView) rootView.findViewById(R.id.positive_button);
        positiveButton.setOnClickListener(positiveButtonListener);
        if(positiveButtonText != null){
            positiveButton.setText(positiveButtonText);
        }

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_text_view);
        TextView messageTextView = (TextView) rootView.findViewById(R.id.message_text_view);
        if("".equals(message)||null == message){
            messageTextView.setVisibility(View.GONE);
        }else{
            messageTextView.setText(message);
        }
        titleTextView.setText(mTitle);
        if(cancelButtonHidden){
            cancelButton.setVisibility(View.GONE);
        }
    }
}
