package com.kevin.bean;

import java.io.Serializable;

public class PhoneInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9008152709419410230L;
	private String phoneNum;

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	@Override
	public String toString() {
		return "PhoneInfo [phoneNum=" + phoneNum + "]";
	}
}
