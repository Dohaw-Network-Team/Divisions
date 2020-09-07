package net.dohaw.play.divisions.files;

import me.c10coding.coreapi.files.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultConfig extends Config {

    public DefaultConfig(JavaPlugin plugin) {
        super(plugin, "config.yml");
    }

    public int getDefaultDivisionGoldAmount(){
        return config.getInt("Default Division Gold Amount");
    }

    public double getMeleeDamageScale(){
        return config.getDouble("Damage Calculations.Scale.Melee Damage Scale");
    }

    public double getToughnessScale(){
        return config.getDouble("Damage Calculations.Scale.Toughness Scale");
    }

    public double getBowDamageScale(){
        return config.getDouble("Damage Calculations.Scale.Bow Damage Scale");
    }

    public double getMeleeDamageDivisionScale(){
        return config.getDouble("Damage Calculations.Division Scale.Melee Damage Division Scale");
    }

    public double getRangedDamageDivisionScale(){
        return config.getDouble("Damage Calculations.Division Scale.Ranged Damage Division Scale");
    }


}
