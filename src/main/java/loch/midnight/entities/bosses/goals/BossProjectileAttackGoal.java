package loch.midnight.entities.bosses.goals;

import loch.midnight.entities.bosses.boss_creation.Boss;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BossProjectileAttackGoal extends Goal {
    private final MobEntity mob;
    private final RangedAttackMob owner;
    @Nullable
    private LivingEntity target;
    private int updateCountdownTicks;
    private final double mobSpeed;
    private int seenTargetTicks;
    private final int minIntervalTicks;
    private final int maxIntervalTicks;
    private final float maxShootRange;
    private final float minShootRange;
    private final float squaredMaxShootRange;
    private final float squaredMinShootRange;

    GoalParticles particles;

    public BossProjectileAttackGoal(Boss mob, double mobSpeed, int intervalTicks, float minShootRange, float maxShootRange) {
        this(mob, mobSpeed, intervalTicks, intervalTicks, minShootRange, maxShootRange);
    }

    public BossProjectileAttackGoal(Boss mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float minShootRange, float maxShootRange) {
        this.updateCountdownTicks = -1;
        if (!(mob instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        } else {

            this.particles = new GoalParticles(mob);

            this.owner = mob;
            this.mob = (MobEntity)mob;
            this.mobSpeed = mobSpeed;

            this.minIntervalTicks = minIntervalTicks;
            this.maxIntervalTicks = maxIntervalTicks;

            this.minShootRange = minShootRange;
            this.maxShootRange = maxShootRange;

            this.squaredMaxShootRange = maxShootRange * maxShootRange;
            this.squaredMinShootRange = minShootRange * minShootRange;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            this.target = livingEntity;
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldContinue() {
        return this.canStart() || this.target.isAlive() && !this.mob.getNavigation().isIdle();
    }

    public void stop() {
        this.target = null;
        this.seenTargetTicks = 0;
        this.updateCountdownTicks = -1;
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {

        this.particles.emmit_particle_of(ParticleTypes.ASH, 2);

        final double current_target_distance = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());

        boolean bl = this.mob.getVisibilityCache().canSee(this.target);
        if (bl) {
            ++this.seenTargetTicks;
        } else {
            this.seenTargetTicks = 0;
        }

        // if the target is too far away, run towards it
        if (!(current_target_distance > (double)this.squaredMaxShootRange) && this.seenTargetTicks >= 5) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
        }

        // if the target is too close, run away
        if (!(current_target_distance < (double)this.squaredMinShootRange) && this.seenTargetTicks >= 5) {
            this.mob.getNavigation().stop();
        } else {
            Vec3d vec3d = NoPenaltyTargeting.findFrom((PathAwareEntity) this.mob, 16, 7, this.target.getPos());
            if (vec3d != null) {
                this.mob.getNavigation().startMovingAlong(this.mob.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0), this.mobSpeed);
            }
        }

        this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
        if (--this.updateCountdownTicks == 0) {
            if (!bl) {
                return;
            }

            float f = (float)Math.sqrt(current_target_distance) / this.maxShootRange;
            float g = MathHelper.clamp(f, 0.1F, 1.0F);
            this.owner.shootAt(this.target, g);
            this.updateCountdownTicks = MathHelper.floor(f * (float)(this.maxIntervalTicks - this.minIntervalTicks) + (float)this.minIntervalTicks);
        } else if (this.updateCountdownTicks < 0) {
            this.updateCountdownTicks = MathHelper.floor(MathHelper.lerp(Math.sqrt(current_target_distance) / (double)this.maxShootRange, (double)this.minIntervalTicks, (double)this.maxIntervalTicks));
        }

    }
}
