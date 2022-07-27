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
	
	private final Set<ManagedReference<ServerPolygon>> polygons = new HashSet<ManagedReference<ServerPolygon>>();
//	protected ArrayList<Polygon> polygon;
//	protected Color color;
//	protected BufferedImage bufferedImage; 
//	protected TexturePaint texturePaint;
	protected int[] rgb;
	protected float trans;
	protected Boolean filled;
	protected float scale;
	protected float area;
	protected int polyX, polyY;
	protected long lastTimestamp;
	
	
	public ServerSpace(Space space) {
		ArrayList<Polygon> polygon_array = space.getPolygon();
		for(int i = 1; i< polygon_array.size(); i++) {
			ServerPolygon s_polygon = new ServerPolygon(polygon_array.get(i).xpoints, polygon_array.get(i).ypoints);
			this.polygons.add(AppContext.getDataManager().createReference(s_polygon));
		}
		this.setName(space.getName());
		this.setRGB(space.getRGB());
		this.setTrans(space.getTrans());
		this.setFilled(space.isFilled());
		this.setX(space.getX());
		this.setY(space.getY());
		this.setWidth(space.getWidth());
		this.setHeight(space.getHeight());
		this.setMovement(space.getHorizontalMovement(), space.getVerticalMovement());
		this.setId(space.getId());
		this.setScale(space.getScale());
		this.scale(this.getScale());
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
			for (int j = 1; j< xcoords.length; j++) {
				xcoords[j] = (int) (xcoords[j]+ x-this.getX());
				ycoords[j] = (int) (ycoords[j]+ y-this.getY());
			}
			s_polygon.setPolyX(xcoords);
			s_polygon.setPolyY(ycoords);
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
			for (int j = 1; j< xcoords.length; j++) {
				xcoords[j] = (int) (xcoords[j]*factor);
				ycoords[j] = (int) (ycoords[j]*factor);
			}
			s_polygon.setPolyX(xcoords);
			s_polygon.setPolyY(ycoords);
		}		
		this.setAllXY(positionOld.x, positionOld.y);
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
}
