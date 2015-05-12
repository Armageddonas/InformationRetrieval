import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetrievalModels {

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

            //Check if every word has the QueryID in the list
            for (int i = 0; i < allWords.size(); i++) {
                //if (!allWords.get(i).containsID(allIds.get(j))) {
                if (!allWords.get(i).containsID(allIds.get(j))) {
                    inAllDocuments = false;
                }
            }
            //If the QueryID is in the list of all the words save it
            if (inAllDocuments == true) {
                intersectCollection.add(allIds.get(j));
            }
        }
        //</editor-fold>

        BooleanList = intersectCollection;
        StoreRetrievedIds("Boolean", BooleanList);
    }

    public void RunTF() {
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

        //<editor-fold defaultstate="collapsed" desc="Calculate tfidf for every doc by sum them">
        int i = 0;
        while (i < allRetrievedDocs.size() - 1) {
            while (i < allRetrievedDocs.size() - 1 && allRetrievedDocs.get(i).CompareId(allRetrievedDocs.get(i + 1)) == true) {
                allRetrievedDocs.get(i).tf += allRetrievedDocs.get(i + 1).tf;
                allRetrievedDocs.remove(i + 1);
            }
            i++;
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Sorting">        
        Collections.sort(allRetrievedDocs, new Comparator<CollectionDoc>() {
            @Override
            public int compare(CollectionDoc doc1, CollectionDoc doc2) {
                int returnVal = 0;

                if (doc1.tf_idf > doc2.tf_idf) {
                    returnVal = -1;
                } else if (doc1.tf_idf < doc2.tf_idf) {
                    returnVal = 1;
                } else {
                    returnVal = 0;
                }

                return returnVal;
            }
        });
        //</editor-fold>

        /*//<editor-fold defaultstate="collapsed" desc="Keep first 100">
         while (allRetrievedDocs.size() > 100) {
         allRetrievedDocs.remove(allRetrievedDocs.size() - 1);
         }
         //</editor-fold>*/
        TF_List = allRetrievedDocs;
        StoreRetrievedIds("TF", TF_List);
    }

    public void RunTF_IDF() {
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

        //<editor-fold defaultstate="collapsed" desc="Calculate tfidf for every doc by sum them">
        int i = 0;
        while (i < allRetrievedDocs.size() - 1) {
            while (i < allRetrievedDocs.size() - 1 && allRetrievedDocs.get(i).CompareId(allRetrievedDocs.get(i + 1)) == true) {
                allRetrievedDocs.get(i).tf_idf += allRetrievedDocs.get(i + 1).tf_idf;
                allRetrievedDocs.remove(i + 1);
            }
            i++;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Sorting">        
        Collections.sort(allRetrievedDocs, new Comparator<CollectionDoc>() {
            @Override
            public int compare(CollectionDoc doc1, CollectionDoc doc2) {
                int returnVal = 0;

                if (doc1.tf_idf > doc2.tf_idf) {
                    returnVal = -1;
                } else if (doc1.tf_idf < doc2.tf_idf) {
                    returnVal = 1;
                } else {
                    returnVal = 0;
                }

                return returnVal;
            }
        });
        //</editor-fold>

        TF_IDF_List = allRetrievedDocs;
        StoreRetrievedIds("TF-IDF", TF_IDF_List);
    }

    public void RunB25() {
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

        //<editor-fold defaultstate="collapsed" desc="Calculate tfidf for every doc by sum them">
        int i = 0;
        while (i < allRetrievedDocs.size() - 1) {
            while (i < allRetrievedDocs.size() - 1 && allRetrievedDocs.get(i).CompareId(allRetrievedDocs.get(i + 1)) == true) {
                allRetrievedDocs.get(i).b25 += allRetrievedDocs.get(i + 1).b25;
                allRetrievedDocs.remove(i + 1);
            }
            i++;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Sorting">        
        Collections.sort(allRetrievedDocs, new Comparator<CollectionDoc>() {
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

        B25_List = allRetrievedDocs;
        StoreRetrievedIds("B25", B25_List);
    }

    public void EvaluateList(ArrayList<Integer> retrievedList, ArrayList<Integer> evaluationList) {

    }

    public void EvalTF_IDF() {
        evalTF_IDF = new Evaluation(TF_IDF_List, myQuery.QueryID);
        evalTF_IDF.RunEvaluation();
        StoreEvalStats("TF-IDF", evalTF_IDF);
    }

    public void EvalTF() {
        evalTF = new Evaluation(TF_List, myQuery.QueryID);
        evalTF.RunEvaluation();
        StoreEvalStats("TF", evalTF);
    }

    public void EvalBoolean() {
        if (BooleanList.size() != 0) {
            evalBoolean = new Evaluation(BooleanList, myQuery.QueryID);
            evalBoolean.RunEvaluation();
            StoreEvalStats("Boolean", evalBoolean);
        }
    }

    public void EvalB25() {
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
        RunTF();
        RunB25();
        RunBooleanModel();
        RunTF_IDF();
    }

    public RetrievalModels() {

    }

    public void StoreRetrievedIds(String model, ArrayList retrievedList) {
        if (retrievedList.size() == 0) {
            System.out.println("No ids to store");
            return;
        }
        //<editor-fold defaultstate="collapsed" desc="For non boolean models">
        ArrayList<Integer> mylist;
        if (retrievedList.get(0) instanceof CollectionDoc) {

            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RETRIEVED_IDS_PATH, true)))) {
                for (CollectionDoc temp : (ArrayList<CollectionDoc>) retrievedList) {
                    out.println(myQuery.QueryID + "," + model + "," + temp.id + "," + temp.tf + "," + temp.tf_idf + "," + temp.b25);
                }
            } catch (IOException ex) {
                Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
            }
        } //</editor-fold>
        else {
            mylist = (ArrayList<Integer>) retrievedList;

            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RETRIEVED_IDS_BOOLEAN_PATH, true)))) {
                for (int temp : mylist) {
                    out.println(myQuery.QueryID + "," + temp);
                }
            } catch (IOException ex) {
                Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void StoreEvalStats(String modelName, Evaluation modelStats) {

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(EVAL_MODELS_PATH, true)))) {
            out.println(myQuery.QueryID + "," + modelName + "," + modelStats.recall + "," + modelStats.precision + "," + modelStats.r_precision);
        } catch (IOException ex) {
            Logger.getLogger(RetrievalModels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
