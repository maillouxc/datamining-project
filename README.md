# datamining-project
This is a project for FGCU's knowledge discovery and data mining course.
It focuses on spam email classification.

Part 1 of the project is an implementation of a spam email classifier from scratch in Java 8,
using a Naive Bayes classifier, and also a K-nearest-neighbors classifier.

Part 2 of the project is a pair of experiments in Scikit learn with various other algorithms.

## Run instructions:

### Ensure that the dataset meets the following criteria:
   - All files must be .txt files
   - All files must be stored alone in their directory with no other files
   - All files be divided between two subdirectories, `test` and `training`
   - All spam emails are expected to have a filename beginning with "sp"

### Usage:

  Part 1:
  
  ```
  usage: Classify 
    data_path
    algorithm_name (Either "knn" or "naivebayes")
    [k] The K value to use - only used if algorithm is "knn"
  ```
  
  Part 2:
  
  ```
  usage: python experiments.py
  
  There are no arguments - the program is merely a script which conducts experiments with various values.
  ```
  
  
### Run instuctions:

Part 1:

 1. Ensure that you have Java JRE 8 installed.
 2. Compile the program using Gradle:
      (From the project's part1 subdirectory)
      `./gradlew build`
      The generated Java class files will then be in the /build/classes/java/main directory.
      (The program can also be compiled like a standard java program if this has problems).
 3. Run the program as `java Classify arguments`
      The arugments are described in the above section

Part 2:

  1. Ensure that you have Python 3.7 or greater installed.
     a. If you have _both_ Python 2 and Python 3 installed, you will need to ensure you run the program using `python3`.
  2. Ensure that you have pip installed.
     b. If you have _both_ Python 2 and Python 3 installed, you will need to run pip commands using `pip3`.
  3. Install scikit-learn: `pip install sklearn`
  4. Install numpy: `pip install numpy`
  5. Run the program as: `python experiments.py path_to_data`
