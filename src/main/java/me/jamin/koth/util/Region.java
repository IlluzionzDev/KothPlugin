package me.jamin.koth.util;

import lombok.*;
import org.bukkit.Location;

/**
 * A class representing a region (an arena between two locations)
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Region {

    /**
     * Locations
     */
    @Getter
    @Setter
    public Location firstLocation;

    @Getter
    @Setter
    public Location secondLocation;

    /**
     * Check if a location is in this region
     *
     * @param location Location to check if is in
     * @return If in this region
     */
    public boolean inRegion(final Location location) {
        double x1 = firstLocation.getX();
        double y1 = firstLocation.getY();
        double z1 = firstLocation.getZ();

        double x2 = secondLocation.getX();
        double y2 = secondLocation.getY();
        double z2 = secondLocation.getZ();

        return ((location.getX() >= x1 && location.getX() <= x2) || (location.getX() <= x1 && location.getX() >= x2))
                && ((location.getY() >= y1 && location.getY() <= y2) || (location.getY() <= y1 && location.getY() >= y2))
                && ((location.getZ() >= z1 && location.getZ() <= z2) || (location.getZ() <= z1 && location.getZ() >= z2));

//        return (((location.getX() >= x1) && (location.getY() >= y1) && (location.getZ() >= z1)) && ((location.getX() <= x2) && (location.getY() <= y2) && (location.getZ() <= z2)))
//                /* make sure correct for all orientation */
//                || (((location.getX() <= x1) && (location.getY() <= y1) && (location.getZ() <= z1)) && ((location.getX() >= x2) && (location.getY() >= y2) && (location.getZ() >= z2)));
    }

}
