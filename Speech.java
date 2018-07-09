import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import java.io.*;

/**
 * Write a description of class Speech here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Speech
{
    // instance variables - replace the example below with your own
    private int x;

    /**
     * Constructor for objects of class Speech
     */
    public Speech()
    {
        // initialise instance variables
        x = 0;
    }

    public String launchVoice() throws Exception
    {
        System.out.println(System.getProperty("user.dir"));
        Configuration config = new Configuration();
        config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        config.setDictionaryPath("7569.dic");
        config.setLanguageModelPath("7569.lm");

        
        LiveSpeechRecognizer lsr = new LiveSpeechRecognizer(config);
        lsr.startRecognition(true);
        
        SpeechResult result = null;
        String keyword = "";
        while((result = lsr.getResult()) != null)
        {
            String cmd = result.getHypothesis().toLowerCase();
            System.out.println(cmd);
            
            if(cmd.contains("i am cold") || cmd.equalsIgnoreCase("cold"))
            {
                System.out.println("Ok. I have turned up the temperature by 5 degrees");
                keyword = "Hot";
                return keyword;
                //return "Hot";
            }
            
            else if(cmd.contains("i am hot") || cmd.equalsIgnoreCase("hot"))
            {
                System.out.println("Ok. I have turned down the temperature by 5 degrees");
                keyword = "Cool";
                return keyword;
                //return "Cool";
            }
            
            else if(cmd.contains("turn the heat up"))
            {
                System.out.println("Ok. I have turned up the temperature by 5 degrees");
                keyword = "Hot";
                return keyword;
                //return "Hot";
            }
            
            else if(cmd.contains("turn the heat down"))
            {
                System.out.println("Ok. I have turned down the temperature by 5 degrees");
                keyword = "Cool";
                return keyword;
                //return "Cool";
            }
            else if(cmd.contains("my steering wheel is cold"))
            {
                System.out.println("Ok. I have turned on the heated steering wheel");
                keyword = "Steering Hot";
                return keyword;
                //return "Steering Hot";
            }
            
            else if(cmd.contains("my steering wheel is hot"))
            {
                System.out.println("Ok. I have turned on the cooled steering wheel");
                keyword = "Steering Cold";
                return keyword;
                //return "Steering Cold";
            }
            else if(cmd.contains("my seat is hot"))
            {
                System.out.println("Ok. I have turned on your cooled seat to the max setting");
                keyword = "Seat Cold";
                return keyword;
                //return "Seat Cold";
            }
            else if(cmd.contains("my seat is cold"))
            {
                System.out.println("Ok. I have turned on your heated seat to the max setting");
                keyword = "Seat Hot";
                return keyword;
                //return "Seat Hot";
            }
        
            else if(cmd.contains("i am hot turn the A C on") || cmd.contains("very hot"))
            {
                System.out.println("Ok. I have turned on the A/C");
                return "A/C";
            }
            
            else if(cmd.contains("not enough fan speed"))
            {
                System.out.println("Ok. Let me increase the fan speed");
                keyword = "Fan Increase";
                return keyword;
            }
            
            else if(cmd.contains("too much fan speed"))
            {
                System.out.println("Ok. Let me decrease the fan speed");
                keyword = "Fan Decrease";
                return keyword;
            }
            
            else if(cmd.equals("cancel") || cmd.equals("exit") || cmd.equals("stop"))
            {
                System.out.println("Exiting....");
                System.exit(0);
            }
            else
            {
                System.out.println("Sorry. I don't understand that. Please try again");
                
            }
        }
        //lsr.stopRecognition();
        return keyword;
    }
    
    public static void main(String[] args) throws Exception
    {
        Speech s = new Speech();
        s.launchVoice();
    }
}
