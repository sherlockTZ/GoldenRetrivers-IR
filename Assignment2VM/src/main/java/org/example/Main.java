package org.example;


import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
// import org.apache.lucene.store.RAMDirectory;


public class Main {

    private static String INDEX_DIRECTORY = "./index";

    public static void main (String[]args ) throws Exception {

        cleanExistingIndex(INDEX_DIRECTORY);

        Analyzer analyzer = new StandardAnalyzer();
        ClassicSimilarity clsimilarity = new ClassicSimilarity();
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY)); //directory where in index will be stored in memory
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setSimilarity(clsimilarity);
        IndexWriter iwriter = new IndexWriter(directory, config);

        String FT_DIRECTORY_PATH = "./datasources/ft";
        String FBIS_DIRECTORY_PATH = "./datasources/fbis";
        String LATIMES_DIRECTORY_PATH = "./datasources/latimes";
        String FEDREGISTER_DIRECTORY_PATH = "./datasources/fr94";

        ArrayList<Document> documentsFT = ParsingDocuments.parseFT(FT_DIRECTORY_PATH);
        ArrayList<Document> documentsFBIS = ParsingDocuments.parseFBIS(FBIS_DIRECTORY_PATH);
        ArrayList<Document> documentsLATIMES = ParsingDocuments.parseLATimes(LATIMES_DIRECTORY_PATH);
        ArrayList<Document> documentsFEDREGISTER = ParsingDocuments. parseFedRegister(FEDREGISTER_DIRECTORY_PATH);


        Create_Index.Make_Index(documentsFT, iwriter);
        Create_Index.Make_Index(documentsFBIS, iwriter);
        Create_Index.Make_Index(documentsLATIMES, iwriter);
        Create_Index.Make_Index(documentsFEDREGISTER, iwriter);//

        iwriter.close();

        search_Index.searchText();
        directory.close();

        }



    public static void cleanExistingIndex(String INDEX_DIRECTORY) throws IOException {
        if (Files.exists(Paths.get(INDEX_DIRECTORY))){
            FileUtils.cleanDirectory(new File("./index"));
        }
    }
}