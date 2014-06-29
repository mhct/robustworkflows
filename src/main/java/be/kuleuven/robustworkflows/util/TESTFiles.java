package be.kuleuven.robustworkflows.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.antactors.dmas2.DMAS2ExplorationRepliesHolder;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;

public class TESTFiles {
	public static void main(String[] args) throws IOException {
//		Error in ACOSelection, DMAS2ExplorationRepliesHolder 
//		[replies=[ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], 
//		actor=Actor[akka://RobustWorkflows/user/2#424319197]], 
//			ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], actor=Actor[akka://RobustWorkflows/user/2#424319197]], 
//				ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], actor=Actor[akka://RobustWorkflows/user/2#424319197]], 					ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], actor=Actor[akka://RobustWorkflows/user/2#424319197]], ExplorationReplyWrapper [reply=ExplorationReply [computationTime=26000, pheroLevel=0.0], actor=Actor[akka://RobustWorkflows/user/2#424319197]]], alpha=0.5, beta=5.0]
			
		double basePheromoneLevel = 0.000001;
		String acoName = "static";
		int max = 1;
		int bestHeuristics = 1000;
		
		String x = "x = c(";
		String y = "y1 = c(";
		for (int i=1; i<=max; i++) {
			ExplorationReplyWrapper w1 = new ExplorationReplyWrapper(null, new ExplorationReply(null, 26000, 100*i*basePheromoneLevel));
			ExplorationReplyWrapper w2 = new ExplorationReplyWrapper(null, new ExplorationReply(null, bestHeuristics, basePheromoneLevel));
			
			DMAS2ExplorationRepliesHolder holder = DMAS2ExplorationRepliesHolder.getInstance();
			holder.add(w1);
			holder.add(w2);
			
			int w2Selected = 0;
			for (int trial=0; trial<1; trial++) {
				if (holder.acoSelection().getReply().getComputationTime() == bestHeuristics) {
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
		File output = new File("/tmp/aco" + acoName + ".txt");
		Files.write(x, output, Charset.defaultCharset());
		Files.append(y, output, Charset.defaultCharset());
		Files.append("aco" + acoName + " <- data.frame(x=x, y=y1)", output, Charset.defaultCharset());
		
//		assertTrue(resp.isPossible());

		// probabilistic test
		// for 1000 executions it should return w2 more than 50% of the time
		
		
	}

}
