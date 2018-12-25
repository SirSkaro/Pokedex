package skaro.pokedex.core;

public interface IServiceManager 
{
	public IService getService(ServiceType type) throws ServiceException;
}

enum ServiceType
{
	COLOR,
	EMOJI,
	POKE_FLEX,
	PERK,
	DISCORD,
	CONFIG,
	COMMAND,
	;
}

class ServiceException extends Exception
{
	private static final long serialVersionUID = -1748009148112647973L;

	public ServiceException(String msg)
	{
		super(msg);
	}
}