package com.kim.datrie;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author kim 2014年10月21日
 */
public class DoubleArrayTrieTest {

	@Test
	public void testExists() {
		DoubleArrayTrie trie = new DoubleArrayTrie(2);
		trie.insert("你");
		trie.insert("你好");
		TestCase.assertTrue(trie.exists("你"));
		TestCase.assertFalse(trie.exists("我"));
		TestCase.assertTrue(trie.exists("你好"));
		TestCase.assertFalse(trie.exists("你们"));
	}

	@Test
	public void testPrefix() {
		DoubleArrayTrie trie = new DoubleArrayTrie(2);
		trie.insert("你们");
		trie.insert("天安门");
		TestCase.assertTrue(trie.prefix("你"));
		TestCase.assertTrue(trie.prefix("你们"));
		TestCase.assertFalse(trie.prefix("你们好"));
		TestCase.assertTrue(trie.prefix("天"));
		TestCase.assertTrue(trie.prefix("天安"));
		TestCase.assertTrue(trie.prefix("天安门"));
		TestCase.assertFalse(trie.prefix("我"));
	}
}
