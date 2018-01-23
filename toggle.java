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
 * toggle is used for the lane departure warning system.
 * This class contains switches for lane depature warning
 * and active steering assist
 */
public class toggle extends HBox
    {
        private final Label l = new Label();
        private final Button button = new Button();
        private SimpleBooleanProperty on = new SimpleBooleanProperty(false);
        
        public toggle() 
        {
            init();
            on.addListener((a,b,c) -> 
            {
                if (c) 
                {
                    l.setText("ENABLED");
                    setStyle("-fx-background-color: green;");
                    l.toFront();
                }
                else
                {
                    l.setText("DISABLED");
                    setStyle("-fx-background-color: grey;");
                    button.toFront();
                }
            }
            );
        }
        
        public SimpleBooleanProperty switchOn() 
        {
            return on; 
        }
        
        private void init() 
        {
            
            l.setText("DISABLED");
            getChildren().addAll(l, button);    
            button.setOnAction((e) -> 
            {
                on.set(!on.get());
            }
            );
            l.setOnMouseClicked((e) -> 
            {
                on.set(!on.get());
            }
            );
            style();
            bindVals();
        }
        
        private void style() 
        {
            //Default Width
            setWidth(100);
            l.setAlignment(Pos.CENTER);
            setStyle("-fx-background-color: grey; -fx-text-fill:black; -fx-background-radius: 4;");
            setAlignment(Pos.CENTER_LEFT);
        }
        
        private void bindVals() 
        {
            l.prefWidthProperty().bind(widthProperty().divide(2));
            l.prefHeightProperty().bind(heightProperty());
            button.prefWidthProperty().bind(widthProperty().divide(2));
            button.prefHeightProperty().bind(heightProperty());
        }
    
    }