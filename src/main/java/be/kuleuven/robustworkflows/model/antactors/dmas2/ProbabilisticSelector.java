package be.kuleuven.robustworkflows.model.antactors.dmas2;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class ProbabilisticSelector {
	
	private static RandomDataGenerator gen = new RandomDataGenerator(new MersenneTwister());
	
	/**
	 * Selects a number within a list of possibilities, given by specific probabilities.
	 * 
	 * @param probabilities
	 * @return
	 */
	public static int select(double[] probabilities) {
		double[] intervals = new double[probabilities.length + 1];
		
		intervals[0] = 0.0;
		for(int i=0; i<probabilities.length; i++) {
			intervals[i+1] = intervals[i] + probabilities[i]; 
		}
		//check the intervals validity
		if(Math.abs(intervals[intervals.length-1] - 1.0) > 0.000001) {
			throw new RuntimeException("There is a problem with the given probabilities array");
		}
		
		return search(intervals, gen.nextUniform(0.0, 1.0));
	}
	
	private static int search(double[] intervals, double number) {
		if(number < 0.0 || number > 1.0) {
			throw new IllegalArgumentException("Illegal search number");
		}
		
		for(int i=0; i<intervals.length-1; i++) {
			if(number >= intervals[i] && number < intervals[i+1]) {
				return i;
			}
		}
		
		throw new RuntimeException("Impossible to find value");
	}
}
