package util;

public class Patient extends Entity {
	private String data;
	
	
	public Patient(String id, String data, Division division) {
		super(id, division);
		this.data = data;
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
