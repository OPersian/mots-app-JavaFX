package trial.contrôleurs;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import trial.classesGestionContenu.Article;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import trial.MainMotsApp;

/**
 * FXML Controller class
 *
 * @author Persianova, Golubnycha
 */
public class MatièreTableauContrôleur implements Initializable {
    
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
    // Reference to the main application.
    
    @FXML
    private void ajouteArticleToTable(ActionEvent event) throws MalformedURLException {
        
        ObservableList<Article> mabase = matièreTableView.getItems();
        //private ObservableList<Article> mabase = FXCollections.observableArrayList();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-"
                + "MM-yyyy");    
        LocalDate date = LocalDate.now();
        String date_format = dateTextField.getText();
        try {
            date = LocalDate.parse(date_format, formatter);
        } 
        catch (DateTimeParseException e) {
            System.out.println("Date format est incorrect!");
        }  
        
        String monurl = sourceTextField.getText();   
        //URL source = new URL(monurl); 
        URL source = new URL("http://sample.com.ua");                
        try {
            source = new URL(monurl);
        }
        catch (MalformedURLException e) {
            System.out.println("URL format incorrect!");
        }     
        
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
        if (MainMotsApp.mabase_stockage != null){
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

            matièreTableView.getItems().setAll(MainMotsApp.mabase_stockage);
        }
              
    }    
    
}
