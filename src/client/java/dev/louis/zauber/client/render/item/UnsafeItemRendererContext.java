package dev.louis.zauber.client.render.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Unit;

public interface UnsafeItemRendererContext {
    ThreadLocal<LivingEntity> RENDERER_ENTITY = new ThreadLocal<>();
    ThreadLocal<Unit> IN_STAFF_RENDERING = new ThreadLocal<>();
}
