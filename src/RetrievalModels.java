import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetrievalModels {

    Query myQuery;
    ArrayList<Integer> BooleanList;
    ArrayList<CollectionDoc> TF_IDF_List;
    ArrayList<CollectionDoc> TF_List;
    ArrayList<CollectionDoc> B25_List;
    Evaluation booleanEval;
    Evaluation TFEval;
    Evaluation TF_IDFEval;
    Evaluation B25_Eval;

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

        //<editor-fold defaultstate="collapsed" desc="Calculate idf for every word and sum them">
        for (int i = 0; i < allRetrievedDocs.size(); i++) {
            int counter = 0;
            for (int j = 0; j < allRetrievedDocs.size(); j++) {
                if (allRetrievedDocs.get(i).CompareId(allRetrievedDocs.get(j)) == true) {
                    counter++;
                    if (counter > 1) {
                        allRetrievedDocs.get(i).tf_idf += allRetrievedDocs.get(j).tf_idf;
                        allRetrievedDocs.remove(j);
                    }
                }
            }
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
        TF_IDF_List = allRetrievedDocs;
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

        //<editor-fold defaultstate="collapsed" desc="Sum tf">
        for (int i = 0; i < allRetrievedDocs.size(); i++) {
            int counter = 0;
            for (int j = 0; j < allRetrievedDocs.size(); j++) {
                if (allRetrievedDocs.get(i).CompareId(allRetrievedDocs.get(j)) == true) {
                    counter++;
                    if (counter > 1) {
                        allRetrievedDocs.get(i).tf += allRetrievedDocs.get(j).tf;
                        allRetrievedDocs.remove(j);
                    }
                }
            }
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

        //<editor-fold defaultstate="collapsed" desc="Calculate b25 for every word and sum them">
        for (int i = 0; i < allRetrievedDocs.size(); i++) {
            int counter = 0;
            for (int j = 0; j < allRetrievedDocs.size(); j++) {
                if (allRetrievedDocs.get(i).CompareId(allRetrievedDocs.get(j)) == true) {
                    counter++;
                    if (counter > 1) {
                        allRetrievedDocs.get(i).b25 += allRetrievedDocs.get(j).b25;
                        allRetrievedDocs.remove(j);
                    }
                }
            }
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

        /*//<editor-fold defaultstate="collapsed" desc="Keep first 100">
         while (allRetrievedDocs.size() > 100) {
         allRetrievedDocs.remove(allRetrievedDocs.size() - 1);
         }
         //</editor-fold>*/
        TF_List = allRetrievedDocs;
    }

    public void EvaluateList(ArrayList<Integer> retrievedList, ArrayList<Integer> evaluationList) {

    }

    public void TF_IDFEval() {
        TF_IDFEval = new Evaluation(TF_IDF_List, myQuery.QueryID);
        TF_IDFEval.RunEvaluation();
    }
}
