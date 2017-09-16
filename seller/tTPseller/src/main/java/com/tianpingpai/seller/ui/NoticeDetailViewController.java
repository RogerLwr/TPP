package com.tianpingpai.seller.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.NoticeManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SelectTimeViewController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Statistics(page = "公告详情")
@Layout(id = R.layout.ui_notice_detail)
@SuppressWarnings("unused")
public class NoticeDetailViewController extends BaseViewController {

    public static final String KEY_TYPE = "key.type";
    public static final String KEY_NOTICE = "key.notice";

    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SelectTimeViewController startTimeController = new SelectTimeViewController();
    private SelectTimeViewController endTimeController = new SelectTimeViewController();

    private EditText noticeContentEditText;

    private int type;
    private long id;

    @Binding(id = R.id.title_text_view)
    private TextView titleTextView;
    @Binding(id = R.id.content_text_view)
    private TextView contentTextView;
    @Binding(id = R.id.start_time_text_view)
    private TextView beginTimeTextView;
    @Binding(id = R.id.end_time_text_view)
    private TextView endTimeTextView;
    @Binding(id = R.id.submitButton)
    private Button submitButton;

    private SelectTimeViewController.OnSelectedListener startTimeSelectionListener = new SelectTimeViewController.OnSelectedListener() {
        @Override
        public void onSelected() {
            beginTimeTextView.setText(startTimeController.getSelectedTime());
        }
    };

    private SelectTimeViewController.OnSelectedListener endTimeSelectedListener = new SelectTimeViewController.OnSelectedListener() {
        @Override
        public void onSelected() {
            endTimeTextView.setText(endTimeController.getSelectedTime());
        }
    };

    private View.OnClickListener onBeginTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActionSheet(true).setViewController(startTimeController);
            getActionSheet(true).setHeight(DimensionUtil.dip2px(300));
            startTimeController.setActivity(getActivity());
            startTimeController.setOnSelectedListener(startTimeSelectionListener);
            getActionSheet(true).show();
        }
    };
    private View.OnClickListener onEndTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActionSheet(true).setViewController(endTimeController);
            getActionSheet(true).setHeight(DimensionUtil.dip2px(300));
            startTimeController.setActivity(getActivity());
            endTimeController.setOnSelectedListener(endTimeSelectedListener);
            getActionSheet(true).show();
        }
    };
    private View.OnClickListener onSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Date endDate = null, beginDate = null;
            try {
                beginDate = sdf.parse(beginTimeTextView.getText().toString());
                endDate = sdf.parse(endTimeTextView.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(endDate != null && endDate.after(beginDate)){
                String content = noticeContentEditText.getText().toString().trim();
                if(TextUtils.isEmpty(content)){
                    Toast.makeText(getActivity(), "公告内容不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    String beginTime = sdf.format(beginDate);
                    String endTime = sdf.format(endDate);
                    loadData(content, beginTime, endTime);
                }
            }else{
                Toast.makeText(getActivity(), "结束时间不能早于开始时间或相同", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        type = getActivity().getIntent().getIntExtra(KEY_TYPE, CommonUtil.DEFAULT_ARG);
        Model notice = getActivity().getIntent().getParcelableExtra(KEY_NOTICE);
        setActionBarLayout(R.layout.ab_title_white);
        if(type == CommonUtil.TYPE_UPDATE_NOTICE){
            setTitle("编辑公告");
        }else{
            setTitle("发布公告");
        }

        noticeContentEditText = (EditText) rootView.findViewById(R.id.et_notice_content);
        beginTimeTextView.setOnClickListener(onBeginTimeClickListener);
        endTimeTextView.setOnClickListener(onEndTimeClickListener);
        submitButton.setOnClickListener(onSubmitClickListener);
        if(type == CommonUtil.TYPE_UPDATE_NOTICE && notice != null){
            submitButton.setText("更新公告");
            noticeContentEditText.setText(notice.getString("content"));
            id = notice.getLong("id");
            try {
                Date date_begin = sdf.parse(notice.getString("beginTime"));
                Date date_end = sdf.parse(notice.getString("endTime"));
                String str_begin = sdf.format(date_begin);
                String str_end = sdf.format(date_end);

                beginTimeTextView.setText(str_begin);
                endTimeTextView.setText(str_end);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else{
            beginTimeTextView.setText(startTimeController.getSelectedTime());
            endTimeTextView.setText(endTimeController.getSelectedTime());
        }

        showContent();
    }

    private void loadData(String content, String beginTime, String endTime){
        HttpRequest<ModelResult<Model>> req;
        if( type == CommonUtil.TYPE_UPDATE_NOTICE ){
            req = new HttpRequest<>(URLApi.NOTIFICATIONS_UPDATE, listener);
            req.addParam("id", String.valueOf(id));
        }else{
            req = new HttpRequest<>(URLApi.NOTIFICATIONS_ADD, listener);
        }
        req.setMethod(HttpRequest.POST);
        UserModel user = UserManager.getInstance().getCurrentUser();
        req.addParam("saler_id", String.valueOf(user.getUserID()));
        req.addParam("content", content);
        req.addParam("beginTime", beginTime);
        req.addParam("endTime", endTime);
        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                hideLoading();
            }
        });
        showLoading();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            hideLoading();
            if(data.isSuccess()){
                getActivity().setResult(101);
                getActivity().finish();
                NoticeManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate,null);
            }
        }
    };
}
