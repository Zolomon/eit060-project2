package util;

public abstract class Entity implements EntityWithAccessControl {
	private Division division;
	private String id;
	
	public Entity(String id, Division division) {
		this.id = id;
		this.division = division;
	}
	
	public String getId() {
		return id;
	}
	
	public Division getDivision() {
		return division;
	}
}
