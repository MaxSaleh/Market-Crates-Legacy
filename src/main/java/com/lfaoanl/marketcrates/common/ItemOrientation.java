package com.lfaoanl.marketcrates.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import com.mojang.math.Quaternion;

import java.util.Random;
import java.util.function.Consumer;

public class ItemOrientation {

    private final ItemStack itemStack;

    private final float height;
    private final Quaternion[] rotation = new Quaternion[3];


    public static final ItemOrientation EMPTY = new ItemOrientation(ItemStack.EMPTY);

    private static Quaternion HORIZONTAL;

    private boolean hasOrientations = false;

    @SuppressWarnings("unchecked")
    private static final Consumer<PoseStack>[] cratePositions = new Consumer[]{
            (Object m) -> ((PoseStack) m).translate(0.6, 0.1, 0.65), // Upper Left
            (Object m) -> ((PoseStack) m).translate(0.6, 0.1, 0.4), // Middle left

            (Object m) -> ((PoseStack) m).translate(0.6, 0.1, 0.12), // Lower left
            (Object m) -> ((PoseStack) m).translate(0.4, 0.1, 0.65), // Upper right

            (Object m) -> ((PoseStack) m).translate(0.4, 0.1, 0.4), // Middle right
            (Object m) -> ((PoseStack) m).translate(0.4, 0.1, 0.12) // Lower right
    };

    public ItemOrientation(ItemStack itemStack) {
        this.itemStack = itemStack;
        height = 0.1f;

//        if (MarketCrates.proxy.getWorld() != null) {
            generateOrientations();
//        }
    }

    public Runnable generateOrientations() {
        if (hasOrientations) {
            return () -> {
            };
        }

        int lowIncline = -10;
        int incline = 25;
        rotation[0] = new Quaternion(randomInt(lowIncline, incline), randomInt(45), randomInt(incline), true);
        rotation[1] = new Quaternion(randomInt(lowIncline, incline), randomInt(45), randomInt(incline), true);
        rotation[2] = new Quaternion(randomInt(lowIncline, incline), randomInt(45), randomInt(incline), true);

        HORIZONTAL = new Quaternion(85, 0, 0, true);

        hasOrientations = true;

        return () -> {
        };
    }

    public static NonNullList<ItemOrientation> toItemOrientation(NonNullList<ItemStack> stacks) {
        NonNullList<ItemOrientation> list = NonNullList.withSize(stacks.size(), ItemOrientation.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            list.set(i, new ItemOrientation(stacks.get(i)));
        }
        return list;
    }

    public static NonNullList<ItemStack> toItemStack(NonNullList<ItemOrientation> stacks) {
        NonNullList<ItemStack> list = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            list.set(i, stacks.get(i).itemStack);
        }
        return list;
    }

    private float randomInt(int i) {
        return randomInt(-i, i);
    }

    private float randomInt(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    public void render(int index, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
        matrix.pushPose();
        cratePositions[index].accept(matrix);

        int round = Math.max(1, Math.round(itemStack.getCount() / 21f));
        for (int i = 0; i < round; i++) {
            renderLayer(i, matrix, buffer, light, overlay);
        }

        matrix.popPose();
    }

    private void renderLayer(int layer, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
        matrix.pushPose();

        matrix.translate(0, height * layer, 0);
        matrix.mulPose(rotation[layer]);

        renderItem(matrix, buffer, light, overlay);

        matrix.popPose();
    }

    private void renderItem(PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
        matrix.mulPose(HORIZONTAL);
        matrix.scale(0.75f, 0.75f, 0.75f);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                itemStack,
                ItemTransforms.TransformType.GROUND,
                light,
                overlay,
                matrix,
                buffer,
                0 // (LivingEntity).getId()
        );
    }

    public boolean isEmpty() {
        if (this.itemStack == ItemStack.EMPTY) {
            return true;
        } else if (this.itemStack.getItem() != Items.AIR) {
            return this.itemStack.getCount() <= 0;
        } else {
            return true;
        }
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}


