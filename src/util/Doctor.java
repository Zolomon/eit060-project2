package util;

public class Doctor extends Entity {

	public Doctor(String name, Division division) {
		super(name, division);
		this.myType = "Doctor";
	}

	@Override
	public boolean canAccess(Record journal, int access) {
		if ((access == EntityWithAccessControl.READ || access == EntityWithAccessControl.WRITE)
				&& journal.getDoctor() == this)
			return true;
		if (access == EntityWithAccessControl.READ
				&& journal.getDivision() == this.getDivision())
			return true;

		// To create a new journal
		if (access == EntityWithAccessControl.EXECUTE
				&& journal.getDivision() == this.getDivision())
			return true;

		return false;
	}

}
