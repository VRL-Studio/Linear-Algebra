package eu.mihosoft.vrl.linalg;


import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.visual.GlobalBackgroundPainter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * A simple 2df geometry & data plotter for VRL-Studio.
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@eu.mihosoft.vrl.annotation.ComponentInfo(name="File2DPlotter", category="Linear Algebra/Graphics", description = "visualizes *.2df files")
public class File2DPlotter implements Serializable, GlobalBackgroundPainter {
  private static final long serialVersionUID=1;

  private transient double[][] vertices;
  private transient int[][] triangles;
  private transient double[] data;
  private transient Color minC;
  private transient Color maxC;
  private transient Double minV;
  private transient Double maxV;
  private transient boolean showText;
  private transient float fontSize;
  private transient Color textColor;
  private transient boolean showGrid;
  private transient Color gridColor;
  private transient int resX;
  private transient int resY;
  private transient int backgroundOffsetX;
  private transient int backgroundOffsetY;
  private transient int backgroundTransparency;
  private transient BufferedImage buffer;
  private transient boolean useBackgroundBuffer;
  private transient boolean dirty;



  @MethodInfo(hide=false)
  public BufferedImage plot(
    @ParamGroupInfo(group="Data|true|Geometry Data (Vertixces, Triangles and Vertex data)")
    @ParamInfo(name="Vertices", style="default", options="serialization=false") double[][] vertices,
    @ParamInfo(name="Triangles", style="default", options="serialization=false") int[][] triangles,
    @ParamInfo(name="Data", style="default", nullIsValid = true, options="serialization=false") double[] data,
    @ParamGroupInfo(group="Settings|false|Plotter Settings (Colors, Text etc.);Value Range|false|Colors for value range")
    @ParamInfo(name="Min ", style="color-chooser", options="value=java.awt.Color.green") Color minC,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Max", style="color-chooser", options="value=java.awt.Color.red") Color maxC,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Min Value", style="color-chooser", nullIsValid = true, options="") Double minV,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Max Value", style="color-chooser", nullIsValid = true, options="") Double maxV,
    @ParamGroupInfo(group="Settings;Grid|false|Grid options (show grid, grid color)")
    @ParamInfo(name="Show Grid", style="default", options="value=true") boolean showGrid,
    @ParamGroupInfo(group="Settings;Grid")
    @ParamInfo(name="Grid Color", style="color-chooser", options="value=java.awt.Color.black") Color gridColor,
    @ParamGroupInfo(group="Settings;Graphics|false|Graphics options (Resolution, etc.);Resolution|false|Image resolution")
    @ParamInfo(name="X", style="default", options="value=400") int resX,
    @ParamGroupInfo(group="Settings;Graphics;Resolution")
    @ParamInfo(name="Y", style="default", options="value=400") int resY,
    @ParamGroupInfo(group="Settings;Graphics;Background|false|Show Plotter on Background")
    @ParamInfo(name="Show on Background", style="default", options="value=false") boolean paintOnBackground,
    @ParamGroupInfo(group="Settings;Graphics;Background")
    @ParamInfo(name="Use Image Buffer", style="default", options="value=true") boolean useBackgroundBuffer,
    @ParamGroupInfo(group="Settings;Graphics;Background")
    @ParamInfo(name="Background X Offset", style="default", options="value=0") int backgroundOffsetX,
    @ParamGroupInfo(group="Settings;Graphics;Background")
    @ParamInfo(name="Background Y Offset", style="default", options="value=0") int backgroundOffsetY,
    @ParamGroupInfo(group="Settings;Graphics;Background")
    @ParamInfo(name="Global Background Transparency", style="slider", options="value=0;min=0;max=100") int backgroundTransparency
    
  ){

    this.vertices = vertices;
    this.triangles = triangles;
    this.data = data;
    
    this.minC = minC;
    this.maxC = maxC;
    this.minV = minV;
    this.maxV = maxV;
    this.showText = showText;
    this.fontSize = fontSize;
    this.textColor= textColor;
    this.showGrid = showGrid;
    this.gridColor = gridColor;
    this.resX = resX;
    this.resY = resY;

    dirty = true;


    //if(buffer==null || buffer.getWidth() != resX || buffer.getHeight() != resY) {
      buffer =
          new BufferedImage(resX,resY,BufferedImage.TYPE_INT_ARGB);
    //}


    Graphics2D g2 = buffer.createGraphics();

    this.useBackgroundBuffer = useBackgroundBuffer;

    this.backgroundOffsetX = 0;
    this.backgroundOffsetY = 0;

    this.backgroundTransparency = 0;

    paintGeometry(g2,0,0);

    this.backgroundOffsetX = backgroundOffsetX;
    this.backgroundOffsetY = backgroundOffsetY;

    this.backgroundTransparency = backgroundTransparency;

    VisualCanvas canvas = 
      VRL.getCurrentProjectController().
      getCurrentCanvas();

    if(paintOnBackground) {
      canvas.remove(this);
      canvas.add(this);
      canvas.repaint();
    } else {
      canvas.remove(this);
      canvas.repaint();
    }
    
    g2.dispose();
    
    return buffer;
  }

  @MethodInfo(noGUI=true)
  public void paintGlobal(Graphics g) {
    if(useBackgroundBuffer) {

      if(dirty) {

        dirty = false;
        
        //if(buffer==null || buffer.getWidth() != resX || buffer.getHeight() != resY) {
            buffer = new BufferedImage(resX,resY,BufferedImage.TYPE_INT_ARGB);
        //}
  
        Graphics2D g2B = buffer.createGraphics();
  
        paintGeometry(g2B, 0, 0);
  
        g2B.dispose();
      }

      if(buffer!=null) {
        Graphics2D g2 = (Graphics2D)g;
        Composite composite = g2.getComposite();
        AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    (float)(1.0-backgroundTransparency/100.0));
        g2.setComposite(ac1);
        
        g.drawImage(buffer, backgroundOffsetX, backgroundOffsetY, null);
        g2.setComposite(composite);
      }

    } else {
      Graphics2D g2 = (Graphics2D)g ;
      Composite composite = g2.getComposite();
      AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    (float)(1.0-backgroundTransparency/100.0));
      g2.setComposite(ac1);
        
      paintGeometry(g, backgroundOffsetX, backgroundOffsetY);
      g2.setComposite(composite);
    }
  }

  
  private void paintGeometry(Graphics graphics, int offsetX, int offsetY) {
    Graphics2D g2 = (Graphics2D)graphics;

    // TODO catch NPE
    int numVerts = vertices.length;
    int numTris = triangles.length;
    //double[] data = new double[vertices.length] // tmp
    int dataLength = data==null?0:data.length;

    double minVal = data==null?0:data[0];
    double maxVal = data==null?0:data[0];

    for (int x = 0; x < dataLength; x++) { 
        double value = data[x] ;
        if(value < minVal) minVal = value;
        if(value > maxVal) maxVal = value;
    }
    

    if(this.minV!=null) {
      minVal = this.minV;
    }

    if(this.maxV!=null) {
      maxVal = this.maxV;
    }

    double valDiff = maxVal-minVal;

    Font font = g2.getFont().deriveFont( fontSize );
    g2.setFont(font);;

    g2.setStroke(new BasicStroke(1));

    double xMin = vertices[0][0];
    double yMin = vertices[0][1];
    double xMax = vertices[0][0];
    double yMax = vertices[0][1];

    for (int i = 0; i < numVerts; i++) { 
      double x = vertices[i][0];
      double y = vertices[i][1];
      if(x < xMin) xMin = x;
      if(y < yMin) yMin = y;
      if(x > xMax) xMax = x;
      if(y > yMax) yMax = y;
    }

    double xDiff = xMax - xMin;
    double yDiff = yMax - yMin;

    double dx = (double)(resX)/(double)(xDiff);
    double dy = (double)(resY)/(double)(yDiff);

    double rw = dx; double rh = dy;
    
    for (int i = 0; i < numTris; i++) { 

        int v1 = triangles[i][0];
        int v2 = triangles[i][1];
        int v3 = triangles[i][2];

        double vx1 = vertices[v1][0];
        double vx2 = vertices[v2][0];
        double vx3 = vertices[v3][0];
        double vy1 = vertices[v1][1];
        double vy2 = vertices[v2][1];
        double vy3 = vertices[v3][1];


        double vd1 = data==null?0:data[v1];
        double vd2 = data==null?0:data[v2];
        double vd3 = data==null?0:data[v3];
        

        Composite composite = g2.getComposite();

        AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    (float)(1.0-backgroundTransparency/100.0));
        g2.setComposite(ac1);

        //g2.setPaint(new Color(r,g,b,a))

        int[] rx = new int[3];
        int[] ry = new int[3];

        rx[0] = (int)(vx1*dx + offsetX - xMin*dx);
        ry[0] = (int)(vy1*dy + offsetY - yMin*dy);
        rx[1] = (int)(vx2*dx + offsetX - xMin*dx);
        ry[1] = (int)(vy2*dy + offsetY - yMin*dy);
        rx[2] = (int)(vx3*dx + offsetX - xMin*dx);
        ry[2] = (int)(vy3*dy + offsetY - yMin*dy);
        

        if(data!=null) {
          Color c1 = interpolateColor(vd1,minVal,maxVal);
          Color c2 = interpolateColor(vd2,minVal,maxVal);
          Color c3 = interpolateColor(vd3,minVal,maxVal);
  
          Paint p = createTexture(rx,ry,c1,c2,c3);
          g2.setPaint(p);
          g2.fillPolygon(rx,ry,3);
        }
        
        if(showGrid) {
          g2.setColor(gridColor);       
          g2.drawPolygon(rx,ry,3);
        }

        g2.setComposite(composite);
    }
  }

  public void removeFromBackground() {
      VisualCanvas canvas = 
        VRL.getCurrentProjectController().
        getCurrentCanvas();
      canvas.remove(this);
      canvas.repaint();
  }

  private Color interpolateColor(double value, double minVal, double maxVal) {
      double valueforC = Math.min(maxVal,Math.max(value,minVal));
      double valDiff = maxVal-minVal;

      int r = (int)(minC.getRed()+ (valueforC-minVal)/valDiff*(maxC.getRed()-minC.getRed()));
      int g = (int)(minC.getGreen()+ (valueforC-minVal)/valDiff*(maxC.getGreen()-minC.getGreen()));
      int b = (int)(minC.getBlue()+ (valueforC-minVal)/valDiff*(maxC.getBlue()-minC.getBlue()));
      int a = (int)(minC.getAlpha()+ (valueforC-minVal)/valDiff*(maxC.getAlpha()-minC.getAlpha()));

      Color valueColor = new Color(r,g,b,a);

      return valueColor;
  }


  private  int areaTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
    return (int)(0.5*Math.abs((x1-x3)*(y2-y1)-(x1-x2)*(y3-y1)));
  }

  
  private Paint createTexture(int[] rx, int[] ry, Color c1, Color c2, Color c3) {

    // extend triangle by 26 pixels (estimate) to prevent black holes from low res
    double extension = 26;
    int centerX = (rx[0]+rx[1]+rx[2])/3;
    int centerY = (ry[0]+ry[1]+ry[2])/3;

    int centerToR0X = rx[0] - centerX;
    int centerToR0Y = ry[0] - centerY;
    int centerToR1X = rx[1] - centerX;
    int centerToR1Y = ry[1] - centerY;
    int centerToR2X = rx[2] - centerX;
    int centerToR2Y = ry[2] - centerY;

    double dist0 = Math.sqrt(centerToR0X*centerToR0X + centerToR0Y*centerToR0Y);
    double dist1 = Math.sqrt(centerToR1X*centerToR1X + centerToR1Y*centerToR1Y);
    double dist2 = Math.sqrt(centerToR2X*centerToR2X + centerToR2Y*centerToR2Y);

    int[] rxNew = {
            (int) (rx[0] + extension * centerToR0X / dist0),
            (int) (rx[1] + extension * centerToR1X / dist1),
            (int) (rx[2] + extension * centerToR2X / dist2)
    };

    int[] ryNew = {
          (int) (ry[0] + extension * centerToR0Y / dist0),
                  (int) (ry[1] + extension * centerToR1Y / dist1),
                  (int) (ry[2] + extension * centerToR2Y / dist2)
    };

    Polygon pl=new Polygon(rxNew,ryNew,3);
    Rectangle r=pl.getBounds();
    
    double scale = 0.1;

    double scaledWidth = r.width * scale;
    double scaledHeight = r.height * scale;

    // create low-res image texture with overlapping (extension, see above)
    BufferedImage b=new BufferedImage((int)scaledWidth +1, (int)scaledHeight+1, BufferedImage.TYPE_INT_RGB);
    int a=areaTriangle(rxNew[0], ryNew[0], rxNew[1], ryNew[1], rxNew[2], ryNew[2]);
    int[] ca1={c1.getRed(), c1.getGreen(), c1.getBlue()}; int[] ca2={c2.getRed(), c2.getGreen(), c2.getBlue()}; int[] ca3={c3.getRed(), c3.getGreen(), c3.getBlue()};
    for(int i=0; i<scaledWidth; i++)
    for(int j=0; j<scaledHeight; j++) {
      //if(pl.contains(r.x+(int)(i/scale), r.y+(int)(j/scale))) {
        int ix=r.x+(int)(i/scale), jy=r.y+(int)(j/scale);
        int a1=areaTriangle(ix, jy, rxNew[0], ryNew[0], rxNew[1], ryNew[1]);
        int a2=areaTriangle(ix, jy, rxNew[0], ryNew[0], rxNew[2], ryNew[2]);
        int a3=areaTriangle(ix, jy, rxNew[1], ryNew[1], rxNew[2], ryNew[2]);
    
        int[] c=new int[3];
        for(int l=0; l<3; l++) c[l]=(int)((1.0*a1/a)*ca3[l]+(1.0*a2/a)*ca2[l]+(1.0*a3/a)*ca1[l]);
        int cr = Math.min(255, c[0]);
        int cg = Math.min(255, c[1]);
        int cb = Math.min(255, c[2]);
        //int colWithAlpha = (ca << 24) | (cr << 16) | (cg << 8) | cb;
        int col = (cr << 16) | (cg << 8) | cb;
        b.setRGB(i, j, col);
      //}
    }

    TexturePaint texture = new TexturePaint(b, r);
    
    return texture;
    
  }
}