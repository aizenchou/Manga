package com.aizen.manga.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.aizen.manga.module.Chapter;
import com.aizen.manga.module.Manga;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.DecelerateInterpolator;

public class NetAnalyse {

	public static String cookieValue = "";
	
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	public static ArrayList<String> parseHtmlToPageURLs(String URL) {
		return null;
	}

	public static ArrayList<Manga> parseHtmlToList(String URL,
			String imageCacheDir) throws Exception {
		ArrayList<Manga> mangas = new ArrayList<>();
		Document doc = Jsoup.connect(URL).timeout(3000).get();
		Elements listElements = doc.select("ul#contList > li");
		for (Element li : listElements) {
			Manga manga = new Manga();
			Document lidoc = Jsoup.parseBodyFragment(li.html());
			String link = lidoc.select("a").first().attr("href");
			manga.setLink(link);
			manga.setId(link.substring(6, link.length() - 1));
			manga.setName(lidoc.select("a").first().attr("title"));
			manga.setCoverURL(lidoc.select("a.bcover > img").attr("data-src"));
			manga.setCover(ImageManager.getBitmapFromURL(manga.getCoverURL(),
					imageCacheDir));
			manga.setUpdateto(lidoc.select("a.bcover > span.tt").text());
			manga.setStatus(lidoc.select("a.bcover > span").last()
					.attr("class").equals("sl") ? true : false);
			manga.setUpdateDate(lidoc.select("span.updateon").text());
			manga.setMark(lidoc.select("span.updateon > em").text());
			/*
			 * System.out.println(manga.getLink() + manga.getId() +
			 * manga.getName() + manga.getCoverURL() + manga.getUpdateDate() +
			 * manga.getUpdateto() + manga.getMark());
			 */
			mangas.add(manga);
		}
		return mangas;

	}

	public static Manga parseHtmlToInfo(String URL, String imageCacheDir)
			throws Exception {
		//URL domain = new URL(URL); 
		//HttpURLConnection connection = (HttpURLConnection)domain.openConnection();
		//cookieValue = connection.getHeaderField("Set-Cookie");
		//System.out.println(cookieValue);
		Manga mangaInfo = new Manga();
		Document doc = Jsoup.connect(URL).get();
		Element cont = doc.select("div.book-cont").first();
		Document contDoc = Jsoup.parse(cont.html());
		mangaInfo.setCoverURL(contDoc.select("p.hcover > img").first()
				.attr("src"));
		mangaInfo.setCover(ImageManager.getBitmapFromURL(
				mangaInfo.getCoverURL(), imageCacheDir));
		mangaInfo.setName(contDoc.select("div.book-title > h1").first().text());
		mangaInfo.setAuthor(contDoc.select("a[href*=/search/]").first()
				.parent().text());
		mangaInfo.setPublishDate(contDoc.select("ul.detail-list a").first()
				.text());
		mangaInfo.setStatusIntro(contDoc.select("ul.detail-list > li.status")
				.first().text());
		mangaInfo
				.setDescription(contDoc.select("div#intro-all").first().text());
		return mangaInfo;
	}

	public static ArrayList<Chapter> parseHtmlToChapters(String URL)
			throws IOException {
		Document doc = Jsoup.connect(URL).get();
		Elements eles = doc.select("div.chapter-list");
		ArrayList<Chapter> chapters = new ArrayList<>();
		for (Element ele : eles) {
			Document subListDoc = Jsoup.parse(ele.html());
			ArrayList<Chapter> subChapters = new ArrayList<>();
			Elements uls = subListDoc.select("ul");
			for (Element ul : uls) {
				Elements links = Jsoup.parse(ul.html()).select("a.status0");
				ArrayList<Chapter> ulsChapters = new ArrayList<>();
				for (Element link : links) {
					Chapter chapter = new Chapter(link.text().substring(
							0,
							link.text().length()
									- Jsoup.parse(link.html()).select("i")
											.text().length()),
							link.attr("href"));
					ulsChapters.add(chapter);
				}
				Collections.reverse(ulsChapters);
				subChapters.addAll(ulsChapters);
			}
			Collections.reverse(subChapters);
			chapters.addAll(subChapters);
		}
		return chapters;
	}

	public static String catchData(String URL) throws Exception {
		URL url = new URL(URL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		InputStreamReader input = new InputStreamReader(
				httpConn.getInputStream(), "utf-8");
		BufferedReader bufReader = new BufferedReader(input);
		String line = "";
		StringBuilder contentBuf = new StringBuilder();
		while ((line = bufReader.readLine()) != null) {
			contentBuf.append(line);
		}
		return contentBuf.toString();
	}

	public static Vector<String> getMatchStrings(String regex,
			String dataresult, int groupnum) {
		Vector<String> matchStrings = new Vector<>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(dataresult);
		while (matcher.find())
			matchStrings.add(matcher.group(groupnum));
		return matchStrings;
	}

}
