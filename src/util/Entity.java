package util;

public abstract class Entity implements EntityWithAccessControl {
	private Division division;
	private String name;
	private static int id;
	private int myId;
	protected String myType = "Entity";
	
	public Entity(String name, Division division) {
		this.name = name;
		this.division = division;
		this.myId = id++;
	}
	
	public int getId() {
		return myId;
	}
	
	public String getName() {
		return name;
	}
	
	public Division getDivision() {
		return division;
	}

	public String getType() {
		return myType;
	}
}
