package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.*;

public class Server {

	public static void main(String[] args) {
		HashMap<Integer, Division> divisions = new HashMap<Integer, Division>();
		
		
		// http://en.wikipedia.org/wiki/Uppsala_University_Hospital#Divisions
		divisions.put(0, new Division("Diagnostics, Anesthesia and Technology Division"));
		divisions.put(1, new Division("Emergency and Rehabilitation Division"));
		divisions.put(2, new Division("Oncology, Thorax and Medical Division"));
		divisions.put(3, new Division("Neurology Division"));
		divisions.put(4, new Division("Psychiatry Division"));
		divisions.put(5, new Division("Surgery Division"));
		divisions.put(6, new Division("Women's Health and Pediatrics Division"));
		
		// http://en.wikipedia.org/wiki/List_of_Killer_Instinct_characters
		List<Doctor> docs = new ArrayList<Doctor>();
		docs.add(new Doctor("Fulgore d0_0", divisions.get(0)));
		docs.add(new Doctor("Fulgore d0_1", divisions.get(0)));
		
		docs.add(new Doctor("Riptor d1_0", divisions.get(1)));
		
		List<Nurse> nurses = new ArrayList<Nurse>();
		nurses.add(new Nurse("Sabrewulf n0_0", divisions.get(0)));
		nurses.add(new Nurse("Sabrewulf n0_1", divisions.get(0)));
		
		nurses.add(new Nurse("Eyedol n1_0", divisions.get(1)));
		
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(new Patient("Spinal p5_0", "Broken back", divisions.get(5)));
		patients.add(new Patient("Spinal p5_1", "Broken toe", divisions.get(5)));
		
		patients.add(new Patient("Spinal p4_0", "Fractured skull", divisions.get(4)));
		
	}

}
