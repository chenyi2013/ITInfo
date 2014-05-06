package com.kevin.bean;

import java.util.ArrayList;

public class CityClassification {

	private ArrayList<City> list;
	private String firstchar;

	public ArrayList<City> getList() {
		return list;
	}

	public void setList(ArrayList<City> list) {
		this.list = list;
	}

	public String getFirstchar() {
		return firstchar;
	}

	public void setFirstchar(String firstchar) {
		this.firstchar = firstchar;
	}

	@Override
	public String toString() {
		return "CityClassification [list=" + list + ", firstchar=" + firstchar
				+ "]";
	}

}
