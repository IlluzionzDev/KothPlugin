package me.jamin.koth.scheduler.bukkit;

import lombok.RequiredArgsConstructor;
import me.jamin.koth.scheduler.MinecraftScheduler;
import me.jamin.koth.scheduler.rate.Async;
import me.jamin.koth.scheduler.rate.Sync;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

/**
 * An instance of a {@link MinecraftScheduler} that handles ticking objects
 * Simply implements our methods that provide functionality
 */
@RequiredArgsConstructor
public class BukkitScheduler extends MinecraftScheduler {

    /**
     * The {@link Plugin} we invoke services for
     */
    private final Plugin plugin;

    /**
     * Ids of the schedulers
     */
    private int SYNC_SCHEDULER = -1, ASYNC_SCHEDULER = -1;

    @Override
    public void start() {
        // The Bukkit SYNC scheduler thread
        SYNC_SCHEDULER = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> heartbeat(Sync.class), 0L, 0L);

        // The Bukkit ASYNC scheduler
        ASYNC_SCHEDULER = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, () -> heartbeat(Async.class), 0L, 0L);
    }

    @Override
    public void stop() {
        // Stop invocation
        stopTask(SYNC_SCHEDULER);
        stopTask(ASYNC_SCHEDULER);
    }

    @Override
    public void stopTask(int id) {
        plugin.getServer().getScheduler().cancelTask(id);
    }

    @Override
    public void validateMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("This method must be called on main server thread");
        }
    }

    @Override
    public void validateNotMainThread() {
        if (Bukkit.isPrimaryThread()) {
            throw new RuntimeException("This method must not be called on the main server thread");
        }
    }

    @Override
    public int synchronize(Runnable runnable, long time) {
        return plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, time);
    }

    @Override
    public int desynchronize(Runnable runnable, long time) {
        return plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, runnable, time);
    }

    @Override
    public <T> void desynchronize(Callable<T> callable, Consumer<Future<T>> consumer) {
        // FUTURE TASK //
        FutureTask<T> task = new FutureTask<>(callable);

        // BUKKIT'S ASYNC SCHEDULE WORKER
        new BukkitRunnable() {
            @Override
            public void run() {
                // RUN FUTURE TASK ON THREAD //
                task.run();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // ACCEPT CONSUMER //
                        if (consumer != null) {
                            consumer.accept(task);
                        }
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

}
