package org.example;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.ArrayList;

public class Create_Index {

    static void Make_Index(ArrayList <Document> Docs, IndexWriter indexWriter) throws IOException {

        for(Document Doc : Docs ) {

            indexWriter.addDocument(Doc);
            System.out.println("Adding Doc " + Doc.getField("docno") + " to the Index" );

        }

        System.out.println("All Files Indexed");

    }

}
