package net.yellowstrawberry.recommendation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.yellowstrawberry.user.UserManager;
import org.json.JSONObject;

import static net.yellowstrawberry.auth.Auth.jwt;

@Path("/recommendations")
public class RecommendationHandler {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @HeaderParam("Authorization") String id,
            @QueryParam("offset") Integer offset,
            @QueryParam("max") Integer max
    ) {
        JSONObject d = UserManager.getUserData(jwt.decode(id.replaceAll("Bearer", "").replaceAll(" ", "")).getString("id"));
        if(d==null) return Response.status(Response.Status.UNAUTHORIZED).build();

        JSONObject o = new JSONObject();
        o.put("recommended", Recommendation.getRecommendations(
                d.getString("id"),
                offset,
                max
        ));
        return Response.ok(o).build();
    }
}
