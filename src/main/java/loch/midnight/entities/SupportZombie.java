package loch.midnight.entities;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import loch.midnight.Items.MidnightItems;
import loch.midnight.MidnightEngine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class SupportZombie extends ZombieEntity implements PolymerEntity {

    public static String CUSTOM_NAME = "Support Zombie";

    public SupportZombie(EntityType<? extends ZombieEntity> entityType, World world, LivingEntity target) {
        super(entityType, world);
        this.setBaby(true);
        this.setHealth(10.0F);
        this.setCustomName(Text.of(CUSTOM_NAME));
        if (target != null)
            this.setTarget(target);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ZOMBIE;
    }

    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 0.6, false));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, true));
    }

    public static Box getBoxFromSize(Vec3d pos, int size) {
        return new Box(
                pos.getX()-size,pos.getY()-size,pos.getZ()-size,
                pos.getX()+size,pos.getY()+size,pos.getZ()+size
        );
    }

    public LivingEntity getValidTarget(Entity avoid) {

        LivingEntity valid_target = null;

        if (getWorld() instanceof ServerWorld world) {

            var near_entities = world.getEntitiesByType(
                    TypeFilter.instanceOf(LivingEntity.class),
                    getBoxFromSize(this.getPos(), 10),
                    EntityPredicates.VALID_ENTITY
            );

            for ( Entity entity : near_entities ) {

                if (entity.getUuid() != avoid.getUuid() && entity instanceof LivingEntity livingEntity)
                    valid_target = livingEntity;

            }

        }

        return valid_target;

    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld() instanceof ServerWorld world) {

            // if the target is wearing the crown, then pick somebody else to target
            boolean is_wearing_crown = false;
            LivingEntity targeted = this.getTarget();
            if (targeted != null) {
                if (targeted.getEquippedStack(EquipmentSlot.HEAD).isOf(MidnightItems.ZOMBIE_KING_CROWN))
                    is_wearing_crown=true;
            }

            if (is_wearing_crown) {
                setTarget(getValidTarget(targeted));
                MidnightEngine.LOGGER.info("found player");
            }
        }
    }
}
