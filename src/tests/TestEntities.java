package tests;

import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import server.Server;
import util.Division;
import util.Doctor;
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
		divisions
				.put(6, new Division("Women's Health and Pediatrics Division"));

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
				.get(4)));

		server = new Server();

		try {
			TestPatientReadAccess();
			TestNurseReadAccess();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void TestPatientReadAccess() throws AccessDeniedException {
		// Should only be able to read patient's own journal.

		Patient p0 = patients.get(0);
		Journal j = server.createJournal(p0, docs.get(0), nurses.get(0));
		boolean result = true;

		result &= server.readData(j, p0).equals(p0.getData());
		result &= server.readData(j, patients.get(1)) == null;

		// if (result == false) throw new
		// Exception("TestPatientCanReadOwnRecord");

		System.out.println(String.format("TestPatientReadAccess: %s",
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

}
