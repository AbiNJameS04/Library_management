package Entity;
public class Genere {

    private int genreId;
    private String genereName;

    public int getGenreId() {
        return genreId;
    }
    public String getGenereName() {
        return genereName;
    }
    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }
    public void setGenereName(String genereName) {
        this.genereName = genereName;
    }
    public Genere(int genreId, String genereName) {
        this.genreId = genreId;
        this.genereName = genereName;
    }
    public Genere(String genereName) {
        this.genereName = genereName;
    }
    @Override
    public String toString() {
        return "Genere [genreId=" + genreId + ", genereName=" + genereName + "]";
    }
    

    

}
