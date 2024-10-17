package dev.louis.zauber.client.render.item;

import net.minecraft.entity.LivingEntity;

public interface UnsafeItemRendererContext {
    ThreadLocal<LivingEntity> RENDERER_ENTITY = new ThreadLocal<>();
    ThreadLocal<Integer> STAFF_RENDERING_DEPTH = ThreadLocal.withInitial(() -> 0);
}
