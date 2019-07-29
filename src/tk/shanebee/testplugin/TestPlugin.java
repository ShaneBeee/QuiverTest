package tk.shanebee.testplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tk.shanebee.testplugin.commands.TestCommand;
import tk.shanebee.testplugin.listeners.Quiver;

import java.io.File;

@SuppressWarnings("unused")
public class TestPlugin extends JavaPlugin {

    public static TestPlugin plugin;
    private FileConfiguration quiverData;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        plugin = this;
        loadQuiverData();
        getCommand("test").setExecutor(new TestCommand());
        getServer().getPluginManager().registerEvents(new Quiver(), this);
    }

    @Override
    public void onDisable() {
    }

    private void loadQuiverData() {
        File quiver_file = new File(this.getDataFolder(), "data.yml");
        if (!quiver_file.exists()) {
            saveResource("data.yml", true);
        }
        quiverData = YamlConfiguration.loadConfiguration(quiver_file);
    }

    public FileConfiguration getQuiverData() {
        return this.quiverData;
    }

}
