package loch.midnight.entities;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class ExplosiveArrow extends ArrowEntity implements PolymerEntity {

    PlayerEntity player_owner = null;

    @Override
    public void tick() {
        super.tick();
        if (getWorld() instanceof ServerWorld world) {
            var pos = getPos();
            world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, 2, 0.0,0.0,0.0,0.1);
        }
    }

    public ExplosiveArrow(World world, PlayerEntity player, ItemStack arrow_item, ItemStack arrow_item1) {
        super(world, player, arrow_item, arrow_item1);
        player_owner=player;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ARROW;
    }

    public void spawn_tnt(ServerWorld world, float explosion_power) {
        var pos = getPos();
        world.createExplosion(player_owner, pos.x, pos.y, pos.z, explosion_power, World.ExplosionSourceType.MOB);
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void onCollision(HitResult hitResult) {
        if (this.getWorld() instanceof ServerWorld world) {

            if (hitResult.getType() == HitResult.Type.ENTITY)
                spawn_tnt(world, 3.0F);
            else
                spawn_tnt(world, 1.0F);
        }
    }

}
