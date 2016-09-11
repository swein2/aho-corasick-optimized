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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Implementation of the Aho-Corasick string matching algorithm, described in
 * the paper "Efficient String Matching: An Aid to Bibliographic Search",
 * written by Alfred V. Aho and Margaret J. Corasick, Bell Laboratories, 1975
 *
 * This implementation takes into account the specificities of the HotSpot JVM,
 * and supposed to be the Garbage Collector friendly. The automaton is based
 * only on the primitive data types in order to avoid Autoboxing and Unboxing
 * conversions.
 *
 * @author of the implementation is Yurii Lahodiuk (yura.lagodiuk@gmail.com)
 */
public class AhoCorasickOptimized extends AhoCorasick {

	private static final int INITIAL_STATE = 0;
	private static final int FAIL = -1;

	// the sorted array of the unique characters (alphabet)
	// every character from the alphabet is "mapped" to it's own index inside
	// this array
	// mapping: "character" -> "character index"
	private char[] charToIntMapping;
	// every character, which is not inside the alphabet is mapped to this
	// special index
	private int absentCharInt;

	// the automaton transitions table
	// mapping: "current state AND input character index" -> "new state"
	private int[][] goTo;
	// table of the outputs of every state
	// mapping: "state" -> "matched patterns"
	private List<String>[] output;
	// table of the fail transitions of the automaton
	// mapping: "state" -> "new state"
	private int[] fail;

	public AhoCorasickOptimized(String... patterns) {

		this.initializeCharToIntMapping(patterns);
		this.absentCharInt = this.charToIntMapping.length;

		int maxAmountOfStates = this.getMaxPossibleAmountOfStates(patterns);

		this.initializeTransitionsTable(maxAmountOfStates);
		this.initializeOutputTable(maxAmountOfStates);
		this.initializeFailureTransitions(maxAmountOfStates);

		this.calculateTransitionsTable(patterns);
		this.makeInitialStateNeverFail();
		this.calculateFailureTransitions();
	}

