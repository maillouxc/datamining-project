import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Transforms emails into lists of tokens, optionally filtering their contents with a list of stop words, unwanted
 * symbols, etc.
 */
public class EmailTokenizer {

    /**
     * A list of symbols that should be filtered from the emails. Certain symbols are deliberately NOT included in this
     * list, such as exclamation points, as they tend to be fare more common in spam emails.
     */
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

    /**
     * A list of stop words to ignore, sorted in alphabetical order.
     */
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

    /**
     * A list of words representing the integers one though ten, as these are likely also considered stop words.
     */
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

    /**
     * A list of miscellaneous strings to exclude from the emails. Among other things, our tokenizer doesn't properly
     * parse contractions, so the trailing bits of common contractions are included in this list.
     */
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

    /**
     * All of the strip lists of tokens are combined into a single list of tokens to strip, stored here, so that they
     * may be excluded during tokenizing.
     */
    private List<String> completeListOfTokensToStrip = new ArrayList<>();

    private boolean stripTokens;

    public static int tokensStripped = 0;
    public static int tokensParsed = 0;
    public static int tokensAccepted = 0;

    /**
     * Creates an instance of an EmailTokenizer, with the default strip list.
     *
     * @param stripTokens Whether to exclude tokens in the strip list.
     */
    public EmailTokenizer(boolean stripTokens) {
        this.stripTokens = stripTokens;

        completeListOfTokensToStrip.addAll(symbolsToStrip);
        completeListOfTokensToStrip.addAll(wordsToIgnore);
        completeListOfTokensToStrip.addAll(numberWordsToIgnore);
        completeListOfTokensToStrip.addAll(otherStringsToIgnore);
    }

    /**
     * Same as tokenizeEmail(), but takes in a list of emails and returns a list of results instead of a single email
     * and single result token set.
     */
    public List<List<String>> tokenizeEmails(List<EmailData> emails) {
        List<List<String>> tokenizedEmails = new ArrayList<>();

        for(EmailData email: emails) {
            tokenizedEmails.add(tokenizeEmail(email));
        }

        return tokenizedEmails;
    }

    /**
     * Takes an email and returns a list of tokens, with the predefined list of stop words and other undesirable tokens
     * (as defined in the internals of this class) filtered out if the filterWords param was set when constructing the
     * instance of this class.
     *
     * Also strips raw integer values, since these are likely to skew the results poorly.
     */
    public List<String> tokenizeEmail(EmailData email) {
        StringTokenizer tokenizer = new StringTokenizer(email.contents, " \t\n\r\f");
        List<String> tokens = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().toLowerCase();
            tokensParsed++;
            if (stripTokens) {
                // Strip the chosen tokens, plus any numbers
                if (!completeListOfTokensToStrip.contains(token) && !token.matches("\\d+")) {
                    tokens.add(token);
                    tokensAccepted++;
                }
                else {
                    tokensStripped++;
                }
            }
            else {
                tokens.add(token);
                tokensAccepted++;
            }
        }

        return tokens;
    }

}
