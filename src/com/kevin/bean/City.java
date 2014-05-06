package com.kevin.bean;

public class City {
	private String id;
	private String name;
	private boolean isTitle = false;
	private String indexer;

	public String getIndexer() {
		return indexer;
	}

	public void setIndexer(String indexer) {
		this.indexer = indexer;
	}

	public boolean isTitle() {
		return isTitle;
	}

	public void setTitle(boolean isTitle) {
		this.isTitle = isTitle;
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

	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + "]";
	}

}
