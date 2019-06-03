package model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

public class Settings
{
    //settings
    private String storyLineColor;
    private String ancestorLineColor;
    private String spouseLineColor;
    private boolean storyLineActive = true;
    private boolean ancestorLineActive = true;
    private boolean spouseLineActive = true;
    private String mapType;

    public Settings(String story, String ancestor, String spouse, String type)
    {
        this.storyLineColor = story;
        this.ancestorLineColor = ancestor;
        this.spouseLineColor = spouse;
        this.mapType = type;
    }

    //colors
    private int red = -65536;
    private int blue = -16776961;
    private int green = -16711936;
    private int black = -16777216;
    private int white = -1;
    private int magenta = -65281;
    private int gray = -7829368;
    private int yellow = -256;
    private int cyan = -16711681;
    private int transparent = 0;

    //Map Types
    private int normal = 1;
    private int satellite = 2;
    private int terrain = 3;
    private int hybrid = 4;

    public int getRed() {
        return red;
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getBlack() {
        return black;
    }

    public int getWhite() {
        return white;
    }

    public int getMagenta() {
        return magenta;
    }

    public int getGray() {
        return gray;
    }

    public int getYellow() {
        return yellow;
    }

    public int getCyan() {
        return cyan;
    }

    public int getTransparent() {
        return transparent;
    }

    public boolean isStoryLineActive() {
        return storyLineActive;
    }

    public void setStoryLineActive(boolean storyLineActive) {
        this.storyLineActive = storyLineActive;
    }

    public boolean isAncestorLineActive() {
        return ancestorLineActive;
    }

    public void setAncestorLineActive(boolean ancestorLineActive) {
        this.ancestorLineActive = ancestorLineActive;
    }

    public boolean isSpouseLineActive() {
        return spouseLineActive;
    }

    public void setSpouseLineActive(boolean spouseLineActive) {
        this.spouseLineActive = spouseLineActive;
    }
    public int getNormal() {
        return normal;
    }

    public int getSatellite() {
        return satellite;
    }

    public int getTerrain() {
        return terrain;
    }

    public int getHybrid() {
        return hybrid;
    }

    public void setStoryLineColor(String storyLineColor) {
        this.storyLineColor = storyLineColor;
    }

    public void setAncestorLineColor(String ancestorLineColor) {
        this.ancestorLineColor = ancestorLineColor;
    }

    public void setSpouseLineColor(String spouseLineColor) {
        this.spouseLineColor = spouseLineColor;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getStoryLineColor() {
        return storyLineColor;
    }

    public String getAncestorLineColor() {
        return ancestorLineColor;
    }

    public String getSpouseLineColor() {
        return spouseLineColor;
    }

    public String getMapType() {
        return mapType;
    }
}
