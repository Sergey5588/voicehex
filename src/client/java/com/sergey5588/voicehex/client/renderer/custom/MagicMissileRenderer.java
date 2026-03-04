package com.sergey5588.voicehex.client.renderer.custom;

import com.sergey5588.voicehex.entity.custom.MagicMissile;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class MagicMissileRenderer extends EntityRenderer<MagicMissile> {

    @Override
    public Identifier getTexture(MagicMissile entity) {
        return null;
    }
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal.png");
    private static final RenderLayer END_CRYSTAL;
    private static final float SINE_45_DEGREES;
    private static final String GLASS = "glass";
    private static final String BASE = "base";
    private final ModelPart core;
    private final ModelPart frame;
    private final ModelPart bottom;

    public MagicMissileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        ModelPart modelPart = context.getPart(EntityModelLayers.END_CRYSTAL);
        this.frame = modelPart.getChild("glass");
        this.core = modelPart.getChild("cube");
        this.bottom = modelPart.getChild("base");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("glass", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        modelPartData.addChild("cube", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 16).cuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public void render(MagicMissile magicMissile, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = 0;
        float j = 0;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
        matrixStack.push();
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        matrixStack.translate(0.0F, -0.5F, 0.0F);
        int k = OverlayTexture.DEFAULT_UV;

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0F, 1.5F + h / 2.0F, 0.0F);
        matrixStack.multiply((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        this.frame.render(matrixStack, vertexConsumer, i, k);
        float l = 0.875F;
        matrixStack.scale(0.875F, 0.875F, 0.875F);
        matrixStack.multiply((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        this.frame.render(matrixStack, vertexConsumer, i, k);
        matrixStack.scale(0.875F, 0.875F, 0.875F);
        matrixStack.multiply((new Quaternionf()).setAngleAxis(((float)Math.PI / 3F), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        this.core.render(matrixStack, vertexConsumer, i, k);
        matrixStack.pop();
        matrixStack.pop();
        BlockPos blockPos = magicMissile.getBlockPos();
        if (blockPos != null) {
            float m = (float)blockPos.getX() + 0.5F;
            float n = (float)blockPos.getY() + 0.5F;
            float o = (float)blockPos.getZ() + 0.5F;
            float p = (float)((double)m - magicMissile.getX());
            float q = (float)((double)n - magicMissile.getY());
            float r = (float)((double)o - magicMissile.getZ());
            matrixStack.translate(p, q, r);
            EnderDragonEntityRenderer.renderCrystalBeam(-p, -q + h, -r, g, 0, matrixStack, vertexConsumerProvider, i);
        }

        super.render(magicMissile, f, g, matrixStack, vertexConsumerProvider, i);
    }



    public Identifier getTexture(EndCrystalEntity endCrystalEntity) {
        return TEXTURE;
    }

    public boolean shouldRender(MagicMissile magicMissile, Frustum frustum, double d, double e, double f) {
        return super.shouldRender(magicMissile, frustum, d, e, f);
    }

    static {
        END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
        SINE_45_DEGREES = (float)Math.sin((Math.PI / 4D));
    }



}
