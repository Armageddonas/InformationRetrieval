
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    static String RESULTS_DIRECTORY = "results";
    static String MODELS_DATA_DIRECTORY = "data";
    static String RETRIEVED_IDS_PATH = "results/RetrievedIds.csv";
    static String EVAL_MODELS_PATH = "results/EvaluateModels.csv";
    static String QUERY_DESC_PATH = "results/QueryDesc.csv";
    static String RETRIEVED_IDS_BOOLEAN_PATH = "results/RetrievedBooleanIds.csv";
    static String queries5SanitizedPath = "queries5Sanitized.txt";
    static String queriesAllSanitizedPath = "queries5Sanitized.txt";
    static String TYPE_ALL_WORDS_QUERY = "allwords";
    static String TYPE_FIVE_WORDS_QUERY = "fivewords";
    static boolean flagMultiThtread = false;
    static boolean flagSaveQueries = false;

    public static ArrayList LoadQueries(String filepath, int database, String type) {
        ArrayList<Query> Queries = new ArrayList();
        try {

            BufferedReader in = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = in.readLine()) != null) {
                //Save query QueryID and query description
                int id = Integer.parseInt(line.split("#")[0]);
                String description = line.split("#")[1];

                Query temp;
                temp = new Query(description, id, database, type);

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

    private static String PrintDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String queriesallwordspath = "queriesallwords.txt";
        String queries5wordspath = "queries5words.txt";

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

        //<editor-fold defaultstate="collapsed" desc="if the directory does not exist, create it">
        File theDir2 = new File(MODELS_DATA_DIRECTORY);
        if (!theDir2.exists()) {
            System.out.println("creating directory: " + MODELS_DATA_DIRECTORY);
            boolean result = false;

            try {
                theDir2.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
        //</editor-fold>

        String[] myFiles;
        myFiles = theDir.list();
        for (int i = 0; i < myFiles.length; i++) {
            File myFile = new File(theDir, myFiles[i]);
            myFile.delete();
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Initialize files">       
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RETRIEVED_IDS_PATH, true)))) {
            out.println("QueryID,Database,Model,Relevant Doc,TF,TF_IDF,B25");
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(QUERY_DESC_PATH, true)))) {
            out.println("QueryID,Database,Description,Type");
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RETRIEVED_IDS_BOOLEAN_PATH, true)))) {
            out.println("QueryID,Database,Id,Type");
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(EVAL_MODELS_PATH, true)))) {
            out.println("QueryID,Type,Database,Model,Recall,Precision,R-Precision");
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        if (flagMultiThtread == false) {
            //<editor-fold defaultstate="collapsed" desc="Initialize queries">
            for (int i = 0; i < 4; i++) {
                //for (int i = 0; i < 4; i++) {
                System.out.println("\n#Database: " + i + "\tFive Words");
                ArrayList<Query> queries5words = LoadQueries(queries5wordspath, i, TYPE_FIVE_WORDS_QUERY);
                runAllModels(i, queries5words);

                System.out.println("\n#Database: " + i + "\tAll Words");
                ArrayList<Query> queriesallwords = LoadQueries(queriesallwordspath, i, TYPE_ALL_WORDS_QUERY);
                runAllModels(i, queriesallwords);
            }
            //</editor-fold>
        } //<editor-fold defaultstate="collapsed" desc="Debugging">
        else {

            //System.exit(0);
            for (int i = 0; i < 4; i++) {

                ArrayList<Query> queriesallwords = LoadQueries(queriesallwordspath, i, TYPE_ALL_WORDS_QUERY);
                RunQueryMOdels thread1 = new RunQueryMOdels(queriesallwords, i);
                thread1.setPriority(Thread.MAX_PRIORITY);
                thread1.start();

                ArrayList<Query> queries5words = LoadQueries(queries5wordspath, i, TYPE_FIVE_WORDS_QUERY);
                RunQueryMOdels thread2 = new RunQueryMOdels(queries5words, i);
                thread2.setPriority(Thread.NORM_PRIORITY);
                thread2.start();
            }
            //</editor-fold>
        }
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
                RemoveStopwords(queries);
                StemQueries(queries);
                break;
            default:
                System.out.println("Error: Wrong database number");
                break;
        }

        for (int i = 0; i < queries.size(); i++) {
            Query temp = queries.get(i);
            String filename = temp.database + "-" + temp.QueryID + "-" + temp.type + ".data";

            System.out.println("--------------------------------------------------------------------------" + "\n"
                    + "Query: " + i + " | id: " + temp.QueryID + " | DB: " + temp.database + " | Started on: " + PrintDateTime() + "\nDescription: " + temp.Description
                    + "\n" + "--------------------------------------------------------------------------");

            RetrievalModels loadedModel;
            if (flagSaveQueries == true && (loadedModel = LoadQueryInfo(filename)) != null) {
                loadedModel.EvalAllModels();
            } else {
                RetrievalModels applyModel = new RetrievalModels();
                queries.get(i).InitQuery();
                applyModel.myQuery = queries.get(i);
                applyModel.RunAllModels();
                if (flagSaveQueries == true) {
                    StoreQueryInfo(applyModel, filename);
                }
                applyModel.EvalAllModels();
            }
        }

    }

    public static class RunQueryMOdels extends Thread {

        ArrayList<Query> queries;
        int i;

        public RunQueryMOdels(ArrayList<Query> queries, int i) {
            super("RunQueryMOdels");
            this.queries = queries;
            this.i = i;
        }

        public void run() {
            runAllModels(i, queries);
        }
    }

    public static void StoreQueryInfo(RetrievalModels Model, String filename) {
        try {

            FileOutputStream fout = new FileOutputStream(MODELS_DATA_DIRECTORY + "\\" + filename);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(Model);
            oos.close();

        } catch (Exception ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static RetrievalModels LoadQueryInfo(String filename) {

        RetrievalModels Model;

        try {

            FileInputStream fin = new FileInputStream(MODELS_DATA_DIRECTORY + "\\" + filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Model = (RetrievalModels) ois.readObject();
            ois.close();

            //System.out.println("Load successfully");
            return Model;

        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Failed to load file: " + filename);
            return null;
        }
    }

}
