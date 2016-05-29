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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author thiago
 */
public class CFCRetrieval {

    private Directory index;
    private Analyzer analyzer;

    public CFCRetrieval(Analyzer analyzer) {
        try {
            index = FSDirectory.open(Paths.get("CFCindex/"));
            this.analyzer = analyzer;
        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<CFCDocument> readDocumentsFile(String fileName) {
        ArrayList<CFCDocument> docs = new ArrayList();
        try {
            String line;
            String field = "";
            int start;
            File file = new File(System.getProperty("user.dir"), "data/cfc/" + fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            CFCDocument doc = new CFCDocument();
            while ((line = reader.readLine()) != null) {
                if (line.length() < 2) {
                    continue;
                }
                if (!line.substring(0, 2).contains(" ")) {
                    field = line.substring(0, 2);
                    start = 3;
                } else {
                    start = 2;
                }
                if (field.contains("PN")) {
                    if (!doc.getPaperNumber().isEmpty()) {
                        docs.add(doc);
                    }
                    doc = new CFCDocument();
                    doc.setPaperNumber(doc.getPaperNumber().concat(line.substring(start)));
                }
                if (field.contains("RN")) {
                    doc.setRecordNumber(doc.getRecordNumber().concat(line.substring(start)));
                }
                if (field.contains("AN")) {
                    doc.setAcessionNumber(doc.getAcessionNumber().concat(line.substring(start)));
                }
                if (field.contains("AU")) {
                    doc.setAuthors(doc.getAuthors().concat(line.substring(start)));
                }
                if (field.contains("TI")) {
                    doc.setTitle(doc.getTitle().concat(line.substring(start)));
                }
                if (field.contains("SO")) {
                    doc.setSource(doc.getSource().concat(line.substring(start)));
                }
                if (field.contains("MJ")) {
                    doc.setMajorSubjects(doc.getMajorSubjects().concat(line.substring(start)));
                }
                if (field.contains("MN")) {
                    doc.setMinorSubjects(doc.getMinorSubjects().concat(line.substring(start)));
                }
                if (field.contains("AB") || field.contains("EX")) {
                    doc.setAbstractExtract(doc.getAbstractExtract().concat(line.substring(start)));
                }
                if (field.contains("RF")) {
                    doc.setReferences(doc.getReferences().concat(line.substring(start)));
                }
                if (field.contains("CT")) {
                    doc.setCitations(doc.getCitations().concat(line.substring(start)));
                }

            }
            docs.add(doc);

        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docs;
    }

    public ArrayList<QueryData> readQueriesFile() {
        ArrayList<QueryData> queries = new ArrayList<>();
        try {
            String line;
            String field = "";
            String token;
            int start;
            boolean read = true;
            File file = new File(System.getProperty("user.dir"), "data/cfc/cfquery");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringTokenizer stringTokenizer;
            QueryData query = new QueryData();
            while ((line = reader.readLine()) != null) {
                if (line.length() < 2) {
                    continue;
                }
                if (!line.substring(0, 2).contains(" ")) {
                    field = line.substring(0, 2);
                    start = 3;
                } else {
                    start = 2;
                }
                if (field.contains("QN")) {
                    if (query.getId() != 0) {
                        queries.add(query);
                    }
                    query = new QueryData();
                    query.setId(Integer.valueOf(line.substring(3)));
                }
                if (field.contains("QU")) {
                    query.setQuery(query.getQuery().concat(line.substring(start)));
                }
                if (field.contains("NR")) {
                    query.setNumberRelevantDocuments(Integer.valueOf(line.substring(3)));
                }
                if (field.contains("RD")) {
                    stringTokenizer = new StringTokenizer(line, " ");
                    while (stringTokenizer.hasMoreElements()) {
                        token = String.valueOf(stringTokenizer.nextElement());
                        if (!token.contains("RD")) {
                            if (read) {
                                query.getRelevantDocuments().add(Integer.valueOf(token));
                                read = false;
                            } else {
                                read = true;
                            }
                        }
                    }
                }

            }
            queries.add(query);

        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return queries;
    }

    public void createIndex() {
        try {
            Document document;
            IndexWriter indexWriter = new IndexWriter(index, new IndexWriterConfig(analyzer));

            ArrayList<CFCDocument> CFCdocs = new ArrayList();
            CFCdocs.addAll(readDocumentsFile("cf74"));
            CFCdocs.addAll(readDocumentsFile("cf75"));
            CFCdocs.addAll(readDocumentsFile("cf76"));
            CFCdocs.addAll(readDocumentsFile("cf77"));
            CFCdocs.addAll(readDocumentsFile("cf78"));
            CFCdocs.addAll(readDocumentsFile("cf79"));

            for (CFCDocument doc : CFCdocs) {
                document = new Document();
                document.add(new StringField("paperNumber", doc.getPaperNumber(), Field.Store.YES));
                document.add(new StringField("recordNumber", doc.getRecordNumber(), Field.Store.YES));
                document.add(new StringField("acessionNumber", doc.getAcessionNumber(), Field.Store.YES));
                document.add(new TextField("auhtors", doc.getAuthors(), Field.Store.YES));
                document.add(new TextField("title", doc.getTitle(), Field.Store.YES));
                document.add(new TextField("source", doc.getSource(), Field.Store.YES));
                document.add(new TextField("majorSubjects", doc.getMajorSubjects(), Field.Store.YES));
                document.add(new TextField("minorSubjects", doc.getMinorSubjects(), Field.Store.YES));
                document.add(new TextField("abstractExtract", doc.getAbstractExtract(), Field.Store.YES));
                document.add(new TextField("references", doc.getReferences(), Field.Store.YES));
                document.add(new TextField("citations", doc.getCitations(), Field.Store.YES));
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
                    new String[]{"paperNumber", "recordNumber", "acessionNumber", "authors", "title",
                        "source", "majorSubjects", "minorSubjects", "abstractExtract",
                        "references", "citations"}, analyzer, boosts);
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
                    if (Integer.valueOf(doc.get("recordNumber").trim()) == d) {
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
                    if (d == Integer.valueOf(doc.get("recordNumber").trim())) {
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
                System.out.println((i + 1) + " " + d.get("paperNumber") + "\t" + d.get("title"));
            }
        } catch (IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
