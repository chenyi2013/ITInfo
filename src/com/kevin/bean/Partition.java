package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Partition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 309935346043373609L;

	private ArrayList<Brand> brands;
	private String index;
	private String counter;

	public ArrayList<Brand> getBrands() {
		return brands;
	}

	public void setBrands(ArrayList<Brand> brands) {
		this.brands = brands;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	@Override
	public String toString() {
		return "Partition [brands=" + brands + ", index=" + index
				+ ", counter=" + counter + "]";
	}

}
