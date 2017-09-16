package com.tianpingpai.share;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.pay.R;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;

public class PlatformAdapter extends ModelAdapter<SharePlatform> {

    private ArrayList<SharePlatform> platforms = new ArrayList<>();

    public void registerPlatform(SharePlatform platform){
        platforms.add(platform);
        setModels(platforms);
    }

    @Override
    protected ViewHolder<SharePlatform> onCreateViewHolder(LayoutInflater inflater) {
        return new PlatformViewHolder();
    }

    private class PlatformViewHolder implements ViewHolder<SharePlatform>{

        private View view = View.inflate(ContextProvider.getContext(), R.layout._share_item_platform,null);
        private ImageView iconImageView = (ImageView) view.findViewById(R.id.icon_image_view);
        private TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);

        @Override
        public void setModel(SharePlatform model) {
            iconImageView.setImageResource(model.getIcon());
            nameTextView.setText(model.getName());
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
