package thermostat.thermoFunctions.commands.requestListeners.utility;

import net.dv8tion.jda.api.entities.MessageEmbed;
import thermostat.thermoFunctions.commands.requestListeners.RequestListener;
import thermostat.thermoFunctions.entities.RequestType;

public final class WordFilterListener implements RequestListener {
    public WordFilterListener() {
    }

    @Override
    public void requestComplete() {

    }

    @Override
    public void requestFailed() {

    }

    @Override
    public RequestType getRequestType() {
        return RequestType.WORDFILTEREVENT;
    }
}
