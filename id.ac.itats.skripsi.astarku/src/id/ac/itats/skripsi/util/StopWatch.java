package id.ac.itats.skripsi.util;


public class StopWatch
{
    private long lastTime;
    private long time;
    private String name = "";

    public StopWatch( String name )
    {
        this.name = name;
    }

    public StopWatch()
    {
    }

    public StopWatch setName( String name )
    {
        this.name = name;
        return this;
    }

    public StopWatch start()
    {
        lastTime = System.nanoTime();
        return this;
    }

    public StopWatch stop()
    {
        if (lastTime < 0)
        {
            return this;
        }
        time += System.nanoTime() - lastTime;
        lastTime = -1;
        return this;
    }

    /**
     * @return the delta time in milliseconds
     */
    public long getTime()
    {
        return time / 1000000;
    }

    @Override
    public String toString()
    {               

        return name + " time: " + getSeconds() +" Second";
    }

    public float getSeconds()
    {
        return time / 1e9f;
    }
}
