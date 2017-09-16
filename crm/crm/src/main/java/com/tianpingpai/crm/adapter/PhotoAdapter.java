package com.tianpingpai.crm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.CrmApplication;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.photo.AlbumBitmapCacheHelper;
import com.tianpingpai.crm.photo.CommonUtil;
import com.tianpingpai.crm.photo.PickOrTakeImageActivity;
import com.tianpingpai.crm.ui.LoginViewController;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.util.ImageLoader;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.user.UserModel;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class PhotoAdapter extends BaseAdapter {

    //七牛key的区别值
    private String qiNiuPath;

    private View upView;
    private ImageView loadImage;

    private String currentUpLoadPath;
    //上传图片的尺寸大小
    private int width = 960;
    //上传图片的大小限制
    private int howBig = 500;

    private int status;

    //上传资料的客户
    private CustomerModel customerModel;

    //设置数量
    private boolean isNetPicture = false;
    private int number= 5;
    private int lineNumber = 3;
    private int perWidth ;
    private Activity activity;
    private Resources resources;
    private LayoutInflater inflater = null;
    private ArrayList<String> date = new ArrayList<>();

    public void setUpView(View v,ImageView iv){
        this.upView = v;
        this.loadImage = iv;
    }

    private ArrayList<String> upLoadOkDate = new ArrayList<>();

    public ArrayList<String> getUpLoadOkDate(){
        return upLoadOkDate;
    }

    public String getUpLoadKey(){
        String ss = "";
        for(int i = 0;i< upLoadOkDate.size();i++){
            ss = ss + upLoadOkDate.get(i) + ",";
        }
        if(ss.length()>0){
            return ss.substring(0,ss.length()-1);
        }
        return "";
    }

    public void clearKeyDate(){
        final ActionSheetDialog dialog = new ActionSheetDialog();
        dialog.setActivity((FragmentActivity) activity);
        dialog.setActionSheet(new ActionSheet());
        dialog.setTitle("您确定要删除图片吗?");
        dialog.setPositiveButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < upLoadOkDate.size(); i++) {
                    deleteOld(upLoadOkDate.get(i));
                    Log.e("删除图片", upLoadOkDate.get(i));
                }
                upLoadOkDate.clear();
                dialog.dismiss();
                clearDate();
            }
        });

        dialog.show();

    }
    public void clearDate(){
        date.clear();
        notifyDataSetChanged();
    }

    public void setCustomerModel(CustomerModel cm){
        this.customerModel = cm;
    }

    public void setQiNiuPath(String s){
        this.qiNiuPath = s;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public void setIsNetPicture(boolean isNetPicture){
        this.isNetPicture = isNetPicture;
    }
    public void setActivity(Activity a){
        this.activity = a;
        this.resources=a.getResources();
        inflater = LayoutInflater.from(a);
        perWidth = (((WindowManager) (CrmApplication.getInstance().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getWidth() - CommonUtil.dip2px(activity, 4))/lineNumber;
    }
    public void setGridView(GridView myGV){
//        MyGridView gridView = myGV;
    }
    public void setNumber(int n){
        this.number = n;
    }
    public void setPerWidth(int p){
        this.perWidth = p;
    }
    public void setNetDate(ArrayList<String> list){
        date = list;
        for (int i = 0;i<list.size();i++){
            upLoadOkDate.add(getImageKey(list.get(i)));
        }
        notifyDataSetChanged();

    }
    public void setDate(ArrayList<String> list,boolean isUpload){
        if(date==null){
            date=list;
        }else{
            for (String s: list){
                date.add(s);
            }
        }
        notifyDataSetChanged();
        if(isUpload){
            currentUpLoadPath = list.get(0);
            getQiNiuToken();
        }
    }

    public ArrayList<String> getDate(){
        return date;
    }

    @Override
    public int getCount() {
        if(date ==null){
            return 1;
        }
        if(date.size()==number){
            return number;
        }
        return date.size()+1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return date.get(position);
    }

    @Override
    public View getView( final int position, View convertView, ViewGroup parent) {

        View view = null;
        if(getCount()<=number){
            if(position<(getCount()-1)){
                view = activity.getLayoutInflater().inflate(R.layout.item_pick_up_image_1,null);
                final ImageView iv_1 = (ImageView)view.findViewById(R.id.iv_content);
                ImageView iv_2 =(ImageView)view.findViewById(R.id.iv_pick_or_not);
                if(isNetPicture){
                    ImageLoader.load(date.get(position), iv_1);
                    iv_2.setVisibility(View.GONE);
                }else{
                    Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(date.get(position),perWidth,perWidth,new AlbumBitmapCacheHelper.ILoadImageCallback(){
                        @Override
                        public void onLoadImageCallBack(Bitmap bitmap, String path, Object... objects) {
                            if(bitmap!=null){
                                iv_1.setImageBitmap(bitmap);
                            }
                        }
                    },position);
                    if(bitmap!=null){
                        iv_1.setImageBitmap(bitmap);
                    }

                }
                iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        date.remove(position);
                        deletePosition(position);
                        upLoadOkDate.remove(position);
                        notifyDataSetChanged();
                    }
                });

            }else if(position==(getCount()-1)&&date.size()!=number){
                view = activity.getLayoutInflater().inflate(R.layout.item_add_photo,null);
                ImageView iv_add = (ImageView)view.findViewById(R.id.add_photo);
                iv_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(date.size()<number){
                            if(!isNetPicture){
                                Intent intent = new Intent(activity, PickOrTakeImageActivity.class);
                                intent.putExtra(PickOrTakeImageActivity.EXTRA_NUMS,1);
                                activity.startActivityForResult(intent,status);
                            }
                        }
                    }
                });
            }else{
                view = activity.getLayoutInflater().inflate(R.layout.item_pick_up_image_1,null);
                final ImageView iv_1 = (ImageView)view.findViewById(R.id.iv_content);
                ImageView iv_2 =(ImageView)view.findViewById(R.id.iv_pick_or_not);
                if(isNetPicture){
                    ImageLoader.load(date.get(position), iv_1);
                    iv_2.setVisibility(View.GONE);
                }else{
                    Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(date.get(position),perWidth,perWidth,new AlbumBitmapCacheHelper.ILoadImageCallback(){
                        @Override
                        public void onLoadImageCallBack(Bitmap bitmap, String path, Object... objects) {
                            if(bitmap!=null){
                            iv_1.setImageBitmap(bitmap);
                            }
                        }
                    },position);
                    if(bitmap!=null){
                        iv_1.setImageBitmap(bitmap);
                    }

                }
                iv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        date.remove(position);
                        deletePosition(position);
                        upLoadOkDate.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        }else{

            view = activity.getLayoutInflater().inflate(R.layout.item_pick_up_image_1,null);
            final ImageView iv_1 = (ImageView)view.findViewById(R.id.iv_content);
            ImageView iv_2 =(ImageView)view.findViewById(R.id.iv_pick_or_not);
            ImageLoader.load(date.get(0), iv_1);
            iv_2.setVisibility(View.GONE);

        }
        return view;
    }

    private void getQiNiuToken() {
        showUpLoad(true);
        HttpRequest<ModelResult<String>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/image/token", qiNiuTokenListener);
        req.setParser(new ModelParser<>(String.class));
        setEnabled(false);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_LONG).show();
                setEnabled(true);
                showUpLoad(false);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void setEnabled(boolean enabled) {
//        submitButton.setEnabled(enabled);
//        if (enabled) {
//            hideLoading();
//        } else {
//            setIndicatorCancelable(false);
//            showLoading();
//        }

    }

    private HttpRequest.ResultListener<ModelResult<String>> qiNiuTokenListener = new HttpRequest.ResultListener<ModelResult<String>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<String>> request, ModelResult<String> data) {
            if (data.isSuccess()) {
                uploadImage(data.getModel());
            } else {
                showUpLoad(false);
                setEnabled(true);
                if (data.getCode() == HttpResult.CODE_AUTH) {
                    if (activity != null) {
                        Intent intent = new Intent(activity, ContainerActivity.class);
                        intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                        activity.startActivity(intent);
                    } else {
                        Toast.makeText(ContextProvider.getContext(), "请登录后重试!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ContextProvider.getContext(), "上传图片失败!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void uploadImage(final String token) {

        final UserModel user = UserManager.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), "登录后请重试!", Toast.LENGTH_LONG).show();
            setEnabled(true);
            return;
        }
        Toast.makeText(ContextProvider.getContext(), "正在上传图片,请稍候!", Toast.LENGTH_SHORT).show();

        //-----
//        for (int i = 0; i < date.size(); i++) {
            AlbumBitmapCacheHelper.getInstance().getBitmapFromPath(currentUpLoadPath
                    , width, width, new AlbumBitmapCacheHelper.ILoadImageCallback() {
                @Override
                public void onLoadImageCallBack(Bitmap bitmap, String path, Object... objects) {
                    if (bitmap != null) {
                        Log.e("开始上传：","sdsdsdsadsadasdsadsadasdas====bankString");
                        upload(bitmap, token );
                    }
                }
            }, currentUpLoadPath);
//        }
    }

    private void upload(Bitmap bm, String token) {
//        String curTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String curTime = System.currentTimeMillis()+"";

        String mNewImageKey = "saler/" + customerModel.getUserId() + qiNiuPath + curTime + ".png";
        Map<String, String> params = new HashMap<>();
        params.put("seller:userTel", "" + customerModel.getPhone());
        final UploadOptions opt = new UploadOptions(params, null, true, null, null);
        UploadManager uploadManager = new UploadManager();

        byte[] byteArray = compressImage(bm,howBig);
        Log.e("图片大小======",""+byteArray.length/1024);
        uploadManager.put(byteArray, mNewImageKey, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    upLoadOkDate.add(key);

                    Log.e("上传成功", "sdsadsadsad===============" + key);
                    showUpLoad(false);
                } else {
                    showUpLoad(false);
                    date.remove(currentUpLoadPath);
                    notifyDataSetChanged();
                    String error = info.error;
                    Log.e("上传失败", "==================失败啦-----" + error);
                    if (error.equals("expired token")) {
                        getQiNiuToken();
                    } else {
                        setEnabled(true);
                        Toast.makeText(ContextProvider.getContext(), "上传图片错误:" + error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, opt);
    }

    //压缩图片的方法
    /**
     *
     * @param image 图片数据
     * @param size 压缩到多大（单位是KB）
     * @return 图片字节
     */
    private byte[] compressImage(Bitmap image,int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024> size) {  //循环判断如果压缩后图片是否大于sizekb,大于继续压缩
            options -= 10;//每次都减少10
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }

        return baos.toByteArray();
    }


    private void deleteOld(final String key) {

        String url = ContextProvider.getBaseURL() + "/api/image/delete";
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, deleteListener);
//            req.addParam("key", date.get(position));
        req.addParam("key", key);
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void deletePosition(int position){
        String url = ContextProvider.getBaseURL() + "/api/image/delete";
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, deleteListener);
//            req.addParam("key", date.get(position));
        req.addParam("key", upLoadOkDate.get(position));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    public java.lang.String getImageKey(String pictureUrl) {
        String[] keys = pictureUrl.split(".com/");
        return keys[keys.length - 1];
    }

    private HttpRequest.ResultListener<ModelResult<Void>> deleteListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
        }
    };

    public void showUpLoad(boolean b){
        if(b){
            upView.setVisibility(View.VISIBLE);
            if(loadImage!=null){
                ((AnimationDrawable) loadImage.getBackground()).start();
            }
        }else{
            upView.setVisibility(View.GONE);
        }
    }

}
