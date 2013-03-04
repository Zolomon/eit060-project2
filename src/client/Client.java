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

import util.NetworkCommunication;

public class Client {

	private static final int PORT = 5678;

	public static void main(String[] args) {
		
		System.setProperty("javax.net.ssl.trustStore",
				"./certificates/CA/truststore");

		SSLSocketFactory factory = null;
		SSLContext ctx = null;
		KeyManagerFactory kmf = null;
		KeyStore ks = null;
		TrustManagerFactory tmf = null;

		Scanner scan = new Scanner(System.in);
		System.out.print("Namn: ");
		String id = scan.next();
		System.out.print("Password: ");
		String pass = scan.next();
		
		
		//StringBuffer
		try {
			char[] passphrase = pass.toCharArray();

			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");
			tmf = TrustManagerFactory.getInstance("SunX509");

			ks.load(new FileInputStream(
					"./certificates/"
							+ id + "/" + id + ".jks"), passphrase);
			
			

			kmf.init(ks, passphrase);
			tmf.init(ks);
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			factory = ctx.getSocketFactory();
			
			
			SSLSocket client = (SSLSocket) factory.createSocket("localhost", PORT);

			client.setUseClientMode(true);
			client.startHandshake();
			System.out.println(client);
			
			
			PrintWriter toServer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			NetworkCommunication nc = new NetworkCommunication(toServer, fromServer);
			
			// Parse welcome message
			System.out.println("Welcome: " + nc.receive());
			
			Scanner sc = new Scanner(System.in);
						
			// http://en.wikipedia.org/wiki/REPL
			String response = null;
			String userInput = null;
			do {
				System.out.print("Enter command: ");
				
				// Read - input from client -> server
				userInput = sc.nextLine();
				System.out.println("Input: " + userInput);
				nc.send(userInput);
				
				// Eval - output form server -> client
				response = nc.receive();
				System.out.println("Read Server: " + response);
				
				// Print - print result
				//System.out.println("Handled: " + response);
				
				// Loop - repeat!
			} while (response != null);
	
			
			/*
			while ((fromServer = in.readLine()) != null) {
			    		System.out.println("Server: " + fromServer);
			
			    Scanner sc1 = new Scanner(System.in);
			    fromUser = sc1.next();
			    if (fromUser != null) {
			        System.out.println("Client: " + fromUser);
			        out.println(fromUser);
			    }
			}
			*/
			
//			 OutputStreamWriter outputstreamwriter = new OutputStreamWriter(client.getOutputStream());
//	         toServer = new BufferedWriter(outputstreamwriter);
	            
//			fromServer = new BufferedReader(new InputStreamReader(
//					System.in));
//			
//			
//			String serverOutput;
//			String clientInput;
//			
//			System.out.println("Write hello");
//			while((serverOutput = in.readLine())!= null){
//				in.read(in);
//				out.flush();
			
			
			

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
