package com.aizen.manga.adapter;

import java.util.ArrayList;

import com.aizen.manga.R;
import com.aizen.manga.module.Manga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SearchResultListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Manga> resultListItems;
	private LayoutInflater listContainer;
	private int resource;
	private ListItemView listItemView;

	public final class ListItemView {
		public ImageView resultCover;
		public TextView resultName, resultStatus, resultType, resultDesc, resultAuthor;
		public RatingBar resultRating;
		public ImageButton resultShowDesc;
	}

	public SearchResultListAdapter(Context context, int resource,
			ArrayList<Manga> resultListItems) {
		this.listContainer = LayoutInflater.from(context);
		this.context = context;
		this.resource = resource;
		this.resultListItems = resultListItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return resultListItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return resultListItems.get(position);
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
			
			listItemView.resultCover = (ImageView)convertView.findViewById(R.id.ResultCover);
			listItemView.resultName = (TextView)convertView.findViewById(R.id.ResultName);
			listItemView.resultAuthor = (TextView)convertView.findViewById(R.id.ResultAuthor);
			listItemView.resultStatus = (TextView)convertView.findViewById(R.id.ResultStatus);
			listItemView.resultType = (TextView)convertView.findViewById(R.id.ResultType);
			listItemView.resultDesc = (TextView)convertView.findViewById(R.id.ResultDesc);
			listItemView.resultRating = (RatingBar)convertView.findViewById(R.id.ResultRating);
			listItemView.resultShowDesc = (ImageButton)convertView.findViewById(R.id.ResultShowDesc);
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		listItemView.resultCover.setImageBitmap(resultListItems.get(position).getCover());
		listItemView.resultName.setText(resultListItems.get(position).getName());
		listItemView.resultAuthor.setText(resultListItems.get(position).getAuthor());
		listItemView.resultStatus.setText(resultListItems.get(position).getStatusIntro());
		listItemView.resultRating.setRating(Float.parseFloat(resultListItems.get(position).getMark())/2);
		listItemView.resultType.setText(resultListItems.get(position).getTag());
		listItemView.resultDesc.setText(resultListItems.get(position).getDescription());
		listItemView.resultDesc.setVisibility(View.GONE);
		
		OnClickListener imageButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId()==listItemView.resultShowDesc.getId()) {
					if (listItemView.resultDesc.isShown()) {
						listItemView.resultDesc.setVisibility(View.GONE);
					}else {
						listItemView.resultDesc.setVisibility(View.VISIBLE);
					}
					
				}
			}
		};
		listItemView.resultShowDesc.setOnClickListener(imageButtonListener);

		return convertView;
	}
	
	
		
	
}
