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
				
				
                //... borde vara filen certet?
                char[] passphrase = "1234".toCharArray();
                sslCont = SSLContext.getInstance("TLS");
                
                kmf = KeyManagerFactory.getInstance("SunX509");
                ks = KeyStore.getInstance("JKS");
                
                //testkeys borde vara...
                ks.load(new FileInputStream("testkeys"), passphrase);
                
                System.out.print("passing load");
                
                cFac = sslCont.getSocketFactory();
                
                client = (SSLSocket)cFac.createSocket(HOST, PORT);
                
                client.startHandshake();
                
                System.out.println("So far");
                System.out.print(client);
                
                
                client.close();
				
			}catch (NoSuchAlgorithmException e){
				System.out.println("1");
				e.printStackTrace();
			} catch (UnknownHostException e) {
				System.out.println("2");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("3");
				e.printStackTrace();
			} catch (KeyStoreException e) {
				System.out.println("4");
				e.printStackTrace();
			} catch (CertificateException e) {
				System.out.println("5");
				e.printStackTrace();
			}

			
		}
	}
}