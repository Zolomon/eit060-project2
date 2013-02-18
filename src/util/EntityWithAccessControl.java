package util;

public interface EntityWithAccessControl {
	public static int READ = 1;
	public static int WRITE = 2;
	
	public boolean canAccess(Journal journal, int access);

}
