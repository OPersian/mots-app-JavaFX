package MotsApp.Contrôleurs;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import MotsApp.Modèles.Article;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import MotsApp.MainMotsApp;
import MotsApp.ModèlesGestion.FormatAdapteur;
import MotsApp.ModèlesGestion.ioXmlGestion;
import MotsApp.VueNavigateur;

/**
 * FXML Controller class
 *
 * @author Persianova, Golubnycha
 */
public class ArticleTableauContrôleur implements Initializable {
    
    @FXML private TableView<Article> matièreTableView;
    
    @FXML private TableColumn<Article, String> matièreTitre;
    @FXML private TableColumn<Article, String> matièreAuteur;
    @FXML private TableColumn<Article, String> matièreContenu;
    @FXML private TableColumn<Article, LocalDate> matièreDate;
    @FXML private TableColumn<Article, URL> matièreSource;    
    
    @FXML private TextField titreTextField;
    @FXML private TextField auteurTextField;
    @FXML private TextField contenuTextField;
    @FXML private TextField dateTextField;
    @FXML private TextField sourceTextField;  
       
    private ObservableList<Article> mabase;     
    
    @FXML
    private void ajouteArticleToTable(ActionEvent event) throws MalformedURLException {
        
        ObservableList<Article> mabase = matièreTableView.getItems();
        //private ObservableList<Article> mabase = FXCollections.observableArrayList();
        
        LocalDate date = FormatAdapteur.dateFormat(dateTextField.getText());        
        URL source = FormatAdapteur.urlFormat(sourceTextField.getText());

        mabase.add(new Article(titreTextField.getText(),
            auteurTextField.getText(),
            contenuTextField.getText(),
            date,
            source
            )
        );
        
        titreTextField.setText(null);
        auteurTextField.setText(null);
        contenuTextField.setText(null);
        dateTextField.setText(null);
        sourceTextField.setText(null);
  
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
        //populate from Articles collection only if any Article has been saved to
        // collection with ArticleAjoute
        
        if (MainMotsApp.mabaseArticle_stockage != null){
            matièreTitre.setCellValueFactory(
                cellData -> cellData.getValue().titreProperty());

            matièreAuteur.setCellValueFactory(
                cellData -> cellData.getValue().auteurProperty());

            matièreContenu.setCellValueFactory(
                cellData -> cellData.getValue().contenuProperty());

            matièreDate.setCellValueFactory(
                cellData -> cellData.getValue().dateProperty());

            matièreSource.setCellValueFactory(
                cellData -> cellData.getValue().sourceProperty());

            matièreTableView.getItems().setAll(MainMotsApp.mabaseArticle_stockage);
        }
              
    }    

    MainMotsApp mainMotsApp = new MainMotsApp();//replace showOpenDialog' arg with null and delete this field! (to check if possible)
    
    @FXML
    private void lireDuFichier(ActionEvent event) throws Exception {

        ioXmlGestion.fichierOuvrir(false); // save current article list
        
        VueNavigateur.loadVue(VueNavigateur.ARTICLE_TABLEAU);//reload view 
        //to see changes instantly after adding data from the file to the current "observable list" of articles
    }
    
}
