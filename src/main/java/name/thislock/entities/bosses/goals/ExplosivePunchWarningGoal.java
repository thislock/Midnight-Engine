package name.thislock.entities.bosses.goals;

import name.thislock.entities.bosses.boss_creation.Boss;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ExplosivePunchWarningGoal extends Goal {
    @Override
    public boolean canStart() {
        return true;
    }

    Boss boss;
    public ExplosivePunchWarningGoal(Boss boss) {
        this.boss = boss;
    }

    int potion_delay = 0;

    GoalParticles particles = new GoalParticles(this.boss);
    public void tick() {
        var offset = new Vec3d(0.1, 0.1, 0.1);
        if (boss.getWorld() instanceof ServerWorld world) {
            particles.spawn_particles(world, ParticleTypes.DRAGON_BREATH, boss.getPos(), offset, 3, 0.3);

            if (++potion_delay > 40) {
                potion_delay = 0;
                ItemStack potion = PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.STRONG_HEALING);
                SplashPotionEntity potion_entity = new SplashPotionEntity(world, boss, potion);
                world.spawnEntity(potion_entity);
            }
        }

    }
}
