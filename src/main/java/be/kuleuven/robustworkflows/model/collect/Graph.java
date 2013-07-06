package be.kuleuven.robustworkflows.model.collect;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Graph of nodes of type K
 * 
 * @author mario
 *
 */
public final class Graph<K> {
	private final Multimap<K, K> map; //internal graph representation is an adjacency list
	private K head;
	private boolean empty = true;
	private List<K> visitedNodes;
	
	private Graph() {
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
	
	//FIXME PROBLEM ALERT... this is for a quick hack.. remove as fast as possible
	public Multimap<K, K> raw() {
		return LinkedListMultimap.create(map);
	}
	
	/**
	 * Performs a Depth First Search on the graph 
	 */
	public List<K>  DFS() {
		visitedNodes = Lists.newArrayList(); 
		DFS(head);
		
		return visitedNodes;
	}

	private void DFS(K node) {
		visitedNodes.add(node);
		
		for (K n: map.get(node)) {
			if (!visitedNodes.contains(n) ) {
				DFS(n);
			}
		}
	}

	public static <K> Graph<K> create() {
		return new Graph<K>();
	}
}	
