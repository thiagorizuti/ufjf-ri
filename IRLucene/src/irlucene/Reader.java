/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irlucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiago
 */
public class Reader {

    private BufferedReader reader;

    public Reader() {
    }

    public ArrayList<CFCDocument> readCFCFile(String fileName) {
       ArrayList<CFCDocument> docs = new ArrayList();
        try{           
            String line;
            String field = "";
            int start;
            File file = new File(System.getProperty("user.dir"), "data/cfc/"+fileName);
            reader = new BufferedReader(new FileReader(file));
            CFCDocument doc = new CFCDocument();
            while ((line = reader.readLine()) != null) {
                if(line.length() < 2){
                    continue;
                }
                if(!line.substring(0,2).contains(" ")){
                    field = line.substring(0,2);
                    start = 3;
                }else{
                    start = 2;
                }
                if(field.contains("PN")){
                    if(!doc.getPaperNumber().isEmpty()){
                        docs.add(doc);
                    }
                    doc = new CFCDocument();
                    doc.setPaperNumber(doc.getPaperNumber().concat(line.substring(start)));
                }
                if(field.contains("RN")){
                    doc.setRecordNumber(doc.getRecordNumber().concat(line.substring(start)));
                }
                if(field.contains("AN")){
                    doc.setAcessionNumber(doc.getAcessionNumber().concat(line.substring(start)));
                }
                if(field.contains("AU")){
                    doc.setAuthors(doc.getAuthors().concat(line.substring(start)));
                }
                if(field.contains("TI")){
                    doc.setTitle(doc.getTitle().concat(line.substring(start)));
                }
                if(field.contains("SO")){
                    doc.setSource(doc.getSource().concat(line.substring(start)));
                }
                if(field.contains("MJ")){
                    doc.setMajorSubjects(doc.getMajorSubjects().concat(line.substring(start)));
                }
                if(field.contains("MN")){
                    doc.setMinorSubjects(doc.getMinorSubjects().concat(line.substring(start)));
                }
                if(field.contains("AB") || field.contains("EX")){
                    doc.setAbstractExtract(doc.getAbstractExtract().concat(line.substring(start)));
                }
                if(field.contains("RF")){
                    doc.setReferences(doc.getReferences().concat(line.substring(start)));
                }
                if(field.contains("CT")){
                    doc.setCitations(doc.getCitations().concat(line.substring(start)));
                }
                
            }

        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docs;
    }
    
    public ArrayList<MEDDocument> readMEDFile(String fileName) {
       ArrayList<MEDDocument> docs = new ArrayList();
        try{           
            String line;
            String field = "";
            File file = new File(System.getProperty("user.dir"), "data/med/"+fileName);
            reader = new BufferedReader(new FileReader(file));
            MEDDocument doc = new MEDDocument();
            while ((line = reader.readLine()) != null) {
                if(line.length() < 2){
                    continue;
                }
                if(line.substring(0,2).contains(".I")){
                    field = line.substring(0,2);
                }
                if(line.substring(0,2).contains(".W")){
                    field = line.substring(0,2);
                    continue;
                }               
                if(field.contains(".I")){
                    if(!doc.getId().isEmpty()){
                        docs.add(doc);
                    }
                    doc = new MEDDocument();
                    doc.setId(doc.getId().concat(line.substring(2)));
                }
                if(field.contains(".W")){
                    doc.setContent(doc.getContent().concat(line));
                }
                
            }

        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docs;
    }
}
