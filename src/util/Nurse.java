package util;

public class Nurse extends Entity {

	public Nurse(Division division) {
		super(division);
	}

	@Override
	public boolean canAccess(Journal journal, int access) {
		if ((access == EntityWithAccessControl.READ || access == EntityWithAccessControl.WRITE)
				&& journal.getNurse() == this)
			return true;
		if (access == EntityWithAccessControl.READ
				&& journal.getDivision() == this.getDivision())
			return true;

		return false;
	}

}
