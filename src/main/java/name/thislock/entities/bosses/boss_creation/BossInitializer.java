package name.thislock.entities.bosses.boss_creation;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import name.thislock.ChaosFabric;
import name.thislock.entities.bosses.ExplosionZombie;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BossInitializer {

    public static <T extends Entity> EntityType<T> register_entity(String path, EntityType.Builder<T> item) {
        var id = Identifier.of(ChaosFabric.MOD_ID, path);
        var x = Registry.register(Registries.ENTITY_TYPE, id, item.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)));
        if (x == null) {
            ChaosFabric.LOGGER.error("failed to initialise entity at '{}'", path);
        }
        PolymerEntityUtils.registerType(x);
        return x;
    }

    public static EntityType.Builder<Boss> get_boss_builder(EntityType.EntityFactory<Boss> factory) {
        return EntityType.Builder.<Boss>create(
                factory,
                SpawnGroup.MISC
        );
    }

    public static EntityType.Builder<Boss> average_boss(EntityType.EntityFactory<Boss> factory) {
        return get_boss_builder(factory)
                .dimensions(0.9F, 2.7F)
                .eyeHeight(2.3F)
                .maxTrackingRange(20)
                .trackingTickInterval(1);
    }

    public static <T extends Entity> EntityType<Boss> register_boss(String path, EntityType.EntityFactory<Boss> factory) {
        final EntityType<Boss> boss = register_entity(path, average_boss(factory));
        FabricDefaultAttributeRegistry.register(boss, ExplosionZombie.createExplosionZombieAttributes());
        return boss;
    }

    public static final String EXPLOSIVE_ZOMBIE_PATH = "explosive_zombie_boss";
    public static final EntityType<Boss> EXPLOSIVE_ZOMBIE = register_boss(EXPLOSIVE_ZOMBIE_PATH, ExplosionZombie::new);

    public static void initialize() {
    }

}
