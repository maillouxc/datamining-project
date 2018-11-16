import java.util.List;

public class NaiveBayesClassifier implements SpamEmailClassifier {

    public NaiveBayesClassifier(List<EmailData> trainingData) {
        // TODO
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
