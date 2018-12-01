#!/usr/bin/env python3

import argparse
import os
import glob
import numpy
from time import time
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.pipeline import Pipeline
from sklearn import svm
from sklearn.linear_model import Perceptron


def main():
    parsed_args = parse_command_line_arguments()
    
    data_path = parsed_args.path
    training_emails, training_labels, = load_dataset(data_path + "/training")
    test_emails, test_labels = load_dataset(data_path + "/test")

    svm_classifier = Pipeline([
        ('count_vectorizer', CountVectorizer(stop_words="english")),
        ('tfidf_transformer', TfidfTransformer()),
        ('svm_classifier', svm.SVC(gamma="scale",
                                   C=1,
                                   verbose=False))
    ])

    # Train the svm
    svm_classifier.fit(training_emails, training_labels)

    num_correctly_classified = 0
    num_wrongly_classified = 0
    # Test it
    results = svm_classifier.predict(test_emails)
    for i in range(len(results)):
        if (results[i] == test_labels[i]):
            num_correctly_classified += 1
        else:
            num_wrongly_classified += 1

    print("Accuracy of SVM classifier was " + str("{0:.3%}").format(numpy.mean(results == test_labels)))


def parse_command_line_arguments():
    parser = argparse.ArgumentParser(
        description="Classifies emails as spam or ham")
    parser.add_argument(
        'path',
        help="The path to the two folders, \'test\' and \'training\'"
    )
    return parser.parse_args()


def load_dataset(data_path):
    print("Loading training dataset at " + data_path)

    email_data = []
    email_labels = []

    os.chdir(data_path)
    for file in os.listdir():
        if (file.startswith("sp")):
            email_labels.append("spam")
        else:
            email_labels.append("ham")
        with open(file, 'r') as email_file:
            email_data.append(email_file.read())

    print("Loaded " + str(len(email_labels)) + " training emails")

    return email_data, email_labels


if __name__ == "__main__":
    main()


