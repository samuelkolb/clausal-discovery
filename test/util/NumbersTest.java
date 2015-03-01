package util;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class NumbersTest {

	@Test
	public void testFactorial() {
		assertEquals(3628800, Numbers.getFactorial(10));
	}

	@Test
	public void testPermutationsDistinct() {
		Set<Numbers.Permutation> permutations = new HashSet<>(Numbers.getPermutations(3));
		assertEquals(6, permutations.size());
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{0, 1, 2})));
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{0, 2, 1})));
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{1, 0, 2})));
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{1, 2, 0})));
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{2, 0, 1})));
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{2, 1, 0})));
	}

	@Test
	public void testPermutationsDouble() {
		Set<Numbers.Permutation> permutations = new HashSet<>(Numbers.getPermutations(new int[]{1, 1, 2}));
		assertEquals(3, permutations.size());
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{1, 1, 2})));
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{1, 2, 1})));
		assertTrue(permutations.contains(new Numbers.Permutation(new int[]{2, 1, 1})));
	}

	@Test
	public void testCombinations() {
		Set<Numbers.Permutation> combinations = new HashSet<>(Numbers.getCombinations(3, 5));
		assertEquals(6, combinations.size());
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 0, 0, 1, 2})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 0, 1, 1, 2})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 0, 1, 2, 2})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 1, 1, 1, 2})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 1, 1, 2, 2})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 1, 2, 2, 2})));
	}

	@Test
	public void testTake() {
		Set<Numbers.Permutation> combinations = new HashSet<>(Numbers.take(2, 3));
		assertEquals(6, combinations.size());
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 0, 1})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{1, 0, 0})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 1, 0})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{1, 0, 1})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{0, 1, 1})));
		assertTrue(combinations.contains(new Numbers.Permutation(new int[]{1, 1, 0})));
	}

	@Test
	public void testChoices() {
		Set<Numbers.Permutation> choices = new HashSet<>(Numbers.getChoices(4, 3));
		assertEquals(4, choices.size());
		assertTrue(choices.contains(new Numbers.Permutation(new int[]{0, 1, 2})));
		assertTrue(choices.contains(new Numbers.Permutation(new int[]{0, 1, 3})));
		assertTrue(choices.contains(new Numbers.Permutation(new int[]{0, 2, 3})));
		assertTrue(choices.contains(new Numbers.Permutation(new int[]{1, 2, 3})));
	}
}