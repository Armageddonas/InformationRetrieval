import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.exit;

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

    static String RESULTS_DIRECTORY = "results";
    static String RETRIEVED_IDS_PATH = "results/RetrievedIds.csv";
    static String EVAL_MODELS_PATH = "results/EvaluateModels.csv";
    static String QUERY_DESC_PATH = "results/QueryDesc.csv";
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

        //<editor-fold defaultstate="collapsed" desc="Create folder and remove files">
        File theDir = new File(RESULTS_DIRECTORY);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + RESULTS_DIRECTORY);
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }

        String[] myFiles;
        myFiles = theDir.list();
        for (int i = 0; i < myFiles.length; i++) {
            File myFile = new File(theDir, myFiles[i]);
            myFile.delete();
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Initialize files">       
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RETRIEVED_IDS_PATH, true)))) {
            out.println("QueryID,Model,Relevant Doc,TF,TF_IDF,B25");
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(QUERY_DESC_PATH, true)))) {
            out.println("QueryID,database,Description");
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(EVAL_MODELS_PATH, true)))) {
            out.println("QueryID,Model,Recall,Precision,R-Precision");
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        String queriesallwordspath = "queriesallwords.txt";
        String queries5wordspath = "queries5words.txt";

        //Test database value set to 0
        ArrayList<Query> queries5wordsTest = LoadQueries(queries5wordspath, 0);
        ArrayList<Query> queriesallwordsTest = LoadQueries(queriesallwordspath, 0);

        System.out.println(queries5wordsTest + "\n");

        //for (int i = 0; i < queries5words.size(); i++) {
        for (int i = 0; i <= 0; i++) {
            RetrievalModels temp = new RetrievalModels();
            queries5wordsTest.get(i).InitQuery();
            temp.myQuery = queries5wordsTest.get(i);

            System.out.println("Query: " + temp.myQuery.QueryID);//+ ", TF-IDF model retrieved: \n" + temp.TF_IDF_List);
            //temp.RunBooleanModel();
            temp.RunB25();
            temp.EvalB25();

            //temp.RunTF_IDF();
            //temp.EvalTF_IDF();
            //temp.EvalBoolean();
            /*temp.RunBooleanModel();
             System.out.println("Query: " + temp.myQuery.QueryID + ", Boolean model retrieved: \n" + temp.BooleanList);
             temp.RunTF();
             System.out.println("Query: " + temp.myQuery.QueryID + ", TF model retrieved: \n" + temp.TF_List);

             temp.RunTF_IDF();
             System.out.println("Query: " + temp.myQuery.QueryID + ", TF-IDF model retrieved: \n" + temp.TF_IDF_List);
             temp.RunB25();
             System.out.println("Query: " + temp.myQuery.QueryID + ", Okapi model retrieved: \n" + temp.B25_List);

             temp.EvalTF_IDF();
             System.out.println("Query: " + temp.myQuery.QueryID + ", Eval tfidf: \n" + temp.evalTF_IDF.toString());*/
        }
        System.exit(0);

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
            queries.get(i).InitQuery();
            RetrievalModels applyModel = new RetrievalModels();
            applyModel.myQuery = queries.get(i);
            applyModel.RunBooleanModel();
            applyModel.RunTF();
            applyModel.RunTF_IDF();
            applyModel.RunB25();

            applyModel.EvalTF_IDF();
            //Todo add more evaluation functions
        }
    }
}
