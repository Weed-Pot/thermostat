package thermostat.thermoFunctions.commands.informational;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thermostat.preparedStatements.ErrorEmbeds;
import thermostat.preparedStatements.GenericEmbeds;
import thermostat.mySQL.DataSource;
import thermostat.thermoFunctions.Messages;
import thermostat.thermoFunctions.commands.CommandEvent;
import thermostat.thermoFunctions.entities.CommandType;
import thermostat.thermostat;

import java.util.ArrayList;
import java.util.EnumSet;

import static thermostat.thermoFunctions.Functions.parseMention;

/**
 * Command that when called, shows an embed
 * with the settings of a specific channel.
 */
public class Settings implements CommandEvent {

    private static final Logger lgr = LoggerFactory.getLogger(Settings.class);

    private final Guild eventGuild;
    private final TextChannel eventChannel;
    private final Member eventMember;
    private final String eventPrefix;
    private ArrayList<String> args;

    private EnumSet<Permission> missingThermostatPerms, missingMemberPerms;

    public Settings(Guild eg, TextChannel tc, Member em, String px, ArrayList<String> ag) {
        eventGuild = eg;
        eventChannel = tc;
        eventMember = em;
        eventPrefix = px;
        args = ag;

        checkPermissions();
        if (missingMemberPerms.isEmpty() && missingThermostatPerms.isEmpty()) {
            execute();
        } else {
            lgr.info("Missing permissions on (" + eventGuild.getName() + "/" + eventGuild.getId() + "):" +
                    " [" + missingThermostatPerms.toString() + "] [" + missingMemberPerms.toString() + "]");
            Messages.sendMessage(eventChannel, ErrorEmbeds.errPermission(missingThermostatPerms, missingMemberPerms));
        }
    }

    @Override
    public void checkPermissions() {
        eventGuild
                .retrieveMember(thermostat.thermo.getSelfUser())
                .map(thermostat -> {
                    missingThermostatPerms = findMissingPermissions(CommandType.SETTINGS.getThermoPerms(), thermostat.getPermissions());
                    return thermostat;
                })
                .queue();

        missingMemberPerms = findMissingPermissions(CommandType.SETTINGS.getMemberPerms(), eventMember.getPermissions());
    }

    @Override
    public void execute() {
        // to contain channel id for modification
        String channelId;

        // th!settings [channel]
        if (args.size() >= 1) {
            channelId = parseMention(args.get(0), "#");

            // if channel doesn't exist, show error msg
            if (channelId.isEmpty() || eventGuild.getTextChannelById(channelId) == null) {
                Messages.sendMessage(eventChannel, ErrorEmbeds.channelNotFound(args.get(0)));
                return;
            }
        }
        // th!settings
        else {
            channelId = eventChannel.getId();
        }

        // connects to database and creates channel
        int max = DataSource.queryInt("SELECT MAX_SLOW FROM CHANNEL_SETTINGS WHERE CHANNEL_ID = ?", channelId);

        if (max == -1) {
            Messages.sendMessage(eventChannel, GenericEmbeds.channelNeverMonitored());
            return;
        }

        TextChannel settingsChannel = eventGuild.getTextChannelById(channelId);

        if (settingsChannel != null) {
            Messages.sendMessage(eventChannel,
                    GenericEmbeds.channelSettings(settingsChannel.getName(),
                            eventMember.getUser().getAsTag(),
                            eventMember.getUser().getAvatarUrl(),
                            max,
                            DataSource.queryInt("SELECT MIN_SLOW FROM CHANNEL_SETTINGS WHERE CHANNEL_ID = ?", channelId),
                            DataSource.querySens("SELECT SENSOFFSET FROM CHANNEL_SETTINGS WHERE CHANNEL_ID = ?", channelId),
                            DataSource.queryBool("SELECT MONITORED FROM CHANNEL_SETTINGS WHERE CHANNEL_ID = ?", channelId),
                            DataSource.queryBool("SELECT FILTERED FROM CHANNEL_SETTINGS WHERE CHANNEL_ID = ?", channelId)
                    )
            );
        }
    }
}
