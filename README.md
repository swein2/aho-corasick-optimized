# aho-corasick-optimized
Implementation of the [Aho-Corasick string matching algorithm](https://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_algorithm), which supposed to be Garbage Collector friendly. 
**The automaton implemented using only the primitive data types in order to avoid Autoboxing and Unboxing conversions.**

# Usage
This implementation is self-sufficient.
You can directly copy this class to your project: https://github.com/lagodiuk/aho-corasick-optimized/blob/master/src/main/java/com/lahodiuk/ahocorasick/AhoCorasickOptimized.java

```java
public class Example {

	public static void main(String[] args) {
	
		// Construction of the the automaton
		AhoCorasickOptimized ac = new AhoCorasickOptimized("he", "she", "his", "hers");

		// The same automaton can be used for matching inside different texts
		String text1 = "ushers";
		System.out.println("\n Matching inside text 1: " + text1);
		// In case, when the pattern is found - the callback will be activated:
		ac.match(text1, (start, end, found) -> System.out.printf("%s: [%d..%d]%n", found, start, end));

		// Example of finding substrings inside another text
		String text2 = "ushers ushers ushers ushers ushers abc 123 xyz he she";
		System.out.println("\n Matching inside text 2: " + text2);
		ac.match(text2, (start, end, found) -> System.out.printf("%s: [%d..%d]%n", found, start, end));

		// The structure of automaton can be visualized using Graphviz format
		System.out.println("\n Graphviz structure of automaton:");
		ac.generateGraphvizAutomatonRepresentation(false);
	}
}
```

## The generated automaton can be visualized using Graphviz software
![Graphviz structure of automaton](https://raw.githubusercontent.com/lagodiuk/aho-corasick-optimized/master/img/automaton.png)

# Reference
The Aho-Corasick string matching algorithm, described in the paper: *"Efficient String Matching: An Aid to Bibliographic Search"*, Alfred V. Aho, Margaret J. Corasick, Bell Laboratories, 1975
