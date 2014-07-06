package com.jfinal.ext.base;

import com.alibaba.druid.filter.config.ConfigTools;

public class DruidUtil
{

	public static String decrypt(String pwd, String decrypt)
	{
		try
		{
			if (Boolean.valueOf(decrypt)) pwd = ConfigTools.decrypt(pwd);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return pwd;
	}

}
