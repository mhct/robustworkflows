package com.tests;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestNumberConfig {

	public static void main(String[] args) {
		final Config config = ConfigFactory.load().getConfig("test");
		int myVar = config.getInt("myVar"); 
	}
}
