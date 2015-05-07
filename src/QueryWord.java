import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Konstantinos Chasiotis
 */
public class QueryWord {

    //<editor-fold defaultstate="collapsed" desc="Query">
    String theWord;
    int wordFrequency;
    double tf;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Collection">
    double collTF;
    double collFrequency;
    int df;
    int ctf;
    double idf;
    boolean exists;//Remove
    ArrayList<CollectionDoc> CollectionIds = new ArrayList();
    //</editor-fold>

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
        return "QueryWord{" + "theWord=" + theWord + ", wordFrequency=" + wordFrequency + ", tf=" + tf + ", df=" + df + ", ctf=" + ctf + ", idf=" + idf + ", exists=" + exists + ", CollectionIds=" + CollectionIds + '}';
    }

    public void LoadServerData(QueryWord ServerData) {
        this.df = ServerData.df;
        this.ctf = ServerData.ctf;
        this.CollectionIds = ServerData.CollectionIds;
    }
}
