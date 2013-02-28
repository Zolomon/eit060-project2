package client;

import java.security.*;
import java.security.cert.CertificateException;
import java.io.*;
import java.net.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client {

	private static final int PORT = 5678;

	public static void main(String[] args) {
		System.setProperty(
				"javax.net.ssl.trustStore",
				"C:\\Users\\Tobias\\Documents\\GitHub\\eit060-project2\\certificates\\CA\\truststore");

		SSLSocketFactory factory = null;
		SSLContext ctx = null;
		KeyManagerFactory kmf = null;
		KeyStore ks = null;
		TrustManagerFactory tmf = null;

		try {
			char[] passphrase = "password".toCharArray();

			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");
			tmf = TrustManagerFactory.getInstance("SunX509");

			ks.load(new FileInputStream(
					"c:\\Users\\Tobias\\Documents\\GitHub\\eit060-project2\\certificates\\doctor00\\doctor00.jks"),
					passphrase);

			kmf.init(ks, passphrase);
			tmf.init(ks);
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			factory = ctx.getSocketFactory();

			SSLSocket client = (SSLSocket) factory.createSocket("localhost",
					PORT);
			client.setUseClientMode(true);
			client.startHandshake();
			BufferedReader in;
			PrintWriter out;
			
		while(true){
			out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(client.getOutputStream())));
			out.println("so far");
			out.println();
			out.flush();

			/*
			 * Make sure there were no surprises
			 */
			if (out.checkError())
				System.out
				.println("SSLSocketClient:  java.io.PrintWriter error");

			/* read response */
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
			
			//in.close();
			//out.close();
			//client.close();
		}
		} catch (UnknownHostException e) {
			System.out.println("2");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("3");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
