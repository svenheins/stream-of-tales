package de.svenheins.functions;

import java.awt.Point;
import java.awt.Polygon;
import java.lang.Math;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import math.geom2d.Point2D;
import math.geom2d.polygon.SimplePolygon2D;

public class MyMath {
	public static GeometryFactory geoFactory = new GeometryFactory();

	/*This function will return the determinant of any two dimensional matrix. For this particular function a two dimensional double matrix needs to be passed as arguments - Avishek Ghosh*/

	public static double determinant(double[][] mat) {
		double result = 0;
	
		if(mat.length == 1) {
			result = mat[0][0];
			return result;
		}
	
		if(mat.length == 2) {
			result = mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
			return result;
		}
	
		for(int i = 0; i < mat[0].length; i++) {
			double temp[][] = new double[mat.length - 1][mat[0].length - 1];
		
			for(int j = 1; j < mat.length; j++) {
				System.arraycopy(mat[j], 0, temp[j-1], 0, i);
				System.arraycopy(mat[j], i+1, temp[j-1], i, mat[0].length-i-1);
			}
		
			result += mat[0][i] * Math.pow(-1, i) * determinant(temp);
		}
	
		return result;

	}


	public static double getArea(Polygon p) {
		// now calculate the area of the polygon
		int[] pubXCoord = p.xpoints;
		int[] pubYCoord = p.ypoints;
		double area = 0;
		for (int j=0; j<pubXCoord.length-1; j++) {
			double[][] mat = {{pubXCoord[j], pubXCoord[j+1]}, {pubYCoord[j], pubYCoord[j+1]}};
			area += MyMath.determinant(mat);
		}
		double[][] firstMat = {{pubXCoord[pubXCoord.length-1], pubXCoord[0]}, {pubYCoord[pubYCoord.length-1], pubYCoord[0]}};
		area += MyMath.determinant(firstMat);
		area = Math.abs(area / 2);
		return area;
		//System.out.println("Area: "+ this.getArea());
	}
	
	
	
	/**
	 * @param array
	 * @return minimum element pf the array
	 */
	public static int min(int[] array) {
		int min = array[0];
		if( array.length > 1) {
			for (int i = 1; i<array.length; i++) {
				if (array[i]<min) min = array[i];
			}
		} else {
			// Do nothing, so min will be returned
		}
		return min;
	}
	
	/**
	 * @param array
	 * @return maximum element of the array
	 */
	public static int max(int[] array) {
		int max = array[0];
		if( array.length > 1) {
			for (int i = 1; i<array.length; i++) {
				if (array[i]>max) max = array[i];
			}
		} else {
			// Do nothing, so max will be returned
		}
		return max;
	}
	
	
	public Polygon polyTranslate(Polygon p, double mx, double my) {
		Polygon pTemp = p;
		for (int i = 0; i< p.npoints ; i++){
			pTemp.xpoints[i] = (int) Math.round(p.xpoints[i]+ mx);
			pTemp.ypoints[i] = (int) Math.round(p.ypoints[i]+ my);
		}
		return pTemp;
	}
	
	public static int[] polyTranslate(int[] p, double shift) {
		int[] pTemp = p;
		for (int i = 0; i< p.length ; i++){
			pTemp[i] = (int) Math.round(p[i]+ shift);
		}
		return pTemp;
	}
	
	/** returns the distance from a polygon (0 if inside the polygon) */
	public static double getDistance(Polygon polygon, Point point) {
		double[] xc = new double[polygon.npoints];
		double[] yc = new double[polygon.npoints];
		for (int i = 0; i<xc.length; i++) {
			xc[i] = polygon.xpoints[i];
			yc[i] = polygon.ypoints[i];
		}
		SimplePolygon2D simplePolygon = new SimplePolygon2D(xc, yc);
		Point2D p2D = new Point2D(point.x, point.y);
		
		return simplePolygon.distance(p2D);		
	}
	
	/** returns the distance between two points */
	public static double getDistance(Point pointA, Point pointB) {
		double distance = Math.sqrt((pointA.x-pointB.x)*(pointA.x-pointB.x)+(pointA.y-pointB.y)*(pointA.y-pointB.y));
		return distance;		
	}
	
	/** get nearest Point between polygon and point */
	public static Point getNearest(Polygon polygon, Point point) {
		com.vividsolutions.jts.geom.Point co_point = geoFactory.createPoint(new Coordinate(point.x, point.y));
		Coordinate[] co = new Coordinate[polygon.npoints+1];
		for (int i = 0; i< polygon.npoints; i++) {
			co[i] = new Coordinate(polygon.xpoints[i], polygon.ypoints[i]);
		}
		co[polygon.npoints] = new Coordinate(polygon.xpoints[0], polygon.ypoints[0]);
//		CoordinateList co_polygon = new CoordinateList(co);
		LinearRing linear = geoFactory.createLinearRing(co);

		com.vividsolutions.jts.geom.Polygon co_poly = new com.vividsolutions.jts.geom.Polygon(linear, null, geoFactory);
//		com.vividsolutions.jts.geom.Point g1 = new com.vividsolutions.jts.geom.Point();
		Coordinate[] pts = DistanceOp.closestPoints(co_poly, co_point);
		return (new Point((int)pts[0].x,(int) pts[0].y));
	}
	
	/** explore region of one point: 
	 * take a point and give back a random point its proximity
	 * with a given distance
	 */
	public static Point randomShot(Point point, int distance) {
		int x = (int) (Math.random()*2*distance -distance);
		int y = (int) Math.sqrt(distance*distance - x*x);
		Random random = new Random();
		boolean sign = random.nextBoolean();
		if (sign) y = -y;
		return new Point((int) (point.x+x), (int) (point.y+y));
	}
	
	
	/** explore region of one point: 
	 * take a point and return a point with given x and y distances
	 */
	public static Point randomShotQuadrat(Point point, int distance) {
		int x = (int) (Math.random()*2*distance -distance);
		int y = (int) (Math.random()*2*distance -distance);
		return new Point((int) (point.x+x), (int) (point.y+y));
	}
	
	/** calculate sum of int[] */
	public static int sum(int[] intArray) {
		int sumComplete = 0;
		for (int i = 0; i < intArray.length; i++) {
			sumComplete += intArray[i];
		}
		return sumComplete;
	}
	
	/** addition of float arrays (component wise) */
	public static float[] addFloatArrays(float[] float1, float[] float2) {
		if (float1.length == float2.length) {
			float[] retFloat = new float[float1.length];
			for (int i = 0; i < float1.length; i++) {
				retFloat[i] = float1[i] + float2[i];
			}
			return retFloat;
		} else {
			System.out.println("Wrong dimensions: float1.length="+float1.length+" VS float2.length="+float2.length);
			return null;
		}
	}
	
	/** addition of float arrays (component wise) */
	public static float[] add5FloatArrays(float[] float1, float[] float2, float[] float3, float[] float4, float[] float5) {
		if ((float1.length == float2.length) && (float1.length == float2.length)&& (float1.length == float3.length)&& (float1.length == float4.length)&& (float1.length == float5.length)) {
			float[] retFloat = new float[float1.length];
			for (int i = 0; i < float1.length; i++) {
				retFloat[i] = float1[i] + float2[i] + float3[i] + float4[i] + float5[i];
			}
			return retFloat;
		} else {
			System.out.println("Wrong dimensions: float1.length="+float1.length+" VS float2.length="+float2.length+" VS float3.length="+float3.length+" VS float4.length="+float4.length+" VS float5.length="+float5.length);
			return null;
		}
	}
}
