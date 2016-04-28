package com.simonorj.SimonJPlug;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Statement;
import java.text.MessageFormat;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.ChatColor;

public final class Simon extends JavaPlugin {
    private static final String ALERT_TAG = "」4[Alerts] ";
    private static final String ARCANE_TAG = "」6[ArcaneSurvival] ";
    private static final String TAG_TAG = "」e[Tag] ";
    
    private String[] JDBC = {"","",""};
    private String REPORT_TAG = null;
    private String REPORT_COLOR = "」d";
	private HashSet<UUID> atogState = null;
    private String AC_TAG = null;
    private MessageFormat AC_FORMAT = null;
    private boolean ALERT_ENABLED;
	private MessageFormat ALERT_FORMAT = null;
	private HashSet<String> ALERT_EXEMPT = null;
	private HashSet<String> ALERT_TOALL = null;
	private HashSet<UUID> TAG_ON = new HashSet<UUID>();
	private UUID TAG_IT = null;
	
	private void loadConfig() {
		JDBC[0] = "jdbc:mysql://"+this.getConfig().getString("sql.hostname")+"/"+this.getConfig().getString("sql.database");
		JDBC[1] = this.getConfig().getString("sql.username");
		JDBC[2] = this.getConfig().getString("sql.password");
		REPORT_TAG = ChatColor.translateAlternateColorCodes('&',this.getConfig().getString("report.prefix"))+" ";
		AC_TAG = ChatColor.translateAlternateColorCodes('&',this.getConfig().getString("adminchat.prefix"))+" ";
    	AC_FORMAT = new MessageFormat(ChatColor.translateAlternateColorCodes('&',this.getConfig().getString("adminchat.format")));
    	ALERT_ENABLED = this.getConfig().getBoolean("alert.enabled");
    	ALERT_FORMAT = new MessageFormat(ChatColor.translateAlternateColorCodes('&',this.getConfig().getString("alert.format")));
    	ALERT_EXEMPT = new HashSet<String>(this.getConfig().getStringList("alert.commands.exempt"));
    	ALERT_TOALL = new HashSet<String>(this.getConfig().getStringList("alert.commands.notifyall"));
	}
	
	@Override
    public void onEnable() {
    	this.saveDefaultConfig();
    	atogState = new HashSet<UUID>();
    	loadConfig();
		getServer().getPluginManager().registerEvents(new chatListener(), this);
    	getLogger().info("SimonPlugin by SimonOrJ for Arcane Survival. Command name: \"simonplugin\"");
    	getLogger().info("Included commands: a atoggle nether ping report simon simonorj colwi");
    }
 
