package Controller;

import Entity.Repaire;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RepaireHandler implements HttpHandler {
    private RepaireCRUD repaireCRUD;

    public RepaireHandler(RepaireCRUD repaireCRUD) {
        this.repaireCRUD = repaireCRUD;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetRequest(exchange);
                break;
            case "POST":
                handlepost(exchange);
                break;
            case "PUT":
                handlePutRequest(exchange);
                break;
            case "DELETE":
                handleDeleteRequest(exchange);
                break;
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {

        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 2 || path.length == 3) {
            if (path.length == 2) {
                handleGetAllRepaireRequest(exchange);
            } else {
                handleGetRepaireByIdRequest(exchange);

            }
        }

    }

    private void handlepost(HttpExchange exchange) throws IOException {

        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 2 || path.length == 3) {
            if (path.length == 2) {
                handlePostRequest(exchange);
            } else {
                handlePostRequestRepair(exchange);
            }
        }

    }

    private void handleGetAllRepaireRequest(HttpExchange exchange) throws IOException {
        List<Repaire> repaires = repaireCRUD.getAllRepaire();
        JSONArray jsonArray = new JSONArray();
        for (Repaire repaire : repaires) {
            JSONObject json = new JSONObject();
            json.put("rep_id", repaire.getRep_id());
            json.put("book_id", repaire.getBook_id());
            json.put("damage", repaire.getDamage());
            json.put("repaire_date", repaire.getRepaire_date().toString());
            json.put("repaireStatus", repaire.getRepaireStatus());
            jsonArray.put(json);
        }
        sendResponse(exchange, 200, jsonArray.toString());
    }

    private void handleGetRepaireByIdRequest(HttpExchange exchange) throws IOException {
        int rep_id = Integer.parseInt(exchange.getRequestURI().getPath().replaceAll("[^\\d]", ""));

        Repaire repaire = repaireCRUD.getRepaireById(rep_id);
        if (repaire == null) {
            sendResponse(exchange, 404, "Reapaire data with id " + rep_id + ",not found");
        } else {
            JSONObject json = new JSONObject();
            json.put("rep_id", repaire.getRep_id());
            json.put("book_id", repaire.getBook_id());
            json.put("damage", repaire.getDamage());
            json.put("repaire_date", repaire.getRepaire_date().toString());
            json.put("repaireStatus", repaire.getRepaireStatus());
            sendResponse(exchange, 200, json.toString());
        }

    }

    BookCRUD b = new BookCRUD();

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        try {
            InputStream requestBody = exchange.getRequestBody();
            JSONObject jsonRequest = new JSONObject(new String(requestBody.readAllBytes()));

            if (!jsonRequest.has("book_id") || !jsonRequest.has("damage")) {
                sendResponse(exchange, 400, "Missing required fields");
                return;
            }

            int book_id = jsonRequest.getInt("book_id");
            String damage = jsonRequest.getString("damage");
            String repaireStatus = "Not Started";

            if (damage.equalsIgnoreCase("no damage")) {
                repaireStatus = "No Need";
            }

            Repaire repaire = new Repaire(0, book_id, damage, LocalDate.now(), repaireStatus);
            String result = repaireCRUD.addRepaire(repaire);

            if (result == null) {
                String response = "Repaire record added successfully";
                sendResponse(exchange, 201, response);
            } else {
                sendResponse(exchange, 500, result);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            String errorMessage = "Error parsing request body";
            sendResponse(exchange, 400, errorMessage);
        }
        sendResponse(exchange, 201, "Repaire record added");
    }

    private void handlePutRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] paths = path.split("/");
        if (paths.length == 3) {
            int rep_id = Integer.parseInt(paths[2]);

            InputStream requestBody = exchange.getRequestBody();
            JSONObject jsonRequest = new JSONObject(new String(requestBody.readAllBytes()));

            if (!jsonRequest.has("book_id") || !jsonRequest.has("damage") || !jsonRequest.has("repaireStatus")) {
                sendResponse(exchange, 400, "Missing required fields");
                return;
            }

            int book_id = jsonRequest.getInt("book_id");
            String damage = jsonRequest.getString("damage");
            String repaireStatus = jsonRequest.getString("repaireStatus");

            Repaire repaire = new Repaire(rep_id, book_id, damage, LocalDate.now(), repaireStatus);
            repaireCRUD.updateRepaire(repaire);
            sendResponse(exchange, 200, "Repaire record updated");
        } else {
            sendResponse(exchange, 400, "Bad Request");
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] paths = path.split("/");
        if (paths.length == 3) {
            int rep_id = Integer.parseInt(paths[2]);
            repaireCRUD.deleteRepaire(rep_id);
            sendResponse(exchange, 200, "Repaire record deleted");
        } else {
            sendResponse(exchange, 400, "Bad Request");
        }
    }

    private void handlePostRequestRepair(HttpExchange exchange) throws IOException {
        try {

            List<Repaire> updatedRepaires = repaireCRUD.manageRepair();

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("message", "Repaire status updated successfully");
            jsonResponse.put("updated_repaires", updatedRepaires);

          
            sendResponse(exchange, 200, jsonResponse.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
