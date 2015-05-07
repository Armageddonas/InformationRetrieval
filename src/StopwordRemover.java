/**
 * Homework 3 10/03/14 CSC320
 *
 * @author Adam Bavosa
 */
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Stopword remover. Removes stopwords from tokenizer input (token.txt)
 */
public class StopwordRemover {

    public static void main(String[] args) throws Exception {
        BufferedReader tokenFile = new BufferedReader(new FileReader("token.txt"));
        BufferedReader stopFile = new BufferedReader(new FileReader("inquery"));
        ArrayList<String> stopWords = new ArrayList<String>();
        String nonStopWord, tokenLine;
        boolean isStopword = false;

        PrintWriter writer = new PrintWriter("toStemmer.txt", "UTF-8");

        String stopWord;
        while ((stopWord = stopFile.readLine()) != null) {
            stopWords.add(stopWord);
        }

        while ((tokenLine = tokenFile.readLine()) != null) {
            String[] tokenLineWords = tokenLine.split(" ");

            for (int j = 0; j < tokenLineWords.length; j++) {
                isStopword = false;
                for (int i = 0; i < stopWords.size(); i++) {
                    //System.out.println(stopWords.get(i) + "\t\t stopword " + nonStopWord);
                    if (stopWords.get(i).equals(tokenLineWords[j])) {
                        isStopword = true;
                    }
                    /*if (i == (stopWordCount - 1)) {
                     writer.println(nonStopWord);
                     }*/
                }
                if (isStopword == false) {
                    writer.print(tokenLineWords[j]+" ");
                }
            }
        }

        writer.close();
        tokenFile.close();
        stopFile.close();
        System.out.println("Next run: java Stemmer toStemmer.txt");
    }
}
