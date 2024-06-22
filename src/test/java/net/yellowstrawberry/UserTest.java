package net.yellowstrawberry;

import com.github.f4b6a3.tsid.Tsid;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

import io.restassured.response.Response;
import net.yellowstrawberry.user.UserHandler;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class UserTest {

    private static String token;

    @Test
    public void register(){
        long l= Tsid.fast().toLong();
        given()
                .body("{\"username\": \"%s\", \"id\": \"%s\", \"password\": \"%s\"}".formatted(l, l, l))
                .when().post("/auth/register")
                .then()
                .statusCode(201);
    }

    @BeforeAll
    public static void login(){
        Response r = given()
                .body("{\"id\": \"example\", \"password\": \"pass\"}")
                .when().post("/auth/login");
        assert r.getStatusCode() == 200;
        JSONObject o= new JSONObject(r.getBody().asString());
        token = o.getString("token");
    }

    @Test
    public void me() {
        given()
                .headers(Map.of("Authorization", "Bearer "+token))
                .when().get("/@me")
                .then()
                .statusCode(200);
    }

    @Test
    public void meform() {
        given()
                .headers(Map.of(
                        "Authorization", "Bearer "+token,
                        "Content-Type", "application/json"
                ))
                .body("""
                        {
                          "age": 17,
                          "job": "학생생",
                          "interest": ["photo", "software"],
                          "categories": ["hackathon", "fasta"]
                        }""")
                .when().post("/@me/form")
                .then()
                .statusCode(200);
    }

    @Test
    public void postArchives() {
        given()
                .headers(Map.of(
                        "Authorization", "Bearer "+token,
                        "Content-Type", "application/json"
                ))
                .body("""
                        {
                        	"itemId": 1,
                        	"star": 1.5
                        }""")
                .when().post("/@me/archives")
                .then()
                .statusCode(201);
    }

    @Test
    public void archives() {
        given()
                .headers(Map.of("Authorization", "Bearer "+token))
                .when().get("/@me/archives")
                .then()
                .statusCode(200);
    }

    @Test
    public void recommendations() {
        given()
                .headers(Map.of("Authorization", "Bearer "+token))
                .when().get("/recommendations")
                .then()
                .statusCode(200);
    }

    @Test
    public void item() {
        given()
                .pathParam("id", 1)
                .when().get("/item/{id}")
                .then()
                .statusCode(200);
        given()
                .pathParam("id", 2)
                .when().get("/item/{id}")
                .then()
                .statusCode(404);
    }
    @Test
    public void itemReview() {
        given()
                .pathParam("id", 1)
                .when().get("/item/{id}/review")
                .then()
                .statusCode(200);
    }

    @Test
    public void reviewItem() {
        given()
                .headers(Map.of(
                        "Authorization", "Bearer "+token,
                        "Content-Type", "application/json"
                ))
                .pathParam("id", 1)
                .body("""
                        {
                         	"title": "<title (max 32)>",
                         	"contents": "<contents (max 16000)>",
                         	"star": 5.0
                         }""")
                .when().post("/item/{id}/review")
                .then()
                .statusCode(200);
    }
}