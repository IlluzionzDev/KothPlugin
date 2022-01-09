package me.jamin.koth.kit;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.jamin.koth.controller.PluginController;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle loading kits etc
 */
public enum KitController implements PluginController {
    INSTANCE;

    /**
     * Map of loaded kits
     */
    @Getter
    private final Map<String, PlayerKit> loadedKits = new HashMap<>();

    @Override
    public void initialize(Plugin plugin) {
        // Dummy kits for the demo
        PlayerKit warrior = new PlayerKit("Warrior", "Light armour but a heavy blow.", XMaterial.IRON_SWORD);
        warrior.getItems().put(0, XMaterial.IRON_SWORD.parseItem());
        warrior.armour = List.of(
                XMaterial.CHAINMAIL_BOOTS.parseItem(),
                XMaterial.LEATHER_LEGGINGS.parseItem(),
                XMaterial.CHAINMAIL_CHESTPLATE.parseItem(),
                XMaterial.LEATHER_HELMET.parseItem()
                ).toArray(ItemStack[]::new);
        loadedKits.put("warrior", warrior);

        PlayerKit archer = new PlayerKit("Archer", "Swift and well defended.", XMaterial.BOW);
        archer.getItems().put(0, XMaterial.WOODEN_SWORD.parseItem());
        archer.getItems().put(1, XMaterial.BOW.parseItem());
        archer.getItems().put(8, new ItemStack(Material.ARROW, 64));
        archer.armour = List.of(
                XMaterial.IRON_BOOTS.parseItem(),
                XMaterial.LEATHER_LEGGINGS.parseItem(),
                XMaterial.IRON_CHESTPLATE.parseItem(),
                XMaterial.LEATHER_HELMET.parseItem()
        ).toArray(ItemStack[]::new);
        loadedKits.put("archer", archer);
    }

    @Override
    public void stop(Plugin plugin) {

    }
}
