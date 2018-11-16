import java.util.List;

public class KNearestNeighborsClassifier implements SpamEmailClassifier {

    public KNearestNeighborsClassifier(List<EmailData> trainingEmails, int k) {
        // TODO
    }

    @Override
    public boolean classify(EmailData email) {
        // TODO

        return false; // TODO remove this line.
    }

    @Override
    public String getAlgorithmName() {
        return "knn";
    }

}
