package com.tianpingpai.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

/**
 * 图片选择，拍照，裁剪
 *
 * @author E1230
 */
public class ImagePicker {
    private final int _2000 = 2000;
    private Activity activityContext;
    private Fragment fragmentContext;
    private OnPhotoPickedlistener onPhotoPickedlistener;

    /**
     * 普通的activity使用的构造器，回调activity中的onActivityResult(...)
     *
     * @param context
     * @param onPhotoPickedlistener
     */
    public ImagePicker(Activity context,
                         OnPhotoPickedlistener onPhotoPickedlistener) {
        super();
        this.activityContext = context;
        this.onPhotoPickedlistener = onPhotoPickedlistener;
    }

    /**
     * fragment使用的构造器，解决fragment中调用startActivityForResult时，没有回调fragment中的onActivityResult(...)而调用了Fragment管理activity的onActivityResult(...)
     *
     * @param activityContext
     * @param fragmentContext
     * @param onPhotoPickedlistener
     */
    public ImagePicker(Activity activityContext, Fragment fragmentContext,
                         OnPhotoPickedlistener onPhotoPickedlistener) {
        super();
        this.activityContext = activityContext;
        this.fragmentContext = fragmentContext;
        this.onPhotoPickedlistener = onPhotoPickedlistener;
    }

    /**
     * 在调用的activity或fragment的onActivityResult(...)中调用次方法，处理拍照/相册返回的数据
     */
    public void pickResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case CROPED_PHOTO: // 调用图片裁剪返回的
                Bitmap img = data.getParcelableExtra("data");
                if (onPhotoPickedlistener != null) {
                    onPhotoPickedlistener.photoPicked(null, img);
                }

                break;

            case CAMERA_WITH_DATA: // 照相机程序返回的
//                Toast.makeText(TApplication.getContext(),"拍照返回。。。。" + mCurrentPhotoFile.getPath() + " -crop- " + doCrop,5000).show();
                if (doCrop) {
                    doCropPhoto(mCurrentPhotoFile, cropWidth, cropHeight);
                } else {
                    if (onPhotoPickedlistener != null) {
                        onPhotoPickedlistener.photoPicked(
                                mCurrentPhotoFile.getPath(), null);
                        mCurrentPhotoFile = null;
                    }
                }
                break;
            case PHOTO_PICKED:

//                Bundle b = data.getExtras();
//                for (Iterator<String> it = b.keySet().iterator();it.hasNext();){
//                    String key = it.next();
//                   LogUtil.info("附加值 - " + key + " - " + b.get(key));
//                }
                Uri originalUri = data.getData(); // 调用相册选择返回的图片

