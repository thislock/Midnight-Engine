package name.thislock.entities.bosses.boss_creation;

import name.thislock.ChaosFabric;
import name.thislock.entities.bosses.goals.BossFleeGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BossPhases {

    private final long random_offset_by;
    private long move_switch_frequency;

    public BossPhases(long move_switch_frequency, long random_offset_by) {
        this.move_switch_frequency = move_switch_frequency;
        this.random_offset_by = random_offset_by;
    }

    public void setMoveSwitchFrequency(long val) {
        this.move_switch_frequency = val;
    }

    public void set_attacks(List<Goal> attacks) {
        attack_cycles = attacks;
    }

    // phases
    List<Goal> attack_cycles = new ArrayList<>();
    Goal current_goal = null;
    int goal_index = 0;

    private List<List<Goal>> phases = new ArrayList<>();
    private List<List<Goal>> passed_phases = new ArrayList<>();

    public void add_phase(List<Goal> new_phase) {
        this.phases.add(new_phase);
    }

    private List<Integer> phase_change_ids = new ArrayList<>();
    // for when a condition is met, and the phase is changed, but with extra logic to ensure that specific call isn't repeated
    public void checked_next_phase(Boss boss, int unique_phase_change_id) {
        if (!phase_change_ids.contains(unique_phase_change_id)) {
            phase_change_ids.add(unique_phase_change_id);
            next_phase(boss);
        }
    }

    public void next_phase(Boss boss) {

        if (phases.isEmpty()) {
            boss.boss_info("attempted to change phases when there were no phases left");
            return;
        }

        this.attack_cycles = phases.getFirst();
        passed_phases.add(phases.getFirst());
        phases.removeFirst();

    }

    public void revert_phase(Boss boss) {

        if (passed_phases.isEmpty()) {
            boss.boss_info("attempted to go back a phase, but no phases have passed");
            return;
        }

        this.attack_cycles = passed_phases.getLast();
        phases.addFirst(passed_phases.getLast());
        passed_phases.removeLast();

    }

    private void add_goal(Boss boss) {
        if (current_goal == null) {
            current_goal = attack_cycles.getFirst();
            boss.addGoal(1, current_goal);
        }
        else if (!boss.getGoalSelector().getGoals().contains(current_goal))
            boss.addGoal(1, current_goal);
    }

    public void cycle_goal(Boss boss) {

        if (this.attack_cycles.isEmpty()) {
            ChaosFabric.LOGGER.error("boss initialised without any attack patterns");
            return;
        } else {
            goal_index++;
            if ( goal_index >= attack_cycles.size() )
                goal_index = 0;

            boss.removeGoal(current_goal);
            current_goal = attack_cycles.get(goal_index);
            this.add_goal(boss);
        }

    }

    public void checkers(Boss boss, long ticks_alive) {
        this.running_checker(boss);
        this.check_phase_change(boss, ticks_alive);
    }

    private double random_offset = 0.0;
    private Random random = new Random();

    public void check_phase_change(Boss boss, long ticks_alive) {
        if ( (ticks_alive % (move_switch_frequency+random_offset)) == 0 ) {
            this.cycle_goal(boss);
            boss.boss_info("switched boss move");
            random_offset = this.random.nextLong(-random_offset_by, random_offset_by);
        }
    }

    private boolean running_away = false;
    private int running_comfort_distance = 0;
    private Goal running_away_goal_saver = null;
    private double running_away_speed = 0.0;
    private final int running_away_delay_length = 10;
    private int running_away_ending_delay = 0;
    public void run_away(int comfort_distance, double running_speed) {
        this.running_away = true;
        this.running_comfort_distance = comfort_distance;
        this.running_away_speed = running_speed;
        this.running_away_ending_delay = 0;
        this.running_away_goal_saver = null;
    }

    private void swapRunningGoals(Boss boss) {
        if (running_away_goal_saver == null) {
            this.running_away_goal_saver = this.current_goal;
            this.current_goal = new BossFleeGoal<>(
                    boss,
                    PlayerEntity.class,
                    this.running_comfort_distance,
                    this.running_away_speed, // slowest speed it can run
                    this.running_away_speed  // fastest speed it can run
            );
        } else {
            this.current_goal = this.running_away_goal_saver;
            this.running_away_goal_saver = null;
        }
    }

    private void running_checker(Boss boss) {
        if (this.running_away && boss.getTarget() != null) {
            var target_pos = boss.getTarget().getPos();
            var this_pos = boss.getPos();

            if (this_pos == null || target_pos == null)
                return;

            if (BossMisc.distance_between(target_pos, this_pos) < this.running_comfort_distance) {

                if (running_away_goal_saver == null) {
                    this.swapRunningGoals(boss);
                }

                if (target_pos.distanceTo(this_pos) > this.running_comfort_distance) {
                    this.running_away_ending_delay++;
                    if (this.running_away_ending_delay > this.running_away_delay_length) {
                        this.swapRunningGoals(boss);
                        this.running_away = false;
                    }
                }

            } else {
                this.running_away = false;
            }
        }
    }
}
