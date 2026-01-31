# Absurdle

Absurdle is an adversarial variant of Wordle in which the game manager does **not** choose a secret word at the start. Instead, the manager dynamically adapts to the player’s guesses, always returning feedback that preserves the largest possible set of remaining valid words. The game supports words of any length, which the player selects at the beginning of the game.

The goal of Absurdle is to make the game as difficult as possible while still following standard Wordle feedback rules.

---

## How the Game Works

Absurdle is played using a fixed dictionary of valid words. The player chooses the word length at the start of the game, and only words of that length are considered valid targets.

Each turn proceeds as follows:

1. **Generate Patterns**
   For the player’s guess, the manager computes the Wordle-style feedback pattern (🟩, 🟨, ⬜) for every remaining candidate word.

2. **Group by Pattern**
   Candidate words are grouped by the feedback pattern they would produce for that guess.

3. **Choose the Worst Case**
   The manager selects the pattern associated with the *largest* group of words, minimizing how much the dictionary is reduced.

4. **Tie-Breaking**
   If multiple patterns contain the same maximum number of words, the manager chooses the pattern that comes first in alphabetical order.

5. **Repeat Until Forced**
   The game continues until only one word remains. When the player guesses that word, the manager must return 🟩🟩🟩🟩 and the game ends.

---

## Key Differences from Wordle

* No secret word is chosen at the start
* The player chooses the word length
* Feedback is adversarial but always valid
* The game maximizes ambiguity rather than minimizing guesses
* Deterministic tie-breaking ensures consistent behavior

---

## Purpose

This project demonstrates worst-case reasoning, pattern generation, and adversarial decision-making under strict rules. It is well-suited for coursework, algorithm practice, or exploring game design under adversarial constraints.

---

## Notes

* The dictionary is intentionally small for clarity and testing
* All feedback follows standard Wordle rules
* The manager never lies—it simply chooses the least helpful valid response

---

Have fun playing against a game that doesn’t want you to win!
