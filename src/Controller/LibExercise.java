package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class LibExercise {
    private Connection con;

    public LibExercise(Connection con) {
        this.con = con;
    }

    public List<String> getBooksByAuthor(String authorName) {
        List<String> booksByAuthor = new ArrayList<>();
        try {
            String query = "SELECT book_title, publication_date, genre_name " +
                    "FROM books " +
                    "JOIN authors ON books.author_id = authors.auth_id " +
                    "JOIN genres ON books.genre_id = genres.genre_id " +
                    "WHERE authors.author_name = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, authorName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bookTitle = rs.getString("book_title");
                String publicationDate = rs.getString("publication_date");
                String genreName = rs.getString("genre_name");
                String bookDetails = String.format("%-30s |%-15s |%s", bookTitle, publicationDate, genreName);
                booksByAuthor.add(bookDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booksByAuthor;
    }

    public List<String> getBooksAfterYear(int year) {
        List<String> books = new ArrayList<>();
        try {
            String query = "SELECT books.book_title, books.publication_date, authors.author_name, genres.genre_name " +
                    "FROM books " +
                    "JOIN authors ON books.author_id = authors.auth_id " +
                    "JOIN genres ON books.genre_id = genres.genre_id " +
                    "WHERE year(books.publication_date) > ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bookTitle = rs.getString("book_title");
                String publicationDate = rs.getString("publication_date");
                String authorName = rs.getString("author_name");
                String genreName = rs.getString("genre_name");
                String bookDetails = String.format("%-30s |%-15s |%-20s |%s", bookTitle, publicationDate, authorName,
                        genreName);
                books.add(bookDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<String> getGenreBookCounts() {
        List<String> genreCounts = new ArrayList<>();
        try {
            String query = "SELECT genres.genre_name, COUNT(*) AS Total_books " +
                    "FROM books " +
                    "JOIN genres ON books.genre_id = genres.genre_id " +
                    "GROUP BY genres.genre_name";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String genreName = rs.getString("genre_name");
                int totalBooks = rs.getInt("Total_books");
                String genreCountString = String.format("%-20s | %-10d", genreName, totalBooks);
                genreCounts.add(genreCountString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genreCounts;
    }

    public List<String> getBookAvailability() {
        List<String> bookAvailable = new ArrayList<>();
        try {
            String query = "SELECT b.book_title, a.author_name, g.genre_name, " +
                    "IF(br.borrow_date IS NULL, 'Available', 'Not Available') AS Availability " +
                    "FROM books b " +
                    "INNER JOIN authors a ON b.author_id = a.auth_id " +
                    "INNER JOIN genres g ON b.genre_id = g.genre_id " +
                    "LEFT OUTER JOIN borrowers br ON b.book_id = br.book_id";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bookTitle = rs.getString("book_title");
                String authorName = rs.getString("author_name");
                String genreName = rs.getString("genre_name");
                String availability = rs.getString("Availability");
                String bookDetails = String.format("%-30s |%-20s |%-15s |%s", bookTitle, authorName, genreName,
                        availability);
                bookAvailable.add(bookDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookAvailable;
    }

}
