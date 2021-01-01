package me.shafi.autodispensor.data;

import me.shafi.autodispensor.Autodispensor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class Datamanager {
    private Autodispensor plugin;
    private FileConfiguration dataconfig = null;
    private File configFile = null;

    public Datamanager(Autodispensor plugin){
        this.plugin = plugin;
    }

    public void reloadconfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), "dispensersdata.yml");
        }
        this.dataconfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaualtstream = this.plugin.getResource("dispensersdata.yml");
        if (defaualtstream != null){
            YamlConfiguration deafaultconfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaualtstream));
            this.dataconfig.setDefaults(deafaultconfig);
        }
    }

    public FileConfiguration getConfig() {

        if (this.dataconfig == null){
            reloadconfig();
        }
        return this.dataconfig;
    }

    public void saveConfig() {
        if (this.dataconfig == null || this.configFile == null){
            return;
        }
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE , "Could not save config to " + this.configFile, e);
        }
    }
    public void saveDeafaulConfig(){
        if (this.configFile == null){
            this.configFile = new File(this.plugin.getDataFolder() , "dispensersdata.yml");
        }
        if (!this.configFile.exists()){
            this.plugin.saveResource("dispensersdata.yml" , false);
        }
    }


}
























