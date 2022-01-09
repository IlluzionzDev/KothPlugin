package me.jamin.koth.kit;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import me.jamin.koth.player.GamePlayer;
import me.jamin.koth.ui.ItemCreator;
import me.jamin.koth.ui.UserInterface;
import me.jamin.koth.util.MistString;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kit selection interface.
 */
public class KitInterface extends UserInterface {

    /**
     * Game player for this interface
     */
    @Getter
    private final GamePlayer player;

    public KitInterface(final GamePlayer player) {
        this.player = player;
        setSize(9);

        AtomicInteger slot = new AtomicInteger();
        KitController.INSTANCE.getLoadedKits().forEach((name, kit) -> {
            ItemStack stack = ItemCreator.of(kit.getThumbnail(), kit.getName(),
                    Arrays.asList(kit.getDescription(), "&r", "&7&o(( Click to select kit ))")
            ).build().make();
            items.put(slot.get(), stack);
            listeners.put(slot.getAndIncrement(), itemStack -> {
                player.setSelectedKit(kit);
                XSound.BLOCK_NOTE_BLOCK_PLING.play(player.getPlayer());
                new MistString("&aYou have selected the {kit} kit").toString("kit", kit.getName()).sendMessage(player.getPlayer());
                player.getPlayer().closeInventory();
            });
        });
    }

}
