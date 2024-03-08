package Service;


 
import java.sql.SQLException;
import java.util.List;

import Entity.Book;
 
public interface BookService {
     public List<Book> getAllBooks() throws SQLException;
     
     public Book getBookById(int bookId) throws SQLException;
     public String addBook(Book book) throws SQLException;
     public String updateBook(Book book) throws SQLException;
     public boolean deleteBook(int bookId) throws SQLException;
}