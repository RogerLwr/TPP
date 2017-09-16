package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.adapter.AddressListAdapter;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.manager.AddressManager;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.AddressModel;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.TLog;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SelectCityViewController;

import java.util.ArrayList;
import java.util.List;

@ActionBar(title = "编辑地址")
@SuppressWarnings("unused")
@Statistics(page = "地址编辑")
@Layout(id = R.layout.ui_edit_address)
public class EditAddressViewController extends BaseViewController {

    public static final String KEY_ADDRESS_MODEL = "key.addressModel";
    public static final String KEY_USER_NOT_VALIDATED = "userNotValidated";

    @Binding(id = R.id.name_edit_text)
    private EditText nameEditText;
    @Binding(id = R.id.contact_edit_text)
    private EditText contactEditText;
    @Binding(id = R.id.address_edit_text)
    private EditText addressEditText;
    @Binding(id = R.id.code_edit_text)
    private EditText codeEditText;

    @Binding(id = R.id.select_area_button)
    private TextView selectAreaButton;

    @Binding(id = R.id.submit_button)
    private View submitButton;

    @Binding(id = R.id.address_lv)
    private ListView mAddressLv;

    private ActionSheet actionSheet;
    private AddressModel addressModel;

    private double lat;
    private double lng;

    private boolean userNotValidated;

    private String mCity = "北京市";
    private LocationClient mLocationClient;
    private AddressListAdapter mAdapter;

    PoiSearch mPoiSearch = PoiSearch.newInstance();
    SuggestionSearch mSuggestionSearch = SuggestionSearch.newInstance();
    private List<PoiInfo> mPoiInfos = new ArrayList<>();
    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult result) {
            if (result == null
                    || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(ContextProvider.getContext(), "未找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                mPoiInfos.clear();
                mPoiInfos.addAll(result.getAllPoi());
                for (int j = 0; j < mPoiInfos.size(); j++) {

                    PoiInfo poiInfo = mPoiInfos.get(j);
                    String name = poiInfo.name;
                    LatLng location = poiInfo.location;
                    if (location != null) {
                        TLog.w("xx", "207------------" + name + ",location=" + location.toString());
                        mSuggestionAdapter.add(name);
                    }
                }

                if (mAdapter == null) {
                    mAdapter = new AddressListAdapter(mPoiInfos, getActivity());
                    mAddressLv.setAdapter(mAdapter);
                    mAddressLv.setOnItemClickListener(mAddrItemClickListener);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
                mAddressLv.setVisibility(View.VISIBLE);
            } else if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                String strInfo = "在";
                for (CityInfo cityInfo : result.getSuggestCityList()) {
                    strInfo += cityInfo.city;
                    strInfo += ",";
                }
                strInfo += "找到结果";
                Toast.makeText(ContextProvider.getContext(), strInfo, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };
    private OnGetSuggestionResultListener suggestionListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult res) {
//            Toast.makeText(ContextProvider.getContext(),"Suggestion:" + res,Toast.LENGTH_LONG).show();
            if (res == null || res.getAllSuggestions() == null) {
                return;
            }

//            mSuggestionAdapter.clear();
            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                if (info.key != null) {
                    Log.w("xx", "192------------" + info.key + "," + info.district + "," + info.toString());
                    mSuggestionAdapter.add(info.key);
                }
            }
            mSuggestionAdapter.notifyDataSetChanged();
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e("xx", "182-----------isSelectAddr=" + isSelectAddr);
            if (!isSelectAddr) {
                String keyWord = addressEditText.getText().toString();
                if (TextUtils.isEmpty(keyWord) || mCity == null) {
                    return;
                }
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(mCity)//TODO
                        .keyword(keyWord));
                SuggestionSearchOption so = new SuggestionSearchOption();
                so = so.city(mCity).keyword(keyWord);
                mSuggestionSearch.requestSuggestion(so);
            } else {
                isSelectAddr = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private ArrayAdapter<String> mSuggestionAdapter;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        addressModel = (AddressModel) getActivity().getIntent().getSerializableExtra(KEY_ADDRESS_MODEL);
        userNotValidated = getActivity().getIntent().getBooleanExtra(KEY_USER_NOT_VALIDATED, false);
        if (isEditMode()) {
            setTitle("编辑收货地址");
        } else {
            setTitle("新增地址");
        }

        mSuggestionAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line);
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mSuggestionSearch.setOnGetSuggestionResultListener(suggestionListener);


        if (addressModel != null) {
            contactEditText.setText(addressModel.getPhone());
            if (addressModel.getPhone() != null && !addressModel.getPhone().equals(addressModel.getUserName())) {
                nameEditText.setText(addressModel.getUserName());
            }
            addressEditText.setText(addressModel.getAddress());
            codeEditText.setText(addressModel.getDetail());
            if (!TextUtils.isEmpty(addressModel.getArea())) {
                selectAreaButton.setText(addressModel.getArea());//TODO default area?
            }
        } else {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user != null) {
                contactEditText.setText(user.getPhone());
            }
        }
        addressEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (mLocationClient == null) {
                        mLocationClient = new LocationClient(EditAddressViewController.this.getActivity());
                        mLocationClient.registerLocationListener(myListener);
                        setLocationOption();
                    }
                    mLocationClient.start();
                }
            }
        });

        addressEditText.addTextChangedListener(textWatcher);

