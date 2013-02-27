package client;

import java.security.*;
import java.security.cert.CertificateException;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

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

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 55555;
	
	public static void main(String[] args) {
		SSLSocketFactory cFac = null;
		SSLContext sslCont = null;
		SSLSocket client = null;
		KeyManagerFactory kmf = null;
        KeyStore ks = null;
        
		while (true) {
			
			try{
				
				System.setProperty("javax.net.ssl.keyStore", "certificates/doctor0_0Keystore");
				System.setProperty("javax.net.ssl.keyStorePassword", "doctor00password");
				System.setProperty("javax.net.ssl.trustStore", "certificates/CAtruststore");
				System.setProperty("javax.net.ssl.trustStorePassword", "StorePass");
                
                client = (SSLSocket)cFac.createSocket(HOST, PORT);
                client.setUseClientMode(true);
                
                client.startHandshake();
                
                
                
                System.out.println("So far, closing");
                
                
                client.close();
				
			}catch (UnknownHostException e) {
				System.out.println("2");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("3");
				e.printStackTrace();
			}

			
		}
	}
}