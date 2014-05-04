package com.aizen.manga.module;

import android.graphics.drawable.Drawable;

public class Label {

	String labelText;
	Drawable labelIcon;

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public Drawable getLabelIcon() {
		return labelIcon;
	}

	public void setLabelIcon(Drawable labelIcon) {
		this.labelIcon = labelIcon;
	}

	public Label(String labelText, Drawable labelIcon) {
		super();
		this.labelText = labelText;
		this.labelIcon = labelIcon;
	}

}
