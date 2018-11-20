import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class EmailTokenizer {

    private List<String> symbolsToStrip = Arrays.asList(
            ".",
            ",",
            "_",
            "-",
            "/",
            "*",
            "=",
            ":",
            ">",
            "'",
            "(",
            ")",
            "\"",
            ";",
            "[",
            "]",
            "&",
            "~",
            "?"
    );

    private List<String> wordsToIgnore = Arrays.asList(
            "a",
            "all",
            "am",
            "an",
            "and",
            "any",
            "are",
            "as",
            "at",
            "be",
            "been",
            "but",
            "by",
            "can",
            "days",
            "did",
            "do",
            "does",
            "for",
            "from",
            "had",
            "has",
            "have",
            "he",
            "her",
            "here",
            "him",
            "i",
            "if",
            "in",
            "is",
            "it",
            "its",
            "like",
            "may",
            "me",
            "my",
            "no",
            "not",
            "of",
            "off",
            "on",
            "or",
            "our",
            "ours",
            "pm",
            "put",
            "see",
            "she",
            "so",
            "some",
            "than",
            "that",
            "the",
            "them",
            "then",
            "there",
            "these",
            "they",
            "this",
            "those",
            "though",
            "to",
            "today",
            "too",
            "up",
            "us",
            "was",
            "we",
            "well",
            "were",
            "what",
            "whatever",
            "when",
            "where",
            "whether",
            "which",
            "while",
            "who",
            "will",
            "with",
            "would",
            "yes",
            "yet",
            "you",
            "your",
            "yours"
    );

    private List<String> numberWordsToIgnore = Arrays.asList(
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine",
            "ten"
    );

    private List<String> otherStringsToIgnore = Arrays.asList(
            "subject:",
            "'t",
            "n't",
            "don",
            "'s",
            "re",
            "ve",
            "ll"
    );

    private List<String> completeListOfTokensToStrip = new ArrayList<>();

    public static int tokensStripped = 0;
    public static int tokensParsed = 0;
    public static int tokensAccepted = 0;

    public EmailTokenizer() {
        completeListOfTokensToStrip.addAll(symbolsToStrip);
        completeListOfTokensToStrip.addAll(wordsToIgnore);
        completeListOfTokensToStrip.addAll(numberWordsToIgnore);
        completeListOfTokensToStrip.addAll(otherStringsToIgnore);
    }

    public List<List<String>> tokenizeEmails(List<EmailData> emails) {
        List<List<String>> tokenizedEmails = new ArrayList<>();

        for(EmailData email: emails) {
            tokenizedEmails.add(tokenizeEmail(email));
        }

        return tokenizedEmails;
    }

    public List<String> tokenizeEmail(EmailData email) {
        StringTokenizer tokenizer = new StringTokenizer(email.contents, " \t\n\r\f");
        List<String> tokens = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().toLowerCase();
            tokensParsed++;
            // Strip the chosen tokens, plus any numbers
            if (!completeListOfTokensToStrip.contains(token) && !token.matches("\\d+")) {
                tokens.add(token);
                tokensAccepted++;
            }
            else {
                tokensStripped++;
            }
        }

        return tokens;
    }

}
