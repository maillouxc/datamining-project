#!/usr/bin/env python3

import argparse
from time import time


def main():
    parsedArgs = parse_command_line_arguments()
    load_dataset(parsedArgs.path)


def parse_command_line_arguments():
    parser = argparse.ArgumentParser(
        description="Classifies emails as spam or ham")
    parser.add_argument(
        'path',
        help="The path to the two folders, \'test\' and \'training\'"
    )
    return parser.parse_args()


def load_dataset(dataPath):
    print(dataPath)


if __name__ == "__main__":
    main()


