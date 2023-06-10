package org.scheduler.onsite.scheduleallocator.adaptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.CrossoverPolicy;

public interface AdaptiveCrossoverPolicy extends CrossoverPolicy {

	ChromosomePair crossover(Chromosome first, Chromosome second, double probability)
			throws MathIllegalArgumentException;
}
