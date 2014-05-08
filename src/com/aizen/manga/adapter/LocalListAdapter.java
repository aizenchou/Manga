package com.aizen.manga.adapter;

import java.util.ArrayList;

import com.aizen.manga.R;
import com.aizen.manga.fragment.LocalMangaFrag;
import com.aizen.manga.module.Manga;
import com.aizen.manga.util.FileLoad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LocalListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Manga> localListItems;
	private LayoutInflater listContainer;
	private int resource;
	private String loadDir;
	
	public final class ListItemView {
		public TextView localName, localAuthor, localDownloadCount;
	}
	
	public LocalListAdapter(Context context, int resource, ArrayList<Manga> localListItems, String loadDir) {
		this.listContainer = LayoutInflater.from(context);
		this.context = context;
		this.resource = resource;
		this.localListItems = localListItems;
		this.loadDir = loadDir;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return localListItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = null;
		if (convertView==null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(resource, null);
			
			listItemView.localName = (TextView)convertView.findViewById(R.id.LocalName);
			listItemView.localAuthor = (TextView)convertView.findViewById(R.id.LocalAuthor);
			listItemView.localDownloadCount = (TextView)convertView.findViewById(R.id.LocalDownloadCount);
			
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView)convertView.getTag();
		}

		listItemView.localAuthor.setText(localListItems.get(position).getAuthor());
		listItemView.localName.setText(localListItems.get(position).getName());
		listItemView.localDownloadCount.setText("已下载"+FileLoad.getDirCount( loadDir + "/" + localListItems.get(position).getId() ) + "话");
		
		return convertView;
	}

}
