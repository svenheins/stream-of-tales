/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.functions;

import java.awt.Point;
import java.awt.Polygon;
import java.lang.Math;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import de.svenheins.main.GameStates;
import de.svenheins.objects.WorldPosition;
import math.geom2d.Point2D;
import math.geom2d.polygon.SimplePolygon2D;

public class MyMath {
	public static GeometryFactory geoFactory = new GeometryFactory();
	public static Random random = new Random();
	
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
	
	/** returns the distance between two points */
	public static double getDistance(WorldPosition pointA, WorldPosition pointB) {
		if(pointA.getRoom().equals(pointB.getRoom())) {
		double distance = Math.sqrt((pointA.getX()-pointB.getX())*(pointA.getX()-pointB.getX())+(pointA.getY()-pointB.getY())*(pointA.getY()-pointB.getY()));
		return distance;		
		} else return Double.MAX_VALUE;
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
	
	/** calculate 2D vector length */
	public static float calculateNorm(float vectorx, float vectory) {
		return (float) Math.sqrt(vectorx*vectorx + vectory*vectory);
	}
	
	/** calculate scalarproduct of two vectors */
	public static float calculateScalarProduct(float vector1x, float vector1y, float vector2x, float vector2y){
		return (vector1x*vector2x+vector1y*vector2y);
	}
	
	/** calculate an angle */
	public static float calculateAngleInRad(float vector1x, float vector1y, float vector2x, float vector2y) {
		return (float) (Math.acos(calculateScalarProduct(vector1x, vector1y, vector2x, vector2y)/(calculateNorm(vector1x, vector1y)*calculateNorm(vector2x, vector2y))));
	}
	
	
	/** caculate a by some triangle data vectorA (direction), vectorB (exact), c 
	 * c will be maxSpeed in the game, vectorA is (newMX, newMY) the direction of the goal and vectorB is the areaInfluence (mx, my) */
	public static float calculateTriangleA(float vectorAx, float vectorAy, float vectorBx, float vectorBy, float c) {
		float gamma = calculateAngleInRad(vectorAx, vectorAy, vectorBx, vectorBy);
		float b = calculateNorm(vectorBx, vectorBy);
		float sinGamma = (float) Math.sin(gamma);
		return (float) ( (b/((b/c)*sinGamma)) * Math.sin( Math.PI-Math.asin((b/c)*sinGamma)-gamma));
	}
	
	
	/** trim a gaussian value to 99% percentil (3x standadrDeviation values) */ 
	public static float getTrimmedGaussian(float mean) {
		float standardDeviation = GameStates.standardDeviationItemCount*mean;
		
		float retValue  = (float) (random.nextGaussian()*standardDeviation+mean);
		if (retValue < mean -3*standardDeviation) retValue = mean -3*standardDeviation;
		if (retValue > mean + 3*standardDeviation) retValue =mean +3*standardDeviation;
		
		return retValue;
	}
	
	/** same for array (as above)*/
	public static float[] getTrimmedGaussianArray(float[] means) {
		float[] retValues = means;
		for (int i = 0; i < means.length; i++) {
			if (means[i] != 0) retValues[i] = getTrimmedGaussian(means[i]);
		}
		return retValues;
	}
	
	/** normalize an int to a min and max value */
	public static int normalizeToMinMax(int attribute, int attributeMinimum, int attributeMaximum) {
		if (attribute > attributeMaximum) return attributeMaximum; else
		if (attribute < attributeMinimum) return attributeMinimum; else
			return attribute;
	}
	
	/** calculate distance of 3D double */
	public static double getDistance3D(double x1, double x2, double x3, double y1, double y2, double y3) {
		return Math.sqrt((x1-y1)*(x1-y1)+(x2-y2)*(x2-y2)+(x3-y3)*(x3-y3));
	}
	
	/** calculate distance of 3D double */
	public static double getColorDistance3D(double x1, double x2, double x3, double y1, double y2, double y3) {
		double minx = Math.min(Math.min(x1, x2), x3);
		double miny = Math.min(Math.min(y1, y2), y3);
		double maxx = Math.max(Math.max(x1, x2), x3);
		double maxy = Math.max(Math.max(y1, y2), y3);
		double x11 = x1;
		double x21 = x2;
		double x31 = x3;
		double y11 = y1;
		double y21 = y2;
		double y31 = y3;
		if (maxx - minx > 0.25) {
			if (maxx - x1 > 0.5) x11 = 0;
			if (x1 - minx < 0.12) x11 = 0;
		}
		if (maxx - minx > 0.25) {
			if (maxx - x2 > 0.5) x21 = 0;
			if (x2 - minx < 0.12) x21 = 0;
		}
		if (maxx - minx > 0.25) {
			if (maxx - x3 > 0.5) x31 = 0;
			if (x3 - minx < 0.12) x31 = 0;
		}
		if (maxy - miny > 0.25) {
			if (maxy - y1 > 0.5) y11 = 0;
			if (y1 - miny < 0.12) y11 = 0;
		}
		if (maxy - miny > 0.25) {
			if (maxy - y2 > 0.5) y21 = 0;
			if (y2 - miny < 0.12) y21 = 0;
		}
		if (maxy - miny > 0.25) {
			if (maxy - y3 > 0.5) y31 = 0;
			if (y3 - miny < 0.12) y31 = 0;
		}
//		System.out.println(Math.sqrt((x11-y11)*(x11-y11)+(x21-y21)*(x21-y21)+(x31-y31)*(x31-y31)));
		return Math.sqrt((x11-y11)*(x11-y11)+(x21-y21)*(x21-y21)+(x31-y31)*(x31-y31));
	}
	
	/** calculate distance of 3D double */
	public static double getQuadraticDistance3D(double x1, double x2, double x3, double y1, double y2, double y3) {
		double minx = Math.min(Math.min(x1, x2), x3);
		double miny = Math.min(Math.min(y1, y2), y3);
		double x11 = x1-minx;
		double x21 = x2-minx;
		double x31 = x3-minx;
		double y11 = y1-miny;
		double y21 = y2-miny;
		double y31 = y3-miny;
		return Math.sqrt((x11*x11-y11*y11)*(x11*x11-y11*y11)+(x21*x21-y21*y21)*(x21*x21-y21*y21)+(x31*x31-y31*y31)*(x31*x31-y31*y31));
	}
	
	/** calculate distance of 3D double */
	public static double getCubicDistance3D(double x1, double x2, double x3, double y1, double y2, double y3) {
		return Math.sqrt((x1*x1*x1-y1*y1*y1)*(x1*x1*x1-y1*y1*y1)+(x2*x2*x2-y2*y2*y2)*(x2*x2*x2-y2*y2*y2)+(x3*x3*x3-y3*y3*y3)*(x3*x3*x3-y3*y3*y3));
	}
}
