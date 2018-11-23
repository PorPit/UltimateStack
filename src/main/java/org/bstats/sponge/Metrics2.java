package org.bstats.sponge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPOutputStream;

/**
 * bStats collects some data for plugin authors.
 * <p>
 * Check out https://bStats.org/ to learn more about bStats!
 * <p>
 * DO NOT modify any of this class. Access it from your own plugin ONLY.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Metrics2 implements Metrics {
    /**
     * Internal class for storing information about old bStats instances.
     */
    private static class OutdatedInstance implements Metrics {
        private Object instance;
        private Method method;
        private PluginContainer plugin;

        private OutdatedInstance(Object instance, Method method, PluginContainer plugin) {
            this.instance = instance;
            this.method = method;
            this.plugin = plugin;
        }

        @Override
        public void cancel() {
            // Do nothing, handled once elsewhere
        }

        @Override
        public List<Metrics> getKnownMetricsInstances() {
            return new ArrayList<>();
        }

        @Override
        public JsonObject getPluginData() {
            try {
                return (JsonObject) method.invoke(instance);
            } catch (ClassCastException | IllegalAccessException | InvocationTargetException ignored) { }
            return null;
        }

        @Override
        public PluginContainer getPluginContainer() {
            return plugin;
        }

        @Override
        public int getRevision() {
            return 0;
        }

        @Override
        public void linkMetrics(Metrics metrics) {
            // Do nothing
        }
    }

    static {
        // Do not touch. Needs to always be in this class.
        final String defaultName = "org:bstats:sponge:Metrics".replace(":", ".");
        if (!Metrics.class.getName().equals(defaultName)) {
            throw new IllegalStateException("bStats Metrics interface has been relocated or renamed and will not be run!");
        }
        if (!Metrics2.class.getName().equals(defaultName + "2")) {
            throw new IllegalStateException("bStats Metrics2 class has been relocated or renamed and will not be run!");
        }
    }

    // The version of bStats info being sent
    public static final int B_STATS_VERSION = 1;

    // The version of this bStats class
    public static final int B_STATS_CLASS_REVISION = 2;

    // The url to which the data is sent
    private static final String URL = "https://bStats.org/submitData/sponge";

    // The logger
    private Logger logger;

    // The plugin
    private final PluginContainer plugin;

    // The uuid of the server
    private String serverUUID;

    // Should failed requests be logged?
    private boolean logFailedRequests = false;

    // Should the sent data be logged?
    private static boolean logSentData;

    // Should the response text be logged?
    private static boolean logResponseStatusText;

    // A list with all known metrics class objects including this one
    private final List<Metrics> knownMetricsInstances = new CopyOnWriteArrayList<>();

    // A list with all custom charts
    private final List<CustomChart> charts = new ArrayList<>();

    // The config path
    private Path configDir;

    // The list of instances from the bStats 1 instance's that started first
    private List<Object> oldInstances = new ArrayList<>();

    // The timer task
    private TimerTask timerTask;

    // The constructor is not meant to be called by the user.
    // The instance is created using Dependency Injection (https://docs.spongepowered.org/master/en/plugin/injection.html)
    @Inject
    private Metrics2(PluginContainer plugin, Logger logger, @ConfigDir(sharedRoot = true) Path configDir) {
        this.plugin = plugin;
        this.logger = logger;
        this.configDir = configDir;

        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Listener
    public void startup(GamePreInitializationEvent event) {
        try {
            loadConfig();
        } catch (IOException e) {
            // Failed to load configuration
            logger.warn("Failed to load bStats config!", e);
            return;
        }

        if (Sponge.getServiceManager().isRegistered(Metrics.class)) {
            Metrics provider = Sponge.getServiceManager().provideUnchecked(Metrics.class);
            provider.linkMetrics(this);
        } else {
            Sponge.getServiceManager().setProvider(plugin.getInstance().get(), Metrics.class, this);
            this.linkMetrics(this);
            startSubmitting();
        }
    }

    @Override
    public void cancel() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    @Override
    public List<Metrics> getKnownMetricsInstances() {
        return knownMetricsInstances;
    }

    @Override
    public PluginContainer getPluginContainer() {
        return plugin;
    }

    @Override
    public int getRevision() {
        return B_STATS_CLASS_REVISION;
    }

    /**
     * Links a bStats 1 metrics class with this instance.
     *
     * @param metrics An object of the metrics class to link.
     */
    private void linkOldMetrics(Object metrics) {
        try {
            Field field = metrics.getClass().getDeclaredField("plugin");
            field.setAccessible(true);
            PluginContainer plugin = (PluginContainer) field.get(metrics);
            Method method = metrics.getClass().getMethod("getPluginData");
            linkMetrics(new OutdatedInstance(metrics, method, plugin));
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            // Move on, this bStats is broken
        }
    }

    /**
     * Links an other metrics class with this class.
     * This method is called using Reflection.
     *
     * @param metrics An object of the metrics class to link.
     */
    @Override
    public void linkMetrics(Metrics metrics) {
        knownMetricsInstances.add(metrics);
    }

    /**
     * Adds a custom chart.
     *
     * @param chart The chart to add.
     */
    public void addCustomChart(CustomChart chart) {
        Validate.notNull(chart, "Chart cannot be null");
        charts.add(chart);
    }

    @Override
    public JsonObject getPluginData() {
        JsonObject data = new JsonObject();

        String pluginName = plugin.getName();
        String pluginVersion = plugin.getVersion().orElse("unknown");
        int revision = getRevision();

        data.addProperty("pluginName", pluginName);
        data.addProperty("pluginVersion", pluginVersion);
        data.addProperty("metricsRevision", revision);

        JsonArray customCharts = new JsonArray();
        for (CustomChart customChart : charts) {
            // Add the data of the custom charts
            JsonObject chart = customChart.getRequestJsonObject(logger, logFailedRequests);
            if (chart == null) { // If the chart is null, we skip it
                continue;
            }
            customCharts.add(chart);
        }
        data.add("customCharts", customCharts);

        return data;
    }

    private void startSubmitting() {
        // bStats 1 cleanup. Runs once.
        try {
            Path configPath = configDir.resolve("bStats");
            configPath.toFile().mkdirs();
            String className = readFile(new File(configPath.toFile(), "temp.txt"));
            if (className != null) {
                try {
                    // Let's check if a class with the given name exists.
                    Class<?> clazz = Class.forName(className);

                    // Time to eat it up!
                    Field instancesField = clazz.getDeclaredField("knownMetricsInstances");
                    instancesField.setAccessible(true);
                    oldInstances = (List<Object>) instancesField.get(null);
                    for (Object instance : oldInstances) {
                        linkOldMetrics(instance); // Om nom nom
                    }
                    oldInstances.clear(); // Look at me. I'm the bStats now.

                    // Cancel its timer task
                    // bStats for Sponge version 1 did not expose its timer task - gotta go find it!
                    Map<Thread, StackTraceElement[]> threadSet = Thread.getAllStackTraces();
                    for (Map.Entry<Thread, StackTraceElement[]> entry : threadSet.entrySet()) {
                        try {
                            if (entry.getKey().getName().startsWith("Timer")) {
                                Field timerThreadField = entry.getKey().getClass().getDeclaredField("queue");
                                timerThreadField.setAccessible(true);
                                Object taskQueue = timerThreadField.get(entry.getKey());

                                Field taskQueueField = taskQueue.getClass().getDeclaredField("queue");
                                taskQueueField.setAccessible(true);
                                Object[] tasks = (Object[]) taskQueueField.get(taskQueue);
                                for (Object task : tasks) {
                                    if (task == null) {
                                        continue;
                                    }
                                    if (task.getClass().getName().startsWith(clazz.getName())) {
                                        ((TimerTask) task).cancel();
                                    }
                                }
                            }
                        } catch (Exception ignored) { }
                    }
                } catch (ReflectiveOperationException ignored) { }
            }
        } catch (IOException ignored) { }

        // We use a timer cause want to be independent from the server tps
        final Timer timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // Catch any stragglers from inexplicable post-server-load plugin loading of outdated bStats
                for (Object instance : oldInstances) {
                    linkOldMetrics(instance); // Om nom nom
                }
                oldInstances.clear(); // Look at me. I'm the bStats now.
                // The data collection (e.g. for custom graphs) is done sync
                // Don't be afraid! The connection to the bStats server is still async, only the stats collection is sync ;)
                Scheduler scheduler = Sponge.getScheduler();
                Task.Builder taskBuilder = scheduler.createTaskBuilder();
                taskBuilder.execute(() -> submitData()).submit(plugin);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000 * 60 * 5, 1000 * 60 * 30);
        // Submit the data every 30 minutes, first time after 5 minutes to give other plugins enough time to start
        // WARNING: Changing the frequency has no effect but your plugin WILL be blocked/deleted!
        // WARNING: Just don't do it!

        // Let's log if things are enabled or not, once at startup:
        List<String> enabled = new ArrayList<>();
        List<String> disabled = new ArrayList<>();
        for (Metrics metrics : knownMetricsInstances) {
            if (Sponge.getMetricsConfigManager().areMetricsEnabled(metrics.getPluginContainer())) {
                enabled.add(metrics.getPluginContainer().getName());
            } else {
                disabled.add(metrics.getPluginContainer().getName());
            }
        }
        StringBuilder builder = new StringBuilder().append(System.lineSeparator());
        builder.append("bStats metrics is present in ").append((enabled.size() + disabled.size())).append(" plugins on this server.");
        builder.append(System.lineSeparator());
        if (enabled.isEmpty()) {
            builder.append("Presently, none of them are allowed to send data.").append(System.lineSeparator());
        } else {
            builder.append("Presently, the following ").append(enabled.size()).append(" plugins are allowed to send data:").append(System.lineSeparator());
            builder.append(enabled.toString()).append(System.lineSeparator());
        }
        if (disabled.isEmpty()) {
            builder.append("None of them have data sending disabled.");
            builder.append(System.lineSeparator());
        } else {
            builder.append("Presently, the following ").append(disabled.size()).append(" plugins are not allowed to send data:").append(System.lineSeparator());
            builder.append(disabled.toString()).append(System.lineSeparator());
        }
        builder.append("To change the enabled/disabled state of any bStats use in a plugin, visit the Sponge config!");
        logger.info(builder.toString());
    }

    /**
     * Gets the server specific data.
     *
     * @return The server specific data.
     */
    private JsonObject getServerData() {
        // Minecraft specific data
        int playerAmount = Sponge.getServer().getOnlinePlayers().size();
        playerAmount = playerAmount > 200 ? 200 : playerAmount;
        int onlineMode = Sponge.getServer().getOnlineMode() ? 1 : 0;
        String minecraftVersion = Sponge.getGame().getPlatform().getMinecraftVersion().getName();
        String spongeImplementation = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName();

        // OS/Java specific data
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();

        data.addProperty("serverUUID", serverUUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("minecraftVersion", minecraftVersion);
        data.addProperty("spongeImplementation", spongeImplementation);

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
    private void submitData() {
        final JsonObject data = getServerData();

        JsonArray pluginData = new JsonArray();
        // Search for all other bStats Metrics classes to get their plugin data
        for (Metrics metrics : knownMetricsInstances) {
            if (!Sponge.getMetricsConfigManager().areMetricsEnabled(metrics.getPluginContainer())) {
                continue;
            }
            JsonObject plugin = metrics.getPluginData();
            if (plugin != null) {
                pluginData.add(plugin);
            }
        }

        if (pluginData.size() == 0) {
            return; // All plugins disabled, so we don't send anything
        }

        data.add("plugins", pluginData);

        // Create a new thread for the connection to the bStats server
        new Thread(() -> {
            try {
                // Send the data
                sendData(logger, data);
            } catch (Exception e) {
                // Something went wrong! :(
                if (logFailedRequests) {
                    logger.warn("Could not submit plugin stats!", e);
                }
            }
        }).start();
    }

    /**
     * Loads the bStats configuration.
     *
     * @throws IOException If something did not work :(
     */
    private void loadConfig() throws IOException {
        Path configPath = configDir.resolve("bStats");
        configPath.toFile().mkdirs();
        File configFile = new File(configPath.toFile(), "config.conf");
        HoconConfigurationLoader configurationLoader = HoconConfigurationLoader.builder().setFile(configFile).build();
        CommentedConfigurationNode node;
        if (!configFile.exists()) {
            configFile.createNewFile();
            node = configurationLoader.load();

            // Add default values
            node.getNode("enabled").setValue(true);
            // Every server gets it's unique random id.
            node.getNode("serverUuid").setValue(UUID.randomUUID().toString());
            // Should failed request be logged?
            node.getNode("logFailedRequests").setValue(false);
            // Should the sent data be logged?
            node.getNode("logSentData").setValue(false);
            // Should the response text be logged?
            node.getNode("logResponseStatusText").setValue(false);

            node.getNode("enabled").setComment(
                    "Enabling bStats in this file is deprecated. At least one of your plugins now uses the\n" +
                            "Sponge config to control bStats. Leave this value as you want it to be for outdated plugins,\n" +
                            "but look there for further control");
            // Add information about bStats
            node.getNode("serverUuid").setComment(
                    "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                            "To control whether this is enabled or disabled, see the Sponge configuration file.\n" +
                            "Check out https://bStats.org/ to learn more :)"
            );
            node.getNode("configVersion").setValue(2);

            configurationLoader.save(node);
        } else {
            node = configurationLoader.load();

            if (!node.getNode("configVersion").isVirtual()) {

                node.getNode("configVersion").setValue(2);

                node.getNode("enabled").setComment(
                        "Enabling bStats in this file is deprecated. At least one of your plugins now uses the\n" +
                                "Sponge config to control bStats. Leave this value as you want it to be for outdated plugins,\n" +
                                "but look there for further control");

                node.getNode("serverUuid").setComment(
                        "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                                "To control whether this is enabled or disabled, see the Sponge configuration file.\n" +
                                "Check out https://bStats.org/ to learn more :)"
                );

                configurationLoader.save(node);
            }
        }

        // Load configuration
        serverUUID = node.getNode("serverUuid").getString();
        logFailedRequests = node.getNode("logFailedRequests").getBoolean(false);
        logSentData = node.getNode("logSentData").getBoolean(false);
        logResponseStatusText = node.getNode("logResponseStatusText").getBoolean(false);
    }

    /**
     * Reads the first line of the file.
     *
     * @param file The file to read. Cannot be null.
     * @return The first line of the file or <code>null</code> if the file does not exist or is empty.
     * @throws IOException If something did not work :(
     */
    private String readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            return bufferedReader.readLine();
        }
    }

    /**
     * Sends the data to the bStats server.
     *
     * @param logger The used logger.
     * @param data The data to send.
     * @throws Exception If the request failed.
     */
    private static void sendData(Logger logger, JsonObject data) throws Exception {
        Validate.notNull(data, "Data cannot be null");
        if (logSentData) {
            logger.info("Sending data to bStats: {}", data.toString());
        }
        HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

        // Compress the data to save bandwidth
        byte[] compressedData = compress(data.toString());

        // Add headers
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
        connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

        // Send data
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(compressedData);
        outputStream.flush();
        outputStream.close();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        bufferedReader.close();
        if (logResponseStatusText) {
            logger.info("Sent data to bStats and received response: {}", builder.toString());
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
        GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return outputStream.toByteArray();
    }

    /**
     * Represents a custom chart.
     */
    public static abstract class CustomChart {

        // The id of the chart
        private final String chartId;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         */
        CustomChart(String chartId) {
            if (chartId == null || chartId.isEmpty()) {
                throw new IllegalArgumentException("ChartId cannot be null or empty!");
            }
            this.chartId = chartId;
        }

        private JsonObject getRequestJsonObject(Logger logger, boolean logFailedRequests) {
            JsonObject chart = new JsonObject();
            chart.addProperty("chartId", chartId);
            try {
                JsonObject data = getChartData();
                if (data == null) {
                    // If the data is null we don't send the chart.
                    return null;
                }
                chart.add("data", data);
            } catch (Throwable t) {
                if (logFailedRequests) {
                    logger.warn("Failed to get data for custom chart with id {}", chartId, t);
                }
                return null;
            }
            return chart;
        }

        protected abstract JsonObject getChartData() throws Exception;

    }

    /**
     * Represents a custom simple pie.
     */
    public static class SimplePie extends CustomChart {

        private final Callable<String> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public SimplePie(String chartId, Callable<String> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            String value = callable.call();
            if (value == null || value.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            data.addProperty("value", value);
            return data;
        }
    }

    /**
     * Represents a custom advanced pie.
     */
    public static class AdvancedPie extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, Integer> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 0) {
                    continue; // Skip this invalid
                }
                allSkipped = false;
                values.addProperty(entry.getKey(), entry.getValue());
            }
            if (allSkipped) {
                // Null = skip the chart
                return null;
            }
            data.add("values", values);
            return data;
        }
    }

    /**
     * Represents a custom drilldown pie.
     */
    public static class DrilldownPie extends CustomChart {

        private final Callable<Map<String, Map<String, Integer>>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        public JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, Map<String, Integer>> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean reallyAllSkipped = true;
            for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
                JsonObject value = new JsonObject();
                boolean allSkipped = true;
                for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
                    value.addProperty(valueEntry.getKey(), valueEntry.getValue());
                    allSkipped = false;
                }
                if (!allSkipped) {
                    reallyAllSkipped = false;
                    values.add(entryValues.getKey(), value);
                }
            }
            if (reallyAllSkipped) {
                // Null = skip the chart
                return null;
            }
            data.add("values", values);
            return data;
        }
    }

    /**
     * Represents a custom single line chart.
     */
    public static class SingleLineChart extends CustomChart {

        private final Callable<Integer> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public SingleLineChart(String chartId, Callable<Integer> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            int value = callable.call();
            if (value == 0) {
                // Null = skip the chart
                return null;
            }
            data.addProperty("value", value);
            return data;
        }

    }

    /**
     * Represents a custom multi line chart.
     */
    public static class MultiLineChart extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, Integer> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 0) {
                    continue; // Skip this invalid
                }
                allSkipped = false;
                values.addProperty(entry.getKey(), entry.getValue());
            }
            if (allSkipped) {
                // Null = skip the chart
                return null;
            }
            data.add("values", values);
            return data;
        }

    }

    /**
     * Represents a custom simple bar chart.
     */
    public static class SimpleBarChart extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, Integer> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                JsonArray categoryValues = new JsonArray();
                categoryValues.add(new JsonPrimitive(entry.getValue()));
                values.add(entry.getKey(), categoryValues);
            }
            data.add("values", values);
            return data;
        }

    }

    /**
     * Represents a custom advanced bar chart.
     */
    public static class AdvancedBarChart extends CustomChart {

        private final Callable<Map<String, int[]>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, int[]> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                if (entry.getValue().length == 0) {
                    continue; // Skip this invalid
                }
                allSkipped = false;
                JsonArray categoryValues = new JsonArray();
                for (int categoryValue : entry.getValue()) {
                    categoryValues.add(new JsonPrimitive(categoryValue));
                }
                values.add(entry.getKey(), categoryValues);
            }
            if (allSkipped) {
                // Null = skip the chart
                return null;
            }
            data.add("values", values);
            return data;
        }

    }

}
