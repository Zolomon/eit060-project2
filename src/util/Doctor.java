package util;

public class Doctor extends Entity {

	public Doctor(Division division) {
		super(division);
	}

	@Override
	public boolean canAccess(Journal journal, int access) {
		if ((access == EntityWithAccessControl.READ || access == EntityWithAccessControl.WRITE)
				&& journal.getDoctor() == this)
			return true;
		if (access == EntityWithAccessControl.READ
				&& journal.getDivision() == this.getDivision())
			return true;
		
		return false;
	}

}
