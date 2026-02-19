package net.ray.holoitems;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.ray.HologramAPI.Hologram;
import net.ray.HologramAPI.HologramAPI;
import net.ray.holoitems.config.ConfigGetter;
import net.ray.holoitems.config.IndicatorConfig.DisplayMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ItemHandler {
    public static final ConcurrentHashMap<ItemEntity, ItemData> itemData = new ConcurrentHashMap<>();
    public static ItemEntity lastLookedAtItem = null;
    public static ItemEntity currentHoveredItem = null;

    public static class ItemData {
        public Hologram hologram;
        public List<Hologram> tooltipLines;
        public Component previousName;
        public int previousAmount;
        public boolean showingTooltip = false;

        public ItemData(Hologram holo, Component name, int amount) {
            this.hologram = holo;
            this.tooltipLines = new ArrayList<>();
            this.previousName = name;
            this.previousAmount = amount;
        }
    }

    private static void createHologram(ItemEntity item, Component text) {
        Hologram holo = HologramAPI.create(text, 0, 0, 0)
                .renderDistance(ConfigGetter.iconfig.renderDistance)
                .scale(ConfigGetter.iconfig.indicatorScale)
                .shadow(ConfigGetter.iconfig.shadow)
                .trackEntity(item.getId(), new Vec3(0, 0.5 + ConfigGetter.iconfig.offset, 0));

        holo.onRender(h -> {
            if (item.isAlive()) {
                float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
                float ageInTicks = (float)item.getAge() + partialTick;
                if(ConfigGetter.iconfig.bobbing){
                    float bobbingOffset = (float)(Math.sin(ageInTicks / 10.0F + item.bobOffs) * 0.1F + 0.1F);
                    h.offsetFromEntity = new Vec3(0, 0.5 + ConfigGetter.iconfig.offset + bobbingOffset, 0);
                }

            }
        });

        ItemData data = new ItemData(holo, item.getDisplayName(), item.getItem().getCount());
        itemData.put(item, data);

        holo.onUpdate(h -> updateHologramText(h, item));
    }

    private static void setTooltipVisible(ItemEntity item, boolean visible) {
        ItemData idata = itemData.get(item);
        if (idata == null) return;

        if (visible && !idata.showingTooltip) {
            Minecraft mc = Minecraft.getInstance();
            ItemStack stack = item.getItem();
            List<Component> tooltips = stack.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, TooltipFlag.Default.NORMAL);
            for (Hologram h : idata.tooltipLines) {
                h.remove();
            }
            idata.tooltipLines.clear();
            float tooltipScale = ConfigGetter.iconfig.tooltipScale;
            float lineSpacing = 0.3f * tooltipScale;
            float nameToLoreGap = 0.15f * tooltipScale;
            int loreCount = tooltips.size() - 1;

            if (ConfigGetter.iconfig.tooltipAboveName) {

                for (int i = 1; i < tooltips.size(); i++) {
                    Component line = tooltips.get(i);
                    int loreIndex = i - 1;
                    float yOffset = 0.5f + ConfigGetter.iconfig.offset + nameToLoreGap + ((loreCount - loreIndex) * lineSpacing);

                    Hologram lineHolo = HologramAPI.create(line, 0, 0, 0)
                            .renderDistance(ConfigGetter.iconfig.tooltipRenderDistance)
                            .scale(tooltipScale)
                            .shadow(ConfigGetter.iconfig.shadow)
                            .trackEntity(item.getId(), new Vec3(0, yOffset, 0));
                    lineHolo.onRender(h -> {
                        if (item.isAlive()) {
                            float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
                            float ageInTicks = (float)item.getAge() + partialTick;
                            if(ConfigGetter.iconfig.bobbing){
                                float bobbingOffset = (float)(Math.sin(ageInTicks / 10.0F + item.bobOffs) * 0.1F + 0.1F);
                                h.offsetFromEntity = new Vec3(0, yOffset + bobbingOffset, 0);
                            }

                        }
                    });

                    idata.tooltipLines.add(lineHolo);
                }
            } else {
                float nameYOffset = 0.5f + ConfigGetter.iconfig.offset + (loreCount * lineSpacing) + nameToLoreGap;
                Hologram mainHolo = idata.hologram;
                mainHolo.onRender(h -> {
                    if (item.isAlive()) {
                        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
                        float ageInTicks = (float)item.getAge() + partialTick;
                        if(ConfigGetter.iconfig.bobbing){
                            float bobbingOffset = (float)(Math.sin(ageInTicks / 10.0F + item.bobOffs) * 0.1F + 0.1F);
                            h.offsetFromEntity = new Vec3(0, nameYOffset + bobbingOffset, 0);
                        }

                    }
                });

                for (int i = 1; i < tooltips.size(); i++) {
                    Component line = tooltips.get(i);
                    int loreIndex = i - 1;
                    float yOffset = nameYOffset - nameToLoreGap - ((loreIndex + 1) * lineSpacing);

                    Hologram lineHolo = HologramAPI.create(line, 0, 0, 0)
                            .renderDistance(ConfigGetter.iconfig.tooltipRenderDistance)
                            .scale(tooltipScale)
                            .shadow(ConfigGetter.iconfig.shadow)
                            .trackEntity(item.getId(), new Vec3(0, yOffset, 0));
                    lineHolo.onRender(h -> {
                        if (item.isAlive()) {
                            float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
                            float ageInTicks = (float)item.getAge() + partialTick;
                            if(ConfigGetter.iconfig.bobbing){
                                float bobbingOffset = (float)(Math.sin(ageInTicks / 10.0F + item.bobOffs) * 0.1F + 0.1F);
                                h.offsetFromEntity = new Vec3(0, yOffset + bobbingOffset, 0);
                            }

                        }
                    });

                    idata.tooltipLines.add(lineHolo);
                }
            }

            idata.showingTooltip = true;

        } else if (!visible && idata.showingTooltip) {
            Hologram mainHolo = idata.hologram;
            mainHolo.onRender(h -> {
                if (item.isAlive()) {
                    float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
                    float ageInTicks = (float)item.getAge() + partialTick;
                    if(ConfigGetter.iconfig.bobbing){
                        float bobbingOffset = (float)(Math.sin(ageInTicks / 10.0F + item.bobOffs) * 0.1F + 0.1F);
                        h.offsetFromEntity = new Vec3(0, 0.5 + ConfigGetter.iconfig.offset + bobbingOffset, 0);
                    }

                }
            });
            for (Hologram h : idata.tooltipLines) {
                h.remove();
            }
            idata.tooltipLines.clear();
            idata.showingTooltip = false;
        }
    }

    public static void update() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        Player player = mc.player;
        Level level = player.level();

        if (!ConfigGetter.iconfig.enableIndicator) {
            itemData.forEach((item, data) -> {
                if (data.hologram != null) {
                    data.hologram.remove();
                }
            });
            itemData.clear();
            lastLookedAtItem = null;
            currentHoveredItem = null;
            return;
        }

        ItemEntity currentTarget = getTargetedItem(mc);

        if (ConfigGetter.iconfig.showTooltips) {
            handleTooltipHover(player, level, currentTarget);
        }

        handleDisplayConditions(player, level, currentTarget);
        cleanupOldHolograms(player, level);
    }

    private static void handleTooltipHover(Player player, Level level, ItemEntity currentTarget) {
        ItemEntity newHoveredItem = findItemInViewAngle(player, level);

        if (newHoveredItem != currentHoveredItem) {
            if (currentHoveredItem != null) {
                setTooltipVisible(currentHoveredItem, false);
            }
            if (newHoveredItem != null) {
                setTooltipVisible(newHoveredItem, true);
            }

            currentHoveredItem = newHoveredItem;
        }
    }

    private static ItemEntity findItemInViewAngle(Player player, Level level) {
        int renderDistance = ConfigGetter.iconfig.renderDistance;
        AABB area = new AABB(
                player.getX() - renderDistance, player.getY() - renderDistance, player.getZ() - renderDistance,
                player.getX() + renderDistance, player.getY() + renderDistance, player.getZ() + renderDistance
        );

        List<Entity> nearbyEntities = level.getEntities(player, area);
        ItemEntity closestItem = null;
        double smallestAngle = Double.MAX_VALUE;

        Vec3 lookVec = player.getLookAngle().normalize();

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof ItemEntity item) || !item.isAlive()) {
                continue;
            }

            Vec3 toItem = item.position().subtract(player.getEyePosition()).normalize();
            double dot = lookVec.dot(toItem);
            double angle = Math.acos(Math.max(-1, Math.min(1, dot))) * (180.0 / Math.PI);
            float directionAngle = ConfigGetter.iconfig.directionAngle;

            if (angle < directionAngle) {
                if (angle < smallestAngle) {
                    smallestAngle = angle;
                    closestItem = item;
                }
            }
        }

        return closestItem;
    }

    private static ItemEntity getTargetedItem(Minecraft mc) {
        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hit).getEntity();
            if (entity instanceof ItemEntity) {
                return (ItemEntity) entity;
            }
        }
        return null;
    }

    private static void handleDisplayConditions(Player player, Level level, ItemEntity currentTarget) {
        DisplayMode displayMode = ConfigGetter.iconfig.displayMode;
        int renderDistance = ConfigGetter.iconfig.renderDistance;

        AABB area = new AABB(
                player.getX() - renderDistance, player.getY() - renderDistance, player.getZ() - renderDistance,
                player.getX() + renderDistance, player.getY() + renderDistance, player.getZ() + renderDistance
        );

        List<Entity> nearbyEntities = level.getEntities(player, area);
        if (displayMode == DisplayMode.HOVER && currentTarget != lastLookedAtItem) {
            if (lastLookedAtItem != null) {
                ItemData idata = itemData.get(lastLookedAtItem);
                if (idata != null && idata.hologram != null) {
                    idata.hologram.visible(false);
                }
            }
            lastLookedAtItem = currentTarget;
        }

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof ItemEntity item) || !item.isAlive()) {
                continue;
            }

            double distance = player.distanceTo(item);
            if (distance > renderDistance) {
                cleanupItem(item);
                continue;
            }

            boolean shouldRender = shouldRenderItem(player, item, currentTarget, displayMode);

            if (shouldRender) {
                createOrUpdateHologram(player, item);
            } else {
                cleanupItem(item);
            }
        }
    }

    private static boolean shouldRenderItem(Player player, ItemEntity item, ItemEntity currentTarget, DisplayMode displayMode) {
        switch (displayMode) {
            case ALWAYS_ON:
                return true;

            case HOVER:
                Vec3 lookVec = player.getLookAngle().normalize();
                Vec3 toItem = item.position().subtract(player.getEyePosition()).normalize();
                double dot = lookVec.dot(toItem);
                double angle = Math.acos(Math.max(-1, Math.min(1, dot))) * (180.0 / Math.PI);
                float directionAngle = ConfigGetter.iconfig.directionAngle;
                return angle < directionAngle;

            default:
                return false;
        }
    }

    private static void createOrUpdateHologram(Player player, ItemEntity item) {
        if (!item.isAlive()) {
            cleanupItem(item);
            return;
        }

        Component text = getDisplayText(item);
        ItemData idata = itemData.get(item);

        if (idata == null || idata.hologram == null) {
            createHologram(item, text);
        } else {
            updateHologram(item, idata, text);
        }
    }

    private static Component getDisplayText(ItemEntity item) {
        return IndicatorFormatter.formatItem(item);
    }

    private static void updateHologram(ItemEntity item, ItemData idata, Component text) {
        Component currentName = item.getDisplayName();
        int currentAmount = item.getItem().getCount();

        boolean nameChanged = !idata.previousName.equals(currentName);
        boolean amountChanged = idata.previousAmount != currentAmount;
        boolean tooltipStateChanged = (ConfigGetter.iconfig.showTooltips && item == currentHoveredItem) != idata.showingTooltip;

        if (nameChanged || amountChanged) {
            if (!idata.showingTooltip) {
                idata.hologram.component(text);
            }
            idata.previousName = currentName;
            idata.previousAmount = currentAmount;
        }
        if (tooltipStateChanged) {
            if (ConfigGetter.iconfig.showTooltips && item == currentHoveredItem) {
                setTooltipVisible(item, true);
            } else {
                setTooltipVisible(item, false);
            }
        }

        idata.hologram.visible(true);
    }

    private static void updateHologramText(Hologram holo, ItemEntity item) {
        if (item == null || !item.isAlive()) {
            cleanupItem(item);
            return;
        }

        ItemData idata = itemData.get(item);
        if (idata == null || idata.hologram != holo) {
            return;
        }

        Component newText = getDisplayText(item);
        if (!holo.component.equals(newText)) {
            holo.component(newText);
        }
    }

    private static void cleanupItem(ItemEntity item) {
        ItemData idata = itemData.get(item);
        if (idata != null) {
            if (idata.hologram != null) {
                idata.hologram.visible(false);
            }
            for (Hologram h : idata.tooltipLines) {
                h.remove();
            }
            idata.tooltipLines.clear();
        }

        if (item == currentHoveredItem) {
            currentHoveredItem = null;
        }
        if (item == lastLookedAtItem) {
            lastLookedAtItem = null;
        }
    }

    private static void cleanupOldHolograms(Player player, Level level) {
        itemData.entrySet().removeIf(entry -> {
            ItemEntity item = entry.getKey();
            ItemData idata = entry.getValue();

            if (item == null || !item.isAlive() || item.isRemoved()) {
                if (idata.hologram != null) {
                    idata.hologram.remove();
                }
                return true;
            }

            double distance = player.distanceToSqr(item);
            if (distance > ConfigGetter.iconfig.renderDistance * ConfigGetter.iconfig.renderDistance) {
                if (idata.hologram != null) {
                    idata.hologram.remove();
                }
                return true;
            }

            return false;
        });
    }
}