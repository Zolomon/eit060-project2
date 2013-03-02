package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import java.security.*;
import java.security.cert.CertificateException;

import util.*;
import util.logger.EntityAccessDeniedLogEvent;
import util.logger.EntityAccessLogEvent;
import util.logger.Log;
import util.logger.LogEvent;

public class Server {
	private static HashMap<Integer, Division> divisions;
	private static ArrayList<Doctor> docs;
	private static ArrayList<Nurse> nurses;
	private static ArrayList<Patient> patients;
	private static ArrayList<Record> records;

	private static GovernmentAgent agent;
	private static Log log = new Log(System.out);

	private static Entity currentEntityUser;

	private static final int PORT = 5678;

	public static void main(String[] args) {
		System.setProperty(
				"javax.net.ssl.trustStore",
				"C:\\Users\\Tobias\\Documents\\GitHub\\eit060-project2\\certificates\\CA\\truststore");

		divisions = new HashMap<Integer, Division>();

		// http://en.wikipedia.org/wiki/Uppsala_University_Hospital#Divisions
		divisions.put(0, new Division(
				"Diagnostics, Anesthesia and Technology Division"));
		divisions.put(1, new Division("Emergency and Rehabilitation Division"));
		divisions.put(2, new Division("Oncology, Thorax and Medical Division"));
		divisions.put(3, new Division("Neurology Division"));
		divisions.put(4, new Division("Psychiatry Division"));
		divisions.put(5, new Division("Surgery Division"));
		divisions
				.put(6, new Division("Women's Health and Pediatrics Division"));
		divisions.put(7, new Division("Socialstyrelsen"));

		docs = new ArrayList<Doctor>();
		docs.add(new Doctor("doctor00", divisions.get(0)));
		docs.add(new Doctor("Doctor d0_1", divisions.get(0)));

		docs.add(new Doctor("Doctor d1_0", divisions.get(1)));

		nurses = new ArrayList<Nurse>();
		nurses.add(new Nurse("Sabrewulf n0_0", divisions.get(0)));
		nurses.add(new Nurse("Sabrewulf n0_1", divisions.get(0)));

		nurses.add(new Nurse("Eyedol n1_0", divisions.get(1)));

		patients = new ArrayList<Patient>();
		patients.add(new Patient("Spinal p5_0", "Broken back", divisions.get(0)));
		patients.add(new Patient("Spinal p5_1", "Broken toe", divisions.get(0)));

		patients.add(new Patient("Spinal p4_0", "Fractured skull", divisions
				.get(1)));

		agent = new GovernmentAgent("FRA", divisions.get(7));

		records = new ArrayList<Record>();
		records.add(createJournal(patients.get(0), docs.get(0), nurses.get(0)));
		records.add(createJournal(patients.get(1), docs.get(1), nurses.get(1)));
		records.add(createJournal(patients.get(2), docs.get(2), nurses.get(2)));

		Server s = new Server();
		s.run();
	}

