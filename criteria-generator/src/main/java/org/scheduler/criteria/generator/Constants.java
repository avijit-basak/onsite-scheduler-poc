package org.scheduler.criteria.generator;

public interface Constants {

//	int NO_OF_WORKERS = 10;
//
//	int NO_OF_JOBS = 40;

//	int NO_OF_WORKERS = 50;

//	int NO_OF_JOBS = 200;

	double START_LATITUDE = 22.90511;

	double END_LATITUDE = 23.13768;

	double START_LONGITUDE = 72.45957;

	double END_LONGITUDE = 72.68547;

	String availabilitySlotStartTime = "2022-08-14 09:00:00";

	String availabilitySlotEndTime = "2022-08-14 20:00:00";

	int MIN_JOB_DURATION = 10;

	int MAX_JOB_DURATION = 60;

	int MIN_JOB_PRIORITY = 1;

	int MAX_JOB_PRIORITY = 10;

	int[] SKILL_CODES = new int[] { 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010 };

	int MIN_SKILL_LEVEL = 5;

	int MAX_SKILL_lEVEL = 10;

	int MAX_NO_OF_SKILLS_PER_WORKER = 2;
}
