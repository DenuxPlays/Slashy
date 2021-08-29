package com.denux.slashy.services;
import com.denux.slashy.properties.ConfigString;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    public static Connection con;

    public void connectToDatabase() {

        String[] database = new ConfigString("database", "0").getValue().split(":");

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(database[0]);
        dataSource.setPort(Integer.parseInt(database[1]));
        dataSource.setUser(database[2]);
        dataSource.setPassword(database[3]);
        dataSource.setDatabaseName(database[4]);
        try {
            con = dataSource.getConnection();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getLogChannel(String guild_id) {
        try {
        PreparedStatement preparedStatement = con.prepareStatement("SELECT channel_id FROM logChannel WHERE guild_id = ?");
        preparedStatement.setString(1, guild_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        String channel_id = "You don't have a log channel.";
        while (resultSet.next()) {
            channel_id = resultSet.getString(1);
        }
        return channel_id;
        } catch (Exception exception) {
            exception.printStackTrace();
            return "Error";
        }
    }
}
