package be.kuleuven.robustworkflows.model.antactors.reactive;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;

import com.google.common.collect.Lists;

/**
 * Simple data holder of ExplorationReplies
 * 
 * @author mario
 *
 */
public class ExplorationRepliesHolder {

	private final List<ExplorationReplyWrapper> replies;

	private ExplorationRepliesHolder() {
		replies = Lists.newArrayList();
	}
	
	public void add(ExplorationReply explorationReply, ActorRef sender) {
		replies.add(ExplorationReplyWrapper.getInstance(sender, explorationReply));
	}

	public void add(ExplorationReplyWrapper explorationReplyWrapper) {
		System.out.println("Adding ReplyWrapper");
		replies.add(explorationReplyWrapper);
	}
	
	public boolean atLeastNbReplies(int nbReplies) {
		if (replies.size() >= nbReplies) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEmpty() {
		return replies.isEmpty();
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

	public ImmutableExplorationRepliesHolder getImmutableClone() {
		return new ImmutableExplorationRepliesHolder(Lists.newArrayList(replies));
	}
	
	public static ExplorationRepliesHolder getInstance() {
		return new ExplorationRepliesHolder();
	}
}