                String path = getGalleryImgPath(originalUri);
//                Toast.makeText(TApplication.getContext(),"相册返回。。。。" + path,5000).show();
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                if (doCrop) {
                    doCropPhoto(new File(path), cropWidth, cropHeight);
                } else {
                    if (onPhotoPickedlistener != null) {
                        onPhotoPickedlistener.photoPicked(path, null);
                    }
                }
                break;
        }
    }

    /**
     * 是否裁剪
     */
    private boolean doCrop;
    /**
     * 裁剪宽度
     */
    private int cropWidth;
    /**
     * 裁剪高度
     */
    private int cropHeight;
    /*
     * 用来标识请求照相功能的 activity
     */
    public final int CAMERA_WITH_DATA = 3023;
    /*
     * 用来标识请求 gallery 的 activity
     */
    public final int CAMERA_CROP = 3022;
    /*
     * 用来标识请求 gallery 的 activity
     */
    public final int CROPED_PHOTO = 3021;
    public final int PHOTO_PICKED = 3024;
    /*
     * 拍照的照片存储位置
     */
    private final File PHOTO_DIR_SD = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    private final File PHOTO_DIR_ROOT = new File(Environment.getRootDirectory()
            + "/DCIM/Camera");
    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    /**
     * 有取消选择的图片（需设置监听器），拍照和选择图片3个选项
     *
     * @param cropImg
     * @param outWith
     * @param outHeight
     * @param cancelClickListener
     */
    public void doPickPhotoAction(final boolean cropImg, final int outWith,
                                  final int outHeight,
                                  final OnPickPhotoCancelClickListener cancelClickListener) {
        this.doCrop = cropImg;
        this.cropWidth = outWith;
        this.cropHeight = outHeight;
        File dir = null;
        // showToast(activity, "若添加实时拍摄照片导致重启，请尝试在应用外拍照，再选择从相册中获取进行添加！");
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            dir = PHOTO_DIR_SD;
        } else {
            dir = PHOTO_DIR_ROOT;
        }
        if (!dir.exists()) {
            dir.mkdirs();// 创建照片的存储目录
        }
        mCurrentPhotoFile = new File(dir, getImgName());// 给新照的照片文件命名
        // Wrap our context to inflate list items using correct theme
        Context dialogContext = new ContextThemeWrapper(activityContext,
                android.R.style.Theme_Light);
        String cancel = "返回";
        String[] choices = new String[3];
        choices[0] = "清除";//
        choices[1] = "拍照";// getString(MediaStore.ACTION_IMAGE_CAPTURE); //拍照
        choices[2] = "从相册选择图片";// getString(R.string.pick_photo); //从相册中选择
        ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                android.R.layout.simple_list_item_1, choices);

        AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        builder.setTitle("选择图片");
        builder.setSingleChoiceItems(adapter, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                if (cancelClickListener != null) {
                                    cancelClickListener.onClick();
                                }
                                dialog.dismiss();
                                break;
                            case 1: {
                                doTakePhoto();
                                break;

                            }
                            case 2:
                                doPickPhotoFromGallery();// 从相册中去获取
                                break;
                        }
                    }
                });
        builder.setNegativeButton(cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        builder.create().show();
    }

    /**
     * 只有拍照和选择照片两个选项
     *
     * @param cropImg
     * @param outWith
     * @param outHeight
     */
    public void doPickPhotoAction(final boolean cropImg, final int outWith,
                                  final int outHeight) {
        this.doCrop = cropImg;
        this.cropWidth = outWith;
        this.cropHeight = outHeight;
        File dir = null;
        // showToast(activity, "若添加实时拍摄照片导致重启，请尝试在应用外拍照，再选择从相册中获取进行添加！");
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            dir = PHOTO_DIR_SD;
        } else {
            dir = PHOTO_DIR_ROOT;
        }
        if (!dir.exists()) {
            dir.mkdirs();// 创建照片的存储目录
        }
        mCurrentPhotoFile = new File(dir, getImgName());// 给新照的照片文件命名

        // Wrap our context to inflate list items using correct theme
        Context dialogContext = new ContextThemeWrapper(activityContext,
                android.R.style.Theme_Light);
        String cancel = "返回";
        String[] choices = new String[2];
        choices[0] = "拍照";// getString(MediaStore.ACTION_IMAGE_CAPTURE); //拍照
        choices[1] = "从相册选择图片";// getString(R.string.pick_photo); //从相册中选择
        ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                android.R.layout.simple_list_item_1, choices);

        AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        builder.setTitle("选择图片");
        builder.setSingleChoiceItems(adapter, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0: {
                                doTakePhoto();
                                break;

                            }
                            case 1:
                                doPickPhotoFromGallery();// 从相册中去获取
                                break;
                        }
                    }
                });
        builder.setNegativeButton(cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        builder.create().show();
    }

    /**
     * 直接调用系统拍照，用于自定义界面
     *
     * @param cropImg
     * @param outWith
     * @param outHeight
     */
    public void takePhoto(final boolean cropImg, final int outWith,
                          final int outHeight) {
        this.doCrop = cropImg;
        this.cropWidth = outWith;
        this.cropHeight = outHeight;
        File dir = null;
        // showToast(activity, "若添加实时拍摄照片导致重启，请尝试在应用外拍照，再选择从相册中获取进行添加！");
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            dir = PHOTO_DIR_SD;
        } else {
            dir = PHOTO_DIR_ROOT;
        }
        if (!dir.exists()) {
            dir.mkdirs();// 创建照片的存储目录
        }
        mCurrentPhotoFile = new File(dir, getImgName());// 给新照的照片文件命名

        doTakePhoto();
    }

    /**
     * 直接调用系统相册获取图片，用于自定义界面
     *
     * @param cropImg
     * @param outWith
     * @param outHeight
     */
    public void chooseFromGallery(final boolean cropImg, final int outWith,
                                  final int outHeight) {
        this.doCrop = cropImg;
        this.cropWidth = outWith;
        this.cropHeight = outHeight;
        doPickPhotoFromGallery();// 从相册中去获取
    }

    private void doTakePhoto() {
        try {
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            if (fragmentContext != null)
                fragmentContext
                        .startActivityForResult(intent, CAMERA_WITH_DATA);
            else
                activityContext
                        .startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(activityContext, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        if (doCrop) {
            intent.putExtra("return-data", true);
        }
        return intent;
    }

    // 请求Gallery程序
    private void doPickPhotoFromGallery() {
        try {
            // Launch picker to choose photo for selected contact
            final Intent intent = getPhotoPickIntent();
            if (fragmentContext != null)
                fragmentContext.startActivityForResult(intent, PHOTO_PICKED);
            else
                activityContext.startActivityForResult(intent, PHOTO_PICKED);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(activityContext, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 封装请求Gallery的intent
    private Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//        intent.setType("image/*");

        // if (cropImg) {
        // intent.putExtra("crop", "true");
        // // 设置裁剪框高宽比例和输出的图片尺寸，不设为原始尺寸
        // // if (outHeight > outWith) {
        // // intent.putExtra("aspectX", 1);
        // // intent.putExtra("aspectY", outHeight / outWith);
        // // } else if (outWith > outHeight) {
        // intent.putExtra("aspectX", outWith);
        // intent.putExtra("aspectY", outHeight);
        // // }
        // intent.putExtra("outputX", outWith);
        // intent.putExtra("outputY", outHeight);
        // // 设置使系统返回图片bitmap对象
        // intent.putExtra("return-data", true);
        // }
        return intent;
    }

    private void doCropPhoto(File f, int outWith, int outHeight) {
        try {
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(Uri.fromFile(f), outWith,
                    outHeight);
            if (fragmentContext != null)
                fragmentContext.startActivityForResult(intent, CROPED_PHOTO);
            else
                activityContext.startActivityForResult(intent, CROPED_PHOTO);
        } catch (Exception e) {
            Toast.makeText(activityContext, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序
     */
    private Intent getCropImageIntent(Uri photoUri, int outWith, int outHeight) {
        Intent intent = new Intent("com.android.camera.action.CROP")
                .setDataAndType(photoUri, "image/*").putExtra("crop", "true")
                .putExtra("return-data", true);
        // .putExtra("scale", true)
        // // 黑边
        // .putExtra("scaleUpIfNeeded", true)
        // // 黑边
        intent.putExtra("aspectX", outWith);
        intent.putExtra("aspectY", outHeight);
        intent.putExtra("outputX", outWith);
        intent.putExtra("outputY", outHeight);
        return intent;
    }

    @SuppressWarnings("deprecation")
    private String getGalleryImgPath(Uri photoUri) {
        // 这里开始的第二部分，获取图片的路径：

        String[] proj = {MediaStore.Images.Media.DATA};

        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = activityContext.managedQuery(photoUri, proj, null,
                null, null);

        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();

        // 最后根据索引值获取图片路径
        String path = cursor.getString(column_index);

        return path;
    }

    public static String getImgName() {
//        SimpleDateFormat sdf = new SimpleDateFormat();
//        sdf.applyPattern("yyyyMMdd_HHmmsss");
        StringBuilder result = new StringBuilder("IMG_");
        result.append(DateFormat.format("yyyyMMdd_HHmmsss", new Date()).toString() + (int) (Math.random() * 100))
                .append(".jpg");
        return result.toString();
    }

    public interface OnPhotoPickedlistener {
        /**
         * 调用返回，若为拍照或从图库中直接读取，path有值，bmp为null,若经过裁剪，path为null,bmp有值
         *
         * @param path
         * @param bmp
         */
        public void photoPicked(String path, Bitmap bmp);
    }

    public interface OnPickPhotoCancelClickListener {

        public void onClick();
    }


    /**
     * 将图片信息插入系统数据库，在系统相册中便能看到这个图片
     *
     * @param context
     * @param title
     * @param name
     * @param path
     * @param mimeType
     */
    public static void insertMedia(Context context, String title, String name, String path, String mimeType) {
        ContentValues values = new ContentValues(7);
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);//"image/jpeg"
        values.put(MediaStore.Images.Media.DATA, path);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}