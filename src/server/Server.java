package server;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.math.BigInteger;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.security.cert.X509Certificate;
import java.security.cert.CertificateException;

import util.*;
import util.logger.*;

public class Server {

	// Initiate system before starting the server
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

		System.setProperty("javax.net.ssl.trustStore",
				"./certificates/CA/truststore");

		/*
		 * Create hospital divisions according to UUH (Uppsala University
		 * Hospital).
		 * http://en.wikipedia.org/wiki/Uppsala_University_Hospital#Divisions
		 */
		divisions = new HashMap<Integer, Division>();
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

		/*
		 * Create a "Doctors"-list where all working doctors at the hospital are
		 * put in, together with their division.
		 */
		docs = new ArrayList<Doctor>();
		docs.add(new Doctor("doctor00", divisions.get(0)));
		docs.add(new Doctor("doctor01", divisions.get(0)));
		docs.add(new Doctor("doctor10", divisions.get(1)));

		/*
		 * Create a "Nurse"-list where all working nurses at the hospital are
		 * put in, together with their division.
		 */
		nurses = new ArrayList<Nurse>();
		nurses.add(new Nurse("nurse00", divisions.get(0)));
		nurses.add(new Nurse("nurse01", divisions.get(0)));
		nurses.add(new Nurse("nurse10", divisions.get(1)));

		/*
		 * Create a "Patient" -list where patients with injuries are put in,
		 * together with their injury and what division they will need to visit.
		 */
		patients = new ArrayList<Patient>();
		patients.add(new Patient("patient00", "Broken back", divisions.get(0)));
		patients.add(new Patient("patient01", "Broken toe", divisions.get(0)));
		patients.add(new Patient("patient10", "Fractured skull", divisions
				.get(1)));

		/*
		 * Create a agent who is working for the government, is this case
		 * "Socialstyrelsen".
		 */
		agent = new GovernmentAgent("agent", divisions.get(7));

		/*
		 * Create medical "Records"-list where all medicial journals are stored.
		 */
		records = new ArrayList<Record>();
		records.add(createJournal(patients.get(0), docs.get(0), nurses.get(0)));
		records.add(createJournal(patients.get(1), docs.get(1), nurses.get(1)));
		records.add(createJournal(patients.get(2), docs.get(2), nurses.get(2)));

