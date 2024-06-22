package net.yellowstrawberry.file;

import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.ws.rs.core.MediaType;
import net.yellowstrawberry.db.SQLCommunicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileManager {

    private static final File root = new File("/Users/yellowstrawberry/IdeaProjects/appjamlego");

    // 굳이 만들 필요가 있을까 생각함
    public static String upload(byte[] data, String extension) {
        long l = TsidCreator.getTsid().toLong();
        String id = "/files/"+l+"."+extension.replaceAll("image/", "");
        try (FileOutputStream os = new FileOutputStream(new File(root, id))){
            os.write(data);
            os.flush();
            SQLCommunicator.executeUpdate("INSERT INTO `files` (`id`, `path`, `extension`) VALUES (?, ?, ?);", l, id, extension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return id;
    }


    public static Object[] getFile(String id) {
        try(ResultSet set = SQLCommunicator.executeQuery("SELECT * FROM `files` WHERE `id`=? LIMIT 1;", id)) {
            if(set.first()) {
                System.out.println(new File("/Users/yellowstrawberry/IdeaProjects/appjamlego"+set.getString("path")).isFile());
                return new Object[]{new File("/Users/yellowstrawberry/IdeaProjects/appjamlego"+set.getString("path")), set.getString("extension")};
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}