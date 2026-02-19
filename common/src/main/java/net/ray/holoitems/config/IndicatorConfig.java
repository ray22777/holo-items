package net.ray.holoitems.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;


@Config(name = "holo-items")
public class IndicatorConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip() //Enable holographic item names
    public boolean enableIndicator = true;

    @ConfigEntry.Gui.Tooltip() //Changes how far item names will be shown
    public int renderDistance = 16;


    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public DisplayMode displayMode = DisplayMode.ALWAYS_ON;

    public enum DisplayMode {
        ALWAYS_ON,
        HOVER;
    }

    @ConfigEntry.Gui.Tooltip() //How big the hover area is (in angles).
    public float directionAngle = 10.0f;

    @ConfigEntry.Gui.Tooltip(count = 3)  //Formatting for item names,
    // supporting the use of minecraft color codes (e.g. &a).
    // Placeholders: {name} {amount}.
    public String indicatorFormat = "&7{name} &8x{amount}";

    @ConfigEntry.Gui.Tooltip() //Enables bobbing of text
    public boolean bobbing = true;

    @ConfigEntry.Gui.Tooltip() //Enables shadow for texts.
    public boolean shadow = true;

    @ConfigEntry.Gui.Tooltip() //How big the item names will be.
    public float indicatorScale = 0.6f;

    @ConfigEntry.Gui.Tooltip() //How much to offset item name in blocks.
    public float offset = 0.2f;

    @ConfigEntry.Gui.Tooltip() // Show item tooltips when hovering
    public boolean showTooltips = true;

    @ConfigEntry.Gui.Tooltip() // Maximum distance for tooltips to render
    public float tooltipRenderDistance = 5.0f;

    @ConfigEntry.Gui.Tooltip() // Scale for tooltip text
    public float tooltipScale = 0.4f;

    @ConfigEntry.Gui.Tooltip(count = 2) // If true, tooltip renders above name.
                                        // If false, name is on top with tooltip below
    public boolean tooltipAboveName = true;

}