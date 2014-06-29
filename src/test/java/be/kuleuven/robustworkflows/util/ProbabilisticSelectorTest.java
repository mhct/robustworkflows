package be.kuleuven.robustworkflows.util;

import static org.junit.Assert.*;

import org.apache.commons.math3.distribution.EnumeratedRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Test;

public class ProbabilisticSelectorTest {


	@Test
	public void testSelectSimple2() {
		double[] probabilities = {0.5, 0.5};
		double expectedAvg = 0.5;
		
		double res = 0.0;
		double sample = 0.0;
		for (int i=0; i<100000; i++) {
			sample = ProbabilisticSelector.select(probabilities);
			if (sample == 0) {
				res++; 
			}
		}
		
		res = res/100000;
		
		assertEquals(expectedAvg, res, 0.01);
	}

	@Test
	public void testSelectSimple1() {
		double[] probabilities = {0.1, 0.9};
		double expectedAvg = 0.1;
		
		double res = 0.0;
		double sample = 0.0;
		for (int i=0; i<1000; i++) {
			sample = ProbabilisticSelector.select(probabilities);
			if (sample == 0) {
				res++; 
			}
		}
		
		res = res/1000;
		
		assertEquals(expectedAvg, res, 0.001);
	}

	@Test
	public void testSelectSimple3() {
		final double[] results = {0.0, 1.0};
		final double[] probabilities = {0.1, 0.9};
		final double expectedAvg = 0.1;
		
		EnumeratedRealDistribution d = new EnumeratedRealDistribution(new MersenneTwister(), results, probabilities);
		double res = 0.0;
		double sample = 0.0;
		for (int i=0; i<1000; i++) {
//			sample = ProbabilisticSelector.select(probabilities);
			sample = d.sample();
			System.out.println(sample);
			if (sample == 0.0) {
				res++; 
			}
		}
		
		res = res/1000;
		
		assertEquals(expectedAvg, res, 0.001);
	}

}
