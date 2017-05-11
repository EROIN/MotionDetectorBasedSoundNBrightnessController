
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;

public class Adjust implements Runnable
{
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private static final HaarCascadeDetector detector = new HaarCascadeDetector("hand.xml");
    private Webcam webcam = null;
    private List<DetectedFace> faces = null;
    private int x1,y1,x2,y2;
    
    Adjust()
    {
        webcam = Webcam.getDefault();
	webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open(true);
        x1=x2=y1=y2=-1;
        EXECUTOR.execute(this);
        
        new Timer(50, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                //System.out.println("hsbd");
		if (faces != null) 
                {
                
		Iterator<DetectedFace> dfi = faces.iterator();
		if (dfi.hasNext()) 
                {

			DetectedFace face = dfi.next();
			Rectangle bounds = face.getBounds();
                        x2=(int)bounds.x;
                        y2=(int)bounds.y;
                        if(x1<0)
                        {
                        x1=x2;
                        y1=y2;
                        }
                        else
                        check(x1,x2,y1,y2);
                        //System.out.println( "x1=" +x1 + " y1=" +y1 + " x2" +x2 +" y2=" +y2);
                        System.out.println("x1="+x1+" y1="+y1+" x2="+x2+" y2="+y2);
                }
                else
                {
                x1=-1;
                //System.out.pr    
                }
                }
            }
        }
        ).start();
    }
    
    @Override
    public void run() 
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        while (true) 
        {
			if (!webcam.isOpen()) 
                        {
				return;
			}
//                        System.out.println("dsfbsf");
                        //can't really help with the name.
			faces = detector.detectFaces(ImageUtilities.createFImage(webcam.getImage()));
	}
    }
    
    public static void main(String[] args) throws IOException 
    {
        Adjust adjust = new Adjust();
    }
    
    public void check(int x1,int x2,int y1,int y2)
    {
        if(Math.abs(x1-x2)>50)
        {
            try {
                String str=x1>x2?"xbacklight -inc 5":"xbacklight -dec 5";
                this.x1=this.x2;
                Runtime.getRuntime().exec(str);
            } catch (IOException ex) {
                Logger.getLogger(Adjust.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(Math.abs(y1-y2)>50)
        {
            try {
                String s=y1>y2?"+":"-";
                String str="amixer -D pulse sset Master 10%"+ s;
                this.y1=this.y2;
                Runtime.getRuntime().exec(str);
            } catch (IOException ex) {
                Logger.getLogger(Adjust.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}