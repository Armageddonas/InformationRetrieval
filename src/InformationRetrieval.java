import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    public static ArrayList LoadQueries(String filepath, int database) {
        ArrayList<Query> Queries = new ArrayList();
        try {

            BufferedReader in = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = in.readLine()) != null) {
                //Save query id and query description
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String queriesallwordspath = "queriesallwords.txt";
        String queries5wordspath = "queries5words.txt";

        //Test database value set to 0
        ArrayList<Query> queries5words = LoadQueries(queries5wordspath,0);
        ArrayList<Query> queriesallwords = LoadQueries(queriesallwordspath,0);

        System.out.println(queries5words);
        //System.out.println("\n"+queries5words.get);
        //for (int i = 0; i < queries5words.size(); i++) {
        for (int i = 2; i <= 2; i++) {
            queries5words.get(i).RunQuery();
            RetrievalModels temp = new RetrievalModels();
            temp.myQuery = queries5words.get(i);
            temp.RunBooleanModel();
            temp.RunTF_IDF();
            System.out.println("Query: " + queries5words.get(i).id + ", Boolean model retrieved: \n" + temp.BooleanList);
            System.out.println("Query: " + queries5words.get(i).id + ", TF-IDF model retrieved: \n" + temp.TF_IDF_List);
        }

        /*queries5words.get(4).RunQuery();
         RetrievalModels temp = new RetrievalModels();
         temp.myQuery = queries5words.get(4);
         temp.RunBooleanModel();
         System.out.println("Retrieved: \n" + temp.BooleanList);*/
    }

}
