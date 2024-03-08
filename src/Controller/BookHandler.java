package Controller;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import Entity.Book;
import Service.BookServiceImpli;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookHandler implements HttpHandler {

    private BookCRUD bookCRUD;
    private BookServiceImpli service;

    public BookHandler(Connection con) {
        this.bookCRUD = new BookCRUD(con);
        this.service = new BookServiceImpli(con);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {

            String[] path = exchange.getRequestURI().getPath().split("/");
            if (path.length == 2 || path.length == 3) {
                if (path.length == 2) {
                    handleGetRequest(exchange);
                } else {
                    handleGetRequestById(exchange);

                }
            }

        } else if (method.equals("POST")) {
           handlePostRequest(exchange);
        }

        else if (method.equals("PUT")) {
            handlePutRequest(exchange);
        } else if (method.equals("DELETE")) {

            handleDeleteRequest(exchange);
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
        }
    }

    public void handleGetRequest(HttpExchange exchange) throws IOException {

        try {
            List<Book> books = service.getAllBooks();
            JSONArray jsonArray = new JSONArray();
            for (Book book : books) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", book.getBookId());
                jsonObject.put("title", book.getBookTitle());
                jsonObject.put("authorId", book.getAuthorId());
                jsonObject.put("genreId", book.getGenreId());
                jsonObject.put("publicationDate", book.getPublicationDate());
                jsonObject.put("deleted", book.getIsDelete());
              
                jsonArray.put(jsonObject);
            }
            if (books.isEmpty()) {
                sendJsonResponse(exchange, 404, "No books found");
            } else {
                sendJsonResponse(exchange, 200, jsonArray.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage = "Error parsing request body";
            sendResponse(exchange, 400, errorMessage);
        }
    }

    public void handlePostRequest(HttpExchange exchange) throws IOException {
        try {
            String requestBody = Utils.convertInputStreamToString(exchange.getRequestBody());
            JSONObject json = new JSONObject(requestBody);
            if (!json.has("title") || json.isNull("title") ||
                    !json.has("authorId") || json.isNull("authorId") ||
                    !json.has("genreId") || json.isNull("genreId") ||
                    !json.has("publicationDate") || json.isNull("publicationDate")) {
                String errorMessage = "Missing required fields in request body";
                sendResponse(exchange, 400, errorMessage);
                return;
            }
            String title = json.getString("title");
            int authorId = json.getInt("authorId");
            int genreId = json.getInt("genreId");
            String publicationDateStr = json.getString("publicationDate");
            LocalDate publicationDate = LocalDate.parse(publicationDateStr);

            Book book = new Book(title, publicationDate, authorId, genreId);
            String addBookResult = service.addBook(book);

            if (addBookResult == null) {
                String response = "Book created successfully";
                sendResponse(exchange, 201, response);
            } else {
                sendResponse(exchange, 500, addBookResult);
            }
        } catch (JSONException | IOException | SQLException e) {
            e.printStackTrace();
            String errorMessage = "Error parsing request body";
            sendResponse(exchange, 400, errorMessage);
        }
    }

   

    public void handlePutRequest(HttpExchange exchange) throws IOException {
        try {
            int bookId = Integer.parseInt(exchange.getRequestURI().getPath().replaceAll("[^\\d]", ""));

            if (!bookCRUD.bookExists(bookId)) {
                String errorMessage = "Book with ID " + bookId + " does not exist";
                sendResponse(exchange, 404, errorMessage);

            }

            String requestBody = Utils.convertInputStreamToString(exchange.getRequestBody());
            JSONObject json = new JSONObject(requestBody);
            if (!json.has("title") || json.isNull("title") ||
                    !json.has("authorId") || json.isNull("authorId") ||
                    !json.has("genreId") || json.isNull("genreId") ||
                    !json.has("publicationDate") || json.isNull("publicationDate")) {
                String errorMessage = "Missing required fields in request body";
                sendResponse(exchange, 400, errorMessage);
                return;
            }

            String title = json.getString("title");
            int authorId = json.getInt("authorId");
            int genreId = json.getInt("genreId");
            String publicationDate = json.getString("publicationDate");

            Book book = new Book(bookId, title, LocalDate.parse(publicationDate), authorId, genreId);

            String bookUpdated = service.updateBook(book);

            if (bookUpdated == null) {
                String response = "Book updated successfully";
                sendResponse(exchange, 200, response);
            } else {

                sendResponse(exchange, 500, bookUpdated);
            }
        } catch (NumberFormatException e) {
            String errorMessage = "Invalid book ID format in API";
            sendResponse(exchange, 400, errorMessage);
        } catch (JSONException | IOException | SQLException e) {
            e.printStackTrace();
            String errorMessage = "Error parsing request body";
            sendResponse(exchange, 400, errorMessage);
        }
    }

    public void handleDeleteRequest(HttpExchange exchange) throws IOException {

        try {
            int bookId = Integer.parseInt(exchange.getRequestURI().getPath().replaceAll("[^\\d]", ""));
            if (!bookCRUD.bookExists(bookId)) {
                String errorMessage = "Book with ID " + bookId + " does not exist";
                sendResponse(exchange, 404, errorMessage);

            }
            boolean bookDeleted = service.deleteBook(bookId);
            if (bookDeleted) {
                String response = "Book deleted successfully";
                sendResponse(exchange, 200, response);
            } else {
                String errorMessage = "Failed to delete book";
                sendResponse(exchange, 500, errorMessage);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Error invalid  id");
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Error deleting book from the database");
        }

    }

    public void handleGetRequestById(HttpExchange exchange) throws IOException {
        try {
            int bookId = Integer.parseInt(exchange.getRequestURI().getPath().replaceAll("[^\\d]", ""));

            Book book = service.getBookById(bookId);
            if (book == null) {
                sendResponse(exchange, 404, "Book with id " + bookId + ",not found");
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", book.getBookId());
                jsonObject.put("title", book.getBookTitle());
                jsonObject.put("authorId", book.getAuthorId());
                jsonObject.put("genreId", book.getGenreId());
                jsonObject.put("publicationDate", book.getPublicationDate().toString());
                jsonObject.put("deleted", book.getIsDelete());
                jsonObject.put("repaireStatus", book.getRepairStatus());
                jsonObject.put("damages", book.getDamages());

                sendJsonResponse(exchange, 200, jsonObject.toString());
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "Invalid book ID");
        } catch (SQLException e) {
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    // public void handlePostRequestRepair(HttpExchange exchange) throws IOException, SQLException {

    //     try {
    //         List<Book> books = new ArrayList<>();
    //         books = bookCRUD.manageRepair();
    //         bookCRUD.updateStatus(books);

    //         String response = "repair status updated.";
    //         sendResponse(exchange, 201, response);

    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         String errorMessage = "Error creating book in the database";
    //         sendResponse(exchange, 400, errorMessage);
    //     }
    // }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        if (headers != null) {
            headers.set("Content-Type", "application/json");
        }
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }

}

class Utils {
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}