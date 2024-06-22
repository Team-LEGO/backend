package net.yellowstrawberry.user;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;

import static net.yellowstrawberry.auth.Auth.jwt;

@Path("/%40me")
public class UserHandler {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response me(@HeaderParam("Authorization") String id) {
        JSONObject d = UserManager.getUserData(jwt.decode(id.replaceAll("Bearer", "").replaceAll(" ", "")).getString("id"));
        if(d==null) return Response.status(Response.Status.UNAUTHORIZED).build();
        return Response.ok(d).build();
    }

    @POST
    @Path("/form")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response form(@HeaderParam("Authorization") String id, String json) {
        JSONObject d = UserManager.getUserData(jwt.decode(id.replaceAll("Bearer", "").replaceAll(" ", "")).getString("id"));
        if(d==null) return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            JSONObject a = new JSONObject(json);
            boolean b = UserManager.updateFormData(
                    d.getString("id"),
                    a.getInt("age"),
                    a.getString("job"),
                    a.getJSONArray("interest"),
                    a.getJSONArray("categories")
            );
            if(b) return Response.ok().build();
            else return Response.status(Response.Status.BAD_REQUEST).build();
        }catch (JSONException e) {return Response.status(400).entity("{\"error\": \"Failed to parse json\"}").build();}
    }

    @GET
    @Path("/archives")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchives(
            @HeaderParam("Authorization") String id,
            @QueryParam("offset") Long offset,
            @QueryParam("max") Integer max) {
        JSONObject d = UserManager.getUserData(jwt.decode(id.replaceAll("Bearer", "").replaceAll(" ", "")).getString("id"));
        if(d==null) return Response.status(Response.Status.UNAUTHORIZED).build();

        JSONObject o = UserManager.getArchives(d.getString("id"), offset, max);
        if(o==null) return Response.status(Response.Status.NO_CONTENT).build();
        return Response.ok(o).build();
    }

    @POST
    @Path("/archives")
    @Produces(MediaType.APPLICATION_JSON)
    public Response archives(@HeaderParam("Authorization") String id, String s) {
        JSONObject d = UserManager.getUserData(jwt.decode(id.replaceAll("Bearer", "").replaceAll(" ", "")).getString("id"));
        if(d==null) return Response.status(Response.Status.UNAUTHORIZED).build();

        try {
            JSONObject a = new JSONObject(s);
            UserManager.archive(d.getString("id"), a.getInt("itemId"), a.getFloat("star"));
            return Response.status(201).build();
        }catch (JSONException e) {return Response.status(400).entity("{\"error\": \"Failed to parse json\"}").build();}
    }
}
