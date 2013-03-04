package util;

public abstract class Entity implements EntityWithAccessControl {
	private Division division;
	private String name;
	private static int id;
	private int myId;
	protected String myType = "Entity";
	private String pass;
	private int pw_fault = 0;

	public Entity(String name, Division division, String pass) {
		this.name = name;
		this.division = division;
		this.myId = id++;
		this.pass = pass;
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
	
	public String getPass(){
		return pass;
	}
	
	public void incrementPw_fault(){
		pw_fault++;
		
	}
}
