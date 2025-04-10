package name.thislock.entities.bosses.goals;

import name.thislock.entities.bosses.boss_creation.Boss;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class BossAttackGoal extends MeleeAttackGoal {
    private final Boss boss;
    private int ticks;

    public BossAttackGoal(Boss boss, double speed, boolean pauseWhenMobIdle) {
        super(boss, speed, pauseWhenMobIdle);
        this.boss = boss;
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
        } else {
            this.boss.setAttacking(false);
        }

    }
}