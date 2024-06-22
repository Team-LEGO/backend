package net.yellowstrawberry.user;

import net.yellowstrawberry.db.SQLCommunicator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class UserManager {
    private static final List<String> interestss = Arrays.asList(
            "photo", "software", "idea", "design", "typo", "contest", "hackathon", "foundsupportation", "support"
    );
    private static final List<String> categoriess = Arrays.asList(
            "hackathon", "fasta", "contest", "foundsupportation", "activities", "support", "project", "internship", "etc"
    );

    public static JSONObject getUserData(String id) {
        if(id == null) return null;
        id = id.replaceAll("Basic", "").replaceAll(" ", "");
        System.out.println(id);
        try(ResultSet set = SQLCommunicator.executeQuery("SELECT * FROM `users` WHERE `id`=? LIMIT 1;", id)) {
            if(set.next()) {
                JSONObject o = new JSONObject();
                o.put("id", set.getString("id"));
                o.put("name", set.getString("name"));
                o.put("age", set.getInt("age"));
                o.put("job", set.getString("job"));
                o.put("interests", new JSONArray(set.getString("interests")));
                o.put("categories", new JSONArray(set.getString("categories")));
                o.put("joinedAt", set.getTimestamp("joined").getTime());
                return o;
            }else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateFormData(String id, int age, String job, JSONArray interest, JSONArray categories) {
        if(checkCategories(categories) && checkInterests(interest)) {
            SQLCommunicator.executeUpdate("UPDATE `users` SET `age`=?, `job`=?, `interests`=?, `categories`=? WHERE `id`=?;", age, job, interest.toString(), categories.toString(), id);
            return true;
        }else return false;
    }

    public static JSONObject getArchives(String id, Long offset, Integer max) {
        try(ResultSet set = SQLCommunicator.executeQuery("SELECT * FROM `archives` WHERE `id`=? AND `time`>? ORDER BY `time` DESC LIMIT ?;", id, offset==null?0L : offset, max==null?100:max)) {
            JSONArray a = new JSONArray();
            while (set.next()) {
                JSONObject o = new JSONObject();
                o.put("item", set.getInt("item"));
                o.put("star", set.getFloat("star"));
                o.put("time", set.getString("time"));
                a.put(o);
            }
            if(a.isEmpty()) return null;
            else {
                JSONObject archive = new JSONObject();
                archive.put("archives", a);
                return archive;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void archive(String id, int itemId, float star) {
        SQLCommunicator.executeUpdate("INSERT INTO `archives` (`id`, `item`, `star`, `time`) VALUES (?, ?, ?, ?);", id, itemId, star, new Timestamp(System.currentTimeMillis()));
    }

    private static boolean checkInterests(JSONArray interests) {
        for(Object o : interests) {
            if(!interestss.contains((String) o)) return false;
        }
        return true;
    }

    private static boolean checkCategories(JSONArray categories) {
        for(Object o : categories) {
            if(!categoriess.contains((String) o)) return false;
        }
        return true;
    }

    public static JSONArray getUserInterests(String id) {
        try (ResultSet set = SQLCommunicator.executeQuery("SELECT interests FROM `users` WHERE `id`=?;", id)) {
            if(set.next()) {
                return new JSONArray(set.getString("interests"));
            }else return new JSONArray();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray getUserCategories(String id) {
        try (ResultSet set = SQLCommunicator.executeQuery("SELECT categories FROM `users` WHERE `id`=?;", id)) {
            if(set.next()) {
                return new JSONArray(set.getString("categories"));
            }else return new JSONArray();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
