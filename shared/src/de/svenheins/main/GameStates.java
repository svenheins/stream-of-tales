package de.svenheins.main;

public class GameStates {
	public static String resourcePath = "/resources/";
	public static String svgPath = "/resources/svg/";
	public static String externalImagesPath = "./ressources/images/";
	public static int width = 800;
	public static int height = 600;
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
	public static void setHeight(int height) {
		GameStates.height = height;
	}
	
	public static void setWidth(int width) {
		GameStates.width = width;
	}
	
	
}
