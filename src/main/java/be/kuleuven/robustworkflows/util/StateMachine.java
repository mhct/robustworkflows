package be.kuleuven.robustworkflows.util;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;

/**
 * A simple state machine. The state machine is represented by a transition
 * table. A transition has the form <code>current state + event </code> &rarr;
 * <code>new state</code>. A transition can be recurrent, meaning that an event
 * will <i>not</i> initiate a transition. Events can be initiated from within a
 * state by means of the {@link State#handle(Object, Object)} method or from
 * outside the state machine by means of the {@link #handle(Object, Object)}. An
 * attempt to perform a transition not present in the transition table result in
 * a {@link RuntimeException}. Note that the transition table is immutable.
 * StateMachine instances can only be created using its builder via the
 * {@link #create(State)} method.
 * 
 * @param <E> The event type. Concrete event objects that describe the same
 *          event should be <i>equal</i> (according to {@link #equals(Object)} )
 *          and their {@link #hashCode()} implementation should return the same
 *          value. If the events do not need to contain additional meta data,
 *          {@link Enum}s are the best choice.
 * @param <C> The context type. This is typically the object that contains the
 *          {@link StateMachine}, a {@link State} represents a state of this
 *          object.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 */
public class StateMachine<E, C> {
  
  /**
   * The transition table which defines the allowed transitions.
   */
  protected ImmutableTable<State<E, C>, E, State<E, C>> transitionTable;
  /**
   * The current state.
   */
  protected State<E, C> currentState;
  /**
   * The initial state.
   */
  protected final State<E, C> startState;

  StateMachine(State<E, C> start,
      ImmutableTable<State<E, C>, E, State<E, C>> table) {
    startState = start;
    currentState = start;
    transitionTable = table;
  }

  /**
   * Gives the current {@link State} time to update.
   * @param context Reference to the context.
   */
  public void handle(C context) {
    handle(null, context);
  }

  /**
   * Handle the specified event.
   * @param event The event that needs to be handled by the state machine. If
   *          this results in an attempt to perform a transition which is not
   *          allowed an {@link IllegalArgumentException} is thrown.
   * @param context Reference to the context.
   */
  public void handle(@Nullable E event, C context) {
    if (event != null) {
      changeState(event, context);
    }
    @Nullable
    final E newEvent = currentState.handle(event, context);
    if (newEvent != null) {
      handle(newEvent, context);
    }
  }

  /**
   * Perform a state change if possible.
   * @param event The event that may initiate a state change.
   * @param context Reference to the context.
   */
  protected void changeState(E event, C context) {
    checkArgument(transitionTable.contains(currentState, event),
        "The event %s is not supported when in state %s.", event, currentState);
    final State<E, C> newState = transitionTable.get(currentState, event);
    if (!newState.equals(currentState)) {
      currentState.onExit(event, context);

      currentState = newState;
      currentState.onEntry(event, context);
    }
  }

  /**
   * @return A reference to the current state of this {@link StateMachine}.
   */
  public State<E, C> getCurrentState() {
    return currentState;
  }

  /**
   * Convenience method for checking whether the current state is the same as
   * the specified state.
   * @param s The state to be checked.
   * @return <code>true</code> when the states are the same object,
   *         <code>false</code> otherwise.
   */
  public boolean stateIs(State<E, C> s) {
    return currentState.equals(s);
  }

  /**
   * Convenience method for checking whether the current state is one of the
   * specified states.
   * @param states The states to be checked.
   * @return <code>true</code> when the current state is one of the specified
   *         states, <code>false</code> otherwise.
   */
  public boolean stateIsOneOf(State<E, C>... states) {
    for (final State<E, C> s : states) {
      if (stateIs(s)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return An {@link ImmutableCollection} of all states in this state machine.
   */
  public ImmutableCollection<State<E, C>> getStates() {
	  return new ImmutableSet.Builder<State<E, C>>().addAll(transitionTable.values()).build();
  }

  /**
   * Looks up a state of the specified (sub)type if it exists. If there exist
   * multiple the first encountered is returned.
   * @param type The (sub)type to look for.
   * @return The state of the specified type.
   * @throws IllegalArgumentException if there is no state of the specified
   *           type.
   */
  public <T> T getStateOfType(Class<T> type) {
    for (final State<E, C> state : getStates()) {
      if (type.isInstance(state)) {
        return type.cast(state);
      }
    }
    throw new IllegalArgumentException("There is no instance of " + type
        + " in this state machine.");
  }

  /**
   * Returns true if the current state supports the event.
   * @param event The event to check.
   * @return <code>true</code> when the specified event is supported by the
   *         current state, <code>false</code> otherwise.
   */
  public boolean isSupported(E event) {
    return transitionTable.contains(currentState, event);
  }


  /**
   * Create a new {@link StateMachine} instance with the specified initial
   * state. This method returns a reference to the {@link StateMachineBuilder}
   * which allows for adding of transitions to the state machine.
   * @param initialState The start state of the state machine.
   * @param <E> The event type.
   * @param <C> The context type.
   * @return A reference to the {@link StateMachineBuilder} which is used for
   *         creating the {@link StateMachine}.
   */
  public static <E, C> StateMachineBuilder<E, C> create(State<E, C> initialState) {
    return new StateMachineBuilder<E, C>(initialState);
  }

  /**
   * Facilitates the creation of a {@link StateMachine}.
   * @param <E> Event parameter of {@link StateMachine}.
   * @param <C> Context parameter of {@link StateMachine}.
   * @see StateMachine
   */
  public static final class StateMachineBuilder<E, C> {
    private final ImmutableTable.Builder<State<E, C>, E, State<E, C>> tableBuilder;
    private final State<E, C> start;

    StateMachineBuilder(State<E, C> initialState) {
      tableBuilder = ImmutableTable.builder();
      start = initialState;
    }

    /**
     * Add a transition: <code>state + event &rarr; new state</code>.
     * @param from The from state.
     * @param event The event which triggers the transition.
     * @param to The destination of the transition, the new state.
     * @return A reference to this for method chaining.
     */
    public StateMachineBuilder<E, C> addTransition(State<E, C> from, E event,
        State<E, C> to) {
      tableBuilder.put(from, event, to);
      return this;
    }

    /**
     * Adds all transitions in the specified {@link StateMachine} to this
     * builder. Duplicates are not allowed.
     * @param sm The {@link StateMachine} from which the transitions are copied.
     * @return The builder reference.
     */
    public StateMachineBuilder<E, C> addTransitionsFrom(StateMachine<E, C> sm) {
      tableBuilder.putAll(sm.transitionTable);
      return this;
    }

    /**
     * Builds the {@link StateMachine} as configured by this
     * {@link rinde.sim.util.fsm.StateMachine.StateMachineBuilder}.
     * @return The {@link StateMachine}.
     */
    public StateMachine<E, C> build() {
      return new StateMachine<E, C>(start, tableBuilder.build());
    }
  }
}