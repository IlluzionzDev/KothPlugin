package me.jamin.koth.ui;

import lombok.Getter;
import lombok.Setter;
import me.jamin.koth.KothPlugin;
import me.jamin.koth.scheduler.MinecraftScheduler;
import me.jamin.koth.util.Valid;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Basic user interface to display to a player
 */
public abstract class UserInterface {

    /**
     * This is an internal metadata tag that the player has.
     *
     * This will set the name of the current menu in order to keep
     * track of what menu is currently open
     */
    public static final String TAG_CURRENT = "UI_KOTH";

    /**
     * This is an internal metadata tag that the player has.
     *
     * This will set the name of the previous menu in order to
     * backtrack for returning menus
     */
    public static final String TAG_PREVIOUS = "UI_PREVIOUS_KOTH";

    /**
     * Map of items to slots
     */
    @Getter
    protected final Map<Integer, ItemStack> items = new HashMap<>();

    /**
     * Map of listeners
     */
    @Getter
    protected final Map<Integer, Consumer<ItemStack>> listeners = new HashMap<>();

    /**
     * Amount of slots in the inventory
     */
    @Setter
    private int size = 9 * 3;

    /**
     * This is the title to display at the top of the interface
     */
    @Setter
    private String title = "&8Kit Selection";

    /**
     * This is the player currently viewing the menu.
     * Isn't set till displayed to a player
     */
    protected Player viewer;

    /**
     * Get the currently active menu for the player
     *
     * @param player The player to get menu for
     * @return Found interface or {@code null} See {@link #getInterfaceViaTag(Player, String)}
     */
    public static UserInterface getInterface(final Player player) {
        return getInterfaceViaTag(player, TAG_CURRENT);
    }

    /**
     * Get the previous active menu for the player
     *
     * @param player The player to get menu for
     * @return Found interface or {@code null} See {@link #getInterfaceViaTag(Player, String)}
     */
    public static UserInterface getPrevious(final Player player) {
        return getInterfaceViaTag(player, TAG_PREVIOUS);
    }

    /**
     * Get a {@link UserInterface} from the metadata on a player
     *
     * @param player The player to check metadata
     * @param tag The name of the tag storing the interface
     * @return Found {@link UserInterface} otherwise {@code null}
     */
    public static UserInterface getInterfaceViaTag(final Player player, final String tag) {
        if (player.hasMetadata(tag)) {
            // Cast from tag
            final UserInterface userInterface = (UserInterface) player.getMetadata(tag).get(0).value();
            Valid.checkNotNull(userInterface, "Interface was missing from " + player.getName() + "'s metadata " + tag + "tag!");

            return userInterface;
        }

        return null;
    }

    /**
     * Build, render, and show our {@link UserInterface} to a player
     * Only used to firstly show it to a player, shouldn't be used to re-render
     *
     * @param player The player to show the menu to
     */
    public final void show(Player player) {
        Valid.checkNotNull(size, "Size not set in " + this + " (call setSize in your constructor)");
        Valid.checkNotNull(title, "Title not set in " + this + " (call setTitle in your constructor)");

        // Set our viewer
        viewer = player;

        // Render the menu
        final InterfaceDrawer drawer = InterfaceDrawer.of(size, title);

        // Set items defined by classes upstream
        // Doesn't replace set items
        for (int i = 0; i < drawer.getSize(); i++) {
            final ItemStack item = items.get(i);

            if (item != null && !drawer.isSet(i))
                drawer.setItem(i, item);
        }

        // Set our previous menu if applicable
        final UserInterface previous = getInterface(player);
        if (previous != null)
            player.setMetadata(TAG_PREVIOUS, new FixedMetadataValue(KothPlugin.getInstance(), previous));

        // Register current menu
        MinecraftScheduler.get().synchronize(() -> {
            drawer.display(player);

            player.setMetadata(TAG_CURRENT, new FixedMetadataValue(KothPlugin.getInstance(), UserInterface.this));
        }, 1);
    }

}