    @Override
    public void onDisable() {
//    	this.getConfig().set("alert.commands.exempt", new ArrayList<String>(ALERT_EXEMPT));
//    	this.getConfig().set("alert.commands.notifyall", new ArrayList<String>(ALERT_TOALL));
    	this.saveConfig();
    	getLogger().info("SimonPlugin Disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("a") && sender.hasPermission("simonplugin.a")) {
    		if (args.length == 0) {
        		if (!(sender instanceof Player)) {
        			getServer().getConsoleSender().sendMessage(AC_TAG + "」7Usage: \"a <message>\".");
        			return true;
        		}
    			sender.sendMessage(AC_TAG + "」7Your toggle is currently "+(atogState.contains(((Player)sender).getUniqueId())?"」aon":"」coff")+"」7. Usage: /a <message>");
    			return true;
    		}
    		return adminChatFunction(sender,args);
    	}
    	if (cmd.getName().equalsIgnoreCase("atoggle") && sender.hasPermission("simonplugin.a")) {
    		if (!(sender instanceof Player)) {
    			getServer().getConsoleSender().sendMessage(AC_TAG + "」7You must be a player.");
    			return true;
    		}
    		Player pl = (Player)sender;
    		UUID pID = pl.getUniqueId();
    		
    		if (atogState.contains(pID))
    		{
    			atogState.remove(pID);
    			sender.sendMessage(AC_TAG + "」7Staff chat has been toggled 」coff」7.");
    		}
    		else
    		{
    			atogState.add(pID);
    			sender.sendMessage(AC_TAG + "」7Staff chat has been toggled 」aon」7.");
    		}
    		return true;
    	}
    	if ((cmd.getName().equalsIgnoreCase("graylist") || cmd.getName().equalsIgnoreCase("greylist"))) {
    	    //Connection conn = null;
    	    //PreparedStatement ps = null;
    	    if(!sender.hasPermission("simonplugin.a")) {
	    	    sender.sendMessage(ARCANE_TAG + "」7You are "+(sender.hasPermission("simonplugin.trusted")?"":"not ")+"on the greylist!");
	    	    if(sender.hasPermission("simonplugin.trusted"))
	    	    	sender.sendMessage(ARCANE_TAG + "」7Talk to a staff member to become greylisted.");
	    	    return true;
    	    }
    	    else {
    	    	if(args.length == 0) {
	    	    	sender.sendMessage(ARCANE_TAG + "」7You are a staff member!");
	    	    	return true;
    	    	}
    	    	if(args.length == 1) {
    	    		((Player)sender).performCommand("pex group trusted user add "+args[0]);
    	    	}
    	    }
        	/*
    	    try {
    	    	Class.forName("com.mysql.jdbc.Driver"); // Register
    	    	conn = DriverManager.getConnection(JDBC[0],JDBC[1],JDBC[2]); // Connect
    	    	String sql = "SELECT ign,reg_date FROM greylistApp where status=0 LIMIT ?,?;";
    	    	ps = conn.prepareStatement(sql);
    	    	ps.setInt(1, 0);
    	    	ps.setInt(2, 10);
    	    	
    	    	conn.close();
    	    } catch(Exception e) {
    	    	e.printStackTrace();
    	    	sender.sendMessage(REPORT_TAG + "」7Report wasn't able to record your report.");
    	    	sender.sendMessage(REPORT_TAG + "」7Please contact a staff member instead.");
    	    	return true;
    	    }
    	    */
    	}
    	if (cmd.getName().equalsIgnoreCase("report")) {
    		if (!(sender instanceof Player)) {
    			getServer().getConsoleSender().sendMessage(REPORT_TAG + "」7You must be a player.");
    			return true;
    		}
    		if (args.length == 0) {
    			sender.sendMessage(REPORT_TAG + "」7Usage: /report <anything to report>");
    			if (sender.hasPermission("simonplugin.a")) {
    				sender.sendMessage(REPORT_TAG + "」7Admin usage: /reports");
    			}
    			return true;
    		}
    	    Connection conn = null;
    	    Player p = (Player)sender;
	    	Location loc = p.getLocation();
    	    String m = String.join(" ", args);
    		adminChatOut(REPORT_TAG + "」7Report by 」e" +p.getName() + "」7 at " + loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + ":");
    		adminChatOut(REPORT_TAG + "」7\"" + m +"\"");
    	    try {
    	    	Class.forName("com.mysql.jdbc.Driver"); // Register
    	    	conn = DriverManager.getConnection(JDBC[0],JDBC[1],JDBC[2]); // Connect
    	    	String sql = "INSERT INTO mcreports (user,world,x,y,z,report) values (?,?,?,?,?,?)";
    	    	PreparedStatement ps = conn.prepareStatement(sql);
    	    	ps.setString(1, sender.getName());
    	    	ps.setString(2, loc.getWorld().getName());
    	    	ps.setInt(3, loc.getBlockX());
    	    	ps.setInt(4, loc.getBlockY());
    	    	ps.setInt(5, loc.getBlockZ());
    	    	ps.setString(6, m);
    	    	ps.execute();
    	    	conn.close();
    	    } catch(Exception e) {
    	    	e.printStackTrace();
    	    	sender.sendMessage(REPORT_TAG + "」7Report wasn't able to record your report.");
    	    	sender.sendMessage(REPORT_TAG + "」7Please contact a staff member instead.");
    	    	return true;
    	    }
	    	sender.sendMessage(REPORT_TAG + "」7Your report has been recorded. Thank you.");
	    	
	    	return true;
    	}
    	if (cmd.getName().equalsIgnoreCase("reports") && sender.hasPermission("simonplugin.a")) {
    	    Connection conn = null;
    	    try {
    	    	Integer p = (args.length == 1 ? Integer.parseInt(args[0]) : 1);
    	    	Class.forName("com.mysql.jdbc.Driver"); // Register
    	    	conn = DriverManager.getConnection(JDBC[0],JDBC[1],JDBC[2]); // Connect
    	    	String sql = "SELECT id,user,world,x,y,z,report,status,assignee FROM mcreports WHERE status <> 0 LIMIT ?,?";
    	    	PreparedStatement ps = conn.prepareStatement(sql);
    	    	ps.setInt(1, (p-1)*4);
    	    	ps.setInt(2, 4);
    	    	ResultSet rs = ps.executeQuery();
    	    	sender.sendMessage(REPORT_TAG + "」7Reports (Page " + p + "):");
    	    	while(rs.next()) {
    	    		/* Status codes:
    	    		 * 0 - Resolved
    	    		 * 1 - Unchecked
    	    		 * 2 - Invalid
    	    		 * 3 - Investigating
    	    		 * 4 - 
    	    		 */
    	    		sender.sendMessage(REPORT_COLOR + "| > 」7" + rs.getInt("id") + ". 」e" + rs.getString("user") + "」7 at " + rs.getString("world") + " " + rs.getInt("x") + " " + rs.getInt("y") + " " + rs.getInt("z") + (rs.getString("assignee") == null ? "" : " assigned to " + rs.getString("assignee")) +":");
    	    		sender.sendMessage(REPORT_COLOR + "| > 」7\"" + rs.getString("report") + "\"");
    	    	}
    	    	conn.close();
    	    	return true;
    	    } catch(NumberFormatException e) {
    	    	
    	    } catch(Exception e) {
    	    	e.printStackTrace();
    	    	sender.sendMessage(REPORT_TAG + "」7Report can't connect to the database. Contact SimonOrJ.");
    	    }
    	    return true;
    	}
    	if (cmd.getName().equalsIgnoreCase("nether")) {
	    	String m = "」7Possible number of command arguments are none, two, or three numbers.";
    		Location loc = ((Player)sender).getLocation();
		    if(loc.getWorld().getEnvironment() != World.Environment.NORMAL && loc.getWorld().getEnvironment() != World.Environment.NETHER) {
				sender.sendMessage("」7Are you sure nether portals can exist here?");
				return true;
		    } else if (args.length == 1 || args.length > 3){
				sender.sendMessage(m);
				return true;
		    }
	    	int[] l = {0,0,0};
	    	if(args.length == 0) {
	    		l[0] = loc.getBlockX();
	    		l[1] = loc.getBlockY();
	    		l[2] = loc.getBlockZ();
	    	}
	    	else if(args.length == 2) {
	    		l[0] = Integer.parseInt(args[0]);
	    		l[1] = 0;
	    		l[2] = Integer.parseInt(args[1]);
	    	}
	    	else if(args.length == 3) {
	    		try {
	    		l[0] = Integer.parseInt(args[0]);
	    		l[1] = Integer.parseInt(args[1]);
	    		l[2] = Integer.parseInt(args[2]);
	    		} catch(NumberFormatException x) {
					sender.sendMessage(m);
	    			return true;
	    		}
	    	}
	    	String w;
	    	if(loc.getWorld().getEnvironment() == World.Environment.NORMAL) {
	    		w = "nether";
	    		l[0] = l[0]/8;
	    		l[1] = l[1]/2;
	    		l[2] = l[2]/8;
	    	}
	    	else {
		    	w = "overworld";
	    		l[0] = l[0]*8;
	    		l[1] = l[1]*2;
	    		l[2] = l[2]*8;
		    }
	    	sender.sendMessage("」7The corresponding "+w+" coordinates is: " + l[0] + " " + l[1] + " " + l[2]);
    		return true;
    	}
    	if (cmd.getName().equalsIgnoreCase("simon")||cmd.getName().equalsIgnoreCase("simonorj")) {
    		sender.sendMessage("」cYou wrote something illegal. Rawr.");
    		return true;
    	}
    	if (cmd.getName().equalsIgnoreCase("ping")) {
    		sender.sendMessage("pong!");
    		return true;
    	}
    	if (cmd.getName().equalsIgnoreCase("colwi") && sender.hasPermission("simonplugin.colwi")) {
    		String m = "http://arcaneminecraft.com/mod/coreprotect/?";
    		String ma = "";
    		for (int i = 0;i<args.length;i++) {
    			String[] s = args[i].split(":",2);
    			if (s.length==1) {
    				if (s[0].length() < 1) {
    					ma = "";
    					break;
    				}
    				else ma += "u[]="+s[0]+"&";
    			}
    			else switch (s[0]){
    				case "r":
    					Location loc = ((Player)sender).getLocation();
    					ma += "x="+loc.getBlockX()+"&y="+loc.getBlockY()+"&z="+loc.getBlockZ()+"&r="+s[1]+"&";
    					break;
    				case "a":
    					if(s[1].equalsIgnoreCase("transaction")){
    						ma += "a[]=container&";
    						break;
    					}
    				case "b":
    				case "u":
   						ma += s[0]+"%5B%5D="+s[1]+"&";
    					break;
    				case "e":
    					// Need to implement code
    					break;
    				default:
    					ma += s[0]+"="+s[1]+"&";
    			}
    		}
    		m = (m+ma).substring(0, (m+ma).length()-1);
    		sender.sendMessage("」f----- 」3CoreProtect LWI Link」f -----");
    		sender.sendMessage("」3Link: 」f"+m);
    		if (ma.length() == 0) sender.sendMessage("」3Usage: 」fWrite the arguments as if you're using \"/co l\".");
    		else sender.sendMessage("       」7Remember to press \"Make a Lookup\" button!");
    		return true;
    	}
    	if (cmd.getName().equalsIgnoreCase("simonplugin")) {
    		if(args.length == 0) {
	    		sender.sendMessage("」7SimonPlugin package is created by SimonOrJ.");
	    		sender.sendMessage("」7Available commands are /greylist, /graylist, /nether, /ping, /report, /simonplugin, and /simonorj.");
	    		if (sender.hasPermission("simonplugin.a")) {
	        		sender.sendMessage("」7Staff are able to use /a, /atoggle, and /colwi.");
		    		if (sender.hasPermission("simonplugin.op")) {
		        		sender.sendMessage("」7Admins are able to use subcommands \"alert\" and \"reload\".");
		        		sender.sendMessage("」7Trusted permission node: simonplugin.trusted");
		        		sender.sendMessage("」7Staff permission node: simonplugin.a");
		        		sender.sendMessage("」7Admin permission node: simonplugin.op");
		    		}
	    		}
	    		return true;
    		}
    		if(sender.hasPermission("simonplugin.op")) {
	    		if(args[0].equalsIgnoreCase("reload")) {
	    			this.reloadConfig();
	    			loadConfig();
	    			sender.sendMessage("SimonPlugin Reloaded.");
	    			return true;
	    		}
	        	if (args[0].equalsIgnoreCase("alert")) {
	        		if(args.length==1) {
	        			sender.sendMessage(ALERT_TAG + "」cAlert management module: /simonplugin alert ...");
	        			sender.sendMessage("」7> <on|off> - turns the alerts on/off for all staff.");
	        			sender.sendMessage("」7> <exempt|notifyall|flags> - returns exempted/staff-broadcasted commands.");
	        			sender.sendMessage("」7> <exempt|notifyall> [command|-command]... - adds or removes flags.");
	        			return true;
	        		}
	        		if(args.length==2) {
	        			if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) {
	        				ALERT_ENABLED = args[1].equalsIgnoreCase("on");
	        				this.getConfig().set("alert.enabled", ALERT_ENABLED);
	        				Bukkit.broadcast(ALERT_TAG + "」7Alerts has been " + (ALERT_ENABLED?"enabled":"disabled") + ".","simonplugin.op");
	        				return true;
	        			}
	            		if(!args[1].equalsIgnoreCase("exempt"))
	            			sender.sendMessage("」7NotifyAll: " + Arrays.toString(ALERT_EXEMPT.toArray(new String[0])));
	            		if(!args[1].equalsIgnoreCase("notifyall"))
	            			sender.sendMessage("」7Exempted: " + Arrays.toString(ALERT_TOALL.toArray(new String[0])));
	            		return true;
	        		}
	        		if(!args[1].equalsIgnoreCase("exempt")&&!args[1].equalsIgnoreCase("notifyall")) {
	        			sender.sendMessage("」7> <exempt|notifyall> [command|-command]... - adds or removes flags.");
	        			return true;
	        		}
	        		HashSet<String> S = null;
	        		S = (args[1].equalsIgnoreCase("exempt")?ALERT_EXEMPT:ALERT_TOALL);
	        		for(int i=2; i < args.length;i++) {
        				String[] t = {args[1].toLowerCase(),args[i]};
	        			if(args[i].startsWith("-")){
	        				t[1] = t[1].substring(1);
	        				if(S.remove(t)) {
	        					this.getConfig().getStringList("alert.commands."+t[0]).remove(t[1]);
	        					Bukkit.broadcast(ALERT_TAG+"」7/"+t[1]+" removed from "+t[0]+".", "simonplugin.op");
	        				}
	        				else {
	        					sender.sendMessage(ALERT_TAG+"」7/"+t[1]+" does not exist in "+t[0]+".");
	        				}
	        			}
	        			else {
	        				if(S.add(args[i])) {
	        					this.getConfig().getStringList("alert.commands."+t[0]).add(t[1]);
	        					Bukkit.broadcast(ALERT_TAG+"」7/"+t[1]+" added to "+t[0]+".", "simonplugin.op");
	        				}
	        				else {
	        					sender.sendMessage(ALERT_TAG+"」7/"+t[1]+" already exists in "+t[0]+".");
	        				}
	        			}
	        		}
	        		return true;
	        	}
    		}
    	}
    	if (cmd.getName().equalsIgnoreCase("tag")) {
    		if(args.length == 0) {
    			if(TAG_ON.contains(((Player)sender).getUniqueId())) {
        			sender.sendMessage(TAG_TAG + "」7" + (TAG_IT == null ? "No one is it. Type 」f\"/tag it\"」7 to volunteer to be it!" : "」f" + Bukkit.getPlayer(TAG_IT).getName() + "」7 is it!"));
    				String list = "";
    				for(UUID d : TAG_ON)
    					list += " " + Bukkit.getPlayer(d).getName();
        			sender.sendMessage("」7Players:" + list);
    			}
    			else
    				sender.sendMessage(TAG_TAG + "」7To join, type \"/tag join\". Type \"/tag help\" for more help.");
    			return true;
    		}
    		switch(args[0]) {
    		case "help":
    			sender.sendMessage(TAG_TAG + "」7A simple, vanilla-friendly tag.");
				sender.sendMessage("」7Tag help:");
				sender.sendMessage("」7/tag join - Join the game of tag.");
				sender.sendMessage("」7/tag leave - Quit the game of tag.");
				sender.sendMessage("」7/tag it - Volunteer be it and tag another player.");
				sender.sendMessage("」7/tag reset - Resets the tag game.");
				break;
    		case "join":
    			if(TAG_ON.add(((Player)sender).getUniqueId())) {
    				sender.sendMessage(TAG_TAG + "」7You have joined in a game of tag! Type \"/tag\" for the tag status anytime.");
        			sender.sendMessage(TAG_TAG + "」7" + (TAG_IT == null ? "No one is it. Type 」f\"/tag it\"」7 to volunteer to be it!" : "」f" + Bukkit.getPlayer(TAG_IT).getName() + "」7 is it!"));
    				String list = "";
    				for(UUID d : TAG_ON)
    					list += " " + Bukkit.getPlayer(d).getName();
        			sender.sendMessage("」7Players:" + list);
    	    		for(UUID u : TAG_ON)
    	    			Bukkit.getPlayer(u).sendMessage(TAG_TAG + "」7" + sender.getName() + " has joined the tag!");
    			}
    			else
    				sender.sendMessage(TAG_TAG + "」7Looks like you're already in the game of tag. Type \"/tag\" for the tag status.");
    			break;
    		case "it":
    			TAG_IT = ((Player)sender).getUniqueId();
	    		for(UUID u : TAG_ON)
	    			Bukkit.getPlayer(u).sendMessage(TAG_TAG + "」7" + sender.getName() + " is now it!");
	    		break;
    		case "leave":
    			if(TAG_ON.remove(((Player)sender).getUniqueId())){
    				sender.sendMessage(TAG_TAG + "」7You have left the game of tag. Farewell!");
    	    		for(UUID u : TAG_ON)
    	    			Bukkit.getPlayer(u).sendMessage(TAG_TAG + "」7" + sender.getName() + " has left the tag!");
    		    	if (TAG_IT.equals(((Player)sender).getUniqueId())) {
    		    		TAG_IT = null;
    			    	for(UUID d : TAG_ON)
    			    		Bukkit.getPlayer(d).sendMessage(TAG_TAG + "」7Uh-oh. The tagger just left. Type \"/tag it\" to be the new tagger!");
    		    	}
    			}
    			else
    				sender.sendMessage(TAG_TAG + "」7Looks like you're already out of the game of tag.");
    			break;
    		case "reset":
		    	for(UUID d : TAG_ON)
		    		Bukkit.getPlayer(d).sendMessage(TAG_TAG + "」7" + sender.getName() + " has resetted the game.");
		    	TAG_ON = new HashSet<UUID>();
		    	TAG_IT = null;
		    	break;
    		default:
    			return false;
    		}
    		return true;
    	}
    	return false;
    }
    
	private boolean adminChatFunction (CommandSender sender, String[] args) {
		Object[] pm = { sender.getName(), ChatColor.translateAlternateColorCodes('&',String.join(" ", args))};
		adminChatOut(AC_FORMAT.format(pm));
		return true;
	}
	
	private void adminChatOut (String m) {
		Bukkit.broadcast(m, "simonplugin.a");
		getServer().getConsoleSender().sendMessage(m);
	}
	
	public final class chatListener implements Listener {
		// Global
		@EventHandler
		public void detectChat (AsyncPlayerChatEvent pce)
		{
			Player pl = pce.getPlayer();
			UUID pID = pl.getUniqueId();
			String msg = pce.getMessage();
			
			// if the player's admin chat is toggled on
			if (atogState.contains(pID))
			{
				if (msg.startsWith("」g")) {
					pce.setMessage(msg.replace("」g",""));
				}
				else
				{
					pce.setCancelled(true);
					
					String[] chat = { msg };
					adminChatFunction((CommandSender)pl,chat);
				}
			}
		}
		
		// Alerts
	    @EventHandler
	    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
	    	if(ALERT_ENABLED) {
		        Object[] pm = {e.getPlayer().getName(),e.getMessage()};
		        String cmd = e.getMessage().split(" ", 2)[0].substring(1);
		        if (ALERT_TOALL.contains(cmd))
		            Bukkit.broadcast(ALERT_FORMAT.format(pm), "simonplugin.a");
		        else if (!ALERT_EXEMPT.contains(cmd)||!e.getPlayer().hasPermission("simonplugin.trusted"))
		            Bukkit.broadcast(ALERT_FORMAT.format(pm), "simonplugin.op");
	    	}
	    }
	    
	    // Tag
	    @EventHandler
	    public void detectTag(EntityDamageByEntityEvent e) {
	    	Entity p = e.getDamager();
	    	Entity dp = e.getEntity();
	    	if (p instanceof Player && dp instanceof Player && (TAG_ON.contains(dp.getUniqueId()) || TAG_IT.equals(p.getUniqueId())) ) {
	    		e.setCancelled(true);
	    		if (TAG_IT.equals(p.getUniqueId())) {
		    		TAG_IT = dp.getUniqueId();
		    		String pl = ((Player)dp).getName();
		    		for(UUID u : TAG_ON)
		    			Bukkit.getPlayer(u).sendMessage(TAG_TAG + "」7" + pl + " is now it!");
	    		}
	    	}
	    }
	    @EventHandler
	    public void onPlayerQuit(PlayerQuitEvent e) {
	        // Called when a player leaves a server
	    	UUID u = e.getPlayer().getUniqueId();
	    	if (TAG_ON.remove(u) && TAG_IT.equals(u)) {
	    		TAG_IT = null;
		    	for(UUID d : TAG_ON)
		    		Bukkit.getPlayer(d).sendMessage(TAG_TAG + "」7Uh-oh. The tagger just left. Type \"/tag it\" to be the new tagger!");
	    	}
	    }
	}
}
