package com.tianpingpai.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Locale;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.model.VersionModel;
import com.tianpingpai.utils.SingletonFactory;

public class VersionManager extends ModelManager<ModelEvent, VersionModel> {

	public static VersionManager getInstance() {
		return SingletonFactory.getInstance(VersionManager.class);
	}

	private BroadcastReceiver downloadCompleteReciver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("xx", "downloadCOmplete:" + intent.getExtras().keySet());
			// Intent i = new Intent(Intent.ACTION_VIEW);
			for (String key : intent.getExtras().keySet()) {
				Log.e("xx", "key=" + key);
			}
			install();
		}
	};

	private File apkFile = null;

	public File getApkFile() {
		if (apkFile == null) {
			apkFile = new File(ContextProvider.getContext()
					.getExternalFilesDir("tianpingpai"), "tianpingpai.apk");
		}
		return apkFile;
	}

	private void install() {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(getApkFile()),
				"application/vnd.android.package-archive");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ContextProvider.getContext().startActivity(i);
	}

	public void update(VersionModel version) {
		String url = version.getUrl();
		DownloadManager downloadManager = (DownloadManager) ContextProvider
				.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		File f = getApkFile();
		if (f.exists()) {
			String sha1 = fileToSHA1(getApkFile().getAbsolutePath());
			Log.e("xx", "sha1:" + sha1);
			if (sha1 != null && sha1.equals(version.getSha1())){
				install();
				return;
			}
		} 
		request.setDestinationUri(Uri.fromFile(f));
		request.setTitle("天平派买家");
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
		// request.setMimeType("application/com.trinea.download.file");
		long downloadId = downloadManager.enqueue(request);
		ContextProvider.getContext().registerReceiver(
				downloadCompleteReciver,
				new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		Log.e("xx", "downloadId" + downloadId);
	}

	/**
	 * Get the sha1 value of the filepath specified file
	 * 
	 * @param filePath
	 *            The filepath of the file
	 * @return The sha1 value
	 */
	public String fileToSHA1(String filePath) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath); // Create an
															// FileInputStream
															// instance
															// according to the
															// filepath
			byte[] buffer = new byte[1024]; // The buffer to read the file
			MessageDigest digest = MessageDigest.getInstance("SHA-1"); // Get a
																		// SHA-1
																		// instance
			int numRead = 0; // Record how many bytes have been read
			while (numRead != -1) {
				numRead = inputStream.read(buffer);
				if (numRead > 0)
					digest.update(buffer, 0, numRead); // Update the digest
			}
			byte[] sha1Bytes = digest.digest(); // Complete the hash computing
			return convertHashToString(sha1Bytes); // Call the function to
													// convert to hex digits
		} catch (Exception e) {
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close(); // Close the InputStream
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Convert the hash bytes to hex digits string
	 * 
	 * @param hashBytes
	 * @return The converted hex digits string
	 */
	private static String convertHashToString(byte[] hashBytes) {
		String returnVal = "";
		for (int i = 0; i < hashBytes.length; i++) {
			returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return returnVal.toLowerCase(Locale.ENGLISH);
	}

}
