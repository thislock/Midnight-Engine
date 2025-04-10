package name.thislock.entities;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import static net.minecraft.entity.projectile.AbstractWindChargeEntity.EXPLOSION_BEHAVIOR;

public class KnockbackArrow extends ArrowEntity implements PolymerEntity {
    PlayerEntity player_owner = null;

    public KnockbackArrow(World world, PlayerEntity player, ItemStack arrow_item, ItemStack arrow_item1) {
        super(world, player, arrow_item, arrow_item1);
        player_owner=player;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ARROW;
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld() instanceof ServerWorld world) {
            var pos = getPos();
            world.spawnParticles(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 1, 0.0,0.0,0.0,0.0);
        }
    }

    public void create_wind_explosion(Vec3d pos, float power_multiplier) {
        this.getWorld().createExplosion(
                this, (DamageSource)null,
                EXPLOSION_BEHAVIOR,
                pos.getX(), pos.getY(), pos.getZ(),
                1.2F * power_multiplier,
                false,
                World.ExplosionSourceType.TRIGGER,
                ParticleTypes.GUST_EMITTER_SMALL,
                ParticleTypes.GUST_EMITTER_LARGE,
                SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
        );

    }

    public void spawn_charges(ServerWorld world) {

        var pos = getPos();
        var velocity = getVelocity().multiply(10.0);

        create_wind_explosion(pos, 2);
        create_wind_explosion(pos, 2);

        this.remove(Entity.RemovalReason.DISCARDED);

    }

    @Override
    public void onCollision(HitResult hitResult) {
        if (this.getWorld() instanceof ServerWorld world)
            spawn_charges(world);
    }
}
