package name.thislock.Items.weapons;

import com.mojang.datafixers.schemas.Schema;
import eu.pb4.polymer.core.api.item.PolymerItem;
import name.thislock.ChaosFabric;
import name.thislock.entities.ExplosiveArrow;
import name.thislock.entities.KnockbackArrow;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.datafixer.fix.ItemCustomNameToComponentFix;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AirGlove extends Item implements PolymerItem {

    public static final String ITEM_NAME = "air_launcher";
    public static final Identifier ID = Identifier.of(ChaosFabric.MOD_ID, ITEM_NAME);

    public static final String IN_GAME_NAME = "Air Launcher";

    public static final Item.Settings SETTINGS = new Item.Settings()
            .maxCount(99)
            .modelId(ChaosFabric.minecraftItem("crossbow"));

    public AirGlove(Settings settings) {
        super(settings);
    }

    @Override
    public void modifyClientTooltip(List<Text> tooltip, ItemStack stack, PacketContext context) {
        stack.set(DataComponentTypes.ITEM_NAME, Text.translatable(IN_GAME_NAME));
        tooltip.add(Text.of("THIS IS NOT A TEA PARTY,"));
        tooltip.add(Text.of("SARGENT TWINKLE TOES"));
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {

        if (world instanceof ServerWorld serverWorld) {

            player.limbAnimator.reset();

            ItemStack arrow_item = new ItemStack(Items.ARROW);
            KnockbackArrow arrow = new KnockbackArrow(world, player, arrow_item, arrow_item);

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
