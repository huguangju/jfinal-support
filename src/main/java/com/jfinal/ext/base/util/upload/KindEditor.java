package com.jfinal.ext.base.util.upload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Controller;
import com.jfinal.ext.base.util.Txt;
import com.jfinal.ext.base.util.Validate;
import com.jfinal.upload.UploadFile;

public class KindEditor {
	public static String[] FILE_EXT = { "gif", "jpg", "jpeg", "png", "bmp",
			"swf", "flv", "swf", "flv", "mp3", "wav", "wma", "wmv", "mid",
			"avi", "mpg", "asf", "rm", "rmvb", " doc", "docx", "xls", "xlsx",
			"ppt", "htm", "html", "txt", "zip", "rar", "gz", "bz2" };
	/***
	 * 指定上传地址
	 */
	private static final String UPLOAD_PATH = "/static/upload/";

	@SuppressWarnings("deprecation")
	public static Map<String, Object> upload(Controller c) {
		Map<String, Object> info = new HashMap<String, Object>();
		UploadFile file = null;
		try {
			String dir = c.getPara("dir");
			file = c.getFiles(c.getRequest().getRealPath(UPLOAD_PATH + dir))
					.get(0);

			if (file.getFile().exists() && !Validate.isEmpty(dir)) {

				String ext = Txt.getExt(file.getFileName());

				if (Arrays.asList(FILE_EXT).contains(ext)) {
					String newName = System.currentTimeMillis() + "." + ext;
					file.getFile().renameTo(
							new File(file.getSaveDirectory() + "/" + newName));
					info.put("url", UPLOAD_PATH + dir + "/" + newName);
					info.put("error", 0);
				} else {
					info.put("error", 1);
					info.put("message", "上传文件不符合标准");
					file.getFile().delete();
				}
			} else {
				info.put("message", "上传失败");
				info.put("error", 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			info.put("error", 1);
			info.put("message", "上传文件不符合标准");
			if (file != null)
				file.getFile().delete();
		}

		return info;
	}

	@SuppressWarnings({ "deprecation"})
	public static Map<String, Object> fileManage(HttpServletRequest req) {
		Map<String, Object> result = new HashMap<String, Object>();

		String rootPath = req.getRealPath(UPLOAD_PATH);
		String order = req.getParameter("order") != null ? req.getParameter(
				"order").toLowerCase() : "name";
		String rootUrl = UPLOAD_PATH;
		// 图片扩展名
		String[] fileTypes = new String[] { "gif", "jpg", "jpeg", "png", "bmp" };

		String dirName = req.getParameter("dir");
		if (dirName != null) {
			if (!Arrays.<String> asList(
					new String[] { "image", "flash", "media", "file", "root" })
					.contains(dirName)) {
				return result;
			}
			if (!"root".equals(dirName)) {
				rootPath += File.separator + dirName + File.separator;
				rootUrl += "/" + dirName + "/";
			}

			File saveDirFile = new File(rootPath);
			if (!saveDirFile.exists()) {
				saveDirFile.mkdirs();
			}
		}

		// 根据path参数，设置各路径和URL
		String path = req.getParameter("path") != null ? req
				.getParameter("path") : "";
		String currentPath = rootPath + path;
		String currentUrl = rootUrl + path;
		String currentDirPath = path;
		String moveupDirPath = "";
		if (!"".equals(path)) {
			String str = currentDirPath.substring(0,
					currentDirPath.length() - 1);
			moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0,
					str.lastIndexOf("/") + 1) : "";
		}

		// 不允许使用..移动到上一级目录
		if (path.indexOf("..") >= 0) {
			return result;
		}
		// 最后一个字符不是/
		if (!"".equals(path) && !path.endsWith("/")) {
			return result;
		}
		// 目录不存在或不是目录
		File currentPathFile = new File(currentPath);
		if (!currentPathFile.isDirectory()) {
			return result;
		}

		// 遍历目录取的文件信息
		List<Hashtable<String, Object>> fileList = new ArrayList<Hashtable<String, Object>>();
		if (currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				Hashtable<String, Object> hash = new Hashtable<String, Object>();
				String fileName = file.getName();
				if (file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else if (file.isFile()) {
					String fileExt = fileName.substring(
							fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", Arrays.<String> asList(fileTypes)
							.contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file
								.lastModified()));
				fileList.add(hash);
			}
		}

		if ("size".equals(order))
			Collections.sort(fileList, new SizeComparator());
		else if ("type".equals(order))
			Collections.sort(fileList, new TypeComparator());
		else
			Collections.sort(fileList, new NameComparator());

		result.put("moveup_dir_path", moveupDirPath);
		result.put("current_dir_path", currentDirPath);
		result.put("current_url", currentUrl);
		result.put("total_count", fileList.size());
		result.put("file_list", fileList);

		return result;
	}

}

class NameComparator implements Comparator<Hashtable<String, Object>> {
	@Override
	public int compare(Hashtable<String, Object> a, Hashtable<String, Object> b) {
		if (((Boolean) a.get("is_dir")) && !((Boolean) b.get("is_dir"))) {
			return -1;
		} else if (!((Boolean) a.get("is_dir")) && ((Boolean) b.get("is_dir"))) {
			return 1;
		} else {
			return ((String) a.get("filename")).compareTo((String) b
					.get("filename"));
		}
	}
}

class SizeComparator implements Comparator<Hashtable<String, Object>> {
	public int compare(Hashtable<String, Object> a, Hashtable<String, Object> b) {
		if (((Boolean) a.get("is_dir")) && !((Boolean) b.get("is_dir"))) {
			return -1;
		} else if (!((Boolean) a.get("is_dir"))
				&& ((Boolean) b.get("is_dir"))) {
			return 1;
		} else {
			if (((Long) a.get("filesize")) > ((Long) b.get("filesize"))) {
				return 1;
			} else if (((Long) a.get("filesize")) < ((Long) b
					.get("filesize"))) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}

class TypeComparator implements Comparator<Hashtable<String, Object>> {
	public int compare(Hashtable<String, Object> a, Hashtable<String, Object> b) {
		if (((Boolean) a.get("is_dir")) && !((Boolean) b.get("is_dir"))) {
			return -1;
		} else if (!((Boolean) a.get("is_dir"))
				&& ((Boolean) b.get("is_dir"))) {
			return 1;
		} else {
			return ((String) a.get("filetype")).compareTo((String) b
					.get("filetype"));
		}
	}
}
