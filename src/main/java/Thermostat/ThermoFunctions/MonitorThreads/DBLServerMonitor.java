package Thermostat.ThermoFunctions.MonitorThreads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static Thermostat.thermostat.thermoAPI;
import static Thermostat.thermostat.thermo;

/**
 * Keeps the DBL API of the bot updated
 * with the proper number of servers.
 */
public class DBLServerMonitor
{
    private static final DBLServerMonitor DBLInstance = new DBLServerMonitor();
    private Logger lgr = LoggerFactory.getLogger(DBLServerMonitor.class);

    private DBLServerMonitor() {
        Runnable status = this::setServers;
        ScheduledExecutorService statusScheduler = Executors.newSingleThreadScheduledExecutor();
        statusScheduler.scheduleAtFixedRate(status, 0, 120, TimeUnit.SECONDS);
    }

    private void setServers() {
        int currentServers = thermo.getGuilds().size();
        thermoAPI.setStats(currentServers);
        lgr.info("DBL Rest // Current Servers: " + currentServers);
    }
}
