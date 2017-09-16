package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.DeliverTypeAdapter;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.AddressModel;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.pay.PayResult;
import com.tianpingpai.pay.PayService;
import com.tianpingpai.pay.Payment;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ButtonGroup;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.SubmittingViewController;
import com.tianpingpai.utils.JsonObjectMapper;
import com.tianpingpai.utils.PriceFormat;
import com.tianpingpai.utils.Settings;
import com.tianpingpai.utils.TicketLoader;
import com.tianpingpai.widget.SelectDeliverTimeViewController;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Statistics(page = "订单确认")
@ActionBar(title = "提交订单")
@Layout(id = R.layout.ui_cofirm_order)
public class ConfirmOrderViewController extends BaseViewController {

    private static final int REQUEST_CODE_SELECT_ADDRESS = 1;

    public static final String KEY_ORDER_TYPE = "key.OrderType";
    public static final String KEY_FREIGHT = "key.Freight";
    public static final int ORDER_TYPE_NORMAL = 1;
    public static final int ORDER_TYPE_IN_TIME = 10;

    private SubmittingViewController submittingDialog;

    private AddressModel selectedAddress;
    private Bundle freightBundle = null;

    private String orderIds;
    private ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> sellers;

    private ActionSheet actionSheet;
    private SelectDeliverTimeViewController selectTimeViewController = new SelectDeliverTimeViewController();
    private TicketLoader ticketLoader = new TicketLoader();

    private DeliverTypeAdapter deliverTypeAdapter = new DeliverTypeAdapter();
    @Binding(id = R.id.deliver_type_spinner)
    private Spinner deliverTypeSpinner;
    @Binding(id = R.id.deliver_time_text_view)
    private TextView deliverTimeTextView;
    @Binding(id = R.id.pay_off_line_container)
    private View payOfflineContainer;
    @Binding(id = R.id.pay_off_line_button)
    private CompoundButton payOfflineButton;
    @Binding(id = R.id.pay_on_line_container)
    private View payOnlineContainer;
    @Binding(id = R.id.coupon_number_text_view)
    private TextView couponNumberTextView;
    @Binding(id = R.id.pay_on_line_button)
    private CompoundButton payOnlineButton;
    @Binding(id = R.id.name_text_view)
    private TextView nameTextView;
    @Binding(id = R.id.address_edit_text)
    private TextView addressTextView;
    @Binding(id = R.id.set_address_text_view)
    private TextView setAddressTextView;

    //    @Binding(id = R.id.order_status_text_view)
//    private TextView orderStatusTextView;
    @Binding(id = R.id.content_container)
    private View contentContainer;
    @Binding(id = R.id.sum_container)
    private View sumContainer;
    @Binding(id = R.id.order_sum_text_view)
    private TextView orderSumTextView;
    @Binding(id = R.id.discount_sum_text_view)
    private TextView discountSumTextView;
    @Binding(id = R.id.freight_sum_text_view)
    private TextView freightSumTextView;
    @Binding(id = R.id.payment_sum_text_view)
    private TextView paymentSumTextView;
    @Binding(id = R.id.total_price_text_view)
    private TextView totalPriceTextView;
    @Binding(id = R.id.commit_button)
    private View submitButton;
    @Binding(id = R.id.store_orders_container)
    private LinearLayout storeOrdersContainer;
    @Binding(id = R.id.add_to_collection_image_view)
    private ImageView addToCollectionImageView;

    private boolean addToCollection = true;

    private ArrayList<StoreOrderViewHolder> orderViewHolders = new ArrayList<>();

