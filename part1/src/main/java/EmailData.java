public class EmailData {

    public String contents;
    public Boolean isSpam;

    public EmailData() {}

    public EmailData(String contents, Boolean isSpam) {
        this.contents = contents;
        this.isSpam = isSpam;
    }

}
