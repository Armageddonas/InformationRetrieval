import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Konstantinos Chasiotis
 */
public class InformationRetrieval {

    static String queries5SanitizedPath = "queries5Sanitized.txt";
    static String queriesAllSanitizedPath = "queries5Sanitized.txt";

    public static ArrayList LoadQueries(String filepath, int database) {
        ArrayList<Query> Queries = new ArrayList();
        try {

            BufferedReader in = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = in.readLine()) != null) {
                //Save query QueryID and query description
                int id = Integer.parseInt(line.split("#")[0]);
                String description = line.split("#")[1];

                Query temp;
                temp = new Query(description, id, database);

                Queries.add(temp);
            }
            in.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Queries;
    }

    private static void StemQueries(ArrayList<Query> queries) {
        for (int i = 0; i < queries.size(); i++) {
            queries.get(i).Description = new Stemmer().Run(queries.get(i).Description);
        }
    }

    private static void RemoveStopwords(ArrayList<Query> queries) {
        for (int i = 0; i < queries.size(); i++) {
            queries.get(i).Description = new StopwordRemover().Run(queries.get(i).Description);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String queriesallwordspath = "queriesallwords.txt";
        String queries5wordspath = "queries5words.txt";

        //Test database value set to 0
        ArrayList<Query> queries5wordsTest = LoadQueries(queries5wordspath, 0);
        ArrayList<Query> queriesallwordsTest = LoadQueries(queriesallwordspath, 0);

        System.out.println(queriesallwordsTest + "\n");
        //QueryToFile(queriesallwordsTest);
        //System.out.println(new Stemmer().Run(new String[]{queries5SanitizedPath}) + "\n");
        StemQueries(queriesallwordsTest);
        System.out.println("stemmer \n" + queriesallwordsTest + "\n");
        //for (int i = 0; i < queries5words.size(); i++) {
        for (int i = 2; i <= 2; i++) {
            queries5wordsTest.get(i).RunQuery();
            RetrievalModels temp = new RetrievalModels();
            temp.myQuery = queries5wordsTest.get(i);
            /*temp.RunBooleanModel();
             System.out.println("Query: " + queries5words.get(i).QueryID + ", Boolean model retrieved: \n" + temp.BooleanList);
             temp.RunTF();
             System.out.println("Query: " + queries5words.get(i).QueryID + ", TF model retrieved: \n" + temp.TF_List);
             */
            temp.RunTF_IDF();
            /*System.out.println("Query: " + queries5words.get(i).QueryID + ", TF-IDF model retrieved: \n" + temp.TF_IDF_List);
             temp.RunB25();
             System.out.println("Query: " + queries5words.get(i).QueryID + ", Okapi model retrieved: \n" + temp.B25_List);
             */
            temp.TF_IDFEval();
            System.out.println("Query: " + queries5wordsTest.get(i).QueryID + ", Eval tfidf: \n" + temp.TF_IDFEval.toString());

        }

        
        
        //<editor-fold defaultstate="collapsed" desc="Initialize queries">
        for (int i = 0; i < 4; i++) {
            ArrayList<Query> queries5words = LoadQueries(queries5wordspath, i);
            ArrayList<Query> queriesallwords = LoadQueries(queriesallwordspath, i);

            runAllModels(i, queries5words);
            runAllModels(i, queriesallwords);
        }
        //</editor-fold>
    }

    private static void runAllModels(int database, ArrayList<Query> queries) {

        switch (database) {
            case 0:
                break;
            case 1:
                StemQueries(queries);
                break;
            case 2:
                RemoveStopwords(queries);
                break;
            case 3:
                StemQueries(queries);
                RemoveStopwords(queries);
                break;
            default:
                System.out.println("Error: Wrong database number");
                break;

        }

        for (int i = 0; i < queries.size(); i++) {
            queries.get(i).RunQuery();
            RetrievalModels temp = new RetrievalModels();
            temp.myQuery = queries.get(i);
            temp.RunBooleanModel();
            temp.RunTF();
            temp.RunTF_IDF();
            temp.RunB25();

            temp.TF_IDFEval();
            //Todo add more evaluation functions
        }
    }
}
