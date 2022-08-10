package TestKlassen;

import de.svenheins.functions.MyMath;

public class TestACalculation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		float vectorAx = 1.0f;
		float vectorAy = 0;//(float) Math.sqrt(1);
		float vectorBx = 0.0f;
		float vectorBy = 1.0f;
		float c = 1.0f;
		
		System.out.println("Length of vectorA: "+ MyMath.calculateNorm(vectorAx, vectorAy));
		System.out.println("Length of vectorB: "+ MyMath.calculateNorm(vectorBx, vectorBy));
		System.out.println("scalarproduct between vectorA and vectorB: "+ MyMath.calculateScalarProduct(vectorAx, vectorAy, vectorBx, vectorBy));
		System.out.println("angle between vectorA and vectorB: "+ MyMath.calculateAngleInRad(vectorAx, vectorAy, vectorBx, vectorBy));//(vectorAx, vectorAy));
		System.out.println("A is: " + MyMath.calculateTriangleA(vectorAx, vectorAy, vectorBx, vectorBy, c));
	}

}
