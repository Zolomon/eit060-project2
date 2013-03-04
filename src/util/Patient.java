package util;

public class Patient extends Entity {
	private String data;
	
	
	public Patient(String id, String data, Division division, String pass) {
		super(id, division, pass);
		this.data = data;
		this.myType = "Patient";
	}
	
	public String getData() {
		return this.data;
	}
	
	@Override
	public boolean canAccess(Record journal, int access) {
		if (access == EntityWithAccessControl.READ && journal.getPatient() == this)
			return true;

		return false;
	}

}
