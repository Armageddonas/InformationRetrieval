
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Konstantinos Chasiotis
 */
public class Query implements Serializable {

    int QueryID;
    String Description;
    String type;
    ArrayList<QueryWord> allWords;
    double maxFrequency;
    int database;
    int CollectionSize = 84678;
    int docAveLength;
    static String QUERY_DESC_PATH = "results/QueryDesc.csv";

    //Split every word of the query so you can set attributes
    private void InitializeAllWords() {
        String[] temp = Description.split(" ");

        allWords = new ArrayList();

        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].trim();
            //Todo: change if query tf added allwords.contains
            if (!temp[i].equals("") && !allWords.contains(temp[i])) {
                allWords.add(new QueryWord(temp[i]));
            }
        }

    }

    public void InitQuery() {
        StoreProcessedQuery();
        FindFrequency();
        FindMaxFrequency();
        FindTF();

        //TODO: Crawler finds df, exists, Collection IDs
        for (int i = 0; i < allWords.size(); i++) {
            QueryWord temp = CrawlDocValues(allWords.get(i).theWord);
            while (temp == null) {
                temp = CrawlDocValues(allWords.get(i).theWord);
            }
            allWords.get(i).LoadServerData(temp);
        }

        InitializeModelsValues();
    }

    private void InitDatabaseValues() {
        switch (database) {
            case 0:
                docAveLength = 493;
                break;
            case 1:
                docAveLength = 493;
                break;
            case 2:
                docAveLength = 288;
                break;
            case 3:
                docAveLength = 288;
                break;
            default:
                System.out.println("Error: wrong database number");
                exit(1);
        }
    }

    private QueryWord CrawlDocValues(String Word) {

        try {
            QueryWord temp = new QueryWord(Word);

            String url = "http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?g=p&d=" + database + "&v=" + Word;
            //Sample link: http://fiji4.ccs.neu.edu/~zerg/lemurcgi/lemur.cgi?d=0&v=will
            Document doc = Jsoup.connect(url).maxBodySize(2 * 2048000).get();
            doc.select("hr").remove();
            doc.select("center").remove();

            String[] data = doc.getElementsByTag("body")
                    .text().split(" ");

            //<editor-fold defaultstate="collapsed" desc="Debugging">
            //System.out.println("The word: '" + Word + "'");
            //System.out.println(doc.getElementsByTag("body").text());
            //</editor-fold>
            //Get ctf
            temp.ctf = Integer.parseInt(data[0]);
            //Get df
            temp.df = Integer.parseInt(data[1]);
            for (int i = 2; i < data.length; i += 3) {
                //System.out.println(Integer.parseInt(data[i]));
                int id = Integer.parseInt(data[i]);
                int doclenght = Integer.parseInt(data[i + 1]);
                int tf = Integer.parseInt(data[i + 2]);

                temp.CollectionIds.add(new CollectionDoc(id, doclenght, tf));
            }
            return temp;
        } catch (IOException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void InitializeModelsValues() {
        for (int i = 0; i < allWords.size(); i++) {
            QueryWord curWord = allWords.get(i);
            //<editor-fold defaultstate="collapsed" desc="Find idf for every word">      
            double wordIDF = Math.log((double) CollectionSize / allWords.get(i).df);
            allWords.get(i).idf = wordIDF;
            //</editor-fold>

            for (int j = 0; j < allWords.get(i).CollectionIds.size(); j++) {
                CollectionDoc temp = allWords.get(i).CollectionIds.get(j);
                //<editor-fold defaultstate="collapsed" desc="collection tf">
                allWords.get(i).CollectionIds.get(j).tf
                        = ((double) temp.frequency / (temp.frequency + 0.5 + 1.5 + (temp.doclenght / docAveLength)));
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="collection tf idf">
                allWords.get(i).CollectionIds.get(j).tf_idf
                        = temp.tf * wordIDF;
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Okapi">
                double term1 = (((0 + 0.5) / (0 - 0 + 0.5))
                        / ((curWord.df - 0 + 1 / 2) / (CollectionSize - wordIDF - 0 + 0 + 1 / 2)));
                double term2 = ((1.2 + 1) * temp.tf)
                        / (1.2 * ((1 - 0.75) + 0.75 + (temp.doclenght / docAveLength)) + temp.tf);
                double term3 = ((100 + 1) * temp.tf) / (100 + temp.tf);
                allWords.get(i).CollectionIds.get(j).b25 += Math.log(term1 * term2 * term3);
                //</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="calc weight">
                allWords.get(i).CollectionIds.get(j).WeightTF = allWords.get(i).CollectionIds.get(j).tf * allWords.get(i).wordTF;
                allWords.get(i).CollectionIds.get(j).WeightTF_IDF = allWords.get(i).CollectionIds.get(j).tf_idf * allWords.get(i).wordTF;// * allWords.get(i).CollectionIds.get(j).tf_idf;
                //</editor-fold>
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Maybe later TF">
    private void FindTF() {
        for (int i = 0; i < allWords.size(); i++) {
            allWords.get(i).wordTF = (double) allWords.get(i).wordFrequency / maxFrequency;
        }

    }

    private void FindFrequency() {

        for (int i = 0; i < allWords.size(); i++) {
            int counter = 0;
            //Compare with all the words of the query
            for (int j = 0; j < allWords.size(); j++) {
                if (allWords.get(i).Compare(allWords.get(j)) == true) {
                    counter++;
                    allWords.get(i).wordFrequency = counter;
                }
            }
        }
    }

    //Finds the term with the biggest wordTF
    private void FindMaxFrequency() {
        double max = 0;
        for (int i = 0; i < allWords.size(); i++) {
            if (allWords.get(i).wordFrequency > max) {
                max = allWords.get(i).wordFrequency;
            }
        }
        maxFrequency = max;
    }

    //</editor-fold>
    public Query(String Description, int id, int database, String type) {
        this.Description = SanitizeText(Description);
        this.QueryID = id;
        this.database = database;
        this.type = type;
        InitializeAllWords();
        InitDatabaseValues();
    }

    @Override
    public String toString() {
        return "Query{" + "id=" + QueryID + ", Description=" + Description + '}' + "\n";
    }

    private static String SanitizeText(String Text) {
        String temp = Text.replaceAll("\\p{Punct}+", " ");

        temp = temp.toLowerCase();
        return temp;
    }

    public void StoreProcessedQuery() {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(QUERY_DESC_PATH, true)))) {
            out.println(QueryID + "," + database + "," + Description);
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
