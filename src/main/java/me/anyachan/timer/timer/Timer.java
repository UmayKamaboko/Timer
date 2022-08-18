package me.anyachan.timer.timer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public final class Timer extends JavaPlugin implements Listener {

    public Bar bar = new Bar(this);
    public boolean isRunning = false;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this,this);
        this.saveDefaultConfig();
        if (this.getConfig().contains("data")) {
            this.restoreCounters();
            this.getConfig().set("data", null);
            this.saveConfig();
        }
    }

    @Override
    public void onDisable() {
        if(!bar.getHashMap().isEmpty()) {
            saveCounters();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (label.equalsIgnoreCase("tstart")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cUsage: /tstart <time> <min/sec>"));
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cUsage: /tstart <time> <min/sec>"));
                return true;
            }
            if (isNum(String.valueOf(args[0]))) {
                if (args[1].equalsIgnoreCase("min") || args[1].equalsIgnoreCase("sec")) {

                    int time = Integer.parseInt(args[0]);
                    if (args[1].equalsIgnoreCase("min"))
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&bSet a timer for &6"+time+"&b minutes!"));
                    else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&bSet a timer for &6"+time+"&b seconds!"));
                    }
                    if (args[1].equalsIgnoreCase("min")) {
                        time = time*60;
                    }

                    bar.setTime(time);
                    bar.createBar();
                    bar.addPlayer(player);
                    setRunning(true);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cUsage: /tstart <time> <min/sec>"));
                    return true;
                }
            }
            else{
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6"+args[0]+" is not a number!"));
                return true;
            }

        }
        else if(label.equalsIgnoreCase("tstop")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (isRunning) {
                    setRunning(false);
                    bar.cancelTimer();
                    bar.updateMap(player);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cCancelled counters!"));

                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cThere are no timers running at this time!"));
                    return true;
                }
            }

        }
        else if (label.equalsIgnoreCase("tresume")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!isRunning) {

                    boolean done = bar.resumeTimer();
                    if (done) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cResuming counters!"));
                        setRunning(true);
                        return true;
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cThere are no stopped counters!"));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere are no stopped counters!"));
                }
            }
        }


        return false;
    }



    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!bar.getHashMap().containsKey(event.getPlayer().getUniqueId().toString())) {
            return;
        }
        if (isRunning)
            return;
        boolean done = bar.resumeTimer();
        if (done) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cResuming counters since you are back!"));
            setRunning(true);

        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (!bar.getHashMap().containsKey(event.getPlayer().getUniqueId().toString())) {
            return;
        }
        if (!isRunning)
            return;

        setRunning(false);
        bar.cancelTimer();
        bar.updateMap(event.getPlayer());

    }



    public boolean isNum(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setRunning(boolean run) {
        isRunning = run;
    }

    public void saveCounters() {
        for (Map.Entry<String, List<Float>> entry : bar.getHashMap().entrySet()) {
            this.getConfig().set("data."+entry.getKey(), entry.getValue());
        }
        this.saveConfig();
    }

    public void restoreCounters() {
        if (this.getConfig().contains("data")) {

            this.getConfig().getConfigurationSection("data").getKeys(false).forEach(key -> {
                @SuppressWarnings("unchecked")
                List<Float> datas = this.getConfig().getFloatList("data."+ key);
                bar.getHashMap().put(key,datas);
                bar.createBar();
            });


        }
    }

}

/*
bar = new Bar(this);
        bar.createBar();

        if (Bukkit.getOnlinePlayers().size() > 0) {
            for (Player on : Bukkit.getOnlinePlayers())
                bar.addPlayer(on);
        }



if (!bar.getBar().getPlayers().contains(event.getPlayer()))
            bar.addPlayer(event.getPlayer());



 */