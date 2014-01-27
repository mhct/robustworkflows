package be.kuleuven.robustworkflows.util;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table.Cell;

public class StateMachineToDot {
	private static final String NL = System.getProperty("line.separator");
	  private static final String NODE = "node";
	  private static final String NODE_DEFINITION = "[label=\"\",shape=point]" + NL;
	  private static final String CONN = " -> ";
	  private static final String LABEL_OPEN = "[label=\"";
	  private static final String LABEL_CLOSE = "\"]" + NL;
	  private static final String FILE_OPEN = "digraph stategraph {" + NL;
	  private static final String FILE_CLOSE = "}";

	/**
	   * @param <E>
	 * @param <C>
	 * @return A dot representation of the state machine, can be used for
	   *         debugging the transition table.
	   */
	  public static <E,C> String convert(StateMachine<E, C> fsm) {
		  final ImmutableTable<State<E, C>, E, State<E, C>> transitionTable = fsm.transitionTable;
		  final State<E,C> startState = fsm.startState;
		  
	    int id = 0;
	    final StringBuilder builder = new StringBuilder();
	    builder.append(FILE_OPEN);
	    final Set<State<E, C>> allStates = newHashSet();
	    allStates.addAll(transitionTable.rowKeySet());
	    allStates.addAll(transitionTable.values());
	    final Map<State<E, C>, Integer> idMap = newHashMap();
	    for (final State<E, C> s : allStates) {
	      builder.append(NODE).append(id).append(LABEL_OPEN).append(s.name())
	          .append(LABEL_CLOSE);
	      idMap.put(s, id);
	      id++;
	    }
	    builder.append(NODE).append(id).append(NODE_DEFINITION);
	    builder.append(NODE).append(id).append(CONN).append(NODE)
	        .append(idMap.get(startState)).append(NL);

	    for (final Cell<State<E, C>, E, State<E, C>> cell : transitionTable.cellSet()) {
	      final int id1 = idMap.get(cell.getRowKey());
	      final int id2 = idMap.get(cell.getValue());
	      builder.append(NODE).append(id1).append(CONN).append(NODE).append(id2)
	          .append(LABEL_OPEN).append(cell.getColumnKey()).append(LABEL_CLOSE);
	    }
	    builder.append(FILE_CLOSE);
	    return builder.toString();
	  }
}
