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
 * Clock is the class thta contains the current time. It does update per minute as it is
 * a live clock
 */
public class Clock extends Label
{
    DateTimeFormatter time = DateTimeFormatter.ofPattern("h:mm a");
    public Clock()
    {
        bindTime();
    }
    
    public void bindTime() // bindTime() will combine the time with the label
    {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(0),event -> setText(LocalTime.now().format(time))),new KeyFrame(Duration.seconds(1))); // lambda expressions used in Java 8
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }
}