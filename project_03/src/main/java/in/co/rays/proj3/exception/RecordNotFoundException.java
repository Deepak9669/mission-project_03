package in.co.rays.proj3.exception;

/**
 * @author Deepak Verma
 */
public class RecordNotFoundException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public RecordNotFoundException(String msg){
		super(msg);
	}
}