package me.jamin.koth.kit;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.jamin.koth.player.GamePlayer;
import me.jamin.koth.player.Team;
import me.jamin.koth.util.Valid;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a player kit that a player can have
 */
@RequiredArgsConstructor
public class PlayerKit {

    /**
     * Name of the kit
     */
    @Getter
    private final String name;

    /**
     * Short description of the kit
     */
    @Getter
    private final String description;

    /**
     * Thumbnail material for the GUI
     */
    @Getter
    private final XMaterial thumbnail;

    /**
     * Armour contents
     */
    public ItemStack[] armour;

    /**
     * Map of kit items
     */
    @Getter
    private final Map<Integer, ItemStack> items = new HashMap<>();

    /**
     * Apply this kit to the player
     */
    public void applyKit(final GamePlayer gamePlayer) {
        Valid.checkBoolean(gamePlayer.getTeam() != Team.UNASSIGNED, "Must be in a team before applying a kit");

        // Clear inventory first
        Player player = gamePlayer.getPlayer();
        player.getInventory().clear();

        player.getInventory().setArmorContents(armour);
        items.forEach((slot, stack) -> {
            player.getInventory().setItem(slot, stack);
        });

        // Helmet and leggings always leather but colored by team
        ItemStack leatherHelmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta leatherMeta = (LeatherArmorMeta) leatherHelmet.getItemMeta();
        leatherMeta.setColor(gamePlayer.getTeam() == Team.RED ? Color.RED : Color.BLUE);
        leatherHelmet.setItemMeta(leatherMeta);

        ItemStack leatherLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta meta = (LeatherArmorMeta) leatherHelmet.getItemMeta();
        meta.setColor(gamePlayer.getTeam() == Team.RED ? Color.RED : Color.BLUE);
        leatherLeggings.setItemMeta(meta);

        player.getInventory().setHelmet(leatherHelmet);
        player.getInventory().setLeggings(leatherLeggings);
    }

}
