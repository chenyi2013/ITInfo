package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 经销�?
 * 
 * @author Yaphets
 * 
 */
public class Dealer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2849257979055527880L;
	private String id;
	private String image;
	private String name;
	private List<GoodsInfo> goodInfos = new ArrayList<GoodsInfo>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GoodsInfo> getGoodInfos() {
		return goodInfos;
	}

	public void setGoodInfos(List<GoodsInfo> goodInfos) {
		this.goodInfos = goodInfos;
	}

	@Override
	public String toString() {
		return "Dealer [id=" + id + ", image=" + image + ", name=" + name
				+ ", goodInfos=" + goodInfos + "]";
	}
}
