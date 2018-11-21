/**
 * A simple data holder object for email data. Includes a field for the class label (to be used for training and
 * testing the accuracy of models).
 */
public class EmailData {

    public String contents;
    public Boolean isSpam;

    public EmailData(String contents, Boolean isSpam) {
        this.contents = contents;
        this.isSpam = isSpam;
    }

}
