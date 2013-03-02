package client;

import java.security.*;
import java.security.cert.CertificateException;
import java.util.Scanner;
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

		Scanner sc = new Scanner(System.in);
		;
		System.out.print("Namn: ");
		String id = sc.next();
		System.out.print("Password: ");
		String pass = sc.next();
		try {
			char[] passphrase = pass.toCharArray();

			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");
			tmf = TrustManagerFactory.getInstance("SunX509");

			ks.load(new FileInputStream(
					"C:\\Users\\Tobias\\Documents\\GitHub\\eit060-project2\\certificates\\"
							+ id + "\\" + id + ".jks"), passphrase);

			kmf.init(ks, passphrase);
			tmf.init(ks);
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			factory = ctx.getSocketFactory();

			SSLSocket client = (SSLSocket) factory.createSocket("localhost",
					PORT);

			client.setUseClientMode(true);
			client.startHandshake();
			System.out.println(client);
			
			String readLine;
			BufferedReader fromServer;
			DataOutputStream toServer;
			toServer = new DataOutputStream(client.getOutputStream());
			fromServer = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			
			

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
