package be.kuleuven.robustworkflows.model.collect;

import java.io.Serializable;
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
public final class Graph<K> implements Serializable {
	private static final long serialVersionUID = 20131024L;
	
	private final Multimap<K, K> map; //internal graph representation is an adjacency list
	private K head;
	private boolean empty = true;
	private List<K> visitedNodes;
	
	private Graph() {
		map = ArrayListMultimap.create();
	}
	

	public void put(K key, K value) {
		if (key == null || value == null) {
			throw new RuntimeException("Key or Value can't be null");
		}
		
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
		if (head != null) {
			DFS(head);
		}
		
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (empty ? 1231 : 1237);
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Graph other = (Graph) obj;
		if (empty != other.empty)
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}
	
	
}	
