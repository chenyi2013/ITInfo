package com.kevin.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 经销商列�?
 * @author Yaphets
 *
 */
public class Dealers implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4255745788898785657L;
	private List<Dealer> dealers = new ArrayList<Dealer>();

	public List<Dealer> getDealers() {
		return dealers;
	}

	public void setDealers(List<Dealer> dealers) {
		this.dealers = dealers;
	}

	@Override
	public String toString() {
		return "Dealers [dealers=" + dealers + "]";
	}
}
