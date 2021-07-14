package skaro.pokeflex.api;

public class PokeFlexException extends Exception
{
	private static final long serialVersionUID = 5501053852273757896L;

	public PokeFlexException() { super(); }
	public PokeFlexException(String message) { super(message); }
	public PokeFlexException(String message, Throwable cause) { super(message, cause); }
	public PokeFlexException(Throwable cause) { super(cause); }
}
