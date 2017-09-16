package com.tianpingpai.share;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.pay.R;
import com.tianpingpai.share.weixin.WeixinSharePlatform;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;

public class ShareViewController extends BaseViewController {

    {
        setLayoutId(R.layout._share_ui_share);
    }

    private PlatformAdapter adapter = new PlatformAdapter();
    private ShareContent content;

    public void registerPlatform(SharePlatform platform){
        adapter.registerPlatform(platform);
    }

    public void registerAll(){
        WeixinSharePlatform session = new WeixinSharePlatform();
        session.setScene(WeixinSharePlatform.SceneSession);
        adapter.registerPlatform(session);
    }

    public void setContent(ShareContent content){
        this.content = content;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        GridView gridView = (GridView) rootView.findViewById(R.id.share_grid_view);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(itemClickListener);
        hideActionBar();
        rootView.findViewById(R.id.cancel_button).setOnClickListener(cancelButtonListener);
    }

    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionSheet as = (ActionSheet) getViewTransitionManager();
            as.dismiss();
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SharePlatform platform = adapter.getItem(position);
            if(!platform.isValid()){
                Toast.makeText(ContextProvider.getContext(),platform.getInvalidHint(),Toast.LENGTH_LONG).show();
                return;
            }
            adapter.getItem(position).share(content);
        }
    };
}
