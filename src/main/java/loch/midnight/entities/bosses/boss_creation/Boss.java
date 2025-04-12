package loch.midnight.entities.bosses.boss_creation;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import loch.midnight.MidnightEngine;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.*;

public class Boss extends HostileEntity implements PolymerEntity, RangedAttackMob {

    EntityType<? extends PathAwareEntity> display_entity = EntityType.ZOMBIE;
    public EntityType<? extends PathAwareEntity> custom_entity = EntityType.ZOMBIE;

    ArrayList<UUID> known_players = new ArrayList<>();

    final int default_health_bar_size = 40;
    int health_bar_size = default_health_bar_size;

    BossHealthBar healthBar = new BossHealthBar(this, 40);

    String boss_name = "unnamed boss";
    public BossPhases phases = new BossPhases(100, 10);

    public Boss(
            World world,
            @Nullable Vec3d pos,
            EntityType<? extends HostileEntity> custom_entity,
            EntityType<? extends HostileEntity> display_entity,
            String boss_name,
            long move_switch_frequency
    ) {

        super(custom_entity, world);
        this.boss_info("starting initialising boss");

        this.boss_name = boss_name;

        this.display_entity = display_entity;
        this.custom_entity = custom_entity;
        if (pos != null)
            this.setPosition(pos);

        phases.setMoveSwitchFrequency(move_switch_frequency);

        this.boss_info("created new boss");
    }

    public void createBossDrops(ServerWorld world) {
    }

    protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
        super.dropEquipment(world, source, causedByPlayer);
        this.createBossDrops(world);
    }

    long ticks_alive = 1;

    private double health_last_tick = this.getHealth();
    public boolean hasBeenDamaged() {
        return this.health_last_tick != this.getHealth();
    }

    BossCheckers checks = new BossCheckers();

    private void checkers() {
        phases.checkers(this, this.ticks_alive);
        checks.check_all(this);
    }

    // everything overridden by extended classes
    public void boss_tick() {
    }

    public boolean init_later = true;
    public void init() {
    }

    public Entity create_projectile() {
        return null;
    }

    public void tick() {
        try {
            this.tick_logic();
        } catch (Exception e) {
            var error_message = """
                    something went very wrong!
                    if you're reading this, my potato code failed in some way, and almost crashed the server,  \
                    the following is an error message that minecraft spat out, please report this in the 'bug reporting'   \
                    part of the discord server, so that i can fix it.   \
                    to prevent the error from crashing the server, i had to kill whatever caused it, so sorry about that.  \
                    ERROR:   \n{""" + e.getMessage() + "}";
            MidnightEngine.announce(error_message);
            this.healthBar.delete();
            this.remove(RemovalReason.DISCARDED);
        }
    }

    private void tick_logic() {

        if (!(this.getWorld() instanceof ServerWorld)) {
            return;
        }

        // first time init
        if (this.init_later && this.isAlive() && this.custom_entity != null) {
            this.init();
            this.healthBar = new BossHealthBar(this, health_bar_size);
            this.init_later = false;
        }

        if (!this.healthBar.isValid() && this.isAlive())
            this.healthBar = new BossHealthBar(this, health_bar_size);
        else
            healthBar.tick();

        if (this.isDead())
            this.healthBar.delete();

        ticks_alive++;
        // checks for special conditions
        this.checkers();
        // the logic ran by the inherited boss class
        this.boss_tick();
        this.health_last_tick = this.getHealth();

        super.tick();
    }


    public GoalSelector getGoalSelector() {
        return this.goalSelector;
    }
    public void addGoal(int priority, Goal goal) {
        this.goalSelector.add(priority, goal);
    }
    public void removeGoal(Goal goal) {
        this.goalSelector.remove(goal);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 20.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return this.display_entity;
    }

    public void boss_info(String info) {
        MidnightEngine.LOGGER.info("FOR BOSS {}: {}", boss_name, info);
    }

    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        var damaged = super.damage(world, source, amount);
        boss_info(String.valueOf(amount));
        if (source.getAttacker() instanceof PlayerEntity player)
            this.setTarget(player);
        return damaged;
    }

    public void tickMovement() {
        super.tickMovement();
    }

    public static final String IS_BOSS_NBT = "IsCustomBoss";
    public static final String BOSS_NAME_NBT = "CustomBossName";
    public static final String TICKS_LIVED_NBT = "BossTicksLived";

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean(IS_BOSS_NBT, true);
        nbt.putString(BOSS_NAME_NBT, this.boss_name);
        nbt.putLong(TICKS_LIVED_NBT, this.ticks_alive);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        BossMisc.boss_shoot(this, target, pullProgress);
    }

    @Override
    public Arm getMainArm() {
        return null;
    }


}