	private void run() {
		HashMap<String, Pattern> commands = new HashMap<String, Pattern>();

		/*
		 * Commands:
		 * 
		 * All: list records read record [record_id]
		 * 
		 * Nurse: list patients write record [record_id] [data]
		 * 
		 * Doctor: list patients list nurses write record [record_id] [data]
		 * create record [patient_id] [nurse_id] [data] assign [nurse_id] to
		 * [patient_id]
		 * 
		 * Government Agent: delete [record_id]
		 */

		commands.put("list records", Pattern.compile("list records"));
		commands.put("list nurses", Pattern.compile("list nurses"));
		commands.put("list patients", Pattern.compile("list patients"));

		commands.put("read record",
				Pattern.compile("read record (?<recordid>\\d+)"));
		commands.put("write record",
				Pattern.compile("write record (?<recordid>\\d+) (?<data>).*"));
		commands.put("delete record",
				Pattern.compile("delete record (?<recordid>\\d+)"));

		commands.put(
				"create record",
				Pattern.compile("create record (?<patientid>\\d+) (?<nurseid>\\d+) (?<data>).*"));
		commands.put("assign nurse", Pattern
				.compile("assign (?<nurseid>\\d+) to (?<patientid>\\d+)"));

		SSLSocket client;
		BufferedReader fromClient;
		BufferedWriter toClient;
		String readLine = null;

		try {

			char[] passphrase = "password".toCharArray();

			SSLContext ctx = SSLContext.getInstance("TLS");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("JKS");
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance("SunX509");

			ks.load(new FileInputStream(
					"C:\\Users\\Tobias\\Documents\\GitHub\\eit060-project2\\certificates\\server\\server.jks"),
					passphrase);

			kmf.init(ks, passphrase);
			tmf.init(ks);
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			SSLServerSocketFactory factory = ctx.getServerSocketFactory();

			SSLServerSocket ss = (SSLServerSocket) factory
					.createServerSocket(PORT);

			ss.setNeedClientAuth(true);
			System.out.println("Running server ...");
			System.out.println(ss);
			System.out.println("Server is listening on port " + PORT);

			client = (SSLSocket) ss.accept();

			SSLSession session = client.getSession();
			X509Certificate cert = (X509Certificate) session
					.getPeerCertificateChain()[0];
			String subject = cert.getSubjectDN().getName();
			System.out.println(subject);

			System.out.println("Client connected ...");
			System.out.println(client);

			fromClient = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			OutputStream outPut = null;
			OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outPut);
			toClient = new BufferedWriter(outputstreamwriter);
			
			while((readLine = fromClient.readLine())!= null){
				if(readLine.equals("quit")){
					
					toClient.write("Do not even dare\n");
					toClient.flush();
				}
				System.out.println(readLine);
				System.out.flush();
			}
			
			
//			toClient.writeBytes("Enter your command: ");
//			toClient.flush();
//			
//			readLine = fromClient.readLine();
//			while (readLine != null && !readLine.equals("quit")) {
//
//				// loginClient(fromClient, toClient);
//
//				// TODO: Fix login, fetch real logged in entity
//
//				toClient.writeBytes("Enter your command: ");
//				toClient.flush();
//				readLine = fromClient.readLine();
//
//				for (Entry<String, Pattern> e : commands.entrySet()) {
//					if (e.getValue().matcher(readLine).matches()) {
//						toClient.writeChars(handleCommand(currentEntityUser,
//								e.getKey(), e.getValue()));
//					}
//				}
//
//				// } while (readLine != null && !readLine.equals("quit"));
//
//				// Check username
//
//			}
		} catch (IOException e) {
			System.out.println("Class Server died: " + e.getMessage());
			e.printStackTrace();
			log.updateLog(new LogEvent(Log.LVL_ERROR, "IOException", e
					.toString()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public interface CommandHandler {
		public String handleCommand(EntityWithAccessControl entity, Pattern p);
	}

	public List<Record> getReadableRecords(EntityWithAccessControl entity) {
		List<Record> result = new ArrayList<Record>();

		for (Record r : records) {
			if (entity.canAccess(r, EntityWithAccessControl.READ))
				result.add(r);
		}

		return result;
	}

	@SuppressWarnings("serial")
	HashMap<String, CommandHandler> m = new HashMap<String, CommandHandler>() {
		{
			put("list records", new CommandHandler() {

				@Override
				public String handleCommand(EntityWithAccessControl entity,
						Pattern p) {
					StringBuilder sb = new StringBuilder();

					for (Record r : getReadableRecords(entity))
						sb.append(r.toString() + "\n");

					return sb.toString();
				}

			});
		}
	};

	private String handleCommand(Entity entity, String command, Pattern p) {
		System.out.println(String.format("Handling command [%s] for [#%d, %s]",
				command, entity.getId(), entity.getName()));
		return m.get(command).handleCommand(entity, p);
	}

	private void loginClient(BufferedReader fromClient,
			DataOutputStream toClient) throws IOException {
	}

	public static Record createJournal(Patient patient, Doctor doctor,
			Nurse nurse) throws InvalidParameterException {
		if (patient.getDivision() != nurse.getDivision()
				&& nurse.getDivision() != doctor.getDivision()) {
			// logs the false case. Should message be included?
			log.updateLog(new LogEvent(
					Log.LVL_ERROR,
					"RUNTIME ERROR",
					String.format(
							"Patient #%d, Nurse #%d, Doctor #%d not in same division",
							patient.getId(), nurse.getId(), doctor.getId())));

			throw new InvalidParameterException(
					String.format(
							"Nurse [%s] is not from the same division [%s] as doctor [%s]",
							nurse.getName(), patient.getDivision().getId(),
							doctor.getName()));
		}

		Record record = new Record(patient, doctor, nurse, patient.getData());

		// log update in true case.
		log.updateLog(new EntityAccessLogEvent(doctor, record,
				EntityWithAccessControl.EXECUTE));

		return record;
	}

	public String readData(Record record, Entity entity)
			throws AccessDeniedException {
		try {
			return record.readData(entity);
		} catch (AccessDeniedException e) {

			// logs in false case
			log.updateLog(new EntityAccessDeniedLogEvent(entity, record,
					EntityWithAccessControl.READ));
			e.printStackTrace();
		}
		// logs in true case
		log.updateLog(new EntityAccessLogEvent(entity, record,
				EntityWithAccessControl.READ));
		return null;
	}

	public void writeData(Record record, Entity entity, String data)
			throws AccessDeniedException {
		try {
			record.writeData(entity, data);
		} catch (AccessDeniedException e) {
			// logs in false case
			log.updateLog(new EntityAccessDeniedLogEvent(entity, record,
					EntityWithAccessControl.READ));
			e.printStackTrace();
		}
		// logs in true case
		log.updateLog(new EntityAccessLogEvent(entity, record,
				EntityWithAccessControl.READ));
	}

	public void deleteJournal(Record record, Entity entity)
			throws AccessDeniedException {
		try {
			record.delete(entity);
		} catch (AccessDeniedException e) {
			// logs in false case
			log.updateLog(new EntityAccessDeniedLogEvent(entity, record,
					EntityWithAccessControl.READ));
			e.printStackTrace();
		}
		// logs in true case
		log.updateLog(new EntityAccessLogEvent(entity, record,
				EntityWithAccessControl.READ));
	}
}
