package util;

import java.nio.file.AccessDeniedException;

public class Journal {

	private Patient patient;
	private Nurse nurse;
	private Division division;
	private Doctor doctor;
	private String data;

	public Journal(Patient patient, Division division, Doctor doctor,
			Nurse nurse, String data) {
		this.division = division;
		this.doctor = doctor;
		this.nurse = nurse;
		this.patient = patient;
		this.data = data;
	}

	public Patient getPatient() {
		return patient;
	}

	public Nurse getNurse() {
		return nurse;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public Division getDivision() {
		return division;
	}

	public String getData(EntityWithAccessControl entity)
			throws AccessDeniedException {
		if (entity.canAccess(this, EntityWithAccessControl.READ))
			return this.data;

		throw new AccessDeniedException("Not enough access.");
		// FIXME: LOG
	}

	public void writeData(EntityWithAccessControl entity, String data)
			throws AccessDeniedException {
		if (entity.canAccess(this, EntityWithAccessControl.WRITE)) {
			this.data = data;
			return;
		}
		throw new AccessDeniedException("Not enough access.");
		// FIXME: LOG
	}

}
