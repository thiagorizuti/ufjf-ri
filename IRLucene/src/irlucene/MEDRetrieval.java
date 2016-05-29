/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irlucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author thiago
 */
public class MEDRetrieval {

    private Directory index;
    private Analyzer analyzer;

    public MEDRetrieval(Analyzer analyzer) {
        index = new RAMDirectory();
        this.analyzer = analyzer;
        /*try {
            index = FSDirectory.open(Paths.get("MEDindex/"));
        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    public ArrayList<MEDDocument> readDocumentsFile(String fileName) {
        ArrayList<MEDDocument> docs = new ArrayList();
        try {
            String line;
            String field = "";
            File file = new File(System.getProperty("user.dir"), "data/med/" + fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            MEDDocument doc = new MEDDocument();
            while ((line = reader.readLine()) != null) {
                if (line.length() < 2) {
                    continue;
                }
                if (line.substring(0, 2).contains(".I")) {
                    field = line.substring(0, 2);
                }
                if (line.substring(0, 2).contains(".W")) {
                    field = line.substring(0, 2);
                    continue;
                }
                if (field.contains(".I")) {
                    if (!doc.getId().isEmpty()) {
                        docs.add(doc);
                    }
                    doc = new MEDDocument();
                    doc.setId(doc.getId().concat(line.substring(2)));
                }
                if (field.contains(".W")) {
                    doc.setContent(doc.getContent().concat(line));
                }

            }
            docs.add(doc);

        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docs;
    }

    public ArrayList<QueryData> readQueriesFile() {
        ArrayList<QueryData> queries = new ArrayList<>();
        try {
            String line;
            String field = "";
            int queryId = 1;
            int queryIdOld = 1;
            int rel;
            int relCount = 0;
            File file = new File(System.getProperty("user.dir"), "data/med/MED.QRY");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            QueryData query = new QueryData();
            while ((line = reader.readLine()) != null) {
                if (line.length() < 2) {
                    continue;
                }
                if (line.substring(0, 2).contains(".I")) {
                    field = line.substring(0, 2);
                }
                if (line.substring(0, 2).contains(".W")) {
                    field = line.substring(0, 2);
                    continue;
                }
                if (field.contains(".I")) {
                    if (query.getId() != 0) {
                        queries.add(query);
                    }
                    query = new QueryData();
                    query.setId(Integer.valueOf(line.substring(3)));
                }
                if (field.contains(".W")) {
                    query.setQuery(query.getQuery().concat(line + " "));
                }
            }
            queries.add(query);
            file = new File(System.getProperty("user.dir"), "data/med/MED.REL");
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                queryId = Integer.valueOf(line.split(" ")[0]);
                rel = Integer.valueOf(line.split(" ")[2]);
                queries.get(queryId - 1).getRelevantDocuments().add(rel);
                if (queryId != queryIdOld) {
                    queries.get(queryId - 2).setNumberRelevantDocuments(relCount);
                    relCount = 0;
                }
                queryIdOld = queryId;
                relCount++;
            }
            queries.get(queryId - 1).setNumberRelevantDocuments(relCount);

        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return queries;
    }
    
    public void createIndex() {
        try {
            Document document;
            IndexWriter indexWriter = new IndexWriter(index, new IndexWriterConfig(analyzer));

            ArrayList<MEDDocument> MEDdocs = readDocumentsFile("MED.ALL");

            for (MEDDocument doc : MEDdocs) {
                document = new Document();
                document.add(new StringField("id", doc.getId(), Field.Store.YES));;
                document.add(new TextField("content", doc.getContent(), Field.Store.YES));
                indexWriter.addDocument(document);
            }
            indexWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ScoreDoc[] query(QueryData queryData) {
        HashMap<String, Float> boosts;
        MultiFieldQueryParser queryParser;
        Query q;
        IndexReader indexReader;
        IndexSearcher indexSearcher;
        TopDocs docs;
        ScoreDoc[] hits = null;
        try {
            boosts = new HashMap<>();
            //boosts.put("title", (float) 0.5);
            queryParser = new MultiFieldQueryParser(
                    new String[]{"id", "content"}, analyzer, boosts);
            q = queryParser.parse(queryData.getQuery());
            indexReader = DirectoryReader.open(index);
            indexSearcher = new IndexSearcher(indexReader);
            docs = indexSearcher.search(q, indexReader.numDocs());

            hits = docs.scoreDocs;
            indexReader.close();
        } catch (ParseException | IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hits;
    }
    
    public double[] precisionRecal(QueryData query, ScoreDoc[] hits) {
        double precisionRecall[] = {0, 0};
        int relevantAnswers;
        int answers;
        int relevants;
        IndexReader indexReader;
        IndexSearcher indexSearcher;
        try {
            indexReader = DirectoryReader.open(index);
            indexSearcher = new IndexSearcher(indexReader);
            relevantAnswers = 0;
            answers = hits.length;
            relevants = query.getNumberRelevantDocuments();
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document doc = indexSearcher.doc(docId);
                for (int d : query.getRelevantDocuments()) {
                    if (Integer.valueOf(doc.get("id").trim()) == d) {
                        relevantAnswers++;
                    }
                }
            }
            precisionRecall[0] = (double) relevantAnswers / answers;
            precisionRecall[1] = (double) relevantAnswers / relevants;
        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return precisionRecall;
    }

    public double fMeasure(double precisionRecall[]) {
        return 2 * (precisionRecall[0] * precisionRecall[1]) / (precisionRecall[0] + precisionRecall[1]);
    }

    public double pAtN(QueryData query, ScoreDoc[] hits, int n) {
        double pAtN = 0;
        int limit;
        int relevantAnswers;
        IndexReader indexReader;
        IndexSearcher indexSearcher;
        try {
            indexReader = DirectoryReader.open(index);
            indexSearcher = new IndexSearcher(indexReader);
            relevantAnswers = 0;
            if (n > hits.length) {
                limit = hits.length;
            } else {
                limit = n;
            }
            for (int i = 0; i < limit; ++i) {
                int docId = hits[i].doc;
                Document doc = indexSearcher.doc(docId);
                for (int d : query.getRelevantDocuments()) {
                    if (d == Integer.valueOf(doc.get("id").trim())) {
                        relevantAnswers++;
                    }
                }
            }
            pAtN = 100 * relevantAnswers / n;
        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pAtN;
    }

    public int numDocs() {
        int numDocs = 0;
        try {
            IndexReader indexReader = DirectoryReader.open(index);
            numDocs = indexReader.numDocs();
        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numDocs;
    }

    public void printHits(ScoreDoc[] hits) {
        try {
            IndexReader indexReader = DirectoryReader.open(index);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = indexSearcher.doc(docId);
                System.out.println((i + 1) + " " + d.get("id"));
            }
        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
