package com.jfinal.ext.base.util.apk;

import com.jfinal.ext.base.util.apk.sinaapk.entity.ApkInfo;
import com.jfinal.ext.base.util.apk.sinaapk.utils.ApkUtil;


public class ApkUtils
{
	
	public static String AAPT_PATH = "/root/android-sdk-linux/build-tools/19.0.1/aapt";

	public static void setAaptPath(String path)
	{
		AAPT_PATH = path;
	}

	public static ApkInfo getApkInfo(String apkPath)
	{
		ApkInfo apkInfo = null;
		try
		{
			ApkUtil apk = new ApkUtil();
			if (AAPT_PATH != null) apk.setmAaptPath(AAPT_PATH);
			apkInfo = apk.getApkInfo(apkPath);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return apkInfo;
	}

	/***
	 * 
	 * @param apkPath
	 * @param iconPath
	 *            导出的图标 地址
	 * @return
	 */
	public static ApkInfo getApkInfo(String apkPath, String iconPath)
	{
		ApkInfo apkInfo = getApkInfo(apkPath);
		if (apkInfo != null)
		{
			try
			{
				IconUtil.extractFileFromApk(apkPath, apkInfo.getApplicationIcon(), iconPath);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return apkInfo;
	}

}
