package name.thislock.entities.bosses;

import name.thislock.ChaosFabric;
import name.thislock.entities.bosses.boss_creation.Boss;
import name.thislock.entities.bosses.boss_creation.BossHealthBar;
import name.thislock.entities.bosses.goals.BossAttackGoal;
import name.thislock.entities.bosses.goals.BossFleeGoal;
import name.thislock.entities.bosses.goals.ExplosivePunchGoal;
import name.thislock.entities.bosses.goals.ExplosivePunchWarningGoal;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExplosionZombie extends Boss {

    public void init() {
        this.init_armor();
        this.init_attack_patterns();
    }

    private void init_armor() {
        // Set armor pieces

        var pants = new ItemStack(Items.IRON_LEGGINGS);
        var boots = new ItemStack(Items.IRON_BOOTS);

        var blast_prot =
                this.getWorld().getRegistryManager().getEntryOrThrow(Enchantments.BLAST_PROTECTION);

        pants.addEnchantment(blast_prot, 3);
        boots.addEnchantment(blast_prot, 3);

        this.equipStack(EquipmentSlot.LEGS, pants);
        this.equipStack(EquipmentSlot.FEET, boots);
    }

    private void init_attack_patterns() {
        // phase 1
        {
            List<Goal> phase = new ArrayList<>();
            phase.add(new ProjectileAttackGoal(this, (double)1.25F, 20, 10.0F));
            phase.add(new BossFleeGoal<>(this, PlayerEntity.class, 5.0F, 1.0, 2.0));
            phase.add(new BossAttackGoal(this, 1.5F, false));
            phases.add_phase(phase);
        }

        // phase 2
        {
            List<Goal> phase2 = new ArrayList<>();
            phase2.add(new BossFleeGoal<>(this, PlayerEntity.class, 5.0F, 1.0, 2.0));
            phase2.add(new ProjectileAttackGoal(this, (double)1.25F, 10, 10.0F));
            phase2.add(new BossAttackGoal(this, 2.5F, false));
            phase2.add(new BossFleeGoal<>(this, PlayerEntity.class, 5.0F, 1.0, 2.0));

            phase2.add(new ExplosivePunchWarningGoal(this));
            phase2.add(new ExplosivePunchGoal(this, 3.0, false));

            phase2.add(new BossFleeGoal<>(this, PlayerEntity.class, 5.0F, 1.0, 2.0));

            phases.add_phase(phase2);
        }

        // phase 3, last 10% of health
        {
            List<Goal> phase = new ArrayList<>();

            phase.add(new BossFleeGoal<>(this, PlayerEntity.class, 5.0F, 1.0, 2.0));

            phase.add(new ExplosivePunchWarningGoal(this));
            phase.add(new ExplosivePunchGoal(this, 4.0, false));
            phase.add(new ExplosivePunchGoal(this, 4.0, false));

            phases.add_phase(phase);
        }

        phases.next_phase(this);
    }

    public ExplosionZombie(World world, EntityType<Boss> entityEntityType, @Nullable Vec3d spawn_pos) {
        super(world, spawn_pos, entityEntityType, EntityType.ZOMBIE, "Boom Zombie", 40);
    }

    public ExplosionZombie(EntityType<Boss> type, World world) {
        super(world, null, type, EntityType.ZOMBIE, "Boom Zombie", 40);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
    }

    public boolean tryAttack(ServerWorld world, Entity target) {
        boolean bl = super.tryAttack(world, target);
        if (bl) {
            var pos = this.getPos();
            world.createExplosion(this, pos.x, pos.y, pos.z, 1.0F, World.ExplosionSourceType.MOB);
        }

        return bl;
    }

    public static DefaultAttributeContainer.Builder createExplosionZombieAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 300)
                .add(EntityAttributes.ARMOR, 10)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.ATTACK_DAMAGE, 5.0);
    }

    @Override
    public void boss_tick() {

        if (this.getHealth() < this.getMaxHealth() / 2)
            phases.checked_next_phase(this, 1);

        if (this.getHealth() < this.getMaxHealth() / 10)
            phases.checked_next_phase(this, 2);

        // run away if too much damage is taken
        if (this.hasBeenDamaged() && random.nextInt(5) == 1) {
            phases.run_away(5, 3.0);
        }

    }

    @Override
    public Entity create_projectile() {

        TntEntity projectile = null;
        projectile = new TntEntity(EntityType.TNT, getWorld());
        projectile.setFuse(40);

        return projectile;
    }

}
