/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Konstantinos Chasiotis
 */
public class CollectionDoc {

    int id;
    int doclenght;
    int frequency;
    double tf;
    double tf_idf;

    public CollectionDoc(int id, int doclenght, int f) {
        this.id = id;
        this.doclenght = doclenght;
        this.frequency = f;
    }

    public boolean CompareId(CollectionDoc myDoc) {
        if (this.id == myDoc.id) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "CollectionDoc{" + "id=" + id + ", tf_idf=" + tf_idf + '}'+"\n";
    }

    
}
