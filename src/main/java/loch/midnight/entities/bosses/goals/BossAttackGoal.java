package loch.midnight.entities.bosses.goals;

import loch.midnight.entities.bosses.boss_creation.Boss;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class BossAttackGoal extends MeleeAttackGoal {
    private final Boss boss;
    private int ticks;

    private GoalParticles particles = null;

    public BossAttackGoal(Boss boss, double speed, boolean pauseWhenMobIdle) {
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

    public void tick() {
        super.tick();
        ++this.ticks;
        if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
            this.boss.setAttacking(true);
            particles.emmit_particle_of(ParticleTypes.ASH, 1);
        } else {
            this.boss.setAttacking(false);
            particles.emmit_particle_of(ParticleTypes.SMALL_GUST, 5);
        }

    }
}