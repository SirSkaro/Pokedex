package skaro.pokedex.services;

public class ServiceException extends Exception 
{
	private static final long serialVersionUID = -1748009148112647973L;

	public ServiceException(String msg)
	{
		super(msg);
	}
}
