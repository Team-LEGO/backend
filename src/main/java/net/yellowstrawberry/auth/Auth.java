package net.yellowstrawberry.auth;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.impl.jose.JWT;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/auth")
public class Auth {

    public static final JWT jwt = new JWT();

    @POST
    @Path("/register")
    public Response register(String json) {
        try {
            JSONObject o = new JSONObject(json);
            AuthManager.register(o.getString("username"), o.getString("id"), o.getString("password"));
            return Response.status(201).build();
        }catch (JSONException e) {
            return Response.status(400).entity("{\"error\": \"Failed to parse json\"}").build();
        }
    }

    @POST
    @Path("/login")
    public Response login(String json) {
        try {
            JSONObject o = new JSONObject(json);
            if(AuthManager.login(o.getString("id"), o.getString("password"))) {
                return Response.status(200).entity("{\"token\": %s}".formatted(jwt.sign(JsonObject.of("id", o.getString("id")), new JWTOptions().setAlgorithm("RS256")))).build();
            }
        }catch (JSONException e) {
            return Response.status(400).entity("{\"error\": \"Failed to parse json\"}").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
