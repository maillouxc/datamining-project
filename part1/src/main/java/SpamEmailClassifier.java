/**
 * An interface which defines classes that can classify emails as either spam or ham.
 */
public interface SpamEmailClassifier {

    /**
     * Trains the provided email into the model for the classifier.
     */
    void train(EmailData email);

    /**
     * @return True if the classifier thinks the email is spam.
     */
    boolean classify(EmailData testEmail);

    /**
     * Convenience method for generating things like print statements.
     */
    String getAlgorithmName();

}
