package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONModelMapper;
import com.tianpingpai.seller.adapter.PrintProductItemAdapter;
import com.tianpingpai.seller.manager.BluetoothController;
import com.tianpingpai.seller.manager.BluetoothEvent;
import com.tianpingpai.seller.manager.Line;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import liqp.Template;

@SuppressWarnings("unused")
@ActionBar(title = "打印订单")
@Statistics(page = "打印")
@Layout(id = R.layout.ui_print)
public class PrintViewController extends BaseViewController {
    public static final String KEY_CONTENT = "content";

    @Binding(id = R.id.name_text_view, format = "买家姓名: {{recevier_name}}   {{telephone}}")
    private TextView nameTextView;
    @Binding(id = R.id.store_name_text_view, format = "店铺名称: {{b_shop_name}}")
    private TextView storeNameTextView;
    @Binding(id = R.id.address_text_view, format = "店铺地址: {{address}}")
    private TextView addressTextView;
    @Binding(id = R.id.order_time_text_view, format = "下单时间: {{order_dt}}")
    private TextView orderTimeTextView;
    @Binding(id = R.id.order_remark_text_view, format = "买家留言: {{remark}}")
    private TextView orderRemarkTextView;
    //---------------------------------------//
    @Binding(id = R.id.seller_name_text_view, format = "卖家店名: {{sale_name}}")
    private TextView sellerNameTextView;
    @Binding(id = R.id.seller_contact_text_view, format = "联系方式: {{saler_phone}}")
    private TextView sellerContactTextView;
    @Binding(id = R.id.deliver_time_text_view, format = "预计送达: {{deliver_dt}}")
    private TextView deliverTimeTextView;
    //--------------------------------------//
    @Binding(id = R.id.amount_text_view, format = "￥: {{prod_mny | money}}")
    private TextView amountTextView;
    @Binding(id = R.id.payment_type_text_view)
    private TextView payTypeTextView;
    @Binding(id = R.id.deliver_fee_text_view, format = "￥: {{deliver_mny | money}}")
    private TextView deliverFeeTextView;
    @Binding(id = R.id.coupon_amount_text_view, format = "-￥: {{coupon_mny | money}}")
    private TextView couponAmountTextView;
    @Binding(id = R.id.total_amount_text_view, format = "￥: {{mny | money}}")
    private TextView totalAmountTextView;

    private TextView deviceButton;

    private PrintProductItemAdapter adapter = new PrintProductItemAdapter();
    private Model model = new Model();

    private ModelStatusListener<BluetoothEvent, Model> bluetoothStatusListener = new ModelStatusListener<BluetoothEvent, Model>() {
        @Override
        public void onModelEvent(BluetoothEvent event, Model model) {
            switch (event) {
                case OnConnected:
                    deviceButton.setText("已连接");
                    break;
                case OnConnectFailed:
                    deviceButton.setText("重新连接");
                    break;
                case OnConnecting:
                    deviceButton.setText("正在连接..");
                    break;
                case OnDisconnected:
                    deviceButton.setText("重新连接");
                    break;
            }
        }
    };

    private void setDeviceButtonText() {
        int state = BluetoothController.getInstance().getConnectionState();
        switch (state) {
            case BluetoothController.STATE_CONNECTED:
                deviceButton.setText("已连接");
                break;
            case BluetoothController.STATE_CONNECTING:
                deviceButton.setText("正在连接");
                break;
            case BluetoothController.STATE_NOT_CONNECTED:
                deviceButton.setText("点击连接");
                break;
        }
    }

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        String content = a.getIntent().getStringExtra(KEY_CONTENT);
        Log.e("xx", "112-------"+content);
        try {
            JSONModelMapper.mapObject(new JSONObject(content), model);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        ListView productListView = (ListView) rootView.findViewById(R.id.product_list_view);
        productListView.setAdapter(adapter);
        ArrayList<Model> productList = new ArrayList<>();
        Model header = new Model();
        header.set("isHeader", true);
        productList.add(header);
        productList.addAll(model.getList("prod_list", Model.class));
        adapter.setModels(productList);
        deviceButton = (TextView) setActionBarLayout(R.layout.ab_title_white).findViewById(R.id.ab_right_button);
        deviceButton.setClickable(true);
        deviceButton.setOnClickListener(deviceButtonOnClickListener);
        getBinder().bindData(model);
        if(TextUtils.isEmpty(model.getString("remark"))){
            orderRemarkTextView.setVisibility(View.GONE);
        }
        setDeviceButtonText();
        String paymentType = model.getInt("pay_type") == 0 ? "在线支付" : "货到付款";
        payTypeTextView.setText(paymentType);
        BluetoothController.getInstance().autoConnect();
        BluetoothController.getInstance().registerListener(bluetoothStatusListener);
    }

