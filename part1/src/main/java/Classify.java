public class Classify {

    public static void main(String[] args) {
        // Read the file path to the datasets from the command line args and crash if it is omitted
        // TODO

        // Read the name of the algorithm to use from the command line and crash if it is omitted
        String algorithmName = "";
        try {
            algorithmName = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Argument 1 missing - classification algorithm name.");
            System.exit(1);
        }

        // Execute the appropriate algorithm based on name or crash if we get an unexpected algorithm name
        switch (algorithmName.toLowerCase()) {
            case "knn":
                // We need to find out what the value of K is from the arguments
                int k;
                try {
                    k = Integer.parseInt(args[2]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Expected an integer value for k as argument 2.");
                    System.exit(1);
                }

                // TODO knn classification with value of k
                KNearestNeighborsClassifier knnClassifier = new KNearestNeighborsClassifier();
                break;
            case "naivebayes":
                // TODO naive bayes classification
                NaiveBayesClassifier naiveBayesClassifier = new NaiveBayesClassifier();
                break;
            default:
                System.err.println("Unknown/unsupported algorithm: " + algorithmName);
                System.exit(1);
                break;
        }
    }

}
