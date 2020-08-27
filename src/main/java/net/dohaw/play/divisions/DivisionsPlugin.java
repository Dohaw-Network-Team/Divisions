package net.dohaw.play.divisions;

import me.c10coding.coreapi.BetterJavaPlugin;
import me.c10coding.coreapi.CoreAPI;
import net.dohaw.play.divisions.commands.ConfirmableCommands;
import net.dohaw.play.divisions.commands.DivisionsCommand;
import net.dohaw.play.divisions.events.GeneralListener;
import net.dohaw.play.divisions.files.DefaultPermConfig;
import net.dohaw.play.divisions.files.MessagesConfig;
import net.dohaw.play.divisions.managers.DivisionsManager;
import net.dohaw.play.divisions.managers.PlayerDataManager;
import net.dohaw.play.divisions.runnables.InviteTimer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

/*
    Divisions Plugin
    Author: c10coding
    Started: 6/16/2020
    Finished: ~
    Description: A better version of Factions
 */

public final class DivisionsPlugin extends BetterJavaPlugin {

    private static Economy economy = null;
    private String pluginPrefix;

    private DivisionsManager divisionsManager;
    private PlayerDataManager playerDataManager;
    private DefaultPermConfig defaultPermConfig;
    private MessagesConfig messagesConfig;

    private HashMap<UUID, BukkitTask> invitedPlayers = new HashMap<>();

    @Override
    public void onEnable() {

        hookAPI(this);

        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().fine("Vault hooked!");

        this.pluginPrefix = getConfig().getString("PluginPrefix");

        validateConfigs();
        this.messagesConfig = new MessagesConfig(this);

        loadDefaultRankPermissions();
        loadManagerData();

        registerEvents(new GeneralListener(this));
        registerCommand("divisions", new DivisionsCommand(this));
        registerCommand("divisionsconfirm", new ConfirmableCommands(this));

    }

    @Override
    public void onDisable() {
        saveManagerData();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public void validateConfigs() {

        File playerDataFolder = new File(getDataFolder() + File.separator + "/playerData");
        File divisionsFolder = new File(getDataFolder() + File.separator + "/divisionsData");

        if(!playerDataFolder.exists()){
            if(playerDataFolder.mkdirs()){
                getLogger().info("Creating player data folder...");
            }
        }

        if(!divisionsFolder.exists()){
            if(divisionsFolder.mkdirs()){
                getLogger().info("Creating divisions data folder...");
            }
        }

        File[] files = {new File(getDataFolder(), "config.yml"), new File(getDataFolder(), "divisionsList.yml"), new File(getDataFolder(), "defaultPerms.yml"), new File(getDataFolder(), "messages.yml")};
        for(File f : files){
            if(!f.exists()) {
                saveResource(f.getName(), false);
                getLogger().info("Loading " + f.getName() + "...");
            }
        }
    }

    public String getPluginPrefix(){
        return pluginPrefix;
    }

    public void loadManagerData(){
        divisionsManager = new DivisionsManager(this);
        playerDataManager = new PlayerDataManager(this);
        /*
        //Had to set DivisionsConfigGHandler after playerdatamanager because it's dependent on it
        divisionsManager.setDivisionsHandler(new DivisionsConfigHandler(this));*/
        playerDataManager.loadContents();
        divisionsManager.loadContents();
        playerDataManager.setPlayerDivisions();
    }

    public void saveManagerData(){
        if(divisionsManager != null){
            divisionsManager.saveContents();
        }

        if(playerDataManager != null){
            playerDataManager.saveContents();
        }
    }

    public void loadDefaultRankPermissions(){
        defaultPermConfig = new DefaultPermConfig(this);
        defaultPermConfig.compilePerms();
    }

    public DivisionsManager getDivisionsManager(){
        return divisionsManager;
    }

    public PlayerDataManager getPlayerDataManager(){
        return playerDataManager;
    }

    public DefaultPermConfig getDefaultPermConfig(){
        return defaultPermConfig;
    }

    public MessagesConfig getMessagesConfig(){
        return messagesConfig;
    }

    public HashMap<UUID, BukkitTask> getInvitedPlayers(){
        return invitedPlayers;
    }

    public void addInvitedPlayer(UUID u){
        invitedPlayers.put(u, new InviteTimer(this, u).runTaskTimer(this, 1200L, 1200L));
    }

    public void removeInvitedPlayer(UUID u){
        invitedPlayers.get(u).cancel();
        invitedPlayers.remove(u);
    }

    public boolean hasBeenInvitedRecently(UUID u){
        return invitedPlayers.containsKey(u);
    }

}