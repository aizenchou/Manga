package com.aizen.manga.util;

import java.io.File;
import java.util.ArrayList;

public class FileLoad {
	
	public static int getDirCount(String dirPath) {
		int count = 0;
		File dir = new File(dirPath);
		if (dir.isDirectory()) {
			count = dir.list().length;
		}
		return count;
	}

	public static ArrayList<String> getDirFilenames(String dirPath) {
		ArrayList<String> filenameArrayList = new ArrayList<>();
		String[] filenames = new File(dirPath).list();
		for (String string : filenames) {
			filenameArrayList.add(string);
			System.out.println(string);
		}
		return filenameArrayList;
	}
	
	public static ArrayList<String> getDirFileURIs(String dirPath) {
		ArrayList<String> filenameArrayList = new ArrayList<>();
		String[] filenames = new File(dirPath).list();
		for (String string : filenames) {
			filenameArrayList.add(dirPath+"/"+string);
			System.out.println(dirPath+"/"+string);
		}
		return filenameArrayList;
	}
}
