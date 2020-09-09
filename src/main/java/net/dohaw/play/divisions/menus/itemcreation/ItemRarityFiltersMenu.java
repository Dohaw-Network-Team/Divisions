package net.dohaw.play.divisions.menus.itemcreation;

import me.c10coding.coreapi.APIHook;
import me.c10coding.coreapi.helpers.EnumHelper;
import me.c10coding.coreapi.menus.Menu;
import net.dohaw.play.divisions.customitems.Rarity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemRarityFiltersMenu extends Menu implements Listener {

    private EnumHelper enumHelper;

    public ItemRarityFiltersMenu(APIHook plugin, Menu previousMenu) {
        super(plugin, previousMenu, "Rarity Filters", 36);
        this.enumHelper = plugin.getAPI().getEnumHelper();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void initializeItems(Player p) {

        inv.setItem(10, createGuiItem(Material.LEATHER_CHESTPLATE, "&6Common", new ArrayList<>()));
        inv.setItem(12, createGuiItem(Material.GOLDEN_CHESTPLATE, "&eUncommon", new ArrayList<>()));
        inv.setItem(14, createGuiItem(Material.CHAINMAIL_CHESTPLATE, "&7Rare", new ArrayList<>()));
        inv.setItem(16, createGuiItem(Material.IRON_CHESTPLATE, "&fEpic",  new ArrayList<>()));
        inv.setItem(31, createGuiItem(Material.DIAMOND_CHESTPLATE, "&bExotic", new ArrayList<>()));

        setFillerMaterial(Material.BLACK_STAINED_GLASS_PANE);
        setBackMaterial(Material.ARROW);
        fillMenu(true);
    }

    @EventHandler
    @Override
    protected void onInventoryClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();
        int slotClicked = e.getSlot();

        if(e.getClickedInventory() == null) return;
        if(!e.getClickedInventory().equals(inv)) return;
        e.setCancelled(true);
        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        Rarity rarity = null;
        switch(slotClicked){
            case 10:
                rarity = Rarity.COMMON;
                break;
            case 12:
                rarity = Rarity.UNCOMMON;
                break;
            case 14:
                rarity = Rarity.RARE;
                break;
            case 16:
                rarity = Rarity.EPIC;
                break;
            case 31:
                rarity = Rarity.EXOTIC;
                break;
        }

        if(rarity != null){
            ItemDisplayMenu newMenu = new ItemDisplayMenu(plugin, this, enumHelper.enumToName(rarity), ItemFilter.RARITY, rarity);
            newMenu.initializeItems(player);
        }else if(backMat == clickedItem.getType()){
            goToPreviousMenu(player);
        }

    }
}
