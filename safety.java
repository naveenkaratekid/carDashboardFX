import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.beans.value.*;
import javafx.util.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.animation.*;
import javafx.scene.media.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.beans.property.*;
import javafx.scene.media.*;
import javafx.scene.control.cell.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import javafx.scene.control.TabPane.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.lang.*;
import javafx.beans.property.*;
/**
 * radarCruise
 * 
 * contains the active safety systems settings:
 * Radar Adaptive Cruise Control, Lane Departure Warning, Pre-Collision System
 * 
 */
public class safety extends Menu
{
    // instance variables - replace the example below with your own
    /**
     * Constructor for objects of class bv
     */
    public safety()
    {
        // initialise instance variables
        radar();
    }

    public void radar()
    {
        Stage s = new Stage();
        s.setTitle("Active Safety Systems");
        
        
        
        TabPane tp = new TabPane();
        tp.setStyle("-fx-background-color: black;");
        tp.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        BorderPane borderPane = new BorderPane();
        
        // Radar Cruise Control
        String[] array = {"radarfar.png", "radarmid.png", "radarmid2.png", "radarmid3.png", "radarnear.png"};
        ImageView radarDistance = new ImageView();
        Image distance = new Image(array[0]);
        radarDistance.setImage(distance);
        
        CustomMenuItem cmi = new CustomMenuItem(tp);

        radarDistance.setTranslateX(65);
        radarDistance.setTranslateY(100);

        
        // Pre Collision System
        String[] array1 = {"PCSfar.png", "PCSmid.png", "PCSmid2.png", "PCSmid3.png", "PCSnear.png"};
        ImageView preCollisionDistance = new ImageView();
        Image PCS = new Image(array1[0]);
        preCollisionDistance.setImage(PCS);
        
        Button upPCS = new Button("Close");
        upPCS.setMinWidth(45);
        upPCS.setMaxWidth(45);
        
        Button downPCS = new Button("Far");
        downPCS.setMinWidth(45);
        downPCS.setMaxWidth(45);
        upPCS.setOnAction(new EventHandler<ActionEvent>()
        {
            
            public void handle(ActionEvent e)
            {
                settings s = settings.theInstance();
                preCollisionDistance.setImage(new Image(array1[s.getI()]));
                if(s.getI() >= 4)
                {
                    s.setI(4);
                    preCollisionDistance.setImage(new Image(array1[s.getI()]));
                    //System.out.println("Index " + i);
                }
                else
                {
                    int x = (int)s.getI();
                    x++;
                    s.setI(x);
                    //System.out.println("Index " + i);
                    preCollisionDistance.setImage(new Image(array1[s.getI()]));
                }
            }
        }
        );
        upPCS.setTranslateX(110);
        upPCS.setTranslateY(70);
            
        downPCS.setOnAction(new EventHandler<ActionEvent>()
        {
            
            public void handle(ActionEvent e)
            {
                settings s = settings.theInstance();
                preCollisionDistance.setImage(new Image(array1[s.getI()]));
                if(s.getI() <= 0)
                {
                    s.setI(0);
                    preCollisionDistance.setImage(new Image(array1[s.getI()]));
                }
                else
                {
                    int x = s.getI();
                    x--;
                    s.setI(x);
                    preCollisionDistance.setImage(new Image(array1[s.getI()]));
                }
                
                if(downPCS.isPressed() && (s.getI() == 4))
                {
                    int x = s.getI();
                    x--;
                    s.setI(x);
                    preCollisionDistance.setImage(new Image(array1[s.getI()]));
                }
            }
        }
        );
        downPCS.setTranslateX(110);
        downPCS.setTranslateY(195);
        
        // Buttons for Radar Cruise Control Following Distance
        Button upRadar = new Button("Close");
        upRadar.setMinWidth(45);
        upRadar.setMaxWidth(45);
        Button downRadar = new Button("Far");
        downRadar.setMinWidth(45);
        downRadar.setMaxWidth(45);
        upRadar.setOnAction(new EventHandler<ActionEvent>()
        {

            public void handle(ActionEvent e)
            {
                settings s = settings.theInstance();
                radarDistance.setImage(new Image(array[s.getI()]));
                if(s.getI() >= 4)
                {
                    s.setI(4);
                    radarDistance.setImage(new Image(array[s.getI()]));
                }
                else
                {
                    int x = (int)s.getI();
                    x++;
                    s.setI(x);
                    radarDistance.setImage(new Image(array[s.getI()]));
                }
            }
        }
        );
        upRadar.setTranslateX(110);
        upRadar.setTranslateY(70);
     
        downRadar.setOnAction(new EventHandler<ActionEvent>()
        {
            
            //int i = 4;
            public void handle(ActionEvent e)
            {
                settings s = settings.theInstance();

                if(s.getI() <= 0)
                {
                    s.setI(0);
                    radarDistance.setImage(new Image(array[s.getI()]));
                    
                }
                else
                {
                    int x = s.getI();
                    x--;
                    s.setI(x);
                    //System.out.println("Index " + s.getI());
                    radarDistance.setImage(new Image(array[s.getI()]));
                }
                
                if(downRadar.isPressed() && (s.getI() == 4))
                {
                    int x = s.getI();
                    x--;
                    s.setI(x);
                    //System.out.println("Index " + i);
                    radarDistance.setImage(new Image(array[s.getI()]));
                }
                
                //System.out.println("index is " + s.getI());
            }
        }
        );
        downRadar.setTranslateX(110);
        downRadar.setTranslateY(195);
        
        
        Label radar1 = new Label();
        radar1.setText("Radar Adaptive Cruise Control");
        radar1.setTextFill(Color.WHITE);
        radar1.setFont(new Font("Arial", 15));
        radar1.setTranslateX(25);
        radar1.setTranslateY(230);
        
        
        Label pcs = new Label();
        pcs.setText("Pre-Collision System");
        pcs.setTextFill(Color.WHITE);
        pcs.setFont(new Font("Arial", 15));
        pcs.setTranslateX(65);
        pcs.setTranslateY(230);
        
        
        Label st = new Label();
        st.setText("Active Steering Assist");
        st.setFont(new Font("Arial", 15));
        st.setTextFill(Color.WHITE);
        st.setTranslateX(-150);
        st.setTranslateY(125);

        Group root = new Group();
        Scene scene = new Scene(root, 250, 250, Color.WHITE);
        //(t, l1, l2, st, t1, ld)
        Tab tab1 = new Tab();
        HBox group = new HBox();
        tab1.setGraphic(new ImageView(new Image("radar.png")));
        tab1.setOnSelectionChanged(new EventHandler<Event>() 
        {
    
            public void handle(Event t) 
            {
                if (tab1.isSelected()) 
                {
                    radarDistance.setVisible(true);
                    preCollisionDistance.setVisible(false);
                    upPCS.setVisible(false);
                    downPCS.setVisible(false);
                    upRadar.setVisible(true);
                    downRadar.setVisible(true);
                    group.setVisible(false);
                    pcs.setVisible(false);
                    radar1.setVisible(true);
                }
                
            }
        }
        );
        Group root1 = new Group(group);
        
        // Lane Departure Warning
        Tab tab2 = new Tab();
        tab2.setGraphic(new ImageView(new Image("lanedeparture.png")));
        toggle t = new toggle();
        t.setTranslateX(0);
        t.setTranslateY(150);
        t.setMinWidth(150);
        t.setMaxWidth(150);
        
        Label ld = new Label();
        ld.setText("Lane Departure Warning");
        ld.setTextFill(Color.WHITE);
        ld.setFont(new Font("Arial", 15));
        ld.setTranslateX(-450);
        ld.setTranslateY(50);
        
        

        
        toggle t1 = new toggle();
        t1.setMinWidth(150);
        t1.setMaxWidth(150);
        t1.setTranslateX(-296);
        t1.setTranslateY(75);
        
        root1.getChildren().addAll(radar1, pcs);
        
        group.setTranslateX(50);
        group.setTranslateY(50);
        
        group.getChildren().addAll(t, st, t1, ld);

        tab2.setOnSelectionChanged(new EventHandler<Event>() 
        {
    
            public void handle(Event t) 
            {
                if (tab2.isSelected()) 
                {
                    radarDistance.setVisible(false);
                    upRadar.setVisible(false);
                    downRadar.setVisible(false);
                    preCollisionDistance.setVisible(false);
                    upPCS.setVisible(false);
                    downPCS.setVisible(false);
                    group.setVisible(true);
                    radar1.setVisible(false);
                    st.setVisible(true);
                    ld.setVisible(true);
                    pcs.setVisible(false);
                }
                
                
            }
        }
        );
        
        
        Tab tab3 = new Tab();
        tab3.setGraphic(new ImageView(new Image("collision.png")));
      
        
        tab3.setOnSelectionChanged(new EventHandler<Event>() 
        {
    
            public void handle(Event t) 
            {
                if (tab3.isSelected()) 
                {
                    preCollisionDistance.setVisible(true);
                    upPCS.setVisible(true);
                    downPCS.setVisible(true);
                    radarDistance.setVisible(false);
                    group.setVisible(false);
                    radar1.setVisible(false);
                    pcs.setVisible(true);
                }
                
            }
        }
        );
        preCollisionDistance.setTranslateX(65);
        preCollisionDistance.setTranslateY(100);
        
        tp.getStylesheets().add(this.getClass().getResource("radarcruise.css").toExternalForm());
        tp.applyCss();
        tp.getTabs().addAll(tab1, tab2, tab3);
        
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        
        borderPane.setCenter(tp);
        root.getChildren().addAll(borderPane, radarDistance, upRadar, downRadar, preCollisionDistance, upPCS, downPCS, group, radar1, pcs);
        s.setScene(scene);
        s.show();
    }
}
