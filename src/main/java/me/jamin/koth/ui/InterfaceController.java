package me.jamin.koth.ui;

import me.jamin.koth.KothPlugin;
import me.jamin.koth.controller.PluginController;
import me.jamin.koth.util.Logger;
import me.jamin.koth.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public enum InterfaceController implements PluginController {
    INSTANCE;

    @Override
    public void initialize(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void stop(Plugin plugin) {
    }

    /**
     * This event handles closing of {@link UserInterface}
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInterfaceClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;

        final Player player = (Player) event.getPlayer();
        final UserInterface userInterface = UserInterface.getInterface(player);

        if (userInterface != null) {
            player.removeMetadata(UserInterface.TAG_CURRENT, KothPlugin.getInstance());
        }
    }

    /**
     * Handles invoking the {@link UserInterface} click listeners
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMenuClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;

        final Player player = (Player) event.getWhoClicked();
        final UserInterface userInterface = UserInterface.getInterface(player);

        if (userInterface != null) {
            event.setCancelled(true);
            final ItemStack slotItem = event.getCurrentItem();
            final int slot = event.getSlot();

            // Run listener
            if (userInterface.getListeners().get(slot) != null)
                userInterface.getListeners().get(slot).accept(slotItem);
        }
    }
}
