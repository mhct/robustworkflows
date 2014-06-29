package be.kuleuven.robustworkflows.model.antactors.dmas2;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;

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
	public void testAcoSelection0() {
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
	public void testAcoSelection1() throws IOException {
//		Error in ACOSelection, DMAS2ExplorationRepliesHolder 
//		[replies=[ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], 
//		actor=Actor[akka://RobustWorkflows/user/2#424319197]], 
//			ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], actor=Actor[akka://RobustWorkflows/user/2#424319197]], 
//				ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], actor=Actor[akka://RobustWorkflows/user/2#424319197]], 					ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], actor=Actor[akka://RobustWorkflows/user/2#424319197]], ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], actor=Actor[akka://RobustWorkflows/user/2#424319197]]], alpha=0.5, beta=5.0]
			
		double basePheromoneLevel = 0.1;
		int max = 10;
		
		String x = "x = c(";
		String y = "y1 = c(";
		for (int i=1; i<=max; i++) {
			ExplorationReplyWrapper w1 = new ExplorationReplyWrapper(null, new ExplorationReply(null, 26000, i * basePheromoneLevel));
			ExplorationReplyWrapper w2 = new ExplorationReplyWrapper(null, new ExplorationReply(null, 20000, basePheromoneLevel));
			
			DMAS2ExplorationRepliesHolder holder = DMAS2ExplorationRepliesHolder.getInstance();
			holder.add(w1);
			holder.add(w2);
			
			int w2Selected = 0;
			for (int trial=0; trial<1000; trial++) {
				if (holder.acoSelection().getReply().getComputationTime() == 20000 ) {
					w2Selected++;
				}
			}
			String delimiter = "";
			if (i != max) {
				delimiter = ",";
			} else {
				delimiter = ");";
			}
			
			x += i + delimiter;
			y += w2Selected/1000.0 + delimiter;
		}
		System.out.println(
			    Iterables.class.getProtectionDomain().getCodeSource().getLocation()
			);
		File output = new File("/tmp/aco.txt");
		Files.write("BBBB", output, Charset.defaultCharset());
//		Files.append(y, output, Charset.defaultCharset());
		
//		assertTrue(resp.isPossible());

		// probabilistic test
		// for 1000 executions it should return w2 more than 50% of the time
		
		
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
