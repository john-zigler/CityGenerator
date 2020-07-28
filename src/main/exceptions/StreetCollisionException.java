package main.exceptions;

public class StreetCollisionException extends Exception {
	private static final long serialVersionUID = -8034408051573868084L;

	public StreetCollisionException(String message) {
		super(message);
	}

	public StreetCollisionException() {
		
	}
}
