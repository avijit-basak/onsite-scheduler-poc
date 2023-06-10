package org.scheduler.onsite.scheduleallocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.apache.commons.math3.genetics.RandomKey;
import org.scheduler.onsite.model.Worker;

public class ScheduleChromosome extends RandomKey<Integer> {

	private List<Worker> workers;

	private final FitnessFunction fitnessFunction;

	private int rank;

	public ScheduleChromosome(List<Double> representation, List<Worker> workers, FitnessFunction fitnessFunction)
			throws InvalidRepresentationException {
		super(new ArrayList<>(representation));
		this.workers = new ArrayList<>(workers);
		this.fitnessFunction = fitnessFunction;
	}

	public List<Worker> getWorkers() {
		return workers;
	}

	public FitnessFunction getFitnessFunction() {
		return fitnessFunction;
	}

	@Override
	public double fitness() {
		return fitnessFunction.evaluate(this);
	}

	@Override
	public List<Double> getRepresentation() {
		return super.getRepresentation();
	}

	@Override
	public ScheduleChromosome newFixedLengthChromosome(List<Double> chromosomeRepresentation) {
		return new ScheduleChromosome(chromosomeRepresentation, this.workers, this.fitnessFunction);
	}

	public ScheduleChromosome newFixedLengthChromosome(List<Double> chromosomeRepresentation, List<Worker> workers) {
		return new ScheduleChromosome(chromosomeRepresentation, workers, this.fitnessFunction);
	}

//	public Iterator<Worker> iterateJobWorkers() {
//		return getRepresentation().iterator();
//	}
//
//	public Worker getWorkerForJob(int jobIndex) {
//		return getRepresentation().get(jobIndex);
//	}

	@Override
	public String toString() {
		StringBuilder chromosomeStr = new StringBuilder();
		chromosomeStr.append("\r\n");
		chromosomeStr.append("************************************************************");
		chromosomeStr.append("Fitness : " + getFitness());
		chromosomeStr.append("\r\n");
		Map<Integer, List<Integer>> workerToJobIdMap = new HashMap<>();
		List<Integer> vals = IntStream.rangeClosed(0, this.getLength() - 1).boxed().collect(Collectors.toList());
		List<Integer> jobSequences = decode(vals);
		StringBuilder jobSeqStr = new StringBuilder();
//		jobSeqStr.append("   JobSeq:");
		for (int i = 0; i < getLength(); i++) {
			Integer jobSeq = jobSequences.get(i);
			Integer workerSeq = workers.get(jobSequences.get(i)).getSequence();
			if (!workerToJobIdMap.containsKey(workerSeq)) {
				List<Integer> jobIds = new ArrayList<>();
				jobIds.add(jobSeq);
				workerToJobIdMap.put(workerSeq, jobIds);
			} else {
				workerToJobIdMap.get(workerSeq).add(jobSeq);
			}
//			if (jobSeq < 10) {
//				jobSeqStr.append("| " + jobSeq + "|");
//			} else {
//				jobSeqStr.append("|" + jobSeq + "|");
//			}
		}
		chromosomeStr.append(workerToJobIdMap.toString());
//		chromosomeStr.append(jobSeqStr);
		chromosomeStr.append("\r\n");
//		StringBuilder workerSeqStr = new StringBuilder();
//		workerSeqStr.append("WorkerSeq:");
//		for (int i = 0; i < getLength(); i++) {
//			int workerSeq = workers.get(jobSequences.get(i)).getSequence();
//			if (workerSeq < 10) {
//				workerSeqStr.append("| " + workerSeq + "|");
//			} else {
//				workerSeqStr.append("|" + workerSeq + "|");
//			}
//		}
//		chromosomeStr.append(workerSeqStr);
//		chromosomeStr.append("\r\n");
		chromosomeStr.append("************************************************************");
		// fitnessFunction.evaluate(this, true);
		return chromosomeStr.toString();
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

}
