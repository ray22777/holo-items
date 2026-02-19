package net.ray.holoitems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class ComponentUtilsParser {

    public static Component parseColorCodes(String text) {
        text = text.replace('&', '§');

        MutableComponent result = Component.literal("");
        Style currentStyle = Style.EMPTY;
        StringBuilder currentText = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '§' && i + 1 < text.length()) {
                char code = text.charAt(++i);
                if (currentText.length() > 0) {
                    result.append(Component.literal(currentText.toString())
                            .withStyle(currentStyle));
                    currentText.setLength(0);
                }
                ChatFormatting formatting = ChatFormatting.getByCode(code);
                if (formatting != null) {
                    if (formatting == ChatFormatting.RESET) {
                        currentStyle = Style.EMPTY;
                    } else {
                        currentStyle = currentStyle.applyLegacyFormat(formatting);
                    }
                }
            } else {
                currentText.append(c);
            }
        }
        if (currentText.length() > 0) {
            result.append(Component.literal(currentText.toString())
                    .withStyle(currentStyle));
        }

        return result;
    }
}