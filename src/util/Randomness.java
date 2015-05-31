package util;

import java.util.Random;

/**
 * Created by samuelkolb on 30/05/15.
 *
 * @author Samuel Kolb
 */
public class Randomness {

	public static long seed = -1;

	/**
	 * Returns the current seed
	 * @return The seed that has been set or the current time if no seed was set
	 */
	public static long getSeed() {
		if(seed == -1)
			seed = System.currentTimeMillis();
		return seed;
	}

	/**
	 * Provide a seed (can only be done once)
	 * @param seed	The seed
	 * @return	True iff the seed was set to the new seed
	 */
	public static boolean setSeed(long seed) {
		if(seed == -1)
			Randomness.seed = seed;
		else
			return false;
		return true;
	}

	private static Random random = null;

	/**
	 * Retrieves the (unique) source of randomness
	 * @return	A random object initialised with the seed (from getSeed)
	 */
	public static Random getRandom() {
		if(random == null)
			random = new Random(getSeed());
		return random;
	}
}
