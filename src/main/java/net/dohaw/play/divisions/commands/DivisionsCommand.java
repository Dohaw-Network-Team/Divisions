package net.dohaw.play.divisions.commands;

import me.c10coding.coreapi.chat.ChatFactory;
import net.dohaw.play.divisions.Message;
import net.dohaw.play.divisions.Placeholder;
import net.dohaw.play.divisions.division.Division;
import net.dohaw.play.divisions.division.DivisionStatus;
import net.dohaw.play.divisions.DivisionsPlugin;
import net.dohaw.play.divisions.files.MessagesConfig;
import net.dohaw.play.divisions.managers.DivisionsManager;
import net.dohaw.play.divisions.managers.PlayerDataManager;
import net.dohaw.play.divisions.menus.PermissionsMenu;
import net.dohaw.play.divisions.playerData.PlayerData;
import net.dohaw.play.divisions.rank.Permission;
import net.dohaw.play.divisions.rank.Rank;
import net.dohaw.play.divisions.utils.PlayerHelper;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DivisionsCommand implements CommandExecutor {

    private DivisionsPlugin plugin;
    private DivisionsManager divisionsManager;
    private PlayerDataManager playerDataManager;
    private MessagesConfig messagesConfig;
    private ChatFactory chatFactory;
    private String prefix;

    public DivisionsCommand(DivisionsPlugin plugin){
        this.plugin = plugin;
        this.divisionsManager = plugin.getDivisionsManager();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.chatFactory = plugin.getAPI().getChatFactory();
        this.messagesConfig = plugin.getMessagesConfig();
        this.prefix = plugin.getPluginPrefix();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length > 0){
                String msg = null;
                //divisions create <NAME> <PUBLIC | PRIVATE>
                if(args[0].equalsIgnoreCase("create") && args.length == 3){
                    String divisionName = args[1];
                    String status = args[2];
                    if(playerDataManager.getByPlayerObj(player).getDivision() == null){
                        if(!divisionsManager.hasContent(divisionName)){
                            if(status.equalsIgnoreCase("public") || status.equalsIgnoreCase("private")){
                                /*
                                    Prevents a player from triggering a confirmable command
                                 */
                                if(!divisionName.equalsIgnoreCase("abort")){

                                    /*
                                        public = anyone can join
                                        private = need an invite
                                     */
                                    if(status.equalsIgnoreCase("public")){
                                        divisionsManager.createNewDivision(divisionName, player, DivisionStatus.PUBLIC);
                                    }else{
                                        divisionsManager.createNewDivision(divisionName, player, DivisionStatus.PRIVATE);
                                    }

                                    playerDataManager.getByPlayerObj(player).setDivision(divisionsManager.getDivision(divisionName).getName());
                                    playerDataManager.getByPlayerObj(player).setRank(null);

                                    msg = messagesConfig.getMessage(Message.DIVISION_CREATED);
                                    msg = MessagesConfig.replacePlaceholder(msg, Placeholder.DIVISION_NAME, divisionName);
                                }else{
                                    msg = messagesConfig.getMessage(Message.DIVISION_NAME_ABORT);
                                }
                            }else{
                                msg = messagesConfig.getMessage(Message.DIVISION_STATUS);
                            }
                        }else{
                            msg = messagesConfig.getMessage(Message.DIVISION_ALREADY_CREATED);
                        }
                    }else{
                        msg = messagesConfig.getMessage(Message.ALREADY_IN_DIVISION);
                    }
                }else if(args[0].equalsIgnoreCase("disband") && args.length == 1){

                    String divisionName = playerDataManager.getPlayerByUUID(player.getUniqueId()).getDivision();
                    Division division = divisionsManager.getDivision(divisionName);
                    if(playerDataManager.getPlayerByUUID(player.getUniqueId()).getDivision() != null){
                        if(division.getLeader().getPLAYER_UUID().equals(player.getUniqueId())) {

                            TextComponent tcMsg = new TextComponent(chatFactory.colorString(prefix) + " Are you sure you wish to disband your division? By doing so, you will keep your Garrison, but lose all Division stats, power, and more! Press ");
                            TextComponent yes = new TextComponent(chatFactory.colorString("&a&lYes"));
                            TextComponent afterYes = new TextComponent(chatFactory.colorString("&f to continue with this actions or "));
                            TextComponent no = new TextComponent(chatFactory.colorString("&4&lNo"));
                            TextComponent afterNo = new TextComponent(chatFactory.colorString("&f to not disband your division"));

                            yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/divisionsconfirm disband " + divisionName));
                            yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Disband Division").create()));

                            no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/divisionsconfirm disband abort"));
                            no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Abort...").create()));

                            tcMsg.addExtra(yes);
                            tcMsg.addExtra(afterYes);
                            tcMsg.addExtra(no);
                            tcMsg.addExtra(afterNo);

                            player.spigot().sendMessage(tcMsg);

                        }else{
                            msg = messagesConfig.getMessage(Message.MUST_BE_LEADER);
                        }
                    }else{
                        msg = messagesConfig.getMessage(Message.NOT_IN_DIVISION);
                    }
                }else if(args[0].equalsIgnoreCase("info")){
                    if(playerDataManager.getByPlayerObj(player).getDivision() != null){
                        if(args.length == 2){
                            String divisionArg = args[1];
                            if(divisionsManager.getDivision(divisionArg) != null){
                                String divisionName = playerDataManager.getByPlayerObj(player).getDivision();
                                Division division = divisionsManager.getDivision(divisionName);
                                displayDivisionInfo(division, player);
                            }
                        }else{
                            String divisionName = playerDataManager.getByPlayerObj(player).getDivision();
                            Division playerDivision = divisionsManager.getDivision(divisionName);
                            displayDivisionInfo(playerDivision, player);
                        }
                    }else{
                        msg = messagesConfig.getMessage(Message.NOT_IN_DIVISION);
                    }
                }else if(args[0].equalsIgnoreCase("invite") && args.length == 2){

                    String playerName = args[1];
                    if(playerDataManager.getByPlayerObj(player).getDivision() != null){
                        if(Bukkit.getPlayer(playerName) != null){
                            if(playerDataManager.can(player.getUniqueId(), Permission.CAN_INVITE_PLAYERS)){
                                if(playerDataManager.getByPlayerObj(Bukkit.getPlayer(playerName)).getDivision() == null){
                                    invitePlayer(player);
                                    msg = messagesConfig.getMessage(Message.INVITER);
                                    msg = messagesConfig.replacePlaceholder(msg, Placeholder.PLAYER_NAME, playerName);
                                }else{
                                    msg = messagesConfig.getMessage(Message.TARGET_PLAYER_ALREADY_IN_DIVISION);
                                }
                            }else{
                                msg = messagesConfig.getMessage(Message.NO_PERM_INVITE);
                            }
                        }else{
                            msg = messagesConfig.getMessage(Message.INVALID_PLAYER);
                        }
                    }else{
                        msg = messagesConfig.getMessage(Message.NOT_IN_DIVISION);
                    }
                }else if(args[0].equalsIgnoreCase("kick") && args.length == 2){

                    String playerName = args[1];
                    if(player.getName().equalsIgnoreCase(playerName)){
                        chatFactory.sendPlayerMessage("You can't kick yourself!", true, player, prefix);
                        return false;
                    }

                    if(playerDataManager.getByPlayerObj(player).getDivision() != null){
                        if(Bukkit.getPlayer(playerName) != null) {
                            Player kickedPlayer = Bukkit.getOfflinePlayer(playerName).getPlayer();
                            if (playerDataManager.can(player.getUniqueId(), Permission.CAN_KICK_PLAYERS)) {
                                if (playerDataManager.getByPlayerObj(Bukkit.getPlayer(playerName)).getDivision() != null) {

                                    String kickerDivisionName = playerDataManager.getByPlayerObj(player).getDivision();
                                    String kickedPlayerDivisionName = playerDataManager.getByPlayerObj(kickedPlayer).getDivision();

                                    Rank kickerRank = playerDataManager.getByPlayerObj(player).getRank();
                                    Rank kickedPlayerRank = playerDataManager.getByPlayerObj(kickedPlayer).getRank();

                                    if(kickedPlayerRank == null){
                                        msg = messagesConfig.getMessage(Message.ACTION_LEADER_DENY);
                                        chatFactory.sendPlayerMessage(msg, true, player, prefix);
                                        return false;
                                    }

                                    if (kickerDivisionName.equalsIgnoreCase(kickedPlayerDivisionName)) {
                                        //Kicker has a higher rank than kicked player
                                        if(Rank.isAHigherRank(kickerRank, kickedPlayerRank) == 1){

                                            Division division = divisionsManager.getDivision(kickerDivisionName);
                                            PlayerData kickedPlayerData = playerDataManager.getByPlayerObj(kickedPlayer);

                                            kickedPlayerData.setDivision(null);
                                            kickedPlayerData.setRank(null);
                                            division.removePlayer(kickedPlayerData);

                                            playerDataManager.setPlayerData(kickedPlayer.getUniqueId(), kickedPlayerData);
                                            divisionsManager.setDivision(kickerDivisionName, division);

                                            if(kickedPlayer.isOnline()){
                                                msg = messagesConfig.getMessage(Message.KICKED_PLAYER_NOTIFIER);
                                                msg = MessagesConfig.replacePlaceholder(msg, Placeholder.PLAYER_NAME, player.getName());
                                            }

                                        }else if(Rank.isAHigherRank(kickerRank, kickedPlayerRank) == 0){
                                            msg = messagesConfig.getMessage(Message.TARGET_PLAYER_SAME_RANK);
                                        }else{
                                            msg = messagesConfig.getMessage(Message.TARGET_PLAYER_HIGHER_RANK);
                                        }
                                    }else{
                                        msg = messagesConfig.getMessage(Message.TARGET_PLAYER_NOT_IN_YOUR_DIVISION);
                                    }
                                } else {
                                    msg = messagesConfig.getMessage(Message.TARGET_PLAYER_NO_DIVISION);
                                }
                            }else{
                                msg = messagesConfig.getMessage(Message.NO_PERM_KICK);
                            }
                        }else{
                            msg = messagesConfig.getMessage(Message.INVALID_PLAYER);
                        }
                    }
                }else if(args[0].equalsIgnoreCase("edit")){

                    if(playerDataManager.isInDivision(player)){
                        if(args.length == 2){
                            if(args[1].equalsIgnoreCase("status")) {
                                if (playerDataManager.isInDivision(player)) {
                                    if (playerDataManager.can(player.getUniqueId(), Permission.CAN_ALTER_STATUS)) {

                                        String divisionName = playerDataManager.getByPlayerObj(player).getDivision();
                                        Division playerDivision = divisionsManager.getDivision(divisionName);
                                        DivisionStatus currentStatus = playerDivision.getStatus();

                                        DivisionStatus divStatus;
                                        if (currentStatus.equals(DivisionStatus.PRIVATE)) {
                                            divStatus = DivisionStatus.PUBLIC;
                                        } else {
                                            divStatus = DivisionStatus.PRIVATE;
                                        }
                                        playerDivision.setStatus(divStatus);
                                        divisionsManager.setDivision(divisionName, playerDivision);

                                        msg = messagesConfig.getMessage(Message.ALTER_STATUS);
                                        msg = MessagesConfig.replacePlaceholder(msg, Placeholder.STATUS, divStatus.name());
                                    }
                                }
                            }else if(args[1].equalsIgnoreCase("perms")) {
                                if(playerDataManager.can(player.getUniqueId(), Permission.CAN_EDIT_PERMS)){
                                    PermissionsMenu permissionsMenu = new PermissionsMenu(plugin);
                                    permissionsMenu.initializeItems(player);
                                    permissionsMenu.openInventory(player);
                                }else{
                                    msg = messagesConfig.getMessage(Message.NO_PERM_EDIT_PERMISSIONS);
                                }
                            }
                        }
                    }else{
                        msg = messagesConfig.getMessage(Message.NOT_IN_DIVISION);
                    }

                }else if((args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("demote")) && args.length == 2){

                    String playerName = args[1];
                    if(playerDataManager.isInDivision(player)){

                        if(args[0].equalsIgnoreCase("promote")) {
                            if (!playerDataManager.can(player.getUniqueId(), Permission.CAN_PROMOTE_MEMBERS)) {
                                msg = messagesConfig.getMessage(Message.NO_PERM_PROMOTING);
                                chatFactory.sendPlayerMessage(msg, true, player, prefix);
                                return false;
                            }
                        }else{
                            if(!playerDataManager.can(player.getUniqueId(), Permission.CAN_DEMOTE_MEMBERS)){
                                msg = messagesConfig.getMessage(Message.NO_PERM_DEMOTING);
                                chatFactory.sendPlayerMessage(msg, true, player, prefix);
                                return false;
                            }
                        }

                        if(player.getName().equalsIgnoreCase(playerName)){
                            msg = messagesConfig.getMessage(Message.ACTION_YOURSELF_DENY);
                            chatFactory.sendPlayerMessage(msg, true, player, prefix);
                            return false;
                        }

                        if(PlayerHelper.isValidOnlinePlayer(playerName)){
                            Player playerAffected = Bukkit.getPlayer(playerName);
                            if(playerDataManager.isInDivision(playerAffected)){
                                if(playerDataManager.isInSameDivision(player, playerAffected)){

                                    PlayerData playerAffectedData = playerDataManager.getByPlayerObj(playerAffected);
                                    Rank playerAffectedRank = playerAffectedData.getRank();

                                    if(playerAffectedRank != null){
                                        Rank newRank;
                                        String playerAffectedMsg;
                                        String playerMsg;
                                        if(args[0].equalsIgnoreCase("promote")){

                                            if(!Rank.isLastRank(playerAffectedRank)){
                                                newRank = Rank.getNextRank(playerAffectedRank);
                                                playerAffectedMsg = messagesConfig.getMessage(Message.PLAYER_AFFECTED_PROMOTION);
                                                playerAffectedMsg = MessagesConfig.replacePlaceholder(playerAffectedMsg, Placeholder.RANK, newRank.name());

                                                playerMsg = messagesConfig.getMessage(Message.PROMOTER);
                                                playerMsg = MessagesConfig.replacePlaceholder(playerMsg, Placeholder.RANK, newRank.name());
                                                playerMsg = MessagesConfig.replacePlaceholder(playerMsg, Placeholder.PLAYER_NAME, playerName);
                                            }else{
                                                chatFactory.sendPlayerMessage("This player is already the highest rank they can be!", true, player, prefix);
                                                return false;
                                            }

                                        }else{

                                            if(!Rank.isFirstRank(playerAffectedRank)){
                                                newRank = Rank.getPreviousRank(playerAffectedRank);
                                                playerAffectedMsg = messagesConfig.getMessage(Message.PLAYER_AFFECTED_DEMOTION);
                                                playerAffectedMsg = MessagesConfig.replacePlaceholder(playerAffectedMsg, Placeholder.RANK, newRank.name());

                                                playerMsg = messagesConfig.getMessage(Message.DEMOTER);
                                                playerMsg = MessagesConfig.replacePlaceholder(playerMsg, Placeholder.PLAYER_NAME, playerName);
                                                playerMsg = MessagesConfig.replacePlaceholder(playerMsg, Placeholder.RANK, newRank.name());
                                            }else{
                                                chatFactory.sendPlayerMessage("This player is already the lowest rank they can be!", true, player, prefix);
                                                return false;
                                            }

                                        }
                                        playerAffectedData.setRank(newRank);
                                        playerDataManager.setPlayerData(playerAffected.getUniqueId(), playerAffectedData);

                                        String divisionName = playerAffectedData.getDivision();
                                        Division division = divisionsManager.getDivision(divisionName);
                                        division.updatePlayerData(playerAffectedData);

                                        chatFactory.sendPlayerMessage(playerAffectedMsg, true, playerAffected, prefix);
                                        chatFactory.sendPlayerMessage(playerMsg, true, player, prefix);
                                        return false;
                                    }else{
                                        /*
                                            This person is owner
                                        */
                                        msg = messagesConfig.getMessage(Message.ACTION_LEADER_DENY);
                                    }
                                }else{
                                    msg = messagesConfig.getMessage(Message.TARGET_PLAYER_NOT_IN_YOUR_DIVISION);
                                }
                            }else{
                                msg = messagesConfig.getMessage(Message.TARGET_PLAYER_NO_DIVISION);
                            }
                        }else{
                            msg = messagesConfig.getMessage(Message.INVALID_PLAYER);
                        }
                    }else{
                        msg = messagesConfig.getMessage(Message.NOT_IN_DIVISION);
                    }
                }else if(args[0].equalsIgnoreCase("join") && args.length == 2){

                    String divisionName = args[1];
                    if(!playerDataManager.isInDivision(player)){
                        if(divisionsManager.getDivision(divisionName) != null){

                            Division div = divisionsManager.getDivision(divisionName);
                            if(div.getStatus() != DivisionStatus.PRIVATE){
                                PlayerData pd = playerDataManager.getByPlayerObj(player);
                                div.addPlayer(pd);
                                divisionsManager.setDivision(divisionName, div);

                                pd.setDivision(divisionName);
                                pd.setRank(Rank.FRESH_MEAT);
                                playerDataManager.setPlayerData(player.getUniqueId(), pd);

                                msg = messagesConfig.getMessage(Message.DIVISION_JOIN);
                                msg = MessagesConfig.replacePlaceholder(msg, Placeholder.DIVISION_NAME, divisionName);
                            }else{
                                msg = messagesConfig.getMessage(Message.DIVISION_NOT_PUBLIC);
                            }
                        }else{
                            msg = messagesConfig.getMessage(Message.NOT_A_DIVISION);
                        }
                    }else{
                        msg = messagesConfig.getMessage(Message.ALREADY_IN_DIVISION);
                    }

                }else if(args[0].equalsIgnoreCase("announce") && args.length == 2){
                    if(playerDataManager.isInDivision(player)){
                        if(playerDataManager.can(player.getUniqueId(), Permission.CAN_SEND_DIVISION_ANNOUNCEMENTS)){

                        }
                    }else{
                        msg = messagesConfig.getMessage(Message.NOT_IN_DIVISION);
                    }
                }

                if(msg != null){
                    chatFactory.sendPlayerMessage(msg, true, player, prefix);
                }

            }
        }
        return false;
    }

    private void displayDivisionInfo(Division division, Player playerToSendTo){
        chatFactory.sendPlayerMessage("", false, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&aDivision info:", true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&eName: &c" + division.getName(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&ePower: &c" + division.getPower(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&eGold: &c" + division.getGoldAmount(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&eLeader: &c" + division.getLeader().getPLAYER().getName(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&eKills: &c" + division.getKills(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&eCasualties: &c" + division.getCasualties(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&eNumber members: &c" + division.getPlayers().size(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&eHearts destroyed: &c" + division.getHeartsDestroyed(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("&eShrines conquered: &c" + division.getShrinesConquered(), true, playerToSendTo, prefix);
        chatFactory.sendPlayerMessage("", false, playerToSendTo, prefix);
    }

    private void invitePlayer(Player inviter){

        String divisionName = playerDataManager.getByPlayerObj(inviter).getDivision();
        TextComponent msg = new TextComponent(chatFactory.colorString("You have been invited to the division &e" + divisionName + "&f by " + "&e" + inviter.getName() + ".&fIf you wish to accept this invite, press "));
        TextComponent join = new TextComponent(chatFactory.colorString("&aJoin"));
        TextComponent msg2 = new TextComponent(chatFactory.colorString(". If you wish to decline this invite, press "));
        TextComponent decline = new TextComponent(chatFactory.colorString("&cDecline"));

        join.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join Division").create()));
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Decline invite").create()));
        join.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/divisionsconfirm invaccept " + divisionName));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/divisionsconfirm invaccept abort"));

        msg.addExtra(join);
        msg.addExtra(msg2);
        msg.addExtra(decline);

    }

}