package com.kevin.bean;

public class News {
	private String id;
	private String cover;
	private String title;
	private String description;
	private String upfile;
	private String date;

	public News(String id, String cover, String title, String description,
			String upfile, String date) {
		super();
		this.id = id;
		this.cover = cover;
		this.title = title;
		this.description = description;
		this.upfile = upfile;
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpfile() {
		return upfile;
	}

	public void setUpfile(String upfile) {
		this.upfile = upfile;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", cover=" + cover + ", title=" + title
				+ ", description=" + description + ", upfile=" + upfile
				+ ", date=" + date + "]";
	}

}
