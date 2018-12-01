#!/usr/bin/env python3

import argparse
import os
import glob
from time import time

from sklearn.feature_extraction.text import CountVectorizer
from sklearn import svm
from sklearn.linear_model import Perceptron


def main():
    parsed_args = parse_command_line_arguments()

    data_path = parsed_args.path
    training_emails, training_labels = load_dataset(data_path + "/training")
    test_emails, test_labels = load_dataset(data_path + "/test")

    svm_classifier = train_svm_classifier(training_emails, training_labels)

    results = svm_classifier.predict(test_emails)
    print(results)


def parse_command_line_arguments():
    parser = argparse.ArgumentParser(
        description="Classifies emails as spam or ham")
    parser.add_argument(
        'path',
        help="The path to the two folders, \'test\' and \'training\'"
    )
    return parser.parse_args()


def load_dataset(data_path):
    print("Loading dataset at " + data_path)

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

    count_vectorizer = CountVectorizer(stop_words='english')
    email_features = count_vectorizer.fit_transform(email_data)

    print("Loaded " + str(len(email_labels)) + " emails")

    return email_features, email_labels


def train_svm_classifier(emails, labels):
    classifier = svm.SVC(
        gamma='scale',
        C=1,
        verbose=True)
    classifier.fit(emails, labels)
    return classifier


if __name__ == "__main__":
    main()


