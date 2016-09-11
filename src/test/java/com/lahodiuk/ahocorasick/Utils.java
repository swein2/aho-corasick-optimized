//   Copyright 2015 Yurii Lahodiuk (yura.lagodiuk@gmail.com)
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package com.lahodiuk.ahocorasick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Utils {

	public static String generateRandomString(Random rnd, char[] alphabet, int maxLength) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < (rnd.nextInt(maxLength) + 1); i++) {
			sb.append(alphabet[rnd.nextInt(alphabet.length)]);
		}
		return sb.toString();
	}

	public static String[] generateRandomNeedles(Random rnd, char[] alphabet, int maxPatternsAmount, int maxPatternLength) {
		List<String> needlesList = new ArrayList<>();
		for (int i = 0; i < (rnd.nextInt(maxPatternsAmount) + 1); i++) {
			needlesList.add(generateRandomString(rnd, alphabet, maxPatternLength));
		}
		String[] needles = needlesList.toArray(new String[0]);
		return needles;
	}

	/**
	 * Find all needles using Aho-Corasick algorithm
	 */
	public static List<Found> matchUsingAhoCorasick(String haystack, String... needles) {
		AhoCorasickOptimized alg = new AhoCorasickOptimized(needles);

		List<Found> result = new ArrayList<>();
		alg.match(haystack, (start, end, found) -> result.add(new Found(found, start, end)));

		Collections.sort(result);
		return result;
	}

	/**
	 * Find all needles using java.lang.String.indexOf(String, int)
	 */
	public static List<Found> matchUsingDefaultJavaFunctionality(String haystack, String... needles) {
		List<Found> result = new ArrayList<>();

		for (String needle : needles) {
			int fromIndex = 0;

			while (fromIndex >= 0) {

				int foundIndex = haystack.indexOf(needle, fromIndex);

				if (foundIndex >= 0) {
					result.add(new Found(needle, foundIndex, (foundIndex + needle.length()) - 1));
					fromIndex = foundIndex + 1;
				} else {
					break;
				}
			}
		}

		Collections.sort(result);
		return result;
	}
}
