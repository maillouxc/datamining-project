import java.util.*;
import java.util.stream.Collectors;

public class KNearestNeighborsClassifier implements SpamEmailClassifier {

    private final int k;

    private EmailTokenizer emailTokenizer = new EmailTokenizer();

    private List<String> uniqueTokens = new ArrayList<>();
    private List<EmailTermVector> termsVectors = new ArrayList<>();

    public KNearestNeighborsClassifier(List<EmailData> trainingEmails, int k) {
        this.k = k;
        trainingEmails.forEach(this::train);
    }

    @Override
    public void train(EmailData email) {
        List<String> tokensFoundInEmail = emailTokenizer.tokenizeEmail(email);
        HashMap<String, Integer> tokenCounts = new HashMap<>();

        // Find the frequency count of each token in the email
        tokensFoundInEmail.forEach(token -> tokenCounts.merge(token, 1, Integer::sum));

        // Add any previously unseen unique tokens to each other term vector from previous emails with a count of 0
        tokenCounts.keySet().forEach(token -> {
            boolean isPreviouslyUnseenToken = !uniqueTokens.contains(token);
            if (isPreviouslyUnseenToken) {
                uniqueTokens.add(token);
                termsVectors.forEach(termVector -> termVector.termFrequencyCounts.merge(token, 0, Integer::sum));
            }
        });

        // Add any unique words from the other emails that aren't found in this email to our terms count with count 0
        for (String token : uniqueTokens) {
            tokenCounts.merge(token, 0, Integer::sum);
        }

        termsVectors.add(new EmailTermVector(email.isSpam, tokenCounts));
    }

    @Override
    public boolean classify(EmailData email) {
        List<String> emailTokens = emailTokenizer.tokenizeEmail(email);
        HashMap<String, Integer> tokenCounts = new HashMap<>();
        emailTokens.forEach(token -> tokenCounts.merge(token, 1, Integer::sum));

        List<Map.Entry<Boolean, Double>> cosineSimilarities = new ArrayList<>();
        EmailTermVector testEmailTermVector = new EmailTermVector(email.isSpam, tokenCounts);

        for (EmailTermVector trainingEmailTermVector : termsVectors) {
            double cosineSimilarity = cosineSimilarity(testEmailTermVector, trainingEmailTermVector);
            cosineSimilarities.add(
                    new AbstractMap.SimpleImmutableEntry<>(
                            trainingEmailTermVector.isSpam, cosineSimilarity
                    )
            );
        }

        cosineSimilarities.sort((s1, s2) -> s2.getValue().compareTo(s1.getValue()));
        int spamVotes = 0;
        int hamVotes = 0;
        for (int i = 0; i < this.k; i++) {
            if (cosineSimilarities.get(i).getKey()) {
                spamVotes++;
            } else {
                hamVotes++;
            }
        }

        return spamVotes > hamVotes;
    }

    private double cosineSimilarity(EmailTermVector email1, EmailTermVector email2) {
        // Calculate the vector dot product
        int dotProduct = 0;

        Set<String> email1Words = email1.termFrequencyCounts.keySet();
        Set<String> email2Words = email2.termFrequencyCounts.keySet();
        for (String token : email1Words) {
            int email1TokenFrequency = email1.termFrequencyCounts.get(token);
            int email2TokenFrequency = 0;
            if (email2Words.contains(token)) {
                email2TokenFrequency = email2.termFrequencyCounts.get(token);
            }
            dotProduct += email1TokenFrequency * email2TokenFrequency;
        }

        return (float) dotProduct / (uniqueTokens.size() * uniqueTokens.size());
    }

    @Override
    public String getAlgorithmName() {
        return "knn";
    }

    /**
     * Convenience class used to associate the class label for the training data with the term vector itself.
     */
    private class EmailTermVector {

        boolean isSpam;
        HashMap<String, Integer> termFrequencyCounts;

        public EmailTermVector(boolean isSpam, HashMap<String, Integer> termFrequencyCounts) {
            this.isSpam = isSpam;
            this.termFrequencyCounts = termFrequencyCounts;
        }

        @Override
        public String toString() {
            return String.format(
                    "Spam: %b\nNon-zero count entries: \n%s",
                    isSpam,
                    termFrequencyCounts
                            .entrySet()
                            .stream()
                            .filter(entry -> entry.getValue() > 0)
                            .collect(Collectors.toList())
            );
        }

    }

}