		// Start the server
		Server s = new Server();
		s.run();
	}

	private void run() {

		// Create SSL Socket which will wait and listen for a request.
		// BufferedReader and -Writer are instantiated
		// for transmitting and receiving data.
		SSLSocket client;
		// BufferedReader fromClient;
		// BufferedWriter toClient;
		String readLine = null;

		// Creating hashmap to store all of the commands
		HashMap<String, Pattern> commands = new HashMap<String, Pattern>();

		/*
		 * Commands:
		 * 
		 * "All": list records read record [record_id]
		 * 
		 * "Nurse": list patients write record [record_id] [data]
		 * 
		 * "Doctor": list patients list nurses write record [record_id] [data]
		 * create record [patient_id] [nurse_id] [data] assign [nurse_id] to
		 * [patient_id]
		 * 
		 * "Government Agent": delete [record_id]
		 */

		commands.put("list records", Pattern.compile("list records"));
		commands.put("list nurses", Pattern.compile("list nurses"));
		commands.put("list patients", Pattern.compile("list patients"));
		// commands.put("read record",
		// Pattern.compile("read record (?<recordid>\\d+)"));
		// commands.put("write record",
		// Pattern.compile("write record (?<recordid>\\d+) (?<data>).*"));
		// commands.put("delete record",
		// Pattern.compile("delete record (?<recordid>\\d+)"));
		// commands.put(
		// "create record",
		// Pattern.compile("create record (?<patientid>\\d+) (?<nurseid>\\d+) (?<data>).*"));
		// commands.put("assign nurse", Pattern
		// .compile("assign (?<nurseid>\\d+) to (?<patientid>\\d+)"));

		try {

			char[] passphrase = "password".toCharArray();

			SSLContext ctx = SSLContext.getInstance("TLS");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance("JKS");
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance("SunX509");

			ks.load(new FileInputStream("./certificates/server/server.jks"),
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
			printSocketInfo(client);
			System.out.println("Client connected ...");

			PrintWriter out = new PrintWriter(new PrintWriter(
					client.getOutputStream()));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));

			try {

				// outputLine = processInput(null);
				// out.println(outputLine);

				// while ((clientOutput = in.readLine()) != null) {
				// clientOutput = processInput(serverInput);
				// out.println(clientOutput);
				// if (clientOutput.equals("exit"))
				// break;
				// }

				// fromClient = new BufferedReader(new InputStreamReader(
				// client.getInputStream()));
				//
				//
				// OutputStreamWriter outputstreamwriter = new
				// OutputStreamWriter(client.getOutputStream());
				// serverInput = new BufferedWriter(outputstreamwriter);
				//
				String fromClient = null;
				while ((fromClient = in.readLine()) != null) {
					out.println(fromClient);
					fromClient = in.readLine();
					out.write(fromClient, 0, fromClient.length());
					out.println();
					out.flush();
				}

				// out.writeBytes("Enter your command: ");
				// toClient.flush();
				//
				// readLine = fromClient.readLine();
				// while (readLine != null && !readLine.equals("quit")) {
				//
				// // loginClient(fromClient, toClient);
				//
				// // TODO: Fix login, fetch real logged in entity
				//
				// toClient.writeBytes("Enter your command: ");
				// toClient.flush();
				// readLine = fromClient.readLine();
				//
				// for (Entry<String, Pattern> e : commands.entrySet()) {
				// if (e.getValue().matcher(readLine).matches()) {
				// toClient.writeChars(handleCommand(currentEntityUser,
				// e.getKey(), e.getValue()));
				// }
				// }
				//
				// // } while (readLine != null && !readLine.equals("quit"));
				//
				// // Check username
				//
				// }
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("The problem lays here");
			}

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

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * >>>>>>>>>>>>>>>>
	 */

	// handled commmunication between client and serve. is not finished and
	// needs to be more generic
	private static String processInput(String input) {
		String output = null;

		if (input == null) {
			output = "Your name is: ";
		}
		if (input.equals("doctor00")) {
			currentEntityUser = findEntity("doctor00");
		}

		return output;
	}

	// finds the correct entity. is not finished.
	private static Entity findEntity(String userName) {

		for (int i = 0; i < docs.size(); i++) {
			if (docs.get(i).getName().equals(userName)) {
				return docs.get(i);
			}
		}
		for (int i = 0; i < nurses.size(); i++) {
			if (nurses.get(i).getName().equals(userName)) {
				return nurses.get(i);
			}
		}
		for (int i = 0; i < patients.size(); i++) {
			if (patients.get(i).getName().equals(userName)) {
				return patients.get(i);
			}
		}

		return null;
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * >>>>>>>>>>>>>>>>
	 */

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

	// EMPTY METHOD!
	private void loginClient(BufferedReader fromClient,
			DataOutputStream toClient) throws IOException {
	}

	/*
	 * Creates private medical journal for a patient. In the journal the
	 * assigned doctor, who treated the patient, and assisted nurse are store as
	 * well.
	 */
	public static Record createJournal(Patient patient, Doctor doctor,
			Nurse nurse) throws InvalidParameterException {
		// Checks if all entities are in the same division
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

		// If they are create new medical journal
		Record record = new Record(patient, doctor, nurse, patient.getData());

		// log update in true case.
		log.updateLog(new EntityAccessLogEvent(doctor, record,
				EntityWithAccessControl.EXECUTE));

		return record;
	}

	public String readData(Record record, Entity entity)
			throws AccessControlException {
		try {
			return record.readData(entity);
		} catch (AccessControlException e) {

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
			throws AccessControlException {
		try {
			record.writeData(entity, data);
		} catch (AccessControlException e) {
			// logs in false case
			log.updateLog(new EntityAccessDeniedLogEvent(entity, record,
					EntityWithAccessControl.READ));
			e.printStackTrace();
		}
		// logs in true case
		log.updateLog(new EntityAccessLogEvent(entity, record,
				EntityWithAccessControl.READ));
	}

	/*
	 * Deletes the medical journal
	 */
	public void deleteJournal(Record record, Entity entity)
			throws AccessControlException {
		try {
			record.delete(entity);
		} catch (AccessControlException e) {
			// logs in false case
			log.updateLog(new EntityAccessDeniedLogEvent(entity, record,
					EntityWithAccessControl.READ));
			e.printStackTrace();
		}
		// logs in true case
		log.updateLog(new EntityAccessLogEvent(entity, record,
				EntityWithAccessControl.READ));
	}

	/*
	 * Prints out information about a newly connected client
	 */
	private static void printSocketInfo(SSLSocket s) {
		System.out.println("Socket class: " + s.getClass());
		System.out.println("   Remote address = "
				+ s.getInetAddress().toString());
		System.out.println("   Remote port = " + s.getPort());
		System.out.println("   Local socket address = "
				+ s.getLocalSocketAddress().toString());
		System.out.println("   Local address = "
				+ s.getLocalAddress().toString());
		System.out.println("   Local port = " + s.getLocalPort());
		System.out.println("   Need client authentication = "
				+ s.getNeedClientAuth());
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = " + ss.getCipherSuite());
		System.out.println("   Protocol = " + ss.getProtocol());
	}
}
