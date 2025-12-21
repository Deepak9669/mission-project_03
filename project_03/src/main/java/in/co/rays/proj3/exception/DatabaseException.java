package in.co.rays.proj3.exception;

/**
 * @author Deepak Verma
 */
public class DatabaseException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public DatabaseException(String msg){
		super(msg);
	}
}