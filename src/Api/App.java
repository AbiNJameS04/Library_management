package Api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import Controller.BookCRUD;
import Controller.LibExercise;
import Entity.Author;
import Entity.Book;

public class App {

    private Connection con;

    public Connection DbConnection() {
        try {
             ResourceBundle rd = ResourceBundle.getBundle("resources.system",new Locale("en_US"));
             String loadDriver = rd.getString("driver");
             String url = rd.getString("url");
            String username = rd.getString("username");
            Class.forName(loadDriver);
            con = DriverManager.getConnection(url, username, "");
            System.out.println("Connected to the database!");
            return con;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error: MySQL JDBC Driver not found!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error: Failed to connect to the database!");
        }
        return con;
    }

    private void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Authors
    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM authors");
            while (rs.next()) {
                int authId = rs.getInt("auth_id");
                String authorName = rs.getString("author_name");
                String phone = rs.getString("phone");
                Author author = new Author(authId, authorName, phone);
                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();

        }
        return authors;
    }

    public Author getAuthorById(int authorId) {
        Author author = null;
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM authors WHERE auth_id = ?");
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String authorName = rs.getString("author_name");
                String phone = rs.getString("phone");
                author = new Author(authorId, authorName, phone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
        }
        return author;
    }

    public boolean addAuthor(Author author) {
        try {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO authors(author_name, phone) VALUES (?, ?)");
            stmt.setString(1, author.getAuthorName());
            stmt.setString(2, author.getPhone());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    public boolean updateAuthor(Author author) {
        try {
            PreparedStatement stmt = con.prepareStatement("update authors set author_name=?,phone=? where auth_id=?");
            stmt.setString(1, author.getAuthorName());
            stmt.setString(2, author.getPhone());
            stmt.setInt(3, author.getAuthId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    public boolean deleteAuthor(int authId) {
        try {
            if (!authorExists(authId)) {
                System.err.println("Author with ID " + authId + " does not exist.");
                return false;
            }
            PreparedStatement stmt = con.prepareStatement("DELETE FROM authors where auth_id=?");
            stmt.setInt(1, authId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    private boolean authorExists(int authorId) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT auth_id FROM authors WHERE auth_id = ?");
        stmt.setInt(1, authorId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private boolean authorNameExists(String name) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT auth_id FROM authors WHERE author_name = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public static void main(String[] args) throws SQLException, IOException {
        App app = new App();
        app.DbConnection();

        // HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        // App a = new App();
        // server.createContext("/books", new BookHandler(a.DbConnection()));
        // server.setExecutor(null);
        // server.start();
        // System.out.println("Server started on port 8000");

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nLIBRARY MANAGEMENT");
        System.out.println("--------------------------");
        while (true) {
            System.out.println("\nChoose an Option:");
            System.out.println("1. Authors");
            System.out.println("2. Books");
            System.out.println("3. Exercise");
            System.out.println("4. Exit");

            int tableOption = scanner.nextInt();
            scanner.nextLine();

            switch (tableOption) {
                case 1:
                    handleAuthorOperations(app, scanner);
                    break;
                case 2:
                    handleBookOperations(app, scanner);
                    break;
                case 3:
                    handleExcercises(app, scanner);
                    break;
                case 4:
                    System.out.println("Exited");
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void handleAuthorOperations(App app, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Author");
            System.out.println("2. Edit Author");
            System.out.println("3. Delete Author");
            System.out.println("4. View all Authors");
            System.out.println("5. Exit");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    // Add Author
                    System.out.println("Enter author name:");
                    String name = scanner.nextLine();
                    System.out.println("Enter phone number:");
                    String phone = scanner.nextLine();
                    Author newAuthor = new Author(name, phone);
                    if (app.addAuthor(newAuthor)) {
                        System.out.println("Author added successfully.");
                    } else {
                        System.out.println("Failed to add author.");
                    }
                    break;
                case 2:
                    // Edit Author
                    System.out.println("Enter author ID to edit:");
                    int editId = scanner.nextInt();
                    if (!app.authorExists(editId)) {
                        System.err.println("Author with ID " + editId + " does not exist.");
                        break;
                    }
                    scanner.nextLine();
                    System.out.println("Enter new author name:");
                    String newName = scanner.nextLine();
                    System.out.println("Enter new phone number:");
                    String newPhone = scanner.nextLine();
                    Author editAuthor = new Author(editId, newName, newPhone);
                    if (app.updateAuthor(editAuthor)) {
                        System.out.println("Author updated successfully.");
                    } else {
                        System.out.println("Failed to update author.");
                    }
                    break;
                case 3:
                    // Delete Author
                    System.out.println("Enter author ID to delete:");
                    int deleteId = scanner.nextInt();
                    if (app.deleteAuthor(deleteId)) {
                        System.out.println("Author deleted successfully.");
                    } else {
                        System.out.println("Failed to delete author.");
                    }
                    break;
                case 4:
                    // View all Authors
                    System.out.println("All authors:");
                    System.out.printf("%-10s %-20s %-15s%n", "Author ID", "Author Name", "Phone");
                    List<Author> allAuthors = app.getAllAuthors();
                    for (Author a : allAuthors) {
                        System.out.printf("%-10s %-20s %-15s%n", a.getAuthId(), a.getAuthorName(), a.getPhone());
                    }
                    break;
                case 5:
                    System.out.println("Exited");
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void handleBookOperations(App app, Scanner scanner) {

        BookCRUD bookc = new BookCRUD(app.DbConnection());
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add book");
            System.out.println("2. Edit book");
            System.out.println("3. Delete book");
            System.out.println("4. View all books");
            System.out.println("5. Book Repair");
            System.out.println("6. Exit");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    // Add book
                    System.out.println("Enter book title:");
                    String title = scanner.nextLine();
                    LocalDate publicationDate = null;
                    boolean validDate = false;
                    while (!validDate) {
                        System.out.println("Enter publication date (YYYY-MM-DD):");
                        String dateS = scanner.nextLine();
                        try {
                            publicationDate = LocalDate.parse(dateS);
                            validDate = true;
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format.");
                        }
                    }
                    System.out.println("Enter author ID:");
                    int authorId = scanner.nextInt();
                    System.out.println("Enter genre ID:");
                    int genreId = scanner.nextInt();
                    Book newBook = new Book(title, publicationDate, authorId, genreId);
                    if (bookc.addBook(newBook) == null) {
                        System.out.println("Book added successfully.");
                    } else {
                        System.out.println(bookc.addBook(newBook));
                    }
                    break;
                case 2:
                    // Edit book
                    System.out.println("Enter book ID to edit:");
                    int editId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter new book title:");
                    String newTitle = scanner.nextLine();
                    System.out.println("Enter new publication date (YYYY-MM-DD):");
                    String newDateStr = scanner.nextLine();
                    LocalDate newPublicationDate = LocalDate.parse(newDateStr);
                    System.out.println("Enter new author ID:");
                    int newAuthorId = scanner.nextInt();
                    System.out.println("Enter new genre ID:");
                    int newGenreId = scanner.nextInt();
                    Book editBook = new Book(editId, newTitle, newPublicationDate, newAuthorId, newGenreId);
                    if (bookc.updateBook(editBook) == null) {
                        System.out.println("Book updated successfully.");
                    } else {
                        System.out.println(bookc.updateBook(editBook));
                    }
                    break;
                case 3:
                    // Delete book
                    System.out.println("Enter book ID to delete:");
                    int deleteId = scanner.nextInt();
                    if (bookc.deleteBook(deleteId)) {
                        System.out.println("Book deleted successfully.");
                    } else {
                        System.out.println("Failed to delete book.");
                    }
                    break;
                case 4:
                    // View all books
                    System.out.println("All Books:");
                    System.out.printf("%-10s %-30s %-15s %-10s %-10s%n", "Book ID", "Book Title", "Publication Date",
                            "Author ID", "Genre ID");
                    List<Book> allBooks = bookc.getAllBooks();
                    for (Book b : allBooks) {
                        System.out.printf("%-10d %-30s %-15s %-10d %-10d%n", b.getBookId(), b.getBookTitle(),
                                b.getPublicationDate(), b.getAuthorId(), b.getGenreId());
                    }
                    break;
                // case 5:
                // System.out.print("Enter monthly budget: ");
                // double monthlyBudget;
                // try {
                // monthlyBudget = Double.parseDouble(scanner.nextLine());
                // } catch (NumberFormatException e) {
                // System.out.println("Invalid budget format.");
                // return;
                // }

                // bookc.processRepair(monthlyBudget);

                case 6:
                    System.out.println("Exited");
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void handleExcercises(App app, Scanner scanner) throws SQLException {
        LibExercise lib = new LibExercise(app.DbConnection());

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Get Books by Author");
            System.out.println("2. Get Books After a Certain Year");
            System.out.println("3. Get Genre Wise Book Counts");
            System.out.println("4. Get Book Availability");

            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nGet Books By Author Name");
                    System.out.print("Enter the author's name: ");
                    String authorName = scanner.nextLine();
                    if (!app.authorNameExists(authorName)) {
                        System.out.println("Auther Name not Exist in Database");
                        break;
                    }
                    List<String> booksByAuthor = lib.getBooksByAuthor(authorName);
                    System.out.println("Books by author " + authorName + ":");
                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.printf("%-30s | %-15s | %s\n", "Title", "Publication Date", "Genre");
                    System.out.println("-----------------------------------------------------------------------------");
                    for (String book : booksByAuthor) {
                        System.out.println(book);
                    }
                    break;
                case 2:
                    System.out.println("\nGet Books After a Certain Year");
                    System.out.print("Enter the year: ");
                    int year = scanner.nextInt();
                    List<String> booksAfterYear = lib.getBooksAfterYear(year);
                    System.out.println("Books published after " + year + ":");
                    System.out.println(
                            "--------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-30s | %-15s | %-20s | %s\n", "Title", "Publication Date", "Author", "Genre");
                    System.out.println(
                            "--------------------------------------------------------------------------------------------------------");
                    for (String book : booksAfterYear) {
                        System.out.println(book);
                    }
                    break;
                case 3:
                    System.out.println("\nGet Genre Wise Book Counts");
                    List<String> genreCounts = lib.getGenreBookCounts();
                    System.out.println("------------------------------------------------");
                    System.out.printf("%-20s | %-10s\n", "Genre Name", "Total Books");
                    System.out.println("------------------------------------------------");
                    for (String genreCount : genreCounts) {
                        System.out.println(genreCount);
                    }
                    break;
                case 4:
                    List<String> bookDetails = lib.getBookAvailability();
                    System.out.println("Book Details with Availability:");
                    System.out.println(
                            "--------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-30s | %-20s | %-15s | %s\n", "Title", "Author", "Genre", "Availability");
                    System.out.println(
                            "--------------------------------------------------------------------------------------------------------");
                    for (String details : bookDetails) {
                        System.out.println(details);
                    }

                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

}
