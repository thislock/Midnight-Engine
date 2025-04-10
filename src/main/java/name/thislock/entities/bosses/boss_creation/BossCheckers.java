package name.thislock.entities.bosses.boss_creation;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class BossCheckers {


    public void check_all(Boss boss) {
        this.deadPlayerChecker(boss);
    }

    private int initial_spawning_delay = 0;
    private void deadPlayerChecker(Boss boss) {

        if (++initial_spawning_delay < 100)
            return;

        if (boss.getWorld() instanceof ServerWorld world) {

            boolean player_is_nearby = false;
            for (ServerPlayerEntity player : world.getPlayers()) {
                var player_pos = player.getPos();
                var this_pos = boss.getPos();

                var distance = BossMisc.distance_between(this_pos, player_pos);

                if (distance > 50.0 && player.isAlive())
                    player_is_nearby = true;
            }

            if (player_is_nearby) {

                boss.remove(Entity.RemovalReason.DISCARDED);
                boss.healthBar.delete();
            }

        }

    }

}
