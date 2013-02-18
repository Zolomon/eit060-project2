package util;

public class GovernmentAgent extends Entity {

	public GovernmentAgent(String id, Division division) {
		super(id, division);
	}

	@Override
	public boolean canAccess(Journal journal, int access) {
		if (access == EntityWithAccessControl.READ)
			return true;

		// To delete a journal
		if (access == EntityWithAccessControl.EXECUTE)
			return true;

		return false;
	}

}
