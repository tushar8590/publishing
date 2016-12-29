package com.sourcecode.standalone;

import com.sourcecode.spring.FunctionConstants;

public class PriceUpdaterFactory {
	
	public static PriceUpdater getPriceUpdater(String priceUpdaterName){
		if(priceUpdaterName.equalsIgnoreCase(FunctionConstants.priceUpdaterFlipkart))
			return new FlipkartProductPriceUpdater();
		else if(priceUpdaterName.equalsIgnoreCase(FunctionConstants.priceUpdaterAmazon))
			return new AmazonProductPriceUpdater();
		else if(priceUpdaterName.equalsIgnoreCase(FunctionConstants.priceUpdaterSnapdeal))
			return new SnapdealProductPriceUpdater();
		else 
			return null;
		
		
	}
}
