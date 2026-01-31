import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    // [[ ALL OF MAIN PROVIDED ]]
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = recordGuess(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // [[ PROVIDED ]]
    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // [[ PROVIDED ]]
    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // [[ PROVIDED ]]
    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

    // Behavior:
    //   - Prunes a dictionary so that it only contains unique words of the desired
    //     length, returning this result as a set of strings.
    // Parameters:
    //   - List<String> contents - the contents of the dictionary to be pruned
    //   - int wordLength - the desired length of the words that will be left in the
    //     dictionary
    // Returns:
    //   - Set<String> - the set that will contain the pruned dictionary of unique words
    //     of length wordLength
    // Exceptions:
    //   - throws IllegalArgumentException if the word length passed in is less than 1
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if (wordLength < 1) {
            throw new IllegalArgumentException("word length must be at least 1");
        }
        Set<String> words = new HashSet<>();
        for (int i = 0; i < contents.size(); i++) {
            String currentWord = contents.get(i);
            if (currentWord.length() == wordLength) {
                words.add(currentWord);
            }
        }
        return words;
    }

    // Behavior:
    //   - Returns the pattern for the user's guess corresponding to the largest number
    //     of words left to choose from. If there are multiple patterns that yield the
    //     same number of words remaining, the pattern that comes first alphabetically 
    //     will be chosen. The dictionary of possible words is reduced to the remaining
    //     words corresponding to the chosen pattern.
    // Parameters:
    //   - String guess - the guess used to produce the possible patterns to choose from
    //   - Set<String> words - the dictionary that will be used to find the pattern
    //     corresponding to the most words still remaining. The set will then be reduced
    //     to only contain the remaining words.
    //   - int wordLength - the length of the words in the dictionary
    // Returns:
    //   - String - the pattern corresponding to the least pruning of the dictionary
    // Exceptions:
    //   - throws IllegalArgumentException if the length of the guess does not equal
    //     wordLength or if the set of words is empty
    public static String recordGuess(String guess, Set<String> words, int wordLength) {
        if (words.isEmpty() || guess.length() != wordLength) {
            throw new IllegalArgumentException("set of words is empty or guess does not " +
                "have the correct length"); 
        }
        Map<String, Set<String>> targetWordGroups = new TreeMap<>();
        addPatternsToMap(guess, words, targetWordGroups);
        String maxPattern = "";
        int maxWords = 0;
        for (String pattern : targetWordGroups.keySet()) {
            int numOfWords = targetWordGroups.get(pattern).size();
            if (numOfWords > maxWords) {
                maxWords = numOfWords;
                maxPattern = pattern;
            }
        }
        words.clear();
        words.addAll(targetWordGroups.get(maxPattern));
        return maxPattern;
    }

    // Behavior:
    //   - Generates and returns the pattern for a given target word and guess. A green
    //     tile indicates that a character in guess is in the same spot in word, a
    //     yellow tile means that a character in guess is in word, but in a different
    //     location, and a gray tile means that a character in guess is not in word.
    // Parameters:
    //   - String word - the target word that guess will be compared to
    //   - String guess - the guess that has each of its characters compared to those in
    //     word 
    // Returns:
    //   - String - the pattern that resembles the accuracy of each character in guess
    //     in relation to word
    public static String patternFor(String word, String guess) {
        List<String> patternList = new ArrayList<>();
        for (int i = 0; i < guess.length(); i++) {
            patternList.add(guess.substring(i, i + 1));
        }
        Map<Character, Integer> unusedCharacters = new TreeMap<>();
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            if (unusedCharacters.containsKey(currentChar)) {
                unusedCharacters.put(currentChar, unusedCharacters.get(currentChar) + 1);
            } else {
                unusedCharacters.put(currentChar, 1);
            }
        }
        setGreens(word, patternList, unusedCharacters);
        return setYellowsAndGrays(guess, patternList, unusedCharacters);
    }

    // Behavior:
    //   - Sets a character of a guess to a green tile when it is the same as the 
    //     character at the corresponding index of the target word.
    // Parameters:
    //   - String word - the target word that the letters of the guess will be compared
    //     to
    //   - List<String> patternList - the list whose elements correspond to characters
    //     in a guess and will be compared to the characters of word 
    //   - Map<Character, Integer> unusedCharacters - the map that is used to determine
    //     how many instances of a given character in target word have not yet been
    //     matched to letters in the user's guess.
    // Returns:
    //   - void
    public static void setGreens(String word, List<String> patternList, Map<Character, Integer>
        unusedCharacters) {
        for (int i = 0; i < patternList.size(); i++) {
            if (patternList.get(i).equals(word.substring(i, i + 1))) {
                patternList.set(i, GREEN);
                unusedCharacters.put(word.charAt(i), unusedCharacters.get(word.charAt(i)) - 1);
            }
        }
    }

    // Behavior:
    //   - Sets the characters of a guess to yellow tiles when they are not already set
    //     to green and there are instances of this character in the target word that
    //     have not yet been matched to letters in the guess. If an element does not
    //     meet this criteria and is not set to a green tile, it is set to gray. Once
    //     all tiles are set, a String representing the pattern is returned.
    // Parameters:
    //   - String guess - the guess whose characters will be checked to determine if the
    //     elements of the list should be set to a yellow tile or gray tile
    //   - List<String> patternList - the list corresponding to the characters of guess
    //     that is being altered to create a pattern
    //   - Map<Character, Integer> unusedCharacters - the map that is used to determine
    //     if a character has unused instances and if an element of the list should be
    //     set to a yellow tile
    // Returns:
    //   - String - the pattern that resembles the accuracy of each character in guess
    public static String setYellowsAndGrays(String guess, List<String> patternList, 
        Map<Character, Integer> unusedCharacters) {
        String pattern = "";
        for (int i = 0; i < patternList.size(); i++) {
            if (!patternList.get(i).equals(GREEN) && unusedCharacters.containsKey
                (guess.charAt(i)) && unusedCharacters.get(guess.charAt(i)) > 0) {
                patternList.set(i, YELLOW);
                unusedCharacters.put(guess.charAt(i), unusedCharacters.get(guess.
                    charAt(i)) - 1);
            } else if (!patternList.get(i).equals(GREEN)){
                patternList.set(i, GRAY);
            }
            pattern += patternList.get(i);
        }
        return pattern;
    }

    // Behavior:
    //   - Adds all possible patterns that can result from a guess and a set of target
    //     words to a map and pairs each pattern to the set of all given words that
    //     yield that pattern. 
    // Parameters:
    //   - String guess - the guess that will be checked against each target word in a
    //     dictionary of words to create patterns
    //   - Set<String> words - the dictionary of all the words that will be compared to
    //     guess
    //   - Map<String, Set<String>> targetWordGroups - the map that will hold the 
    //     patterns and their corresponding set of words
    // Returns:
    //   - void
    public static void addPatternsToMap(String guess, Set<String> words, Map<String, 
        Set<String>> targetWordGroups) {
        for (String word : words) {
            String pattern = patternFor(word, guess);
            if (targetWordGroups.containsKey(pattern)) {
                targetWordGroups.get(pattern).add(word);
            } else {
                Set<String> targetWords = new HashSet<>();
                targetWords.add(word);
                targetWordGroups.put(pattern, targetWords);
            }
        }
    }
}
