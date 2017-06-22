package com.centralway.assessment.phonebook;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureTestDatabase
public class PhonebookWebApplicationTests {

    @LocalServerPort
    protected int port;

    private final String user01Username = "adam@maillinator.com";
    private final String user01Password = "P@ssw0d1";
    private final String user02Username = "tom@maillinator.com";
    private final String user02Password = "P@ssw0d2";

    private final String clientId = "trusted-app";
    private final String clientSecret = "secret";

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void test01RootIsSecured() {
        given().when().get("/").then().statusCode(401);
    }

    @Test
    public void test02UserRegistration() {
        String requestBody = "{\"username\":\"" + user01Username + "\", \"password\":\""+ user01Password+"\"}";
        given()
                .contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/user/register")
                        .then().assertThat().statusCode(201).and().body("id", notNullValue());

        requestBody = "{\"username\":\"" + user02Username + "\", \"password\":\""+ user02Password+"\"}";
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user/register")
                .then().assertThat().statusCode(201).and().body("id", notNullValue());
    }

    @Test
    public void test04AuthenticateViaOauth() {
        String requestBody = "grant_type=password&username=" + user01Username + "&password=" + user01Password;
        given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                //.
                .auth().preemptive().basic(clientId, clientSecret)
                .when()
                .post("/oauth/token")
                .then().assertThat().statusCode(200).and().body("access_token", notNullValue());
    }

    @Test
    public void test05AuthenticateViaUserEndpoint() {
        String requestBody = "{\"username\":\"" + user01Username + "\", \"password\":\""+ user01Password+"\"}";
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user")
                .then().assertThat().statusCode(201).and().body("token", notNullValue());
    }

    @Test
    public void test06AddContactRequiresAuthentication() {
        String requestBody = "{\"first_name\":\"Alice\", \"last_name\":\"Cooper\"}";
        given()
                .auth().preemptive().basic(clientId, clientSecret)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/contacts")
                .then().assertThat().statusCode(401);
    }

    @Test
    public void test07AddContact() {
        String requestBody = "{\"username\":\"" + user01Username + "\", \"password\":\""+ user01Password+"\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user")
                .andReturn();
        JsonPath jsonPath = new JsonPath(response.asString());
        String accessToken = jsonPath.getString("token");

        requestBody = "{\"first_name\":\"Alice\", \"last_name\":\"Cooper\"}";

        given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/contacts")
                .then().assertThat().statusCode(201).and().body("id", notNullValue());
    }

    @Test
    public void test08AddedContactIsOnlyVisibleToTheOwner() {
        String requestBody = "grant_type=password&username=" + user01Username + "&password=" + user01Password;
        Response response = given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                //.
                .auth().preemptive().basic(clientId, clientSecret)
                .when()
                .post("/oauth/token")
                .andReturn();
        JsonPath jsonPath = new JsonPath(response.asString());
        String accessToken = jsonPath.getString("access_token");

        //First user must have 1 contact
        given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .get("/contacts")
                .then().assertThat().statusCode(200).and()
                .body("first_name", contains("Alice"), "first_name", hasSize(1));

        //Second user must have no contacts
        requestBody = "grant_type=password&username=" + user02Username + "&password=" + user02Password;
        response = given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                //.
                .auth().preemptive().basic(clientId, clientSecret)
                .when()
                .post("/oauth/token")
                .andReturn();
        jsonPath = new JsonPath(response.asString());
        accessToken = jsonPath.getString("access_token");
        
        given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .get("/contacts")
                .then().assertThat().statusCode(200).and()
                .body("first_name", hasSize(0));
    }

    @Test
    public void test09AddPhone() {
        String requestBody = "grant_type=password&username=" + user01Username + "&password=" + user01Password;
        Response response = given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                //.
                .auth().preemptive().basic(clientId, clientSecret)
                .when()
                .post("/oauth/token")
                .andReturn();
        JsonPath jsonPath = new JsonPath(response.asString());
        String accessToken = jsonPath.getString("access_token");

        response = given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .get("/contacts")
                .andReturn();
        jsonPath = new JsonPath(response.asString());

        //let's get the ID of the sole contact we have in our list
        Integer contactId = (Integer) jsonPath.getList("id").get(0);

        requestBody = "{\"phone\":\"+1 (555) 123-456\"}";
        given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/contacts/" + contactId + "/entries")
                .then().assertThat().statusCode(201).and().body("id", notNullValue());
    }

    @Test
    public void test10ContactCannotBeDeletedByTheOwner() {
        String requestBody = "grant_type=password&username=" + user01Username + "&password=" + user01Password;
        Response response = given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                //.
                .auth().preemptive().basic(clientId, clientSecret)
                .log().all()
                .when()
                .post("/oauth/token")
                .andReturn();
        JsonPath jsonPath = new JsonPath(response.asString());
        String adamAccessToken = jsonPath.getString("access_token");

        response = given()
                .auth().oauth2(adamAccessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .get("/contacts")
                .andReturn();
        jsonPath = new JsonPath(response.asString());

        //let's get the ID of the sole contact we have in our list
        Integer contactId = (Integer) jsonPath.getList("id").get(0);

        //and let's try to delete it using the user who does not own it (operation will fail)
        requestBody = "{\"username\":\"" + user02Username + "\", \"password\":\""+ user02Password+"\"}";
        response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user")
                .andReturn();
        jsonPath = new JsonPath(response.asString());
        String tomAccessToken = jsonPath.getString("token");
        given()
                .auth().oauth2(tomAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/contacts/" + contactId)
                .then().assertThat().statusCode(403);

        //let's delete with Adam now, will work
        given()
                .auth().oauth2(adamAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/contacts/" + contactId)
                .then().assertThat().statusCode(200).and().body(isEmptyOrNullString());

        //Contact list must be empty now
        given()
                .auth().oauth2(adamAccessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .get("/contacts")
                .then().assertThat().statusCode(200).and()
                .body("first_name", hasSize(0));
    }

    @Test
    public void test11TwoContactsCanHaveTheSamePhoneNumber() {
        String requestBody = "{\"username\":\"" + user02Username + "\", \"password\":\""+ user02Password+"\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/user")
                .andReturn();
        JsonPath jsonPath = new JsonPath(response.asString());
        String accessToken = jsonPath.getString("token");

        requestBody = "{\"first_name\":\"Alice\", \"last_name\":\"Cooper\"}";
        response = given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/contacts")
                .andReturn();
        jsonPath = new JsonPath(response.asString());
        Long aliceId = jsonPath.getLong("id");

        requestBody = "{\"first_name\":\"Claire\", \"last_name\":\"Salas\"}";
        response = given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/contacts")
                .andReturn();
        jsonPath = new JsonPath(response.asString());
        Long claireId = jsonPath.getLong("id");

        requestBody = "{\"phone\":\"+81 555 111 2222 ext. 45\"}";
        given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/contacts/" + aliceId + "/entries")
                .then().assertThat().statusCode(201).and().body("id", notNullValue());

        given()
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/contacts/" + claireId + "/entries")
                .then().assertThat().statusCode(201).and().body("id", notNullValue());
    }


}
