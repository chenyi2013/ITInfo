package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 商家详细信息
 * @author Yaphets
 *
 */
public class RetailerInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2521384517798026223L;
	private String id;
	private String title;
	private String area;
	private String address;
	private String description;
	private List<String> coordinate = new ArrayList<String>();
	private List<String> tel = new ArrayList<String>();
	private List<String> qq = new ArrayList<String>();
	private List<String> img = new ArrayList<String>();
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(List<String> coordinate) {
		this.coordinate = coordinate;
	}
	public List<String> getTel() {
		return tel;
	}
	public void setTel(List<String> tel) {
		this.tel = tel;
	}
	public List<String> getQq() {
		return qq;
	}
	public void setQq(List<String> qq) {
		this.qq = qq;
	}
	public List<String> getImg() {
		return img;
	}
	public void setImg(List<String> img) {
		this.img = img;
	}
	@Override
	public String toString() {
		return "RetailerInfo [id=" + id + ", title=" + title + ", area=" + area
				+ ", address=" + address + ", description=" + description
				+ ", coordinate=" + coordinate + ", tel=" + tel + ", qq=" + qq
				+ ", img=" + img + "]";
	}
}
