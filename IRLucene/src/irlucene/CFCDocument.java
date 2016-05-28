/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irlucene;


/**
 *
 * @author thiago
 */
public class CFCDocument{
    
    private String paperNumber;
    private String recordNumber;
    private String acessionNumber;
    private String authors;
    private String title;
    private String source;
    private String majorSubjects;
    private String minorSubjects;
    private String abstractExtract;
    private String references;
    private String citations;
    
    public CFCDocument(){
        paperNumber = "";
        recordNumber = "";
        acessionNumber = "";
        authors = "";
        title = "";
        source = "";
        majorSubjects = "";
        minorSubjects = "";
        abstractExtract = "";
        references = "";
        citations = "";
    }

    public String getPaperNumber() {
        return paperNumber;
    }

    public void setPaperNumber(String paperNumber) {
        this.paperNumber = paperNumber;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMajorSubjects() {
        return majorSubjects;
    }

    public void setMajorSubjects(String majorSubjects) {
        this.majorSubjects = majorSubjects;
    }

    public String getMinorSubjects() {
        return minorSubjects;
    }

    public void setMinorSubjects(String minorSubjects) {
        this.minorSubjects = minorSubjects;
    }

    public String getAbstractExtract() {
        return abstractExtract;
    }

    public void setAbstractExtract(String abstractExtract) {
        this.abstractExtract = abstractExtract;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getCitations() {
        return citations;
    }

    public void setCitations(String citations) {
        this.citations = citations;
    }

    public String getAcessionNumber() {
        return acessionNumber;
    }

    public void setAcessionNumber(String acessionNumber) {
        this.acessionNumber = acessionNumber;
    }
    
    
    
}
