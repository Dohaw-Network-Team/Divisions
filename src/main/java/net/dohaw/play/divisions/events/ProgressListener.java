package net.dohaw.play.divisions.events;

import net.dohaw.corelib.ChatSender;
import net.dohaw.play.divisions.DivisionsPlugin;
import net.dohaw.play.divisions.PlayerData;
import net.dohaw.play.divisions.archetypes.ArchetypeWrapper;
import net.dohaw.play.divisions.archetypes.spells.Spell;
import net.dohaw.play.divisions.archetypes.spells.SpellWrapper;
import net.dohaw.play.divisions.archetypes.spells.active.ActiveSpell;
import net.dohaw.play.divisions.customitems.CustomItem;
import net.dohaw.play.divisions.events.custom.LevelUpEvent;
import net.dohaw.play.divisions.exceptions.InvalidCustomItemKeyException;
import net.dohaw.play.divisions.managers.CustomItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class ProgressListener implements Listener {

    private CustomItemManager customItemManager;

    public ProgressListener(DivisionsPlugin plugin){
        this.customItemManager = plugin.getCustomItemManager();
    }

    /*
        Gives a player their new spells.
     */
    @EventHandler
    public void onPlayerLevelUp(LevelUpEvent e){

        PlayerData pd = e.getPlayerData();
        int level = pd.getLevel();

        Player player = pd.getPlayer().getPlayer();
        ArchetypeWrapper archetype = pd.getArchetype();
        List<SpellWrapper> unlockedSpells = Spell.getArchetypeSpellsUnlocked(archetype, level);

        for(SpellWrapper spell : unlockedSpells){

            /*
                Bow spells are not bound to a custom item
             */
            if(spell instanceof ActiveSpell){

                ActiveSpell aSpell = (ActiveSpell) spell;
                String customItemBindedToKey = spell.getCustomItemBindedToKey();
                /*
                    This may be null if I either gave the spell the wrong key or gave the custom item the wrong key, so checking for null just in case
                 */
                CustomItem customItem = customItemManager.getByKey(customItemBindedToKey);
                if(customItem != null){

                    PlayerInventory inv = player.getInventory();
                    ItemStack spellItem = customItem.toItemStack();
                    ItemStack alteredItem = customItemManager.alterMeta(pd, aSpell, spellItem);
                    inv.addItem(alteredItem);

                }else{
                    try {
                        throw(new InvalidCustomItemKeyException(customItemBindedToKey));
                    } catch (InvalidCustomItemKeyException ex) {
                        ex.printStackTrace();
                    }
                }

            }

        }

        if(!unlockedSpells.isEmpty()){
            ChatSender.sendPlayerMessage("You have leveled up to level &e" + level + "&f. You have unlocked the following spells: ", false, player, null);
            for(SpellWrapper spell : unlockedSpells){
                ChatSender.sendPlayerMessage("&cSpell: &e" + spell.getName(), false, player, null);
            }
        }

    }

}
