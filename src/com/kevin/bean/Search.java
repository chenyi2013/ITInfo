package com.kevin.bean;

import java.io.Serializable;

public class Search implements Serializable{
       /**
	 * 
	 */
	private static final long serialVersionUID = -6556534217073100643L;
	private int id;
       private String name;
       private String counter;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCounter() {
		return counter;
	}
	public void setCounter(String counter) {
		this.counter = counter;
	}
       
}
