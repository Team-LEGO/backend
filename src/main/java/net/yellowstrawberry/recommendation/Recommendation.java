package net.yellowstrawberry.recommendation;

import net.yellowstrawberry.db.SQLCommunicator;
import net.yellowstrawberry.user.UserManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Recommendation {

    public static JSONArray getRecommendations(String id, Integer offset, Integer max) {
        JSONArray recommendationArray = new JSONArray();
        try(ResultSet set = SQLCommunicator.executeQuery("SELECT * FROM `items` WHERE (%s) OR (%s) LIMIT ?, ?;".formatted(
                toSQLLikes("interests", UserManager.getUserInterests(id)),
                toSQLLikes("categories", UserManager.getUserCategories(id))
        ), offset==null?0:offset, max==null?100:max)) {
            System.out.println("SELECT * FROM `items` WHERE (%s) OR (%s) LIMIT ?, ?;".formatted(
                    toSQLLikes("interests", UserManager.getUserInterests(id)),
                    toSQLLikes("categories", UserManager.getUserCategories(id))));
            while (set.next()) {
                System.out.println(set.getString("title"));
                JSONObject o = new JSONObject();
                o.put("id", set.getInt("id"));
                o.put("title", set.getString("title"));
                o.put("thumbnail", set.getString("thumbnail"));
                o.put("contents", set.getString("contents").substring(0, Math.min(100, set.getString("contents").length())));
                recommendationArray.put(o);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendationArray;
    }

    private static String toSQLLikes(String column, JSONArray a) {
        StringBuilder sb = new StringBuilder();
        for (Object o : a) {
            if(!sb.isEmpty()) sb.append(" OR ");
            sb.append("`").append(column).append("` LIKE '%").append((String) o).append("%'");
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}
