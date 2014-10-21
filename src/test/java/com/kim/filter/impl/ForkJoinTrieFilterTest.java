package com.kim.filter.impl;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.kim.filter.TrieCounter;

/**
 * @author kim 2014年9月2日
 */
public class ForkJoinTrieFilterTest {

	private final List<String> trie = new ArrayList<String>();

	private final String content;

	public ForkJoinTrieFilterTest() throws Exception {
		super();
		this.content = IOUtils.toString(SimpleTrieFilterTest.class.getResourceAsStream("War and Peace.txt"), "UTF-8");
		for (String each : IOUtils.readLines(new FileReader(new File(SimpleTrieFilterTest.class.getResource("Words.txt").getFile())))) {
			this.trie.add(each.trim());
		}
	}

	@Test
	public void testFilter() throws Exception {
		TrieCounter after = new ForkJoinTrieFilter(1024, "*", this.trie).filter(this.content);
		int total = 0;
		for (int index = 0; index != -1; total++) {
			index = after.source().indexOf("*", index + 1);
		}
		TestCase.assertSame(16, total);
		TestCase.assertSame(16, after.filtered());
		TestCase.assertTrue(after.source().length() < this.content.length());
	}

	@Test
	public void testFilterSingleWord() throws Exception {
		List<String> trie = new ArrayList<String>();
		trie.add("亲");
		trie.add("你");
		trie.add("你好");
		TrieCounter after = new ForkJoinTrieFilter(1024, "*", trie).filter("亲爱的你,你好吗");
		TestCase.assertSame(3, after.filtered());
	}
}
