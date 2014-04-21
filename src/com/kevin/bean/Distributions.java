package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 厂商列表
 * @author Yaphets
 *
 */
public class Distributions implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5024988146795259436L;
	private List<Distribution> deDistributions = new ArrayList<Distribution>();

	public List<Distribution> getDeDistributions() {
		return deDistributions;
	}

	public void setDeDistributions(List<Distribution> deDistributions) {
		this.deDistributions = deDistributions;
	}

	@Override
	public String toString() {
		return "Distributions [deDistributions=" + deDistributions + "]";
	}
}
