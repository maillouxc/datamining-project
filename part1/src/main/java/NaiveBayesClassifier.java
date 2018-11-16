import java.util.ArrayList;
import java.util.List;

/**
 * TODO finish this documentation.
 *
 * Of course, with Naive Bayes done in this manner, we are assuming independence of words. That is, we are not looking
 * at full sentences or phrases, but rather individual words chosen.
 */
public class NaiveBayesClassifier implements SpamEmailClassifier {

    private EmailTokenizer emailTokenizer;

    public NaiveBayesClassifier(List<EmailData> trainingData) {
        emailTokenizer = new EmailTokenizer();
        List<List<String>> tokenizedEmails = new ArrayList<>();
        for (EmailData email : trainingData) {
            tokenizedEmails.add(emailTokenizer.tokenizeEmail(email));
        }

        // TODO

        System.out.printf(
                "\n%d/%d tokens stripped. %d remaining\n",
                EmailTokenizer.tokensStripped,
                EmailTokenizer.tokensParsed,
                EmailTokenizer.tokensAccepted
        );

        double percentTokensRemoved = ((float) EmailTokenizer.tokensStripped / EmailTokenizer.tokensParsed) * 100;
        String percentRemovedString = String.format("%.3f", percentTokensRemoved);
        System.out.println("Removed " + percentRemovedString + "%. of tokens\n");
    }

    @Override
    public boolean classify(EmailData email) {
        // TODO

        return false; // TODO remove this line
    }

    @Override
    public String getAlgorithmName() {
        return "naivebayes";
    }

}
