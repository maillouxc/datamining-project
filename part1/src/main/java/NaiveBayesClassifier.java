import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

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
    private HashMap<String, Double> tokenHamProbabilities = new HashMap<>();

    private double overallProbabilityOfSpam;
    private double overallProbabilityOfHam;

    public NaiveBayesClassifier(List<EmailData> trainingData) {
        emailTokenizer = new EmailTokenizer();
        for (EmailData email : trainingData) {
            train(email);
        }

        spamTokenCounts.forEach((token, count) -> {
            double probabilitySpamEmailContainsToken = (double) count / totalSpamTokens;
            tokenSpamProbabilities.put(token, probabilitySpamEmailContainsToken);
            String probabilityString = String.format("%.6f", probabilitySpamEmailContainsToken);
            System.out.println("P(Spam|" + token + ") = " + probabilityString); // TODO remove
        });

        hamTokenCounts.forEach((token, count) -> {
            double probabilityHamEmailContainsToken = (double) count / totalHamTokens;
            tokenHamProbabilities.put(token, probabilityHamEmailContainsToken);
            String probabilityString = String.format("%.6f", probabilityHamEmailContainsToken);
            System.out.println("P(Ham|" + token + ") = " + probabilityString); // TODO remove
        });

        System.out.println("\nOverall probability that email is spam, P(S) = " + overallProbabilityOfSpam);
        System.out.println("Overall probability of ham, P(H) = " + overallProbabilityOfHam);

        //printTokenCounts();
    }

    public void train(EmailData email) {
        if (email.isSpam) {
            numSpam++;
        } else {
            numHam++;
        }
        overallProbabilityOfSpam = (float) numSpam / (numSpam + numHam);
        overallProbabilityOfHam = 1 - overallProbabilityOfSpam;

        List<String> tokens = emailTokenizer.tokenizeEmail(email);
        for (String token : tokens) {
            if (email.isSpam) {
                spamTokenCounts.merge(token, 1, Integer::sum);
                totalSpamTokens++;
                tokenSpamProbabilities.put(token, mEstimateProbability(spamTokenCounts.get(token), totalSpamTokens));
            }
            else {
                hamTokenCounts.merge(token, 1, Integer::sum);
                totalHamTokens++;
                tokenSpamProbabilities.put(token, mEstimateProbability(hamTokenCounts.get(token), totalHamTokens));
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
            double logProbability = Math.log1p(spamminessOfWord);
            sumOfLogsOfSpamProbabilities += logProbability;
        }

        double probabilityOfSpam = (sumOfLogsOfSpamProbabilities * overallProbabilityOfSpam);
        System.out.printf("Probability of spam for this email = %.5f\n", probabilityOfSpam);

        return probabilityOfSpam > 0.5;
    }

    private double mEstimateProbability(int instances, int totalNumValues) {
        int m = 3; // TODO choose best m
        double p = (double) 1/3; // TODO choose best p
        return (instances + (m * p)) / (totalNumValues + m);
    }

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

    private void printTokenCounts() {
        List<String> hamCountStrings = new ArrayList<>();
        List<String> spamCountStrings = new ArrayList<>();

        hamTokenCounts.forEach((key, value) -> hamCountStrings.add(value + " = " + key));
        spamTokenCounts.forEach((key, value) -> spamCountStrings.add(value + " = " + key));

        hamCountStrings.sort((str1, str2) -> {
            int int1 = new Scanner(str1).useDelimiter("\\D+").nextInt();
            int int2 = new Scanner(str2).useDelimiter("\\D+").nextInt();
            return Integer.compare(int1, int2);
        });
        spamCountStrings.sort((str1, str2) -> {
            int int1 = new Scanner(str1).useDelimiter("\\D+").nextInt();
            int int2 = new Scanner(str2).useDelimiter("\\D+").nextInt();
            return Integer.compare(int1, int2);
        });

        for (String s : hamCountStrings) { System.out.println(s); }
        for (String s : spamCountStrings) { System.out.println(s); }

        System.out.printf(
                "\n%d/%d tokens stripped. %d remaining\n",
                EmailTokenizer.tokensStripped,
                EmailTokenizer.tokensParsed,
                EmailTokenizer.tokensAccepted
        );

        double percentTokensRemoved = ((float) EmailTokenizer.tokensStripped / EmailTokenizer.tokensParsed) * 100;
        String percentRemovedString = String.format("%.3f", percentTokensRemoved);
        System.out.println("Removed " + percentRemovedString + "%. of tokens\n");

        System.out.println("Total ham tokens = " + totalHamTokens);
        System.out.println("Total spam tokens = " + totalSpamTokens);
    }

    @Override
    public String getAlgorithmName() {
        return "naivebayes";
    }

}
