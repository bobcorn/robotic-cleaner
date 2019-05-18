package it.unibo.lampadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import it.unibo.qactors.QActorContext;
import it.unibo.qactors.QActorUtils;
import it.unibo.qactors.akka.QActor;

public class hueClient {
	private static QActorContext ctx = null;
	private static String evId = null;
	private static String hueBridgeAddr = "192.168.1.85";
	private static String lampId = "3";
	private static String hueCmdPrefix;
	private static CloseableHttpClient httpclient = null;

	public static void setQaCtx(QActor qa, String curevId, String ip, String lId) {
		ctx = qa.getQActorContext();
		evId = curevId;
		hueBridgeAddr = ip.replaceAll("[\"']+", "");
		lampId = lId.replaceAll("[\"']+", "");
		
		hueCmdPrefix = "http://" + hueBridgeAddr + "/api/2GgKjwy9JAlW57Dl7qJ1ZRgEpWvjZi6ghN6hesAC/";
	}

	public static void setOff(QActor qa) {
		sendPut(qa, "{'on':false}", "lights/" + lampId + "/state");
	}

	public static void setOn(QActor qa) {
		sendPut(qa, "{'on':true, 'bri':167}", "lights/" + lampId + "/state");
	}

	public static void sendPut(QActor qa, String data, String url) {
		// System.out.println("sendPut " + url);
		try {
			data = data.replace("'", "\"");
			url = hueCmdPrefix + url.replace("'", "\"");
			if (httpclient == null)
				httpclient = HttpClients.createDefault();
			HttpPut request = new HttpPut(url);
			System.out.println(url);
			StringEntity params = new StringEntity(data, "UTF-8");
			params.setContentType("application/json");
			request.addHeader("content-type", "application/json");
			request.addHeader("Accept", "*/*");
			request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
			request.addHeader("Accept-Language", "en-US,en;q=0.8");
			request.setEntity(params);
			CloseableHttpResponse response = httpclient.execute(request);
			getAnswerFromServer(response, "put");
		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
		}
	}

	public static void sendGet(QActor qa, String cmd) {
		try {
			if (httpclient == null)
				httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(hueCmdPrefix + cmd);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			getAnswerFromServer(response, "get");
		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
		}
	}

	protected static void getAnswerFromServer(CloseableHttpResponse response, String verb) {
		try {
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output;
			String info = "";
			while ((output = br.readLine()) != null) {
				info = info + output;
			}
			System.out.println("Output from Server .... ");
			System.out.println(info);
			if (ctx != null) {
				String msg = evId + "(" + verb + ", '" + info + "')";
				// System.out.println(msg);
				QActorUtils.raiseEvent(ctx, "clienthttp", evId, msg);
			}
		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
		}

	}

	/*
	 * Just to test
	 */
	public static void main(String args[]) throws InterruptedException {
		sendGet(null, "lights/" + lampId);
		for (int i = 1; i <= 10; i++) {
			setOff(null);
			Thread.sleep(1000);
			setOn(null);
			Thread.sleep(1000);
		}
		setOff(null);
	}
}
/*
 * http://www.mio-ip.it/
 */