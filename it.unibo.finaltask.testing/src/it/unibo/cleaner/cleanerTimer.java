package it.unibo.cleaner;

import it.unibo.qactors.akka.QActor;

public class cleanerTimer {
	private static long startTime = -1;

	public static void startTimer(QActor qa) {
		// Use of nano instead of millis to ensure precision
		startTime = System.nanoTime();
	}

	public static void stopTimer(QActor qa) {
		long stopTime = System.nanoTime();
		// Conversion from nano to millis as that is what the robot needs
		long timeMoved = (stopTime - startTime) / 1000000;
		// Correction for virtual robot
		timeMoved -= 16;
		
		if (timeMoved > 0)
			qa.addRule("timeMoved(" + Long.toString(timeMoved) + ")");
		
		// Resetting this value to negative to help debugging
		startTime = -1;
	}
}
