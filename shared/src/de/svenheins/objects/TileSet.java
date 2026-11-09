/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
 



import javax.imageio.ImageIO;

import de.svenheins.main.GameStates;
 
public class TileSet
{
	int tileWidth;
	int tileHeight;
 
	String tileSetName;
	String tileSetFileName;
 
	ArrayList<BufferedImage> tileSet=new ArrayList<BufferedImage>();
 
	public TileSet(String fileName, String name, int width, int height)
	{
		this.tileSetName=name;
		this.tileSetFileName=fileName;
		this.tileWidth = width;
		this.tileHeight = height;
		try {
			BufferedImage picTileSet=ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+fileName));
			int limWidth=picTileSet.getWidth()/width;
			int limHeight=picTileSet.getHeight()/height;
			for(int x=0;x<limWidth;x++)
			{
				for(int y=0;y<limHeight;y++)
				{
					BufferedImage tileImage=picTileSet.getSubimage(x*width, y*height, width, height);
//					BufferedImage tileImage2 = toCompatibleImage(tileImage);
					this.tileSet.add(tileImage);
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(GameStates.resourcePath+"images/"+fileName);
		}
	}
	
	/** 2014.01.16 added this constructor for variable sprites */
	public TileSet(String fileName, String name, int[] x, int[] y, int[] width, int[] height)
	{
		this.tileSetName=name;
		this.tileSetFileName=fileName;
		try {
			BufferedImage picTileSet=ImageIO.read(this.getClass().getResource(GameStates.resourcePath+"images/"+fileName));
//			int countJ = 0;
			for(int i = 0; i < x.length; i++) {
//				for(int j = 0; j < yLimit[i]; j++) {
					BufferedImage tileImage=picTileSet.getSubimage(x[i], y[i], width[i], height[i]);
//					BufferedImage tileImage2 = toCompatibleImage(tileImage);
					this.tileSet.add(tileImage);
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(GameStates.resourcePath+"images/"+fileName);
		}
	}
 
 
	public BufferedImage getTileImage(int index)
	{
		return tileSet.get(index);
	}
	
	public ArrayList<BufferedImage> getTileSet() {
		return this.tileSet;
	}
	
	public String getName() {
		return this.tileSetName;
	}
	
	/** tileNames of single tiles start with tileName plus "_" plus the number */
	public String[] getTileNames(int begin, int end) {
		String[] names = new String[end-begin+1];
		for (int i = begin; i <=end ; i++) {
			names[i-begin] = this.getName()+"_"+i;
		}
		return names;
	}
	
	public String getFileName() {
		return this.tileSetFileName;
	}
	
	public int getSize() {
		return tileSet.size();
	}
 
	
	private BufferedImage toCompatibleImage(BufferedImage image)
	{
		// obtain the current system graphical settings
		GraphicsConfiguration gfx_config = GraphicsEnvironment.
			getLocalGraphicsEnvironment().getDefaultScreenDevice().
			getDefaultConfiguration();

		/*
		 * if image is already compatible and optimized for current system 
		 * settings, simply return it
		 */
		if (image.getColorModel().equals(gfx_config.getColorModel()))
			return image;

		// image is not optimized, so create a new image that is
		BufferedImage new_image = gfx_config.createCompatibleImage(
				image.getWidth(), image.getHeight(), image.getTransparency());

		// get the graphics context of the new image to draw the old image on
		Graphics2D g2d = (Graphics2D) new_image.getGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// return the new optimized image
		return new_image; 
	}
}