package net.ray.holoitems;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.ray.holoitems.config.ConfigGetter;

public class IndicatorFormatter {
    public static Component formatItem(ItemEntity item) {
        String format = ConfigGetter.iconfig.indicatorFormat;
        ItemStack itemStack = item.getItem();
        Component itemNameComponent = itemStack.getHoverName();
        String itemName = itemNameComponent.getString();
        int amount = itemStack.getCount();
        String defaultName = itemStack.getItem().getName().getString();
        boolean isRenamed = !itemName.equals(defaultName);
        boolean isDefaultName = itemNameComponent.getStyle().equals(Style.EMPTY);

        if (format.equals("{name}")) {
            return itemNameComponent;
        }

        String[] parts = format.split("\\{name\\}", -1);

        if (parts.length == 1) {
            String result = format.replace("{amount}", String.valueOf(amount));
            return ComponentUtilsParser.parseColorCodes(result);
        }

        if (isDefaultName) {
            if (isRenamed ) {
                String result = format
                        .replace("{name}", "§o" + itemName)
                        .replace("{amount}", String.valueOf(amount));
                return ComponentUtilsParser.parseColorCodes(result);
            }
            else{
                String result = format
                        .replace("{name}", itemName)
                        .replace("{amount}", String.valueOf(amount));
                return ComponentUtilsParser.parseColorCodes(result);
            }
        }
        MutableComponent result = Component.empty();

        if (!parts[0].isEmpty()) {
            String beforeText = parts[0].replace("{amount}", String.valueOf(amount));
            result.append(ComponentUtilsParser.parseColorCodes(beforeText));
        }
        result.append(itemNameComponent);

        if (parts.length > 1 && !parts[1].isEmpty()) {
            String afterText = parts[1].replace("{amount}", String.valueOf(amount));
            result.append(ComponentUtilsParser.parseColorCodes(afterText));
        }

        return result;
    }
}