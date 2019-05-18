package it.unibo.cleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;

import it.unibo.qactors.QActorUtils;
import it.unibo.qactors.akka.QActor;

public class Mapper {

	private static Hashtable<String, String> vars;

	private static String extractVariable(String name) {
		return QActorUtils.substituteVars(vars, name);
	}

	public static Map extractMap(QActor qa) throws Exception {
		if ((vars = QActorUtils.evalTheGuard(qa, " !?size(X,Y)")) != null) {
			int x = Integer.parseInt(extractVariable("X"));
			int y = Integer.parseInt(extractVariable("Y"));
			MapBuilder map = new MapBuilder();

			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					String cellStatusA = "status(cell(" + i + "," + j + "),";
					String status = "S";
					String cellStatusB = ")";
					if ((vars = QActorUtils.evalTheGuard(qa, " !?" + cellStatusA + status + cellStatusB)) != null)
						map.addCell(i, j, extractVariable(status).charAt(0));
				}
			}

			return map.getMap();
		} else
			return null;
	}

	public static ArrayList<Map> loadMaps(String scene) throws Exception {
		String path = "./maps/" + scene + "/map";
		String ext = ".pl";
		ArrayList<Map> maps = new ArrayList<>();
		int n = 0;

		while (true) {
			n++;
			File f = new File(path + n + ext);
			if (!f.exists())
				break;

			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader(f));
				String cell;
				MapBuilder map = new MapBuilder();
				while ((cell = in.readLine()) != null) {
					// status(cell(X,Y),S).
					String prefix = "status(cell(";
					String suffix = ").";
					if (cell.startsWith(prefix) && cell.endsWith(suffix)) {
						String[] parts = cell.substring(prefix.length(), cell.length() - suffix.length()).split(",");
						if (parts.length == 3) {
							int x = Integer.parseInt(parts[0]);
							char status = parts[2].charAt(0);

							parts = parts[1].split("\\)");
							if (parts.length >= 1) {
								int y = Integer.parseInt(parts[0]);
								map.addCell(x, y, status);
							} else
								throw new IllegalArgumentException("Invalid map in file");
						} else
							throw new IllegalArgumentException("Invalid map in file");
					} else
						throw new IllegalArgumentException("Invalid map in file");
				}

				Map res = map.getMap();
				maps.add(res);
			} catch (Exception e) {
				throw e;
			} finally {
				if (in != null)
					in.close();
			}
		}

		return maps;
	}

}
