package loch.midnight.entities.bosses;

import loch.midnight.entities.bosses.boss_creation.Boss;
import loch.midnight.entities.bosses.goals.BossSummonGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class ZombieKing extends Boss {

    public ZombieKing(EntityType<Boss> type, World world) {
        super(world, null, type, EntityType.ZOMBIE, "Zombie King", 30);
        this.phases.disableBossPhases();
    }

    public void init() {
        this.init_armor();
    }

    private void init_armor() {
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
        this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
    }

    @Override
    protected void initGoals() {

        EntityType.EntityFactory<Entity> minion_builder = ZombieKingMinion::new;

        this.goalSelector.add(0, new SwimGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new BossSummonGoal(this, minion_builder, 5));
    }

}
