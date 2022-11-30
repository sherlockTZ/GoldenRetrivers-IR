package org.example;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;

public class search_Index {

    private static String INDEX_DIRECTORY = "./index";

    
    public static void searchText() throws Exception {

        BM25Similarity BM25similarity = new BM25Similarity();
        ClassicSimilarity clsimilarity = new ClassicSimilarity();
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY)); //directory where in index will be stored in memory
        IndexReader reader = DirectoryReader.open(directory); //initialising the index reader
        IndexSearcher searcher = new IndexSearcher(reader); //initialising the index searcher
        //searcher.setSimilarity(BM25similarity); //setting similarity of searcher
        searcher.setSimilarity(clsimilarity);

        File file2 = new File("./output.txt"); //new path to store the scores of the docs and qrs
        FileOutputStream fout = new FileOutputStream(file2);

        ArrayList<String> queries = SearchFiles.CreateQueries("./queries");
        ArrayList<Integer> queryID = SearchFiles.CreateIds("./queries");
        QueryParser qp = new QueryParser("text", new StandardAnalyzer() ); //initialising a query parser and giving it an analyser and the fiels we wish to search



        Query query = null;
        int indx = 0;
        ArrayList<Query> pqueries = new ArrayList<>(); //query list containing the parsed queries using query parser
        TopDocs hits = null;
        String s = "";
        int x = 0;

        for (int i = 0; i < queries.size(); i++) {
            query = qp.parse(queries.get(i)); //parsing queries from queries array
            pqueries.add(query); //adding them to querys list
        }

        for (indx = 1; indx < queries.size(); indx++) {
            hits = searcher.search(pqueries.get(indx), 1000);//searching the info field using the parsed queries and storing the top 50 results
            for (ScoreDoc sd : hits.scoreDocs) { //accessing the scoreDocs field using the ScoreDoc object
                x ++;
                Document d = searcher.doc(sd.doc); //storing the topdoc fields in a document
                s = s + queryID.get(indx) + " " + "0" + " " + d.get("docno") + " " + x +  " " + sd.score + " " + "STANDARD" + "\n"; //pint results that are needed for trec_eval

            }

        }

        PrintWriter pw = new PrintWriter(fout);
        pw.write(s);
        pw.flush();
        fout.close();
        pw.close();

//        reader.close();
        //directory.close();


    }

}
