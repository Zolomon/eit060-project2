package util;

import java.nio.file.AccessDeniedException;

public class Record {

	private Patient patient;
	private Nurse nurse;
	private Division division;
	private Doctor doctor;
	private String data;
	private boolean toDelete;
	private int myId;
	private static int id;

	public Record(Patient patient, Doctor doctor, Nurse nurse, String data) {
		this.division = patient.getDivision();
		this.doctor = doctor;
		this.nurse = nurse;
		this.patient = patient;
		this.data = data;
		this.myId = id++;
	}

	public int getId() {
		return myId;
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

	public String readData(EntityWithAccessControl entity)
			throws AccessDeniedException {
		if (entity.canAccess(this, EntityWithAccessControl.READ))
			return this.data;

		throw new AccessDeniedException("Not enough access to read.");
	}

	public void writeData(EntityWithAccessControl entity, String data)
			throws AccessDeniedException {
		if (entity.canAccess(this, EntityWithAccessControl.WRITE)) {
			this.data = data;
			return;
		}
		throw new AccessDeniedException("Not enough access to write.");
	}

	public boolean delete(EntityWithAccessControl entity)
			throws AccessDeniedException {
		if (entity.canAccess(this, EntityWithAccessControl.EXECUTE)) {
			toDelete = true;
			return true;
		}

		throw new AccessDeniedException("Not enough access to delete.");
	}

	public boolean toDelete() {
		return toDelete;
	}

	public String toString() {
		return String
				.format("Record #%d\nPatient #%d, Patient: %s\nNurse #%d, Nurse: %s\nDoctor #%d, Doctor: %s\nData: %s",
						this.myId, this.patient.getId(),
						this.patient.getName(), this.nurse.getId(),
						this.nurse.getName(), this.doctor.getId(),
						this.doctor.getName(), this.data);
	}
}
