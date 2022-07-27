package de.svenheins.functions;

import java.awt.Polygon;
import java.lang.Math;

public class MyMath {

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
	
}
