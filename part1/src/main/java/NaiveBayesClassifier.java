import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A SpamEmailClassifier based on Naive Bayesian classification.
 *
 * With Naive Bayes done in this manner, we are assuming independence of words. That is, we are not looking
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
    private HashMap<String, Double> tokenHamProbabilities = new HashMap<>();

    private double priorProbabilityOfSpam;
    private double priorProbabilityOfHam;

    public NaiveBayesClassifier(List<EmailData> trainingData) {
        emailTokenizer = new EmailTokenizer(true);
        trainingData.forEach(this::train);
    }

    @Override
    public void train(EmailData email) {
        // Recalculate prior probabilities based on class of new email
        if (email.isSpam) {
            numSpam++;
        } else {
            numHam++;
        }
        priorProbabilityOfSpam = (float) numSpam / (numSpam + numHam);
        priorProbabilityOfHam = (float) numHam / (numSpam + numHam);

        // Get list of unique tokens from email
        List<String> tokens = emailTokenizer.tokenizeEmail(email)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        // Track the overall frequency counts of each token
        for (String token : tokens) {
            if (email.isSpam) {
                spamTokenCounts.merge(token, 1, Integer::sum);
                totalSpamTokens++;
            }
            else {
                hamTokenCounts.merge(token, 1, Integer::sum);
                totalHamTokens++;
            }
        }

        recalculateAllTokenClassProbabilities();
    }

    private void recalculateAllTokenClassProbabilities() {
        for (Map.Entry<String, Integer> spamToken : spamTokenCounts.entrySet()) {
            tokenSpamProbabilities.put(
                    spamToken.getKey(),
                    mEstimateProbability(spamToken.getValue(), numSpam)
            );
        }

        for (Map.Entry<String, Integer> hamToken : hamTokenCounts.entrySet()) {
            tokenHamProbabilities.put(
                    hamToken.getKey(),
                    mEstimateProbability(hamToken.getValue(), numHam)
            );
        }
    }

    @Override
    public boolean classify(EmailData email) {
        List<String> tokens = emailTokenizer.tokenizeEmail(email);

        double sumLogSpamProbabilities = 0;
        double sumLogHamProbabilities = 0;

        for (String word : tokens) {
            sumLogSpamProbabilities += Math.log(getSpamminessOfWord(word));
            sumLogHamProbabilities += Math.log(getHamminessOfWord(word));
        }

        sumLogSpamProbabilities += Math.log(priorProbabilityOfSpam);
        sumLogHamProbabilities += Math.log(priorProbabilityOfHam);

        return sumLogSpamProbabilities > sumLogHamProbabilities;
    }

    private double mEstimateProbability(int numInstancesOfTokenInClass, int numTrainingEmailsInClass) {
        double m = totalSpamTokens + totalHamTokens;
        double p = (double) 1 / m;
        return (numInstancesOfTokenInClass + (m*p)) / (numTrainingEmailsInClass + m);
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
            pSpamGivenWord = mEstimateProbability(0, numSpam);
        }
        return pSpamGivenWord;
    }

    private double getHamminessOfWord(String word) {
        double pHamGivenWord;
        if (tokenHamProbabilities.containsKey(word)) {
            pHamGivenWord = tokenHamProbabilities.get(word);
        }
        else {
            pHamGivenWord = mEstimateProbability(0, numHam);
        }
        return pHamGivenWord;
    }

    @Override
    public String getAlgorithmName() {
        return "naivebayes";
    }

}
