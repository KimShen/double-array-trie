package com.kim.datrie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base from https://github.com/dingyaguang117/DoubleArrayTrie
 * 
 * @author kim 2014年10月21日
 */
public class DoubleArrayTrie {

	private final char END_CHAR = '\0';

	private int pos = 1;

	private int count = 0;

	private int base[];

	private int check[];

	private char tail[];

	private Map<Character, Integer> map = new HashMap<Character, Integer>();

	public DoubleArrayTrie(int size) {
		base = new int[size];
		check = new int[size];
		tail = new char[size];
		base[1] = 1;
		map.put(END_CHAR, 1);
	}

	private void extendArray() {
		base = Arrays.copyOf(base, base.length * 2);
		check = Arrays.copyOf(check, check.length * 2);
	}

	private void extendsTail() {
		tail = Arrays.copyOf(tail, tail.length * 2);
	}

	private int charCode(char c) {
		Integer code = map.get(c);
		return code != null ? code : compute(c, count++);
	}

	private int compute(char c, int count) {
		if (!map.containsKey(c)) {
			map.put(c, count);
		}
		return count;
	}

	private int copy2TailArray(String s, int p) {
		int pos = this.pos;
		while (s.length() - p + 1 > tail.length - pos) {
			extendsTail();
		}
		for (int i = p; i < s.length(); ++i) {
			tail[pos] = s.charAt(i);
			pos++;
		}
		return pos;
	}

	private int check(Integer[] set) {
		for (int i = 1;; ++i) {
			boolean flag = true;
			for (int j = 0; j < set.length; ++j) {
				int cur_p = i + set[j];
				if (cur_p >= base.length)
					extendArray();
				if (base[cur_p] != 0 || check[cur_p] != 0) {
					flag = false;
					break;
				}
			}
			if (flag)
				return i;
		}
	}

