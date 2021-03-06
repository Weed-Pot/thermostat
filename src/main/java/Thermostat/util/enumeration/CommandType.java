package thermostat.util.enumeration;

import net.dv8tion.jda.api.Permission;
import thermostat.commands.CommandTrigger;

import java.util.EnumSet;

/**
 * Used to identify commands on the Command listener.
 * Handy for organizing permissions for every command.
 * @see CommandTrigger
 */
public enum CommandType {

    // ***************************************************************
    // **                       INFORMATIONAL                       **
    // ***************************************************************

    CHART(
            "chart", "ch",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES
            ),
            EnumSet.of(
                    Permission.MANAGE_SERVER
            ), EmbedType.HELP_CHART),
    GETMONITOR("getmonitor", "gm",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS
            ),
            EnumSet.of(
                    Permission.MANAGE_CHANNEL
            ), EmbedType.HELP_GETMONITOR),
    SETTINGS("settings", "st",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS
            ),
            EnumSet.of(
                    Permission.MANAGE_CHANNEL
            ), EmbedType.HELP_SETTINGS),

    // ***************************************************************
    // **                       MONITORING                          **
    // ***************************************************************

    MONITOR("monitor", "mn",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_HISTORY,
                    Permission.MANAGE_CHANNEL
            ),
            EnumSet.of(
                    Permission.MANAGE_CHANNEL
            ), EmbedType.HELP_MONITOR),
    SENSITIVITY("sensitivity", "ss",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MANAGE_CHANNEL
            ),
            EnumSet.of(
                    Permission.MANAGE_CHANNEL
            ), EmbedType.HELP_SENSITIVITY),
    SETBOUNDS("setbounds", "sb",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MANAGE_CHANNEL
            ),
            EnumSet.of(
                    Permission.MANAGE_CHANNEL
            ), EmbedType.HELP_SETBOUNDS),
    SETCACHING("setcaching", "sc",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MANAGE_CHANNEL
            ),
            EnumSet.of(
                    Permission.MANAGE_SERVER
            ), EmbedType.HELP_SETCACHE),

    // ***************************************************************
    // **                       MODERATION                          **
    // ***************************************************************

    BAN("ban", "bn",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.BAN_MEMBERS
            ),
            EnumSet.of(
                    Permission.BAN_MEMBERS
            ), EmbedType.HELP_BAN),
    KICK("kick", "kk",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MANAGE_CHANNEL
            ),
            EnumSet.of(
                    Permission.KICK_MEMBERS
            ), EmbedType.HELP_KICK),
    MUTE("mute", "mt",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MANAGE_ROLES
            ),
            EnumSet.of(
                    Permission.VOICE_MUTE_OTHERS,
                    Permission.KICK_MEMBERS
            ), EmbedType.HELP_MUTE),
    PURGE("purge", "ex",
            EnumSet.of(
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_MANAGE
            ),
            EnumSet.of(
                    Permission.MESSAGE_MANAGE
            ), EmbedType.HELP_PURGE),

    // ***************************************************************
    // **                         UTILITY                           **
    // ***************************************************************
    FILTER("filter", "ft",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_HISTORY,
                    Permission.MANAGE_CHANNEL,
                    Permission.MANAGE_WEBHOOKS
            ),
            EnumSet.of(
                    Permission.MANAGE_CHANNEL,
                    Permission.MANAGE_SERVER
            ), EmbedType.HELP_FILTER),

    // ***************************************************************
    // **                       OTHER                               **
    // ***************************************************************
    INFO("info", "io",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_ADD_REACTION
            ), EmbedType.HELP_INFO),
    HELP("help", "hp",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_ADD_REACTION
            ), EmbedType.HELP_INFO),
    GUIDE("", "",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS
            ), EmbedType.GUIDE),
    INVITE("invite", "iv",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS
            ), EmbedType.HELP_INVITE),
    PREFIX("prefix", "px",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS
            ),
            EnumSet.of(
                    Permission.MANAGE_SERVER
            ), EmbedType.HELP_PREFIX),
    VOTE("vote", "vo",
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS
            ), EmbedType.HELP_VOTE),

    // ***************************************************************
    // **                   Internal Commands                       **
    // **     (Used as reference for Thermostat's Permissions)      **
    // ***************************************************************
    ADD_REACTIONS(
            EnumSet.of(
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_MANAGE,
                    Permission.MESSAGE_ADD_REACTION
            )
    ),
    SYNAPSE_MONITOR(
            EnumSet.of(
                    Permission.MESSAGE_HISTORY,
                    Permission.MANAGE_CHANNEL
            )
    ),
    WORDFILTEREVENT(
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_MANAGE,
                    Permission.MANAGE_WEBHOOKS
            )
    ),
    DELETE_REACTIONS(
            EnumSet.of(
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_MANAGE
            )
    ),
    DELETE_MESSAGE(
            EnumSet.of(
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_MANAGE
            )
    ),
    EDIT_MESSAGE(
            EnumSet.of(
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY
            )
    ),
    SEND_MESSAGE_TEXT(
            EnumSet.of(
                    Permission.MESSAGE_WRITE
            )
    ),
    SEND_MESSAGE_EMBED(
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS
            )
    ),
    SEND_MESSAGE_ATTACHMENT(
            EnumSet.of(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES
            )
    );

    /**
     * Long and short alias to run the command.
     */
    public final String alias1, alias2;

    /**
     * Permissions that Thermostat needs to run the command.
     */
    private final EnumSet<Permission> thermoPermissions;

    /**
     * Permissions that the initiator needs to run the command.
     */
    private final EnumSet<Permission> memberPermissions;

    /**
     * The help Embed associated with this command.
     */
    private final EmbedType embedType;

    CommandType(String alias1, String alias2, EnumSet<Permission> thermoPermissions, EnumSet<Permission> memberPermissions, EmbedType type) {
        this.alias1 = alias1;
        this.alias2 = alias2;
        this.thermoPermissions = thermoPermissions;
        this.memberPermissions = memberPermissions;
        this.embedType = type;
    }

    CommandType(String alias1, String alias2, EnumSet<Permission> thermoPermissions, EmbedType type) {
        this.alias1 = alias1;
        this.alias2 = alias2;
        this.thermoPermissions = thermoPermissions;
        this.memberPermissions = EnumSet.noneOf(Permission.class);
        this.embedType = type;
    }

    CommandType(EnumSet<Permission> thermoPermissions) {
        this.alias1 = null;
        this.alias2 = null;
        this.thermoPermissions = thermoPermissions;
        this.memberPermissions = null;
        this.embedType = null;
    }

    /**
     * @return First alias of command.
     */
    public String getAlias1() {
        return alias1;
    }

    // Functions below must return clones, otherwise the originals will get modified by methods
    // that process these EnumSets due to the abstraction EnumSet provides..

    /**
     * @return A set of permissions required by Thermostat to run the command.
     */
    public EnumSet<Permission> getThermoPerms() { return thermoPermissions.clone(); }

    /**
     * @return A set of permissions a member must have to run the command.
     */
    public EnumSet<Permission> getMemberPerms() { return memberPermissions.clone(); }

    /**
     * @return The embed type associated with this command type.
     */
    public EmbedType getEmbedType() {
        return embedType;
    }
}