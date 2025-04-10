package name.thislock.Items.armor;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import name.thislock.ChaosFabric;
import name.thislock.entities.KnockbackArrow;
import name.thislock.entities.SupportZombie;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ZombieKingCrown extends Item implements PolymerItem {

    public static final String ITEM_NAME = "zombie_king_crown";
    public static final Identifier ID = Identifier.of(ChaosFabric.MOD_ID, ITEM_NAME);

    public static final String IN_GAME_NAME = "Zombie King Crown";

    public static final Item.Settings SETTINGS = new Item.Settings()
            .maxCount(1)
            .maxDamage(200)
            .armor(ArmorMaterials.GOLD, EquipmentType.HELMET)
            .modelId(ChaosFabric.minecraftItem("golden_helmet"));

    public ZombieKingCrown(Settings settings) {
        super(settings);
    }



    @Override
    public void modifyClientTooltip(List<Text> tooltip, ItemStack stack, PacketContext context) {
        stack.set(DataComponentTypes.ITEM_NAME, Text.translatable(IN_GAME_NAME));
        tooltip.add(Text.of("Will summon zombies to aid you in conquest!"));
    }

    float OpponentHealthTracker = 0.0F;
    UUID opponentID = null;

    static Random random = new Random();

    double spawningBucket = 0.0;
    final double bucketCap = 100.0;

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {

        if (slot != null && entity instanceof LivingEntity wearer) {

            if (wearer.getAttacking() != null) {

                opponentID = wearer.getAttacking().getUuid();

                double spawningChance = 0.0;

                if (OpponentHealthTracker != wearer.getAttacking().getHealth()) {
                    spawningChance += Math.abs(wearer.getAttacking().getHealth() - OpponentHealthTracker) * (bucketCap/10);
                }
                else {
                    spawningChance += random.nextDouble(0.1, Math.max(spawningBucket, 0.2)) / 10;
                }

                OpponentHealthTracker = wearer.getAttacking().getHealth();

                if (wearer instanceof ServerPlayerEntity player) {
                    var pos = wearer.getAttacking().getPos();
                    world.spawnParticles(player, ParticleTypes.DRAGON_BREATH, true, true, pos.x, pos.y, pos.z,
                            (int)(spawningBucket/10), // particle amount
                            random.nextDouble()/10.0, // offsets
                            random.nextDouble()/10.0,
                            random.nextDouble()/10.0,
                            this.spawningBucket/1000.0); // speed
                }

                spawningBucket += spawningChance;

                if (spawningBucket > bucketCap) {
                    SupportZombie supportZombie = new SupportZombie(EntityType.ZOMBIE, world, wearer.getAttacking());
                    supportZombie.setPosition(wearer.getAttacking().getPos());
                    world.spawnEntity(supportZombie);
                    spawningBucket = 0;
                }

            } else {
                OpponentHealthTracker = 0;
            }

        }

    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.COMPASS;
    }

}
