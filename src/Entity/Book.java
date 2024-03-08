package Entity;
import java.time.LocalDate;

public class Book {
    private int bookId;
    private String bookTitle;
    private LocalDate publicationDate;
    private int authorId;
    private int genreId;
    private Author author;
    private Genere genre;
    private boolean is_delete;
    private int damages; 
    private String repairStatus;
    private Damages damage;

    public Book(int bookId, String bookTitle, LocalDate publicationDate, int authorId, int genreId, boolean is_delete,
                String repairStatus, Damages damage) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.publicationDate = publicationDate;
        this.authorId = authorId;
        this.genreId = genreId;
        this.is_delete = is_delete;
        this.repairStatus = repairStatus;
        this.damage = damage;
    }


    public int getBookId() {
        return bookId;
    }

    public boolean getIsDelete() {
        return is_delete;
    }

    public String getBookTitle() {
        return bookTitle;
    }
    public int getDamages() {
        return damages;
    }
    public String getRepairStatus() {
        return repairStatus;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getGenreId() {
        return genreId;
    }
    public int getDamageId() {
        return damages;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    public void setDamages(int damages) {
        this.damages = damages;
    }
    public Damages getDamage() {
        return damage;
    }

    public void setDamage(Damages damage) {
        this.damage = damage;
    }

    public Book(String bookTitle, LocalDate publicationDate, int authorId, int genreId, int damages) {
        this.bookTitle = bookTitle;
        this.publicationDate = publicationDate;
        this.authorId = authorId;
        this.genreId = genreId;
        this.damages = damages;
    }


    public void setIsDelete(boolean is_delete) {
        this.is_delete = is_delete;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    public void setDamageID(int damages) {
        this.damages = damages;
    }

    public void setRepairStatus(String repairStatus) {
        this.repairStatus = repairStatus;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    

    public Book(String bookTitle, LocalDate publicationDate, int authorId, int genreId, int damages,
            String repairStatus) {
        this.bookTitle = bookTitle;
        this.publicationDate = publicationDate;
        this.authorId = authorId;
        this.genreId = genreId;
        this.damages = damages;
        this.repairStatus = repairStatus;
    }

   
    public Book(int bookId, String bookTitle, LocalDate publicationDate, int authorId, int genreId, boolean is_delete) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.publicationDate = publicationDate;
        this.authorId = authorId;
        this.genreId = genreId;
        this.is_delete = is_delete;
        
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public Book(int bookId, String bookTitle, LocalDate publicationDate, int authorId, int genreId) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.publicationDate = publicationDate;
        this.authorId = authorId;
        this.genreId = genreId;
    }

    public Book(String bookTitle, LocalDate publicationDate, int authorId, int genreId) {
        this.bookTitle = bookTitle;
        this.publicationDate = publicationDate;
        this.authorId = authorId;
        this.genreId = genreId;
    }

    
   

    @Override
    public String toString() {
        return "Book [bookId=" + bookId + ", bookTitle=" + bookTitle + ", publicationDate=" + publicationDate
                + ", authorId=" + authorId + ", genreId=" + genreId + ", is_delete=" + is_delete + ", damages="
                + damages + ", repairStatus=" + repairStatus + "]";
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Genere getGenre() {
        return genre;
    }

    public void setGenre(Genere genre) {
        this.genre = genre;
    }

    public Book(int bookId, String bookTitle, LocalDate publicationDate, Author author, Genere genre) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.publicationDate = publicationDate;
        this.author = author;
        this.genre = genre;
    }

    public Book() {
        //TODO Auto-generated constructor stub
    }

}
