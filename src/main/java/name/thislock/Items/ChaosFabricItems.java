package name.thislock.Items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import name.thislock.ChaosFabric;
import name.thislock.Items.armor.ZombieKingCrown;
import name.thislock.Items.weapons.AirGlove;
import name.thislock.Items.weapons.DynamiteGlove;
import name.thislock.Items.test_item.TestItem;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ChaosFabricItems {

    public static Item register(Identifier id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        var item = Items.register(key, factory, settings);
        ChaosFabric.LOGGER.info("initialized the item: {}", id.toString());

        return item;
    }

    // items
    public static final TestItem TEST_ITEM = (TestItem) register(TestItem.ID, TestItem::new, TestItem.SETTINGS);

    public static final DynamiteGlove DYNAMITE_GLOVE = (DynamiteGlove) register(DynamiteGlove.ID, DynamiteGlove::new, DynamiteGlove.SETTINGS);
    public static final AirGlove AIR_GLOVE = (AirGlove) register(AirGlove.ID, AirGlove::new, AirGlove.SETTINGS);

    public static final ZombieKingCrown ZOMBIE_KING_CROWN = (ZombieKingCrown) register(ZombieKingCrown.ID, ZombieKingCrown::new, ZombieKingCrown.SETTINGS);

    public static void initialize() {

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(ChaosFabric.MOD_ID, "item_group"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(() -> new ItemStack(Items.OBSIDIAN))
                .displayName(Text.of("chaos fabric"))
                .entries(((context, entries) -> {

                    entries.add(TEST_ITEM);
                    entries.add(DYNAMITE_GLOVE);
                    entries.add(AIR_GLOVE);
                    entries.add(ZOMBIE_KING_CROWN);

                })).build()
        );

        ChaosFabric.LOGGER.info("********************************************");
        ChaosFabric.LOGGER.info("finished initializing custom chaos mod items");
        ChaosFabric.LOGGER.info("********************************************");

    }

}
