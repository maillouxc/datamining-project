import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Classify {

    /**
     * Classifies emails as either spam or ham using the chosen algorithm, and prints accuracy info.
     *
     * @param args The command line args, in the following order:
     *
     *             dataPath  - The path to a directory containing two folders, which must be named "training" a "test",
     *                         that contain the training and testing email data to use.
     *
     *             algorithm - Either "knn" or "naivebayes".
     *
     *             k         - Only when algorithm is "knn".
     */
    public static void main(String[] args) {
        // Read the file path to the datasets from the command line args and crash if it is omitted
        String dataPath = args[0];
        if (dataPath == null || dataPath.isEmpty()) {
            throw new IllegalStateException("Argument 1 missing - path to data.");
        }

        // Read the name of the algorithm to use from the command line and crash if it is omitted
        String algorithmName = "";
        try {
            algorithmName = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Argument 2 missing - classification algorithm name.");
            System.exit(1);
        }

        // Next, we need to read the training and test datasets from the filesystem
        // This can crash if the directory structure of the data isn't as expected.
        String trainingDataPath = dataPath + File.separator + "training";
        String testDataPath = dataPath + File.separator + "test";
        List<EmailData> trainingEmails;
        List<EmailData> testEmails;

        try {
            trainingEmails = loadEmailDataFromPath(trainingDataPath);
            testEmails = loadEmailDataFromPath(testDataPath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load email data.", e);
        }

        // Execute the appropriate algorithm based on name or crash if we get an unexpected algorithm name
        switch (algorithmName.toLowerCase()) {
            case "knn":
                // We need to find out what the value of K is from the arguments
                int k = 1;
                try {
                    k = Integer.parseInt(args[2]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Expected an integer value for k as argument 2.");
                    System.exit(1);
                }

                KNearestNeighborsClassifier knnClassifier = new KNearestNeighborsClassifier(trainingEmails, k);
                testAccuracyOfClassifier(knnClassifier, testEmails);
                break;
            case "naivebayes":
                NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier(trainingEmails);
                testAccuracyOfClassifier(naiveBayesClassifier, testEmails);
                break;
            default:
                System.err.println("Unknown/unsupported algorithm: " + algorithmName);
                System.exit(1);
                break;
        }
    }

    private static ArrayList<EmailData> loadEmailDataFromPath(String path) throws IOException {
        ArrayList<EmailData> emailData = new ArrayList<>();

        File folder = new File(path);
        File[] emailFiles = folder.listFiles();
        assert emailFiles != null;

        for (File emailFile : emailFiles) {
            boolean isSpam = emailFile.getName().startsWith("sp");
            String emailText = String.join(" ", Files.readAllLines(emailFile.toPath()));
            emailData.add(new EmailData(emailText, isSpam));
        }

        return emailData;
    }

    /**
     * Tests and prints the accuracy of the chosen SpamEmailClassifier provided.
     */
    private static void testAccuracyOfClassifier(SpamEmailClassifier classifier, List<EmailData> testEmails) {
        int numCorrectlyClassified = 0;
        for (EmailData email : testEmails) {
            boolean isActuallySpam = email.isSpam;
            boolean isClassifiedAsSpam = classifier.classify(email);
            boolean classifiedCorrectly = isClassifiedAsSpam == isActuallySpam;
            numCorrectlyClassified += classifiedCorrectly ? 1 : 0;
        }

        double accuracy = ((double) numCorrectlyClassified / testEmails.size()) * 100;
        String accuracyString = String.format("%.3f", accuracy);

        System.out.println("\nhaAccuracy of " + classifier.getAlgorithmName() + " was " + accuracyString + "%.");
        System.out.println(numCorrectlyClassified + "/" + testEmails.size() + " emails classified correctly.");
    }

}
