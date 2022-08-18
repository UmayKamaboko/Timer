
package me.anyachan.timer.timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.*;

public class Bar {

    public static HashMap<String, List<Float>> taskHashMap = new HashMap<>();
    private final Timer plugin;
    public static BossBar bar;
    public static int taskID;
    public static double status = 1.0;
    public static float timeLeft;
    public Bar(Timer plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        bar.addPlayer(player);

    }

    public void setTime(int time) {
        int finalTick = time * 20;
        timeLeft = (float) (finalTick / 20);

    }

    public void setVariables(double firstVar,float secondVar) {
        status = firstVar;
        timeLeft = secondVar;
    }

    public void updateMap(Player player) {
        ArrayList<Float> list = new ArrayList<>();
        list.add(timeLeft);
        list.add((float) status);
        taskHashMap.put(player.getUniqueId().toString(),list);


    }


    public void createBar() {
        bar = Bukkit.createBossBar(format("&cCountdown"), BarColor.YELLOW, BarStyle.SEGMENTED_10);
        bar.setVisible(true);
        cast();

    }

    public void cancelTimer() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public HashMap<String, List<Float>> getHashMap() {

        return taskHashMap;
    }


    public boolean resumeTimer() {
        if (!(taskHashMap.isEmpty())) {
            for (String i : taskHashMap.keySet()) {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    bar.addPlayer(Bukkit.getPlayer(UUID.fromString(i)));
                    setVariables(taskHashMap.get(i).get(1), taskHashMap.get(i).get(0));
                    cast();
                }
            }
            return true;
        }
        return false;

    }

    public boolean resumeAfterStart() {
        if (!(taskHashMap.isEmpty())) {
            for (String i : taskHashMap.keySet()) {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    Bukkit.broadcastMessage(taskHashMap.toString());
                    bar.addPlayer(Bukkit.getPlayer(UUID.fromString(i)));
                    setVariables(taskHashMap.get(i).get(1), taskHashMap.get(i).get(0));
                    cast();
                }
            }
            return true;
        }
        return false;
    }

    public void cast() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int count = (int) timeLeft;
            double progress = status;
            final double time = 1.0/count;

            @Override
            public void run() {
                bar.setProgress(progress);

                if (count>0) {
                    count--;
                    bar.setTitle(format("&cCountdown: " + count));

                    progress = progress - time;
                    if (progress <= 0) {
                        progress = 1.0;
                    }
                    status = progress;
                    timeLeft = count;
                    updateMap(bar.getPlayers().get(0));
                }
                else {

                    plugin.setRunning(false);
                    bar.removeAll();
                    cancelTimer();
                }



            }
        },0,20);


    }
    private String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&',msg);
    }


}

