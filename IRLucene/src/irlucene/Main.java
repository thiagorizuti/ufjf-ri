/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irlucene;

import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.search.ScoreDoc;

/**
 *
 * @author thiago
 */
public class Main {
    /*
    * Analyzers: SimpleAnalyzer, StopAnalyzer, WhitespaceAnalyzer.
    * Calculates and displays the mean f-measure and mean p@5.
    */
    public static void main(String args[]) {
        metricsMeanCFC(new StopAnalyzer(), 0);
        //metricsMeanMED(new StopAnalyzer());
    }

    public static void metricsMeanCFC(Analyzer analyzer, float titleBoost) {
        CFCRetrieval cfc = new CFCRetrieval(analyzer);
        cfc.createIndex();
        ArrayList<QueryData> queries = cfc.readQueriesFile();
        double precisionRecall[];
        double pAt5Mean = 0;
        double fMeasureMean = 0;
        ScoreDoc[] hits;
        for (QueryData query : queries) {
            hits = cfc.query(query, titleBoost);
            precisionRecall = cfc.precisionRecal(query, hits);
            fMeasureMean += cfc.fMeasure(precisionRecall);
            pAt5Mean += cfc.pAtN(query, hits, 5);
        }
        System.out.println("f-measure mean: " + fMeasureMean / queries.size());
        System.out.println("p@5 mean: " + pAt5Mean / queries.size());
    }

    public static void metricsMeanMED(Analyzer analyzer) {
        MEDRetrieval med = new MEDRetrieval(analyzer);
        med.createIndex();
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
    
    public static void prCFC(Analyzer analyzer, float titleBoost) {
        CFCRetrieval cfc = new CFCRetrieval(analyzer);
        cfc.createIndex();
        ArrayList<QueryData> queries = cfc.readQueriesFile();
        double precisionRecall[];
        double pAt5Mean = 0;
        double fMeasureMean = 0;
        ScoreDoc[] hits;
        for (QueryData query : queries) {
            hits = cfc.query(query, titleBoost);
            precisionRecall = cfc.precisionRecal(query, hits);
            System.out.println(precisionRecall[0] + "\t" + precisionRecall[1]);
        }        
    }
    
    public static void prMED(Analyzer analyzer) {
        MEDRetrieval med = new MEDRetrieval(analyzer);
        med.createIndex();
        ArrayList<QueryData> queries = med.readQueriesFile();
        double precisionRecall[];
        double pAt5Mean = 0;
        double fMeasureMean = 0;
        ScoreDoc[] hits;
        for (QueryData query : queries) {
            hits = med.query(query);
            precisionRecall = med.precisionRecal(query, hits);
            System.out.println(precisionRecall[0] + "\t" + precisionRecall[1]);
        }        
    }
}
