public interface SpamEmailClassifier {

    /**
     * @return True if the classifier thinks the email is spam.
     */
    boolean classify(EmailData testEmail);

    /**
     * Convenience method for generating things like print statements.
     */
    String getAlgorithmName();

}
