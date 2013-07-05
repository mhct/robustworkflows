package be.kuleuven.robustworkflows.model.collect;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Adjacency list representing a Graph
 * 
 * @author mario
 *
 */
public final class AdjacencyList<K> {
	private final Multimap<K, K> map;
	private K head;
	private boolean empty = true;
	private List<K> visitedNodes = Lists.newArrayList();
	
	private AdjacencyList() {
		map = ArrayListMultimap.create();
	}
	

	public void put(K key, K value) {
		if (empty) {
			head = key;
			empty = false;
		}
		map.put(key, value);
	}

	public Collection<K> get(K key) {
		return map.get(key);
	}
	
	public K head() {
		return head;
	}
	
	/**
	 * Performs a Depth First Search on the graph 
	 */
	public void  DFS() {
		DFS(head);
	}

	private void DFS(K node) {
		visitedNodes.add(node);
		
		System.out.println(node);
		for (K n: map.get(node)) {
			if (!visitedNodes.contains(n) ) {
				DFS(n);
			}
		}
	}

	public static <K> AdjacencyList<K> create() {
		return new AdjacencyList<K>();
	}
}	
