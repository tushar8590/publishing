package com.sourcecode.fkloadproductupdatedata;
/***
 * The Exception class.
 * Please refer to the instructions.txt
 *
 * @author vijay.v@flipkart.com
 * @version 1.0
 * Copyright (c) Flipkart India Pvt. Ltd.
 */

import java.lang.Exception;

public class AffiliateAPIException extends Exception {
    AffiliateAPIException(String message) {
        super(message);
    }
}