/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irlucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
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

    public void createIndex() {
        try {
            Document document;
            Reader reader = new Reader();
            IndexWriter indexWriter = new IndexWriter(index, new IndexWriterConfig(analyzer));

            ArrayList<CFCDocument> CFCdocs = new ArrayList();
            CFCdocs.addAll(reader.readCFCFile("cf74"));
            CFCdocs.addAll(reader.readCFCFile("cf75"));
            CFCdocs.addAll(reader.readCFCFile("cf76"));
            CFCdocs.addAll(reader.readCFCFile("cf77"));
            CFCdocs.addAll(reader.readCFCFile("cf78"));
            CFCdocs.addAll(reader.readCFCFile("cf79"));

            for (CFCDocument doc : CFCdocs) {
                document = new org.apache.lucene.document.Document();
                document.add(new StringField("paperNumber", doc.getPaperNumber(), Field.Store.YES));
                document.add(new StringField("recordNumber", doc.getRecordNumber(), Field.Store.YES));
                document.add(new StringField("acessionNumber", doc.getAcessionNumber(), Field.Store.YES));
                document.add(new TextField("auhtors", doc.getAuthors(), Field.Store.YES));
                TextField titleField = new TextField("title", doc.getTitle(), Field.Store.YES);
                //titleField.setBoost((float) 0.5);
                document.add(titleField);
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
    
    public void queryFromFile(){
        try {
            String querystr = "What are the effects of calcium on the physical "
                    + "properties of mucus from CF patients?";
            Query q = new QueryParser("title", analyzer).parse(querystr);
            int hitsPerPage = 1000;
            IndexReader indexReader = DirectoryReader.open(index);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            TopDocs docs = indexSearcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                org.apache.lucene.document.Document d = indexSearcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("paperNumber") + "\t" + d.get("title"));
            }
            indexReader.close();
            index.close();
        } catch (ParseException | IOException ex) {
            Logger.getLogger(CFCRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
