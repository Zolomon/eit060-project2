package util;

public class Patient extends Entity {
	private String name;
	private String data;
	
	
	public Patient(String name, String data, Division division) {
		super(division);
		
		this.name = name;
		this.data = data;
	}

	@Override
	public boolean canAccess(Journal journal, int access) {
		if (access == EntityWithAccessControl.READ && journal.getPatient() == this)
			return true;

		return false;
	}

}
