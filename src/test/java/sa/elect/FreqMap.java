package sa.elect;

import java.util.HashMap;

public class FreqMap<K> extends HashMap<K, Integer> {
	@Override
	public Integer get(Object key) {
		if (containsKey(key)) return super.get(key);
		return 0;
	}

	public void increment(K key) {
		put(key, get(key) + 1);
	}

}