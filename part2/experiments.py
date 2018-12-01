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

training_emails = []
training_labels = []
test_emails = []
test_labels = []

def main():
    global training_emails, training_labels, test_emails, test_labels
    parsed_args = parse_command_line_arguments()    
    data_path = parsed_args.path
    training_emails, training_labels, = load_dataset(data_path + "/training")
    test_emails, test_labels = load_dataset(data_path + "/test")

    test_svm_classifier(C=1, use_idf=True, kernel='linear')
    test_svm_classifier(C=2, use_idf=True, kernel='linear')
    test_svm_classifier(C=10, use_idf=True, kernel='linear')


def parse_command_line_arguments():
    parser = argparse.ArgumentParser(
        description="Tests two types of email spam classifiers")
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


def test_svm_classifier(C, use_idf, kernel):
    svm_classifier = Pipeline([
        ('count_vectorizer', CountVectorizer(stop_words="english")),
        ('tfidf_transformer', TfidfTransformer(use_idf=use_idf)),
        ('svm_classifier', svm.SVC(gamma="scale",
                                   C=C,
                                   class_weight=None,
                                   kernel=kernel
                                   verbose=False))
    ])

    print(svm_classifier.get_params()['svm_classifier'])

    # Train the svm
    svm_classifier.fit(training_emails, training_labels)

    # Test it
    num_correctly_classified = 0
    num_wrongly_classified = 0
    results = svm_classifier.predict(test_emails)
    for i in range(len(results)):
        if (results[i] == test_labels[i]):
            num_correctly_classified += 1
        else:
            num_wrongly_classified += 1

    accuracy = str("{0:.3%}").format(numpy.mean(results == test_labels))
    print("Accuracy of SVM classifier was " + accuracy)


if __name__ == "__main__":
    main()


