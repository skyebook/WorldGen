/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.skyebook.worldgen;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author Skye Book
 */
public class Util {

    public static Material coloredUnshaded(AssetManager assetManager, ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        return mat;
    }
}
