package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 商家列表
 * @author Yaphets
 *
 */
public class Retailer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<QQInfo> qq = new ArrayList<QQInfo>();
	private String agency_id;
	private String name;
	private List<PhoneInfo> phoneNum = new ArrayList<PhoneInfo>();
	public List<QQInfo> getQq() {
		return qq;
	}
	public void setQq(List<QQInfo> qq) {
		this.qq = qq;
	}
	public String getAgency_id() {
		return agency_id;
	}
	public void setAgency_id(String agency_id) {
		this.agency_id = agency_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PhoneInfo> getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(List<PhoneInfo> phoneNum) {
		this.phoneNum = phoneNum;
	}
	@Override
	public String toString() {
		return "Retailer [qq=" + qq + ", agency_id=" + agency_id + ", name="
				+ name + ", phoneNum=" + phoneNum + "]";
	}
}