	@Override
	public final void match(final String text, MatchCallback callback) {

		int state = INITIAL_STATE;

		for (int ci = 0; ci < text.length(); ci++) {

			char chr = text.charAt(ci);
			int char2IntMappingIndex = Arrays.binarySearch(this.charToIntMapping, chr);
			int chrInt = char2IntMappingIndex < 0 ? this.absentCharInt : char2IntMappingIndex;

			while (this.goTo[state][chrInt] == FAIL) {
				state = this.fail[state];
			}

			state = this.goTo[state][chrInt];

			List<String> matched = this.output[state];
			for (int j = 0; j < matched.size(); j++) {
				String found = matched.get(j);
				callback.onMatch((ci - found.length()) + 1, ci, found);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeOutputTable(int maxAmountOfStates) {
		this.output = new List[maxAmountOfStates];
		for (int i = 0; i < this.output.length; i++) {
			this.output[i] = new ArrayList<>();
		}
	}

	private void initializeFailureTransitions(int maxAmountOfStates) {
		this.fail = new int[maxAmountOfStates];
		Arrays.fill(this.fail, FAIL);
		this.fail[INITIAL_STATE] = INITIAL_STATE;
	}

	private void initializeTransitionsTable(int maxAmountOfStates) {
		this.goTo = new int[maxAmountOfStates][this.charToIntMapping.length + 1];
		for (int[] row : this.goTo) {
			Arrays.fill(row, FAIL);
		}
	}

	private void makeInitialStateNeverFail() {
		for (int i = 0; i < this.goTo[INITIAL_STATE].length; i++) {
			if (this.goTo[INITIAL_STATE][i] == FAIL) {
				this.goTo[INITIAL_STATE][i] = INITIAL_STATE;
			}
		}
	}

	private int getMaxPossibleAmountOfStates(String... patterns) {
		int maxAmountOfStates = 1;
		for (String s : patterns) {
			maxAmountOfStates += s.length();
		}
		return maxAmountOfStates;
	}

	private void initializeCharToIntMapping(String... patterns) {
		Set<Character> uniqueChars = new HashSet<>();
		for (String s : patterns) {
			for (char c : s.toCharArray()) {
				uniqueChars.add(c);
			}
		}
		this.charToIntMapping = new char[uniqueChars.size()];
		int charToIntMappingIdx = 0;
		for (char c : uniqueChars) {
			this.charToIntMapping[charToIntMappingIdx] = c;
			charToIntMappingIdx++;
		}
		Arrays.sort(this.charToIntMapping);
	}

	// Calculation of the failure transitions using BFS
	private void calculateFailureTransitions() {

		Queue<Integer> queue = new LinkedList<>();

		// all states of depth 1 (counting from the initial state)
		// have failure transition to the initial state
		for (int stateReachableFromInitial : this.goTo[INITIAL_STATE]) {
			if (stateReachableFromInitial != INITIAL_STATE) {
				queue.add(stateReachableFromInitial);
				this.fail[stateReachableFromInitial] = INITIAL_STATE;
			}
		}

		while (!queue.isEmpty()) {
			int curr = queue.remove();

			for (int chrInt = 0; chrInt < this.goTo[curr].length; chrInt++) {

				int stateReachableFromCurr = this.goTo[curr][chrInt];

				if (stateReachableFromCurr != FAIL) {
					queue.add(stateReachableFromCurr);

					int state = this.fail[curr];
					while (this.goTo[state][chrInt] == FAIL) {
						state = this.fail[state];
					}

					this.fail[stateReachableFromCurr] = this.goTo[state][chrInt];
					this.output[stateReachableFromCurr].addAll(this.output[this.fail[stateReachableFromCurr]]);
				}
			}
		}
	}

	private void calculateTransitionsTable(String... patterns) {

		int newState = 0;
		for (String s : patterns) {

			int state = INITIAL_STATE;

			// index of the current character
			int ci = 0;

			// traversal through the states, which are already created
			while (ci < s.length()) {
				char chr = s.charAt(ci);
				int chrInt = Arrays.binarySearch(this.charToIntMapping, chr);

				if (this.goTo[state][chrInt] != FAIL) {
					state = this.goTo[state][chrInt];
					ci++;
				} else {
					break;
				}
			}

			// creation of the new states
			while (ci < s.length()) {
				char chr = s.charAt(ci);
				int chrInt = Arrays.binarySearch(this.charToIntMapping, chr);

				newState = newState + 1;
				this.goTo[state][chrInt] = newState;
				state = newState;

				ci++;
			}

			// remember current pattern as the output for the last processed
			// state
			this.output[state].add(s);
		}
	}

	public void generateGraphvizAutomatonRepresentation(boolean displayEdgesToInitialState) {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph automaton {").append('\n');
		{
			sb.append("\tgraph [rankdir=LR];\n");
			Queue<Integer> queue = new LinkedList<>();
			queue.add(INITIAL_STATE);

			List<Integer> visitedStates = new ArrayList<>();

			while (!queue.isEmpty()) {
				int state = queue.remove();
				visitedStates.add(state);

				for (int charInt = 0; charInt < this.charToIntMapping.length; charInt++) {
					if ((this.goTo[state][charInt] != FAIL) && (this.goTo[state][charInt] != INITIAL_STATE)) {
						queue.add(this.goTo[state][charInt]);

						sb.append('\t').append(state).append(" -> ").append(this.goTo[state][charInt])
								.append(" [label=").append(this.charToIntMapping[charInt]).append(", weight=100, style=bold];").append("\n");
					}
				}
			}

			for (int state : visitedStates) {
				if (displayEdgesToInitialState || ((this.fail[state] != INITIAL_STATE) || (state == INITIAL_STATE))) {
					sb.append('\t').append(state).append(" -> ").append(this.fail[state])
							.append(" [style=dashed, color=gray, constraint=false];").append("\n");
				}
			}

			for (int state : visitedStates) {
				if (!this.output[state].isEmpty()) {
					sb.append('\t').append(state).append(" [shape=doublecircle];\n");
				} else {
					sb.append('\t').append(state).append(" [shape=circle];\n");
				}
			}
		}
		sb.append("}");
		System.out.println(sb.toString());
	}

	public static void main(String[] args) {
		AhoCorasickOptimized alg = new AhoCorasickOptimized("he", "she", "his", "hers");
		alg.match("ushers", (start, end, found) -> System.out.println(found));
		System.out.println();
		alg.match("ushers ushers ushers ushers ushers", (start, end, found) -> System.out.println(found));

		System.out.println();
		alg.generateGraphvizAutomatonRepresentation(false);
	}
}
