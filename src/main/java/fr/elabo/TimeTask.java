package fr.elabo;

import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

public class TimeTask extends TimerTask {
    private final MinecraftServer server;
    private final boolean overlay;
    private final String prefix;
    private final DateTimeFormatter formatter;
    private final DateTimeFormatter innerFormatter;
    /**
     * Store last sent time to avoid repetitions.
     */
    private String timeNotified = "";

    /**
     * @param server Used to find users to talk to.
     * @param config The module configuration object.
     */
    public TimeTask(MinecraftServer server, String format, String prefix, boolean overlay) {
        this.server = server;
        this.overlay = overlay;
        this.prefix = prefix;
        formatter = DateTimeFormatter.ofPattern(format);
        innerFormatter = DateTimeFormatter.ofPattern("HH:mm");
    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(innerFormatter);
        if ((time.endsWith("0") || time.endsWith("5")) && !timeNotified.equals(time)) {
            timeNotified = now.format(formatter);
            server.getPlayerManager().broadcast(Text.of(prefix + timeNotified), overlay);
        }
    }
}
