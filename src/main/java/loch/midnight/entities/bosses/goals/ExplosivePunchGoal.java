package loch.midnight.entities.bosses.goals;

import loch.midnight.entities.bosses.boss_creation.Boss;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExplosivePunchGoal extends MeleeAttackGoal {
    private final Boss boss;
    private int ticks;

    GoalParticles particles;

    public ExplosivePunchGoal(Boss boss, double speed, boolean pauseWhenMobIdle) {
        super(boss, speed, pauseWhenMobIdle);
        this.boss = boss;
        this.particles = new GoalParticles(boss);
    }

    public void start() {
        super.start();
        this.ticks = 0;
    }

    public void stop() {
        super.stop();
        this.boss.setAttacking(false);
    }

    public void spawn_explosion_at(@Nullable Entity target) {

        if (target == null) {
            this.boss.boss_info("attempted to create explosion punch without target");
            return;
        }

        var pos = this.mob.getPos();
        if (target.getWorld() instanceof ServerWorld serverWorld && pos != null) {
            var explosion_owner = this.boss;
            serverWorld.createExplosion(explosion_owner, pos.x, pos.y, pos.z, 2.5F, World.ExplosionSourceType.MOB);
            explosion_owner.boss_info("punched the shit outta the player with explosive punch sucessfully");
        } else {
            boss.boss_info("failed to create explosion from explosion punch");
        }

    }

    final int explosionDelay = 10;
    int explosionCooldown = 0;

    public void tick() {
        super.tick();

        particles.emmit_particle_of(ParticleTypes.DRIPPING_LAVA, 3);

        ++this.ticks;
        if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
            this.boss.setAttacking(true);

            if (this.boss.getTarget() == null) return;

            if (explosionCooldown < 1 && this.boss.getTarget().getPos().distanceTo(this.boss.getPos()) < 3.0) {
                this.spawn_explosion_at(this.boss.getTarget());
                explosionCooldown = explosionDelay;
            }
            --explosionCooldown;

            for (int i = 0; i < 2; i++) {
                particles.emmit_particle_of(ParticleTypes.FLASH, 1);
            }

        } else {
            this.boss.setAttacking(false);
        }

    }
}
