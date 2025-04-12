package loch.midnight.entities.bosses.boss_creation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class BossHealthBar {

    Boss boss;
    DisplayEntity.TextDisplayEntity text_display;

    private final int health_bar_size;

    public boolean isValid() {
        return this.text_display != null;
    }

    public BossHealthBar(Boss boss, int health_bar_size) {

        if (boss.getFirstPassenger() != null)
            boss.getFirstPassenger().remove(Entity.RemovalReason.DISCARDED);
        if (boss.healthBar != null)
            if (boss.healthBar.isValid())
                boss.healthBar.delete();

        this.text_display = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, boss.getWorld());

        text_display.setPosition(boss.getPos());

        text_display.setBillboardMode(DisplayEntity.BillboardMode.VERTICAL);

        this.boss = boss;

        this.health_bar_size = health_bar_size;

        text_display.setText(Text.of("CREATING..."));
        boss.boss_info("created new health bar for boss");

        boss.getWorld().spawnEntity(text_display);
    }

    private String generate_health_bar() {
        String generated_bar = "";

        final double boss_health = this.boss.getHealth();
        final double max_boss_health = this.boss.getMaxHealth();

        final double health_ratio = boss_health / max_boss_health;
        for (int i = 0; i < this.health_bar_size; i++) {
            final double text_ratio = (double) i / (double) this.health_bar_size;
            if (text_ratio < health_ratio)
                generated_bar = generated_bar.concat("|");
            else
                generated_bar = generated_bar.concat(".");
        }

        return generated_bar;
    }

    private String get_health_bar_string() {
        var final_string = boss.boss_name;
        final_string = final_string + "\n" + this.generate_health_bar();
        return final_string;
    }

    double boss_health_history = 0.0;

    public void tick() {

        // checks to see if the boss was damaged, and if they were, update the health bar.
        if (boss_health_history != this.boss.getHealth()) {
            text_display.setText(Text.literal(this.get_health_bar_string()).withColor(Colors.YELLOW));
            if (!text_display.hasVehicle())
                text_display.startRiding(boss);

            boss_health_history = boss.getHealth();
        }
    }

    public void delete() {
        text_display.remove(Entity.RemovalReason.DISCARDED);
    }

}
