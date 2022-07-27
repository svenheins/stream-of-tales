package de.svenheins.objects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.svenheins.main.GameStates;
import de.svenheins.managers.TextureManager;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.svenheins.functions.MyMath;

import de.svenheins.animation.SpaceAnimation;


/**
 * @author Sven Heins
 * 
 * Space is defined by an ArrayList of polygons, that consist of minimum three edges
 * a svg-file can be used to load a space
 *
 */
public class Space extends LocalObject{
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	
	
	private int[] pubXCoord;
	private int[] pubYCoord;
	
	protected ArrayList<Polygon> polygon;
//	protected Color color;
	protected int[] rgb;
	protected BufferedImage bufferedImage; 
	protected TexturePaint texturePaint;
	protected float trans;
	protected Boolean filled;
	protected float scale;
	protected float area;
//	protected float mx, my;
//	protected float height, width;
	protected int polyX, polyY;
	
	protected SpaceAnimation spaceAnimation;
	
	
	
	/**
	 * constructor: sets the standard polygon
	 */
	public Space() {
//		setColor(Color.BLACK);
		setRGB(new int[]{0,0,0});
		setFilled(true);
		setTrans(1.0f);
		setPolygon( new ArrayList<Polygon>());
		//(getClass().getResource(GamePanel.resourcePath+"images/"+src))
//		setPolygon(GamePanel.svgPath+"Zeichnung.svg");	
		setPolygon((getClass().getResource(GameStates.resourcePath+"svg/"+"Zeichnung.svg")));
		
		spaceAnimation = new SpaceAnimation(this);
		this.texturePaint = null;
		this.bufferedImage = null;
	}
	
	public Space( String str, BigInteger id,  int[] rgb, Boolean filled, float trans, float scale) {
//		setColor(color);
		setFilled(filled);
		setTrans(trans);
		setRGB(rgb);
		setPolygon( new ArrayList<Polygon>());
//		addPolygon(GamePanel.svgPath+str);
		addPolygon((getClass().getResource(GameStates.resourcePath+"svg/"+str)));
		this.setId(id);
		this.setName(str);
		this.scale = scale;
		this.scale(scale);
		this.width = this.width*scale;
		this.height = this.height *scale;
		spaceAnimation = new SpaceAnimation(this);
		this.texturePaint = null;
		this.bufferedImage = null;
	}
	
	public Space(ArrayList<Polygon> polygon, int polyX, int polyY, String str, BigInteger id,  int[] rgb, Boolean filled, float trans, float scale) {
//		setColor(color);
		setFilled(filled);
		setTrans(trans);
		setRGB(rgb);
		setPolygon( polygon);
		setAllXY(polyX, polyY);
//		addPolygon(GamePanel.svgPath+str);
		//addPolygon((getClass().getResource(GameStates.resourcePath+"svg/"+str)));
		this.setId(id);
		this.setName(str);
		this.scale = scale;
		this.scale(scale);
		this.width = this.width*scale;
		this.height = this.height *scale;
		spaceAnimation = new SpaceAnimation(this);
		this.texturePaint = null;
		this.bufferedImage = null;
	}
	
	public Space( String str, BigInteger id, String textureString, float trans) {
		setPolygon( new ArrayList<Polygon>());
//		addPolygon(GamePanel.svgPath+str);
		addPolygon((getClass().getResource(GameStates.resourcePath+"svg/"+str)));
		this.setId(id);
		spaceAnimation = new SpaceAnimation(this);
		loadTexture(textureString);
//		setColor(null);
		setRGB(new int[]{0,0,0});
		setFilled(true);
		setTrans(trans);
	}
	
	public void loadTexture(String textureString) {
		bufferedImage = TextureManager.manager.getTexture(textureString);
		setTexturePaint(new TexturePaint(bufferedImage, new Rectangle(this.getPolyX(),this.getPolyY(), this.bufferedImage.getWidth(), this.bufferedImage.getHeight())));
	}
	
	public void setTexturePaint(TexturePaint tp) {
		this.texturePaint = tp;
	}
	
	public void updateSpace()	{
		float timeNow = System.currentTimeMillis();
		spaceAnimation.update(timeNow);

	}

	public ArrayList<Polygon> getPolygon() {
		return polygon;
	}

	public void setPolygon(ArrayList<Polygon> polygon) {
		this.polygon = polygon;
	}
	
