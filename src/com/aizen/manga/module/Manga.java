package com.aizen.manga.module;

import android.graphics.Bitmap;

public class Manga {

	private String id;
	private String name;
	private String author;
	private String publishDate;
	private String updateDate;
	private String lcoation;
	private String tag;
	private String otherName;
	private boolean status; //true��ʾ�������أ�false��ʾ�Ѿ����
	private String description;
	private String mark;
	private String link;
	private String coverURL;
	private Bitmap cover;
	private String updateto;

	public Manga(String id, String name, String author, String publishDate,
			String updateDate, String lcoation, String tag, String otherName,
			boolean status, String description, String mark, String link,
			String coverURL, Bitmap cover, String updateto) {
		super();
		this.id = id;
		this.name = name;
		this.author = author;
		this.publishDate = publishDate;
		this.updateDate = updateDate;
		this.lcoation = lcoation;
		this.tag = tag;
		this.otherName = otherName;
		this.status = status;
		this.description = description;
		this.mark = mark;
		this.link = link;
		this.coverURL = coverURL;
		this.cover = cover;
		this.updateto = updateto;
	}

	public Manga(String id, String name, String author, String link) {
		super();
		this.id = id;
		this.name = name;
		this.author = author;
		this.link = link;
	}

	public Manga(String id, String name, String author, String mark,
			String coverURL) {
		super();
		this.id = id;
		this.name = name;
		this.author = author;
		this.mark = mark;
		this.coverURL = coverURL;
	}

	
	public Manga(String id, String name, String updateDate, boolean status,
			String mark, String link, String coverURL, String updateto) {
		super();
		this.id = id;
		this.name = name;
		this.updateDate = updateDate;
		this.status = status;
		this.mark = mark;
		this.link = link;
		this.coverURL = coverURL;
		this.updateto = updateto;
	}

	public Manga() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getLcoation() {
		return lcoation;
	}

	public void setLcoation(String lcoation) {
		this.lcoation = lcoation;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCoverURL() {
		return coverURL;
	}

	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}

	public Bitmap getCover() {
		return cover;
	}

	public void setCover(Bitmap cover) {
		this.cover = cover;
	}

	public String getUpdateto() {
		return updateto;
	}

	public void setUpdateto(String updateto) {
		this.updateto = updateto;
	}

}
