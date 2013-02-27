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

 
	private static final int PORT = 55554;
	
	public static void main(String[] args) {
		SSLSocketFactory factory = null;
		SSLContext ctx = null;
		SSLSocket client = null;
		KeyManagerFactory kmf = null;
        KeyStore ks = null;
        
		while (true) {
			
			try{
				
				//System.setProperty("javax.net.ssl.keyStore", "certificates/doctor0_0Keystore");
				//System.setProperty("javax.net.ssl.keyStorePassword", "doctor00password");
				//System.setProperty("javax.net.ssl.trustStore", "certificates/CAtruststore");
				//System.setProperty("javax.net.ssl.trustStorePassword", "StorePass");
                
					
				java.net.InetAddress localMachine =
						java.net.InetAddress.getLocalHost();
				
	                char[] passphrase = "doctor00password".toCharArray();

	                ctx = SSLContext.getInstance("TLS");
	                kmf = KeyManagerFactory.getInstance("SunX509");
	                ks = KeyStore.getInstance("JKS");

	                ks.load(new FileInputStream("c:\\Users\\Tobias\\Documents\\GitHub\\eit060-project2\\certificates\\doctor0_0Keystore"), passphrase);

	                kmf.init(ks, passphrase);
	                ctx.init(kmf.getKeyManagers(), null, null);

	                factory = ctx.getSocketFactory();
				
                client = (SSLSocket)factory.createSocket(localMachine, PORT);
                client.setUseClientMode(true);
                
                client.startHandshake();
   
				
			}catch (UnknownHostException e) {
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
}