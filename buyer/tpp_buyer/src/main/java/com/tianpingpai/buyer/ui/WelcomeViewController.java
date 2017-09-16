package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.AddressManager;
import com.tianpingpai.buyer.model.AddressModel;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
@ActionBar(layout = R.layout.ab_title_green,title = "欢迎来到天平派")
@Layout(id = R.layout.vc_welcome)
public class WelcomeViewController extends BaseViewController{

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        AddressManager.getInstance().registerListener(addressListener);
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        AddressManager.getInstance().unregisterListener(addressListener);
    }

    private ModelStatusListener<ModelEvent, AddressModel> addressListener = new ModelStatusListener<ModelEvent, AddressModel>() {
        @Override
        public void onModelEvent(ModelEvent event, AddressModel model) {
            if(getActivity() != null){
                getActivity().finish();
            }
        }
    };

    @OnClick(R.id.complete_info_button)
    private View.OnClickListener completeInfoButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, EditAddressViewController.class);
            intent.putExtra(EditAddressViewController.KEY_USER_NOT_VALIDATED,true);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.call_service_button)
    private View.OnClickListener callServiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String serviceLine = getActivity().getString(R.string.service_line);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + serviceLine));
                    getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.look_around_button)
    private View.OnClickListener lookAroundButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SelectMarketViewController.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    };
}
