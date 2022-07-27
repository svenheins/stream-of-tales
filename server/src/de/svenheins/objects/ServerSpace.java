package de.svenheins.objects;


import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

public class ServerSpace extends WorldObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int[] pubXCoord;
	protected int[] pubYCoord;
	
	private Set<ManagedReference<ServerPolygon>> polygons = new HashSet<ManagedReference<ServerPolygon>>();
//	protected ArrayList<Polygon> polygon;
//	protected Color color;
//	protected BufferedImage bufferedImage; 
//	protected TexturePaint texturePaint;
	protected int[] rgb;
	protected float trans;
	protected Boolean filled;
	protected float scale;
	protected float area;
//	protected int polyX, polyY;
	protected long lastTimestamp;
	
	
	public ServerSpace(Space space) {
		ArrayList<Polygon> polygon_array = space.getPolygon();
		ServerPolygon s_polygon;
		for(int i = 0; i< polygon_array.size(); i++) {
			s_polygon = new ServerPolygon(polygon_array.get(i).xpoints, polygon_array.get(i).ypoints);
			this.polygons.add(AppContext.getDataManager().createReference(s_polygon));
		}
		this.setName(space.getName());
		this.setRGB(space.getRGB());
		this.setTrans(space.getTrans());
		this.setFilled(space.isFilled());
		this.setX(space.getPolyX());
		this.setY(space.getPolyY());
		this.setWidth(space.getWidth());
		this.setHeight(space.getHeight());
		this.setMovement(space.getHorizontalMovement(), space.getVerticalMovement());
		this.setId(space.getId());
		this.setScale(space.getScale());
//		this.scale(this.getScale());
		this.lastTimestamp = System.currentTimeMillis();
	}
	
	
//	public void moveOnX(long duration){
//		float movement = duration * mx/1000;
//		setX(getX()+movement);
//	}
//	
//	public void moveOnY(long duration){
//		float movement = duration * my/1000;
//		setY(getY()+movement);
//	}
	
	public void move(long timestamp){
		long duration = timestamp - this.lastTimestamp;
		float movementX = duration * mx/1000;
		float movementY = duration * my/1000;
		setAllXY(getX()+movementX, getY()+movementY);
		this.lastTimestamp = timestamp;
	}
	
	public void setAllXY(float x, float y) {
		for (ManagedReference<ServerPolygon> polygon : polygons)
		{
			ServerPolygon s_polygon = polygon.getForUpdate();
			int[] xcoords = s_polygon.getPolyX();
			int[] ycoords = s_polygon.getPolyY();
			Polygon p = new Polygon(xcoords, ycoords, xcoords.length);
//			for (int j = 0; j< xcoords.length; j++) {
				p.translate( (int) (x-this.getX()), (int) (y-this.getY()));
//				xcoords[j] = (int) (xcoords[j]+ x-this.getX());
//				ycoords[j] = (int) (ycoords[j]+ y-this.getY());
//			}
//			s_polygon.setPolyX(xcoords);
//			s_polygon.setPolyY(ycoords);
			s_polygon.setPolyX(p.xpoints);
			s_polygon.setPolyY(p.ypoints);
		}		
		this.setX(x);
		this.setY(y);
	}
	
	
	public void scale(float factor) {
		Point positionOld = new Point((int)this.getX(), (int)this.getY());
		this.setAllXY(0, 0);
		for (ManagedReference<ServerPolygon> polygon : polygons)
		{
			ServerPolygon s_polygon = polygon.getForUpdate();
			int[] xcoords = s_polygon.getPolyX();
			int[] ycoords = s_polygon.getPolyY();
			for (int j = 0; j< xcoords.length; j++) {
				xcoords[j] = (int) (xcoords[j]*factor);
				ycoords[j] = (int) (ycoords[j]*factor);
			}
			s_polygon.setPolyX(xcoords);
			s_polygon.setPolyY(ycoords);
		}		
		this.setAllXY(positionOld.x, positionOld.y);
		this.setWidth(factor*this.getWidth());
		this.setHeight(factor*this.getHeight());
	}
	
	public void setRGB(int[] rgb){
		this.rgb = rgb;
	}
	
	public int[] getRGB(){
		return rgb;
	}
	
	public boolean isFilled(){
		return filled;
	}
	
	public void setFilled(boolean b) {
		this.filled = b;
	}
	
	public void setTrans(float trans) {
		this.trans = trans;
	}
	
	public float getTrans(){
		return trans;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public ArrayList<Polygon> getPolygon() {
		ArrayList<Polygon> p = new ArrayList<Polygon>();
		
		/** run through polygon and add each to p */
		for (ManagedReference<ServerPolygon> serverpolygon : polygons) {
			ServerPolygon s = serverpolygon.get();
			int[] tempPolyX = s.polyX;
			int[] tempPolyY = s.polyY;
			
//			for( int i = 0; i<tempPolyX.length; i++) {
//				tempPolyX[i] = (int) (tempPolyX[i] - this.getX());
//				tempPolyY[i] = (int) (tempPolyY[i] - this.getY());
//			}
			
			p.add(new Polygon(tempPolyX, tempPolyY, s.polyX.length));
//			System.out.println("got length= "+s.polyX.length);
        }
		return p;
	}
}
