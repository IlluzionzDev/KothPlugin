package me.jamin.koth.ui;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.mysql.cj.ServerVersion;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import me.jamin.koth.util.TextUtil;
import me.jamin.koth.util.Valid;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Utility class to easily build custom items. We can set flags,
 * names and lore, and enchantments. Provides a way to
 * just easily construct an item
 */
@Builder
public final class ItemCreator {

    /**
     * The actual item stack this represents
     */
    private final ItemStack item;

    /**
     * The {@link XMaterial} of the item
     */
    private final XMaterial material;

    /**
     * The amount of items in the stack
     */
    @Builder.Default
    private final int amount = 1;

    /**
     * Damage to the item for setting custom metadata
     */
    @Builder.Default
    private final int damage = -1;

    /**
     * Custom model data
     */
    @Builder.Default
    private final int customModelData = 0;

    /**
     * The display name of the item
     */
    private final String name;

    /**
     * The lore strings to display
     */
    @Singular
    private final List<String> lores;

    /**
     * The enchants applied for the item mapped by level
     */
    @Singular
    private final Map<XEnchantment, Integer> enchants;

    /**
     * If the {@link ItemStack} has the unbreakable flag
     */
    private boolean unbreakable;

    /**
     * Should we hide all tags from the item (enchants, etc.)?
     */
    @Builder.Default
    private boolean hideTags = false;

    /**
     * Should we add glow to the item? (adds a fake enchant and uses
     * item flags to hide it)
     *
     * The enchant is visible on older MC versions.
     */
    @Builder.Default
    private boolean glow = false;

    /**
     * The actual metadata of the item stack
     */
    private final ItemMeta meta;

    //  -------------------------------------------------------------------------
    //  Constructing
    //  -------------------------------------------------------------------------

    /**
     * @return This item suitable for a {@link com.illuzionzstudios.mist.ui.UserInterface}
     */
    public ItemStack makeUIItem() {
        unbreakable = true;
        hideTags = true;

        return make();
    }

    /**
     * Finally construct the {@link ItemStack} from all parameters
     *
     * @return The built item
     */
    public ItemStack make() {
        // Make sure base item and material are set
        Valid.checkBoolean((material != null && material.parseMaterial() != null) || item != null, "Material or item must be set!");

        // Actual item we're building on
        ItemStack stack = item != null ? item.clone() : new ItemStack(material.parseMaterial(), amount);
        final ItemMeta stackMeta = meta != null ? meta.clone() : stack.getItemMeta();

        Valid.checkNotNull(stackMeta, "Item metadata was somehow null");

        // Skip if trying to build on air
        if (material == XMaterial.AIR)
            return stack;

        // Set damage
        if (damage != -1) {
            try {
                stack.setDurability((short) damage);
            } catch (final Throwable ignored) {
            }

            try {
                if (stackMeta instanceof Damageable) {
                    ((Damageable) stackMeta).setDamage(damage);
                }
            } catch (final Throwable ignored) {
            }
        }

        // Custom model data only in 1.14+
        if (customModelData != 0)
            stackMeta.setCustomModelData(customModelData);

        // Glow
        if (glow) {
            stackMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }

        // Enchantments
        if (enchants != null) {
            for (final XEnchantment ench : enchants.keySet()) {
                stackMeta.addEnchant(ench.parseEnchantment(), enchants.get(ench), true);
            }
        }

        // Name and lore
        if (name != null) {
            stackMeta.setDisplayName(TextUtil.formatText("&r" + name));
        }

        if (lores != null && !lores.isEmpty()) {
            final List<String> coloredLores = new ArrayList<>();

            lores.forEach(line -> {
                // Colour and split by \n
                List<String> lines = Arrays.asList(line.split("\\r?\\n"));
                // Append '&7' before every line instead of ugly purple italics
                lines.forEach(line2 -> coloredLores.add(TextUtil.formatText(ChatColor.GRAY + line2)));
            });
            stackMeta.setLore(coloredLores);
        }

        // Unbreakable
        if (unbreakable) {
            XProperty.UNBREAKABLE.apply(stackMeta, true);
        }

        // Finally apply metadata
        stack.setItemMeta(stackMeta);

        return stack;
    }

    /**
     * Convenience method to get a new item creator with material, name and lore set
     *
     * @param material The {@link XMaterial} to set
     * @param name The name of the item
     * @param lore Collection of lore strings
     * @return THe builder with these properties
     */
    public static ItemCreatorBuilder of(final XMaterial material, final String name, @NonNull final Collection<String> lore) {
        return of(material, name, lore.toArray(new String[lore.size()]));
    }

    /**
     * See {@link #of(XMaterial, String, Collection)}
     */
    public static ItemCreatorBuilder of(final XMaterial material, final String name, @NonNull final String... lore) {
        return ItemCreator.builder().material(material).name(name).lores(Arrays.asList(lore)).hideTags(true);
    }

    /**
     * Get a new item creator from material
     *
     * @param mat existing material
     * @return the new item creator
     */
    public static ItemCreatorBuilder of(final XMaterial mat) {
        Valid.checkNotNull(mat, "Material cannot be null!");

        return ItemCreator.builder().material(mat);
    }

}
