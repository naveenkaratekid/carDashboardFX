/**
 * settings is a singleton class.
 * It is only used in the radarCruise, which contains:
 * Radar Adaptive Cruise Control
 * Lane Departure Warning
 * Pre-Collision System
 */
public class settings
{
    public int i = 0;
    private static settings s;
    
    protected settings()
    {
        
    }
    
    public static settings theInstance()
    {
        if(s == null)
        {
            s = new settings();
        }
        return s;
    }
    
    public void setI(int j)
    {
        this.i = j;
    }
    
    public int getI()
    {
        return i;
    }
}