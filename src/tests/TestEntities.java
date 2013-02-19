package tests;

import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import server.Server;
import util.Division;
import util.Doctor;
import util.GovernmentAgent;
import util.Journal;
import util.Nurse;
import util.Patient;

public class TestEntities {
	private static HashMap<Integer, Division> divisions;
	private static List<Doctor> docs;
	private static List<Nurse> nurses;
	private static List<Patient> patients;
	private static Server server;
	private static ArrayList<Journal> journals;
	private static GovernmentAgent agent;

	public static void main(String[] args) {
		divisions = new HashMap<Integer, Division>();

		// http://en.wikipedia.org/wiki/Uppsala_University_Hospital#Divisions
		divisions.put(0, new Division(
				"Diagnostics, Anesthesia and Technology Division"));
		divisions.put(1, new Division("Emergency and Rehabilitation Division"));
		divisions.put(2, new Division("Oncology, Thorax and Medical Division"));
		divisions.put(3, new Division("Neurology Division"));
		divisions.put(4, new Division("Psychiatry Division"));
		divisions.put(5, new Division("Surgery Division"));
		divisions.put(6, new Division("Women's Health and Pediatrics Division"));
		divisions.put(7, new Division("Socialstyrelsen"));

		docs = new ArrayList<Doctor>();
		docs.add(new Doctor("Fulgore d0_0", divisions.get(0)));
		docs.add(new Doctor("Fulgore d0_1", divisions.get(0)));

		docs.add(new Doctor("Riptor d1_0", divisions.get(1)));

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

		server = new Server();

		try {
			TestPatientReadAccess();
			TestPatientWriteAccess();
			TestNurseReadAccess();
			TestNurseWriteAccess();
			TestDoctorReadAccess();
			TestDoctorWriteAccess();
			TestGovernmentAgentReadAccess();
			TestGovernmentAgentDeleteAccess();

		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private static void TestPatientReadAccess() throws AccessDeniedException {
		// A pateint should only be able to read patient's own journal.

		Patient p0 = patients.get(0);
		Journal j = server.createJournal(p0, docs.get(0), nurses.get(0));
		boolean result = true;

		result &= server.readData(j, p0).equals(p0.getData());
		result &= server.readData(j, patients.get(1)) == null;

		System.out.println(String.format("TestPatientReadAccess: %s",
				result ? "succeeded" : "failed"));
	}

	private static void TestPatientWriteAccess() throws AccessDeniedException {
		// A patient should be able to write to no journals.

		Patient p0 = patients.get(0);
		Journal j1 = server.createJournal(p0, docs.get(0), nurses.get(0));

		boolean result = true;
		// Patient's can't write to any journal.
		String actual = "new value 1";
		String expected = p0.getData();
		server.writeData(j1, p0, actual);
		result &= server.readData(j1, nurses.get(0)).equals(expected);

		String newVal2 = "new value 2";
		server.writeData(j1, nurses.get(1), newVal2);
		result &= server.readData(j1, nurses.get(0)).equals(expected);

		System.out.println(String.format("TestPatientWriteAccess: %s",
				result ? "succeeded" : "failed"));

	}

	private static void TestNurseReadAccess() throws AccessDeniedException {
		// A nurse should be able to read its patient's journal, and other
		// patients' journals who are in the same division.

		Patient p0 = patients.get(0);
		Patient p1 = patients.get(1);
		Journal j1 = server.createJournal(p0, docs.get(0), nurses.get(0));
		Journal j2 = server.createJournal(p1, docs.get(0), nurses.get(0));
		boolean result = true;
		// Nurse can read its patient's journal
		result &= server.readData(j1, nurses.get(0)).equals(p0.getData());
		// Nurse in same division can read patient journals who exist in the
		// same division
		result &= server.readData(j1, nurses.get(1)).equals(p0.getData());

		// Nurse outside of patient's division should not be able to read its
		// journal
		result &= server.readData(j1, nurses.get(2)) == null;

		System.out.println(String.format("TestNurseReadAccess: %s",
				result ? "succeeded" : "failed"));
	}

	private static void TestNurseWriteAccess() throws AccessDeniedException {
		// A nurse should be able to write to its patient's journal, and to
		// noone else's.

		Patient p0 = patients.get(0);
		Journal j1 = server.createJournal(p0, docs.get(0), nurses.get(0));

		boolean result = true;
		// Nurse can write to its patient's journal
		String actual = "new value 1";
		String expected = "new value 1";
		server.writeData(j1, nurses.get(0), actual);
		result &= server.readData(j1, nurses.get(0)).equals(expected);

		// Nurses are not allowed to write to other patients' journals.
		actual = "new value 2";
		server.writeData(j1, nurses.get(1), actual);
		result &= server.readData(j1, nurses.get(0)).equals(expected);

		System.out.println(String.format("TestNurseWriteAccess: %s",
				result ? "succeeded" : "failed"));
	}

	private static void TestDoctorReadAccess() throws AccessDeniedException {
		// A doctor should be able to read its patient's journals, and
		// those in the same division

		Patient p0 = patients.get(0);
		Patient p1 = patients.get(1);
		Patient p2 = patients.get(2);

		Journal j0 = server.createJournal(p0, docs.get(0), nurses.get(0));
		Journal j1 = server.createJournal(p1, docs.get(1), nurses.get(1));
		Journal j2 = server.createJournal(p2, docs.get(2), nurses.get(2));

		boolean result = true;
		// Doctor can read its patients' journals
		String expected = p0.getData();
		result &= server.readData(j0, docs.get(0)).equals(expected);

		// Doctor can read its division's journals
		expected = p1.getData();
		result &= server.readData(j1, docs.get(0)).equals(expected);

		// Doctor can't read outside its division
		expected = p2.getData();
		result &= server.readData(j2, docs.get(0)) == null;

		System.out.println(String.format("TestDoctorReadAccess: %s",
				result ? "succeeded" : "failed"));
	}

	private static void TestDoctorWriteAccess() throws AccessDeniedException {
		// A doctor can write to its patients' journals, but no other.

		Patient p0 = patients.get(0);
		Patient p1 = patients.get(1);
		Patient p2 = patients.get(2);

		Journal j0 = server.createJournal(p0, docs.get(0), nurses.get(0));
		Journal j1 = server.createJournal(p1, docs.get(1), nurses.get(1));
		Journal j2 = server.createJournal(p2, docs.get(2), nurses.get(2));

		boolean result = true;

		// Doctor can write to its patient's journal
		String actual = "new value 1";
		String expected = "new value 1";
		server.writeData(j0, docs.get(0), actual);
		result &= server.readData(j0, docs.get(0)).equals(expected);

		// Doctors are not allowed to write to other patients' journals.
		actual = "new value 2";
		expected = p1.getData();
		server.writeData(j1, docs.get(0), actual);
		result &= server.readData(j1, nurses.get(0)).equals(expected);

		System.out.println(String.format("TestDoctorWriteAccess: %s",
				result ? "succeeded" : "failed"));
	}

	private static void TestGovernmentAgentReadAccess()
			throws AccessDeniedException {
		Patient p0 = patients.get(0);
		Patient p1 = patients.get(1);
		Patient p2 = patients.get(2);

		Journal j0 = server.createJournal(p0, docs.get(0), nurses.get(0));
		Journal j1 = server.createJournal(p1, docs.get(1), nurses.get(1));
		Journal j2 = server.createJournal(p2, docs.get(2), nurses.get(2));

		boolean result = true;
		// Doctor can read its patients' journals
		String expected = p0.getData();
		result &= server.readData(j0, agent).equals(expected);

		// Doctor can read its division's journals
		expected = p1.getData();
		result &= server.readData(j1, agent).equals(expected);

		// Doctor can't read outside its division
		expected = p2.getData();
		result &= server.readData(j2, agent).equals(expected);

		System.out.println(String.format("TestGovernmentAgentReadAccess: %s",
				result ? "succeeded" : "failed"));
	}

	private static void TestGovernmentAgentDeleteAccess() throws AccessDeniedException {
		Patient p0 = patients.get(0);

		Journal j0 = server.createJournal(p0, docs.get(0), nurses.get(0));

		boolean result = true;
		// Doctor can read its patients' journals
		server.deleteJournal(j0, agent);
		result &= j0.toDelete() == true;

		System.out.println(String.format("TestGovernmentAgentDeleteAccess: %s",
				result ? "succeeded" : "failed"));
	}
}
