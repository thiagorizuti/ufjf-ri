/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irlucene;

import java.util.ArrayList;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.ScoreDoc;

/**
 *
 * @author thiago
 */
public class Main {

    public static void main(String args[]) {
        doMED();
    }
    
    public static void doCFC(){
        CFCRetrieval cfc = new CFCRetrieval(new StandardAnalyzer());
        cfc.createIndex();
        System.out.println(cfc.numDocs());
        ArrayList<QueryData> queries = cfc.readQueriesFile();
        ScoreDoc[] hits = cfc.query(queries.get(0));
        double precisionRecall[] = cfc.precisionRecal(queries.get(0), hits);
        double fmeasure = cfc.fMeasure(precisionRecall);
        double pAt5 = cfc.pAtN(queries.get(0), hits, 5);
        System.out.println("precision: " + precisionRecall[0] + " recall: " + precisionRecall[1]);
        System.out.println("fmeasure: " + fmeasure);
        System.out.println("pAt5: " + pAt5);
    }
    
    public static void doMED(){
        MEDRetrieval med = new MEDRetrieval(new StandardAnalyzer());
        med.createIndex();
        System.out.println(med.numDocs());
        ArrayList<QueryData> queries = med.readQueriesFile();
        ScoreDoc[] hits = med.query(queries.get(0));
        double precisionRecall[] = med.precisionRecal(queries.get(0), hits);
        double fmeasure = med.fMeasure(precisionRecall);
        double pAt5 = med.pAtN(queries.get(0), hits, 5);
        System.out.println("precision: " + precisionRecall[0] + " recall: " + precisionRecall[1]);
        System.out.println("fmeasure: " + fmeasure);
        System.out.println("pAt5: " + pAt5);
    }
}
