package skaro.pokedex.core;

import java.util.ArrayList;
import java.util.List;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class PrivilegeChecker 
{
	private static IGuild supportGuild;
	private static List<IRole> privilegedRoles;
	
	public PrivilegeChecker(IDiscordClient client)
	{
		supportGuild = client.getGuildByID(339583821072564255L);
		privilegedRoles = new ArrayList<IRole>();
		
		for(Privilege priv : Privilege.values())
			privilegedRoles.add(supportGuild.getRoleByID(priv.getID()));
	}
	
	public boolean userIsPrivileged(IUser user)
	{
		List<IRole> userRoles = supportGuild.getRolesForUser(user);
		
		for(IRole role : userRoles)
			if(privilegedRoles.contains(role))
				return true;
		
		return false;
	}
}
