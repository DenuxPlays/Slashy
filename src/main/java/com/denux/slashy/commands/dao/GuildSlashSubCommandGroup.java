package com.denux.slashy.commands.dao;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

/**
 * Data-Access-Object (DAO) that represents a Sub Command Group.
 */
public abstract class GuildSlashSubCommandGroup {

    protected SubcommandGroupData subcommandGroupData = null;
    protected Class[] subCommandClasses = null;

    public SubcommandGroupData getSubCommandGroupData () { return this.subcommandGroupData; }
    public Class[] getSubCommandClasses() { return this.subCommandClasses; }
}
