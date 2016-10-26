package com.lahodiuk.ahocorasick;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class AhoCorasickTest {

	private static final boolean DEBUG_OUTPUT = false;

	@Test
	public void test1() {

		String haystack = "ushers";
		String[] needles = { "he", "she", "his", "hers" };

		List<Found> expected = Utils.matchUsingDefaultJavaFunctionality(haystack, needles);
		List<Found> actual = Utils.matchUsingAhoCorasick(haystack, needles);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test2() {

		String haystack = "ushers ushers ushers ushers ushers test 123 ushers";
		String[] needles = { "he", "she", "his", "hers", "abcdef" };

		List<Found> expected = Utils.matchUsingDefaultJavaFunctionality(haystack, needles);
		List<Found> actual = Utils.matchUsingAhoCorasick(haystack, needles);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test3() {

		Random rnd = new Random(0);

		char[] alphabet = "abcd".toCharArray();
		int maxPatternLength = 10;
		int maxPatternsAmount = 100;
		int maxHaystackLength = 1000;
		int numTests = 100;

		for (int test = 0; test < numTests; test++) {

			String haystack = Utils.generateRandomString(rnd, alphabet, maxHaystackLength);
			String[] needles = Utils.generateRandomNeedles(rnd, alphabet, maxPatternsAmount, maxPatternLength);

			List<Found> expected = Utils.matchUsingDefaultJavaFunctionality(haystack, needles);
			List<Found> actual = Utils.matchUsingAhoCorasick(haystack, needles);

			if (DEBUG_OUTPUT) {
				System.out.println(haystack);
				System.out.println(Arrays.toString(needles));
				System.out.println(actual);
				System.out.println();
			}

			Assert.assertEquals(expected, actual);
		}
	}
}
