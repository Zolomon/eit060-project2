package client;

import java.security.*;
import java.io.*;
import java.net.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client {

	public static void main(String[] args) {
		while (true) {
			Socket client = null;
			PrintWriter out = null;

			// creating a SSLengine
			// Create/initialize the SSLContext with key material
			//char[] passphrase = "passphrase".toCharArray();
			
			// First initialize the key and trust material.
			//KeyStore ksKeys = KeyStore.getInstance("JKS");
			//ksKeys.load(new FileInputStream("testKeys"), passphrase);
			//KeyStore ksTrust = KeyStore.getInstance("JKS");
			//ksTrust.load(new FileInputStream("testTrust"), passphrase);

			// KeyManager's decide which key material to use.
			//KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			//kmf.init(ksKeys, passphrase);

			// TrustManager's decide whether to allow connections.
			//TrustManagerFactory tmf = TrustManagerFactory
			//		.getInstance("SunX509");
			//tmf.init(ksTrust);
			
			//SSLContext sslContext = SSLContext.getInstance("TLS");
			//sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			//SSLEngine engine = sslContext.createSSLEngine("Address to server?", 6789);

			//engine.setUseClientMode(true);

			try {

				client = new Socket("Address to server?", 6789);
				out = new PrintWriter(client.getOutputStream(), true);
				if (client.isConnected() != true) {
					return;
				}

				InputStream in = client.getInputStream();

			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Running Client ...");
		}

	}

}
