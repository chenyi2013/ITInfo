package com.kevin.bean;

import java.io.Serializable;

/**
 * 商品信息
 * @author Yaphets
 *
 */
public class GoodsInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2686892162769816489L;
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
		return "GoodsInfo [id=" + id + ", title=" + title + "]";
	}
}
