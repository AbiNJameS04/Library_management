package Service;


 
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import Api.App;
import Controller.BookCRUD;
import Entity.Book;
 

 
public class BookServiceImpli implements BookService {
    App a=new App();
    @SuppressWarnings("unused")
    private BookCRUD books;
    BookCRUD bookRepo=new BookCRUD(a.DbConnection());
 
    public BookServiceImpli(Connection con) {
        this.books=new BookCRUD(a.DbConnection());
    }

    @Override
    public List<Book> getAllBooks() throws SQLException {
        return bookRepo.getAllBooks();
    }
 
    @Override
    public Book getBookById(int bookId) throws SQLException {
        return bookRepo.getBookById(bookId);
    }
 
    @Override
    public String addBook(Book book) throws SQLException {
        return bookRepo.addBook(book);
    }
 
    @Override
    public String updateBook(Book book) throws SQLException {
        return bookRepo.updateBook(book);
    }
 
    @Override
    public  boolean deleteBook(int bookId) throws SQLException {
        return bookRepo.deleteBook(bookId);
    }

   
   
}
 