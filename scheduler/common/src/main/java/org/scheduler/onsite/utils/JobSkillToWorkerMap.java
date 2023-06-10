package org.scheduler.onsite.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Combinations;
import org.scheduler.onsite.model.Worker;

public class JobSkillToWorkerMap {

	private Map<String, List<Worker>> skillToWorkerMap = new HashMap<String, List<Worker>>();

	public JobSkillToWorkerMap(List<Worker> workers) {
		for (Worker worker : workers) {
			Integer[] skillCodes = worker.getSkillCodeLevelMap().keySet().<Integer>toArray(new Integer[1]);
			int skillCount = skillCodes.length;
			for (int i = 1; i <= skillCount; i++) {
				Combinations combinations = new Combinations(skillCount, i);
				for (int[] combIndexes : combinations) {
					StringBuilder skillCodeKeyBuilder = new StringBuilder();
					for (int index : combIndexes) {
						skillCodeKeyBuilder.append(skillCodes[index]);
						skillCodeKeyBuilder.append("-");
					}
					String skillCodeKey = skillCodeKeyBuilder.substring(0, skillCodeKeyBuilder.lastIndexOf("-"));
					updateSkillMap(skillCodeKey, worker);
				}
			}
		}
	}

	private void updateSkillMap(String skillCode, Worker worker) {
		if (!skillToWorkerMap.containsKey(skillCode)) {
			skillToWorkerMap.put(skillCode, new ArrayList<Worker>());
		}
		skillToWorkerMap.get(skillCode).add(worker);
	}

	public List<Worker> getWorkers(List<Integer> skillCodes) {
		StringBuilder keyCode = new StringBuilder();
		for (Integer skillCode : skillCodes) {
			keyCode.append(skillCode.toString());
			keyCode.append("-");
		}
		return Collections.unmodifiableList(skillToWorkerMap.get(keyCode.substring(0, keyCode.lastIndexOf("-"))));
	}

}
