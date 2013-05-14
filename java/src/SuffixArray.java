import java.util.*;

public class SuffixArray {

	// suffix array in O(n*log(n))
	public static int[] suffixArray(CharSequence str) {
		int n = str.length();
		Integer[] order = new Integer[n];
		for (int i = 0; i < n; i++)
			order[i] = n - 1 - i;

		// stable sort of characters. java8 lambda syntax
		Arrays.sort(order, (a, b) -> str.charAt(a) - str.charAt(b));

		// sa[i] - suffix on i'th position after sorting by first len characters
		// rank[i] - position of the i'th suffix after sorting by first len characters
		int[] sa = new int[n];
		int[] rank = new int[n];
		for (int i = 0; i < n; i++) {
			sa[i] = order[i];
			rank[i] = str.charAt(i);
		}

		for (int len = 1; len < n; len *= 2) {
			// Suffixes are already sorted by first len characters.
			// Now sort suffixes by first len * 2 characters.
			int[] r = rank.clone();
			rank[sa[0]] = 0;
			for (int i = 1; i < n; i++) {
				int s1 = sa[i - 1];
				int s2 = sa[i];
				rank[s2] = r[s1] == r[s2] && Math.max(s1, s2) + len < n && r[s1 + len / 2] == r[s2 + len / 2] ? rank[s1] : i;
			}
			int[] cnt = new int[n];
			for (int i = 0; i < n; i++)
				cnt[i] = i;
			int[] s = sa.clone();
			for (int i = 0; i < n; i++) {
				// s[i] - order of suffixes sorted by first len characters
				// (s[i] - len) - order of suffixes sorted only by second len characters
				int s1 = s[i] - len;
				if (s1 >= 0)
					sa[cnt[rank[s1]]++] = s1;
			}
		}
		return sa;
	}

	// longest common prefixes array in O(n)
	public static int[] lcp(int[] sa, CharSequence s) {
		int n = sa.length;
		int[] rank = new int[n];
		for (int i = 0; i < n; i++)
			rank[sa[i]] = i;
		int[] lcp = new int[n - 1];
		for (int i = 0, h = 0; i < n; i++) {
			if (rank[i] < n - 1) {
				for (int j = sa[rank[i] + 1]; Math.max(i, j) + h < s.length() && s.charAt(i + h) == s.charAt(j + h); ++h)
					;
				lcp[rank[i]] = h;
				if (h > 0)
					--h;
			}
		}
		return lcp;
	}

	// Usage example
	public static void main(String[] args) {
		String s1 = "abcab";
		int[] sa1 = suffixArray(s1);

		// print suffixes in lexicographic order
		for (int p : sa1)
			System.out.println(s1.substring(p));

		System.out.println("lcp = " + Arrays.toString(lcp(sa1, s1)));

		// random test
		Random rnd = new Random();
		for (int step = 0; step < 100000; step++) {
			int n = rnd.nextInt(100) + 1;
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < n; i++)
				s.append((char) ('\0' + rnd.nextInt(10)));
			int[] sa = suffixArray(s);
			int[] lcp = lcp(sa, s);
			for (int i = 0; i + 1 < n; i++) {
				String a = s.substring(sa[i]);
				String b = s.substring(sa[i + 1]);
				if (a.compareTo(b) >= 0
						|| !a.substring(0, lcp[i]).equals(b.substring(0, lcp[i]))
						|| (a + " ").charAt(lcp[i]) == (b + " ").charAt(lcp[i]))
					throw new RuntimeException("error");
			}
		}
		System.out.println("Test passed");
	}
}
