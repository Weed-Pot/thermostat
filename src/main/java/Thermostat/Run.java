package thermostat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

/**
 * Runs a sole instance of Thermostat.
 */
public final class Run {
    public static final Logger lgr = LoggerFactory.getLogger(Run.class);

    public static void main(String[] args) {
        try {
            Thermostat.initializeThermostat();
        } catch (LoginException ex) {
            lgr.error("Thermostat was unable to start due to this problem:", ex);
            Thermostat.shutdownThermostat();
            System.exit(-1);
        }
    }
}