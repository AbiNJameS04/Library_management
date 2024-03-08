package Junit;
import org.junit.Test;
import Api.App;
import Controller.BookCRUD;
import Entity.Book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;    
    
public class BookHandlerTest {

    @Test
    public void testGetDatabyId()
    {
        App app=new App();
       BookCRUD bookrepo=new BookCRUD(app.DbConnection());
       Book book=new Book();
       String dateString = "2000-02-20";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
       Book book1=new Book(1,"It",localDate,1,1,false);
       try{
       book=bookrepo.getBookById(1);
       }catch(SQLException e)
       {
        e.printStackTrace();
       }
         assertEquals(book1,book);
    }

  
    @Test
    public void testGetAllBooks() throws SQLException {
        App app=new App();
        BookCRUD bookrepo=new BookCRUD(app.DbConnection());
       
        List<Book> books = bookrepo.getAllBooks();

        assertEquals(17, books.size(), "Size of books should match");

       
    }


    @Test
    public void testAddBook() throws SQLException {
        
        App app=new App();
        BookCRUD bookrepo=new BookCRUD(app.DbConnection());
        List<Book> initialBooks = bookrepo.getAllBooks();
        int beforeadding = initialBooks.size();

        LocalDate date2 = LocalDate.parse("1977-01-28", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Book books = new Book(2, "The Lord", date2, 2, 3, false);
        bookrepo.addBook(books);

       
        List<Book> updatedBooks = bookrepo.getAllBooks();
        int updatedSize = updatedBooks.size();

        assertEquals(beforeadding + 1, updatedSize, "Size of books increase by 1 after adding");

    
    }

    @Test
    public void testDeleteBookById() throws SQLException {
        
        App app=new App();
        BookCRUD bookrepo=new BookCRUD(app.DbConnection());
        List<Book> initialBooks = bookrepo.getAllBooks();
        int initialSize = initialBooks.size();

       
        int bookIdToDelete = 1;
        bookrepo.deleteBook(bookIdToDelete);
        List<Book> updatedBooks = bookrepo.getAllBooks();
        int updatedSize = updatedBooks.size();

        assertEquals(initialSize - 1, updatedSize, "Size of books decrease by 1 after deletion");

        for (Book book : updatedBooks) {
            assertNotEquals(bookIdToDelete, book.getBookId(), "Deleted book should not be present in updated list");
        }

        Book deletedBook = bookrepo.getBookById(bookIdToDelete);
        assertNull(deletedBook, "Deleted book should not be found in the database");
    }


   

    
}
