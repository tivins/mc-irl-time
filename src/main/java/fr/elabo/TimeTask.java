package fr.elabo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

public class TimeTask extends TimerTask {
    private final MinecraftServer server;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); // :ss
    private String timeNotified = "";

    public TimeTask(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(formatter);
        if ((time.endsWith("0") || time.endsWith("5")) && !timeNotified.equals(time)) {
            timeNotified = time;
            server.getPlayerManager().broadcast(Text.literal("Il est " + now.format(formatter)), false);
        }
    }
}
