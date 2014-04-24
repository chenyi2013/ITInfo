package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Normal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3184766604773693240L;

	private ArrayList<Partition> partition;
	private String counter;

	public ArrayList<Partition> getPartition() {
		return partition;
	}

	public void setPartition(ArrayList<Partition> partition) {
		this.partition = partition;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	@Override
	public String toString() {
		return "Normal [partition=" + partition + ", counter=" + counter + "]";
	}

}
