package de.svenheins.test;

import de.svenheins.functions.MyStrings;

public class TestStringMapFileToPoint {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String testString = "abc_112012_212309123.map";
		System.out.println(MyStrings.getPointFromMapFile(testString).x + " | "+MyStrings.getPointFromMapFile(testString).y);
	}

}
