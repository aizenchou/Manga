package com.aizen.manga.adapter;

import java.util.ArrayList;
import com.aizen.manga.R;
import com.aizen.manga.module.Label;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Label> Labels = new ArrayList<Label>();
	private LayoutInflater listContainer;
	private int resource;
	private ListItemView listItemView;

	public final class ListItemView {
		public TextView labelText;
		public ImageView labelIcon;
	}

	public NavigationDrawerListAdapter(Context context, int resource,
			ArrayList<Label> Labels) {
		this.listContainer = LayoutInflater.from(context);
		this.context = context;
		this.resource = resource;
		this.Labels = Labels;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Labels.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Labels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(resource, null);
			
			listItemView.labelIcon = (ImageView) convertView.findViewById(R.id.LabelIcon);
			listItemView.labelText = (TextView) convertView.findViewById(R.id.LabelText);
			
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		
		listItemView.labelIcon.setImageDrawable(Labels.get(position).getLabelIcon());
		listItemView.labelText.setText(Labels.get(position).getLabelText());
		
		return convertView;
	}

}
