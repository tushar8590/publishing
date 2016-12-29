package com.sourcecode.spring.model;

import com.sourcecode.spring.PriceUpdatertype;

public class ElectronicsPriceUpdater {
	private String updaterName;
	private PriceUpdatertype updaterType;
	public String getUpdaterName() {
		return updaterName;
	}
	public void setUpdaterName(String updaterName) {
		this.updaterName = updaterName;
	}
	public PriceUpdatertype getUpdaterType() {
		return updaterType;
	}
	public void setUpdaterType(PriceUpdatertype updaterType) {
		this.updaterType = updaterType;
	}
}
