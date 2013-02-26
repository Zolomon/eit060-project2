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

	public static void main(String[] args) {
		while (true) {
			SSLSocket client = null;
			PrintWriter out = null;
			String localhost = null;
			SSLSocketFactory clientSockFac = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLContext sslContext = null;
			
			try {

				SSLEngine sslEngineClient = getEngine(localhost, sslContext);

				client = (SSLSocket) clientSockFac.createSocket(localhost, 6789); 
				//out = new PrintWriter(client.getOutputStream(), true);

				//InputStream in = client.getInputStream();
				SocketChannel socketChannel = SocketChannel.open();
				socketChannel.configureBlocking(false);
				socketChannel.connect(new InetSocketAddress(localhost, 6789));

				SSLSession session = sslEngineClient.getSession();
				
				ByteBuffer myAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
				ByteBuffer myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
				ByteBuffer peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
				ByteBuffer peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());

				doHandshake(socketChannel, sslEngineClient, myNetData, peerNetData);

				myAppData.put("hello".getBytes());
				myAppData.flip();
				
				while (myAppData.hasRemaining()) {
				    // Generate SSL/TLS encoded data (handshake or application data)
				    SSLEngineResult res = sslEngineClient.wrap(myAppData, myNetData);

				    // Process status of call
				    if (res.getStatus() == SSLEngineResult.Status.OK) {
				        myAppData.compact();

				        // Send SSL/TLS encoded data to peer
				        while(myNetData.hasRemaining()) {
				            int num = socketChannel.write(myNetData);
				            if (num == -1) {
				                // handle closed channel
				            } else if (num == 0) {
				                // no bytes written; try again later
				            }
				        }
				    }

				    // Handle other status:  BUFFER_OVERFLOW, CLOSED
				    
				}
				
				// Read SSL/TLS encoded data from peer
				int num = socketChannel.read(peerNetData);
				if (num == -1) {
				    // Handle closed channel
				} else if (num == 0) {
				    // No bytes read; try again ...
				} else {
				    // Process incoming data
				    peerNetData.flip();
				    SSLEngineResult res = sslEngineClient.unwrap(peerNetData, peerAppData);

				    if (res.getStatus() == SSLEngineResult.Status.OK) {
				        peerNetData.compact();

				        if (peerAppData.hasRemaining()) {
				            // Use peerAppData
				        }
				    }
				    // Handle other status:  BUFFER_OVERFLOW, BUFFER_UNDERFLOW, CLOSED
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Running Client ...");
		}

	}

	private static void doHandshake(SocketChannel socketChannel, SSLEngine engine,
			ByteBuffer myNetData, ByteBuffer peerNetData) throws Exception {

		int appBufferSize = engine.getSession().getApplicationBufferSize();
		ByteBuffer myAppData = ByteBuffer.allocate(appBufferSize);
		ByteBuffer peerAppData = ByteBuffer.allocate(appBufferSize);

		engine.beginHandshake();
		SSLEngineResult.HandshakeStatus hs = engine.getHandshakeStatus();

		switch (hs) {

		case NEED_UNWRAP:
			// Receive handshaking data from peer
			if (socketChannel.read(peerNetData) < 0) {
				// Handle closed channel
			}

			// Process incoming handshaking data
			peerNetData.flip();
			SSLEngineResult res = engine.unwrap(peerNetData, peerAppData);
			peerNetData.compact();
			hs = res.getHandshakeStatus();

			// Check status
			switch (res.getStatus()) {
			case OK :
				// Handle OK status
				break;

				// Handle other status: BUFFER_UNDERFLOW, BUFFER_OVERFLOW, CLOSED

			}
			break;

		case NEED_WRAP :
			// Empty the local network packet buffer.
			myNetData.clear();

			// Generate handshaking data
			res = engine.wrap(myAppData, myNetData);
			hs = res.getHandshakeStatus();

			// Check status
			switch (res.getStatus()) {
			case OK :
				myNetData.flip();

				// Send the handshaking data to peer
				while (myNetData.hasRemaining()) {
					if (socketChannel.write(myNetData) < 0) {
						// Handle closed channel
					}
				}
				break;

				// Handle other status:  BUFFER_OVERFLOW, BUFFER_UNDERFLOW, CLOSED

			}
			break;

		case NEED_TASK :
			// Handle blocking tasks
			break;

			// Handle other status:  // FINISHED or NOT_HANDSHAKING

		}
	}

	// Processes after handshaking




private static SSLEngine getEngine(String localhost, SSLContext sslContext) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyManagementException{

	// Create/initialize the SSLContext with key material

	char[] passphrase = "passphrase".toCharArray();

	// First initialize the key and trust material.
	KeyStore ksKeys = KeyStore.getInstance("JKS");
	ksKeys.load(new FileInputStream("testKeys"), passphrase);
	KeyStore ksTrust = KeyStore.getInstance("JKS");
	ksTrust.load(new FileInputStream("testTrust"), passphrase);

	// KeyManager's decide which key material to use.
	KeyManagerFactory kmf =
	    KeyManagerFactory.getInstance("SunX509");
	kmf.init(ksKeys, passphrase);

	// TrustManager's decide whether to allow connections.
	TrustManagerFactory tmf =
	    TrustManagerFactory.getInstance("SunX509");
	tmf.init(ksTrust);

	sslContext = SSLContext.getInstance("TLS");
	sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

	// We're ready for the engine.
	SSLEngine engine = sslContext.createSSLEngine(localhost, 6789);

	// Use as client
	engine.setUseClientMode(true);
	return engine;
}

}
