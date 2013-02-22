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
		ServerSocket ss;
		try {
			ss = new ServerSocket(6789);

			System.out.println("Running server ...");

			Socket client;
			BufferedReader fromClient;
			DataOutputStream toClient;
			String readLine = null;

			while (true) {
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
			// TODO: Log
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
			e.printStackTrace();
			// TODO: LOG
		}
		return null;
	}

	public void writeData(Journal journal, EntityWithAccessControl entity,
			String data) throws AccessDeniedException {
		try {
			journal.writeData(entity, data);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			// TODO: LOG
		}
	}

	public void deleteJournal(Journal journal, EntityWithAccessControl entity)
			throws AccessDeniedException {
		try {
			journal.delete(entity);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			// TODO: LOG
		}
	}
}
