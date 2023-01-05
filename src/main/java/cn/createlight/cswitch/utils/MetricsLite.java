package cn.createlight.cswitch.utils;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.service.NKServiceManager;
import cn.nukkit.plugin.service.RegisteredServiceProvider;
import cn.nukkit.plugin.service.ServicePriority;
import cn.nukkit.utils.Config;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;


@SuppressWarnings({"WeakerAccess", "unused"})
public class MetricsLite {

    static {
        if (System.getProperty("bstats.relocatecheck") == null || !System.getProperty("bstats.relocatecheck").equals("false")) {
            final String defaultPackage = new String(new byte[]{'o', 'r', 'g', '.', 'b', 's', 't', 'a', 't', 's', '.', 'n', 'u', 'k', 'k', 'i', 't'});
            final String examplePackage = new String(new byte[]{'y', 'o', 'u', 'r', '.', 'p', 'a', 'c', 'k', 'a', 'g', 'e'});
            if (MetricsLite.class.getPackage().getName().equals(defaultPackage) || MetricsLite.class.getPackage().getName().equals(examplePackage)) {
                throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
            }
        }
    }

    public static final int B_STATS_VERSION = 1;

    private static final String URL = "https://bStats.org/submitData/bukkit";

    private boolean enabled;

    private static boolean logFailedRequests;

    private static boolean logSentData;

    private static boolean logResponseStatusText;

    private static String serverUUID;

    private final Plugin plugin;

    private final int pluginId;

    /**
     * Class constructor.
     *
     * @param plugin The plugin which stats should be submitted.
     * @param pluginId The id of the plugin.
     *                 It can be found at <a href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
     */
    public MetricsLite(Plugin plugin, int pluginId) {
        Preconditions.checkNotNull(plugin);
        this.plugin = plugin;
        this.pluginId = pluginId;

        File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        Config config = new Config(configFile);

        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) config.getAll();
        if (!config.isString("serverUuid")) {
            map.put("serverUuid", UUID.randomUUID().toString());
        } else {
            try {
                UUID.fromString(config.getString("serverUuid"));
            } catch (Exception ignored){
                map.put("serverUuid", UUID.randomUUID().toString());
            }
        }
        if (!config.isBoolean("enabled")) {
            map.put("enabled", true);
        }
        if (!config.isBoolean("logFailedRequests")) {
            map.put("logFailedRequests", false);
        }
        if (!config.isBoolean("logSentData")) {
            map.put("logSentData", false);
        }
        if (!config.isBoolean("logResponseStatusText")) {
            map.put("logResponseStatusText", false);
        }
        config.setAll(map);
        config.save();

        enabled = config.getBoolean("enabled", true);
        serverUUID = config.getString("serverUuid");
        logFailedRequests = config.getBoolean("logFailedRequests", false);
        logSentData = config.getBoolean("logSentData", false);
        logResponseStatusText = config.getBoolean("logResponseStatusText", false);