    private View.OnClickListener deviceButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, ManageBlueToothDevicesViewController.class);
            getActivity().startActivity(intent);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BluetoothController.getInstance().unregisterListener(bluetoothStatusListener);
    }

    @OnClick(R.id.device_button)
    private View.OnClickListener printButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<Line> lines = new ArrayList<>();
            Line header = new Line();
            header.setBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.header_print_filled));
            lines.add(header);

            lines.add(getVirtualLine());
            lines.add(new Line("买家姓名:" + model.getString("recevier_name") + model.getString("telephone") + "\n"));
            lines.add(new Line("店铺名称:" + model.getString("b_shop_name") + "\n"));
            lines.add(new Line("店铺地址:" + model.getString("address") + "\n"));
            lines.add(new Line("下单时间:" + model.getString("order_dt") + "\n"));
            if(!TextUtils.isEmpty(model.getString("remark"))){
                lines.add(new Line("买家留言:" + model.getString("remark") + "\n"));
            }
            lines.add(getVirtualLine());
            lines.add(new Line("卖家店名:" + model.getString("sale_name") + "\n"));
            lines.add(new Line("联系方式:" + model.getString("saler_phone") + "\n"));
            lines.add(new Line("预计送达:" + model.getString("deliver_dt") + "\n"));
            lines.add(getVirtualLine());
            lines.add(new Line("商品名    单价     数量     总价" + "\n"));
            List<Model> productList = model.getList("prod_list", Model.class);

            Template nameTemplate = Template.parse("{{prod_name}}\n");
            Template numberTemplate = Template.parse("       ￥{{coupon_price | money | limit:7}}   x{{prod_num}}  ￥{{__total_price__ | money | limit:7}}\n");
            if (productList != null && !productList.isEmpty()) {
                for (Model product : productList) {
                    double total = model.getDouble("coupon_price") * model.getInt("prod_num");
                    product.set("_total_price_", total);
                    Map<String, Object> map = product.getAll();
                    Log.e("xx", "map:" + map);
                    String name = nameTemplate.render(map);
                    String numbers = numberTemplate.render(map);
                    lines.add(new Line(name));
                    lines.add(new Line(numbers));
//                    if(!TextUtils.isEmpty(product.getString("remark"))){
//                        lines.add(new Line("备注:"+product.getString("remark")));
//                    }
                }
            }
            lines.add(getVirtualLine());

            Map<String, Object> all = model.getAll();
            String[] titles = {"商品金额:",  "优惠金额:","运费金额:", "合计金额:"};
            String payType = model.getInt("pay_type") == 0 ? "在线支付" : "货到付款";
            String[] templates = {"￥{{prod_mny | money}}", "-￥{{coupon_mny | money}}", "￥{{deliver_mny | money}}","{{mny}}"};

            lines.add(new Line("支付方式:" + "               " + payType + "\n"));

            for (int i = 0; i < titles.length; i++) {
                String title = titles[i];
                Template t = Template.parse(templates[i]);
                String number = t.render(all) + "\n";

                //total 16;
                StringBuilder sb = new StringBuilder(title);
                int space = 22 - number.length();
                for (int j = 0; j < space; j++) {
                    sb.append(" ");
                }
                sb.append(number);
                lines.add(new Line(sb.toString()));

            }
            lines.add(getVirtualLine());
            lines.add(Line.LineFeed());
            Line companyName = new Line("北京兄弟之恒科技有限公司\n");
            companyName.setAlignment(Line.ALIGNMENT_MIDDLE);
            lines.add(companyName);
            Line serviceLine = new Line("客服电话:4006-406-010\n");
            serviceLine.setAlignment(Line.ALIGNMENT_MIDDLE);
            lines.add(serviceLine);
            lines.add(Line.LineFeed());
            Line signatureLine = new Line("客户签名:_____________\n");
            signatureLine.setAlignment(Line.ALIGNMENT_MIDDLE);
            lines.add(signatureLine);
            lines.add(Line.LineFeed());
            lines.add(Line.LineFeed());
            lines.add(Line.LineFeed());
            BluetoothController.getInstance().print(lines);
        }
    };

    private Line getVirtualLine() {
        Line line = new Line();
        line.setText("--------------------------------\n");
        return line;
    }
}
