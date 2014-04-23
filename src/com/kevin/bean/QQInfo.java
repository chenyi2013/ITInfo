package com.kevin.bean;

import java.io.Serializable;

public class QQInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9008275282519208278L;
	private String qq;

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	@Override
	public String toString() {
		return "PhoneInfo [qq=" + qq + "]";
	}
}
