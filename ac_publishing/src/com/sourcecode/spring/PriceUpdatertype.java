package com.sourcecode.spring;

public enum PriceUpdatertype {
	
	 DAILY("Daily"),WEEKLY("Weekly");
	
	private final String text;

    /**
     * @param text
     */
    private PriceUpdatertype(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
