package be.kuleuven.robustworkflows.model.clientagent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import com.google.common.collect.Lists;

public class ClientAgentTest {

	@Test
	public void testEvaluateComposition() {
		
		ExplorationResult er1 = mock(ExplorationResult.class);
		when(er1.totalComputationTime()).thenReturn(100l);

		ExplorationResult er2 = mock(ExplorationResult.class);
		when(er2.totalComputationTime()).thenReturn(101l);
		
		ExplorationResult selected = evaluateComposition(Lists.newArrayList(er1, er2));
		
//		assertEquals(er1, selected);
		System.out.println(selected.totalComputationTime());
		
	}
//TODO added this algorithm here, only to test if it was ok.. it seems so.. 
	
	public ExplorationResult evaluateComposition(List<ExplorationResult> replies) {
		
		Collections.sort(replies, new Comparator<ExplorationResult>() {

			@Override
			public int compare(ExplorationResult o1, ExplorationResult o2) {
				final long o1Time = o1.totalComputationTime();
				final long o2Time = o2.totalComputationTime();
				
				if (o1Time < o2Time) {
					return -1;
				} else if (o2Time > o2Time) {
					return 1;
				}
					
				return 0;
			}
			
		});
		
		assert replies.get(0) != null;
		return replies.get(0);
	}

}
