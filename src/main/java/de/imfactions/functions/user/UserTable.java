package de.imfactions.functions.user;

import de.imfactions.Data;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.util.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class UserTable {

    private Data data;
    private MySQL mySQL;
    private UserUtil userUtil;

    public UserTable(UserUtil userUtil, Data data){
        this.data = data;
        this.userUtil = userUtil;
        mySQL = data.getMySQL();
        createUserTable();
    }
    
    private void createUserTable(){
        mySQL.update("CREATE TABLE IF NOT EXISTS user (uuid VARCHAR(60), ether INT(10), onlinetime BIGINT, firstJoin DATETIME, lastSeen BIGINT, PRIMARY KEY(uuid))");
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        try {
            ResultSet rs = mySQL.querry("SELECT uuid, ether, onlinetime, firstJoin, lastSeen FROM `user` WHERE 1");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                User user = new User(uuid, rs.getInt("ether"), rs.getLong("onlinetime"), rs.getDate("firstJoin"), rs.getLong("lastSeen"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void createUser(UUID uuid, int ether, long onlinetime, Date firstJoin, long lastSeen) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mySQL.update("INSERT INTO user VALUES ('" + uuid.toString() + "', " + ether + ", " + onlinetime + ", '" + sdf.format(firstJoin) + "', " + lastSeen + ")");
    }

    public void deleteUser(User user) {
        mySQL.update("DELETE FROM user WHERE uuid = '" + user.getUUID() + "'");
    }

    public void saveUser(User user) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mySQL.update("UPDATE user SET ether = " + user.getEther() + ", onlinetime = " + user.getOnlinetime() + ", firstJoin = '" + sdf.format(user.getFirstJoin()) + "', lastSeen = " + user.getLastSeen() + " WHERE uuid = '" + user.getUUID().toString() + "'");
    }
}
