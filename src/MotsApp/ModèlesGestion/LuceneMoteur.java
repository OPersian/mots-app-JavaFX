package MotsApp.ModèlesGestion;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import MotsApp.MainMotsApp;
import MotsApp.Modèles.Article;
import MotsApp.Modèles.Photo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Persianova, Golubnycha
 */
public class LuceneMoteur {

    //to add search results to a list
    public static ObservableList<Article> baseArticleResultats = FXCollections.observableArrayList();
    public static ObservableList<Photo> basePhotoResultats = FXCollections.observableArrayList();
    
    public static void créerIndex() throws IOException{

        StopAnalyzer analyzer = new StopAnalyzer();
        //StopAnalyzer removes common English words that are not usually useful for indexing.
            
        Directory dir = FSDirectory.open(Paths.get("."));
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer); // java.lang.IllegalStateException: do not share IndexWriterConfig instances across IndexWriters
        iwc.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc); // Lucene only allows one writer on an index at a time
        
        Document document;
           
        try {            
            for (Article i : MainMotsApp.mabaseArticle_stockage) {
                document = new Document(); // need to create new document at each iteration not to loose data
                FieldType myFieldType = new FieldType();             
                myFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
                myFieldType.setStored(true); // on stocke le texte
                myFieldType.setTokenized(true);
                myFieldType.freeze();
                Field myFieldTitre = new Field("titre",i.getTitre(),myFieldType); 
                document.add(myFieldTitre);
                Field myFieldAuteur = new Field("auteur",i.getAuteur(),myFieldType); 
                document.add(myFieldAuteur);         
                Field myFieldContenu = new Field("contenu",i.getContenu(),myFieldType); 
                document.add(myFieldContenu);                         
                Field myFieldDate = new Field("date",i.getDate().toString(),myFieldType); 
                document.add(myFieldDate); 
                Field myFieldSource = new Field("source",i.getSource().toString(),myFieldType); 
                document.add(myFieldSource);                         
                writer.addDocument(document);
                            
                System.out.println(document + "\n");//to debug in console
                            
                writer.commit(); //on finalize
                //writer.close(); //avoided: org.apache.lucene.store.AlreadyClosedException: this IndexWriter is closed
            }             
            System.out.println("Indexing of articles has been successfully produced.");
        }
        catch (Exception e) {
            System.out.println("Can not produce indexing of articles with lucene \n" + e.toString());
        }
        
        try {     
            for (Photo i : MainMotsApp.mabasePhoto_stockage) {                
                document = new Document();
                FieldType myFieldType = new FieldType();             
                myFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
                myFieldType.setStored(true); // on stocke le texte
                myFieldType.setTokenized(true);
                myFieldType.freeze();
                try {
                    Field myFieldTitre = new Field("titre",i.getTitre(),myFieldType); 
                    document.add(myFieldTitre);
                    Field myFieldAuteur = new Field("auteur",i.getAuteur(),myFieldType); 
                    document.add(myFieldAuteur);   
                    Field myFieldContenu = new Field("contenu",i.getContenu(),myFieldType); 
                    document.add(myFieldContenu);                         
                    Field myFieldDate = new Field("date",i.getDate().toString(),myFieldType); 
                    document.add(myFieldDate); 
                    Field myFieldSource = new Field("source",i.getSource().toString(),myFieldType); 
                    document.add(myFieldSource);     
                }
                catch (Exception e){
                    System.out.println("can't add photo's fields to document\n" + e.toString());
                }
                try {
                    writer.addDocument(document);
                }
                catch (Exception e){
                    System.out.println("can't add document with photo to writer\n" + e.toString());
                }               
                            
                System.out.println(document + "\n");//to debug in console
                
                try {            
                    writer.commit(); //on finalize
                    //writer.close(); //avoided: org.apache.lucene.store.AlreadyClosedException: this IndexWriter is closed
                }
                catch (Exception e){
                    System.out.println("can't commit the writer with photos\n" + e.toString());
                }                     
            }
            System.out.println("Indexing of photos has been successfully produced.");
        }
        catch (Exception e) {
            System.out.println("Can not produce indexing of photos with lucene \n" + e.toString());
        }
        try {
            writer.close(); //on ferme
            System.out.println("Writer has been successfully closed.");
        }
        catch (Exception e) {
            System.out.println("Can not close the writer.\n" + e.toString());
        }
}

    public static void chercherDansIndex(String requête) 
                                        throws ParseException, IOException{
        try {
            // ouvrir l’index
            Directory fsDir = FSDirectory.open(Paths.get("."));

            DirectoryReader reader = DirectoryReader.open(fsDir);
            IndexSearcher searcher = new IndexSearcher(reader);
            
            // exécuter une requête
            Analyzer analyzer = new StopAnalyzer();
            
            // construire l'objet Query
            QueryParser parser = new QueryParser("titre", analyzer);
            Query q = parser.parse(requête);
            
            // rechercher pour query
            int maxHits = 10; //number of results to show (Lucene documentation)
            TopDocs docs = searcher.search(q, maxHits); // changed from 1 to 10
            
            //parcourir les résultats
            ScoreDoc[] hits = docs.scoreDocs;
            
            try {
                for (ScoreDoc i : hits) {
                // retrieve the document from the 'ScoreDoc' object
                Document doc = searcher.doc(i.doc);
                String titre = doc.get("titre");
                String auteur = doc.get("auteur");
                String contenu = doc.get("contenu"); // for article
                String description = doc.get("contenu"); // for photo
                String date = doc.get("date");              
                URL source = new URL(doc.get("source"));
                Article article = new Article();
                Photo photo = new Photo();
                try {
                    article.setTitre(titre);
                    article.setAuteur(auteur);
                    article.setContenu(contenu);
                    article.setDate(LocalDate.parse(date));
                    article.setSource(source);
                    baseArticleResultats.add(article); //to perform in TableView
                }
                catch (Exception e) {
                    System.out.println("Lucene can not get search reasults (if any) in articles \n" + e.toString());
                    continue; // if can't set hit's attributes, then it's a Photo type, not Article
                }
                // if item is not of class type Article, it is of Photo
                if (photo.getTitre() == null) {
                    try {
                        photo.setTitre(titre);
                        photo.setAuteur(auteur);
                        photo.setContenu(description);
                        photo.setDate(LocalDate.parse(date));
                        photo.setSource(source);
                        basePhotoResultats.add(photo); //to perform in TableView 
                    }
                    catch (Exception e) {
                        System.out.println("Lucene can not get search reasults (if any) in photos \n" + e.toString());
                        continue;
                    }
                    }
                }
            } //try ended
            catch(Exception e) {
                System.out.println("Error fetching search results \n" + e.getMessage());
            }
        }
        catch (IOException | ParseException e) {
            System.out.println("Error searching " + requête + ":\n " + e.getMessage());
        }
    }
}
