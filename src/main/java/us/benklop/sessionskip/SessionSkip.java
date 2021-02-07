package us.benklop.sessionskip;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class SessionSkip extends Plugin implements Listener
{
    protected boolean debug;
    protected boolean enabled;
    protected Collection listeners;
    protected Collection hostnames;
    protected Collection remoteips;
    protected Collection players;
    
    @Override
    public void onEnable()
    {
        this.debug = this.getConfig().getBoolean("debug", true);
        this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Debug output set to {0}.", this.debug);
        
        this.enabled = this.getConfig().getBoolean("enabled", true);
        this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Plugin state is set to {0}.", this.enabled);
        
        this.listeners = this.getConfig().getList("listeners");
        this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Loaded {0} listener rules.", this.listeners.size());
        
        this.hostnames = this.getConfig().getList("hostnames");
        this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Loaded {0} hostname rules.", this.hostnames.size());
        
        this.remoteips = this.getConfig().getList("remoteips");
        this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Loaded {0} remote IP rules.", this.remoteips.size());
  
        this.players = this.getConfig().getList("players");
        this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Loaded {0} players rules.", this.players.size());
        
        this.getProxy().getPluginManager().registerListener(this, this);
        
        this.getProxy().getPluginManager().registerCommand(this, new SessionSkipCommand(this));
    }
    
    Configuration getConfig() {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
    public void onDisable()
    {
        this.debug = true;
        this.enabled = false;
        this.listeners = null;
        this.hostnames = null;
        this.remoteips = null;
        this.players = null;
        
        this.getProxy().getPluginManager().unregisterListeners(this);
        
        this.getProxy().getPluginManager().unregisterCommands(this);
    }
    
    @EventHandler
    public void onAsyncPreLoginEvent(PreLoginEvent e)
    {    	
        PendingConnection handler = (PendingConnection) e.getConnection();
        
        String playerName = handler.getName();
        String playerIp = handler.getAddress().getAddress().getHostAddress();
        String hostname = handler.getVirtualHost().getHostString();
        
        String playerAdress = playerName+'@'+playerIp + '/'+hostname;
        
        if(!this.enabled)
        {
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Authenticating player {0} ({1}) since SessionSkip is not enabled in the config.", new Object[]{ playerName, handler.getAddress().toString() });
            return;
        }
        
        if(this.debug)
        {
        	this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Connection from player: {0}", playerAdress);
        	
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Connection via listener: {0}", handler.getVirtualHost().toString());
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Connection via hostname: {0}", handler.getVirtualHost().getHostString());
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Connection from remote IP: {0}", handler.getAddress().getAddress().getHostAddress());
        }
        
        if( this.listeners.contains( handler.getVirtualHost().toString() ) )
        {
            handler.setOnlineMode( false );
            this.getProxy().getLogger().log( Level.INFO, "[SessionSkip] Skipping session server authentication for player {0} ({1}) since listener matched {2}", new Object[]{ handler.getName(), handler.getAddress().toString(), handler.getVirtualHost().toString() } );
            return;
        }
        
        if( this.hostnames.contains( handler.getVirtualHost().getHostString() ) )
        {
            handler.setOnlineMode( false );
            this.getProxy().getLogger().log( Level.INFO, "[SessionSkip] Skipping session server authentication for player {0} ({1}) since hostname matched {2}", new Object[]{ handler.getName(), handler.getAddress().toString(), handler.getVirtualHost().getHostString() } );
            return;
        }
        
        if( this.remoteips.contains( handler.getAddress().getAddress().getHostAddress() ) )
        {
            handler.setOnlineMode( false );
            this.getProxy().getLogger().log( Level.INFO, "[SessionSkip] Skipping session server authentication for player {0} ({1}) since remote IP matched {2}", new Object[]{ handler.getName(), handler.getAddress().toString(), handler.getAddress().getAddress().getHostAddress() } );
            return;
        }
        
        // Player specific rules:
        // 		David191212@*/* -> User name is David191212 (do not check player IP / server IP)
        // 		David191212@192.168.1.7/example.com -> User David191212 has ip 192.168.1.7 and must connect to example.com
        // 		David191212@192.168.1.7/* -> User has ip 192.168.1.7
        // 		David191212@*/example.com -> Username is David191212 and must connect to example.com
        
        if(this.players.contains(playerAdress))
        {
            handler.setOnlineMode(false);
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Skipping session server authentication for player {0} ({1}) since player token matched {2}", new Object[]{playerName, playerIp, playerAdress});
            return;
        }
        
        if(this.players.contains(playerName+"@*/*"))
        {
            handler.setOnlineMode(false);
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Skipping session server authentication for player {0} ({1}) since player token matched {2}", new Object[]{playerName, playerIp, playerName+"@*/*"});
            return;
        }
        
        if(this.players.contains(playerName+"@*/"+hostname))
        {
            handler.setOnlineMode(false);
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Skipping session server authentication for player {0} ({1}) since player token matched {2}", new Object[]{playerName, playerIp, playerName+"@*/"+hostname});
            return;
        }
        
        if(this.players.contains(playerName+'@'+playerIp+"/*"))
        {
            handler.setOnlineMode(false);
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Skipping session server authentication for player {0} ({1}) since player token matched {2}", new Object[]{playerName, playerIp, playerName+'@'+playerIp+"/*"});
            return;
        }
        
        if(this.debug)
        {
            this.getProxy().getLogger().log(Level.INFO, "[SessionSkip] Authenticating player {0} ({1}) since no skip rules matched.", new Object[]{ playerName, handler.getAddress().toString() });
        }
    }
}
