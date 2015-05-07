import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetrievalModels {

    Query myQuery;
    ArrayList<Integer> BooleanList;
    ArrayList<Document> TF_IDF_List;
    Evaluation booleanEval;
    Evaluation TFEval;
    Evaluation TF_IDFEval;

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

            //Check if every word has the id in the list
            for (int i = 0; i < allWords.size(); i++) {
                if (!allWords.get(i).CollectionIds.contains(allIds.get(j))) {
                    inAllDocuments = false;
                }
            }
            //If the id is in the list of all the words save it
            if (inAllDocuments == true) {
                intersectCollection.add(allIds.get(j));
            }
        }
        //</editor-fold>

        BooleanList = intersectCollection;
    }

    public void RunTF_IDF() {
        ArrayList<QueryWord> allWords = myQuery.allWords;
        ArrayList<CollectionDoc> allIds = new ArrayList();

        //<editor-fold defaultstate="collapsed" desc="Create list with all the ids">
        for (int i = 0; i < allWords.size(); i++) {
            for (int j = 0; j < allWords.get(i).CollectionIds.size(); j++) {
                CollectionDoc temp = allWords.get(i).CollectionIds.get(j);

                allIds.add(temp);
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Calculate idf for every word and sum them">
        for (int i = 0; i < allIds.size(); i++) {
            int counter = 0;
            for (int j = 0; j < allIds.size(); j++) {
                if (allIds.get(i).CompareId(allIds.get(j)) == true) {
                    counter++;
                    allIds.get(i).tf_idf += allIds.get(j).idf * allIds.get(j).tf;
                    if (counter > 1) {
                        allIds.remove(j);
                    }
                }
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Sorting">        
        Collections.sort(allIds, new Comparator<Document>() {
            @Override
            public int compare(Document doc1, Document doc2) {
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

        //<editor-fold defaultstate="collapsed" desc="Keep first 100">
        while (allIds.size() > 100) {
            allIds.remove(allIds.size() - 1);
        }
        //</editor-fold>
        TF_IDF_List = allIds;
    }

    public void EvaluateList(ArrayList<Integer> retrievedList, ArrayList<Integer> evaluationList) {

    }
    /*
     public void Statistics(String FileName) {
    
     File f = new File(FileName + ".txt");
     if (!f.exists()) {
     try {
     FileOutputStream fout = new FileOutputStream(f);
    
     BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));
     bw.write(recall + "," + precision + "," + standard11 + "," + r_precision + "," + QueryId + "," +);
     } catch (IOException ex) {
     Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
    
     try {
     FileOutputStream fout = new FileOutputStream(FileName + ".txt", true);
    
     BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));
    
     bw.write("Query ID: " + QueryId);
     bw.newLine();
     bw.close();
     } catch (FileNotFoundException ex) {
     Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
     } catch (IOException ex) {
     Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
     } finally {
     try {
     fos.close();
     } catch (IOException ex) {
     Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     }*/
    /*
     class Document {
    
     int id;
     double tf;
     double idf;
     double tf_idf = 0;
    
     public Document(int id, double idf, double tf) {
     this.id = id;
     this.idf = idf;
     this.tf = tf;
     }
    
     public boolean CompareId(Document myDoc) {
     if (this.id == myDoc.id) {
     return true;
     }
     return false;
     }
    
     @Override
     public String toString() {
     return "Document{" + "id=" + id + ", idf=" + idf + ", tf_idf=" + tf_idf + '}' + "\n";
     }
    
     }*/
}
