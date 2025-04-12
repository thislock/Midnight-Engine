package loch.midnight.entities.bosses;

import eu.pb4.polymer.core.api.entity.PolymerEntity;

import loch.midnight.entities.bosses.boss_creation.Boss;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class ZombieKingMinion extends ZombieEntity implements PolymerEntity {

    public ZombieKingMinion(Boss boss) {

        super(EntityType.ZOMBIE, boss.getWorld());

        this.setBaby(true);
        this.setHealth(5.0F);

        this.setTarget(boss.getTarget());
    }

    public ZombieKingMinion(EntityType<Entity> entityEntityType, World world) {
        super(EntityType.ZOMBIE, world);

        this.setBaby(true);
        this.setHealth(5.0F);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ZOMBIE;
    }

    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 0.6, false));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
    }
}
