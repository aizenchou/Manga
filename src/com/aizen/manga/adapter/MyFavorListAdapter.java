package com.aizen.manga.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aizen.manga.R;
import com.aizen.manga.adapter.MangaListAdapter.ListItemView;
import com.aizen.manga.module.Manga;
import com.aizen.manga.util.ImageManager;

public class MyFavorListAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<Manga> myFavorListItems;
	private LayoutInflater listContainer;
	private int resource;
	private String cacheDir;

	public final class ListItemView {
		public ImageView myFavorCover;
		public TextView myFavorName, myFavorStatus, lastRead;
	}

	
	public MyFavorListAdapter(Context context, int resource, ArrayList<Manga> myFavorListItems, String cacheDir) {
		this.listContainer = LayoutInflater.from(context);
		this.context = context;
		this.resource = resource;
		this.myFavorListItems = myFavorListItems;
		this.cacheDir = cacheDir;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myFavorListItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = null;
		if (convertView==null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(resource, null);
			
			listItemView.myFavorCover = (ImageView)convertView.findViewById(R.id.myFavorCover);
			listItemView.myFavorName = (TextView)convertView.findViewById(R.id.myFavorName);
			listItemView.myFavorStatus = (TextView)convertView.findViewById(R.id.myFavorStatus);
			listItemView.lastRead = (TextView)convertView.findViewById(R.id.lastRead);
		//	listItemView.mangaIsLike = (TextView)convertView.findViewById(R.id.mangaIsLike);
			
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView)convertView.getTag();
		}
		try {
			listItemView.myFavorCover.setImageBitmap(ImageManager.getBitmapFromURL(myFavorListItems.get(position).getCoverURL(), cacheDir));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(myFavorListItems.get(position).getName());
		listItemView.myFavorName.setText(myFavorListItems.get(position).getName());
		listItemView.myFavorStatus.setText(myFavorListItems.get(position).getStatusIntro());
		String lastReadText = myFavorListItems.get(position).getLastRead();
		if (lastReadText == null) {
			listItemView.lastRead.setText("漫画进度：还没开始看");
		}else {
			listItemView.lastRead.setText("上次看到："+myFavorListItems.get(position).getLastRead());
		}
		
		//listItemView.mangaIsLike.setText("喜欢");
		
		return convertView;
	}
}
