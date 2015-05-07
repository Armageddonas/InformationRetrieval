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
    int tf;
    double idf;
    double tf_idf = 0;

    public CollectionDoc(int id, int doclenght, int tf) {
        this.id = id;
        this.doclenght = doclenght;
        this.tf = tf;
    }

    public boolean CompareId(CollectionDoc myDoc) {
        if (this.id == myDoc.id) {
            return true;
        }
        return false;
    }
}
