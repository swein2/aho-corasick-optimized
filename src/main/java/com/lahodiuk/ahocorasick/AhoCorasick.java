package com.lahodiuk.ahocorasick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * Implementation of the Aho-Corasick string matching algorithm, described in
 * the paper "Efficient String Matching: An Aid to Bibliographic Search",
 * written by Alfred V. Aho and Margaret J. Corasick, Bell Laboratories, 1975
 *
 * @author of the implementation is Yurii Lahodiuk (yura.lagodiuk@gmail.com)
 */
public class AhoCorasick {

	private static final int INITIAL_STATE = 0;

	private Map<Integer, Map<Character, Integer>> goTo;
	private Map<Integer, List<String>> output;
	private Map<Integer, Integer> fail;

	public AhoCorasick(String... patterns) {

		this.goTo = new HashMap<>();
		this.goTo.put(0, new HashMap<Character, Integer>());

		this.output = new HashMap<>();
		this.fail = new HashMap<>();

		this.initializeGoTo(patterns);
		this.initializeFail();
	}

	public void match(String text, MatchCallback callback) {
		int state = INITIAL_STATE;
		for (int i = 0; i < text.length(); i++) {
			char chr = text.charAt(i);

			while (this.isFail(state, chr)) {
				state = this.fail.get(state);
			}
			state = this.goTo(state, chr);

			List<String> matched = this.output(state);
			for (int j = 0; j < matched.size(); j++) {
				String found = matched.get(j);
				callback.onMatch((i - found.length()) + 1, i, found);
			}
		}
	}

	private void initializeFail() {
		Queue<Integer> queue = new LinkedList<>();

		for (int stateReachableFromInitial : this.goTo.get(INITIAL_STATE).values()) {
			queue.add(stateReachableFromInitial);
			this.fail.put(stateReachableFromInitial, INITIAL_STATE);
		}

		while (!queue.isEmpty()) {
			int curr = queue.remove();

			for (Entry<Character, Integer> kv : this.goTo.get(curr).entrySet()) {
				char chr = kv.getKey();
				int stateReachableFromCurr = kv.getValue();

				queue.add(stateReachableFromCurr);

				int state = this.fail.get(curr);
				while (this.isFail(state, chr)) {
					state = this.fail.get(state);
				}

				this.fail.put(stateReachableFromCurr, this.goTo(state, chr));

				this.output(stateReachableFromCurr).addAll(this.output(this.fail.get(stateReachableFromCurr)));
			}
		}
	}

	private void initializeGoTo(String... patterns) {
		int newState = 0;
		for (String s : patterns) {

			int state = INITIAL_STATE;
			int chrIdx = 0;
			while (this.goTo.get(state).containsKey(s.charAt(chrIdx)) && (chrIdx < s.length())) {
				state = this.goTo(state, s.charAt(chrIdx));
				chrIdx++;
			}

			while (chrIdx < s.length()) {
				newState = newState + 1;
				this.goTo.put(newState, new HashMap<Character, Integer>());

				Map<Character, Integer> charToState = this.goTo.get(state);
				if (charToState == null) {
					charToState = new HashMap<>();
				}
				charToState.put(s.charAt(chrIdx), newState);
				this.goTo.put(state, charToState);
				state = newState;
				chrIdx++;
			}

			this.output.put(state, new ArrayList<>(Arrays.asList(s)));
		}
	}

	protected boolean isFail(int state, char character) {
		if (state == INITIAL_STATE) {
			return false;
		}
		return !this.goTo.get(state).containsKey(character);
	}

	protected int goTo(int state, char character) {
		if ((state == INITIAL_STATE) && !this.goTo.get(state).containsKey(character)) {
			return 0;
		}
		return this.goTo.get(state).get(character);
	}

	protected List<String> output(int s) {
		if (this.output.containsKey(s)) {
			return this.output.get(s);
		}
		return Collections.emptyList();
	}

	public static interface MatchCallback {

		void onMatch(int startPosition, int endPosition, String matched);
	}
}
