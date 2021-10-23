package de.imfactions.functions.texture;

import com.mojang.authlib.properties.Property;
import de.imfactions.IMFactions;
import de.imfactions.util.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TextureTable {
    private final MySQL mySQL;

    public TextureTable(IMFactions factions) {
        this.mySQL = factions.getData().getMySQL();
        createTextureTable();
    }

    public void createTextureTable(){
        mySQL.update("CREATE TABLE IF NOT EXISTS textures (url VARCHAR(200), texture VARCHAR(1000), signature VARCHAR(1000), PRIMARY KEY (url))");
    }
    
    public void insertTexture(String url, Property property){
        insertTexture(url, property.getValue(), property.getSignature());
    }
    
    public void insertTexture(String url, String texture, String signature){
        mySQL.update("INSERT INTO textures (url, texture, signature) VALUES ('" + url + "', '" + texture + "', '" + signature + "')");
    }
    
    public Property getTexture(String url){
        try{
            ResultSet resultSet = mySQL.querry("SELECT * FROM textures WHERE url = '" + url + "'");
            if (resultSet.next()){
                return new Property("textures", resultSet.getString("texture"), resultSet.getString("signature"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new NullPointerException("Skin not found");
    }

    public boolean isTextureSaved(String url){
        try{
            ResultSet resultSet = mySQL.querry("SELECT url FROM textures WHERE url = '" + url + "'");
            return resultSet.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
