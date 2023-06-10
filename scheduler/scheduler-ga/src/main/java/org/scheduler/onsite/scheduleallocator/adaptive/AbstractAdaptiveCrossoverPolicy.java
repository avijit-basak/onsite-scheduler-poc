package org.scheduler.onsite.scheduleallocator.adaptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;

public abstract class AbstractAdaptiveCrossoverPolicy implements AdaptiveCrossoverPolicy {

	public ChromosomePair crossover(Chromosome first, Chromosome second) throws MathIllegalArgumentException {
		return null;
	}

}
