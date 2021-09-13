package com.denux.slashy.commands.dao;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Data-Access-Object (DAO) that represents a Sub Command.
 */
public abstract class GuildSlashSubCommand {

    protected SubcommandData subcommandData = null;

    public SubcommandData getSubCommandData () { return this.subcommandData; }
}
