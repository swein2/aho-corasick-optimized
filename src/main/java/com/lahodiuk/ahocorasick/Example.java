package com.lahodiuk.ahocorasick;

public class Example {

	public static void main(String[] args) {

		AhoCorasickOptimized ac = new AhoCorasickOptimized("he", "she", "his", "hers");

		String text1 = "ushers";
		System.out.println("Matching inside text 1: " + text1);
		ac.match(text1, (start, end, found) -> System.out.printf("%s: [%d..%d]%n", found, start, end));
		System.out.println();

		String text2 = "ushers ushers ushers ushers ushers abc 123 xyz he she";
		System.out.println("Matching inside text 2: " + text2);
		ac.match(text2, (start, end, found) -> System.out.printf("%s: [%d..%d]%n", found, start, end));
		System.out.println();

		System.out.println("Graphviz structure of automaton:");
		ac.generateGraphvizAutomatonRepresentation(false);
	}
}
