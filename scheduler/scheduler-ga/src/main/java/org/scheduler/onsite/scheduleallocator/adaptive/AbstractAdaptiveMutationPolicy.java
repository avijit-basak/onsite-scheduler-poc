package org.scheduler.onsite.scheduleallocator.adaptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;

public abstract class AbstractAdaptiveMutationPolicy implements AdaptiveMutationPolicy {

	@Override
	public Chromosome mutate(Chromosome paramChromosome) throws MathIllegalArgumentException {
		return null;
	}

}
