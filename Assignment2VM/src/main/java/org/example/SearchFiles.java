package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

// Not all of the above imports are necessary, should try remove them for ease of

public class SearchFiles {


    public static ArrayList<String> CreateQueries(String queriesPath) throws IOException{

        BufferedReader buffer = Files.newBufferedReader(Paths.get(queriesPath), StandardCharsets.UTF_8);
        ArrayList<String> queries = new ArrayList<String>();
        ArrayList<Integer> queryid = new ArrayList<Integer>();

        String queryString = "";
        Integer queryID = 1;
        String line = buffer.readLine();

        System.out.println("Reading in queries...");
        Boolean test = true;
        Boolean collect = false;

        while ((line = buffer.readLine()) != null) {
            // loops until end of file

            if(line.isEmpty() == false) {
                // System.out.println(line);
                if(line.trim().equals("<desc> Description:")) {
                    collect = true;
                    queryString = "";
                }
                else if(collect == true) {
                    queryString += " " + line;
                }
                else {
                    queryString = "";
                }
            }
            else if (collect == true) {
                //add query string to ArrayList and then turn off collection
                queries.add(queryString);
                queryid.add(queryID);
                collect = false;
                queryString = "";
                queryID++;
            }
            else {
                queryString = "";
            }

        }

        return queries;
    }

    public static ArrayList<Integer> CreateIds(String queriesPath) throws IOException{

        BufferedReader buffer = Files.newBufferedReader(Paths.get(queriesPath), StandardCharsets.UTF_8);

        ArrayList<Integer> queryid = new ArrayList<Integer>();

        String line = buffer.readLine();

        System.out.println("Reading in queries...");

        while ((line = buffer.readLine()) != null) {
            // loops until end of file

            if(line.isEmpty() == false) {
                if(line.substring(0,5).equals("<num>")) {
                    String s = line.substring(13,17).trim();
                    Integer i =Integer.parseInt(s);
                    queryid.add(i);
                }
            }

        }

        return queryid;
    }
}