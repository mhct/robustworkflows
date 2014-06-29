package be.kuleuven.robustworkflows.model.antactors.dmas2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.util.ProbabilisticSelector;

import com.google.common.collect.Lists;

/**
 * Simple data holder of ExplorationReplies
 * 
 * @author mario
 *
 */
public class DMAS2ExplorationRepliesHolder {

	private final List<ExplorationReplyWrapper> replies;
	public static final double MINIMUM_PHEROMONE = 0.1;
	private double alpha = 1.0;
	private double beta = 1.0;
	
	private DMAS2ExplorationRepliesHolder() {
		replies = Lists.newArrayList();
	}
	
	public void add(ExplorationReply explorationReply, ActorRef sender) {
		replies.add(ExplorationReplyWrapper.getInstance(sender, explorationReply));
	}

	public void add(ExplorationReplyWrapper explorationReplyWrapper) {
		replies.add(explorationReplyWrapper);
	}
	
	public boolean atLeastNbReplies(int nbReplies) {
		if (replies.size() >= nbReplies) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Selects direction using ACO heuristic. described by dorigo1999ant "Ant Algorithms for Discrete Optimization (article) 
	 * Marco Dorigo and Gianni Di Caro and Luca Maria Gambardella
	 * 
	 * More specifically the ACS heuristic
	 */
	public ExplorationReplyWrapper acoSelection() {
		List<ExplorationReplyWrapper> repliesCopy = new ArrayList<ExplorationReplyWrapper>(replies);
		if (repliesCopy.size() == 0) {
			return ExplorationReplyWrapper.empty();
		} else if (repliesCopy.size() == 1) {
			return repliesCopy.get(0);
		} else {
			
			sort(repliesCopy);
			double[] probabilities = calculateProbabilities(repliesCopy);
			System.out.println(probabilities[0] + " - " + probabilities[1]);
			return repliesCopy.get(ProbabilisticSelector.select(probabilities));
		}
	}
	
	/**
	 * Calculates the probability to follow the path indicated by each pheromonelevel
	 * 
	 */
	private double[] calculateProbabilities(List<ExplorationReplyWrapper> replies) {
		double[] probabilities = new double[replies.size()];
		
		double divisor = divisor(replies);
		for(int i=0; i<probabilities.length; i++) {
			probabilities[i] = calculateMulti(replies.get(i)) / divisor; 
		}
		
		return probabilities;
	}

	private double divisor(List<ExplorationReplyWrapper> replies) {
		double sum = 0.0;
		for(int i=0; i<replies.size(); i++) {
			sum += calculateMulti(replies.get(i));
		}
		
		return sum;
	}
	
	private double calculateMulti(ExplorationReplyWrapper pheromoneLevel) {
		return Math.pow(pheromoneLevel.getLevel(), alpha ) * Math.pow(heuristic(pheromoneLevel.getReply().getComputationTime()), beta );
	}
	
	/**
	 * The heuristic calculation depends on the domain that is used. but all the values should be between specified limits.
	 * Current implementation returns the inverse of the response time. (we want short response times)
	 * @param agent
	 * @return
	 */
	private double heuristic(double responseTime) {
		return 1.0 / responseTime;
	}

	
	public boolean isEmpty() {
		return replies.isEmpty();
	}

	private void sort(List<ExplorationReplyWrapper> repliesWrapper) {
		Collections.sort(repliesWrapper, new Comparator<ExplorationReplyWrapper>() {
			
			@Override
			public int compare(ExplorationReplyWrapper obj1, ExplorationReplyWrapper obj2) {
				final long o1 = obj1.getReply().getComputationTime();
				final long o2 = obj2.getReply().getComputationTime();

				if (o1 < o2) {
					return -1;
				} else if (o1 > o2) {
					return 1;
				}
					
				return 0;
			}
			
		});
	
	}
	
	public ExplorationReplyWrapper bestExplorationReply() {
		if (replies.size() == 0) {
			return ExplorationReplyWrapper.empty();
		} else if (replies.size() == 1) {
			return replies.get(0);
		} else {
			Collections.sort(replies, new Comparator<ExplorationReplyWrapper>() {
	
				@Override
				public int compare(ExplorationReplyWrapper obj1, ExplorationReplyWrapper obj2) {
					final long o1 = obj1.getReply().getComputationTime();
					final long o2 = obj2.getReply().getComputationTime();
					
					if (o1 < o2) {
						return -1;
					} else if (o1 > o2) {
						return 1;
					}
						
					return 0;
				}
				
			});
		
			return replies.get(0);
		}
	}
	
	public void clear() {
		replies.clear();
	}

	public DMAS2ImmutableExplorationRepliesHolder getImmutableClone() {
		return new DMAS2ImmutableExplorationRepliesHolder(Lists.newArrayList(replies));
	}
	
	public static DMAS2ExplorationRepliesHolder getInstance() {
		return new DMAS2ExplorationRepliesHolder();
	}

	@Override
	public String toString() {
		return "DMAS2ExplorationRepliesHolder [replies=" + replies + ", alpha="
				+ alpha + ", beta=" + beta + "]";
	}
	
	
}
