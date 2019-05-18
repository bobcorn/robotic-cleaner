package it.unibo.cleaner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import alice.tuprolog.SolveInfo;
import it.unibo.qactors.QActorContext;
import it.unibo.qactors.QActorUtils;
import it.unibo.qactors.akka.QActor;

public class TestCleaner {

	private static QActor cleaner;

	static final int DELAY = 403;
	static final int DELAY_MID_MOVE = 50;
	static final int DELAY_PROCESS = 10;

	static final String RULE_STOPPED = "cleanStop";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		it.unibo.ctxFinalSysTesting.MainCtxFinalSysTesting.initTheContext();
		Thread.sleep(1000);
		cleaner = QActorUtils.getQActor("cleaner_ctrl");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cleaner.terminate();
	}

	@Before
	public void setup() throws Exception {
		Thread.sleep(1000);
		sendStart();
		Thread.sleep(DELAY);
	}

	@After
	public void tearDown() throws Exception {
		sendNextStep();
	}

	private void sendMsg(String id) throws Exception {
		cleaner.sendMsg(id, cleaner.getNameNoCtrl(), QActorContext.dispatch, id + "(true)");
	}

	private void sendNextStep() throws Exception {
		sendMsg("nextStep");
	}

	private void sendStop() throws Exception {
		sendMsg("stopAutoClean");
	}

	private void sendStart() throws Exception {
		sendMsg("startAutoClean");
	}

	private void sendSonar() {
		cleaner.emit("sonarDetect", "sonarDetect(player)");
	}

	@Test
	public void testExternalStop() throws Exception {
		SolveInfo info;
		for (int i = 0; i < 10; i++) {
			info = cleaner.solveGoal(RULE_STOPPED);
			assertFalse(info.isSuccess());
			Thread.sleep(DELAY);
			sendNextStep();
		}

		Thread.sleep(DELAY_MID_MOVE);
		sendStop();
		Thread.sleep(DELAY_PROCESS);
		info = cleaner.solveGoal(RULE_STOPPED);
		assertTrue(info.isSuccess());
	}

	private void checkMaps(ArrayList<Integer> sonar, ArrayList<Map> maps) throws Exception {
		SolveInfo info;

		for (int n = 0; n < maps.size(); n++) {
			info = cleaner.solveGoal(RULE_STOPPED);
			assertFalse("Already stopped at step #" + n, info.isSuccess());

			sendNextStep();
			if (sonar.contains(n)) {
				Thread.sleep(DELAY_MID_MOVE);
				sendSonar();
			}

			Thread.sleep(DELAY);
			assertEquals("Failed step #" + (n + 1), maps.get(n), Mapper.extractMap(cleaner));
		}

		info = cleaner.solveGoal(RULE_STOPPED);
		assertTrue(info.isSuccess());
	}

	 @Test
	 public void testMovementNoObstacle() throws Exception {
	 ArrayList<Integer> sonar = new ArrayList<>();
	 ArrayList<Map> maps = Mapper.loadMaps("noObstacles");
	 checkMaps(sonar, maps);
	 }

	@Test
	public void testMovementFix() throws Exception {
		ArrayList<Integer> sonar = new ArrayList<>();
		sonar.add(50);
		sonar.add(67);
		sonar.add(78);
		sonar.add(85);
		sonar.add(144);
		sonar.add(148);
		sonar.add(155);
		sonar.add(162);
		ArrayList<Map> maps = Mapper.loadMaps("fixed");
		checkMaps(sonar, maps);
	}

	@Test
	public void testMovementMobile() throws Exception {
		ArrayList<Integer> sonar = new ArrayList<>();
		sonar.add(10);
		sonar.add(17);
		ArrayList<Map> maps = Mapper.loadMaps("mobile");
		checkMaps(sonar, maps);
	}

}
