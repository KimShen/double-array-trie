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
public class SimpleTrieFilterTest {

	private final List<String> trie = new ArrayList<String>();

	private final String content;

	public SimpleTrieFilterTest() throws Exception {
		super();
		this.content = IOUtils.toString(SimpleTrieFilterTest.class.getResourceAsStream("War and Peace.txt"), "UTF-8");
		for (String each : IOUtils.readLines(new FileReader(new File(SimpleTrieFilterTest.class.getResource("Words.txt").getFile())))) {
			this.trie.add(each.trim());
		}
	}

	@Test
	public void testFilter() throws Exception {
		TrieCounter after = new SimpleTrieFilter("*", this.trie).filter(this.content);
		int total = 0;
		int index = -1;
		while ((index = after.source().indexOf("*", index + 1)) != -1) {
			total++;
		}
		TestCase.assertSame(after.filtered(), total);
		TestCase.assertTrue(after.source().length() < this.content.length());
	}

	@Test
	public void testFilterSingleWord() throws Exception {
		List<String> trie = new ArrayList<String>();
		trie.add("亲");
		trie.add("你");
		trie.add("你好");
		TrieCounter after = new SimpleTrieFilter("*", trie).filter("亲爱的你,你好吗");
		TestCase.assertSame(3, after.filtered());
	}

	@Test
	public void testFilterSequenceWord() throws Exception {
		TrieCounter after = new SimpleTrieFilter("*", this.trie).filter("你把把邓小平怎么样了？辦證上哪儿去办？卖枪网站的地址是什么？枪决女犯枪决现场足球世界杯！");
		int total = 0;
		int index = -1;
		while ((index = after.source().indexOf("*", index + 1)) != -1) {
			total++;
		}
		TestCase.assertSame(after.filtered(), total);
	}
}
