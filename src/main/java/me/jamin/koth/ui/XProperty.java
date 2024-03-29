package me.jamin.koth.ui;

import com.mysql.cj.ServerVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.jamin.koth.util.TextUtil;
import me.jamin.koth.util.Valid;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

/**
 * Some properties of things.
 * TODO: I think this needs to be tweaked does it actually have any use?
 */
@RequiredArgsConstructor
public enum XProperty {

    // ItemMeta
    /**
     * The unbreakable property of ItemMeta
     */
    UNBREAKABLE(ItemMeta.class, boolean.class),

    // Entity
    /**
     * The glowing entity property, currently only support white color
     */
    GLOWING(Entity.class, boolean.class),

    /**
     * The AI navigator entity property
     */
    AI(Entity.class, boolean.class),

    /**
     * The gravity entity property
     */
    GRAVITY(Entity.class, boolean.class),

    /**
     * Silent entity property that controls if the entity emits sounds
     */
    SILENT(Entity.class, boolean.class),

    /**
     * The god mode entity property
     */
    INVULNERABLE(Entity.class, boolean.class);

    /**
     * The class that this enum applies for, for example {@link Entity}
     */
    @Getter
    private final Class<?> requiredClass;

    /**
     * The "setter" field type, for example setSilent method accepts boolean
     */
    private final Class<?> setterMethodType;

    /**
     * Apply the property to the entity. Class must be compatible with {@link #requiredClass}
     *
     * Example: SILENT.apply(myZombieEntity, true)
     */
    public final void apply(Object instance, Object key) {
        Valid.checkBoolean(requiredClass.isAssignableFrom(instance.getClass()), this + " accepts " + requiredClass.getSimpleName() + ", not " + instance.getClass().getSimpleName());

        try {
            final Method m = getMethod(instance.getClass());
            m.setAccessible(true);

            m.invoke(instance, key);

        } catch (final ReflectiveOperationException e) {
        }
    }

    /**
     * Can this property be used on this server for the given class? Class must be compatible with {@link #requiredClass}
     */
    public final boolean isAvailable(Class<?> clazz) {
        try {
            getMethod(clazz);
        } catch (final ReflectiveOperationException e) {
                return false;
        }

        return true;
    }

    // Automatically returns the correct getter or setter method for class
    private Method getMethod(Class<?> clazz) throws ReflectiveOperationException {
        return clazz.getMethod("set" + (toString().equals("AI") ? "AI" : TextUtil.formatText(toString().toLowerCase(), true)), setterMethodType);
    }

}
