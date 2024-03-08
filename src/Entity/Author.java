package Entity;

public class Author {
    private int authId;
    private String authorName;
    private String phone;

    public Author( int authId,String authorName, String phone) {
        this.authId = authId;
        this.authorName = authorName;
        this.phone = phone;
    }

    public Author(String authorName, String phone) {
        this.authorName = authorName;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Author [authId=" + authId + ", authorName=" + authorName + ", phone=" + phone + "]";
    }



    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    
}

