package me.shafi.autodispensor;

import me.shafi.autodispensor.data.Datamanager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Autodispensor extends JavaPlugin implements Listener {
    public Map<String , ItemStack[]> dispenserinv = new HashMap<String, ItemStack[]>();
    public Map<String , ArrayList<Block>> dispensers = new HashMap<String, ArrayList<Block>>();
    public Map<String , Block[]> dispenserstoarray = new HashMap<String, Block[]>();

    private ArrayList<Integer> taskids = new ArrayList<Integer>();
    private int taskid ;
    private Datamanager data;

    public ArrayList<Block> dispensersetter = new ArrayList<Block>();
    String check = "dispenserinv";
    String check2 = "dispensers";
    Random rd = new Random();
    private int delaytime;
    public ArrayList<Integer> uuids = new ArrayList<Integer>();


    @Override
    public void onEnable() {
        System.out.println("Plugin has started");
        this.data = new Datamanager(this);
        this.getServer().getPluginManager().registerEvents(this , this);
        this.saveDefaultConfig();
        data.saveDeafaulConfig();


        if (this.getConfig().contains("dispinvdata")){
            this.restoreDispinv();
        }
        if (data.getConfig().contains("dispenserdata")){
            this.restoreDispenser();
        }

    }
    @Override
    public void onDisable() {
        if (!dispenserinv.isEmpty()){
            this.saveDispinv();
        }
        if (!dispensers.isEmpty()){
            this.saveDispenser();
        }
    }
    public void saveDispinv(){

        for (Map.Entry<String , ItemStack[]> entry : dispenserinv.entrySet()){
            this.getConfig().set("dispinvdata." + entry.getKey() , entry.getValue());
        }
        this.saveConfig();
    }

    public void restoreDispinv(){
        this.getConfig().getConfigurationSection("dispinvdata").getKeys(false).forEach(key ->{
            ItemStack[] content = ((List<ItemStack>) this.getConfig().get("dispinvdata." + key)).toArray(new ItemStack[0]);
            dispenserinv.put(key , content);
        });
    }
    public void saveDispenser(){
        dispenserstoarray.put(check2 , dispensers.get(check2).toArray(new Block[1]));

        for (Map.Entry<String , Block[]> entry : dispenserstoarray.entrySet()){
            data.getConfig().set("dispenserdata." + entry.getKey() , entry.getValue());
        }
        data.saveConfig();
    }

    public void restoreDispenser(){
        data.getConfig().getConfigurationSection("dispenserdata").getKeys(false).forEach(key ->{

          //  ArrayList<Dispenser> content = ((ArrayList<Dispenser>) data.getConfig().get("dispenserdata." + key));


                dispensers.put(key , (ArrayList<Block>) data.getConfig().get("dispenserdata." + key));

        });
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("ad.commands")){
        if (label.equals("ad")) {



            if (!(sender instanceof Player)) {

                System.out.println(ChatColor.RED + "Sorry, this command can only execute by player");
            } else {
                Player player = (Player) sender;
                if (args.length == 1){





                Block block = player.getTargetBlock(null ,6);
                if (args[0].equals("setinv")) {
                    Inventory inv = Bukkit.createInventory(player, 9, "auto dispenser inventory");

                    if (dispenserinv.containsKey(check)) {

                        inv.setContents(dispenserinv.get(check));
                    }
                    player.openInventory(inv);
                }

                if (args[0].equals("set")) {


                    if (!(block.getType() == Material.AIR)) {
                        block.setType(Material.DISPENSER);
                        BlockData blockData = block.getBlockData();
                        if (blockData instanceof Directional) {


                            Directional directional = (Directional) blockData;
                            directional.setFacing(BlockFace.UP);
                            block.setBlockData(blockData);
                            InventoryHolder inevntory = (InventoryHolder) block.getState();
                            infarrow(inevntory, player, block);

                            dispensersetter.add(block);
                            dispensers.put(check2 , dispensersetter);
                            player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "Dispenser is set successfully");


                        } else {
                        }
                    } else {
                        player.sendMessage(ChatColor.GOLD + "[AutoDispenser] " + ChatColor.RED + "look at the block not air");
                    }

                } else if (args[0].equals("go")) {
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "Launching");
                    if (dispensers.isEmpty()){
                        player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "could not find any dispenser");
                    }else {


                        for (int i = 0; i < dispensers.get(check2).size(); i++) {

                            Dispenser d = (Dispenser) dispensers.get(check2).get(i).getState();
                            disptrigger(d);
                        }
                    }

                    return true;
                } else if (args[0].equals("stop")){

                        if (!taskids.isEmpty()){
                            player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "Stopped");
                           disptrigger2();

                    }else {
                            player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "pls do /ad go to execute this");
                        }

                }else if (args[0].equals("help")){
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad setinv : to set dispenser inventory" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad set : set dispenser where you looking at" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad go : to dispense " );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad stop : to stop dispensing" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad help : to get help" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad settime (time in ticks): to set delay time of launch");

                }else {
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad setinv : to set dispenser inventory" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad set : set dispenser where you looking at" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad go : to dispense " );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad stop : to stop dispensing" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad help : to get help" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad settime (time in ticks): to set delay time of launch");
                }
            }
                else if (args.length == 2){
                    if (args[0].equals("settime")){
                        delaytime = Integer.valueOf(args[1]);
                        player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "time set to: " + delaytime);
                    }
                }
                else {
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad setinv : to set dispenser inventory" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad set : set dispenser where you looking at" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad go : to dispense " );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad stop : to stop dispensing" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad help : to get help" );
                    player.sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "do /ad settime (time in ticks): to set delay time of launch");
                }
        }
        }
        }
        return false;

    }


    public void infarrow(InventoryHolder dispenser, Player p , Block block) {


        taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {



                if (dispenser.getInventory().isEmpty()) {
                  dispenser.getInventory().setContents(dispenserinv.get(check));

                }


            }


        }, 0, 10);

    }

    public void disptrigger(Dispenser d) {



        taskids.add(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {



                d.dispense();

            }
        }, 0, delaytime));

    }
    public void disptrigger2(){
        for (int i = 0; i < taskids.size(); i++){
            Bukkit.getServer().getScheduler().cancelTask(taskids.get(i));
        }
    }
    @EventHandler
    public void onGuiClose(InventoryCloseEvent e){

        if (e.getView().getTitle().equals("auto dispenser inventory")){
            e.getPlayer().sendMessage(ChatColor.GOLD + "[Auto Dispenser] " + ChatColor.GREEN + "inventory saved successfully");
            dispenserinv.put(check , e.getInventory().getContents());
        }
    }
    @EventHandler
    public void onBlockbreaks(BlockBreakEvent event){
        Block block = event.getBlock();
        try {
            Dispenser d = (Dispenser) block.getState();
            if(dispensers.get(check2).contains(d)){
                dispensers.get(check2).remove(d);
            }
        }
        catch (ClassCastException e){
            return ;
        }


    }


}