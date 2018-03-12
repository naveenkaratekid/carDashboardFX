import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.input.*;
import javafx.beans.value.*;
import javafx.util.*;
import javafx.beans.*;
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
import javafx.scene.media.MediaPlayer.*;
/**
 * Screen2 is the main screen containing all UI elements
 * 
 * 
 */
@SuppressWarnings("unchecked") 
public class Main extends Application
{
    // instance variables - replace the example below with your own
    public static List<String> fileExtensions = Arrays.asList(".mp3");
    public static int extensionLength = 3;
    private Duration totalTime;
    private MediaPlayer player;
    public Label currentlyPlaying = new Label();
    ProgressBar progress = new ProgressBar();
    final TableView<Map> metadataTable = new TableView<>();
    private ChangeListener<Duration> progressListener;
    private MapChangeListener<String, Object> metaChangeListener;
   
    public static String tagName = "Tag", valueName = "Value";
    public Label artist;
    private Button equalizer;
       
    private void setMetaDataDisplay(ObservableMap<String, Object> metadata)
    {
        metadataTable.getItems().setAll(convertMetaToTable(metadata));
        metaChangeListener = new MapChangeListener<String, Object>()
        {
          public void onChanged(Change<? extends String, ?> change) 
          {
            metadataTable.getItems().setAll(convertMetaToTable(metadata));
          }
        };
        metadata.addListener(metaChangeListener);
    }

    private ObservableList<Map> convertMetaToTable(ObservableMap<String, Object> metadata) 
    {
        ObservableList<Map> all = FXCollections.observableArrayList();
        for (String key: metadata.keySet()) 
        {
          Map<String, Object> dataRow = new HashMap<>();
          dataRow.put(tagName, key);
          dataRow.put(valueName, metadata.get(key));
          all.add(dataRow);
        }
        return all;
    }
    
