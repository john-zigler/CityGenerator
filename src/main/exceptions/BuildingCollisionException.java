package main.exceptions;

public class BuildingCollisionException extends Exception {
	private static final long serialVersionUID = -5744713166895666023L;

	public BuildingCollisionException(String message) {
		super(message);
	}

	public BuildingCollisionException() {
		
	}
}
