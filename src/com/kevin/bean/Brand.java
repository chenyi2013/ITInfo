package com.kevin.bean;

import java.io.Serializable;

public class Brand implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1941315391696115044L;

	private String id;
	private String title;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Brand [id=" + id + ", title=" + title + "]";
	}

}
