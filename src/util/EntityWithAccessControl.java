package util;

public interface EntityWithAccessControl {
	public static int READ = 1;
	public static int WRITE = 2;
	public static int EXECUTE = 3;

	public boolean canAccess(Record journal, int access);

}
