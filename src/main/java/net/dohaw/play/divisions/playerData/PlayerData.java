package net.dohaw.play.divisions.playerData;

import lombok.Getter;
import lombok.Setter;
import net.dohaw.play.divisions.DivisionChannel;
import net.dohaw.play.divisions.rank.Rank;
import net.dohaw.play.divisions.rank.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;
import java.util.UUID;

public class PlayerData {

    @Getter final private OfflinePlayer PLAYER;
    @Getter final private String PLAYER_NAME;
    @Getter final private UUID PLAYER_UUID;
    @Getter final private FileConfiguration PLAYER_CONFIG;

    @Getter @Setter private String division = null;

    @Getter @Setter private Rank rank;
    @Getter @Setter private int kills, casualties, shrinesConquered;
    @Getter @Setter private double heartsDestroyed;
    @Getter @Setter private DivisionChannel channel = DivisionChannel.NONE;

    @Getter private EnumMap<Permission, Object> permissions = new EnumMap<>(Permission.class);

    public PlayerData(final OfflinePlayer PLAYER, final FileConfiguration PLAYER_CONFIG, Rank rank){
        this.PLAYER = PLAYER;
        this.PLAYER_NAME = PLAYER.getName();
        this.PLAYER_UUID = PLAYER.getUniqueId();
        this.rank = rank;
        this.PLAYER_CONFIG = PLAYER_CONFIG;
    }

    public void replacePermission(Permission perm, Object value){
        permissions.replace(perm, value);
    }

    public void putPermission(Permission perm, Object value){
        permissions.put(perm, value);
    }

    public boolean hasPermission(Permission perm){
        return permissions.get(perm) != null;
    }

}