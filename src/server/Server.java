package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.*;
import java.security.*;

import util.*;

public class Server {
	private List<Journal> journals = new ArrayList<Journal>();
	private Log log = new Log();
	
	//trying to initiate a SSLSocketfactory to the handshake
	private SSLServerSocketFactory  socketFac = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	
	public static void main(String[] args) {

		// Temporary tcp-connection
		// TODO: FIXME: Make this an SSLServersocket instead...
		SSLSocket ss;
		try {
		
			//creates server socket
			ss = socketFac.createServerSocket();

			System.out.println("Running server ...");

			Socket client;
			BufferedReader fromClient;
			DataOutputStream toClient;
			String readLine = null;

			while (true) {
			
			//listens on a connection
			//do we need to bind it?
			SSLSocket socket =(SSLSocket)ss.accept();
			
			//sets up the handshake
			SSLSession session = socket.getSession();
			
			//forces the client to authenticate itself. Men hur gör //man det?
				socket.setNeedClientAuth(true);
				
				client = ss.accept();

				fromClient = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				toClient = new DataOutputStream(client.getOutputStream());

				
				loginClient(fromClient, toClient);
				
				while (!readLine.equals("quit")) {
					
					/*
					 *  Commands:
					 *  
					 *  All:
					 *  list records
					 *  read record id
					 *  
					 *  Patient:
					 *  
					 *  
					 *  Nurse:
					 *  
					 */
					
				}

				
				
				// Check username

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void loginClient(BufferedReader fromClient,
			DataOutputStream toClient) {
		// TODO Auto-generated method stub
		
	}

	public Journal createJournal(Patient p0, Doctor doctor, Nurse nurse)
			throws InvalidParameterException {
		if (p0.getDivision() != nurse.getDivision()
				&& nurse.getDivision() != doctor.getDivision()) {
			throw new InvalidParameterException(
					String.format(
							"Nurse [%s] is not from the same division [%s] as doctor [%s]",
							nurse.getId(), p0.getDivision().getId(),
							doctor.getId()));
			
			//logs the false case. Should message be included?
			log.updateLog(new Events(1, doctor, null, false));
		}

		Journal j = new Journal(p0, doctor, nurse, p0.getData());
		
		//log update in true case.
		log.updateLog(new Events(1, doctor, j, true));
		
		return j;
	}

	public String readData(Journal journal, EntityWithAccessControl entity)
			throws AccessDeniedException {
		try {
			return journal.readData(entity);
		} catch (AccessDeniedException e) {
		
		//logs in false case
		log.updateLog(new Events(2, entity, journal, false));
			e.printStackTrace();
		}
		//logs in true case
		log.updateLog(new Events(2, entity, journal, true));
		return null;
	}

	public void writeData(Journal journal, EntityWithAccessControl entity,
			String data) throws AccessDeniedException {
		try {
			journal.writeData(entity, data);
		} catch (AccessDeniedException e) {
			//logs in false case
			log.updateLog(new Events(3, entity, journal, false));
			e.printStackTrace();
		}
		//logs in true case
		log.updateLog(new Events(3, entity, journal, true));
	}

	public void deleteJournal(Journal journal, EntityWithAccessControl entity)
			throws AccessDeniedException {
		try {
			journal.delete(entity);
		} catch (AccessDeniedException e) {
		//logs in false case
		log.updateLog(new Events(4, entity, journal, false));
			e.printStackTrace();
		}
		//logs in true case
		log.updateLog(new Events(4, entity, journal, true));
	}
}
