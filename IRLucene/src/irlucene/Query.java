/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irlucene;

import java.util.ArrayList;

/**
 *
 * @author thiago
 */
public class Query {
    
    private int id;
    private String query;
    private int numberRelevantDocuments;
    private ArrayList<Integer> relevantDocuments;
    
    public Query(){
        query = "";
        relevantDocuments = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberRelevantDocuments() {
        return numberRelevantDocuments;
    }

    public void setNumberRelevantDocuments(int numberRelevantDocuments) {
        this.numberRelevantDocuments = numberRelevantDocuments;
    }

    public ArrayList<Integer> getRelevantDocuments() {
        return relevantDocuments;
    }

    public void setRelevantDocuments(ArrayList<Integer> relevantDocuments) {
        this.relevantDocuments = relevantDocuments;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }   
    
}
