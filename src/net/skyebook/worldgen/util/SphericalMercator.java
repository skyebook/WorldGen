/**
 * Creative Commons Attribution-ShareAlike 2.0 license.
 */
package net.skyebook.worldgen.util;

/**
 * Taken from the OpenStreetMap wiki: http://wiki.openstreetmap.org/wiki/Mercator#Java
 */
public class SphericalMercator {

    public static double y2lat(double aY) {
        return Math.toDegrees(2 * Math.atan(Math.exp(Math.toRadians(aY))) - Math.PI / 2);
    }

    public static double lat2y(double aLat) {
        return Math.toDegrees(Math.log(Math.tan(Math.PI / 4 + Math.toRadians(aLat) / 2)));
    }
}