    private ButtonGroup buttonGroup = new ButtonGroup();
    private CompoundButton.OnCheckedChangeListener payMethodCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                return;
            }
            for (StoreOrderViewHolder holder : orderViewHolders) {
                if (buttonView == payOnlineButton) {
                    holder.setPayType(0);//TODO
                } else {
                    holder.setPayType(1);
                }
            }
            update();
        }
    };
    private SelectDeliverTimeViewController.OnSelectedListener onSelectedListener = new SelectDeliverTimeViewController.OnSelectedListener() {
        @Override
        public void onSelected() {
            deliverTimeTextView.setText(selectTimeViewController.getSelectedTime());
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        int orderType = a.getIntent()
                .getIntExtra(KEY_ORDER_TYPE, ORDER_TYPE_NORMAL);

        freightBundle = a.getIntent().getBundleExtra(KEY_FREIGHT);
        if (orderType == ORDER_TYPE_IN_TIME) {
            sellers = ShoppingCartManager.getInstance().getInTimeOrderItems();
        } else {
            sellers = ShoppingCartManager.getInstance().getInOrderItems();
            Log.e("sellers===",sellers.size()+"");
        }
        PayService.getInstance().registerListener(payResultListener);
    }

    @Override
    public void onActivityDestroyed(Activity a) {
        super.onActivityDestroyed(a);
        PayService.getInstance().unregisterListener(payResultListener);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        deliverTimeTextView.setText(selectTimeViewController.getSelectedTime());
        selectTimeViewController.setOnSelectedListener(onSelectedListener);
        deliverTypeSpinner.setAdapter(deliverTypeAdapter);
        buttonGroup.add(payOnlineButton, payOnlineContainer);
        buttonGroup.add(payOfflineButton, payOfflineContainer);
//        double freight = 0;
        if (freightBundle != null) {
            for (String key : freightBundle.keySet()) {
//                freight += freightBundle.getDouble(key);//ky
                double freight = freightBundle.getDouble(key);
                for (Pair<SellerModel, ArrayList<ProductModel>> sm : sellers) {
                    SellerModel seller = sm.first;
                    if (key.equals(seller.getId() + "")) {
                        seller.setFreight(freight);
                    }
                }
            }
        }

        for (Pair<SellerModel, ArrayList<ProductModel>> sm : sellers) {
            SellerModel seller = sm.first;
            StoreOrderViewHolder holder = new StoreOrderViewHolder(getActivity().getLayoutInflater());
            storeOrdersContainer.addView(holder.getView());
            holder.setProducts(sm.second);
            holder.setModel(seller);
            holder.setIsMulty(sellers.isEmpty());
            orderViewHolders.add(holder);
        }

        if (sellers.size() == 1) {
//            sumContainer.setVisibility(View.GONE);
            orderViewHolders.get(0).expand();
        }

//        setActionBarLayout(R.layout.ab_title_white);
        buttonGroup.setOnCheckedChangeListener(payMethodCheckedChangeListener);
        ticketLoader.setTicketLoaderListener(new TicketLoader.TicketLoaderListener() {
            @Override
            public void onTicketLoaded(TicketLoader.Ticket t) {
                loadData();
            }

            @Override
            public void onTicketFailed(HttpError error) {

            }
        });
        ticketLoader.load();
        showLoading();
    }

    private Model root;

    private void update() {
        if (root == null) {
            return;
        }
        List promotions = root.getList("promotionInfos", Model.class);
        double total = 0;
        for (int i = 0; i < promotions.size(); i++) {
            Object obj = promotions.get(i);
            if (obj instanceof Model) {//服务器返回  "";
                Model m = (Model) promotions.get(i);
                for (StoreOrderViewHolder holder : orderViewHolders) {
                    if (holder.getModel().getId() == m.getInt("shopId")) {
                        holder.setPromotion(m);
                    }
                }
            }
        }

        for (StoreOrderViewHolder holder : orderViewHolders) {
            total += holder.getTotal();
        }
        totalPriceTextView.setText(String.format("合计:%s", PriceFormat.format(total)));
        int couponNumber = root.getInt("couponNum");
        if (couponNumber > 0) {
            couponNumberTextView.setText(String.format("有%d张优惠券", couponNumber));
            couponNumberTextView.setVisibility(View.VISIBLE);
        }
        orderSumTextView.setText(String.format("￥%s", PriceFormat.format(getOrderSumPrice())));
        discountSumTextView.setText(String.format("-￥%s", PriceFormat.format(getTotalDiscount())));
        freightSumTextView.setText(String.format("￥%s", PriceFormat.format(getTotalFreight())));
        paymentSumTextView.setText(String.format("￥%s", PriceFormat.format(getTotalPrice())));
    }

    private ResultListener<ModelResult<Model>> getInfoListener = new ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            Log.e("xx", "data:" + data.getModel());
            hideLoading();
            if (data.isSuccess()) {
                root = data.getModel();
                Model address = root.getModel("address");
                if (address != null) {
                    AddressModel addressModel = new AddressModel();
                    JsonObjectMapper.map(address, addressModel);
                    updateAddressInfo(addressModel);
                }
                List<Model> deliverMethods = root.getList("deliverMethods", Model.class);
                deliverTypeAdapter.setModels((ArrayList<Model>) deliverMethods);

                List<Model> payMethods = root.getList("payMethods", Model.class);
                boolean hasDefault = false;
                if (payMethods != null && !payMethods.isEmpty()) {
                    for (Model m : payMethods) {
                        int id = m.getInt("id");
                        boolean isDefault = m.getBoolean("default");
                        if (isDefault && !hasDefault) {
                            hasDefault = true;
                        }
                        //TODO use constant
                        if (id == 0) {
                            payOnlineContainer.setVisibility(View.VISIBLE);
                            payOnlineButton.setChecked(isDefault);
                            payOfflineButton.setChecked(!isDefault);
                            if (isDefault) {
                                payOnlineButton.performClick();
                            } else {
                                payOfflineButton.performClick();
                            }
                        } else {
                            payOfflineContainer.setVisibility(View.VISIBLE);
                            payOfflineButton.setChecked(isDefault);
                            if (isDefault) {
                                payOfflineButton.performClick();
                            } else {
                                payOnlineButton.performClick();
                            }
                            payOnlineButton.setChecked(!isDefault);
                        }
                    }
                }

                if (!hasDefault) {
                    payOfflineContainer.setVisibility(View.VISIBLE);
                    payOnlineButton.setVisibility(View.VISIBLE);
                    payOnlineButton.setChecked(true);
                    payOfflineButton.setChecked(false);
                }

                update();
            }
        }
    };

    private void loadData() {
        String url = ContextProvider.getBaseURL() + "/api/order/confirm";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, getInfoListener);
        String data = "[";
        for (int i = 0; i < sellers.size(); i++) {
            ArrayList<ProductModel> products = sellers.get(i).second;
            double priceSum = 0;
            for (final ProductModel p : products) {
                priceSum += p.getCouponPrice() * p.getProductNum();
            }
            long shopId = sellers.get(i).first.getId();
            double total = priceSum;
            data = data + "{shopId:" + shopId + ",money:" + PriceFormat.format(total) + "},";
        }
        if (data.length() >= 2) {
            data = data.substring(0, data.length() - 1);
        }
        data = data + "]";
        req.addParam("data", data);
        req.addParam("coupon_type", "-1");
        req.addParam("marketid", MarketManager.getInstance().getCurrentMarket().getId() + "");
        req.setParser(new GenericModelParser());
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    @Override
    protected void onReloadData() {
        super.onReloadData();
        loadData();//TODO
    }

    private void updateAddressInfo(AddressModel addressInfo) {
        selectedAddress = addressInfo;
        String name = "收货人: " + addressInfo.getUserName();
        nameTextView.setText(name);
        String address = "收货地址: " + addressInfo.getArea() + " "
                + addressInfo.getAddress() + addressInfo.getDetail();
        addressTextView.setText(address);
        if (!TextUtils.isEmpty(name)) {
            setAddressTextView.setText("");
        }
    }

    //TODO test
    @Override
    public void onActivityResult(Activity a, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a, requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_ADDRESS
                && resultCode == Activity.RESULT_OK) {
            AddressModel addressInfo = (AddressModel) data
                    .getSerializableExtra(AddressListViewController.KEY_ADDRESS_MODEL);
            updateAddressInfo(addressInfo);
        }
    }

    @OnClick(R.id.deliver_time_text_view)
    private OnClickListener timeButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            actionSheet = getActionSheet(true);
            selectTimeViewController.setActivity(getActivity());
            actionSheet.setViewController(selectTimeViewController);
            actionSheet.show();
        }
    };

    @OnClick(R.id.address_container)
    private OnClickListener addressesButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intentAddress = new Intent(getActivity(),
                    ContainerActivity.class);
            intentAddress.putExtra(ContainerActivity.KEY_CONTENT, AddressListViewController.class);
            intentAddress.putExtra(AddressListViewController.KEY_IS_SELECTION_MODE, true);
            getActivity().startActivityForResult(intentAddress, REQUEST_CODE_SELECT_ADDRESS);
        }
    };

    @OnClick(R.id.add_to_collection_image_view)
    private OnClickListener addToCollectionImageViewListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            addToCollection = !addToCollection;
            if (addToCollection) {
                addToCollectionImageView.setImageResource(R.drawable.check_box_rect_green_checked);
            } else {
                addToCollectionImageView.setImageResource(R.drawable.check_box_rect_green_unchecked);
            }
        }
    };

    private boolean isAddressSelected() {
        return selectedAddress != null;
    }

    private void submit() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), "登录后重试！", Toast.LENGTH_LONG).show();
            if (actionSheet != null) {
                actionSheet.dismiss();
            }
            return;
        }
        int payType = payOfflineButton.isChecked() ? 1 : 0;
        Model deliverTypeModel = deliverTypeAdapter.getItem(deliverTypeSpinner.getSelectedItemPosition());
        int deliverType = deliverTypeModel == null ? 0 :deliverTypeModel.getInt("id");

        Model model = new Model();
        model.set("pay_type", payType);
        model.set("deliver_type", deliverType);
        ArrayList<Model> sellerList = new ArrayList<>();

        for (StoreOrderViewHolder holder : orderViewHolders) {
            Model seller = holder.getSubmitParam();
            sellerList.add(seller);
        }
        double totalPrice = getTotalPrice();
        model.setList("sale_user", sellerList);
        model.set("mny", totalPrice);

        String json = Model.valueString(model.getList("sale_user", Model.class));

