package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * @author Mack_TB
 */

public class Main {
    private static int numSentences = 0;
    private static int numWords = 0;
    private static int numSymbols = 0;
    private static int numSyllable = 0;
    private static int numPolySyllable = 0;
    private static int numCharacters = 0;

    public static void main(String[] args) {
        if (args.length != 0) {
            String pathToFile = args[0];
            try (Scanner scanner = new Scanner(new File(pathToFile))) {
                System.out.println("The text is:");
                String regex = "[.!?]";
                String regex2 = "\\s";
                String[] strings; // to get different characters
                String[] sentences;
                String[] words;
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    System.out.print(line + " ");
                    sentences = line.split(regex);
                    for (int i = 0; i < sentences.length; i++) {
                        String sentence = sentences[i];
                        if (sentence.matches("\\s.+")) {
                            sentence = sentence.replaceFirst("\\s", "");
                        }

                        words = sentence.split(regex2);
                        numWords += words.length;

                        for (String word : words) {
                            numCharacters += word.length();
                        }

                        int[] numSyllablenPoly = countSyllablenPoly(words);
                        numSyllable += numSyllablenPoly[0];
                        numPolySyllable += numSyllablenPoly[1];
                    }
                    numSentences += sentences.length;
                    // count number of characters
                    strings = line.split(regex2);
                    for (String str : strings) {
                        numSymbols += str.length();
                    }
                }

                System.out.println("\n\nWords: " + numWords);
                System.out.println("Sentences: " + numSentences);
                System.out.println("Characters: " + numSymbols);
                System.out.println("Syllables: " + numSyllable);
                System.out.println("Polysyllables: " + numPolySyllable);

                System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
                String scoreType = new Scanner(System.in).next();
                System.out.println();

                computeScore(scoreType);

            } catch (FileNotFoundException e) {
                System.err.println("File not founded " + e.getMessage());
            }
        } else {
            System.out.println("Please, enter the name of the file in argument");
        }

    }

    private static void computeScore(String scoreType) {
        String df;
        double avgAge = 0;
        String[] score = new String[2];

        double scoreARI = 4.71 * ((double) numSymbols / numWords) +
                0.5 * ((double) numWords / numSentences) -
                21.43;
        double scoreFK = 0.39 * ((double) numWords / numSentences) +
                11.8 * ((double) numSyllable / numWords) -
                15.59;
        double scoreSMOG = 1.043 * Math.sqrt(numPolySyllable * (double) 30 / numSentences) +
                3.1291;

        double L = (double) numCharacters / numWords * 100; // average number of character per 100 words
        double S = (double) numSentences / numWords * 100; // average number of sentence per 100 words
        double scoreCL = 0.0588 * L - 0.296 * S - 15.8;

        switch (scoreType) {
            case "ARI":
                score = getScoreDfnAge(scoreARI);
                System.out.printf("Automated Readability Index: %s (about %d year olds).%n", score[0], Integer.parseInt(score[1]));
                break;

            case "FK":
                score = getScoreDfnAge(scoreFK);
                System.out.printf("Flesch–Kincaid readability tests: %s (about %d year olds).%n", score[0], Integer.parseInt(score[1]));
                break;

            case "SMOG":
                score = getScoreDfnAge(scoreSMOG);
                System.out.printf("Simple Measure of Gobbledygook: %s (about %d year olds).%n", score[0], Integer.parseInt(score[1]));
                break;

            case "CL":
                score = getScoreDfnAge(scoreCL);
                System.out.printf("Coleman–Liau index: %s (about %d year olds).%n", score[0], Integer.parseInt(score[1]));
                break;

            case "all":
                score = getScoreDfnAge(scoreARI);
                System.out.printf("Automated Readability Index: %s (about %d year olds).%n", score[0], Integer.parseInt(score[1]));
                avgAge += Integer.parseInt(score[1]);

                score = getScoreDfnAge(scoreFK);
                System.out.printf("Flesch-Kincaid readability tests: %s (about %d year olds).%n", score[0], Integer.parseInt(score[1]));
                avgAge += Integer.parseInt(score[1]);

                score = getScoreDfnAge(scoreSMOG);
                System.out.printf("Simple Measure of Gobbledygook: %s (about %d year olds).%n", score[0], Integer.parseInt(score[1]));
                avgAge += Integer.parseInt(score[1]);

                //scoreCL = Math.ceil(scoreCL);
                score = getScoreDfnAge(scoreCL);
                System.out.printf("Coleman-Liau index: %s (about %d year olds).%n", score[0], Integer.parseInt(score[1]));
                avgAge += Integer.parseInt(score[1]);

                avgAge /= 4;
                break;

            default:
                System.out.println("Please choose one of these options (ARI, FK, SMOG, CL, all)");
                break;
        }

        if (scoreType.equals("all")) {
            df = new DecimalFormat("#.##").format(avgAge);
            System.out.printf("\nThis text should be understood in average by %s year olds.", df);
        } /*else {
            System.out.printf("\nThis text should be understood by %s year olds", Integer.parseInt(score[1]));
        }*/
    }

    private static int[] countSyllablenPoly(String[] words) {
        String vowels = "aeiouyAEIOUY";
        int numVowels, numSyllable = 0, numPolySyllable = 0;;
        for (String word : words) {
//                            System.out.print(word+" ");
            if (word.matches(".+e")) {
                word = word.substring(0, word.length()-1);
            } // remove e when it comes at the end of the word
            char[] letters = word.toCharArray();
            numVowels = 0;
            for (int j = 0; j < letters.length; j++) {
                char letter = letters[j];
                if (vowels.contains(String.valueOf(letter))) {
                    if (j + 1 <= letters.length - 1 && vowels.contains(String.valueOf(letters[j + 1]))) {
                        if (j + 2 <= letters.length - 1 && vowels.contains(String.valueOf(letters[j + 2]))) {
                            j++; // to avoid counting triple successive vowels
                        }
                        j++; // to avoid counting double successive vowels
                    }
                    numVowels++;
                }
            }
            if (numVowels == 0) {
                numSyllable++;
            } else {
                numSyllable += numVowels;
                if (numVowels >= 3) {
                    numPolySyllable++;
                }
            }
            //System.out.println("syllable="+numVowels);
        }
        return new int[]{numSyllable, numPolySyllable};
    }

    private static String[] getScoreDfnAge(double scoreARI) {
        String df = new DecimalFormat("#.##").format(scoreARI);
        int scoreRounded = (int) Math.round(scoreARI);
        int age = findAge(scoreRounded);
        return new String[]{df, String.valueOf(age)};
    }

    private static int findAge(int score) {
        String rangeAge = null;
        int age = 1; // age is about the upper bound in rangeAge
        switch (score) {
            case 1:
                rangeAge = "5-6";
                break;
            case 2:
                rangeAge = "6-7";
                break;
            case 3:
                rangeAge = "7-9";
                break;
            case 13:
                rangeAge = "18-24";
                break;
            case 14:
                rangeAge = "24+";
                break;
            default:
                int j = 9;
                for (int i = 4; i <= 12; i++) {
                    String range = j + "-" + (j + 1);
                    if (i == score) {
                        rangeAge = range;
                        break;
                    }
                    j++;
                }
                break;
        }

        if (rangeAge != null) {
            if (rangeAge.matches("\\d-\\d+")) {
                age = Integer.parseInt(rangeAge.substring(2));
            } else if (rangeAge.matches("\\d{2}-\\d{2}")) {
                age = Integer.parseInt(rangeAge.substring(3));
            } else {
                age = 24;
            }
        }
        return age;
    }

}