	public void setPolygon(URL src) {
		setPolygon( new ArrayList<Polygon>());
		addPolygon(src);
	}
	
	public void addPolygon(URL src) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				Document document;
				try {
					document = builder.parse(src.openStream());					
					int numPaths = document.getElementsByTagName("path").getLength();
					//System.out.println(numPaths);
					float ar = this.getArea();
					float tempMinX = 1000;
					float tempMinY = 1000; 
					float tempMaxX = -1000;
					float tempMaxY = -1000; 
					for(int k = 0; k< numPaths; k++) {
						String polyDataRead = document.getElementsByTagName("path").item(k).getAttributes().getNamedItem("d").getNodeValue();
						//System.out.println(polyDataRead);
						String strCutDataRead1 = polyDataRead.substring(2,polyDataRead.length()-3);
						String strCutDataRead2 = strCutDataRead1.replaceAll("[A-Z] ", "");
						strCutDataRead2 = strCutDataRead2.replaceAll("[a-z] ", "");
						//System.out.println(strCutDataRead2);
						String[] strField = strCutDataRead2.split(" ");
						int numPoints = strField.length;
						int[] polyXCoords = new int[numPoints];
						int[] polyYCoords = new int[numPoints];
						// Convert String into Polygon
						for (int i=0; i<numPoints; i++)
						{
							//System.out.println("Punkt: "+strField[i]);
							String[] co = strField[i].split(",");
							float f_x = Float.parseFloat(co[0]);
							float f_y = Float.parseFloat(co[1]);
							int x = Math.round(f_x);
							int y = Math.round(f_y);
							polyXCoords[i] = x;
							polyYCoords[i] = y;
							//System.out.println("Integer: x="+x+" y="+y);
						}
						// for k = 0 set the first minimum as root coordinates
						if (k==0) {
							tempMinX = MyMath.min(polyXCoords);
							tempMinY = MyMath.min(polyYCoords);
							tempMaxX = MyMath.max(polyXCoords);
							tempMaxY = MyMath.max(polyYCoords);
						}
						
						pubXCoord = polyXCoords;
						pubYCoord = polyYCoords;
						Polygon pAdd = new Polygon(pubXCoord, pubYCoord, pubXCoord.length);
						//System.out.println(pAdd.npoints);
						this.polygon.add( pAdd);
						ar += MyMath.getArea(pAdd);
						
						// set new upper left corner (local coordinate system)
						setX(Math.min(tempMinX, MyMath.min(pubXCoord)));
						setY(Math.min(tempMinY, MyMath.min(pubYCoord)));
						setPolyX((int) Math.min(tempMinX, MyMath.min(pubXCoord)));
						setPolyY((int) Math.min(tempMinY, MyMath.min(pubYCoord)));
						tempMinX = Math.min(tempMinX, MyMath.min(polyXCoords));
						tempMinY = Math.min(tempMinY, MyMath.min(polyYCoords));
						tempMaxX = Math.max(tempMaxX,MyMath.max(polyXCoords));
						tempMaxY = Math.max(tempMaxY,MyMath.max(polyYCoords));
					}
					this.setArea(ar);
					this.setHeight(tempMaxY-this.getY());
					this.setWidth(tempMaxX-this.getX());
				} catch (SAXException e) {
					e.printStackTrace();
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPolyY(int polyY) {
		// TODO Auto-generated method stub
		this.polyY = polyY;
		setZIndex(polyY+getHeight());
	}

	public void setPolyX(int polyX) {
		// TODO Auto-generated method stub
		this.polyX = polyX;
	}
	
	public int getPolyX() {
		return this.polyX;
	}
	
	public int getPolyY() {
		return this.polyY;
	}

	/**
	 * Paint each Polygon of the Polygon-ArrayList
	 * @param g the Graphics2D-instance
	 */
	public void paint(Graphics2D g, int x, int y){
		int type = AlphaComposite.SRC_OVER;
		// reset the paintmode
		g.setPaintMode();
		// individualize paintmode
		AlphaComposite alphaTrans = AlphaComposite.getInstance(type, this.getTrans());
 	  	g.setComposite(alphaTrans);
 	  	Color color = new Color(this.rgb[0], this.rgb[1], this.rgb[2]);
 		g.setColor(color);
 		// TODO: check if texture exists for this space
 		if(texturePaint != null && bufferedImage !=null) {
 			setTexturePaint(new TexturePaint(bufferedImage, new Rectangle(this.getPolyX(),this.getPolyY(),this.bufferedImage.getWidth(),this.bufferedImage.getHeight())));
 			g.setPaint(texturePaint);
 		}
 		
 		Polygon pTemp;
		for (int i = 0; i<this.getPolygon().size(); i++) {
//			if (x >0 || y>0) {
				int[] x_shift = MyMath.polyTranslate(this.getPolygon().get(i).xpoints, 0);
				int[] y_shift =  MyMath.polyTranslate(this.getPolygon().get(i).ypoints, 0);
				
				pTemp = new Polygon(x_shift, y_shift, this.getPolygon().get(i).npoints);
//			} else
//				pTemp = new Polygon(this.getPolygon().get(i).xpoints, this.getPolygon().get(i).ypoints, this.getPolygon().get(i).npoints);
				g.translate(x, y);
			if (this.isFilled()){
				g.fillPolygon(pTemp);
			} else {
				g.drawPolygon(pTemp);
			}
			g.translate(-x, -y);
		}
 			
 		
	}
	
	/**
	 * @param color set new Color, for example: Color(110, 220, 50) (RGB) Color
	 * @param alpha between 0.0f and 1.0f, percentage of transpareny
	 * @param filled is the space filled or not?
	 */
	public void animate(int[] rgb, float alpha, boolean filled) {
		this.setTrans(alpha);
		this.setRGB(rgb);
		this.setFilled(filled);
	}
	
	/**
	 * whole the Space Object (1 or more Polygons) get new coordinates 
	 * (measured at the upper left corner of the global Bounding Box)
	 * @param x set X-coordinate
	 * @param y set y-coordinate
	 * 
	 */
	public void setAllXY(float x, float y) {
		this.setX(x);
		this.setY(y);
		for (int i = 0; i<this.getPolygon().size(); i++) {
			this.getPolygon().get(i).translate( (int) x-this.getPolyX(), (int) y-this.getPolyY());
		}
		this.setPolyX((int) x);
		this.setPolyY((int) y);
	}
	
	/**
	 * @param duration
	 * always move; the collision should be handled in the collision-thread
	 */
	public void move(long duration){
		float movementX = duration * this.getHorizontalMovement()/1000;
		float movementY = duration * this.getVerticalMovement()/1000;
		// Always update
		if(GameStates.getWidth()>0 && GameStates.getHeight()>0) {
			setX(this.getX()+movementX);
			setY(this.getY()+movementY);
		}
	}
	
	
	public SpaceAnimation getSpaceAnimation() {
		return spaceAnimation;
	}
	
	public void setSpaceAnimation(SpaceAnimation spaceAnimation) {
		this.spaceAnimation = spaceAnimation;
	}

	@Override
	public void setY(float y) {
		this.y = y;
		setZIndex(y+this.getHeight());
	}
	
//	public Color getColor() {
//		return color;
//	}
//
//	public void setColor(Color color) {
//		this.color = color;
//	}

	public Boolean isFilled() {
		return filled;
	}

	public void setFilled(Boolean filled) {
		this.filled = filled;
	}

	public float getTrans() {
		return trans;
	}
	
	public void setTrans(float alpha) {
		this.trans = alpha;
	}

	public float getArea() {
		return this.area;
	}
	
	public void setArea(float area) {
		this.area = area;
	}
	
	public void setRGB(int[] rgb){
		this.rgb= rgb;
	}
	
	public int[] getRGB(){
		return rgb;
	}
	
	public void scale(float factor) {
		Point positionOld = new Point((int)this.getX(), (int)this.getY());
		this.setAllXY(0, 0);
		for (int i = 0; i<this.getPolygon().size(); i++) {
			for (int j =0; j< this.getPolygon().get(i).npoints; j++) {
				this.getPolygon().get(i).xpoints[j] = (int) (this.getPolygon().get(i).xpoints[j] * factor);
				this.getPolygon().get(i).ypoints[j] = (int) (this.getPolygon().get(i).ypoints[j] * factor);
			}
		}
		this.setAllXY(positionOld.x, positionOld.y);
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void setPubXCoord(int[] pubXCoord) {
		this.pubXCoord = pubXCoord;
	}
	
	public void setPubYCoord(int[] pubYCoord) {
		this.pubYCoord = pubYCoord;
	}
	
	public int[] getPubXCoord() {
		return pubXCoord;
	}
	
	public int[] getPubYCoord() {
		return pubYCoord;
	}
	
}
