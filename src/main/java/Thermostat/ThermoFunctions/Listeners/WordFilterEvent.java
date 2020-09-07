package thermostat.thermoFunctions.listeners;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thermostat.mySQL.DataSource;
import thermostat.thermoFunctions.Messages;
import thermostat.thermostat;
import thermostat.Embeds;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class WordFilterEvent {

    private static final Logger lgr = LoggerFactory.getLogger(WordFilterEvent.class);
    private static List<String>
            badWords,
            niceWords;
    private static final Random random = new Random();

    private final TextChannel eventChannel;
    private final Message eventMessage;
    private final List<String> message;

    public WordFilterEvent(@NotNull TextChannel eventChannel, @NotNull Message eventMessage, @Nonnull List<String> message) {
        this.eventChannel = eventChannel;
        this.eventMessage = eventMessage;
        this.message = message;
        this.filter();
    }

    public void filter() {
        Member thermostatMember = eventChannel.getGuild().getMember(thermostat.thermo.getSelfUser());

        if (thermostatMember != null) {
            if (!thermostatMember.hasPermission(Permission.MANAGE_WEBHOOKS)) {
                lgr.debug("Thermostat lacks permission MANAGE_WEBHOOKS. Guild: "
                        + eventChannel.getGuild().getId() + " // Channel: " + eventChannel.getId());
                Messages.sendMessage(eventChannel, Embeds.simpleInsufficientPerm("MANAGE_WEBHOOKS"));
                return;
            }

            if (!thermostatMember.hasPermission(Permission.MESSAGE_MANAGE)) {
                lgr.debug("Thermostat lacks permission MANAGE_MESSAGES. Guild: "
                        + eventChannel.getGuild().getId() + " // Channel: " + eventChannel.getId());
                Messages.sendMessage(eventChannel, Embeds.simpleInsufficientPerm("MANAGE_WEBHOOKS"));
                return;
            }

        } else {
            lgr.debug("Thermostat member is null, cancelled filter job. Guild: "
                    + eventChannel.getGuild().getId() + " // Channel: " + eventChannel.getId());
            return;
        }

        boolean messageWasChanged = false;
        for (int index = 0; index < message.size(); ++index) {
            String string = message.get(index);

            if (badWords.stream().anyMatch(string.toLowerCase()::contains)) {
                messageWasChanged = true;
                message.set(index, niceWords.get(random.nextInt(niceWords.size())));
            }
        }

        if (messageWasChanged) {

            eventMessage.delete()
                    .reason("Inappropriate Language Filter (Thermostat)")
                    .queue();

            String webhookURL = getWebhookURL();

            if (webhookURL.equals("N/A")) {
                createWebhook(eventChannel, eventMessage.getAuthor())
                .map(unused -> {
                            sendWebhookMessage(getWebhookURL());
                            return unused;
                }).queue();
            } else {
                updateWebhook(eventChannel, eventMessage.getAuthor(), webhookURL)
                .map(unused -> {
                    sendWebhookMessage(webhookURL);
                    return unused;
                }).queue();
            }
        }
    }

    public String getWebhookURL() {
        return DataSource.queryString("SELECT WEBHOOK_URL FROM " +
                "CHANNEL_SETTINGS JOIN CHANNELS ON (CHANNELS.CHANNEL_ID = CHANNEL_SETTINGS.CHANNEL_ID) " +
                "WHERE CHANNEL_SETTINGS.CHANNEL_ID = ?", eventChannel.getId());
    }

    public void sendWebhookMessage(@Nonnull String webhookURL) {
        WebhookClientBuilder builder = new WebhookClientBuilder(webhookURL);
        builder.setAllowedMentions(AllowedMentions.none());
        WebhookClient client = builder.build();

        client.send(String.join(" ", message));
        client.close();
    }

    public RestAction<Void> updateWebhook(@NotNull TextChannel eventChannel, @NotNull User eventAuthor, String webhookURL) {

        String username = eventAuthor.getName();
        String userAvatarURL;

        if (eventAuthor.getAvatarUrl() != null)
            userAvatarURL = eventAuthor.getAvatarUrl();
        else
            userAvatarURL = eventAuthor.getDefaultAvatarUrl();

        Icon userAvatar = getUserIcon(userAvatarURL);

        return eventChannel
                .retrieveWebhooks()
                .map(webhookList -> findWebhook(webhookList, webhookURL))
                .flatMap(
                        Objects::nonNull,
                        webhook -> webhook.getManager().setName(username).setAvatar(userAvatar)
                );
    }

    @Nullable
    public Webhook findWebhook(@Nonnull List<Webhook> webhookList, @Nonnull String webhookURL) {
        Webhook foundWebhook = null;

        for (Webhook webhook : webhookList) {
            if (webhook.getUrl().equals(webhookURL)) {
                foundWebhook = webhook;
            }
        }

        return foundWebhook;
    }


    public RestAction<Webhook> createWebhook(@NotNull TextChannel eventChannel, @NotNull User eventAuthor) {

        String username = eventAuthor.getName();
        String userAvatarURL;

        if (eventAuthor.getAvatarUrl() != null)
            userAvatarURL = eventAuthor.getAvatarUrl();
        else
            userAvatarURL = eventAuthor.getDefaultAvatarUrl();

        Icon userAvatar = getUserIcon(userAvatarURL);

        return eventChannel
                .createWebhook(username)
                .map(
                        webhook -> {
                            webhook.getManager().setAvatar(userAvatar).setName(username)
                                    .queue();
                            try {
                                DataSource.update("UPDATE CHANNEL_SETTINGS JOIN CHANNELS ON " +
                                                "(CHANNELS.CHANNEL_ID = CHANNEL_SETTINGS.CHANNEL_ID) " +
                                                "SET CHANNEL_SETTINGS.WEBHOOK_URL = ? " +
                                                "WHERE CHANNEL_SETTINGS.CHANNEL_ID = ?",
                                        Arrays.asList(webhook.getUrl(), eventChannel.getId()));
                            } catch (SQLException ex) {
                                lgr.error("Something went wrong while setting Webhook URL!", ex);
                            }
                            return webhook;
                        }
                );
    }

    @Nullable
    @CheckReturnValue
    public Icon getUserIcon(@Nonnull String avatarURL) {
        try {
            InputStream imageStream = new URL(avatarURL + "?size=64").openStream();
            return Icon.from(imageStream, Icon.IconType.JPEG);
        } catch (IOException ex) {
            return null;
        }
    }

    public static void setWordArrays(ArrayList<String> nice, ArrayList<String> prohibited) {
        niceWords = nice;
        badWords = prohibited;
    }
}
