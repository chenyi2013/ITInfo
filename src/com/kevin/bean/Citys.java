package com.kevin.bean;

import java.util.ArrayList;

public class Citys {

	private ArrayList<City> hot_city;
	private ArrayList<CityClassification> all_city;

	public ArrayList<City> getHot_city() {
		return hot_city;
	}

	public void setHot_city(ArrayList<City> hot_city) {
		this.hot_city = hot_city;
	}

	public ArrayList<CityClassification> getAll_city() {
		return all_city;
	}

	public void setAll_city(ArrayList<CityClassification> all_city) {
		this.all_city = all_city;
	}

	@Override
	public String toString() {
		return "Citys [hot_city=" + hot_city + ", all_city=" + all_city + "]";
	}

}
