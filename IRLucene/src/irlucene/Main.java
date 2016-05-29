/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irlucene;

import java.util.ArrayList;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
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

    public static void doCFC() {
        CFCRetrieval cfc = new CFCRetrieval(new WhitespaceAnalyzer());
        cfc.createIndex();
        System.out.println(cfc.numDocs());
        ArrayList<QueryData> queries = cfc.readQueriesFile();
        double precisionRecall[];
        double pAt5Mean = 0;
        double fMeasureMean = 0;
        ScoreDoc[] hits;
        for (QueryData query : queries) {
            hits = cfc.query(query);
            precisionRecall = cfc.precisionRecal(query, hits);
            fMeasureMean += cfc.fMeasure(precisionRecall);
            pAt5Mean += cfc.pAtN(query, hits, 5);
        }
        System.out.println("f-measure mean: " + fMeasureMean / queries.size());
        System.out.println("p@5 mean: " + pAt5Mean / queries.size());
    }

    public static void doMED() {
        MEDRetrieval med = new MEDRetrieval(new SimpleAnalyzer());
        med.createIndex();
        System.out.println(med.numDocs());
        ArrayList<QueryData> queries = med.readQueriesFile();
        double precisionRecall[];
        double pAt5Mean = 0;
        double fMeasureMean = 0;
        ScoreDoc[] hits;
        for (QueryData query : queries) {
            hits = med.query(query);
            precisionRecall = med.precisionRecal(query, hits);
            fMeasureMean += med.fMeasure(precisionRecall);
            pAt5Mean += med.pAtN(query, hits, 5);
        }
        System.out.println("f-measure mean: " + fMeasureMean / queries.size());
        System.out.println("p@5 mean: " + pAt5Mean / queries.size());

    }
}
