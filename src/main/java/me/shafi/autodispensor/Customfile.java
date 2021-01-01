package me.shafi.autodispensor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Customfile {
    private static File file;
    private static FileConfiguration customFile;

    public static void setup(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("autodispensor").getDataFolder() , "dispensersdata.yml");

        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){

            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }
    public static FileConfiguration get(){
        return customFile;
    }
    public static void save(){
        try{
            customFile.save(file);
        }catch (IOException e){
            System.out.println("could not save file");
        }

    }
    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
