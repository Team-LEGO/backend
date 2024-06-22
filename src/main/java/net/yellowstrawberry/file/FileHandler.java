package net.yellowstrawberry.file;

import io.vertx.core.buffer.Buffer;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.multipart.FileItem;
import org.jboss.resteasy.reactive.server.multipart.FormValue;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataInput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;


@Path("/file")
public class FileHandler {

    @POST
    @Path("/item/{itemId}/thumbnail")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadThumbnail(MultipartFormDataInput inputForm) throws IOException {
        for (Map.Entry<String, Collection<FormValue>> attribute : inputForm.getValues().entrySet()) {
            for (FormValue fv : attribute.getValue()) {
                if (fv.isFileItem()) {
                    final FileItem fi = fv.getFileItem();
                    String ct = getContentsType(fv.getFileName());
                    if(ct==null) return Response.status(400).build();
                    if (fi.isInMemory()) {
                        String s = FileManager.upload(fi.getInputStream().readAllBytes(), ct);
                        return Response.accepted().build();
                    } else {
                        try(FileInputStream i = new FileInputStream(fi.getFile().toString())) {
                            FileManager.upload(i.readAllBytes(), ct);
                        }catch (Exception e) {e.printStackTrace();}
                    }
                    return Response.status(200).build();
                }
            }
        }
        return Response.status(400).build();
    }

    private static String getContentsType(String s) {
        if(s.endsWith(".png")) return "image/png";
        else if(s.endsWith(".jpg")) return "image/jpeg";
        else if(s.endsWith(".jpeg")) return "image/jpeg";
        else if(s.endsWith(".gif")) return "image/gif";
        else return null;
    }

    @GET
    @Path("/{id}")
    public Response getFile(@PathParam("id") String id) {
        Object[] nf = FileManager.getFile(id);
        if(nf==null) return Response.status(Response.Status.NOT_FOUND).build();
        Response.ResponseBuilder response = Response.ok(nf[0]);
        response.header("Content-Type", nf[1]);
        return response.build();
    }
}
