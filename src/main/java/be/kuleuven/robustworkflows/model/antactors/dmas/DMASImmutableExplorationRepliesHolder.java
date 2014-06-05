package be.kuleuven.robustworkflows.model.antactors.dmas;

import java.io.Serializable;
import java.util.List;

import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

public class DMASImmutableExplorationRepliesHolder implements Serializable, Iterable<ExplorationReplyWrapper> {

	private static final long serialVersionUID = 07052014L;
	private final ImmutableList<ExplorationReplyWrapper> replies;
	
	public DMASImmutableExplorationRepliesHolder(List<ExplorationReplyWrapper> replies) {
		this.replies = ImmutableList.copyOf(replies);
	}
	
	@Override
	public UnmodifiableIterator<ExplorationReplyWrapper> iterator() {
		return replies.iterator();
	}

}
