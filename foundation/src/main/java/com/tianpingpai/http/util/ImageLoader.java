package com.tianpingpai.http.util;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.ViewTarget;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;

public class ImageLoader {
	
	public static void load(String url,ImageView imageView){
		load(url,imageView,R.drawable.default_loading, R.drawable.default_loading);
	}

	static {
		ViewTarget.setTagId(R.id.glide_tag);
	}

	public static void load(String url,ImageView imageView,int loadingRes,int failureRes){
		if(url != null){
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(loadingRes);
			imageView.setTag(R.id.key_image_url,url);
			mImageLoader.get(url, new MImageListener(url,imageView, failureRes));
//			Glide.with(imageView.getContext()).load(url).placeholder(loadingRes).fallback(failureRes).crossFade().into(imageView);
		}
	}

	static RequestQueue mQueue = Volley.newRequestQueue(ContextProvider.getContext());
	static com.android.volley.toolbox.ImageLoader mImageLoader = new com.android.volley.toolbox.ImageLoader(
			mQueue, new BitmapCache());

	public static class BitmapCache implements ImageCache {
		private LruCache<String, Bitmap> mCache;

		public BitmapCache() {
			int maxSize = 10 * 1024 * 1024;
			mCache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes() * value.getHeight();
				}
			};
		}

		@Override
		public Bitmap getBitmap(String url) {
			return mCache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}

	}

	static class MImageListener implements ImageListener{

		public MImageListener(String url,ImageView imageView,int error){
			this.imageView = imageView;
			this.errorImage = error;
			this.url = url;
		}

		private ImageView imageView;
		private int errorImage;
		private String url;

		@Override
		public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
			if(response.getBitmap() != null) {
				String url = (String) imageView.getTag(R.id.key_image_url);
				if(this.url.equals(url)){
					imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					imageView.setImageBitmap(response.getBitmap());
					imageView.requestLayout();
				}
			}
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setImageResource(errorImage);
		}
	}
}
