package com.aizen.manga.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageManager {

	private static Bitmap getBitmapFromFile(String url, String cacheDir) {
		Bitmap bitmap = null;
		String fileName = NetAnalyse.getMD5Str(url);
		if (fileName == null)
			return null;
		String filePath = cacheDir + "/" + fileName;
		try {
			FileInputStream fis = new FileInputStream(filePath);
			bitmap = BitmapFactory.decodeStream(fis);
			Log.d("imagecache", "读取"+url+"文件成功");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("imagecache", "读取"+url+"文件失败");
			bitmap = null;
		}
		return bitmap;
	}

	private static void setBitmapToFile(String url, String cacheDir,
			Bitmap bitmap) throws FileNotFoundException {
		String fileName = NetAnalyse.getMD5Str(url);
		String filePath = cacheDir + "/" + fileName;
		FileOutputStream fos = new FileOutputStream(filePath);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		Log.d("imagecache", "存入文件"+url);
	}

	public static Bitmap getBitmapFromURL(String url, String cacheDir)
			throws Exception {
		Bitmap bitmap = getBitmapFromFile(url, cacheDir);
		if (bitmap == null) {
			Log.d("imagecache", "缓存中不存在"+url);
			byte[] data = getImage(url);
			int length = data.length;
			bitmap = BitmapFactory.decodeByteArray(data, 0, length);
			setBitmapToFile(url, cacheDir, bitmap);
		}
		return bitmap;

	}

	public static byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection httpURLconnection = (HttpURLConnection) url
				.openConnection();
		httpURLconnection.setRequestMethod("GET");
		httpURLconnection.setReadTimeout(6 * 1000);
		InputStream in = null;
		byte[] b = new byte[1024];
		int len = -1;
		if (httpURLconnection.getResponseCode() == 200) {
			in = httpURLconnection.getInputStream();
			byte[] result = readStream(in);
			in.close();
			return result;

		}
		return null;
	}

	public static byte[] readStream(InputStream in) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		in.close();
		return outputStream.toByteArray();
	}
}