//        addressEditText.addTextChangedListener(textWatcher);
//        addressEditText.setAdapter(mSuggestionAdapter);
        showContent();
    }

    BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mCity = bdLocation.getCity();
            Log.e("xx", "232------" + bdLocation.getCity());
            mLocationClient.stop();
        }
    };

    //设置百度地图相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //打开gps
        option.setServiceName("com.baidu.location.service_v2.9");
//        option.setPoiExtraInfo(true);
        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPriority(LocationClientOption.GpsFirst);       //gps
//        option.setPoiNumber(10);
        option.disableCache(true);
        mLocationClient.setLocOption(option);
    }

    @OnClick(R.id.select_area_button)
    private View.OnClickListener selectRegionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard();
            actionSheet = new ActionSheet();
            actionSheet.setHeight(DimensionUtil.dip2px(300));
            final SelectCityViewController selectCityViewController = new SelectCityViewController();
            selectCityViewController.setCityOnly(false);
            selectCityViewController.setActivity(getActivity());
            selectCityViewController.setActionSheet(actionSheet);
            actionSheet.setViewController(selectCityViewController);
            selectCityViewController.setOnSelectCityListener(new SelectCityViewController.OnSelectCityListener() {
                @Override
                public void onSelectCity(Model model) {
                    //TODO
                    String text = selectCityViewController.getSelectedCity().getString("name") + "-" +
                            selectCityViewController.getSelectedRegion().getString("name") + "-" +
                            model.getString("name");
                    selectAreaButton.setText(text);
                }
            });
            actionSheet.show();
        }
    };

    @OnClick(R.id.submit_button)
    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user == null) {
                return;//TODO
            }
            String name = nameEditText.getText().toString();
            String contact = contactEditText.getText().toString();
            String area = selectAreaButton.getText().toString();
            String address = addressEditText.getText().toString();
            String code = codeEditText.getText().toString();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(ContextProvider.getContext(), "请输入收货人姓名！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(contact)) {
                Toast.makeText(ContextProvider.getContext(), "请输入您的联系方式！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(area)) {
                Toast.makeText(ContextProvider.getContext(), "请输入您的所在区域！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(address)) {
                Toast.makeText(ContextProvider.getContext(), "请输入您的店铺地址！", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = isEditMode() ? ContextProvider.getBaseURL() + "/api/user/updateUserAddr.json" : ContextProvider.getBaseURL() + "/api/user/addUserAddr.json";
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, saveListener);
            req.setMethod(HttpRequest.POST);
            req.addParam("consignee", name);
            req.addParam("phone", contact);
            req.addParam("user_type",UserModel.USER_TYPE_SELLER + "");
            req.addParam("address", address);
            req.addParam("area", area);
            req.addParam("detail", code);
            req.addParam("lat", String.valueOf(lat));
            req.addParam("lng", String.valueOf(lng));
            req.setErrorListener(new CommonErrorHandler(EditAddressViewController.this) {
                @Override
                public void onError(HttpRequest<?> request, HttpError error) {
                    super.onError(request, error);
                    submitButton.setEnabled(true);
                }
            });

            if (isEditMode()) {
                req.addParam("id", addressModel.getId() + "");
            } else {
                req.addParam("user_id", user.getUserID() + "");
            }

            req.setParser(new ModelParser<>(Void.class));
            VolleyDispatcher.getInstance().dispatch(req);
            view.setEnabled(false);
            showLoading();
        }
    };

    public HttpRequest.ResultListener<ModelResult<Void>> saveListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            submitButton.setEnabled(true);
            hideLoading();
            if (data.isSuccess()) {
                //TODO
                AddressManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, null);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), data.getDesc(), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            } else {
                ResultHandler.handleError(data, EditAddressViewController.this);
            }
        }
    };

    private boolean notValidated() {
        return userNotValidated;
    }

    @OnClick(R.id.address_edit_text)
    private View.OnClickListener addressTextViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

//            Intent intent = new Intent(getActivity(), AddressCheckAct.class);
//            getActivity().startActivityForResult(intent, REQUEST_CODE_ADDRESS);
        }
    };

    boolean isSelectAddr = false;

    AdapterView.OnItemClickListener mAddrItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            PoiInfo poiInfo = mPoiInfos.get(position);
            String name = poiInfo.name;
            LatLng location = poiInfo.location;
            if (location == null) {
                Toast.makeText(getActivity(), "当前地址没有经纬度,请选择其他地址试试", Toast.LENGTH_SHORT).show();
            } else {

                isSelectAddr = true;
                addressEditText.setText(name);
                lat = location.latitude;
                lng = location.longitude;
                mAddressLv.setVisibility(View.GONE);
            }
        }
    };


    public boolean isEditMode() {
        return addressModel != null;
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        if (actionSheet != null && actionSheet.isShowing()) {
            actionSheet.dismiss();
            return true;
        }
        return super.onBackKeyDown(a);
    }
}
