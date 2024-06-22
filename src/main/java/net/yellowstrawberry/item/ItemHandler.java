package net.yellowstrawberry.item;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.yellowstrawberry.user.UserManager;
import org.json.JSONException;
import org.json.JSONObject;

import static net.yellowstrawberry.auth.Auth.jwt;

@Path("/item")
public class ItemHandler {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItem(@PathParam("id") String id) {
        if(id == null || id.isEmpty()) return Response.status(Response.Status.BAD_REQUEST).build();

        JSONObject o = ItemManager.getItem(id);
        if(o==null) return Response.status(Response.Status.NOT_FOUND).build();
        else return Response.ok(o).build();
    }

    @GET
    @Path("/{id}/review")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviews(
            @PathParam("id") Integer id,
            @QueryParam("offset") Integer offset,
            @QueryParam("max") Integer max
    ) {
        if(id == null) return Response.status(Response.Status.BAD_REQUEST).build();
        try {
            JSONObject o = ItemManager.getReviews(id, offset, max);
            return Response.ok(o).build();
        }catch (JSONException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Failed to parse json\"}").build();
        }
    }

    @POST
    @Path("/{id}/review")
    @Produces(MediaType.APPLICATION_JSON)
    public Response review(@PathParam("id") Integer id, @HeaderParam("Authorization") String uid, String s) {
        JSONObject d = UserManager.getUserData(jwt.decode(uid.replaceAll("Bearer", "").replaceAll(" ", "")).getString("id"));
        if(d==null) return Response.status(Response.Status.UNAUTHORIZED).build();

        if(id == null) return Response.status(Response.Status.BAD_REQUEST).build();
        try {
            JSONObject o = new JSONObject(s);
            long i = ItemManager.review(
                    d.getString("id"),
                    id,
                    o.getString("title"),
                    o.getString("contents"),
                    o.getFloat("star")
                    );
            if(i==-1) return Response.status(Response.Status.NOT_FOUND).build();
            else return Response.ok("{\"id\": %s}".formatted(i)).build();
        }catch (JSONException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Failed to parse json\"}").build();
        }
    }


}
