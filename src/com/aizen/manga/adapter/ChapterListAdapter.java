package com.aizen.manga.adapter;

import java.util.ArrayList;

import com.aizen.manga.R;
import com.aizen.manga.module.Chapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ChapterListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Chapter> chapterListItems;
	private LayoutInflater gridContainer;
	private int resource;

	public ChapterListAdapter(Context context,
			ArrayList<Chapter> chapterListItems, int resource) {
		this.context = context;
		this.chapterListItems = chapterListItems;
		this.gridContainer = LayoutInflater.from(context);
		this.resource = resource;
	}

	public final class GridItemView {
		public TextView chapterName;
		public CheckBox checkBox;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chapterListItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return chapterListItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GridItemView gridItemView = null;
		if (convertView == null) {
			gridItemView = new GridItemView();
			convertView = gridContainer.inflate(resource, null);

			gridItemView.chapterName = (TextView) convertView
					.findViewById(R.id.mangainfo_chapter_name);
			gridItemView.checkBox = (CheckBox) convertView
					.findViewById(R.id.mangainfo_chapter_select_check);

			convertView.setTag(gridItemView);
		} else {
			gridItemView = (GridItemView) convertView.getTag();
		}
		gridItemView.chapterName.setText(chapterListItems.get(position)
				.getTitle());
		return convertView;
	}

}
