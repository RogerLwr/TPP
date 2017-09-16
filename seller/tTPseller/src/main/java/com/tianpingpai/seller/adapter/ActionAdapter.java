package com.tianpingpai.seller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.seller.ui.LoginViewController;
import com.tianpingpai.seller.SellerUrlInterceptor;
import com.tianpingpai.seller.ui.StatisticsViewController;
import com.tianpingpai.seller.ui.WebViewController;
import com.tianpingpai.seller.ui.CommentListViewController;
import com.tianpingpai.seller.ui.MyInfoViewController;
import com.tianpingpai.seller.ui.NoticeListViewController;
import com.tianpingpai.seller.ui.WalletViewController;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;
@SuppressWarnings("unused")
public class ActionAdapter extends ModelAdapter<MineAction> {

    ArrayList<MineAction> actions = new ArrayList<>();

    private Context context;
    private MineAction statistic;

    public void setContext(Context context) {
        this.context = context;
    }

    MineAction wallet = new MineAction();
    MineAction myScore = new MineAction();
    MineAction agreement = new MineAction();

    public void reset() {

        actions.add(MineAction.getSeperator());

        MineAction info = new MineAction();
        info.icon = R.drawable.ic_my_info;
        info.target = MyInfoViewController.class;
        info.title = "信息管理";
        actions.add(info);

        MineAction notice = new MineAction();
        notice.icon = R.drawable.ic_my_notices;
        notice.target = NoticeListViewController.class;
        notice.title = "我的公告";
        actions.add(notice);

        MineAction comment = new MineAction();
        comment.icon = R.drawable.ic_my_comments;
        comment.target = CommentListViewController.class;
        comment.title = "查看评价";
        actions.add(comment);

        actions.add(MineAction.getSeperator());

//        MineAction msg = new MineAction();
//        msg.icon = R.drawable.ic_msgs;
//        msg.target = MessageListViewController.class;
//        msg.title = "消息中心";
//        actions.add(msg);


        wallet.icon = R.drawable.ic_wallet;
        wallet.target = WalletViewController.class;
        wallet.title = "我的钱包";
        actions.add(wallet);

        statistic = new MineAction();
        statistic.icon = R.drawable.ic_statastics;
//        statistic.target = StatisticAct.class;
        statistic.target = StatisticsViewController.class;
        statistic.title = "我的统计";

        myScore.icon = R.drawable.ic_151117_my_score;
        myScore.target = WebViewController.class;
        UserModel currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            myScore.url = URLApi.getWebBaseUrl() + "/app/score/integral/shop?accessToken=" + currentUser.getAccessToken();
        }
        myScore.title = "积分商城";

        actions.add(MineAction.getSeperator());

        agreement.icon = R.drawable.ic_151103_agreement;
        agreement.target = WebViewController.class;
        agreement.url = URLApi.getBaseUrl() + "/html/app/manage.html";
        agreement.title = "商家规则";
        actions.add(agreement);

//        MineAction coupon = new MineAction();
//        coupon.icon = R.drawable.ic_statastics;
//        coupon.target = CouponListViewController.class;
//        coupon.title = "我的优惠券";
//        actions.add(coupon);

        MineAction feedback = new MineAction();
        feedback.icon = R.drawable.ic_feedback;
//        feedback.target = Umeng
        feedback.title = "意见反馈";
        actions.add(feedback);

        //TODO 意见反馈

        setModels(actions);
    }

    {
        reset();
    }

    @Override
    public boolean isEnabled(int position) {
        return !getItem(position).isSeparator;
    }

    /**
     * 根据后台返回的 供货商级别 控制 我的统计 我的积分商城 模块的显示
     */
    public void addMyScore() {
        if (!actions.contains(myScore)) {
            actions.add(6, statistic);
            actions.add(7, myScore);
            notifyDataSetChanged();
        }
    }

    public Intent getIntent(int position) {
        Intent intent = new Intent(context, ContainerActivity.class);
        if (UserManager.getInstance().isLoggedIn()) {
            Class target = getItem(position).target;
            intent.putExtra(ContainerActivity.KEY_CONTENT, target);
            if (target == WebViewController.class) {
                intent.putExtra(WebViewController.KEY_URL, getItem(position).url);
            }
        } else {
            intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
        }
        if (getItem(position) == myScore) {
            intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
            intent.putExtra(WebViewController.KEY_URL_INTERCEPTOR, SellerUrlInterceptor.class);
        }
        return intent;

    }

    public void setUnreadEnvelopeCount(int unreadEnvelopeCount) {
        this.unreadEnvelopeCount = unreadEnvelopeCount;
        notifyDataSetChanged();
    }

    private int unreadEnvelopeCount = 0;

    @Override
    protected ViewHolder<MineAction> onCreateViewHolder(LayoutInflater inflater) {
        return new ActionViewHolder(inflater);
    }

    boolean isShowMyScore = false;

    public void setIsShowMyScore(boolean isShowMyScore) {
        this.isShowMyScore = isShowMyScore;
        notifyDataSetChanged();
    }

    private class ActionViewHolder implements ViewHolder<MineAction> {

        private View view;
        private View separatorView;
        private View contentContainer;
        private ImageView iconImageView;
        private TextView nameTextView;
        private View dividerLine;

        ActionViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_action, null, false);
            separatorView = view.findViewById(R.id.separator_view);
            contentContainer = view.findViewById(R.id.content_container);
            iconImageView = (ImageView) view.findViewById(R.id.icon_image_view);
            nameTextView = (TextView) view.findViewById(R.id.title_text_view);
            dividerLine = view.findViewById(R.id.divider_line);
        }

        @Override
        public void setModel(MineAction model) {
            if (model.isSeparator) {
                separatorView.setVisibility(View.VISIBLE);
                contentContainer.setVisibility(View.GONE);
            } else {
                separatorView.setVisibility(View.GONE);
                contentContainer.setVisibility(View.VISIBLE);
                iconImageView.setImageResource(model.icon);
                nameTextView.setText(model.title);

                if (model.icon == R.drawable.ic_feedback) {
                    dividerLine.setVisibility(View.INVISIBLE);
                } else {
                    dividerLine.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
