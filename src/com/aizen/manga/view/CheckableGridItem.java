package com.aizen.manga.view;

import com.aizen.manga.R;

import android.R.bool;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CheckableGridItem extends RelativeLayout implements Checkable {

	private Context context;
	private boolean checked;
	private TextView chapterTitle;
	private CheckBox checkBox;

	public CheckableGridItem(Context context) {
		this(context, null, 0);
	}

	public CheckableGridItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CheckableGridItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.gridview_mangachapter,
				this);
		chapterTitle = (TextView) findViewById(R.id.mangainfo_chapter_name);
		checkBox = (CheckBox) findViewById(R.id.mangainfo_chapter_select_check);
	}

	@Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub
		this.checked = checked;
		checkBox.setChecked(checked);
		checkBox.setVisibility(checked ? View.VISIBLE : View.GONE);
	}

	public void setChapterTitle(String title) {
		chapterTitle.setText(title);
	}
	
	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return checked;
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		setChecked(!checked);
	}

}
