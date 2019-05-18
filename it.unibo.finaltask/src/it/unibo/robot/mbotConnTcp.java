package it.unibo.robot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

import it.unibo.qactors.akka.QActor;

public class mbotConnTcp {
	private static String hostName = "localhost";
	private static int port = 8999;
	private static String sep = ";";
	protected static Socket clientSocket;
	protected static PrintWriter outToServer;
	protected static BufferedReader inFromServer;
	private static final QActor SELF_QA = null;
	private static final int FOREVER = -1;
	private static final int ROTATE_TIME = 200;

	public mbotConnTcp() {
		try {
			initClientConn(SELF_QA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initClientConn(QActor qa) throws Exception {
		clientSocket = new Socket(hostName, port);
		outToServer = new PrintWriter(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		initReceiver(qa);
	}

	protected static void initReceiver(final QActor qa) {
		new Thread() {
			public void run() {
				while (true) {
					try {
						String inpuStr = inFromServer.readLine();
						String jsonMsgStr = inpuStr.split(";")[1];
						JSONObject jsonObject = new JSONObject(jsonMsgStr);
						JSONObject jsonArg;
						switch (jsonObject.getString("type")) {
						case "webpage-ready":
							System.out.println("webpage-ready ");
							break;
						case "sonar-activated":
							jsonArg = jsonObject.getJSONObject("arg");
							String sonarName = jsonArg.getString("sonarName");
							int distance = Math.abs(jsonArg.getInt("distance"));
							qa.emit("sonar", "sonar(NAME, player, DISTANCE)".replace("NAME", sonarName.replace("-", ""))
									.replace("DISTANCE", ("" + distance)));
							break;
						case "collision":
							jsonArg = jsonObject.getJSONObject("arg");
							String objectName = jsonArg.getString("objectName");
							qa.emit("sonarDetect",
									"sonarDetect(TARGET)".replace("TARGET", objectName.replace("-", "")));
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public static void sendCmd(String msg, int time) throws Exception {
		if (outToServer == null)
			return;
		String jsonString = "{ 'type': '" + msg + "', 'arg': " + time + " }";
		JSONObject jsonObject = new JSONObject(jsonString);
		msg = sep + jsonObject.toString() + sep;
		System.out.println("sending msg=" + msg);
		outToServer.println(msg);
		outToServer.flush();
	}

	protected void println(String msg) {
		System.out.println(msg);
	}

	private static int parseTimeString(String time) {
		try {
			int t = Integer.parseInt(time);
			return t > 0 ? t : FOREVER;
		} catch (Exception e) {
			return FOREVER;
		}
	}

	public static void mbotForward(QActor qa, String time) {
		try {
			sendCmd("moveForward", parseTimeString(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mbotBackward(QActor qa, String time) {
		try {
			sendCmd("moveBackward", parseTimeString(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mbotLeft(QActor qa) {
		try {
			mbotStop(qa);
			sendCmd("turnLeft", ROTATE_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mbotRight(QActor qa) {
		try {
			mbotStop(qa);
			sendCmd("turnRight", ROTATE_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mbotStop(QActor qa) {
		try {
			sendCmd("alarm", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Just for testing
	// public static void main(String[] args) {
	// try {
	// initClientConn(SELF_QA);
	// System.out.println("STARTING ... ");
	// mbotForward(SELF_QA);
	// Thread.sleep(1000);
	// mbotBackward(SELF_QA);
	// Thread.sleep(1000);
	// mbotLeft(SELF_QA);
	// Thread.sleep(1000);
	// mbotForward(SELF_QA);
	// Thread.sleep(1000);
	// mbotRight(SELF_QA);
	// Thread.sleep(1000);
	// mbotForward(SELF_QA);
	// Thread.sleep(1000);
	// mbotStop(SELF_QA);
	// System.out.println("END");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
