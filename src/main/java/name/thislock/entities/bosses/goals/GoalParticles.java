package name.thislock.entities.bosses.goals;

import name.thislock.entities.bosses.boss_creation.Boss;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class GoalParticles {

    Boss boss;
    private static Random random = new Random();

    public GoalParticles(Boss boss) {
        this.boss = boss;
    }

    public void spawn_particles(ServerWorld world, ParticleEffect particleType, Vec3d pos, Vec3d offset, int amount, double speed) {
        world.spawnParticles(particleType, pos.x, pos.y, pos.z, amount, offset.x, offset.y, offset.z, speed);
    }

    private int counter = 0;

    public void emmit_particles() {

        final int frequency = 5;

        if (counter++ < frequency)
            return;

        if (this.boss.getWorld() instanceof ServerWorld world && this.boss.getTarget() instanceof ServerPlayerEntity player) {
            var pos = this.boss.getPos();
            var offset = new Vec3d(random.nextDouble(), random.nextDouble(), random.nextDouble());
            this.spawn_particles(world, ParticleTypes.DRIPPING_LAVA, pos, offset, frequency, 0.3);
        }
    }

}
