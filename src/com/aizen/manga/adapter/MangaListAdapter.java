package com.aizen.manga.adapter;

import java.util.ArrayList;
import com.aizen.manga.R;
import com.aizen.manga.module.Manga;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MangaListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Manga> mangaListItems;
	private LayoutInflater listContainer;
	private int resource;

	public final class ListItemView {
		public ImageView mangaCover;
		public TextView mangaName, mangaUpdateTo, mangaMark, mangaIsLike, mangaUpdateDate, mangaStatus;
	}

	
	public MangaListAdapter(Context context, int resource,ArrayList<Manga> mangaListItems) {
		this.listContainer = LayoutInflater.from(context);
		this.context = context;
		this.resource = resource;
		this.mangaListItems = mangaListItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mangaListItems.size();
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
			
			listItemView.mangaCover = (ImageView)convertView.findViewById(R.id.mangaCover);
			listItemView.mangaName = (TextView)convertView.findViewById(R.id.mangaName);
			listItemView.mangaUpdateTo = (TextView)convertView.findViewById(R.id.mangaUpdateTo);
			listItemView.mangaMark = (TextView)convertView.findViewById(R.id.mangaMark);
			listItemView.mangaUpdateDate = (TextView)convertView.findViewById(R.id.mangaUpdateDate);
			listItemView.mangaStatus = (TextView)convertView.findViewById(R.id.mangaStatus);
			listItemView.mangaIsLike = (TextView)convertView.findViewById(R.id.mangaIsLike);
			
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView)convertView.getTag();
		}
		listItemView.mangaCover.setImageBitmap(mangaListItems.get(position).getCover());
		listItemView.mangaName.setText(mangaListItems.get(position).getName());
		listItemView.mangaUpdateTo.setText(mangaListItems.get(position).getUpdateto());
		listItemView.mangaMark.setText(mangaListItems.get(position).getMark());
		listItemView.mangaUpdateDate.setText(mangaListItems.get(position).getUpdateDate());
		listItemView.mangaStatus.setText(mangaListItems.get(position).isStatus()?"正在连载":"已完结");
		listItemView.mangaIsLike.setText("喜欢");
		
		return convertView;
	}

}
