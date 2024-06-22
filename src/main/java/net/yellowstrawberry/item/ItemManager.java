package net.yellowstrawberry.item;

import com.github.f4b6a3.tsid.TsidCreator;
import net.yellowstrawberry.db.SQLCommunicator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemManager {
    public static JSONObject getItem(String id) {
        try (ResultSet set = SQLCommunicator.executeQuery("SELECT * FROM `items` WHERE `id`=? LIMIT 1;", id)) {
            if(set.next()) {
                JSONObject o = new JSONObject();
                o.put("id", set.getInt("id"));
                o.put("title", set.getString("title"));
                o.put("contents", set.getString("contents"));
                o.put("thumbnail", set.getString("thumbnail"));
                return o;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static long review(String id, int item, String title, String contents, float star) {
        try (ResultSet set = SQLCommunicator.executeQuery("SELECT 1 FROM `items` WHERE `id`=?;", item)) {
            if(set.next()) {
                long rid = TsidCreator.getTsid().toLong();
                SQLCommunicator.executeUpdate("INSERT INTO `reviews` (`id`, `user`, `item`, `title`, `contents`, `star`) VALUES (?, ?, ?, ?, ?, ?);", rid, id, item, title, contents, star);
                return rid;
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public static JSONObject getReviews(int id, Integer offset, Integer limit) {
        try (ResultSet set = SQLCommunicator.executeQuery("SELECT * FROM `reviews` WHERE `id`=? LIMIT ?, ?;", id, offset==null?0:offset, limit==null?20:limit)) {
            JSONArray a = new JSONArray();
            while (set.next()) {
                JSONObject o = new JSONObject();
                o.put("title", set.getString("title"));
                o.put("contents", set.getString("contents"));
                o.put("username", set.getString("user"));
                o.put("star", set.getFloat("star"));
                a.put(o);
            }
            JSONObject o = new JSONObject();
            o.put("reviews", a);
            return o;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
