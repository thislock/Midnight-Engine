package loch.midnight.entities.bosses.goals;

import loch.midnight.MidnightEngine;
import loch.midnight.entities.bosses.boss_creation.Boss;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;

public class BossSummonGoal extends Goal {

    Boss boss;
    EntityType.EntityFactory<Entity> summoned_entity_factory;
    final int amount_summoned;

    public BossSummonGoal(Boss boss, EntityType.EntityFactory<Entity> summoned_entity_factory, int amount_summoned) {
        this.boss = boss;
        this.summoned_entity_factory = summoned_entity_factory;
        this.amount_summoned = amount_summoned;
    }

    @Override
    public boolean canStart() {
        return boss.getTarget() != null;
    }

    int summoning_counter = 1;

    final int summoning_delay = 10; // in ticks
    int summoning_timer = 1;

    public void start() {
        MidnightEngine.LOGGER.info("started zombie process");
        this.summoning_counter = 1;
        this.summoning_timer = 1;
    }

    private void summon_entity() {
        var entity = this.summoned_entity_factory.create(null, boss.getWorld());
        boss.getWorld().spawnEntity(entity);
        entity.setPosition(boss.getPos());
    }

    public void tick() {

        summoning_timer++;

        if (summoning_timer / summoning_counter > summoning_delay && summoning_counter > amount_summoned) {
            MidnightEngine.LOGGER.info("summoning new zombie");
            summoning_counter++;
            this.summon_entity();
        }

        if (summoning_counter > amount_summoned) {
            MidnightEngine.LOGGER.info("stopped summoning");
        }

    }

}
