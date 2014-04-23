package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Retailers implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 348551988871997314L;
	private List<Retailer> retailersList = new ArrayList<Retailer>();

	public List<Retailer> getRetailersList() {
		return retailersList;
	}

	public void setRetailersList(List<Retailer> retailersList) {
		this.retailersList = retailersList;
	}

	@Override
	public String toString() {
		return "Retailers [retailersList=" + retailersList + "]";
	}
}
