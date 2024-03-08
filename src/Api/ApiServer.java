package Api;

import com.sun.net.httpserver.HttpServer;

import Controller.BookHandler;
import Controller.RepaireCRUD;
import Controller.RepaireHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ApiServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        App a = new App();
        RepaireCRUD repaireCRUD = new RepaireCRUD();

        RepaireHandler repaireHandler = new RepaireHandler(repaireCRUD);
        server.createContext("/repaire", repaireHandler);
        server.createContext("/books", new BookHandler(a.DbConnection()));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8000");
    }

}