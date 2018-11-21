import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * TODO finish this documentation.
 *
 * Of course, with Naive Bayes done in this manner, we are assuming independence of words. That is, we are not looking
 * at full sentences or phrases, but rather individual words chosen.
 */
public class NaiveBayesClassifier implements SpamEmailClassifier {

    private EmailTokenizer emailTokenizer;

    private int numSpam = 0;
    private int numHam = 0;

    private int totalSpamTokens = 0;
    private int totalHamTokens = 0;

    private HashMap<String, Integer> spamTokenCounts = new HashMap<>();
    private HashMap<String, Integer> hamTokenCounts = new HashMap<>();

    private HashMap<String, Double> tokenSpamProbabilities = new HashMap<>();

    private double overallProbabilityOfSpam;

    public NaiveBayesClassifier(List<EmailData> trainingData) {
        emailTokenizer = new EmailTokenizer(false);
        for (EmailData email : trainingData) {
            train(email);
        }

        spamTokenCounts.forEach((token, count) -> {
            double probabilitySpamEmailContainsToken = (double) count / totalSpamTokens;
            tokenSpamProbabilities.put(token, probabilitySpamEmailContainsToken);
        });
    }

    @Override
    public void train(EmailData email) {
        if (email.isSpam) {
            numSpam++;
        } else {
            numHam++;
        }
        overallProbabilityOfSpam = (float) numSpam / (numSpam + numHam);

        List<String> tokens = emailTokenizer.tokenizeEmail(email)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        for (String token : tokens) {
            if (email.isSpam) {
                spamTokenCounts.merge(token, 1, Integer::sum);
                totalSpamTokens++;
                tokenSpamProbabilities.put(token, mEstimateProbability(spamTokenCounts.get(token), totalSpamTokens));
            }
            else {
                hamTokenCounts.merge(token, 1, Integer::sum);
                totalHamTokens++;
            }
        }
    }

    @Override
    public boolean classify(EmailData email) {
        List<String> tokens = emailTokenizer.tokenizeEmail(email);

        // Use the sum of logs of probabilities of each word instead of products to avoid underflow problems
        double sumOfLogsOfSpamProbabilities = 0;
        for (String word : tokens) {
            double spamminessOfWord = getSpamminessOfWord(word);
            sumOfLogsOfSpamProbabilities -= Math.log(spamminessOfWord);
        }
        double probabilityOfSpam = 1 - (Math.exp(-sumOfLogsOfSpamProbabilities) * overallProbabilityOfSpam);
        return probabilityOfSpam > 0.5;
    }

    private double mEstimateProbability(int instances, int totalNumValues) {
        int m = 3; // TODO choose best m
        double p = (double) 1/3; // TODO choose best p
        return (instances + (m * p)) / (totalNumValues + m);
    }

    /**
     * Returns the probability that a given word is found in spam emails.
     */
    private double getSpamminessOfWord(String word) {
        double pSpamGivenWord;
        if (tokenSpamProbabilities.containsKey(word)) {
            pSpamGivenWord = tokenSpamProbabilities.get(word);
        }
        else {
            pSpamGivenWord = mEstimateProbability(0, spamTokenCounts.size());
        }
        return pSpamGivenWord;
    }

    @Override
    public String getAlgorithmName() {
        return "naivebayes";
    }

}