    private void setCurrentlyPlaying(MediaPlayer newPlayer)
    {
        newPlayer.seek(Duration.ZERO);
        progress.setProgress(0);
        progressListener = new ChangeListener<Duration>() 
        {
          public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) 
          {
            progress.setProgress(1.0 * newPlayer.getCurrentTime().toMillis() / newPlayer.getTotalDuration().toMillis());
          }
        };
        newPlayer.currentTimeProperty().addListener(progressListener);
        String artist = (String) newPlayer.getMedia().getMetadata().get("artist");
        String source = newPlayer.getMedia().getSource();
        source = source.substring(0, source.length() - extensionLength);
        source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
        String songInfo = "Now Playing: " + source + "\nArtist: " + artist;
        currentlyPlaying.setText(songInfo);
        //currentlyPlaying.setText(String.format("Now Playing: %s \nArtist: %s", source, artist));
    } 
  
    private void createControls()
    {
          artist = new Label();
          artist.setWrapText(true);
          artist.setId("artist");
    }
    
    private void handleMetadata(String key, Object val) 
    {
        if (key.equals("artist")) 
        {
             artist.setText(val.toString());
        } 
    }
    
    public String getElement(String[] array1, int idx) 
    {
        return array1[idx];
    }   
    
    private MediaPlayer createPlayer(String mediaSource) 
    {
        Media media = new Media(mediaSource);
        player = new MediaPlayer(media);
        media.getMetadata().addListener(new MapChangeListener<String, Object>() 
        {
            
            public void onChanged(Change<? extends String, ? extends Object> c) 
            {
              if (c.wasAdded()) 
              {
                  handleMetadata(c.getKey(), c.getValueAdded());
              }
            }
        }
        );
        player.setOnError(new Runnable() 
        {
          public void run() 
          {
            System.out.println("Error " + player.getError());
          }
        }
        );
        return player;
    }
    
    public void start(Stage stage)
    {
        /*------------------------------------------------Audio Player------------------------------------------------*/
        List<String> pars = getParameters().getRaw();
        // NOTE: Line below: You will have to modify the directory path in order for it to work. Or it will throw exception
        File dir = (pars.size() > 0)? new File(pars.get(0)): new File("C:\\Users\\Naveen K\\Desktop\\screen\\music"); // NOTE: You will have to modify the directory on your computer so that the music can play.
        if (!dir.exists() || !dir.isDirectory()) 
        {
          System.out.println("Source can't be found");
          Platform.exit();
          return;
        }
        List<MediaPlayer> players = new ArrayList<>(); // create the players list
        for (String f : dir.list(new FilenameFilter() 
        {
          public boolean accept(File dir, String name) 
          {
            for (String ext: fileExtensions) 
            {
              if (name.endsWith(".mp3")) 
              {
                return true;
              }
            }
            return false;
          }
        }
        )
        ) 
        players.add(createPlayer("file:///" + (dir + "\\" + f).replace("\\", "/").replaceAll(" ", "%20")));
        if (players.isEmpty()) 
        {
          System.out.println("No audio found");
          Platform.exit();
          return;
        }    
        
        MediaView mediaView = new MediaView(players.get(0));
        Image nextImage = new Image(getClass().getResourceAsStream("next.png"));
        Button skip = new Button("Next", new ImageView(nextImage));
        Image playImage = new Image(getClass().getResourceAsStream("play.png"));
        Button play = new Button("Play", new ImageView(playImage));
        play.getStylesheets().add(this.getClass().getResource("buttonstyle.css").toExternalForm());
        play.setMinWidth(110);
        play.setMaxWidth(110);
        
        // play pause rewind and next track buttons
        Image previousImage = new Image(getClass().getResourceAsStream("previous.png"));
        Button previous = new Button("Previous", new ImageView(previousImage));
        
        Image pauseImage = new Image(getClass().getResourceAsStream("pause.png"));
        Button pause = new Button("Pause", new ImageView(pauseImage));
    
        // play audio
        for (int i = 0; i < players.size(); i++) 
        {
          MediaPlayer player = players.get(i);
          MediaPlayer nextPlayer = players.get((i + 1) % players.size());
          //createControls(i);
          createControls();
          
          player.setOnEndOfMedia(new Runnable() 
          {
            public void run() 
            {
              player.currentTimeProperty().removeListener(progressListener);
              player.getMedia().getMetadata().removeListener(metaChangeListener);
              player.stop();
              //player.getMedia().getMetadata().get("artist");
              //mediaView.getMediaPlayer().getMedia().getMetadata().get("artist");
              mediaView.setMediaPlayer(nextPlayer);
              nextPlayer.play();
            }
          }
          );
        }
        
        /*
         code below is for the volume slider
        
         */

        double volNum = mediaView.getMediaPlayer().getVolume();
        Label vol = new Label();
        vol.setFont(new Font("Arial", 15));
        vol.setTextFill(Color.BLACK);
        vol.setText(Double.toString(volNum));
        vol.setTranslateX(460);
        vol.setTranslateY(-5050);
        Slider volume = new Slider(0,100,volNum);
        volume.getStylesheets().add(this.getClass().getResource("volumeStyle.css").toExternalForm());
        volume.setShowTickLabels(false);
        volume.setOrientation(Orientation.VERTICAL);
        volume.setSnapToTicks(false);
        volume.setMinWidth(250);
        volume.setMinHeight(400);
        volume.setMaxWidth(250);
        volume.setMaxHeight(400);
        //volume.setValue(mediaView.getMediaPlayer().getVolume() * 100); // 1.0 = max 0.0 = min
        double curVol = mediaView.getMediaPlayer().getVolume() * 100;
        volume.setValue(curVol);
        volume.valueProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(javafx.beans.Observable observable) 
            {
                mediaView.getMediaPlayer().setVolume(volume.getValue() / 100);
            }
        }
        );
        
        vol.textProperty().bind(volume.valueProperty().asString("Vol: %.0f"));
        volume.setTranslateX(365);
        volume.setTranslateY(-4630);

        //next track.
        skip.setOnAction(new EventHandler<ActionEvent>() 
        {
          public void handle(ActionEvent actionEvent) 
          {
            MediaPlayer curPlayer = mediaView.getMediaPlayer();
            curPlayer.currentTimeProperty().removeListener(progressListener);
            curPlayer.stop();
            int i = (players.indexOf(curPlayer) + 1) % players.size();
            MediaPlayer nextPlayer = players.get(i);
            mediaView.setMediaPlayer(nextPlayer);
            nextPlayer.setVolume(volume.getValue() / 100);
            boolean playing = curPlayer.getStatus().equals(Status.PLAYING);
            //System.out.println(playing);
            if(playing)
            {
                 curPlayer.stop();
                 nextPlayer.play();
                 
            }
            
            //nextPlayer.play();
          }
        }
        );
        skip.setTranslateX(810);
        skip.setTranslateY(-3800);
        
        // previous track.
        previous.setOnAction(new EventHandler<ActionEvent>() 
        {
          public void handle(ActionEvent actionEvent) 
          {
            MediaPlayer curPlayer = mediaView.getMediaPlayer();
            curPlayer.currentTimeProperty().removeListener(progressListener); 
            curPlayer.stop();
            int i = (players.indexOf(curPlayer) - 1) % players.size();
            if(i < 0)
            {
                i = players.size() - 1;
                createControls();
            }
            MediaPlayer prevPlayer = players.get(i);
            mediaView.setMediaPlayer(prevPlayer);
            
            boolean playing = curPlayer.getStatus().equals(Status.PLAYING);
            if(playing)
            {
                 curPlayer.stop();
                 prevPlayer.setVolume(volume.getValue() / 100);
                 prevPlayer.play();
                 
            }
            
            }
        }
        );
        previous.setTranslateX(575);
        previous.setTranslateY(-3860);
        
        // Play and pause
        play.setOnAction(new EventHandler<ActionEvent>() 
        {
          public void handle(ActionEvent actionEvent) 
          {
            if ("Pause".equals(play.getText())) 
            {
              mediaView.getMediaPlayer().pause();
              play.setText("Play");
              play.setGraphic(new ImageView(playImage));
            } 
            else 
            {
              mediaView.getMediaPlayer().play();
              play.setText("Pause");
              play.setGraphic(new ImageView(pauseImage));
            }
          }
        }
        );
        play.setTranslateX(696);
        play.setTranslateY(-3770);
        play.setTranslateY(-3740);
        
        // display track info
        mediaView.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() 
        {
          public void changed(ObservableValue<? extends MediaPlayer> observableValue, MediaPlayer oldPlayer, MediaPlayer newPlayer) 
          {
            setCurrentlyPlaying(newPlayer);
          }
        }
        );
        mediaView.setTranslateX(750);
        mediaView.setTranslateY(-4000);
        
        artist.setFont(new Font("Arial", 20));
        artist.setTranslateX(300);
        artist.setTranslateY(-5000);
        
        currentlyPlaying.setFont(new Font("Arial", 20));
        currentlyPlaying.setWrapText(true);
        currentlyPlaying.setTranslateX(600);
        currentlyPlaying.setTranslateY(-4000);
        
        mediaView.setMediaPlayer(players.get(0));
        setCurrentlyPlaying(mediaView.getMediaPlayer());
        /*-------------------------------------------------------------------------------------------------------------------------------------------------*/
        Image bar = new Image("blackbar.png");
        
        ImageView statusBar = new ImageView();
        statusBar.setImage(bar);
        /*
         code below is for the volume slider
        
         */

        /*double volNum = mediaView.getMediaPlayer().getVolume();
        Label vol = new Label();
        vol.setFont(new Font("Arial", 15));
        vol.setTextFill(Color.BLACK);
        vol.setText(Double.toString(volNum));
        vol.setTranslateX(460);
        vol.setTranslateY(-5050);
        Slider volume = new Slider(0,100,volNum);
        volume.getStylesheets().add(this.getClass().getResource("volumeStyle.css").toExternalForm());
        volume.setShowTickLabels(false);
        volume.setOrientation(Orientation.VERTICAL);
        volume.setSnapToTicks(false);
        volume.setMinWidth(250);
        volume.setMinHeight(400);
        volume.setMaxWidth(250);
        volume.setMaxHeight(400);
        
        volume.setValue(mediaView.getMediaPlayer().getVolume() * 100); // 1.0 = max 0.0 = min
        volume.valueProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(javafx.beans.Observable observable) 
            {
                mediaView.getMediaPlayer().setVolume(volume.getValue() / 100);
            }
        }
        );
        
        vol.textProperty().bind(volume.valueProperty().asString("Vol: %.0f"));
        volume.setTranslateX(365);
        volume.setTranslateY(-4630);*/
        
        
        
        
        // code below will enable the popup containing the settings for the radar cruise control, lane departure warning, and the pre-collision system
        
        /*-------------------------------------------------------------------------------------------------------------------------------------------------*/
        /*------------------------------------------------Equalizer button------------------------------------------------*/        
        equalizer = new Button("Sound\nSettings");
        equalizer.setFont(new Font("Arial", 17.5));
        equalizer.setMinWidth(110);
        equalizer.setMaxWidth(110);
        equalizer.setMinHeight(65);
        equalizer.setMaxHeight(65);
        equalizer.setTranslateX(750);
        equalizer.setTranslateY(-4000);
        equalizer.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                Stage stage1 = new Stage();
                stage1.setTitle("Audio & Equalizer Settings");
                /*------------------------------------------------Bass------------------------------------------------*/
                Slider s = new Slider(0, 6, 0);
                Label l = new Label("Bass");
                l.setFont(new Font("Arial", 20));
                l.setTextFill(Color.WHITE);
                l.setTranslateX(200);
                l.setTranslateY(-300);
                s.setMin(-6);
                s.setMax(6);
                s.setMinorTickCount(0);
                s.setMajorTickUnit(1);
                s.setSnapToTicks(true);
                s.setShowTickMarks(true);
                s.setShowTickLabels(true);
                
                s.setLabelFormatter(new StringConverter<Double>()
                {
                    public String toString(Double x)
                    {
                        if(x == 0)
                        {
                            return "/\\";
                        }
                        return " ";
                    }
        
                    public Double fromString(String s1)
                    {
                        switch(s1)
                        {
                            case "/\\":
                                return (double)0;
                            default:
                                return (double)0;
                        }
                    }
                }
                );
                s.setMaxWidth(250);
                s.setMaxHeight(300);
                s.setMinWidth(250);
                s.setMinHeight(300);
                s.setTranslateX(100); 
                s.setTranslateY(-90);
                
                /*------------------------------------------------Treble------------------------------------------------*/
                Slider s1 = new Slider(0, 6, 0);
                Label l1 = new Label("Treble");
                l1.setFont(new Font("Arial", 20));
                l1.setTextFill(Color.WHITE);
                l1.setTranslateX(197);
                l1.setTranslateY(-960);
                s1.setMin(-6);
                s1.setMax(6);
                s1.setMinorTickCount(0);
                s1.setMajorTickUnit(1);
                s1.setSnapToTicks(true);
                s1.setShowTickMarks(true);
                s1.setShowTickLabels(true);
                s1.setLabelFormatter(new StringConverter<Double>()
                {
                    public String toString(Double x)
                    {
                        if(x == 0)
                        {
                            return "/\\";
                        }
                        return " ";
                    }
        
                    public Double fromString(String s1)
                    {
                        switch(s1)
                        {
                            case "/\\":
                                return (double)0;
                            default:
                                return (double)0;
                        }
                    }
                }
                ); 
                s1.setMaxWidth(250);
                s1.setMaxHeight(300);
                s1.setMinWidth(250);
                s1.setMinHeight(300);
                s1.setTranslateX(100); 
                s1.setTranslateY(-750);
        
                Button b1 = new Button("+");
                b1.setMinWidth(25);
                b1.setMinHeight(25);
                b1.setMaxWidth(25);
                b1.setMaxHeight(25);
                Button b2 = new Button("-");
                b2.setMinWidth(25);
                b2.setMinHeight(25);
                b2.setMaxWidth(25);
                b2.setMaxHeight(25);
                b1.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double x = s.getValue();
                        x++;
                        s.setValue(x);
                    }
                }
                );
                
                b2.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double x = s.getValue();
                        x--;
                        s.setValue(x);
                    }
                }
                );
                b1.setTranslateX(355);
                b1.setTranslateY(-310);
                
                b2.setTranslateX(70);
                b2.setTranslateY(-345);
                
                /*------------------------------------------------Fade------------------------------------------------*/
                Slider s11 = new Slider(0, 6, 0);
                Label l11 = new Label("Fade");
                l11.setFont(new Font("Arial", 20));
                l11.setTextFill(Color.WHITE);
                l11.setTranslateX(205);
                l11.setTranslateY(-1325);
                s11.setMin(-6);
                s11.setMax(6);
               
                s11.setMinorTickCount(0);
                s11.setMajorTickUnit(1);
                s11.setSnapToTicks(true);
                s11.setShowTickMarks(true);
                s11.setShowTickLabels(true);
                
                s11.setLabelFormatter(new StringConverter<Double>()
                {
                    public String toString(Double x)
                    {
                        if(x == 0)
                        {
                            return "/\\";
                        }
                        return " ";
                    }
        
                    public Double fromString(String s1)
                    {
                        switch(s1)
                        {
                            case "/\\":
                                return (double)0;
                            default:
                                return (double)0;
                        }
                    }
                }
                );
                
                s11.setMaxWidth(250);
                s11.setMaxHeight(300);
                s11.setMinWidth(250);
                s11.setMinHeight(300);
                s11.setTranslateX(100); 
                s11.setTranslateY(-1115);
        
                Button b11 = new Button("+");
                b11.setMinWidth(25);
                b11.setMinHeight(25);
                b11.setMaxWidth(25);
                b11.setMaxHeight(25);
                Button b21 = new Button("-");
                b21.setMinWidth(25);
                b21.setMinHeight(25);
                b21.setMaxWidth(25);
                b21.setMaxHeight(25);
                b11.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double x = s11.getValue();
                        x++;
                        s11.setValue(x);
                    }
                }
                );
                
                b21.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double x = s11.getValue();
                        x--;
                        s11.setValue(x);
                    }
                }
                );
                b11.setTranslateX(355);
                b11.setTranslateY(-1335);
                
                b21.setTranslateX(70);
                b21.setTranslateY(-1370);
                
                Button forward = new Button("Front");
                Button rear = new Button("Rear");
                Button left = new Button("Left");
                Button right = new Button("Right");
                
                ImageView iv1 = new ImageView();
                Image line1 = new Image("redLine.png");
                iv1.setImage(line1);
                
                ImageView iv2 = new ImageView();
                iv2.setImage(line1);
                iv2.setRotate(iv2.getRotate() + 90);
                
                ImageView iv3 = new ImageView();
                iv3.setImage(new Image("surround.png"));
                
                Button def = new Button("Default");
                def.setMinWidth(75);
                def.setMinHeight(25);
                def.setMaxWidth(75);
                def.setMaxHeight(25);
                
                def.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        iv1.setTranslateX(500);
                        iv1.setTranslateY(-439);
                
                        iv2.setTranslateX(500);
                        iv2.setTranslateY(-451);
                    }
                }
                );
                
                def.setTranslateX(550);
                def.setTranslateY(-870);
                iv1.setTranslateX(500);
                iv1.setTranslateY(-439);
        
                iv2.setTranslateX(500);
                iv2.setTranslateY(-451); 
                
                iv3.setTranslateX(500);
                iv3.setTranslateY(-350);
                
                forward.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double y = iv1.getTranslateY();
                        //y--;
                        if(iv1.getTranslateY() <= -514)
                        {
                           iv1.setTranslateY(-514);
                           //System.out.println("Line reached max -514");
                           
                        }
                        else
                        {
                            y--;
                            iv1.setTranslateY(y);
                        }

                    }
                }
                );
                forward.setTranslateX(555);
                forward.setTranslateY(-575);
                
                rear.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double y = iv1.getTranslateY();
                        y++;
                        if(iv1.getTranslateY() >= -363)
                        {
                           iv1.setTranslateY(-363);
                           //System.out.println("Line reached min -363");
                        }
                        else
                        {
                            y++;
                            iv1.setTranslateY(y);
                        }
                        
                    }
                }
                );
                rear.setTranslateX(555);
                rear.setTranslateY(-410);
                
                left.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double x = iv2.getTranslateX();
                        if(iv2.getTranslateX() <= 425)
                        {
                            iv2.setTranslateX(425);
                            //System.out.println("Line reached min -425");
                        }
                        else
                        {
                            x--;
                            iv2.setTranslateX(x);
                        }
                    }
                }
                );
                left.setTranslateX(450);
                left.setTranslateY(-545);
                
                right.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double x = iv2.getTranslateX();
                        if(iv2.getTranslateX() >= 576)
                        {
                            iv2.setTranslateX(576);
                            //System.out.println("Line reached min -576");
                        }
                        else
                        {
                            x++;
                            iv2.setTranslateX(x);
                        }
                    }
                }
                );
                right.setTranslateX(665);
                right.setTranslateY(-580);

                /////////////////////////////////////
                Button b111 = new Button("+");
                b111.setMinWidth(25);
                b111.setMinHeight(25);
                b111.setMaxWidth(25);
                b111.setMaxHeight(25);
                Button b211 = new Button("-");
                b211.setMinWidth(25);
                b211.setMinHeight(25);
                b211.setMaxWidth(25);
                b211.setMaxHeight(25);
                b111.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double x = s1.getValue();
                        x++;
                        s1.setValue(x);
                    }
                }
                );
                b211.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        double x = s1.getValue();
                        x--;
                        s1.setValue(x);
                    }
                }
                );
                b111.setTranslateX(355);
                b111.setTranslateY(-970);
                b211.setTranslateX(70);
                b211.setTranslateY(-1005);
                
                VBox root = new VBox(10);
                root.setStyle("-fx-background-color: black;");
                root.setMaxWidth(750);
                root.setMinWidth(750);
                root.setMinHeight(450);
                root.setMaxHeight(450);
                root.getChildren().addAll(s, l, b1, b2, iv3, iv1, iv2, forward, rear, left, right, s1, l1, b111, b211, def, s11, l11, b11, b21);
                Scene scene = new Scene(root);
                stage1.setScene(scene);
                stage1.show();
            }
        }
        );
        /*------------------------------------------------Clock------------------------------------------------*/
        Clock c = new Clock();
        c.setFont(new Font("Arial", 30));
        c.setTextFill(Color.WHITE);
        c.setTranslateX(675);
        c.setTranslateY(-4570);
        
        /*|||||||||||||||||||||||||||||||||||||||||||||||||||||Driver Controls||||||||||||||||||||||||||||||||||||||||||||||*/
        
        /*------------------------------------------------Driver Heated cooled seats------------------------------------------------*/
        Slider s = new Slider(0, 6, 0);
        HBox root = new HBox(10);
        ImageView iv = new ImageView();
        Image temperature = new Image("temperature4.png");
        iv.setImage(temperature);
        iv.setTranslateX(37);
        iv.setTranslateY(-2340);
        s.setMin(0);
        s.setMax(6);
        s.setValue(3);
        s.setMinorTickCount(0);
        s.setMajorTickUnit(1);
        s.setSnapToTicks(true);
        s.setShowTickMarks(true);
        s.setShowTickLabels(true);
        s.setLabelFormatter(new StringConverter<Double>()
        {
            public String toString(Double x)
            {
                if(x == 0)
                {
                    return "Warmer";
                }
                if (x == 3)
                {
                    return "Off";
                }
                if (x == 6)
                {
                   return "Cooler";                  
                }
                return " ";
            }
            public Double fromString(String s1)
            {
                switch(s1)
                {
                    case "Warmer":
                        return (double)0;
                        
                    case "Off":
                        return (double)3;
                    
                    case "Cooler":
                        return (double)6;
                        
                    default:
                        return (double)6;
                }
            }
        }
        );
        s.setMinWidth(420);
        s.setMinHeight(500);
        s.setMaxWidth(420);
        s.setMaxHeight(500);    
        s.setTranslateX(1); 
        s.setTranslateY(-40);
        s.getStylesheets().add(this.getClass().getResource("styleSeat.css").toExternalForm());
        s.setStyle("-fx-font: 20px \"Arial\"; -fx-padding: 5;");
        s.applyCss();
       
        /*   ------------------------------------------------------------------------   */
        /*------------------------------------------------Passenger Heated cooled seats------------------------------------------------*/
        Slider sPass = new Slider(0, 6, 0);
        /*ImageView ivPass = new ImageView();
        Image temperaturePass = new Image("temperature4.png");
        ivPass.setImage(temperature);
        ivPass.setTranslateX(1097);
        ivPass.setTranslateY(-3053);*/
        sPass.setMin(0);
        sPass.setMax(6);
        sPass.setValue(3);
        sPass.setMinorTickCount(0);
        sPass.setMajorTickUnit(1);
        sPass.setSnapToTicks(true);
        sPass.setShowTickMarks(true);
        sPass.setShowTickLabels(true);
        sPass.setLabelFormatter(new StringConverter<Double>()
        {
            public String toString(Double x)
            {
                if(x == 0)
                {
                    return "Warmer";
                }
                if (x == 3)
                {
                    return "Off";
                }
                if (x == 6)
                {
                   return "Cooler";                  
                }
                return " ";
            }

            public Double fromString(String s1)
            {
                switch(s1)
                {
                    case "Warmer":
                        return (double)0;
                        
                    case "Off":
                        return (double)3;
                    
                    case "Cooler":
                        return (double)6;
                        
                    default:
                        return (double)6;
                }
            }
        }
        );
        sPass.setMinWidth(420);
        sPass.setMinHeight(500);
        sPass.setMaxWidth(420);
        sPass.setMaxHeight(500);
        sPass.setTranslateX(1060); 
        sPass.setTranslateY(460);
        sPass.getStylesheets().add(this.getClass().getResource("styleSeat.css").toExternalForm());
        sPass.setStyle("-fx-font: 20px \"Arial\"; -fx-padding: 5;");
        sPass.applyCss();
        /*   ------------------------------------------------------------------------   */
        
        /*------------------------------------------------Driver Fan Speed------------------------------------------------*/
        Slider s1 = new Slider();
        s1.setShowTickMarks(true);
        s1.setMin(0);
        s1.setMax(10);
        s1.setValue(0);
        s1.setShowTickLabels(true);
        s1.setMajorTickUnit(1);
        s1.setMinorTickCount(0);
        s1.setBlockIncrement(1);
        s1.setSnapToTicks(true);
        s1.setMinWidth(420);
        s1.setMinHeight(500);
        s1.setMaxWidth(420);
        s1.setMaxHeight(500);
        s1.setTranslateX(-172);
        s1.setTranslateY(-875);
        s1.getStylesheets().add(this.getClass().getResource("styleFan.css").toExternalForm());
        s1.setStyle("-fx-font: 20px \"Arial\"; -fx-pref-height: 100; -fx-padding: 30;");
        s1.applyCss();
   
        /*------------------------------------------------Pass Fan Speed------------------------------------------------*/
        Slider s1Pass = new Slider();
        s1Pass.setShowTickMarks(true);
        s1Pass.setMin(0);
        s1Pass.setMax(10);
        s1Pass.setValue(0);
        s1Pass.setShowTickLabels(true);
        s1Pass.setMajorTickUnit(1);
        s1Pass.setMinorTickCount(0);
        s1Pass.setBlockIncrement(1);
        s1Pass.setOrientation(Orientation.VERTICAL);
        s1Pass.setSnapToTicks(true);
        s1Pass.setMinWidth(420);
        s1Pass.setMinHeight(500);
        s1Pass.setMaxWidth(420);
        s1Pass.setMaxHeight(500);
        s1Pass.setTranslateX(1240);
        s1Pass.setTranslateY(-1375);
        s1Pass.getStylesheets().add(this.getClass().getResource("styleFanPass.css").toExternalForm());
        s1Pass.setStyle("-fx-font: 20px \"Arial\"; -fx-pref-height: 100; -fx-padding: 30;");
        s1Pass.applyCss();
        
        /*------------------------------------------------Driver Fan Animation------------------------------------------------*/
        ImageView fanAnimate = new ImageView();
        Image fanAnim = new Image("fanicon.png");
        fanAnimate.setImage(fanAnim);
        RotateTransition rt = new RotateTransition();
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setNode(fanAnimate);
        rt.setByAngle(360);
        rt.setInterpolator(Interpolator.LINEAR);      
        rt.setAutoReverse(false);
        fanAnimate.setTranslateX(10);
        fanAnimate.setTranslateY(-4130);
        s1.valueProperty().addListener(new ChangeListener<Number>() 
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                if(s1.getValue() == 0)
                {
                    rt.setRate(0);
                }
                else
                {
                    rt.setRate(s1.getValue() / 10);
                }
                rt.play();
                
            }
        }
        );
        
        /*------------------------------------------------Pass Fan Animation------------------------------------------------*/
        ImageView fanAnimatePass = new ImageView();
        Image fanAnimPass = new Image("fanicon.png");
        fanAnimatePass.setImage(fanAnimPass);
        RotateTransition rtPass = new RotateTransition();
        rtPass.setCycleCount(Animation.INDEFINITE);
        rtPass.setNode(fanAnimatePass);
        rtPass.setByAngle(360);
        rtPass.setInterpolator(Interpolator.LINEAR);      
        rtPass.setAutoReverse(false);
        fanAnimatePass.setTranslateX(1400);
        fanAnimatePass.setTranslateY(-4200);
        s1Pass.valueProperty().addListener(new ChangeListener<Number>() 
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                if(s1Pass.getValue() == 0)
                {
                    rtPass.setRate(0);
                }
                else
                {
                    rtPass.setRate(s1Pass.getValue() / 10);
                }
                rtPass.play();
            }
        }
        );
        
        /*------------------------------------------------line dividing------------------------------------------------*/
        ImageView lineView = new ImageView();
        Image line = new Image("line.png");
        lineView.setImage(line);
        lineView.setTranslateX(0);
        lineView.setTranslateY(-2360);
        
        s1.setOrientation(Orientation.VERTICAL);
        VBox root1 = new VBox(root);
        root1.setPadding(new Insets(10));
        
        root1.setStyle("-fx-background-color:white; -fx-padding: 10; -fx-pref-width: 50;");
        root1.getChildren().setAll(s1, s);
   
        /*------------------------------------------------Front Defrost------------------------------------------------*/
        ToggleGroup tg = new ToggleGroup();
        ToggleButton defrost = new ToggleButton();
        Image defrostOff = new Image(getClass().getResourceAsStream("defrost.png"));
        defrost.setGraphic(new ImageView(defrostOff));
        defrost.setSelected(false);
        defrost.getStylesheets().add(this.getClass().getResource("defrostStyle.css").toExternalForm());
        defrost.setTranslateX(199);
        defrost.setTranslateY(-2485);
        
        /*------------------------------------------------Rear Defrost / Heated Mirrors------------------------------------------------*/
        ToggleButton rearDefrost = new ToggleButton();
        Image reardefrostOff = new Image(getClass().getResourceAsStream("rearDefrost.png"));
        rearDefrost.setGraphic(new ImageView(reardefrostOff));
        rearDefrost.setSelected(false);
        rearDefrost.getStylesheets().add(this.getClass().getResource("rearDefrostStyle.css").toExternalForm());
        rearDefrost.setTranslateX(178);
        rearDefrost.setTranslateY(-2475);      
        
        /*------------------------------------------------Driver AC------------------------------------------------*/
        ToggleGroup togG = new ToggleGroup();
        ToggleButton ac = new ToggleButton("A/C");
        ac.getStylesheets().add(this.getClass().getResource("autoStyle.css").toExternalForm());
        ac.setSelected(false);
        ac.setTranslateX(212);
        ac.setTranslateY(-2815);
        
        /*------------------------------------------------Pass AC------------------------------------------------*/
        ToggleButton acPass = new ToggleButton("A/C");
        acPass.getStylesheets().add(this.getClass().getResource("autoStyle.css").toExternalForm());
        acPass.setSelected(false);
        acPass.setTranslateX(1235);
        acPass.setTranslateY(-2859);
        
        /*------------------------------------------------Driver ZoneBody------------------------------------------------*/
        ToggleButton zone = new ToggleButton();
        Image body = new Image(getClass().getResourceAsStream("zone1.png"));
        zone.setGraphic(new ImageView(body));
        zone.getStylesheets().add(this.getClass().getResource("zoneStyle.css").toExternalForm());
        zone.setTranslateX(178);
        zone.setTranslateY(-3110);
        
        /*------------------------------------------------Driver face zone------------------------------------------------*/
        ToggleButton faceZone = new ToggleButton();
        Image face = new Image(getClass().getResourceAsStream("face1.png"));
        faceZone.setGraphic(new ImageView(face));
        faceZone.setSelected(true);
        faceZone.getStylesheets().add(this.getClass().getResource("faceStyle.css").toExternalForm());
        faceZone.setTranslateX(210);
        faceZone.setTranslateY(-3235);
        
        /*------------------------------------------------Driver Feet Zone------------------------------------------------*/
        ToggleButton feetZone = new ToggleButton();
        Image feet = new Image(getClass().getResourceAsStream("feet1.png"));
        feetZone.setGraphic(new ImageView(feet));
        feetZone.setSelected(true);
        feetZone.getStylesheets().add(this.getClass().getResource("feetStyle.css").toExternalForm());
        feetZone.setTranslateX(139);
        feetZone.setTranslateY(-3250);
        
        /*------------------------------------------------Pass ZoneBody------------------------------------------------*/
        ToggleButton zonePass = new ToggleButton();
        Image bodyPass = new Image(getClass().getResourceAsStream("zone1Pass.png"));
        zonePass.setGraphic(new ImageView(bodyPass));
        zonePass.getStylesheets().add(this.getClass().getResource("zoneStyle.css").toExternalForm());
        zonePass.setTranslateX(1200);
        zonePass.setTranslateY(-3338);
        
        /*------------------------------------------------Face Zone Pass------------------------------------------------*/
        ToggleButton faceZonePass = new ToggleButton();
        Image facePass = new Image(getClass().getResourceAsStream("face1pass.png"));
        faceZonePass.setGraphic(new ImageView(facePass));
        faceZonePass.setSelected(true);
        faceZonePass.getStylesheets().add(this.getClass().getResource("faceStylePass.css").toExternalForm());
        faceZonePass.setTranslateX(1255);
        faceZonePass.setTranslateY(-3463);
        ToggleButton feetZonePass = new ToggleButton();
        Image feetPass = new Image(getClass().getResourceAsStream("feet1.png"));
        feetZonePass.setGraphic(new ImageView(feetPass));
        feetZonePass.setSelected(true);
        feetZonePass.getStylesheets().add(this.getClass().getResource("feetStyle.css").toExternalForm());
        feetZonePass.setTranslateX(1331);
        feetZonePass.setTranslateY(-3478);
        
        /*------------------------------------------------Driver Recirculate------------------------------------------------*/
        ToggleButton recirculate = new ToggleButton();
        Image recirculateAir = new Image(getClass().getResourceAsStream("recirculate.png"));
        recirculate.setGraphic(new ImageView(recirculateAir));
        recirculate.setSelected(false);
        recirculate.getStylesheets().add(this.getClass().getResource("recirculateStyle.css").toExternalForm());
        recirculate.setTranslateX(185);
        recirculate.setTranslateY(-3295);
        
        /*------------------------------------------------Pass Recirculate------------------------------------------------*/
        ToggleButton recirculatePass = new ToggleButton();
        Image recirculateAirPass = new Image(getClass().getResourceAsStream("recirculate.png"));
        recirculatePass.setGraphic(new ImageView(recirculateAirPass));
        recirculatePass.setSelected(false);
        recirculatePass.getStylesheets().add(this.getClass().getResource("recirculateStyle.css").toExternalForm());
        recirculatePass.setTranslateX(1210);
        recirculatePass.setTranslateY(-3351);
        
        /*------------------------------------------------Driver Temperature------------------------------------------------*/
        Slider s2 = new Slider(); 
        s2.setOrientation(Orientation.VERTICAL);
        s2.getStylesheets().add(this.getClass().getResource("styleTemperature.css").toExternalForm());
        s2.setStyle("-fx-pref-height: 75; -fx-padding: 30;");
        s2.setMin(49);
        s2.setMax(99);
        s2.setValue(51);
        s2.setMaxHeight(500);
        s2.setMinHeight(500);
        s2.setMinorTickCount(0);
        s2.setMajorTickUnit(1.0);
        s2.setTranslateX(320);
        s2.setTranslateY(-1870);

        /*------------------------------------------------driver temperature display on the status bar------------------------------------------------*/
        Label temp = new Label();
        temp.setFont(Font.font("Arial", 30));
        temp.setTextFill(Color.web("#02f0f0"));
        s2.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                if(s2.getValue() > 72)
                {
                    temp.setTextFill(Color.web("#ff0000"));
                }
                
                /*else if(s2.getValue() == 72)
                {
                    temp.setTextFill(Color.web("#d9d9d9"));
                }*/
                
                else if(s2.getValue() < 72)
                {
                    temp.setTextFill(Color.web("#02f0f0"));
                }
            }
        }
        );
        temp.textProperty().bind(s2.valueProperty().asString("%.0f").concat("°"));
        temp.setTranslateX(360);
        temp.setTranslateY(-4498);
        
        // NOTE: We are assuming that there are physical analog controls
        /*----------------------------------------------------------------------------------*/
       
        /*------------------------------------------------------------------------------------------*/
        
        /*       Heated Cooled Seat display button for driver       */
        
        MenuBar mb1 = new MenuBar();
        Menu seatIcon1 = new Menu();
        seatIcon1.setStyle("-fx-background-color: black;");
        Image left = new Image("seatleft.png");
        seatIcon1.setGraphic(new ImageView(left));
        seatIcon1.setVisible(true);
        
        Slider seatTempSlider = new Slider();
        seatTempSlider.setOrientation(Orientation.HORIZONTAL);
        seatTempSlider.setMin(0);
        seatTempSlider.setMax(6);
        seatTempSlider.setTranslateX(7.5);
        seatTempSlider.setValue(3);
        seatTempSlider.setVisible(true);
        seatTempSlider.getStylesheets().add(this.getClass().getResource("steeringSlider.css").toExternalForm());
        seatTempSlider.setMinorTickCount(0);
        seatTempSlider.setMajorTickUnit(1.0);
        seatTempSlider.setSnapToTicks(true);
        seatTempSlider.setShowTickMarks(true);
        seatTempSlider.setShowTickLabels(true);
        seatTempSlider.setLabelFormatter(new StringConverter<Double>()
        {
            public String toString(Double x)
            {
                if(x == 0)
                {
                    return " Warmer ";
                }
                if (x == 3)
                {
                    return "Off";
                }
                if (x == 6)
                {
                   return "Cooler";                  
                }
                return " ";
            }

            public Double fromString(String s1)
            {
                switch(s1)
                {
                    case " Warmer ":
                        return (double)0;
                        
                    case "Off":
                        return (double)3;
                    
                    case "Cooler":
                        return (double)6;
                        
                    default:
                        return (double)6;
                }
            }
        }
        );
        
        seatIcon1.showingProperty().addListener(new ChangeListener<Boolean>() // here we are seeing if the icon is selected
        {
            public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal)
            {
                seatIcon1.setGraphic(new ImageView(left));            
                if(seatTempSlider.getValue() == 0)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft3.png")));
                }
                
                else if(seatTempSlider.getValue() == 1)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft2.png")));
                }
                
                else if(seatTempSlider.getValue() == 2)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft1.png")));
                }
                
                else if(seatTempSlider.getValue() == 3)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("seatleft.png"))    );
                }
                
                else if(seatTempSlider.getValue() == 4)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft1.png")));
                }
                
                else if(seatTempSlider.getValue() == 5)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft2.png")));
                }
                
                else if(seatTempSlider.getValue() == 6)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft3.png")));
                }
            }
        }
        );
        
        seatTempSlider.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                //seatIcon1.setGraphic(new ImageView("seatleft.png"));     
                s.setValue(seatTempSlider.getValue());
                if(seatTempSlider.getValue() == 0)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft3.png")));
                }
                
                else if(seatTempSlider.getValue() == 1)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft2.png")));
                }
                
                else if(seatTempSlider.getValue() == 2)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft1.png")));
                }
                
                else if(seatTempSlider.getValue() == 3)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("seatleft.png")));
                }
                
                else if(seatTempSlider.getValue() == 4)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft1.png")));
                }
                
                else if(seatTempSlider.getValue() == 5)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft2.png")));
                }
                
                else if(seatTempSlider.getValue() == 6)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft3.png")));
                }
            }
        }
        );
        Image leftSeat = new Image("seaticon1.png");
        CustomMenuItem cmi1 = new CustomMenuItem(seatTempSlider);
        ImageView ivSeat = new ImageView(leftSeat);
        ivSeat.setImage(leftSeat);
        cmi1.setGraphic(ivSeat);
        ivSeat.setTranslateX(1);
        cmi1.setHideOnClick(false);
        
        seatIcon1.getItems().add(cmi1);
        mb1.getMenus().add(seatIcon1);
        mb1.setTranslateX(215);
        mb1.setTranslateY(-4612);
        mb1.setMinWidth(50);
        mb1.setMaxWidth(50);
        mb1.setStyle("-fx-background-color:black;");
        s.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                seatTempSlider.setValue(s.getValue());
                if(s.getValue() == 0)
                { 
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft3.png")));
                }
                else if(s.getValue() == 1)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft2.png")));
                }
                
                else if(s.getValue() == 2)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("heatedleft1.png")));
                }
                
                else if(s.getValue() == 4)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft1.png")));
                }
                
                else if(s.getValue() == 5)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft2.png")));
                }
                
                else if(s.getValue() == 6)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("cooledleft3.png")));
                }
                    
                else if (s.getValue() == 3)
                {
                    seatIcon1.setGraphic(new ImageView(new Image("seatleft.png")));
                }
            }
        }
        );
        
        /*------------------------------------------------Heated/Cooled Seat display on the Status Bar for driver-----------------------------------------*/
        /*Label seatDriver = new Label();
        Image seatLeft = new Image("seatleft.png");
        ImageView seat = new ImageView();
        seat.setImage(seatLeft);
        seatDriver.setGraphic(seat);
        seat.setVisible(true);
        s.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                if(s.getValue() == 0)
                { 
                    seatDriver.setGraphic(new ImageView(new Image("heatedleft3.png")));
                }
                else if(s.getValue() == 1)
                {
                    seatDriver.setGraphic(new ImageView(new Image("heatedleft2.png")));
                }
                
                else if(s.getValue() == 2)
                {
                    seatDriver.setGraphic(new ImageView(new Image("heatedleft1.png")));
                }
                
                else if(s.getValue() == 4)
                {
                    seatDriver.setGraphic(new ImageView(new Image("cooledleft1.png")));
                }
                
                else if(s.getValue() == 5)
                {
                    seatDriver.setGraphic(new ImageView(new Image("cooledleft2.png")));
                }
                
                else if(s.getValue() == 6)
                {
                    seatDriver.setGraphic(new ImageView(new Image("cooledleft3.png")));
                }
                    
                else if (s.getValue() == 3)
                {
                    seatDriver.setGraphic(new ImageView(new Image("seatleft.png")));
                }
            }
        }
        );
        //seatPass.textProperty().bind(s2Pass.valueProperty().asString("%.0f").concat("°"));
        seatDriver.setVisible(true);
        seatDriver.setTranslateX(235);
        seatDriver.setTranslateY(-4648);*/
        
        
        /*------------------------------------------------Pass Temperature------------------------------------------------*/
        Slider s2Pass = new Slider();
        s2Pass.setOrientation(Orientation.VERTICAL);
        s2Pass.getStylesheets().add(this.getClass().getResource("styleTemperature.css").toExternalForm());
        s2Pass.setStyle("-fx-pref-height: 75; -fx-padding: 30;");
        s2Pass.setMin(49);
        s2Pass.setMax(99);
        s2Pass.setValue(51);
        s2Pass.setMaxHeight(500);
        s2Pass.setMinHeight(500);
        s2Pass.setMinorTickCount(0);
        s2Pass.setMajorTickUnit(1.0);
        s2Pass.setTranslateX(1050);
        s2Pass.setTranslateY(-2370);
        HBox root2 = new HBox(s2, s2Pass);
        
        Label l2 = new Label();
        l2.setFont(new Font("Arial", 20));
        l2.textProperty().bind(s2Pass.valueProperty().asString("%.0f").concat("°")); 
        /*              */
        
        /*------------------------------------------------temperature display on the status bar for front passenger------------------------------------------------*/
        Label tempPass = new Label();
        tempPass.setFont(Font.font("Arial", 30));
        tempPass.setTextFill(Color.web("#02f0f0"));
        s2Pass.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                if(s2Pass.getValue() > 72)
                {
                    tempPass.setTextFill(Color.web("#ff0000"));
                }
                
                /*else if(s2Pass.getValue() == 72)
                {
                    tempPass.setTextFill(Color.web("#d9d9d9"));
                }*/
                
                else if(s2Pass.getValue() < 72)
                {
                    tempPass.setTextFill(Color.web("#02f0f0"));
                }
            }
        }
        );
        tempPass.textProperty().bind(s2Pass.valueProperty().asString("%.0f").concat("°"));
        tempPass.setTranslateX(1090);
        tempPass.setTranslateY(-4534);
        
        /*------------------------------------------------Driver Display-----------------------------------------*/
        Text driverDisplay = new Text();
        driverDisplay.setText("DRIVER");
        driverDisplay.setFont(new Font("Arial", 40));
        driverDisplay.setTranslateX(150);
        driverDisplay.setTranslateY(-4625);
        
        /*------------------------------------------------Passenger Display-----------------------------------------*/
        Text passDisplay = new Text();
        passDisplay.setText("PASSENGER");
        passDisplay.setFont(new Font("Arial", 40));
        passDisplay.setTranslateX(1100);
        passDisplay.setTranslateY(-4670);
        /*------------------------------------------------Heated/Cooled Seat display on the Status Bar for front passenger-----------------------------------------*/
        /*Label seatPass = new Label();
        Image seatRight = new Image("seatright.png");
        ImageView seat1 = new ImageView();
        seat1.setImage(seatRight);
        seatPass.setGraphic(seat1);
        seat1.setVisible(true);
        sPass.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                if(sPass.getValue() == 0)
                {
                    seatPass.setGraphic(new ImageView(new Image("heatedright3.png")));
                }
                else if(sPass.getValue() == 1)
                {
                    seatPass.setGraphic(new ImageView(new Image("heatedright2.png")));
                }
                
                else if(sPass.getValue() == 2)
                {
                    seatPass.setGraphic(new ImageView(new Image("heatedright1.png")));
                }
                
                else if(sPass.getValue() == 4)
                {
                    seatPass.setGraphic(new ImageView(new Image("cooledright1.png")));
                }
                
                else if(sPass.getValue() == 5)
                {
                    seatPass.setGraphic(new ImageView(new Image("cooledright2.png")));
                }
                
                else if(sPass.getValue() == 6)
                {
                    seatPass.setGraphic(new ImageView(new Image("cooledright3.png")));
                    
                }
                    
                else if (sPass.getValue() == 3)
                {
                    seatPass.setGraphic(new ImageView(new Image("seatright.png")));
                }
            }
        }
        );
        //seatPass.textProperty().bind(s2Pass.valueProperty().asString("%.0f").concat("°"));
        seatPass.setVisible(true);
        seatPass.setTranslateX(1175);
        seatPass.setTranslateY(-4608);*/
        
        /*       Heated Cooled Seat display button for passenger       */
        
        MenuBar mb2 = new MenuBar();
        Menu seatIcon2 = new Menu();
        seatIcon2.setStyle("-fx-background-color: black;");
        Image right = new Image("seatright.png");
        seatIcon2.setGraphic(new ImageView(right));
        seatIcon2.setVisible(true);
        
        Slider seatTempSliderPass = new Slider();
        seatTempSliderPass.setOrientation(Orientation.HORIZONTAL);
        seatTempSliderPass.setMin(0);
        seatTempSliderPass.setMax(6);
        seatTempSliderPass.setTranslateX(7.5);
        seatTempSliderPass.setValue(3);
        seatTempSliderPass.setVisible(true);
        seatTempSliderPass.getStylesheets().add(this.getClass().getResource("steeringSlider.css").toExternalForm());
        seatTempSliderPass.setMinorTickCount(0);
        seatTempSliderPass.setMajorTickUnit(1.0);
        seatTempSliderPass.setSnapToTicks(true);
        seatTempSliderPass.setShowTickMarks(true);
        seatTempSliderPass.setShowTickLabels(true);
        seatTempSliderPass.setLabelFormatter(new StringConverter<Double>()
        {
            public String toString(Double x)
            {
                if(x == 0)
                {
                    return " Warmer ";
                }
                if (x == 3)
                {
                    return "Off";
                }
                if (x == 6)
                {
                   return "Cooler";                  
                }
                return " ";
            }

            public Double fromString(String s1)
            {
                switch(s1)
                {
                    case " Warmer ":
                        return (double)0;
                        
                    case "Off":
                        return (double)3;
                    
                    case "Cooler":
                        return (double)6;
                        
                    default:
                        return (double)6;
                }
            }
        }
        );
        
        seatIcon2.showingProperty().addListener(new ChangeListener<Boolean>() // here we are seeing if the icon is selected
        {
            public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal)
            {
                seatIcon2.setGraphic(new ImageView(right)); 
                
                if(seatTempSliderPass.getValue() == 0)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("heatedright3.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 1)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("heatedright2.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 2)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("heatedright1.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 3)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("seatright.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 4)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright1.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 5)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright2.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 6)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright3.png")));
                }
            }
        }
        );
        
        
        seatTempSliderPass.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                //seatIcon1.setGraphic(new ImageView("seatleft.png"));     
                sPass.setValue(seatTempSliderPass.getValue());
                
                if(seatTempSliderPass.getValue() == 0)
                {
                    seatIcon2.setGraphic(new ImageView("heatedright3.png"));
                }
                
                else if(seatTempSliderPass.getValue() == 1)
                {
                    seatIcon2.setGraphic(new ImageView("heatedright2.png"));
                }
                
                else if(seatTempSliderPass.getValue() == 2)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("heatedright1.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 3)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("seatright.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 4)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright1.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 5)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright2.png")));
                }
                
                else if(seatTempSliderPass.getValue() == 6)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright3.png")));
                }
                
            }
        }
        );
        
        CustomMenuItem cmi2 = new CustomMenuItem(seatTempSliderPass);
        cmi2.setHideOnClick(false);
        seatIcon2.getItems().add(cmi2);
        mb2.getMenus().add(seatIcon2);
        mb2.setTranslateX(1175);
        mb2.setTranslateY(-4660);
        mb2.setMinWidth(75);
        mb2.setMaxWidth(75);
        mb2.setStyle("-fx-background-color:black;");
        sPass.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                seatTempSliderPass.setValue(sPass.getValue());
                if(sPass.getValue() == 0)
                { 
                    seatIcon2.setGraphic(new ImageView(new Image("heatedright3.png")));
                }
                else if(sPass.getValue() == 1)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("heatedright2.png")));
                }
                
                else if(sPass.getValue() == 2)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("heatedright1.png")));
                }
                
                else if(sPass.getValue() == 4)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright1.png")));
                }
                
                else if(sPass.getValue() == 5)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright2.png")));
                }
                
                else if(sPass.getValue() == 6)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("cooledright3.png")));
                }
                    
                else if (sPass.getValue() == 3)
                {
                    seatIcon2.setGraphic(new ImageView(new Image("seatright.png")));
                }
            }
        }
        );
        
        
        /*------------------------------------------------Heated & Cooled Steering Wheel------------------------------------------------*/
        MenuBar mb = new MenuBar();
        Menu steeringIcon = new Menu();
        steeringIcon.setStyle("-fx-background-color: black;");
        steeringIcon.setGraphic(new ImageView(new Image("steeringwheel1.png")));
        steeringIcon.setVisible(true);
        
        Slider steeringWheel = new Slider();
        steeringWheel.setOrientation(Orientation.HORIZONTAL);
        steeringWheel.setMin(0);
        steeringWheel.setMax(2);
        steeringWheel.setTranslateX(7.5);
        steeringWheel.setValue(1);
        steeringWheel.setVisible(true);
        steeringWheel.getStylesheets().add(this.getClass().getResource("steeringSlider.css").toExternalForm());
        steeringWheel.setMinorTickCount(0);
        steeringWheel.setMajorTickUnit(1.0);
        steeringWheel.setSnapToTicks(true);
        steeringWheel.setShowTickMarks(true);
        steeringWheel.setShowTickLabels(true);
        steeringWheel.setLabelFormatter(new StringConverter<Double>()
        {
            public String toString(Double x)
            {
                if(x == 0)
                {
                    return " Warmer ";
                }
                if (x == 1)
                {
                    return "Off";
                }
                if (x == 2)
                {
                   return "Cooler";                  
                }
                return " ";
            }

            public Double fromString(String s1)
            {
                switch(s1)
                {
                    case " Warmer ":
                        return (double)0;
                        
                    case "Off":
                        return (double)1;
                    
                    case "Cooler":
                        return (double)2;
                        
                    default:
                        return (double)2;
                }
            }
        }
        );
        
        Image cooledSteer = new Image("cooledsteeringwheel1.png");
        Image heatedSteer = new Image("heatedsteeringwheel1.png");
        Image steerSelected = new Image("steeringwheel1clicked.png");
        Image steer = new Image("steeringwheel1.png");
        steeringIcon.showingProperty().addListener(new ChangeListener<Boolean>() // here we are seeing if the icon is selected
        {
            public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal)
            {
                //steeringIcon.setGraphic(new ImageView(steerSelected));            
                if(steeringWheel.getValue() == 0)
                {
                    steeringIcon.setGraphic(new ImageView(heatedSteer));
                }
                
                else if(steeringWheel.getValue() == 1)
                {
                    steeringIcon.setGraphic(new ImageView(steer));
                }
                
                else if(steeringWheel.getValue() == 2)
                {
                    steeringIcon.setGraphic(new ImageView(cooledSteer));
                }
                
            }
        }
        );
        // both these are required. first part above is for returning true or false. second part below is for changing the number
        steeringWheel.valueProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) 
            {
                //steeringIcon.setGraphic(new ImageView(steerSelected));      
                if(steeringWheel.getValue() == 0)
                {
                    steeringIcon.setGraphic(new ImageView(heatedSteer));
                }
                
                else if(steeringWheel.getValue() == 1)
                {
                    steeringIcon.setGraphic(new ImageView(steer));
                }
                
                else if(steeringWheel.getValue() == 2)
                {
                    steeringIcon.setGraphic(new ImageView(cooledSteer));
                }
            }
        }
        );
        
        ToggleGroup group1 = new ToggleGroup(); 
        
        RadioMenuItem comfort = new RadioMenuItem();
        comfort.setText("COMFORT");
        comfort.setSelected(false);
        comfort.setStyle("-fx-text-fill: green;");
        
        RadioMenuItem normal = new RadioMenuItem();
        normal.setText("NORMAL");
        normal.setSelected(true);
        normal.setStyle("-fx-text-fill: lightgray;");
        
        RadioMenuItem sport = new RadioMenuItem();
        sport.setText("SPORT");
        sport.setSelected(false);
        sport.setStyle("-fx-text-fill: orange;");
        
        comfort.setToggleGroup(group1);
        normal.setToggleGroup(group1);
        sport.setToggleGroup(group1);
        
        
        CustomMenuItem cmi = new CustomMenuItem(steeringWheel);
        cmi.setHideOnClick(false);
        steeringIcon.getItems().add(cmi);   
        steeringIcon.getItems().add(comfort);
        steeringIcon.getItems().add(normal);
        steeringIcon.getItems().add(sport);
        
        mb.getMenus().add(steeringIcon);
        mb.setTranslateX(77);
        mb.setTranslateY(-4800);
        mb.setMinWidth(50);
        mb.setMaxWidth(50);
        mb.setStyle("-fx-background-color:black;");
        /*-----------------------------------------------------------------------------*/

        Label mode = new Label();
        mode.setFont(Font.font("Arial", 15));
        mode.setText("NORMAL");
        mode.setTextFill(Color.LIGHTGRAY);
        group1.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle)
            {
                if(comfort.isSelected() && !normal.isSelected() && !sport.isSelected())
                {
                    mode.setText("COMFORT");
                    mode.setTextFill(Color.web("#22b14c"));
                }
                else if(normal.isSelected() && !comfort.isSelected() && !sport.isSelected())
                {
                    mode.setText("NORMAL");
                    mode.setTextFill(Color.LIGHTGRAY);
                }
                else if(sport.isSelected() && !normal.isSelected() && !comfort.isSelected())
                {
                    mode.setText("SPORT");
                    mode.setTextFill(Color.ORANGE);
                }
            }
        }
        );
        
        mode.setTranslateX(135);
        mode.setTranslateY(-4830);
         
        /*------------------------------------------------Radar Cruise button------------------------------------------------*/
        ToggleButton radarCruise = new ToggleButton();
        Image radar = new Image("radar.png");
        radarCruise.setGraphic(new ImageView(radar));
        radarCruise.setSelected(false);
        radarCruise.setTranslateX(0);
        radarCruise.setTranslateY(-4459);
        radarCruise.getStylesheets().add(this.getClass().getResource("radarButton.css").toExternalForm());
        statusBar.setTranslateX(0);
        statusBar.setTranslateY(-4410);
        radarCruise.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                radarCruise rc = new radarCruise();
                Group root = new Group();
                root.getChildren().addAll(rc);
                statusBar.setVisible(true);
                tempPass.setVisible(true);
                temp.setVisible(true);
            }
        }
        );
        
        
        /*------------------------------------------------Driver Auto------------------------------------------------*/
        auto auto = new auto();
        auto.setText("AUTO");
        auto.create(s1);
        auto.setTranslateX(200);
        auto.setTranslateY(-2790);
        auto.getStylesheets().add(this.getClass().getResource("autoStyle.css").toExternalForm());
        auto.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                s1.setValue(s1.getMax());
                auto.setSelected(true);
            }
        }
        );
        s1.valueProperty().addListener((observable, oldValue, newValue) -> 
        {
            auto.setSelected(false);
        }
        );

        /*------------------------------------------------Pass Auto------------------------------------------------*/
        auto automaticPass = new auto();
        automaticPass.setText("AUTO");
        automaticPass.create(s1Pass);
        automaticPass.setTranslateX(1225);
        automaticPass.setTranslateY(-2834);
        automaticPass.getStylesheets().add(this.getClass().getResource("autoStyle.css").toExternalForm());
        automaticPass.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                s1Pass.setValue(s1.getMax());
                automaticPass.setSelected(true);
            }
        }
        );
        
        s1Pass.valueProperty().addListener((observable, oldValue, newValue) -> 
        {
            automaticPass.setSelected(false);
        }
        );
        
        /*------------------------------------------------SYNC fan driver to Pass------------------------------------------------*/
        ToggleButton syncFan = new ToggleButton("SYNC");
        syncFan.setMaxHeight(37);
        syncFan.setMinHeight(37);
        syncFan.getStylesheets().add(this.getClass().getResource("syncStyle.css").toExternalForm());
        syncFan.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                s1Pass.setValue(s1.getValue());
                syncFan.setSelected(true);
            }
        }
        );
        
        s1Pass.valueProperty().addListener(new ChangeListener<Number>() 
        {
            public void changed(ObservableValue<? extends Number> observableValue, Number previous, Number now) 
            {
                if (s1Pass.isValueChanging()) 
                {
                    syncFan.setSelected(false);
                }
            }
        }
        );
        
        syncFan.setTranslateX(-10);
        syncFan.setTranslateY(-3325);
 
        /*------------------------------------------------SYNC fan pass to driver------------------------------------------------*/
        ToggleButton syncFanPass = new ToggleButton("SYNC");
        syncFanPass.setMaxHeight(37);
        syncFanPass.setMinHeight(37);
        syncFanPass.getStylesheets().add(this.getClass().getResource("syncStyle.css").toExternalForm());
        syncFanPass.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                s1.setValue(s1Pass.getValue());
                syncFanPass.setSelected(true);
            }
        }
        );
        
        s1.valueProperty().addListener(new ChangeListener<Number>() 
        {
            public void changed(ObservableValue<? extends Number> observableValue, Number previous, Number now) 
            {
                if (s1.isValueChanging()) 
                {
                    syncFanPass.setSelected(false);
                }
            }
        }
        );
        
        syncFanPass.setTranslateX(1400);
        syncFanPass.setTranslateY(-3362);
        
       
        /*------------------------------------------------SYNC temperature driver to Pass------------------------------------------------*/
        ToggleButton syncTemperature = new ToggleButton("SYNC");
        syncTemperature.setMaxHeight(37);
        syncTemperature.setMinHeight(37);
        syncTemperature.getStylesheets().add(this.getClass().getResource("syncStyle.css").toExternalForm());
        syncTemperature.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                s2Pass.setValue(s2.getValue());
                syncTemperature.setSelected(true);
            }
        }
        );
        
        s2Pass.valueProperty().addListener(new ChangeListener<Number>() 
        {
            public void changed(ObservableValue<? extends Number> observableValue, Number previous, Number now) 
            {
                if (s2Pass.isValueChanging()) {
                    syncTemperature.setSelected(false);
                }
            }
        }
        );
        
        syncTemperature.setTranslateX(335);
        syncTemperature.setTranslateY(-3251);
       
        /*------------------------------------------------SYNC Temperature Pass to driver------------------------------------------------*/
        ToggleButton syncTemperaturePass = new ToggleButton("SYNC");
        syncTemperaturePass.setMaxHeight(37);
        syncTemperaturePass.setMinHeight(37);
        syncTemperaturePass.getStylesheets().add(this.getClass().getResource("syncStyle.css").toExternalForm());
        syncTemperaturePass.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {    
                s2.setValue(s2Pass.getValue());
                syncTemperaturePass.setSelected(true);
            }
        }
        );
        
        s2.valueProperty().addListener(new ChangeListener<Number>() 
        {
            public void changed(ObservableValue<? extends Number> observableValue, Number previous, Number now) 
            {
                if (s2.isValueChanging()) 
                {
                    syncTemperaturePass.setSelected(false);
                }
            }
        }
        );
        
        syncTemperaturePass.setTranslateX(1065);
        syncTemperaturePass.setTranslateY(-3288);
        /*------------------------------------------------------------------------------------------------*/
        VBox root3 = new VBox(root, root2);
        Scene scene = new Scene(root3);
        s2.applyCss();
        Pane p = (Pane) s2.lookup(".thumb");
        Pane p1 = (Pane) s2Pass.lookup(".thumb");
        Label l = new Label();
        root3.getChildren().setAll(sPass, s, s1, s1Pass, s2, s2Pass);
        root3.getChildren().addAll(lineView, defrost, rearDefrost, auto, automaticPass, ac, acPass, zone, faceZone, feetZone, zonePass, faceZonePass, feetZonePass, recirculate, recirculatePass, syncTemperature, syncTemperaturePass, syncFan, syncFanPass, play, skip, previous, mediaView, currentlyPlaying, fanAnimate, fanAnimatePass, equalizer, statusBar, radarCruise, temp, tempPass, c, /*seatPass,*/ /*seatDriver,*/ mb1, mb2, driverDisplay, passDisplay, mb, mode, volume, vol);
        l.setFont(new Font("Arial", 20));
        l.textProperty().bind(s2.valueProperty().asString("%.0f").concat("°"));
        p.getChildren().add(l);
        p1.getChildren().add(l2);  
        /* ------------------------------------------------------------- */
        stage.setWidth(1500);
        stage.setHeight(850);
        scene.getStylesheets().add(this.getClass().getResource("custommenu.css").toExternalForm());
        //scene.getStylesheets().add(this.getClass().getResource("custommenu.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() // this block of code will exit all threads running when program exits 
        {
            public void handle(WindowEvent we) 
            {
                Platform.exit();
                System.exit(0);
            }
        }
        );
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}