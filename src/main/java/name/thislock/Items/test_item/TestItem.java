package name.thislock.Items.test_item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import name.thislock.ChaosFabric;
import name.thislock.entities.bosses.ExplosionZombie;
import name.thislock.entities.bosses.boss_creation.BossInitializer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Random;

public class TestItem extends Item implements PolymerItem {

    public static final String ITEM_NAME = "test_item";
    public static final Identifier ID = Identifier.of(ChaosFabric.MOD_ID, ITEM_NAME);

    public static final Item.Settings SETTINGS = new Item.Settings()
            .maxCount(1)
            .maxDamage(200)
            .modelId(ChaosFabric.minecraftItem("diamond"))
            .component(DataComponentTypes.CUSTOM_NAME, Text.translatable("test item"));

    public TestItem(Settings settings) {
        super(settings);
    }

    Random random = new Random();

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {

        if (world instanceof ServerWorld serverWorld) {

            for (int i = 0; i < random.nextInt(20); i++) {

                serverWorld.spawnParticles(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        player.getX(), player.getY(), player.getZ(),
                        random.nextInt(3),random.nextDouble(),random.nextDouble(),random.nextDouble(),random.nextDouble()+0.5
                );

            }

            ExplosionZombie testing_boss = new ExplosionZombie(world, BossInitializer.EXPLOSIVE_ZOMBIE, player.getPos());
            serverWorld.spawnEntity(testing_boss);

//            if (serverWorld.canSpawnEntitiesAt(player.getChunkPos())) {
//
//                ItemStack trident_item = new ItemStack(Items.TRIDENT);
//
//                TridentEntity trident = new TridentEntity(world, player, trident_item);
//
//                trident.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, player.getMovementSpeed() + 2.0F, 0.0F);
//
//                serverWorld.spawnEntity(trident);
//
//            }

        } else {
            return ActionResult.FAIL;
        }

        return ActionResult.SUCCESS_SERVER;

    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.DIAMOND;
    }

}