        if (enabled) {
            boolean found = false;
            for (Class<?> service : Server.getInstance().getServiceManager().getKnownService()) {
                try {
                    service.getField("B_STATS_VERSION");
                    found = true;
                    break;
                } catch (NoSuchFieldException ignored) { }
            }
            Server.getInstance().getServiceManager().register(MetricsLite.class, this, plugin, ServicePriority.NORMAL);
            if (!found) {
                startSubmitting();
            }
        }
    }


    /**
     * Checks if bStats is enabled.
     *
     * @return Whether bStats is enabled or not.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Starts the Scheduler which submits our data every 30 minutes.
     */
    private void startSubmitting() {
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    timer.cancel();
                    return;
                }
                Server.getInstance().getScheduler().scheduleTask(plugin, () -> submitData());
            }
        }, 1000 * 60 * 5, 1000 * 60 * 30);
    }

    /**
     * Gets the plugin specific data.
     * This method is called using Reflection.
     *
     * @return The plugin specific data.
     */
    public JsonObject getPluginData() {
        JsonObject data = new JsonObject();

        String pluginName = plugin.getName();
        String pluginVersion = plugin.getDescription().getVersion();

        data.addProperty("pluginName", pluginName);
        data.addProperty("id", pluginId);
        data.addProperty("pluginVersion", pluginVersion);

        JsonArray customCharts = new JsonArray();
        data.add("customCharts", customCharts);

        return data;
    }

    /**
     * Gets the server specific data.
     *
     * @return The server specific data.
     */
    private JsonObject getServerData() {
        int playerAmount = Server.getInstance().getOnlinePlayers().size();
        int onlineMode = Server.getInstance().getPropertyBoolean("xbox-auth", false) ? 1 : 0;
        String minecraftVersion = Server.getInstance().getVersion();
        String softwareName = Server.getInstance().getName();

        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();

        data.addProperty("serverUUID", serverUUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("bukkitVersion", minecraftVersion);
        data.addProperty("bukkitName", softwareName);

        data.addProperty("javaVersion", javaVersion);
        data.addProperty("osName", osName);
        data.addProperty("osArch", osArch);
        data.addProperty("osVersion", osVersion);
        data.addProperty("coreCount", coreCount);

        return data;
    }

    /**
     * Collects the data and sends it afterwards.
     */
    @SuppressWarnings("unchecked")
    private void submitData() {
        final JsonObject data = getServerData();

        JsonArray pluginData = new JsonArray();
        Server.getInstance().getServiceManager().getKnownService().forEach((service) -> {
            try {

                List<RegisteredServiceProvider<?>> providers = null;
                try {
                    Field field = Field.class.getDeclaredField("modifiers");
                    field.setAccessible(true);
                    Field handle = NKServiceManager.class.getDeclaredField("handle");
                    field.setInt(handle, handle.getModifiers() & ~Modifier.FINAL);
                    handle.setAccessible(true);
                    providers = ((Map<Class<?>, List<RegisteredServiceProvider<?>>>) handle.get((NKServiceManager) (Server.getInstance().getServiceManager()))).get(service);
                } catch(IllegalAccessException | IllegalArgumentException | SecurityException e) {
                    if (logFailedRequests) {
                        plugin.getLogger().warning("Failed to link to metrics class " + service.getName(), e);
                    }
                }

                if (providers != null) {
                    for (RegisteredServiceProvider<?> provider : providers) {
                        try {
                            Object plugin = provider.getService().getMethod("getPluginData").invoke(provider.getProvider());
                            if (plugin instanceof JsonObject) {
                                pluginData.add((JsonElement) plugin);
                            }
                        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) { }
                    }
                }
            } catch (NoSuchFieldException ignored) { }
        });

        data.add("plugins", pluginData);

        new Thread(() -> {
            try {
                sendData(plugin, data);
            } catch (Exception e) {
                if (logFailedRequests) {
                    plugin.getLogger().warning("Could not submit plugin stats of " + plugin.getName(), e);
                }
            }
        }).start();
    }

    /**
     * Sends the data to the bStats server.
     *
     * @param plugin Any plugin. It's just used to get a logger instance.
     * @param data The data to send.
     * @throws Exception If the request failed.
     */
    private static void sendData(Plugin plugin, JsonObject data) throws Exception {
        Preconditions.checkNotNull(data);
        if (Server.getInstance().isPrimaryThread()) {
            throw new IllegalAccessException("This method must not be called from the main thread!");
        }
        if (logSentData) {
            plugin.getLogger().info("Sending data to bStats: " + data);
        }
        HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

        byte[] compressedData = compress(data.toString());

        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip");
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

        connection.setDoOutput(true);
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(compressedData);
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        }

        if (logResponseStatusText) {
            plugin.getLogger().info("Sent data to bStats and received response: " + builder);
        }
    }

    /**
     * Gzips the given String.
     *
     * @param str The string to gzip.
     * @return The gzipped String.
     * @throws IOException If the compression failed.
     */
    private static byte[] compress(final String str) throws IOException {
        if (str == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
        }
        return outputStream.toByteArray();
    }

}