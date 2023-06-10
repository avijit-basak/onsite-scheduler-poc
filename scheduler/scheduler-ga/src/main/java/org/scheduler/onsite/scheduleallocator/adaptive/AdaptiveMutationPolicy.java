package org.scheduler.onsite.scheduleallocator.adaptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

public interface AdaptiveMutationPolicy extends MutationPolicy {

	public Chromosome mutate(Chromosome chromosome, double probability) throws MathIllegalArgumentException;

}
