package org.example;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParsingDocuments {

    public static ArrayList<Document> parseFBIS(String FBIS_DIRECTORY_PATH) throws IOException {

        File directory = new File(FBIS_DIRECTORY_PATH);
        File[] directoryListing = directory.listFiles();
        //safe all documents in an arraylist
        ArrayList<Document> documents = new ArrayList<>();

        for (File edition : directoryListing) {

            FileInputStream inputstream = new FileInputStream(edition);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));

            Document thisdoc = null;
            String docID = "";
            String thisLine = null;

            outer:
            while ((thisLine = br.readLine()) != null) {
                while (!(thisLine.startsWith("<DOCNO>"))) {
                    thisLine = br.readLine();
                    if (thisLine == null) { //this part is only used when a journal edition contain no articles at all
                        break outer;
                    }
                }

                thisdoc = new Document();
                //docID = thisLine.substring(8,15);
                thisLine = thisLine.replace("<DOCNO>", "");
                thisLine = thisLine.replace("</DOCNO>", "");
                docID = thisLine.trim();

                thisLine = br.readLine();

                while (!(thisLine.startsWith("<DOCNO>"))) {
                    if (thisLine.startsWith("<DATE1>")) {
                        thisLine = thisLine.replace("<DATE1>", "");
                        thisLine = thisLine.replace("</DATE1>", "");
                        thisLine = thisLine.trim();
                        thisdoc.add(new TextField("date", thisLine, Field.Store.YES));
                    }
                    if (thisLine.startsWith("<H3> <TI>")) {
                        //remove <H3> <TI> and <TI></H3>
                        thisLine = thisLine.replace("<H3> <TI>", "");
                        thisLine = thisLine.replace("</TI></H3>", "");
                        thisLine = thisLine.trim();
                        thisdoc.add(new TextField("title", thisLine, Field.Store.YES));


                    }
                    if (thisLine.startsWith("<TEXT>")) {
                        thisLine = br.readLine();
                        while (!(thisLine.startsWith("<TEXT>"))) {
                            if (thisLine.startsWith("</TEXT>")) {
                                break;
                            }
                            if (!(thisLine.equals(""))) { //only add field if the line contains information (is not empty)
                                thisLine = thisLine.trim();
                                thisdoc.add(new TextField("text", thisLine, Field.Store.YES));
                            }
                            thisLine = br.readLine();
                        }
                    }

                    thisLine = br.readLine();

                    if (thisLine == null) { //in case this is the last document
                        thisdoc.add(new TextField("docno", docID, Field.Store.YES));
                        documents.add(thisdoc);
                        System.out.println("Parsed document with ID " + docID);
                        break;
                    }

                    if (thisLine.startsWith("<DOCNO>")) {//saves document and creates next
                        thisdoc.add(new TextField("docno", docID, Field.Store.YES));
                        documents.add(thisdoc);
                        System.out.println("Parsed document with ID " + docID);
                        //start the creation of a new document
                        thisdoc = new Document();
                        thisLine = thisLine.replace("<DOCNO> ", "");
                        thisLine = thisLine.replace(" </DOCNO>", "");
                        docID = thisLine.trim();
                        thisLine = br.readLine();
                    }
                }
            }
        }
        System.out.println("Parsing Finished");
        return documents;
    }






    public static ArrayList<Document> parseFT(String FT_DIRECTORY_PATH) throws IOException {

        int count = 0;
        int i = 0;
        File[] files2;
        File[] files1 = new File(FT_DIRECTORY_PATH).listFiles();
        //File[] files1 = new File("/Users/seanhawk/Desktop/assignment2_IR/assignment2_IR/Assignment Two/ft").listFiles();

        ArrayList<File> filelist = new ArrayList<>();
        org.jsoup.nodes.Document docFT;

        StringBuilder docNO = new StringBuilder();
        StringBuilder Hl = new StringBuilder();
        StringBuilder txt = new StringBuilder();

        ArrayList<org.apache.lucene.document.Document> ParsedFT = new ArrayList<>();

        for (i = 0; i < files1.length; i++) {
            if (files1[i].isDirectory()) {
                files2 = files1[i].listFiles();
                for (File file : files2) {
                    Collections.addAll(filelist, file);
                }
            }
        }
        System.out.println(filelist.size());
        for (File file : filelist) {
            docFT = Jsoup.parse(file, "UTF-8");
            Elements docs = docFT.getElementsByTag("DOC");
            for (Element doc : docs) {
                String DocNos = doc.getElementsByTag("DOCNO").text();
                String linksdate = doc.getElementsByTag("DATE").text();
                String linksHL = doc.getElementsByTag("HEADLINE").text();
                String linkstext = doc.getElementsByTag("TEXT").text();
                org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
                count++;
                document.add(new TextField("docno", DocNos.toString(), Field.Store.YES));
                document.add(new TextField("title", linksHL.toString(), Field.Store.YES));
                document.add(new TextField("text", linkstext.toString(), Field.Store.YES));
                ParsedFT.add(document);
            }
            System.out.println("Parsed" + " " + file.getName());
        }
        System.out.println("Parsing of each File in FT Done");
        return ParsedFT;
    }






    public static ArrayList<Document> parseFedRegister(String FEDREGISTER_DIRECTORY_PATH) throws IOException {
        ArrayList<Document> fedRegisterDocList = new ArrayList<>();
        File[] directories = new File(FEDREGISTER_DIRECTORY_PATH).listFiles(File::isDirectory);
        String docno, text, title;
        for (File directory : directories) {
            File[] files = directory.listFiles();
            for (File file : files) {
                org.jsoup.nodes.Document d = Jsoup.parse(file, "UTF-8");
                Elements documents = d.select("DOC");
                for (Element document : documents) {

                    title = document.select("DOCTITLE").text();
                    document.select("DOCTITLE").remove();
                    document.select("ADDRESS").remove();
                    document.select("SIGNER").remove();
                    document.select("SIGNJOB").remove();
                    document.select("BILLING").remove();
                    document.select("FRFILING").remove();
                    document.select("DATE").remove();
                    document.select("CRFNO").remove();
                    document.select("RINDOCK").remove();

                    docno = document.select("DOCNO").text();
                    text = document.select("TEXT").text();

                    Document doc = new Document();
                    doc.add(new TextField("docno", docno, Field.Store.YES));
                    doc.add(new TextField("text", text, Field.Store.YES));
                    doc.add(new TextField("title", title, Field.Store.YES));
                    fedRegisterDocList.add(doc);
                }
            }
        }
        return fedRegisterDocList;
    }







    public static ArrayList<Document> parseLATimes(String LATIMES_DIRECTORY_PATH) throws IOException {
        File myPath = new File(LATIMES_DIRECTORY_PATH); //change the corpus location
        File[] myFiles = myPath.listFiles();
        ArrayList<Document> doclist = new ArrayList<>();
        String myDOCNO, myHEADLINE, myTEXT;
        for (File myFile : myFiles) {
            org.jsoup.nodes.Document doc = Jsoup.parse(myFile, null, "");
            Elements myElement = doc.select("DOC");
            for(Element myData: myElement) {
                Document document = new Document();
                myDOCNO = myData.select("DOCNO").text();
                myHEADLINE = myData.select("HEADLINE").text();
                myTEXT = myData.select("TEXT").text();

                document.add(new TextField("docno", myDOCNO, Field.Store.YES));
                document.add(new TextField("title", myHEADLINE, Field.Store.YES));
                document.add(new TextField("text", myTEXT, Field.Store.YES));
                doclist.add(document);
            }
        }
        return doclist;
    }













}




