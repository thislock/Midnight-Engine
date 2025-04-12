package loch.midnight.Items.weapons;

import eu.pb4.polymer.core.api.item.PolymerItem;
import loch.midnight.MidnightEngine;
import loch.midnight.entities.ExplosiveArrow;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class DynamiteGlove extends Item implements PolymerItem {

    public static final String ITEM_NAME = "dynamite_launcher";
    public static final Identifier ID = Identifier.of(MidnightEngine.MOD_ID, ITEM_NAME);

    public static final String IN_GAME_NAME = "Dynamite Launcher";

    public static final Settings SETTINGS = new Item.Settings()
            .maxCount(99)
            .modelId(MidnightEngine.minecraftItem("crossbow"))
            .component(DataComponentTypes.CUSTOM_NAME, Text.translatable(IN_GAME_NAME));

    public DynamiteGlove(Settings settings) {
        super(settings);
    }

    @Override
    public void modifyClientTooltip(List<Text> tooltip, ItemStack stack, PacketContext context) {
        stack.set(DataComponentTypes.ITEM_NAME, Text.translatable(IN_GAME_NAME));
        tooltip.add(Text.of("AMERICA, MAGGOTS"));
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {

        if (world instanceof ServerWorld serverWorld) {

            player.limbAnimator.reset();

            ItemStack arrow_item = new ItemStack(Items.ARROW);
            ExplosiveArrow arrow = new ExplosiveArrow(world, player, arrow_item, arrow_item);

            arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, player.getMovementSpeed() + 2.0F, 0.0F);

            serverWorld.spawnEntity(arrow);
            player.getActiveItem().damage(2, player);

            if (!player.isCreative())
                player.getStackInHand(hand).decrement(1);
        }

        return ActionResult.SUCCESS_SERVER;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.COMPASS;
    }
}
