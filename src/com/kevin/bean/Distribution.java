package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 厂商实体�?
 * @author Yaphets
 */
public class Distribution implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -958515937583753802L;
	private String title;
	private String cover;
	private String focus;
	private String content;
	private String address;
	private List<String> coordinate = new ArrayList<String>();
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getFocus() {
		return focus;
	}
	public void setFocus(String focus) {
		this.focus = focus;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<String> getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(List<String> coordinate) {
		this.coordinate = coordinate;
	}
	@Override
	public String toString() {
		return "Distribution [title=" + title + ", cover=" + cover + ", focus="
				+ focus + ", content=" + content + ", address=" + address
				+ ", coordinate=" + coordinate + "]";
	}
}
