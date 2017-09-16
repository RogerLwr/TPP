package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.share.ShareContent;
import com.tianpingpai.share.ShareViewController;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.QRCodeView;
//@Layout(id = R.layout.my_shop_qr_code_view_controller)
public class MyShopQRCodeViewController extends BaseViewController {

    public static final String KEY_TEXT = "text";

    private String text;

//    @Binding(id = R.id.content_text_view)
    TextView contentTextView;

    private String content = "打开微信的 \"扫一扫\" 功能,扫描下方的二维码即可进入您的店铺进行采购。您也可以点击右上角的分享按钮把店铺链接分享给您的客户。";

    float originalBrightness;

    {
        setLayoutId(R.layout.my_shop_qr_code_view_controller);
    }

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        text = a.getIntent().getStringExtra(KEY_TEXT);

        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
        originalBrightness = lp.screenBrightness;
        lp.screenBrightness = 0.92f;
        a.getWindow().setAttributes(lp);
    }

    @Override
    public void onActivityDestroyed(Activity a) {
        super.onActivityDestroyed(a);
        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
        lp.screenBrightness = originalBrightness;
        a.getWindow().setAttributes(lp);
    }

    @Override
    public void didSetContentView(Activity a) {
        super.didSetContentView(a);
        ImageView abRightButton = (ImageView) setActionBarLayout(R.layout.ab_title_white_right_img).findViewById(R.id.ab_right_button);
        abRightButton.setImageResource(R.drawable.ic_shared);
        abRightButton.setOnClickListener(shareButtonListener);
        setTitle("二维码");
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        contentTextView = (TextView)rootView.findViewById(R.id.content_text_view);
                showContent();
        contentTextView.setText(content);
        QRCodeView qrCodeView = (QRCodeView) rootView.findViewById(R.id.qr_code_view);
        UserModel currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(ContextProvider.getContext(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
            getActivity().startActivity(intent);
        }else {
            text = URLApi.getWebBaseUrl() + "/saler/upstream/shop_detail?s_user_id=" + currentUser.getUserID() + "&is_skip=2";
            qrCodeView.setData(text);
        }
    }

    private View.OnClickListener shareButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ShareContent content = new ShareContent();
            content.setTitle("我在天平派开的店铺");
            content.setDescription("我的店主打各种餐饮原材料,欢迎前来选购,优惠多多哦!");
            content.setLink(text);
            content.setThumbImage(BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.share_logo));
            ShareViewController shareViewController = new ShareViewController();
            shareViewController.registerAll();
            shareViewController.setContent(content);
            shareViewController.setActivity(getActivity());
            getActionSheet(true).setHeight(DimensionUtil.dip2px(180));
            getActionSheet(true).setViewController(shareViewController);
            getActionSheet(true).show();
        }
    };
}
