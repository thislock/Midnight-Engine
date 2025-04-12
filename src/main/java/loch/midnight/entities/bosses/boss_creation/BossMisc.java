package loch.midnight.entities.bosses.boss_creation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BossMisc {

    public static void boss_shoot(Boss boss, LivingEntity target, float pullProgress) {

        Entity fake_projectile = boss.create_projectile();
        if (fake_projectile == null)
            return;

        double d = target.getX() - boss.getX();
        double e = target.getEyeY() - (double)1.1F;
        double f = target.getZ() - boss.getZ();
        double g = Math.sqrt(d * d + f * f) * (double)0.2F;
        World world = boss.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            ItemStack itemStack = new ItemStack(Items.SNOWBALL);
            ProjectileEntity snowball = new SnowballEntity(serverWorld, boss, itemStack);

            ProjectileEntity.spawn(snowball, serverWorld, itemStack, (entity) -> entity.setVelocity(d, e + g - entity.getY(), f, 1.6F, 12.0F));

            snowball.setInvisible(true);

            // spawn custom projectile entity and have it ride the snowball

            fake_projectile.setPosition(snowball.getPos());
            serverWorld.spawnEntity(fake_projectile);

            fake_projectile.startRiding(snowball);
        }

        boss.playSound(SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, 1.0F, 0.4F / (boss.getRandom().nextFloat() * 0.4F + 0.8F));

    }

    public static double distance_between(Vec3d pos1, Vec3d pos2) {
        if (pos1 == null || pos2 == null)
            return Double.POSITIVE_INFINITY;
        return pos1.distanceTo(pos2);
    }

}
