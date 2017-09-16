package com.tianpingpai.model;

import android.content.pm.PackageManager.NameNotFoundException;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.core.ContextProvider;

public class VersionModel {

	public static final int UPDATE_TYPE_UMENG = 1;
	public static final int UPDATE_TYPE_SELF = 2;

	@SerializedName("update")
	private int update = -1;
	@SerializedName("url")
	private String url;
	@SerializedName("sha1")
	private String sha1;
	@SerializedName("update_type")
	private int updateType;
	@SerializedName("version")
	private String version;
	@SerializedName("force")
	private int force;
	
	public boolean forceUpdate(){
		return force == 1;
	}

	public boolean shouldUpdate() {
//		if (update == 1) {
//			return true;
//		}
		try {
			String versionName = ContextProvider
					.getContext()
					.getPackageManager()
					.getPackageInfo(
							ContextProvider.getContext().getPackageName(), 0).versionName;
			return versionName.compareToIgnoreCase(version) < 0;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getUpdateType() {
		return updateType;
	}

	public void setUpdateType(int updateType) {
		this.updateType = updateType;
	}

	public int getUpdate() {
		return update;
	}

	public void setUpdate(int update) {
		this.update = update;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}
}
