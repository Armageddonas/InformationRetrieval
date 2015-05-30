import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetrievalModels implements Serializable{

    Query myQuery;
    ArrayList<Integer> BooleanList = new ArrayList();
    ArrayList<CollectionDoc> TF_List = new ArrayList();
    ArrayList<CollectionDoc> TF_IDF_List = new ArrayList();
    ArrayList<CollectionDoc> B25_List = new ArrayList();
    Evaluation evalBoolean;
    Evaluation evalTF;
    Evaluation evalTF_IDF;
    Evaluation evalB25;
    String RETRIEVED_IDS_PATH = "results/RetrievedIds.csv";
    String RETRIEVED_IDS_BOOLEAN_PATH = "results/RetrievedBooleanIds.csv";
    String EVAL_MODELS_PATH = "results/EvaluateModels.csv";

    public void RunBooleanModel() {
        System.out.println("Run model Boolean");
        ArrayList<Integer> allIds = new ArrayList();
        ArrayList<QueryWord> allWords = myQuery.allWords;

        //<editor-fold defaultstate="collapsed" desc="Create list with all the ids">
        for (int i = 0; i < allWords.size(); i++) {
            for (int j = 0; j < allWords.get(i).CollectionIds.size(); j++) {
                Integer tempId = allWords.get(i).CollectionIds.get(j).id;
                if (!allIds.contains(tempId)) {
                    allIds.add(tempId);
                }
            }
        }
        //</editor-fold>

        ArrayList<Integer> intersectCollection = new ArrayList();

        //<editor-fold defaultstate="collapsed" desc="Find the intersect of the lists">
        //Access all ids
        for (int j = 0; j < allIds.size(); j++) {
            boolean inAllDocuments = true;

            //<editor-fold defaultstate="collapsed" desc="Check if every word has the QueryID in the list">
            for (int i = 0; i < allWords.size(); i++) {
                if (!allWords.get(i).containsID(allIds.get(j))) {
                    inAllDocuments = false;
                    break;
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="If the QueryID is in the list of all the words save it">
            if (inAllDocuments == true) {
                intersectCollection.add(allIds.get(j));
            }
            //</editor-fold>
        }
        //</editor-fold>

        BooleanList = new ArrayList(intersectCollection);
        StoreRetrievedIds("Boolean", BooleanList);
    }

    public void EvalTF_IDF() {
        System.out.println("Run evaluation for TF-IDF model");
        evalTF_IDF = new Evaluation(TF_IDF_List, myQuery.QueryID);
        evalTF_IDF.RunEvaluation();
        StoreEvalStats("TF-IDF", evalTF_IDF);
    }

    public void EvalTF() {
        System.out.println("Run evaluation for TF model");
        evalTF = new Evaluation(TF_List, myQuery.QueryID);
        evalTF.RunEvaluation();
        StoreEvalStats("TF", evalTF);
    }

    public void EvalBoolean() {
        System.out.println("Run evaluation for Boolean model");
            evalBoolean = new Evaluation(BooleanList, myQuery.QueryID);
            evalBoolean.RunEvaluation();
            StoreEvalStats("Boolean", evalBoolean);
    }

    public void EvalB25() {
        System.out.println("Run evaluation for B25 model");
        evalB25 = new Evaluation(B25_List, myQuery.QueryID);
        evalB25.RunEvaluation();
        StoreEvalStats("B25", evalB25);
    }

    public void EvalAllModels() {
        EvalTF_IDF();
        EvalTF();
        EvalBoolean();
        EvalB25();
    }

    public void RunAllModels() {
        RunBooleanModel();

        System.out.println("Run all models opt");
        ArrayList<QueryWord> allWords = myQuery.allWords;
        ArrayList<CollectionDoc> allRetrievedDocs = new ArrayList();

        //<editor-fold defaultstate="collapsed" desc="Create list with all the ids">
        for (int i = 0; i < allWords.size(); i++) {
            for (int j = 0; j < allWords.get(i).CollectionIds.size(); j++) {
                CollectionDoc temp = allWords.get(i).CollectionIds.get(j);

                allRetrievedDocs.add(temp);
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Sorting doc ids">
        Collections.sort(allRetrievedDocs, new Comparator<CollectionDoc>() {
            @Override
            public int compare(CollectionDoc doc1, CollectionDoc doc2) {
                int returnVal = 0;

                if (doc1.id > doc2.id) {
                    returnVal = -1;
                } else if (doc1.id < doc2.id) {
                    returnVal = 1;
                } else {
                    returnVal = 0;
                }

                return returnVal;
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Calculate tfidf,tf,b25 for every doc by sum them">
        int i = 0;
        while (i < allRetrievedDocs.size() - 1) {
            while (i < allRetrievedDocs.size() - 1 && allRetrievedDocs.get(i).CompareId(allRetrievedDocs.get(i + 1)) == true) {
                allRetrievedDocs.get(i).WeightTF += allRetrievedDocs.get(i + 1).WeightTF;
                allRetrievedDocs.get(i).WeightTF_IDF += allRetrievedDocs.get(i + 1).WeightTF_IDF;
                allRetrievedDocs.get(i).b25 += allRetrievedDocs.get(i + 1).b25;
                allRetrievedDocs.remove(i + 1);
            }
            i++;
        }

        //</editor-fold>
        B25_List = new ArrayList(allRetrievedDocs);
        //<editor-fold defaultstate="collapsed" desc="Sorting">        
        Collections.sort(B25_List, new Comparator<CollectionDoc>() {
            @Override
            public int compare(CollectionDoc doc1, CollectionDoc doc2) {
                int returnVal = 0;

                if (doc1.b25 > doc2.b25) {
                    returnVal = -1;
                } else if (doc1.b25 < doc2.b25) {
                    returnVal = 1;
                } else {
                    returnVal = 0;
                }

                return returnVal;
            }
        });
        //</editor-fold>

        TF_IDF_List = new ArrayList(allRetrievedDocs);
        //<editor-fold defaultstate="collapsed" desc="Sorting">        
        Collections.sort(TF_IDF_List, new Comparator<CollectionDoc>() {
            @Override
            public int compare(CollectionDoc doc1, CollectionDoc doc2) {
                int returnVal = 0;

                if (doc1.WeightTF_IDF > doc2.WeightTF_IDF) {
                    returnVal = -1;
                } else if (doc1.WeightTF_IDF < doc2.WeightTF_IDF) {
                    returnVal = 1;
                } else {
                    returnVal = 0;
                }

                return returnVal;
            }
        });
        //</editor-fold>

        TF_List = new ArrayList(allRetrievedDocs);
        
        //<editor-fold defaultstate="collapsed" desc="Sorting">        
        Collections.sort(TF_List, new Comparator<CollectionDoc>() {
            @Override
            public int compare(CollectionDoc doc1, CollectionDoc doc2) {
                int returnVal = 0;

                if (doc1.WeightTF > doc2.WeightTF) {
                    returnVal = -1;
                } else if (doc1.WeightTF < doc2.WeightTF) {
                    returnVal = 1;
                } else {
                    returnVal = 0;
                }

                return returnVal;
            }
        });
        //</editor-fold>     
    }
    

    public void StoreRetrievedIds(String model, ArrayList retrievedList) {
        if (1 == 1) {
            System.out.println("Warning: Skipped store retrieved ids function");
            return;
        }

        if (retrievedList.isEmpty()) {
            System.out.println("Warning: No ids retrieved to store");
            return;
        }
        //<editor-fold defaultstate="collapsed" desc="For non boolean models">
        ArrayList<Integer> mylist;
        if (retrievedList.get(0) instanceof CollectionDoc) {

            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RETRIEVED_IDS_PATH, true)))) {
                for (CollectionDoc temp : (ArrayList<CollectionDoc>) retrievedList) {
                    out.println(myQuery.QueryID + "," + myQuery.database + "," + model + "," + temp.id + "," + temp.tf + "," + temp.tf_idf + "," + temp.b25);
                }
            } catch (IOException ex) {
                Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
            }
        } //</editor-fold>
        else {
            mylist = (ArrayList<Integer>) retrievedList;

            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RETRIEVED_IDS_BOOLEAN_PATH, true)))) {
                for (int temp : mylist) {
                    out.println(myQuery.QueryID + "," + myQuery.database + "," + temp + "," + myQuery.type);
                }
            } catch (IOException ex) {
                Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void StoreEvalStats(String modelName, Evaluation modelStats) {

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(EVAL_MODELS_PATH, true)))) {
            //out.println(myQuery.QueryID + "," + modelName + "," + modelStats.recall + "%," + modelStats.precision + "%," + modelStats.r_precision+"%");
            out.println(myQuery.QueryID + "," + myQuery.type + "," + myQuery.database + "," + modelName + "," + modelStats.recall + "," + modelStats.precision + "," + modelStats.r_precision);
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
