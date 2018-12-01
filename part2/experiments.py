#!/usr/bin/env python3

import argparse
import os
import glob
from time import time

from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn import svm
from sklearn.linear_model import Perceptron


def main():
    parsed_args = parse_command_line_arguments()
    data_path = parsed_args.path

    # We need the tfidf_transformer and counts_vect here to pass the the test email loader.
    # There are other ways to do this, but this is the most convenient.
    # It's important to supply this tranformer because we need to maintain the vocabulary between test and training
    training_emails, training_labels, tfidf_transformer, counts_vect = load_training_dataset(data_path + "/training")
    test_emails, test_labels = load_test_dataset(data_path + "/test", tfidf_transformer, counts_vect)

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


def load_training_dataset(data_path):
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

    count_vectorizer = CountVectorizer(stop_words='english')
    tfidf_transformer = TfidfTransformer(use_idf=False)
    email_feature_data = tfidf_transformer.fit_transform(count_vectorizer.fit_transform(email_data))

    print(email_feature_data)
    print("Loaded " + str(len(email_labels)) + " training emails")

    return email_feature_data, email_labels, tfidf_transformer, count_vectorizer


def load_test_dataset(data_path, tfidf_transformer, counts_vect):
    print("Loading test dataset at " + data_path)

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

    email_feature_data = tfidf_transformer.transform(counts_vect.transform(email_data))

    print(email_feature_data)
    print("Loaded " + str(len(email_labels)) + " test emails")

    return email_feature_data, email_labels


def train_svm_classifier(emails, labels):
    classifier = svm.SVC(
        gamma='scale',
        C=1,
        verbose=True)
    classifier.fit(emails, labels)
    return classifier


if __name__ == "__main__":
    main()


