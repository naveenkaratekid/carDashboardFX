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
 * auto is for the automatic climate control. this class represents the AUTO
 * button, which automatically adjusts fan speed
 */
public class auto extends ToggleButton
{
    // instance variables - replace the example below with your own
    private int x;
    private Slider s1;
    ToggleButton automatic;
    /**
     * Constructor for objects of class auto
     */
    public auto()
    {
        // initialise instance variables
        
    }

    public void create(Slider s1)
    {
        ToggleButton automatic = new ToggleButton("AUTO");
        automatic.setTranslateX(200);
        //automatic.setTranslateY(-2950);
        automatic.setTranslateY(-2790);
        automatic.getStylesheets().add(this.getClass().getResource("autoStyle.css").toExternalForm());

    }
}
