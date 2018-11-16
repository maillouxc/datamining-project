import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * TODO finish this documentation.
 *
 * Of course, with Naive Bayes done in this manner, we are assuming independence of words. That is, we are not looking
 * at full sentences or phrases, but rather individual words chosen.
 */
public class NaiveBayesClassifier implements SpamEmailClassifier {

    long initialNumTokens = 0;
    long totalNumTokens = 0; // TODO remove test code

    public NaiveBayesClassifier(List<EmailData> trainingData) {
        // TODO remove test code

        List<List<String>> tokenizedEmails = new ArrayList<>();
        for (EmailData email : trainingData) {
            tokenizedEmails.add(tokenizeEmail(email));
        }

        System.out.println("Initial num tokens = " + initialNumTokens);
        System.out.println("Total num tokens = " + totalNumTokens);
        long removedNumTokens = initialNumTokens - totalNumTokens;
        System.out.println(removedNumTokens + " tokens removed.");
        double percentTokensRemoved = (((float) removedNumTokens / totalNumTokens) * 100);
        String percentRemovedString = String.format("%.3f", percentTokensRemoved);
        System.out.println("Removed " + percentRemovedString + "%. of tokens");
        System.out.println();

        // TODO
    }

    @Override
    public boolean classify(EmailData email) {
        // TODO

        return false; // TODO remove this line
    }

    private List<String> tokenizeEmail(EmailData email) {
        StringTokenizer tokenizer = new StringTokenizer(email.contents, " \t\n\r\f");
        List<String> tokens = new ArrayList<>();

        List<String> tokensToStrip = new ArrayList<>();
        tokensToStrip.add("subject:");
        tokensToStrip.addAll(Arrays.asList(
                "subject:",
                "the",
                "it",
                "a",
                "in",
                "on",
                "for",
                "you",
                "me",
                "is",
                "by",
                "of",
                "to",
                "no",
                "yes",
                "and",
                "be",
                "do",
                "if",
                "was",
                "this",
                "i",
                "that",
                "are",
                "your",
                "or",
                "as",
                "what",
                "at"
        ));

        System.out.println();
        System.out.println();
        System.out.println("Tokenized email is:\n");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().toLowerCase();
            // Remove numbers and any tokens that are on the list of tokens to strip
            if (!tokensToStrip.contains(token) && !token.matches("\\d+")) {
                tokens.add(token);
                System.out.println(token);
                totalNumTokens++;
            }
            initialNumTokens++; // this counts tokens overall without any filtering
        }
        System.out.println();
        System.out.println();

        return tokens;
    }

    @Override
    public String getAlgorithmName() {
        return "naivebayes";
    }

}
