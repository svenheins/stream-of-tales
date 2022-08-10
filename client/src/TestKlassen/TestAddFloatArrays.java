package TestKlassen;

import de.svenheins.functions.MyMath;

public class TestAddFloatArrays {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		float[] tfloat1 = new float[]{1.0f,2.0f,3.0f};
		float[] tfloat2 = new float[]{1.0f,2.0f, 3.0f};
		for (int i = 0; i < tfloat1.length; i++) {
			System.out.println("array element["+i+"]="+MyMath.addFloatArrays(tfloat1, tfloat2)[i]);
		}
	}

}
