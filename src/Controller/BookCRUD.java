package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Entity.Book;

public class BookCRUD {
    private Connection con;
    int budget = 100;

    public BookCRUD(Connection con) {
        this.con = con;
    }

    public BookCRUD() {
        // TODO Auto-generated constructor stub
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM books");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int book_id = rs.getInt("book_id");
                String title = rs.getString("book_title");
                int authorId = rs.getInt("author_id");
                int genreId = rs.getInt("genre_id");
                java.sql.Date sqlPublicationDate = rs.getDate("publication_date");
                java.time.LocalDate publicationDate = sqlPublicationDate.toLocalDate();
                boolean deleted = rs.getBoolean("deleted");
                
                Book book = new Book(book_id, title, publicationDate, authorId, genreId, deleted);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
        }
        return books;
    }

    public Book getBookById(int bookId) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Book book = null;
        try {
            stmt = con.prepareStatement("SELECT * FROM books WHERE book_id = ?");
            stmt.setInt(1, bookId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int book_id = rs.getInt("book_id");
                String title = rs.getString("book_title");
                int authorId = rs.getInt("author_id");
                int genreId = rs.getInt("genre_id");
                java.sql.Date sqlPublicationDate = rs.getDate("publication_date");
                java.time.LocalDate publicationDate = sqlPublicationDate.toLocalDate();
                boolean deleted = rs.getBoolean("deleted");
                
                book = new Book(book_id, title, publicationDate, authorId, genreId, deleted);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
        return book;
    }

    public String addBook(Book book) {
        try {
            if (bookTitleExists(book.getBookTitle())) {
                return "Book with Title " + book.getBookTitle() + " alredy  exist.";
            }
            if (!authorExists(book.getAuthorId())) {
                return "Author with ID " + book.getAuthorId() + " does not exist.";
            }

            if (!genreExists(book.getGenreId())) {
                return "Genre with ID " + book.getGenreId() + " does not exist.";
            }
            if (book.getPublicationDate().isAfter(LocalDate.now())) {
                return "Publication date cannot be in the future";
            }

            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO books (book_title, publication_date, author_id, genre_id) VALUES (?, ?, ?, ?)");
            stmt.setString(1, book.getBookTitle());
            stmt.setDate(2, java.sql.Date.valueOf(book.getPublicationDate()));
            stmt.setInt(3, book.getAuthorId());
            stmt.setInt(4, book.getGenreId());
           
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return null;
            } else {
                return "Failed to add book.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return "Database error occurred.";
        }
    }

    public boolean authorExists(int authorId) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT auth_id FROM authors WHERE auth_id = ?");
        stmt.setInt(1, authorId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public boolean bookExists(int bookId) {

        PreparedStatement stmt;
        try {
            stmt = con.prepareStatement("SELECT book_id FROM books WHERE book_id = ?");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean bookTitleExists(String booktitle) {

        PreparedStatement stmt;
        try {
            stmt = con.prepareStatement("SELECT book_title FROM books WHERE book_title = ?");
            stmt.setString(1, booktitle);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean genreExists(int genreId) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT genre_id FROM genres WHERE genre_id = ?");
        stmt.setInt(1, genreId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public String updateBook(Book book) {
        try {
            if (!authorExists(book.getAuthorId())) {
                return "Author with ID " + book.getAuthorId() + " does not exist.";
            }

            if (!genreExists(book.getGenreId())) {
                return "Genre with ID " + book.getGenreId() + " does not exist.";
            }
            if (book.getPublicationDate().isAfter(LocalDate.now())) {
                return "Publication date cannot be in the future";
            }
            if (bookTitleExists(book.getBookTitle())) {
                return "Book with Title " + book.getBookTitle() + " alredy  exists.";
            }
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE books SET book_title=?, publication_date=?, author_id=?, genre_id=? WHERE book_id=?");
            stmt.setString(1, book.getBookTitle());
            stmt.setDate(2, java.sql.Date.valueOf(book.getPublicationDate()));
            stmt.setInt(3, book.getAuthorId());
            stmt.setInt(4, book.getGenreId());
            stmt.setInt(5, book.getBookId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return null;
            } else {
                return "Failed to add book.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return "DataBase error Occured";
        }
    }

    public boolean deleteBook(int bookId) {
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM books WHERE book_id=?");
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return false;

        }
    }

    public boolean softdeleteBook(int bookId) {
        try {
            PreparedStatement stmt = con.prepareStatement("update books set is_delete=True WHERE book_id=?");
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return false;

        }
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

    // // repair

    // public List<Book> getRepairBook() {
    //     List<Book> books = new ArrayList<>();
    //     try {
    //         String query = "SELECT * FROM books WHERE damage_id IS NOT NULL";
    //         PreparedStatement stmt = con.prepareStatement(query);

    //         ResultSet rs = stmt.executeQuery();
    //         while (rs.next()) {
    //             int book_id = rs.getInt("book_id");
    //             String title = rs.getString("book_title");
    //             int authorId = rs.getInt("author_id");
    //             int genreId = rs.getInt("genre_id");
    //             LocalDate publicationDate = rs.getDate("publication_date").toLocalDate();
    //             boolean deleted = rs.getBoolean("deleted");
    //             String repairStatus = rs.getString("repaire_status");
    //             int damageId = rs.getInt("damage_id");

    //             // Fetch damage details from damage table
    //             Damages damage = getDamageById(damageId);

    //             Book book = new Book(book_id, title, publicationDate, authorId, genreId, deleted, repairStatus, damage);
    //             books.add(book);
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return books;
    // }

    // public Damages getDamageById(int damageId) {
    //     Damages damage = null;
    //     try {
    //         PreparedStatement stmt = con.prepareStatement("SELECT * FROM damages WHERE damage_id = ?");
    //         stmt.setInt(1, damageId);
    //         ResultSet rs = stmt.executeQuery();
    //         if (rs.next()) {
    //             int id = rs.getInt("damage_id");
    //             String type = rs.getString("damage_type");
    //             int severity = rs.getInt("severity");
    //             double repairCost = rs.getDouble("repair_cost");
    //             damage = new Damages(id, type, severity, repairCost);
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return damage;
    // }

    // public List<Book> manageRepair() throws SQLException {
    //     List<Book> books = new ArrayList<>();
    //     List<Book> booksToAdd = new ArrayList<>();
    //     books = sortBooks(getRepairBook());

    //     int totalCost = 0;
    //     for (Book book : books) {
    //         if (!book.getRepairStatus().equals("Completed")) {
    //             double repairCost = book.getDamage().getRepairCost();
    //             totalCost += repairCost;
    //             if (totalCost <= budget) {
    //                 book.setRepairStatus("Completed");
    //                 booksToAdd.add(book);
    //             } else {
    //                 book.setRepairStatus("In Progress");
    //                 booksToAdd.add(book);
    //             }
    //         }
    //     }
    //     return booksToAdd;
    // }

    // public int getSeverity(Damages damage) {
    //     return damage.getSeverity();
    // }

    // public double setPrice(Damages damage) {
    //     return damage.getRepairCost();
    // }

    // public List<Book> sortBooks(List<Book> books) throws SQLException {
    //     books.sort((b1, b2) -> {
    //         return getSeverity(b2.getDamage()) - getSeverity(b1.getDamage());
    //     });
    //     return books;
    // }

    // public void updateDataStatus(int bookId, String repair_status) {
    //     try {
    //         String q1 = "UPDATE books SET repaire_status = ? WHERE book_id = ?";
    //         PreparedStatement stmnt1 = con.prepareStatement(q1);
    //         stmnt1.setString(1, repair_status);
    //         stmnt1.setInt(2, bookId);
    //         stmnt1.executeUpdate();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }
    // public void updateStatus(List<Book> books) {
    //     for (Book book : books) {
    //         updateDataStatus(book.getBookId(), book.getRepairStatus());
    //     }
    // }
}
