package TestKlassen;

import java.math.BigInteger;
import java.util.Vector;

import de.svenheins.functions.MyMath;
import de.svenheins.main.GameStates;
import de.svenheins.main.AttributeType;
import de.svenheins.main.Priority;
import de.svenheins.managers.AreaInfluenceManager;
import de.svenheins.objects.AreaInfluence;
import de.svenheins.objects.Entity;
import de.svenheins.objects.TileSet;

public class TestInfluenceManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TileSet tileSet = new TileSet(GameStates.standardTileNamePlayer, "standardPlayer", 32, 64);
		float[] values = new float[AttributeType.values().length];
//		for (int i = 0; i < values.length; i++ ) {
		values[AttributeType.ARMOR.ordinal()] = 100.234f;
//		}
		BigInteger runID = BigInteger.valueOf(0);
		AreaInfluence iArea1 = new AreaInfluence(runID, System.currentTimeMillis(), System.currentTimeMillis(), new Entity(tileSet, "test", BigInteger.valueOf(0), 0, 0, 300), "testGroup1", false, values, Priority.HIGH);
		runID = runID.add(BigInteger.valueOf(1));
		AreaInfluence iArea2 = new AreaInfluence(runID, System.currentTimeMillis(), System.currentTimeMillis(), new Entity(tileSet, "test", BigInteger.valueOf(0), 0, 0, 300), "testGroup2", false, values, Priority.MEDIUM);
		runID = runID.add(BigInteger.valueOf(1));
		AreaInfluence iArea3 = new AreaInfluence(runID, System.currentTimeMillis(), System.currentTimeMillis(), new Entity(tileSet, "test", BigInteger.valueOf(0), 0, 0, 300), "testGroup3", false, values, Priority.LOW);
		runID = runID.add(BigInteger.valueOf(1));
		AreaInfluence iArea4 = new AreaInfluence(runID, System.currentTimeMillis(), System.currentTimeMillis(), new Entity(tileSet, "test", BigInteger.valueOf(0), 0, 0, 300), "testGroup4", false, values, Priority.LOW);
		runID = runID.add(BigInteger.valueOf(1));
		AreaInfluence iArea5 = new AreaInfluence(runID, System.currentTimeMillis(), System.currentTimeMillis(), new Entity(tileSet, "test", BigInteger.valueOf(0), 0, 0, 300), "testGroup5", false, values, Priority.HIGH);
		runID = runID.add(BigInteger.valueOf(1));
		AreaInfluence iArea6 = new AreaInfluence(runID, System.currentTimeMillis(), System.currentTimeMillis(), new Entity(tileSet, "test", BigInteger.valueOf(0), 0, 0, 300), "testGroup6", false, values, Priority.MEDIUM);
		
		
//		for (int i = 0; i < AreaInfluenceManager.size(); i++ ) {
//			System.out.println("No: "+i + " is "+AreaInfluenceManager.get(i).getGroupName()+" with priority: "+AreaInfluenceManager.get(i).getPriority());
//		}
		
		
		AreaInfluenceManager.add(iArea1);
		output();
		
		AreaInfluenceManager.add(iArea2);
		output();
		
		AreaInfluenceManager.add(iArea3);
		output();
		
		AreaInfluenceManager.add(iArea4);
		output();
		
		AreaInfluenceManager.add(iArea5);
		output();

		AreaInfluenceManager.add(iArea6);
		output();
		
	}
	
	public static void output() {
		int i = 0;
		for (BigInteger tempID: AreaInfluenceManager.idList) {
			System.out.println("No: "+i + " is "+AreaInfluenceManager.get(tempID).getGroupName()+" with priority: "+AreaInfluenceManager.get(tempID).getPriority());
			i++;
		}
	}

}
