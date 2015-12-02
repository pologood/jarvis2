package com.mogujie.jarvis.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;

/**
 * Created by muming on 15/12/1.
 */
public class AppRestTest {

    private String baseUrl ="http://127.0.0.1:8080";


    @Test
    public void jobAdd() throws UnirestException {

        HttpResponse<JsonNode> jsonResponse = Unirest.post(baseUrl + "/job/add")
                .header("accept", "application/json")
                .queryString("apiKey", "123")
                .field("parameter", "value")
                .field("foo", "bar")
                .asJson();


//        // Given
//        HttpUriRequest request = new HttpGet( "https://api.github.com/users/eugenp" );
//
//        // When
//        HttpResponse response = HttpClientBuilder.create().build().execute( request );
//
//        // Then
//        GitHubUser resource = RetrieveUtil.retrieveResourceFromResponse(
//                response, GitHubUser.class);
//        assertThat( "eugenp", Matchers.is(resource.getLogin()) );
    }





}
