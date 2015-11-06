package util;

import basic.ArrayUtil;

import java.util.*;

/**
 * Created by samuelkolb on 20/02/15.
 */
public class Numbers {

	public static class Permutation {

		private final int[] array;

		public int[] getArray() {
			return array;
		}

		public Integer[] getIntegerArray() {
			Integer[] integers = new Integer[array.length];
			for(int i = 0; i < array.length; i++)
				integers[i] = array[i];
			return integers;
		}

		public Permutation(int[] array) {
			this.array = array;
		}

		public Permutation substitute(int[] substitutes) {
			int[] substitution = new int[array.length];
			for(int i = 0; i < substitution.length; i++)
				substitution[i] = substitutes[this.array[i]];
			return new Permutation(substitution);
		}

		public <T> List<T> applyList(T[] array) {
			List<T> result = new ArrayList<>();
			for(int i : getArray())
				result.add(array[i]);
			return result;
		}

		public <T> List<T> applyList(List<T> list) {
			List<T> result = new ArrayList<>();
			for(int i : getArray())
				result.add(list.get(i));
			return result;
		}

		public <T> T[] applyArray(T[] array) {
			T[] result = ArrayUtil.copy(array);
			for(int i = 0; i < getArray().length; i++)
				result[i] = (array[getArray()[i]]);
			return result;
		}

		public int getDistinctCount() {
			Set<Integer> elements = new HashSet<>();
			for(Integer i : getArray())
				elements.add(i);
			return elements.size();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Permutation that = (Permutation) o;
			return Arrays.equals(array, that.array);

		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(array);
		}

		@Override
		public String toString() {
			return Arrays.toString(array);
		}

		/**
		 * Determines whether this permutation is sorted
		 * @return	True iff this permutation containsInstance a non-decreasing sequence of numbers
		 */
		public boolean isSorted() {
			int current = getArray()[0];
			for(int i = 1; i < getArray().length; i++)
				if(getArray()[i] < current)
					return false;
				else
					current = Math.max(current, getArray()[i]);
			return true;
		}
	}

	public static List<Permutation> take(int pool, int length) {
		if(length < pool)
			throw new UnsupportedOperationException("Length has to be at least as large as the pool size");
		List<int[]> substitutesList = getCombinationArrays(pool, length);
		List<Permutation> permutations = new ArrayList<>();
		for(int[] substitutes : substitutesList)
			permutations.addAll(getPermutations(substitutes));
		return permutations;
	}

	public static List<Permutation> getCombinations(int pool, int length) {
		return convert(getCombinationArrays(pool, length));
	}

	public static List<int[]> getCombinationArrays(int pool, int length) {
		if(length < pool)
			throw new UnsupportedOperationException("Length has to be at least as large as the pool size");
		List<int[]> combinations = new ArrayList<>();
		addCombinations(new int[]{}, pool - 1, length, combinations);
		return combinations;
	}

	private static void addCombinations(int[] prefix, int max, int length, Collection<int[]> combinations) {
		if(length == 0) {
			combinations.add(prefix);
		} else {
			int last = prefix.length > 0 ? prefix[prefix.length - 1] : 0;
			if(max - last < length)
				addCombinations(newPrefix(prefix, last), max, length - 1, combinations);
			if(prefix.length > 0 && last < max)
				addCombinations(newPrefix(prefix, last + 1), max, length - 1, combinations);
		}
	}

	public static List<Permutation> getChoices(int pool, int length) {
		return convert(getChoiceArrays(pool, length));
	}

	public static List<int[]> getChoiceArrays(int pool, int length) {
		if(length > pool)
			throw new UnsupportedOperationException("Length cannot exceed pool size");
		List<int[]> choices = new ArrayList<>();
		addChoices(new int[]{}, pool - 1, length, choices);
		return choices;
	}

	private static void addChoices(int[] prefix, int max, int length, Collection<int[]> choices) {
		if(length == 0) {
			choices.add(prefix);
		} else {
			int last = prefix.length > 0 ? prefix[prefix.length - 1] : -1;
			for(int i = last + 1; i <= max - length + 1; i++) {
				addChoices(newPrefix(prefix, i), max, length - 1, choices);
			}
		}
	}

	public static List<Permutation> getPermutations(int[] elements) {
		// TODO sort and skip equal elements
		List<Permutation> originalPermutations = getPermutations(elements.length);
		Set<Permutation> permutations = new HashSet<>();
		for(Permutation permutation : originalPermutations)
			permutations.add(permutation.substitute(elements));
		return new ArrayList<>(permutations);
	}

	public static List<Permutation> getPermutations(int n) {
		return convert(getPermutationArrays(n));
	}

	public static List<int[]> getPermutationArrays(int n) {
		long capacity = getFactorial(n);
		if(capacity > Integer.MAX_VALUE)
			throw new IllegalArgumentException("n (" +  n + ") is too large");
		List<int[]> permutations = new ArrayList<>((int) capacity);
		addPermutations(new int[]{}, range(n - 1), permutations);
		return permutations;
	}

	public static void main(String[] args) {
		Set<int[]> permutations = new HashSet<>();
		addPermutations(new int[]{}, new int[]{1, 1, 2}, permutations);
		for(int[] p : permutations)
			System.out.println(Arrays.toString(p));
	}

	private static void addPermutations(int[] prefix, int[] elements, Collection<int[]> permutations) {
		if(elements.length == 1) {
			permutations.add(newPrefix(prefix, elements[0]));
		} else {
			for(int i = 0; i < elements.length; i++) {
				int[] newPrefix = newPrefix(prefix, elements[i]);
				int[] newElements = leaveOut(elements, i);
				addPermutations(newPrefix, newElements, permutations);
			}
		}
	}

	private static int[] newPrefix(int[] prefix, int number) {
		int[] newPrefix = new int[prefix.length + 1];
		System.arraycopy(prefix, 0, newPrefix, 0, prefix.length);
		newPrefix[prefix.length] = number;
		return newPrefix;
	}

	private static int[] leaveOut(int[] elements, int index) {
		int[] newElements = new int[elements.length - 1];
		//noinspection ManualArrayCopy
		for(int i = 0; i < index; i++)
			newElements[i] = elements[i];
		//noinspection ManualArrayCopy
		for(int i = index; i < newElements.length; i++)
			newElements[i] = elements[i + 1];
		return newElements;
	}

	public static int[] range(int max) {
		if(max < 0)
			throw new IllegalArgumentException("Max cannot be smaller as 0");
		int[] array = new int[max + 1];
		for(int i = 0; i <= max; i++)
			array[i] = i;
		return array;
	}

	public static int[] range(int min, int max) {
		if(max < min)
			throw new IllegalArgumentException("Max cannot be smaller as min");
		int[] array = new int[max - min + 1];
		for(int i = min; i <= max; i++)
			array[i - min] = i;
		return array;
	}

	public static double[] range(double min, double max, double step) {
		int size = (int) Math.ceil((max - min) / step) + 1;
		double[] array = new double[size];
		for(int i = 0; i < size; i++)
			array[i] = min + i * step;
		return array;
	}

	private static List<Permutation> convert(List<int[]> arrays) {
		List<Permutation> permutations = new ArrayList<>(arrays.size());
		for(int[] array : arrays)
			permutations.add(new Permutation(array));
		return permutations;
	}

	public static long getFactorial(int n) {
		long result = n;
		for(--n; n > 1; n--)
			result *= n;
		return result;
	}
}
