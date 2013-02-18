package util;

public abstract class Entity implements EntityWithAccessControl {
	private Division division;
	
	public Entity(Division division) {
		this.division = division;
	}
	
	public Division getDivision() {
		return division;
	}
}
