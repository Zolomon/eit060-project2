package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.AccessControlException;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import util.Division;
import util.Doctor;
import util.Entity;
import util.EntityWithAccessControl;
import util.GovernmentAgent;
import util.NetworkCommunication;
import util.Nurse;
import util.Patient;
import util.Record;
import util.logger.EntityAccessDeniedLogEvent;
import util.logger.EntityAccessLogEvent;
import util.logger.Log;
import util.logger.LogEvent;

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

		try {
			// Create SSL Socket which will wait and listen for a request.
			// BufferedReader and -Writer are instantiated
			// for transmitting and receiving data.
			SSLSocket client;
			// BufferedReader fromClient;
			// BufferedWriter toClient;
			String readLine = null;

			commands = new HashMap<String, Pattern>();

			/*
			 * Commands:
			 * 
			 * "All": list records read record [record_id]
			 * 
			 * "Nurse": list patients write record [record_id] [data]
			 * 
			 * "Doctor": list patients list nurses write record [record_id]
			 * [data] create record [patient_id] [nurse_id] [data] assign
			 * [nurse_id] to [patient_id]
			 * 
			 * "Government Agent": delete [record_id]
			 */

			commands.put("help", Pattern.compile("help"));
			commands.put("list records", Pattern.compile("list records"));
			commands.put("list nurses", Pattern.compile("list nurses"));
			commands.put("list patients", Pattern.compile("list patients"));
			commands.put("read record", Pattern.compile("read record (\\d+)"));
			commands.put("write record",
					Pattern.compile("write record (\\d+) (.*)"));
			commands.put("delete record",
					Pattern.compile("delete record (\\d+)"));
			commands.put(
					"create record",
					Pattern.compile("create record for patient (\\d+) with nurse (\\d+) and data (.*)"));
			commands.put("assign nurse",
					Pattern.compile("assign nurse (\\d+) to patient (\\d+)"));

			// try {

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

			while (true) {

				client = (SSLSocket) ss.accept();
				SSLSession session = client.getSession();
				printSocketInfo(client);
				System.out.println("Client connected ...");

				PrintWriter toClient = new PrintWriter(new PrintWriter(
						client.getOutputStream()));
				BufferedReader fromClient = new BufferedReader(
						new InputStreamReader(client.getInputStream()));

				NetworkCommunication nc = new NetworkCommunication(toClient,
						fromClient);

				String id = nc.receive();

				System.out.println("Client connected ...");

				System.out.println("Logging in client ...");

				// TODO: Login client
				// loginClient(fromClient, toClient);

				// TODO: Fix login, fetch real logged in entity
				currentEntityUser = findEntity(id);

				System.out.println(String.format("Welcome %s! %s",
						currentEntityUser.getName(), currentEntityUser
								.getClass().getName()));

				nc.send(String.format("Welcome %s! %s", currentEntityUser
						.getName(), currentEntityUser.getClass().getName()));

				do {
					readLine = nc.receive();
					System.out.println("read: " + readLine);

					if (readLine == null)
						break;

					for (Entry<String, Pattern> e : commands.entrySet()) {
						if (e.getValue().matcher(readLine).matches()) {
							nc.send(handleCommand(currentEntityUser,
									e.getKey(), e.getValue(), readLine)); 
						}
					}

				} while (readLine != null && !readLine.equals("quit"));

			}
		} catch (IOException e) {
			e.printStackTrace();
			log.updateLog(new LogEvent(Log.LVL_ERROR, "IOException", e
					.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		if (agent.getName().equals(userName))
			return agent;

		return null;
	}

	public interface CommandHandler {
		public String handleCommand(Entity entity, Pattern p, String value); 
	}

	public List<Record> getReadableRecords(Entity entity) {
		List<Record> result = new ArrayList<Record>();

		for (Record r : records) {
			if (entity.canAccess(r, EntityWithAccessControl.READ))
				result.add(r);
		}

		return result;
	}

	private List<Nurse> getNursesForEntity(Entity entity) {
		List<Nurse> result = new ArrayList<Nurse>();

		for (Nurse n : nurses) {
			if (entity.getDivision().getId().equals(n.getDivision().getId()))
				result.add(n);
		}

		return result;
	}

	private List<Patient> getPatientsForEntity(Entity entity) {
		List<Patient> result = new ArrayList<Patient>();

		for (Patient p : patients) {
			if (entity.getDivision().getId().equals(p.getDivision().getId()))
				result.add(p);
		}

		return result;
	}

	@SuppressWarnings("serial")
	HashMap<String, CommandHandler> m = new HashMap<String, CommandHandler>() {
		{
			// commands.put("list patients", Pattern.compile("list patients"));
			// commands.put("read record",
			// Pattern.compile("read record (\\d+)"));
			// commands.put("write record",
			// Pattern.compile("write record (\\d+) (.*)"));
			// commands.put("delete record",
			// Pattern.compile("delete record (\\d+)"));
			// commands.put(
			// "create record",
			// Pattern.compile("create record for patient (\\d+) with nurse (\\d+) and data (.*)"));
			// commands.put("assign nurse",
			// Pattern.compile("assign nurse (\\d+) to patient (\\d+)"));

			put("help", new CommandHandler() {

				@Override
				public String handleCommand(Entity entity, Pattern p, String value) {
					System.out.println(String.format("Handling [%s] ...",
							p.pattern()));
					StringBuilder sb = new StringBuilder();

					sb.append("Command: \t\t\tFormat:\n");

					for (Map.Entry<String, Pattern> command : commands
							.entrySet())
						sb.append(String.format("%s\t\t\t%s\n",
								command.getKey(), command.getValue().pattern()));

					return sb.toString();
				}

			});

			put("list records", new CommandHandler() {

				@Override
				public String handleCommand(Entity entity, Pattern p, String value) {
					System.out.println(String.format("Handling [%s] ...",
							p.pattern()));
					StringBuilder sb = new StringBuilder();

					for (Record r : getReadableRecords(entity))
						sb.append(r.toString() + "\n");

					return sb.toString();
				}

			});

			put("list nurses", new CommandHandler() {

				@Override
				public String handleCommand(Entity entity, Pattern p, String value) {
					System.out.println(String.format("Handling [%s] ...",
							p.pattern()));
					StringBuilder sb = new StringBuilder();

					sb.append("Nurse #id\tName\n");
					sb.append("################################\n");

					for (Nurse r : getNursesForEntity(entity))
						sb.append(String.format("%d\t%s\n", r.getId(),
								r.getName()));

					return sb.toString();
				}
			});

			put("list patients", new CommandHandler() {

				@Override
				public String handleCommand(Entity entity, Pattern p, String value) {
					System.out.println(String.format("Handling [%s] ...",
							p.pattern()));
					StringBuilder sb = new StringBuilder();

					sb.append("Patient #id\tName\t\tData\n"); 	
					sb.append("######################################\n");

					for (Patient r : getPatientsForEntity(entity))
						sb.append(String.format("%d\t\t%s\t%s\n", r.getId(),
								r.getName(), r.getData()));

					return sb.toString();
				}

			});
			
			put("read record", new CommandHandler() {

				@Override
				public String handleCommand(Entity entity, Pattern p, String value) {
					System.out.println(String.format("Handling [%s] ...",
							p.pattern()));
					StringBuilder sb = new StringBuilder();

					for (Record r : records) {
						if (r.getId() == Integer.parseInt(p.matcher(value).group(0)) && 
								entity.canAccess(r, EntityWithAccessControl.READ)) {
							sb.append(r.toString());
						}
					}
					
					return sb.toString();
				}

			});
		}
	};
	private HashMap<String, Pattern> commands;

	private String handleCommand(Entity entity, String command, Pattern p, String value) {
		System.out.println(String.format("Handling command [%s] for [#%d, %s]",
				command, entity.getId(), entity.getName()));
		return m.get(command).handleCommand(entity, p, value);
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
		System.out.println("-> New client connecting:");
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