//        String url = URLUtil.ADDORDER_URL;
        String url = URLUtil.ORDERADD_URL;
        final HttpRequest<ModelResult<Model>> req = new HttpRequest<>(
                url, listener);

        req.setMethod(HttpRequest.POST);
        req.addParam("accessToken", user.getAccessToken());
        req.addParam("ticket", ticketLoader.getTicket().getValue());
        req.addParam("address", selectedAddress.getArea() + " " + selectedAddress.getAddress() + selectedAddress.getDetail());
        req.addParam("b_user_id", user.getUserID());
        req.addParam("deliver_dt", selectTimeViewController.getSelectedTime());
        req.addParam("mny", model.get("mny") + "");
        req.setMinLoadingTime(2000);
        req.addParam("pay_type", payType + "");
        req.addParam("deliver_type", deliverType + "");
        req.addParam("addCommon", addToCollection ? "1" : "0");//TODO
        req.addParam("recevier_name", selectedAddress.getUserName());
        Log.e("xx", "uuid:" + Settings.getInstance().getUUID());
        req.addParam("equip_id", Settings.getInstance().getUUID());
        if (!TextUtils.isEmpty(user.getPhone())) {
            req.addParam("telephone", user.getPhone());
        }
        req.addParam("coupon_type", "-1");
        req.setAttachment(payType);
        req.addParam("sale_users", json);
        req.addParam("source","1");
        req.setParser(new GenericModelParser());
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                submitButton.setEnabled(true);
                if (submittingDialog != null) {
                    submittingDialog.showButtons();
                }
            }
        });

        VolleyDispatcher.getInstance().dispatch(req);
        submitButton.setEnabled(false);
    }

    private double getTotalPrice() {
        double totalPrice = 0;
        for (StoreOrderViewHolder holder : orderViewHolders) {
            totalPrice += holder.getTotal();
        }
        if (totalPrice < 0) {
            totalPrice = 0;
        }
        return totalPrice;
    }

    private double getOrderSumPrice() {
        double sum = 0;
        for (StoreOrderViewHolder holder : orderViewHolders) {
            sum += holder.getOrderPrice();
        }
        return sum;
    }

    private double getTotalDiscount() {
        double sum = 0;
        for (StoreOrderViewHolder holder : orderViewHolders) {
            sum += holder.getDiscount();
        }
        return sum;
    }

    private double getTotalFreight() {
        double sum = 0;
        for (StoreOrderViewHolder holder : orderViewHolders) {
            sum += holder.getFreight();
        }
        return sum;
    }

    private double getTotalPayment() {
        double sum = 0;
        for (StoreOrderViewHolder holder : orderViewHolders) {
            sum += holder.getTotal();
        }
        return sum;
    }

    @OnClick(R.id.commit_button)
    private OnClickListener submitButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isAddressSelected()) {
                Toast.makeText(ContextProvider.getContext(), "您还未添加收货地址", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalPrice = getTotalPrice();
            actionSheet = getActionSheet(true);
            actionSheet.setCancelable(false);
            ConfirmOrderDialog dialog = new ConfirmOrderDialog();
            dialog.setActivity(getActivity());
            dialog.setPrice(totalPrice);
            dialog.setActionSheet(actionSheet);
            dialog.setPositiveButtonListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    submittingDialog = new SubmittingViewController();
                    submittingDialog.setActionSheet(actionSheet);
                    submittingDialog.setActivity(getActivity());
                    submittingDialog.setRetryButtonListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("xx", "onClick");
                            if (ticketLoader.isTicketValid()) {
                                Log.e("xx", "valid");
                                submittingDialog.showLoading();
                                submit();
                            } else {
                                Log.e("xx", "invalid");
//                                reloadTicket();
                            }
                        }
                    });
                    submittingDialog.setCancelButtonListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    });
                    actionSheet.pushViewController(submittingDialog);
                    submit();
                }
            });
            actionSheet.setViewController(dialog);
            actionSheet.show();
        }
    };

    private int time=0 ;
    private ResultListener<ModelResult<Model>> listener = new ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(
                HttpRequest<ModelResult<Model>> request,
                ModelResult<Model> data) {
            if (getActivity() == null) {
                return;
            }
            submitButton.setEnabled(true);
            if (data.isSuccess()) { // 支付完成显示 在线支付的价格
//                Log.e()
                orderIds = data.getModel().getString("order_ids");
                ShoppingCartManager.getInstance().clearShoppingCart();
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                int payType = request.getAttachment(Integer.class);
                if (payType == 1) {//TODO use constants
                    intent.putExtra(ContainerActivity.KEY_CONTENT, PaySuccessViewController.class);
                    intent.putExtra(PaySuccessViewController.KEY_PAYED, false);
                } else {

                    List<Model> payPlatforms = root.getList("payPlaforms",Model.class);
                    ArrayList<String> platforms = new ArrayList<>();
                    for(Model m:payPlatforms){
                        platforms.add(m.toJsonString());
                    }

                    intent.putExtra(ContainerActivity.KEY_CONTENT, SelectPaymentViewController.class);
                    intent.putExtra(SelectPaymentViewController.KEY_ORDER_ID, data.getModel().getString("order_ids"));
                    intent.putExtra(SelectPaymentViewController.KEY_SELECT_COUPON,platforms);
                    Intent nextIntent = new Intent(getActivity(), ContainerActivity.class);
                    nextIntent.putExtra(ContainerActivity.KEY_CONTENT, OrdersViewController.class);
                    nextIntent.putExtra(OrdersViewController.KEY_ORDER_FORM, CommonUtil.NOT_PAY_MONEY + "");
                    intent.putExtra(SelectPaymentViewController.KEY_NEXT_INTENT, nextIntent);
                }
                getActivity().startActivity(intent);
                getActivity().finish();
            } else if (data.getCode() == 7) {
                ticketLoader.getTicket().invalidate();
            } else if (data.getCode() == 6) {
                while (time<5){
                    time ++;
                    submit();
                }
                actionSheet.dismiss();
                Toast.makeText(getActivity(),"该订单有问题",Toast.LENGTH_SHORT).show();
            }else {
                if (submittingDialog != null) {
                    submittingDialog.showButtons();
                }
                ResultHandler.handleError(data, ConfirmOrderViewController.this);
            }
        }
    };

    private ModelStatusListener<PayResult, Payment> payResultListener = new ModelStatusListener<PayResult, Payment>() {
        @Override
        public void onModelEvent(PayResult result, Payment model) {
            if (result.isSuccess() && model.getOrderId().equals(orderIds)) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
    };
}
