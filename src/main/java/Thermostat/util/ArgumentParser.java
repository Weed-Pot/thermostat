package thermostat.util;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import thermostat.util.entities.CommandArguments;
import thermostat.util.enumeration.CommandType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentParser {
    /**
     * Null-safe checker for whether lists have arguments.
     * @param lists Lists to check.
     * @return True if the list does contain data, false otherwise.
     */
    @SafeVarargs
    public static boolean hasArguments(final List<String>... lists) {
        for (List<String> list : lists) {
            if (list == null) {
                return false;
            }

            if (list.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Nonnull
    public static Map<String, List<String>> parseArguments(List<String> arguments) throws ParseException, IllegalArgumentException {
        // Create a HashMap where the final parameters will be stored.
        final Map<String, List<String>> params = new HashMap<>();

        // create a temporary list for the options
        List<String> options = null;

        for (final String arg : arguments) {
            if (arg.charAt(0) == '-') {
                if (arg.length() < 2) {
                    throw new ParseException("Error at argument " + arg + ".", 1);
                }

                options = new ArrayList<>();
                params.put(arg.substring(1)/*.toLowerCase()*/, options);
            } else if (options != null) {
                options.add(arg);
            } else {
                throw new IllegalArgumentException("Illegal argument usage: " + arg + ".");
            }
        }

        return params;
    }

    /**
     * @param eventChannel Target guild
     * @param rawChannels List of arguments
     * @return a list of target channel IDs, along with
     * two StringBuilders with arguments that were invalid.
     */
    @Nonnull
    public static CommandArguments parseChannelArgument(@Nonnull final TextChannel eventChannel, final List<String> rawChannels) {
        ArrayList<String> newArgs = new ArrayList<>();
        StringBuilder
                // Channels that could not be found
                nonValid = new StringBuilder(),
                // Channels that are valid, but are not text channels
                noText = new StringBuilder();

        // if no arguments were valid just add the event channel
        // as the target channel
        if (!hasArguments(rawChannels)) {
            newArgs.add(eventChannel.getId());
        } else {
            // parses arguments into usable IDs, checks if channels exist
            // up to args.size(), last channel
            for (int index = 0; index < rawChannels.size(); ++index) {

                // The argument gets parsed. If it's a mention, it gets formatted
                // into an ID through the parseMention() function.
                // All letters are removed, thus the usage of the
                // originalArgument string.
                String originalArgument = rawChannels.get(index);
                rawChannels.set(index, parseMention(rawChannels.get(index), "#"));

                // Category holder for null checking
                Category channelContainer = eventChannel.getGuild().getCategoryById(rawChannels.get(index));

                if (rawChannels.get(index).isBlank()) {
                    nonValid.append("\"").append(originalArgument).append("\" ");

                } else if (channelContainer != null) {
                    // firstly creates an immutable list of the channels in the category
                    List<TextChannel> TextChannels = channelContainer.getTextChannels();
                    // if list is empty add that it is in msg
                    if (TextChannels.isEmpty()) {
                        noText.append("<#").append(originalArgument).append("> ");
                    }
                    // iterates through every channel and adds its' id to the arg list
                    for (TextChannel it : TextChannels) {
                        newArgs.add(0, it.getId());
                    }
                }

                // removes element from arguments if it's not a valid channel ID
                else if (eventChannel.getGuild().getTextChannelById(rawChannels.get(index)) == null) {
                    nonValid.append("\"").append(originalArgument).append("\" ");
                } else {
                    newArgs.add(rawChannels.get(index));
                }
            }
        }

        return new CommandArguments(nonValid, noText, newArgs);
    }

    /**
     * A function that's used to grab IDs from Discord message mentions.
     *
     * @param mention       The mention grabbed from a message in its'
     *                      plain form.
     * @param discriminator Used to tell what type of mention it is.
     *                      In this case, it's the symbol after the "<"
     *                      sign on Discord mentions. Refer to
     *                      https://discordapp.com/developers/docs/reference
     *                      under the Message Formatting section for
     *                      each discriminator.
     * @return The ID that the mention contained.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String parseMention(String mention, String discriminator) {
        String retString = mention;

        if (mention.startsWith("<" + discriminator) && mention.endsWith(">"))
            retString = mention.substring(2, mention.length() - 1);

        if (retString.startsWith("!") || retString.startsWith("&"))
            retString = retString.substring(1);

        retString = retString.replaceAll("[^\\d]", "");

        // if the sent value is a number larger than 64 bits
        // the given ID is not a valid snowflake, therefore
        // not used.

        try {
            Long.parseUnsignedLong(retString);
        } catch (NumberFormatException ex) {
            // Returns 0 if arguments is empty due to being
            // parsed so when return value is used directly
            // in functions, they won't throw an exception
            retString = "0";
        }

        return retString;
    }

    /**
     * Function that converts a string slowmode argument
     * to a usable Integer one.
     * @param slowmode Slowmode argument, in a string.
     * @return Parsed slowmode value.
     */
    public static int parseSlowmode(String slowmode) throws NumberFormatException {
        int retInteger;
        // second = 1; minute = 60; hour = 3600
        int multiplyValue = 1;

        if (slowmode.contains("m")) {
            multiplyValue = 60;
        } else if (slowmode.contains("h")) {
            multiplyValue = 3600;
        }

        slowmode = slowmode.replaceAll("[^\\d]", "");
        retInteger = Integer.parseUnsignedInt(slowmode) * multiplyValue;

        return retInteger;
    }

    /**
     * Parses a given time value into a calendar instance.
     * @param time Time argument.
     * @return A calendar with the time argument added.
     * Null if argument cannot be parsed.
     */
    @Nullable
    public static Calendar parseTime(@Nonnull final String time) throws IllegalArgumentException {
        final Pattern p = Pattern.compile("(\\d+)([hmsd])");
        final Matcher m = p.matcher(time);

        long totalMinutes = 0;

        while (m.find())
        {
            final int duration = Integer.parseInt(m.group(1));
            final TimeUnit interval = MiscellaneousFunctions.toTimeUnit(m.group(2));
            final long l = interval.toMinutes(duration);
            totalMinutes += l;
        }

        if (totalMinutes == 0) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, (int) totalMinutes);
        return calendar;
    }

    /**
     * Checks if GuildMessageReceivedEvent is valid.
     * An event is valid when its Member object is not null.
     * @return true is event is valid, false otherwise.
     */
    public static boolean validateEvent(final GuildMessageReceivedEvent event) {
        if (event.getMember() == null)
            return false;

        return !event.getMember().getUser().isBot();
    }

    /**
     * Matches the initialization of a Command with a CommandType alias, and returns the CommandType.
     * @param arguments Arguments provided by the user.
     * @param prefix Prefix of the Guild.
     * @return Type of Command. Null if type could not be matched.
     */
    @Nullable
    public static CommandType getCommandByInit(final List<String> arguments, final String prefix) {
        final String initPrefix = arguments.get(0); // First init: Initialization with a prefix. (th!cmd)
        String initMention = null;
        if (arguments.size() > 1) {
            initMention = arguments.get(0).concat(arguments.get(1)); // Second init: Initialization by mention. (@Thermostat cmd)
        }

        // Check if @Thermostat was called without a command
        if (arguments.size() == 1 && arguments.get(0).equalsIgnoreCase("<@!" + Constants.THERMOSTAT_USER_ID + ">")) {
            return CommandType.GUIDE;
        }

        for (final CommandType t : CommandType.class.getEnumConstants()) {
            if (initPrefix.equalsIgnoreCase(prefix + t.alias1) ||
                    initPrefix.equalsIgnoreCase(prefix + t.alias2))
            {
                arguments.remove(0); // Remove the initialization to use in a command.
                return t; // Return the command type if there is a match
            }

            if (initMention != null) {  // Do not proceed if there is less than two arguments.
                if (initMention.equalsIgnoreCase("<@!" + Constants.THERMOSTAT_USER_ID + ">" + t.alias1) ||
                        initMention.equalsIgnoreCase("<@!" + Constants.THERMOSTAT_USER_ID + ">" + t.alias2))
                {
                    arguments.subList(0, 2).clear(); // Remove the initialization to use in a command.
                    return t; // Return command type if there is a match
                }
            }
        }
        return null; // Could not match type.
    }
}
