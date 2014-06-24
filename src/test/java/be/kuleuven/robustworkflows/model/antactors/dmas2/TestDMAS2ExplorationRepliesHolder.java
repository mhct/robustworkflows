package be.kuleuven.robustworkflows.model.antactors.dmas2;

import static org.junit.Assert.*;

import org.junit.Test;

import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;

public class TestDMAS2ExplorationRepliesHolder {

	@Test
	public void testAddExplorationReplyActorRef() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddExplorationReplyWrapper() {
		fail("Not yet implemented");
	}

	@Test
	public void testAtLeastNbReplies() {
		fail("Not yet implemented");
	}

	@Test
	public void testAcoSelection() {
		ExplorationReplyWrapper w2 = new ExplorationReplyWrapper(null, new ExplorationReply(null, 20001, 0.1));
		ExplorationReplyWrapper w3 = new ExplorationReplyWrapper(null, new ExplorationReply(null, 21000, 0.1));
		ExplorationReplyWrapper w1 = new ExplorationReplyWrapper(null, new ExplorationReply(null, 20000, 10000));
		
		DMAS2ExplorationRepliesHolder holder = DMAS2ExplorationRepliesHolder.getInstance();
		holder.add(w1);
		holder.add(w2);
		holder.add(w3);
		
		ExplorationReplyWrapper resp = holder.acoSelection();
		System.out.println(resp.getReply().getComputationTime());
		
	}

	@Test
	public void testIsEmpty() {
		fail("Not yet implemented");
	}

	@Test
	public void testBestExplorationReply() {
		fail("Not yet implemented");
	}

	@Test
	public void testClear() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetImmutableClone() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInstance() {
		fail("Not yet implemented");
	}

}
