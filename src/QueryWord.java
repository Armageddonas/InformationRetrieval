import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Konstantinos Chasiotis
 */
public class QueryWord  implements Serializable{

    //<editor-fold defaultstate="collapsed" desc="Query">
    String theWord;
    int wordFrequency;
    double wordTF;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Collection">
    int df;
    int ctf;
    double idf;
    ArrayList<CollectionDoc> CollectionIds = new ArrayList();
    //</editor-fold>

    /**
     * Searches for the integer key in the sorted array a[].
     *
     * @param key the search key
     * @param a the array of integers, must be sorted in ascending order
     * @return index of key in array a[] if present; -1 if not present
     */
    //http://algs4.cs.princeton.edu/11model/BinarySearch.java.html
    public static int rank(int key, int[] a) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;
            if (key < a[mid]) {
                hi = mid - 1;
            } else if (key > a[mid]) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    public boolean containsID(int id) {

        //<editor-fold defaultstate="collapsed" desc="Sorting">        
        Collections.sort(CollectionIds, new Comparator<CollectionDoc>() {
            @Override
            public int compare(CollectionDoc doc1, CollectionDoc doc2) {
                int returnVal = 0;

                if (doc1.id < doc2.id) {
                    returnVal = -1;
                } else if (doc1.id > doc2.id) {
                    returnVal = 1;
                } else {
                    returnVal = 0;
                }

                return returnVal;
            }
        });
        //</editor-fold>

        if (rank(id, CollectionIdsToArray()) != -1) {
            return true;
        } else {
            return false;
        }
        /*for (int i = 0; i < CollectionIds.size(); i++) {
         if (CollectionIds.get(i).id == id) {
         return true;
         }
         }
         return false;*/
    }

    private int[] CollectionIdsToArray() {
        int[] ids = new int[CollectionIds.size()];
        for (int i = 0; i < CollectionIds.size(); i++) {
            ids[i] = CollectionIds.get(i).id;
        }
        return ids;
    }

    public QueryWord(String theWord) {
        this.theWord = theWord;
    }

    public boolean Compare(QueryWord aWord) {
        if (theWord.equals(aWord.theWord)) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "QueryWord{" + "theWord=" + theWord + ", CollectionIds=" + CollectionIds + '}';
    }

    public void LoadServerData(QueryWord ServerData) {
        this.df = ServerData.df;
        this.ctf = ServerData.ctf;
        this.CollectionIds = ServerData.CollectionIds;
    }
}