	private ArrayList<Integer> childList(int p) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i = 1; i <= map.size(); ++i) {
			if (base[p] + i >= check.length)
				break;
			if (check[base[p] + i] == p) {
				ret.add(i);
			}
		}
		return ret;
	}

	private boolean tailContainString(int start, String s2) {
		for (int i = 0; i < s2.length(); ++i) {
			if (s2.charAt(i) != tail[i + start])
				return false;
		}
		return true;
	}

	private boolean tailMatchString(int start, String s2) {
		s2 += END_CHAR;
		for (int i = 0; i < s2.length(); ++i) {
			if (s2.charAt(i) != tail[i + start])
				return false;
		}
		return true;
	}

	public void insert(List<String> s) {
		for (String each : s) {
			this.insert(each);
		}
	}

	public void insert(String s) {
		s += END_CHAR;
		int pre_p = 1;
		int cur_p;
		for (int i = 0; i < s.length(); ++i) {
			// 获取状态位置
			cur_p = base[pre_p] + charCode(s.charAt(i));
			// 如果长度超过现有，拓展数组
			if (cur_p >= base.length)
				extendArray();
			// 空闲状态
			if (base[cur_p] == 0 && check[cur_p] == 0) {
				base[cur_p] = -pos;
				check[cur_p] = pre_p;
				pos = copy2TailArray(s, i + 1);
				break;
			} else
			// 已存在状态
			if (base[cur_p] > 0 && check[cur_p] == pre_p) {
				pre_p = cur_p;
				continue;
			} else
			// 冲突 1：遇到 Base[cur_p]小于0的，即遇到一个被压缩存到Tail中的字符串
			if (base[cur_p] < 0 && check[cur_p] == pre_p) {
				int head = -base[cur_p];
				// 插入重复字符串
				if (s.charAt(i + 1) == END_CHAR && tail[head] == END_CHAR) {
					break;
				}
				// 公共字母的情况，因为上一个判断已经排除了结束符，所以一定是2个都不是结束符
				if (tail[head] == s.charAt(i + 1)) {
					int avail_base = check(new Integer[] { charCode(s.charAt(i + 1)) });
					base[cur_p] = avail_base;
					check[avail_base + charCode(s.charAt(i + 1))] = cur_p;
					base[avail_base + charCode(s.charAt(i + 1))] = -(head + 1);
					pre_p = cur_p;
					continue;
				} else {
					// 2个字母不相同的情况，可能有一个为结束符
					int avail_base;
					avail_base = check(new Integer[] { charCode(s.charAt(i + 1)), charCode(tail[head]) });
					base[cur_p] = avail_base;
					check[avail_base + charCode(tail[head])] = cur_p;
					check[avail_base + charCode(s.charAt(i + 1))] = cur_p;
					// Tail 为END_FLAG 的情况
					if (tail[head] == END_CHAR)
						base[avail_base + charCode(tail[head])] = 0;
					else
						base[avail_base + charCode(tail[head])] = -(head + 1);
					if (s.charAt(i + 1) == END_CHAR)
						base[avail_base + charCode(s.charAt(i + 1))] = 0;
					else
						base[avail_base + charCode(s.charAt(i + 1))] = -pos;
					pos = copy2TailArray(s, i + 2);
					break;
				}
			} else
			// 冲突2：当前结点已经被占用，需要调整pre的base
			if (check[cur_p] != pre_p) {
				ArrayList<Integer> list1 = childList(pre_p);
				int toBeAdjust;
				ArrayList<Integer> list = null;
				if (true) {
					toBeAdjust = pre_p;
					list = list1;
				}
				int origin_base = base[toBeAdjust];
				list.add(charCode(s.charAt(i)));
				int avail_base = check((Integer[]) list.toArray(new Integer[list.size()]));
				list.remove(list.size() - 1);
				base[toBeAdjust] = avail_base;
				for (int j = 0; j < list.size(); ++j) {
					// BUG
					int tmp1 = origin_base + list.get(j);
					int tmp2 = avail_base + list.get(j);
					base[tmp2] = base[tmp1];
					check[tmp2] = check[tmp1];
					// 有后续
					if (base[tmp1] > 0) {
						ArrayList<Integer> subsequence = childList(tmp1);
						for (int k = 0; k < subsequence.size(); ++k) {
							check[base[tmp1] + subsequence.get(k)] = tmp2;
						}
					}
					base[tmp1] = 0;
					check[tmp1] = 0;
				}
				// 更新新的cur_p
				cur_p = base[pre_p] + charCode(s.charAt(i));
				if (s.charAt(i) == END_CHAR)
					base[cur_p] = 0;
				else
					base[cur_p] = -pos;
				check[cur_p] = pre_p;
				pos = copy2TailArray(s, i + 1);
				break;
			}
		}
	}

	public boolean exists(String word) {
		int pre_p = 1;
		int cur_p = 0;
		for (int i = 0; i < word.length(); ++i) {
			cur_p = base[pre_p] + charCode(word.charAt(i));
			if (cur_p < check.length && check[cur_p] != pre_p)
				return false;
			if (cur_p < base.length && base[cur_p] < 0) {
				if (tailMatchString(-base[cur_p], word.substring(i + 1)))
					return true;
				return false;
			}
			pre_p = cur_p;
		}
		if (cur_p < base.length && (base[cur_p] + charCode(END_CHAR) < check.length) && check[base[cur_p] + charCode(END_CHAR)] == cur_p)
			return true;
		return false;
	}

	private int find(String word) {
		int pre_p = 1;
		int cur_p = 0;
		for (int i = 0; i < word.length(); ++i) {
			// BUG
			cur_p = base[pre_p] + charCode(word.charAt(i));
			if (cur_p < check.length && check[cur_p] != pre_p) {
				return -1;
			}
			if (cur_p < base.length && base[cur_p] < 0) {
				if (tailContainString(-base[cur_p], word.substring(i + 1))) {
					return cur_p;
				}
				return -1;
			}
			pre_p = cur_p;
		}
		return cur_p;
	}

	public boolean prefix(String word) {
		int p = this.find(word);
		return (p < base.length && p != -1) ? true : false;
	}
}