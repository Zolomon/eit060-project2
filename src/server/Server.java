package server;

import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import util.*;

public class Server {
	private List<Journal> journals = new ArrayList<Journal>();

	public static void main(String[] args) {

		while (true) {
			System.out.println("Running server ...");
		}
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

	public void deleteJournal(Journal journal, EntityWithAccessControl entity) throws AccessDeniedException {
		try {
			journal.delete(entity);
		} catch (AccessDeniedException e) {
			e.printStackTrace();
			// TODO: LOG
		}
	}
}
