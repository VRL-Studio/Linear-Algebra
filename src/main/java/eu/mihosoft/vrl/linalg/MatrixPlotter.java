package eu.mihosoft.vrl.linalg;


import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VRL;;
import eu.mihosoft.vrl.visual.GlobalBackgroundPainter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@eu.mihosoft.vrl.annotation.ComponentInfo(name="MatrixPlotter", category="Linear Algebra/Graphics", description = "matrix plotter (values and structure)")
public class MatrixPlotter implements Serializable, GlobalBackgroundPainter {
  private static final long serialVersionUID=1;

  private transient double[][] M;
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
    @ParamInfo(name="Vector", style="default", options="serialization=false") double[] v,
    @ParamInfo(name="Stride", style="default", options="value=1") int stride,
    @ParamGroupInfo(group="Settings|false|Plotter Settings (Colors, Text etc.);Value Range|false|Colors for value range")
    @ParamInfo(name="Min ", style="color-chooser", options="value=java.awt.Color.green") Color minC,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Max", style="color-chooser", options="value=java.awt.Color.red") Color maxC,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Min Value", style="color-chooser", nullIsValid = true, options="") Double minV,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Max Value", style="color-chooser", nullIsValid = true, options="") Double maxV,
    @ParamGroupInfo(group="Settings;Text|false|Text size & color")
    @ParamInfo(name="Show Text", style="default", options="value=false") boolean showText,
    @ParamGroupInfo(group="Settings;Text")
    @ParamInfo(name="Font Size", style="default", options="value=12.0") float fontSize,
    @ParamGroupInfo(group="Settings;Text")
    @ParamInfo(name="Text Color", style="color-chooser", options="value=java.awt.Color.black") Color textColor,
    @ParamGroupInfo(group="Settings;Grid|false|Grid options (show grid, grid color)")
    @ParamInfo(name="Show Grid", style="default", options="value=false") boolean showGrid,
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
    double[][] M = new double[stride][];

    int numRows = v.length/stride;

    for (int j = 0; j < numRows; j++) {
      M[j] = new double[stride];
      for (int i = 0; i < stride; i++) { 
        M[j][i] = v[i+j*stride];
      }
    }
    
    return plot(M,
      minC,maxC,minV,maxV,
      showText,fontSize,textColor,
      showGrid,gridColor,
      resX,resY,
      paintOnBackground,
      useBackgroundBuffer,
      backgroundOffsetX, backgroundOffsetY,
      backgroundTransparency);
  }

  @MethodInfo(hide=false)
  public BufferedImage plot(
    @ParamInfo(name="Vector", style="default", options="serialization=false") double[] v,
    @ParamGroupInfo(group="Settings|false|Plotter Settings (Colors, Text etc.);Value Range|false|Colors for value range")
    @ParamInfo(name="Min ", style="color-chooser", options="value=java.awt.Color.green") Color minC,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Max", style="color-chooser", options="value=java.awt.Color.red") Color maxC,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Min Value", style="color-chooser", nullIsValid = true, options="") Double minV,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Max Value", style="color-chooser", nullIsValid = true, options="") Double maxV,
    @ParamGroupInfo(group="Settings;Text|false|Text size & color")
    @ParamInfo(name="Show Text", style="default", options="value=false") boolean showText,
    @ParamGroupInfo(group="Settings;Text")
    @ParamInfo(name="Font Size", style="default", options="value=12.0") float fontSize,
    @ParamGroupInfo(group="Settings;Text")
    @ParamInfo(name="Text Color", style="color-chooser", options="value=java.awt.Color.black") Color textColor,
    @ParamGroupInfo(group="Settings;Grid|false|Grid options (show grid, grid color)")
    @ParamInfo(name="Show Grid", style="default", options="value=false") boolean showGrid,
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
    double[][] M = new double[1][];

    M[0] = v;
    
    return plot(M,
      minC,maxC,minV,maxV,
      showText,fontSize,textColor,
      showGrid,gridColor,
      resX,resY,
      paintOnBackground,
      useBackgroundBuffer,
      backgroundOffsetX, backgroundOffsetY,
      backgroundTransparency);
  }

  @MethodInfo(hide=false)
  public BufferedImage plot(
    @ParamInfo(name="Matrix", style="default", options="serialization=false") double[][] M,
    @ParamGroupInfo(group="Settings|false|Plotter Settings (Colors, Text etc.);Value Range|false|Colors for value range")
    @ParamInfo(name="Min ", style="color-chooser", options="value=java.awt.Color.green") Color minC,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Max", style="color-chooser", options="value=java.awt.Color.red") Color maxC,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Min Value", style="color-chooser", nullIsValid = true, options="") Double minV,
    @ParamGroupInfo(group="Settings;Value Range")
    @ParamInfo(name="Max Value", style="color-chooser", nullIsValid = true, options="") Double maxV,
    @ParamGroupInfo(group="Settings;Text|false|Text size & color")
    @ParamInfo(name="Show Text", style="default", options="value=false") boolean showText,
    @ParamGroupInfo(group="Settings;Text")
    @ParamInfo(name="Font Size", style="default", options="value=12.0") float fontSize,
    @ParamGroupInfo(group="Settings;Text")
    @ParamInfo(name="Text Color", style="color-chooser", options="value=java.awt.Color.black") Color textColor,
    @ParamGroupInfo(group="Settings;Grid|false|Grid options (show grid, grid color)")
    @ParamInfo(name="Show Grid", style="default", options="value=false") boolean showGrid,
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

    this.M = M;
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


    if(buffer==null || buffer.getWidth() != resX || buffer.getHeight() != resY) {
      buffer =
          new BufferedImage(resX,resY,BufferedImage.TYPE_INT_ARGB);
    }


    Graphics2D g2 = buffer.createGraphics();

    this.useBackgroundBuffer = useBackgroundBuffer;

    this.backgroundOffsetX = 0;
    this.backgroundOffsetY = 0;

    this.backgroundTransparency = 0;

    paintMatrix(g2,0,0);

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
        
        if(buffer==null || buffer.getWidth() != resX || buffer.getHeight() != resY) {
            buffer = new BufferedImage(resX,resY,BufferedImage.TYPE_INT_ARGB);
        }
  
        Graphics2D g2B = buffer.createGraphics();
  
        paintMatrix(g2B, 0, 0);
  
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
      Graphics2D g2 = (Graphics2D)g;
      Composite composite = g2.getComposite();
      AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    (float)(1.0-backgroundTransparency/100.0));
      g2.setComposite(ac1);
        
      paintMatrix(g, backgroundOffsetX, backgroundOffsetY);
      g2.setComposite(composite);
    }
  }

  
  private void paintMatrix(Graphics graphics, int offsetX, int offsetY) {
    Graphics2D g2 = (Graphics2D)graphics;

    // TODO catch NPE
    int numRows = M.length;
    int numCols = M[0].length;

    double minVal = M[0][0];
    double maxVal = M[0][0];

    for (int y = 0; y < numRows; y++) { 
      for (int x = 0; x < numCols; x++) { 
        double value = M[y][x] ;
        if(value < minVal) minVal = value;
        if(value > maxVal) maxVal = value;
      }
    }

    if(this.minV!=null) {
      minVal = this.minV;
    }

    if(this.maxV!=null) {
      maxVal = this.maxV;
    }

    double valDiff = maxVal-minVal;

    Font font = g2.getFont().deriveFont( fontSize );
    g2.setFont(font);

    g2.setStroke(new BasicStroke(1));

    double dx = (double)(resX)/(double)(numCols);
    double dy = (double)(resY)/(double)(numRows);

    double rw = dx; double rh = dy;
    
    for (int y = 0; y < numRows; y++) { 
      for (int x = 0; x < numCols; x++) {  

        double value = M[y][x] ;

        double valueforC = Math.min(maxVal,Math.max(value,minVal));

        int r = (int)(minC.getRed()+ (valueforC-minVal)/valDiff*(maxC.getRed()-minC.getRed()));
        int g = (int)(minC.getGreen()+ (valueforC-minVal)/valDiff*(maxC.getGreen()-minC.getGreen()));
        int b = (int)(minC.getBlue()+ (valueforC-minVal)/valDiff*(maxC.getBlue()-minC.getBlue()));
        int a = (int)(minC.getAlpha()+ (valueforC-minVal)/valDiff*(maxC.getAlpha()-minC.getAlpha()));

        Composite composite = g2.getComposite();

        AlphaComposite ac1 =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    (float)(1.0-backgroundTransparency/100.0));
        g2.setComposite(ac1);

        g2.setPaint(new Color(r,g,b,a));

        double rx = x*dx + offsetX;
        double ry = y*dy + offsetY;

        Shape rect = new java.awt.geom.Rectangle2D.Double(rx,ry,rw,rh);

        g2.fill(rect);
        
        if(showGrid) {
          g2.setColor(gridColor);       
          g2.draw(rect);
        }

        if(showText) {
          String valString = String.format(java.util.Locale.US,"%.2f",value);
          g2.setColor(textColor);
          Rectangle2D tBounds = g2.getFontMetrics().getStringBounds(valString,g2);
          double tw = tBounds.getWidth();
          double th = tBounds.getHeight();
          int tx = (int)(rx+rw/2.0-tw/2.0);
          int ty = (int)(ry+rh/2.0+th/2.0-2);
          g2.drawString(valString,tx,ty);
        }

        g2.setComposite(composite);
      }
    }
  }

  public void removeFromBackground() {
      VisualCanvas canvas = 
        VRL.getCurrentProjectController().
        getCurrentCanvas();
      canvas.remove(this);
      canvas.repaint();
  }
}