package com.aizen.manga;

import java.util.ArrayList;
import java.util.HashMap;

import com.aizen.manga.util.FileLoad;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.os.Build;

public class LocalMangaInfoActivity extends Activity {

	GridView localChapterGridView;
	ArrayList<String> localChapters = new ArrayList<>();
	ArrayList<HashMap<String, Object>> chaptersHashMaps = new ArrayList<>();
	String localMangaURI = "";
	public final static String LOCAL_MANGA_KEY = "local_manga";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_manga_info);
		localMangaURI = getIntent().getExtras().getString(LOCAL_MANGA_KEY);
		System.out.println(localMangaURI);
		localChapters = FileLoad.getDirFilenames(localMangaURI);
		for (String dirName : localChapters) {
			HashMap<String, Object> hashMap = new HashMap<>();
			hashMap.put("chaptername", dirName);
			chaptersHashMaps.add(hashMap);
		}
		localChapterGridView = (GridView)findViewById(R.id.LocalChapterGrid);
		SimpleAdapter localChapterAdapter = new SimpleAdapter(this, chaptersHashMaps, R.layout.gridview_localchapter, new String[] {"chaptername"}, new int[]{R.id.localmangainfo_chapter_name});
		localChapterGridView.setAdapter(localChapterAdapter);
		localChapterGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getApplicationContext(),LocalMangaActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(LocalMangaActivity.LOCAL_CHAPTER_KEY, localMangaURI+"/"+localChapters.get(position));
				it.putExtras(bundle);
				startActivity(it);
			}
		});
		
	}

}
