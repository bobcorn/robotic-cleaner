package it.unibo.cleaner;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;

import it.unibo.qactors.QActorUtils;
import it.unibo.qactors.akka.QActor;

public class mapper {

	private static Hashtable<String, String> vars;

	private static String extractVariable(String name) {
		return QActorUtils.substituteVars(vars, name);
	}

	public static void saveMap(QActor qa) {
		System.out.println("Exporting map...");
		String obstacle = "x";
		String free = "0";
		PrintWriter out = null;
		try {
			File f = new File("./map.pl");
			if (f.exists())
				f.delete();
			FileWriter fp = new FileWriter(f);
			out = new PrintWriter(fp);
			if ((vars = QActorUtils.evalTheGuard(qa, " !?size(X,Y)")) != null) {
				int x = Integer.parseInt(extractVariable("X"));
				int y = Integer.parseInt(extractVariable("Y"));

				for (int i = 0; i < x; i++) {
					for (int j = 0; j < y; j++) {
						String cellStatusA = "status(cell(" + i + "," + j + "),";
						String status = "S";
						String cellStatusB = ")";
						if ((vars = QActorUtils.evalTheGuard(qa, " !?" + cellStatusA + status + cellStatusB)) != null) {
							status = extractVariable(status).equals(obstacle) ? obstacle : free;
							out.println(cellStatusA + status + cellStatusB + ".");
						} else
							throw new Exception("Incomplete map");
					}
				}
			}
			System.out.println("Map exported");
		} catch (Exception e) {
			System.out.println("Cannot access map data, abort export");
		} finally {
			if (out != null)
				out.close();
		}
	}

}
