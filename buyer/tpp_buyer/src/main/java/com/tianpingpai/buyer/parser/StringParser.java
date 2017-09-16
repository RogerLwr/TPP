package com.tianpingpai.buyer.parser;

import com.tianpingpai.http.HttpRequest.Parser;

public class StringParser implements Parser<String, String> {

	@Override
	public String parse(String i) {
		return i;
	}
}
