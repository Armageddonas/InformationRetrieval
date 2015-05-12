import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Evaluation {

    double recall;
    double precision;
    Point[] standard11;
    double r_precision;
    int QueryId;

    //All retrieved docs
    int retrievedDocsTotal;
    //Number of relevant docs retrieved
    int relevantDocsRetrieved;

    //Number of all relevant documents
    int relevantDocsTotal;

    ArrayList<Integer> retrievedList;
    ArrayList<Integer> evaluationList;

    public double CalcRecall() {
        return (double) relevantDocsRetrieved / relevantDocsTotal;
    }

    public double CalcRecall(int listNumber) {
        int relevantDocsRetrieved = 0;

        for (int i = 0; i < listNumber; i++) {
            if (evaluationList.contains(retrievedList.get(i))) {
                relevantDocsRetrieved++;
            }
        }

        return (double) relevantDocsRetrieved / relevantDocsTotal;
    }

    public double CalcPrecision() {
        return (double) relevantDocsRetrieved / retrievedDocsTotal;
    }

    public double CalcPrecision(int listNumber) {
        int relevantDocsRetrieved = 0;

        for (int i = 0; i < listNumber; i++) {
            if (evaluationList.contains(retrievedList.get(i))) {
                relevantDocsRetrieved++;
            }
        }

        return (double) relevantDocsRetrieved / retrievedDocsTotal;
    }

    public Point[] Calc11Points() {
        ArrayList<Point> AllPoints = new ArrayList();
        Point[] st11 = new Point[11];

        for (int i = 0; i < retrievedList.size(); i++) {
            Point temp = new Point();
            temp.x = CalcRecall(i);
            temp.y = CalcPrecision(i);
            AllPoints.add(temp);
        }

        //<editor-fold defaultstate="collapsed" desc="Simplify curve">
        for (int i = 0; i < 11; i++) {
            st11[i] = new Point(i * 0.1, 0);
        }

        for (int i = 0; i < AllPoints.size(); i++) {
            int indexSt11 = (int) AllPoints.get(i).x * 10;

            if (st11[indexSt11].y <= AllPoints.get(i).y) {
                st11[indexSt11].y = AllPoints.get(i).y;
            }
        }
        //</editor-fold>
        return st11;
    }

    private ArrayList<Integer> LoadDocs(String filepath) {

        ArrayList<Integer> relDocs = new ArrayList();
        try {

            BufferedReader in = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = in.readLine()) != null) {
                //Save query id and query description
                int queryID = Integer.parseInt(line.split(" ")[0]);
                int docID = Integer.parseInt(line.split(" ")[1]);

                if (queryID == this.QueryId) {
                    relDocs.add(docID);
                }
            }
            in.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return relDocs;
    }

    public double CalcRPrecision() {
        int counter = 0;
        for (int i = 0; i < retrievedList.size() && i < evaluationList.size(); i++) {
            if (evaluationList.contains(retrievedList.get(i))) {
                counter++;
            }
        }

        return (double) counter / relevantDocsTotal;
    }

    public ArrayList LoadEvaluationList(String filepath) {

        ArrayList<Query> list = new ArrayList();
        try {

            BufferedReader in = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = in.readLine()) != null) {
                //Save query id and query description
                int QueryId = Integer.parseInt(line.split(" ")[0]);
                int DocId = Integer.parseInt(line.split(" ")[1]);

                if (this.QueryId == QueryId) {
                    evaluationList.add(DocId);
                }
            }
            in.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InformationRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public void InitializeVariables() {
        retrievedDocsTotal = retrievedList.size();
        relevantDocsTotal = evaluationList.size();

        //<editor-fold defaultstate="collapsed" desc="relevantDocsRetrieved">
        int counter = 0;

        for (int id : retrievedList) {
            if (evaluationList.contains(id)) {
                counter++;
            }
        }

        relevantDocsRetrieved = counter;
        //</editor-fold>
    }

    public Evaluation(ArrayList retrievedList, int qID) {
        if(retrievedList.size()==0)
        {
            System.out.println("Evaluation: Empty list");
            return;
        }
        //<editor-fold defaultstate="collapsed" desc="Keep first 100">
        while (retrievedList.size() > 100) {
            retrievedList.remove(retrievedList.size() - 1);
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Convert arraylist">
        ArrayList<Integer> mylist;
        if (retrievedList.get(0) instanceof CollectionDoc) {
            mylist = new ArrayList();
            for (Object temp : retrievedList) {
                mylist.add(((CollectionDoc) temp).id);
            }
        } //</editor-fold>
        else {
            mylist = (ArrayList<Integer>) retrievedList;
        }

        this.retrievedList = mylist;
        this.QueryId = qID;
        //todo
        evaluationList = LoadDocs("relevance.txt");
        InitializeVariables();
    }

    public void RunEvaluation() {
        precision = CalcPrecision();
        r_precision = CalcRPrecision();
        recall = CalcRecall();
        standard11 = Calc11Points();
    }

    @Override
    public String toString() {
        return "Evaluation{" + "recall=" + recall + ", precision=" + precision + ", standard11=" + standard11 + ", r_precision=" + r_precision + ", QueryId=" + QueryId + '}';
    }

    class Point {

        double x;
        double y;

        public Point() {
        }

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" + "x=" + x + ", y=" + y + '}';
        }

    }
}
