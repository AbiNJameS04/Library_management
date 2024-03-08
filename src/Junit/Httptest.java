package Junit;

import static org.junit.Assert.assertEquals;


import java.io.IOException;

import java.net.URI;
import java.sql.SQLException;
 
import org.junit.Test;

import Api.App;
import Controller.BookHandler;
 
public class Httptest {
   
    @Test
    public void handleGetRequestByIdTest() throws IOException,SQLException{
    App a=new App();
    TestHttpExchange exchange=new TestHttpExchange();
    exchange.setRequestMethod("GET");
    exchange.setRequestHeader(URI.create("/books/1"));
    BookHandler bookHandler=new BookHandler(a.DbConnection());
    bookHandler.handleGetRequestById(exchange);
    String expectedResponse = "{\"genreId\":1,\"deleted\":false,\"id\":1,\"title\":\"It\",\"authorId\":1,\"publicationDate\":\"2000-02-20\"}";
    assertEquals(expectedResponse, exchange.getResponseBody().toString());
    assertEquals(200,exchange.getResponseCode());
    System.out.println(exchange.getResponseCode());
    }

    @Test
    public void handleGetRequestTest() throws IOException,SQLException{
    App a=new App();
    TestHttpExchange exchange=new TestHttpExchange();
    exchange.setRequestMethod("GET");
    exchange.setRequestHeader(URI.create("/books"));
    BookHandler bookHandler=new BookHandler(a.DbConnection());
    bookHandler.handleGetRequest(exchange);
    assertEquals(200,exchange.getResponseCode());
    }



    @Test
    public void handleDeleteRequestTest() throws IOException,SQLException{
        App a=new App();
    TestHttpExchange exchange=new TestHttpExchange();
    exchange.setRequestMethod("DELETE");
    exchange.setRequestHeader(URI.create("/books/19"));
    BookHandler bookHandler=new BookHandler(a.DbConnection());
    bookHandler.handleDeleteRequest(exchange);
    assertEquals(200,exchange.getResponseCode());
    }

    @Test
    public void handlePostRequestTest() throws IOException,SQLException{
    App a=new App();
    TestHttpExchange exchange=new TestHttpExchange();
    exchange.setRequestMethod("POST");
    exchange.setRequestHeader(URI.create("/books"));
    exchange.setRequestMethod("POST");
    String requestBody = "{ \"title\": \"Sidar\", \"authorId\": 18, \"genreId\": 1, \"publicationDate\": \"2022-01-01\" }";
    exchange.setRequestBody(requestBody);
    BookHandler bookHandler=new BookHandler(a.DbConnection());
    bookHandler.handlePostRequest(exchange);
    assertEquals(201,exchange.getResponseCode());
    }


    @Test
    public void handlePutRequestTest() throws IOException, SQLException {
    App a = new App();
    TestHttpExchange exchange = new TestHttpExchange();
    exchange.setRequestMethod("PUT");
    exchange.setRequestHeader(URI.create("/books/21"));
    String requestBody = "{ \"title\": \"sidar\", \"authorId\": 2, \"genreId\": 2, \"publicationDate\": \"2023-02-01\" }";
    exchange.setRequestBody(requestBody);
    
    BookHandler bookHandler = new BookHandler(a.DbConnection());
    bookHandler.handlePutRequest(exchange);

    assertEquals(200, exchange.getResponseCode());
}



@Test
    public void handlePostRequestInvalidTest() throws IOException,SQLException{
    App a=new App();
    TestHttpExchange exchange=new TestHttpExchange();
    exchange.setRequestMethod("POST");
    exchange.setRequestHeader(URI.create("/books"));
    exchange.setRequestMethod("POST");
    String requestBody = "{ \"title\": \"Sidar\", \"authorId\": 18, \"genreId\": 1, \"publicationDate\": \"2022-01-01\" }";
    exchange.setRequestBody(requestBody);
    BookHandler bookHandler=new BookHandler(a.DbConnection());
    bookHandler.handlePostRequest(exchange);
    assertEquals(500,exchange.getResponseCode());
    }



@Test
public void handleGetRequestByIdInvalidDataTest() throws IOException,SQLException{
App a=new App();
TestHttpExchange exchange=new TestHttpExchange();
exchange.setRequestMethod("GET");
exchange.setRequestHeader(URI.create("/books/66"));
BookHandler bookHandler=new BookHandler(a.DbConnection());
bookHandler.handleGetRequestById(exchange);
assertEquals(404,exchange.getResponseCode());
}

}

