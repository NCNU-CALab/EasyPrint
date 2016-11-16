import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import javax.swing.*;
public class EasyPrint {
    public EasyPrint() {
    }
    /**
     * @param page page.pageCreated(EasyPrint) will be called when a new page is created.
     */
    public EasyPrint(PageListener page) {
        pl = page;
    }
    // definitions for alignment
    /**
     * Draw String with left alignment
     */
    public static final int LEFT = 0;
    /**
     * Draw String with right alignment
     */
    public static final int RIGHT = 1;
    /**
     * Draw String in the center
     */
    public static final int CENTER = 2;
    /**
     * Draw String with both justify
     */
    public static final int BOTH = 3;
    /**
     * Draw String to spread evenly
     */
    public static final int SPREAD = 4;
    /**
     * Draw String in vertical
     */
    public static final int VERTICAL = 5;
    /**
     * Draw String in vertical justify
     */
    public static final int VBOTH = 6;
    /**
     * Draw String in vertical center
     */
    public static final int VCENTER = 7;
    /**
     * Draw String in vertical spread
     */
    public static final int VSPREAD = 8;
    // internal processing mode
    private static final int COUNTPAGE = 0;
    private static final int PREVIEW = 1;
    private static final int PRINT = 2;
    private static String[] modeString = {"COUNTPAGE","PREVIEW","PRINT"};
    /**
     * Get the cursor position.
     * @return {x,y} of cursor position in inches.
     */
    public double[] getXY() {
        mode = COUNTPAGE;
        currentPage = 1;
        processCommand();
        return new double[] {(double)col[inCol][4]/dots, (double)col[inCol][5]/dots};
    }
    /**
     * Get the X coordiate of cursor.
     */
    public double getX() {
        return getXY()[0];
    }
    /**
     * Get the Y coordiate of cursor.
     */
    public double getY() {
        return getXY()[1];
    }
    /**
     * Set the cursor position.
     * @param x The x coordiate in inches
     * @param y The y coordiate in inches
     */
    public void setXY(double x, double y) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETXY, new Object[]{new Double(x),new Double(y)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETXY, new Object[]{new Double(x),new Double(y)},inProcess),commandAt+batchSize);
        }
    }
    // methods for graphics
    /**
     * Clears the specified rectangle by filling it with the background color of the current drawing surface. This operation does not use the current paint mode.
     * <p>
     * The background color of offscreen images may be system dependent. Applications should
     * use <code>setColor</code> followed by <code>fillRect</code> to
     * ensure that an offscreen image is cleared to a specific color.
     * @param x the <i>x</i> coordinate of the rectangle to clear in inches.
     * @param y the <i>y</i> coordinate of the rectangle to clear in inches.
     * @param width the width of the rectangle to clear in inches.
     * @param height the height of the rectangle to clear in inches.
     */
    public void	clearRect(double x, double y, double width, double height) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.CLEARRECT, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.CLEARRECT, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Intersects the current clip with the specified rectangle.
     * @param x the <i>x</i> coordinate of the rectangle to clear in inches.
     * @param y the <i>y</i> coordinate of the rectangle to clear in inches.
     * @param width the width of the rectangle to clear in inches.
     * @param height the height of the rectangle to clear in inches.
     */
    public void clipRect(double x, double y, double width, double height) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.CLEARRECT, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.CLEARRECT, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Copies an area of the component by a distance specified by
     * <code>dx</code> and <code>dy</code>. From the point specified
     * by <code>x</code> and <code>y</code>, this method
     * copies downwards and to the right.  To copy an area of the
     * component to the left or upwards, specify a negative value for
     * <code>dx</code> or <code>dy</code>.
     * If a portion of the source rectangle lies outside the bounds
     * of the component, or is obscured by another window or component,
     * <code>copyArea</code> will be unable to copy the associated
     * pixels. The area that is omitted can be refreshed by calling
     * the component's <code>paint</code> method.
     * @param x      the <i>x</i> coordinate of the source rectangle in inches.
     * @param y      the <i>y</i> coordinate of the source rectangle in inches.
     * @param width  the width of the source rectangle in inches.
     * @param height the height of the source rectangle in inches.
     * @param dx     the horizontal distance to copy the pixels inches.
     * @param dy     the vertical distance to copy the pixels in inches.
     */
    public void copyArea(double x, double y, double width, double height, double dx, double dy) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.COPYAREA, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height),new Double(dx),new Double(dy)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.COPYAREA, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height),new Double(dx),new Double(dy)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws the text given by the specified string, using this
     * graphics context's current font and color. The baseline of the
     * leftmost character is at position (<i>x</i>,&nbsp;<i>y</i>) in inches
     * @param s   the string to be drawn.
     * @param x   the <i>x</i> coordinate in inches.
     * @param y   the <i>y</i> coordinate in inches.
     */
    public void drawString(String s, double x, double y) {
        drawString(s, x, y, 1.0, LEFT);
    }
    /**
     * Draws the text given by the specified string, using this
     * graphics context's current font and color.
     * @param s     the string to be drawn.
     * @param x     the <i>x</i> coordinate in inches.
     * @param y     the <i>y</i> coordinate in inches.
     * @param w     the width of the drawing area in inches.
     * @param style style for the text.
     * @see         EasyPrint#LEFT
     * @see         EasyPrint#RIGHT
     * @see         EasyPrint#BOTH
     * @see         EasyPrint#CENTER
     * @see         EasyPrint#SPREAD
     * @see         EasyPrint#VERTICAL
     * @see         EasyPrint#VBOTH
     * @see         EasyPrint#VSPREAD
     * @see         EasyPrint#VCENTER
     */
    public void drawString(String s, double x, double y, double w, int style) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWSTRING, new Object[]{s,new Double(x),new Double(y), new Double(w), new Integer(style)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWSTRING, new Object[]{s,new Double(x),new Double(y), new Double(w), new Integer(style)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws a 3-D highlighted outline of the specified rectangle.
     * The edges of the rectangle are highlighted so that they
     * appear to be beveled and lit from the upper left corner.
     * <p>
     * The colors used for the highlighting effect are determined
     * based on the current color.
     * The resulting rectangle covers an area that is
     * <code>width&nbsp;+&nbsp;1</code> inches wide
     * by <code>height&nbsp;+&nbsp;1</code> inches tall.
     * @param       x the <i>x</i> coordinate of the rectangle to be drawn.
     * @param       y the <i>y</i> coordinate of the rectangle to be drawn.
     * @param       width the width of the rectangle to be drawn.
     * @param       height the height of the rectangle to be drawn.
     * @param       raised a boolean that determines whether the rectangle
     *                      appears to be raised above the surface
     *                      or sunk into the surface.
     */
    public void draw3DRect(double x, double y, double width, double height, boolean raised) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAW3DRECT, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height),new Boolean(raised)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAW3DRECT, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height),new Boolean(raised)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws the outline of a circular or elliptical arc
     * covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends
     * for <code>arcAngle</code> degrees, using the current color.
     * Angles are interpreted such that 0&nbsp;degrees
     * is at the 3&nbsp;o'clock position.
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin
     * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the
     * <code>width</code> and <code>height</code> arguments.
     * <p>
     * The resulting arc covers an area
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * <p>
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds.
     * @param        x the <i>x</i> coordinate of the
     *                    upper-left corner of the arc to be drawn.
     * @param        y the <i>y</i>  coordinate of the
     *                    upper-left corner of the arc to be drawn.
     * @param        width the width of the arc to be drawn.
     * @param        height the height of the arc to be drawn.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc,
     *                    relative to the start angle.
     */
    public void drawArc(double x, double y, double width, double height, int startAngle, int arcAngle) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWARC, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height),new Integer(startAngle),new Integer(arcAngle)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWARC, new Object[]{new Double(x),new Double(y),new Double(width),new Double(height),new Integer(startAngle),new Integer(arcAngle)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside the specified rectangle of this
     * graphics context's coordinate space, and is scaled if
     * necessary. Transparent pixels do not affect whatever pixels
     * are already there.
     * <p>
     * This method will ensure the completion of drawing.
     * @param    buf    the specified image to be drawn.
     * @param    x      the <i>x</i> coordinate in inches.
     * @param    y      the <i>y</i> coordinate in inches.
     * @param    width  the width of the rectangle in inches.
     * @param    height the height of the rectangle in inches.
     */
    public void drawImage(byte[] buf, double x, double y, double width, double height) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWIMAGE, new Object[]{buf, new Double(x),new Double(y),new Double(width),new Double(height)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWIMAGE, new Object[]{buf, new Double(x),new Double(y),new Double(width),new Double(height)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws an image stored in a file.
     * <p>
     * The image is drawn inside the specified rectangle of this
     * graphics context's coordinate space, and is scaled if
     * necessary. Transparent pixels do not affect whatever pixels
     * are already there.
     * <p>
     * This method will ensure the completion of drawing.
     * @param    file   the file stores the image to be drawn.
     * @param    x      the <i>x</i> coordinate in inches.
     * @param    y      the <i>y</i> coordinate in inches.
     * @param    width  the width of the rectangle in inches.
     * @param    height the height of the rectangle in inches.
     */
    public void drawImage(File file, double x, double y, double width, double height) {
        try {
            byte[] myPic = new byte[(int)file.length()];
            DataInputStream fin = new DataInputStream(new FileInputStream(file));
            fin.readFully(myPic);
            fin.close();
            drawImage(myPic, x, y, width, height);
        } catch(Exception err) {
        }
    }
    /**
     * Draws the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside a paragraph that have enough space for the image.
     * <p>
     * This method will ensure the completion of drawing.
     * @param    buf    the specified image to be drawn.
     * @param    cap    a string describes the image.
     * @param    width  the width of the rectangle in inches.
     * @param    height the height of the rectangle in inches.
     * @param    align  alignment position.
     */
    public void drawImageInParagraph(byte[] buf, String cap, double width, double height, int align) {
        drawImageInParagraph(buf, cap, 0, 0, width, height, align, true);
    }
    /**
     * Draws the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside a paragraph that have enough space for the image.
     * <p>
     * This method will ensure the completion of drawing.
     * @param    buf    the specified image to be drawn.
     * @param    cap    a string describes the image.
     * @param    x      offset from paragraph left or right boundary
     * @param    y      offset from current cursor in paragraph.
     * @param    width  the width of the rectangle in inches.
     * @param    height the height of the rectangle in inches.
     * @param    align  alignment position.
     * @param    forward  forward cursor's y position
     */
    public void drawImageInParagraph(byte[] buf, String cap, double x, double y, double width, double height, int align, boolean forward) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWIMAGEINPARAGRAPH, new Object[]{buf, cap, new Double(x), new Double(y), new Double(width), new Double(height), new Integer(align), new Boolean(forward)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWIMAGEINPARAGRAPH, new Object[]{buf, cap, new Double(x), new Double(y), new Double(width), new Double(height), new Integer(align), new Boolean(forward)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside a paragraph that have enough space for the image.
     * <p>
     * This method will ensure the completion of drawing.
     * @param    file   read Image from this file.
     * @param    cap    a string describes the image.
     * @param    width  the width of the rectangle in inches.
     * @param    height the height of the rectangle in inches.
     * @param    align  alignment position.
     */
    public void drawImageInParagraph(File file, String cap, double width, double height, int align) {
        try {
            byte[] myPic = new byte[(int)file.length()];
            DataInputStream fin = new DataInputStream(new FileInputStream(file));
            fin.readFully(myPic);
            fin.close();
            drawImageInParagraph(myPic, cap, width, height, align);
        } catch(Exception err) {
        }
    }
    /**
     * Draws the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside a paragraph that have enough space for the image.
     * <p>
     * This method will ensure the completion of drawing.
     * @param    file   read Image from this file.
     * @param    cap    a string describes the image.
     * @param    x      offset from left or right paragraph boundary
     * @param    y      offset from current cursor in paragraph.
     * @param    width  the width of the rectangle in inches.
     * @param    height the height of the rectangle in inches.
     * @param    align  alignment position.
     * @param    forward  forward cursor's y position.
     */
    public void drawImageInParagraph(File file, String cap, double x, double y, double width, double height, int align, boolean forward) {
        try {
            byte[] myPic = new byte[(int)file.length()];
            DataInputStream fin = new DataInputStream(new FileInputStream(file));
            fin.readFully(myPic);
            fin.close();
            drawImageInParagraph(myPic, cap, x, y, width, height, align, forward);
        } catch(Exception err) {
        }
    }

    /**
     * Draws a line, using the current color, between the points
     * <code>(x1,&nbsp;y1)</code> and <code>(x2,&nbsp;y2)</code>
     * in inches.
     * @param   x1  the first point's <i>x</i> coordinate in inches.
     * @param   y1  the first point's <i>y</i> coordinate in inches.
     * @param   x2  the second point's <i>x</i> coordinate in inches.
     * @param   y2  the second point's <i>y</i> coordinate in inches.
     */
    public void drawLine(double x1, double y1, double x2, double y2) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWLINE, new Object[]{new Double(x1),new Double(y1),new Double(x2), new Double(y2)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWLINE, new Object[]{new Double(x1),new Double(y1),new Double(x2), new Double(y2)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws the outline of an oval.
     * The result is a circle or ellipse that fits within the
     * rectangle specified by the <code>x</code>, <code>y</code>,
     * <code>width</code>, and <code>height</code> arguments.
     * @param       x the <i>x</i> coordinate of the upper left
     *                     corner of the oval to be drawn in inches.
     * @param       y the <i>y</i> coordinate of the upper left
     *                     corner of the oval to be drawn in inches.
     * @param       width the width of the oval to be drawn in inches.
     * @param       height the height of the oval to be drawn in inches.
     */
    public void drawOval(double x, double y, double width, double height) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWOVAL, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWOVAL, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws a closed polygon defined by
     * arrays of <i>x</i> and <i>y</i> coordinates.
     * Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code>
     * line segments are line segments from
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code>
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for
     * 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * @param        xPoints   a an array of <code>x</code> coordinates in inches.
     * @param        yPoints   a an array of <code>y</code> coordinates in inches.
     * @param        nPoints   a the total number of points.
     */
    public void drawPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWPOLYGON, new Object[]{xPoints,yPoints,new Integer(nPoints)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWPOLYGON, new Object[]{xPoints,yPoints,new Integer(nPoints)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws a sequence of connected lines defined by
     * arrays of <i>x</i> and <i>y</i> coordinates.
     * Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
     * The figure is not closed if the first point
     * differs from the last point.
     * @param       xPoints an array of <i>x</i> points in inches.
     * @param       yPoints an array of <i>y</i> points in inches.
     * @param       nPoints the total number of points
     */
    public void drawPolyline(double[] xPoints, double[] yPoints, int nPoints) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWPOLYLINE, new Object[]{xPoints,yPoints,new Integer(nPoints)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWPOLYLINE, new Object[]{xPoints,yPoints,new Integer(nPoints)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws the outline of the specified rectangle.
     * The left and right edges of the rectangle are at
     * <code>x</code> and <code>x&nbsp;+&nbsp;width</code>.
     * The top and bottom edges are at
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>.
     * The rectangle is drawn using the graphics context's current color.
     * @param         x   the <i>x</i> coordinate
     *                         of the rectangle to be drawn in inches.
     * @param         y   the <i>y</i> coordinate
     *                         of the rectangle to be drawn in inches.
     * @param         width   the width of the rectangle to be drawn in inches.
     * @param         height   the height of the rectangle to be drawn in inches.
     */
    public void drawRect(double x, double y, double width, double height) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws an outlined round-cornered rectangle using this graphics
     * context's current color. The left and right edges of the rectangle
     * are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>,
     * respectively. The top and bottom edges of the rectangle are at
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>.
     * @param      x the <i>x</i> coordinate of the rectangle to be drawn in inches.
     * @param      y the <i>y</i> coordinate of the rectangle to be drawn in inches.
     * @param      width the width of the rectangle to be drawn in inches.
     * @param      height the height of the rectangle to be drawn in inches.
     * @param      arcWidth the horizontal diameter of the arc
     *                    at the four corners in inches.
     * @param      arcHeight the vertical diameter of the arc
     *                    at the four corners in inches.
     */
    public void drawRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWROUNDRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height), new Double(arcWidth), new Double(arcHeight)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWROUNDRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height), new Double(arcWidth), new Double(arcHeight)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Paints a 3-D highlighted rectangle filled with the current color.
     * The edges of the rectangle will be highlighted so that it appears
     * as if the edges were beveled and lit from the upper left corner.
     * The colors used for the highlighting effect will be determined from
     * the current color.
     * @param       x the <i>x</i> coordinate of the rectangle to be filled in inches.
     * @param       y the <i>y</i> coordinate of the rectangle to be filled in inches.
     * @param       width the width of the rectangle to be filled in inches.
     * @param       height the height of the rectangle to be filled in inches.
     * @param       raised a boolean value that determines whether the
     *                      rectangle appears to be raised above the surface
     *                      or etched into the surface.
     */
    public void fill3DRect(double x, double y, double width, double height, boolean raised) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.FILL3DRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height), new Boolean(raised)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.FILL3DRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height), new Boolean(raised)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Fills a circular or elliptical arc covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends
     * for <code>arcAngle</code> degrees.
     * Angles are interpreted such that 0&nbsp;degrees
     * is at the 3&nbsp;o'clock position.
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin
     * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the
     * <code>width</code> and <code>height</code> arguments.
     * <p>
     * The resulting arc covers an area
     * <code>width&nbsp;+&nbsp;1</code> inches wide
     * by <code>height&nbsp;+&nbsp;1</code> inches tall.
     * <p>
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds.
     * @param        x the <i>x</i> coordinate of the
     *                    upper-left corner of the arc to be filled in inches.
     * @param        y the <i>y</i>  coordinate of the
     *                    upper-left corner of the arc to be filled in inches.
     * @param        width the width of the arc to be filled in inches.
     * @param        height the height of the arc to be filled in inches.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc,
     *                    relative to the start angle.
     */
    public void fillArc(double x, double y, double width, double height, int startAngle, int arcAngle) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.FILLARC, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height), new Integer(startAngle), new Integer(arcAngle)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.FILLARC, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height), new Integer(startAngle), new Integer(arcAngle)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Fills an oval bounded by the specified rectangle with the
     * current color.
     * @param       x the <i>x</i> coordinate of the upper left corner
     *                     of the oval to be filled in inches.
     * @param       y the <i>y</i> coordinate of the upper left corner
     *                     of the oval to be filled in inches.
     * @param       width the width of the oval to be filled in inches.
     * @param       height the height of the oval to be filled in inches.
     */
    public void fillOval(double x, double y, double width, double height) {
        if (!inProcess)
           commands.add(new PrintCommand(PrintCommand.FILLOVAL, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.FILLOVAL, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Fills a closed polygon defined by
     * arrays of <i>x</i> and <i>y</i> coordinates.
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code>
     * line segments are line segments from
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code>
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for
     * 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * <p>
     * The area inside the polygon is defined using an
     * even-odd fill rule, also known as the alternating rule.
     * @param        xPoints   a an array of <code>x</code> coordinates in inches.
     * @param        yPoints   a an array of <code>y</code> coordinates in inches.
     * @param        nPoints   a the total number of points.
     */
    public void fillPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.FILLPOLYGON, new Object[]{xPoints,yPoints,new Integer(nPoints)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.FILLPOLYGON, new Object[]{xPoints,yPoints,new Integer(nPoints)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Fills the specified rectangle.
     * The left and right edges of the rectangle are at
     * <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>.
     * The top and bottom edges are at
     * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>.
     * The resulting rectangle covers an area
     * <code>width</code> inches wide by
     * <code>height</code> inches tall.
     * The rectangle is filled using the graphics context's current color.
     * @param         x   the <i>x</i> coordinate
     *                         of the rectangle to be filled in inches.
     * @param         y   the <i>y</i> coordinate
     *                         of the rectangle to be filled in inches.
     * @param         width   the width of the rectangle to be filled in inches.
     * @param         height   the height of the rectangle to be filled in inches.
     */
    public void fillRect(double x, double y, double width, double height) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.FILLRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.FILLRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Draws an outlined round-cornered rectangle using this graphics
     * context's current color. The left and right edges of the rectangle
     * are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>,
     * respectively. The top and bottom edges of the rectangle are at
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>.
     * @param      x the <i>x</i> coordinate of the rectangle to be drawn in inches.
     * @param      y the <i>y</i> coordinate of the rectangle to be drawn.
     * @param      width the width of the rectangle to be drawn in inches.
     * @param      height the height of the rectangle to be drawn in inches.
     * @param      arcWidth the horizontal diameter of the arc
     *                    at the four corners in inches.
     * @param      arcHeight the vertical diameter of the arc
     *                    at the four corners in inches.
     */
    public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.FILLROUNDRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height), new Double(arcWidth), new Double(arcHeight)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.FILLROUNDRECT, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height), new Double(arcWidth), new Double(arcHeight)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Sets the current clip to the rectangle specified by the given
     * coordinates.  This method sets the user clip, which is
     * independent of the clipping associated with device bounds
     * and window visibility.
     * Rendering operations have no effect outside of the clipping area.
     * @param       x the <i>x</i> coordinate of the new clip rectangle in inches.
     * @param       y the <i>y</i> coordinate of the new clip rectangle in inches.
     * @param       width the width of the new clip rectangle in inches.
     * @param       height the height of the new clip rectangle in inches.
     */
    public void setClip(double x, double y, double width, double height) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETCLIP, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETCLIP, new Object[]{new Double(x),new Double(y),new Double(width), new Double(height)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Sets this graphics context's current color to the specified
     * color. All subsequent graphics operations using this graphics
     * context use this specified color.
     * @param aColor   the new rendering color.
     */
    public void setColor(Color aColor) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETCOLOR, new Object[]{aColor},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETCOLOR, new Object[]{aColor},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Sets this graphics context's font to the specified font.
     * All subsequent text operations using this graphics context
     * use this font.
     * @param fname  the font name.
     * @param style  the style constant for the Font. The style argument is an integer bitmask that may be PLAIN, or a bitwise union of BOLD and/or ITALIC (for example, ITALIC or BOLD|ITALIC). If the style argument does not conform to one of the expected integer bitmasks then the style is set to PLAIN.
     * @param size   the size of the Font in inches
     */
    public void setFont(String fname, int style, double size) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETFONT, new Object[]{fname,new Integer(style),new Double(size)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETFONT, new Object[]{fname,new Integer(style),new Double(size)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Sets the paint mode of this graphics context to overwrite the
     * destination with this graphics context's current color.
     * This sets the logical pixel operation function to the paint or
     * overwrite mode.  All subsequent rendering operations will
     * overwrite the destination with the current color.
     */
    public void setPaintMode() {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETPAINTMODE, new Object[]{},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETPAINTMODE, new Object[]{},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Sets the paint mode of this graphics context to alternate between
     * this graphics context's current color and the new specified color.
     * This specifies that logical pixel operations are performed in the
     * XOR mode, which alternates pixels between the current color and
     * a specified XOR color.
     * <p>
     * When drawing operations are performed, pixels which are the
     * current color are changed to the specified color, and vice versa.
     * <p>
     * Pixels that are of colors other than those two colors are changed
     * in an unpredictable but reversible manner; if the same figure is
     * drawn twice, then all pixels are restored to their original values.
     * @param  aColor the XOR alternation color
     */
    public void setXORMode(Color aColor) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETXORMODE, new Object[]{aColor},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETXORMODE, new Object[]{aColor},inProcess),commandAt+batchSize);
        }
    }
    // EasyPrint specific methods
    /**
     * Set paper size.
     * @param media the paper size like "A4", "B4".
     */
    public void setMedia(String media) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETMEDIA, new Object[]{media},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETMEDIA, new Object[]{media},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Set page orientation
     * @param ori the page orientation, either "landscape" or "portrait".
     */
    public void setOri(String ori) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETORI, new Object[]{ori},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETORI, new Object[]{ori},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Specifies the destination printer for jobs
     * @param printer the destination
     */
    public void setPrinter(String printer) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETPRINTER, new Object[]{printer},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETPRINTER, new Object[]{printer},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Specifies the number of copies the application should render for jobs
     * @param c the number of copies
     */
    public void setCopy(int c) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETCOPY, new Object[]{new Integer(c)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETCOPY, new Object[]{new Integer(c)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Set header for each page. The "#page" in display string will be replaced with current page number,
     * and "#total" will be replaced with total numbers. The header will be printed in the middle up of the page.
     * The default header is null.
     * @param display the string to print
     * @param fontName font name
     * @param fontStyle the style constant for the Font. The style argument is an integer bitmask that may be PLAIN, or a bitwise union of BOLD and/or ITALIC (for example, ITALIC or BOLD|ITALIC). If the style argument does not conform to one of the expected integer bitmasks then the style is set to PLAIN.
     * @param fontSize font size in inches.
     */
    public void setHeader(String display, String fontName, int fontStyle, double fontSize) {
        double[] d = getSize();
        double x = d[0]/2;
        double y = 0.5;
        setHeader(display, fontName, fontStyle, fontSize, x, y, 0, CENTER);
    }
    /**
     * Set header for each page. The "#page" in display string will be replaced with current page number,
     * and "#total" will be replaced with number of total pages. The header will be printed in the middle upper of the page.
     * @param display the string to print
     * @param fontName font name
     * @param fontStyle the style constant for the Font. The style argument is an integer bitmask that may be PLAIN, or a bitwise union of BOLD and/or ITALIC (for example, ITALIC or BOLD|ITALIC). If the style argument does not conform to one of the expected integer bitmasks then the style is set to PLAIN.
     * @param fontSize font size in inches.
     * @param x the <i>x</i> coordinate to draw.
     * @param y the <i>y</i> coordinate to draw.
     * @param w the width(height) of the head to draw.
     * @param align alignment of the head.
     * @see         EasyPrint#LEFT
     * @see         EasyPrint#RIGHT
     * @see         EasyPrint#BOTH
     * @see         EasyPrint#CENTER
     * @see         EasyPrint#SPREAD
     * @see         EasyPrint#VERTICAL
     * @see         EasyPrint#VBOTH
     * @see         EasyPrint#VSPREAD
     * @see         EasyPrint#VCENTER
     */
    public void setHeader(String display, String fontName, int fontStyle, double fontSize, double x, double y, double w, int align) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETHEADER, new Object[]{display, fontName, new Integer(fontStyle), new Double(fontSize), new Double(x), new Double(y), new Double(w), new Integer(align)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETHEADER, new Object[]{display, fontName, new Integer(fontStyle), new Double(fontSize), new Double(x), new Double(y), new Double(w), new Integer(align)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Set footer for each page. The "#page" in display string will be replaced with current page number,
     * and "#total" will be replaced with total numbers. The footer will be printed in the middle lower of the page.
     * The default header is "#page / #total"
     * @param display the string to print
     * @param fontName font name
     * @param fontStyle the style constant for the Font. The style argument is an integer bitmask that may be PLAIN, or a bitwise union of BOLD and/or ITALIC (for example, ITALIC or BOLD|ITALIC). If the style argument does not conform to one of the expected integer bitmasks then the style is set to PLAIN.
     * @param fontSize font size in inches.
     */
    public void setFooter(String display, String fontName, int fontStyle, double fontSize) {
        double[] d = getSize();
        double x = d[0]/2;
        double y = d[1] - 0.5;
        setFooter(display, fontName, fontStyle, fontSize, x, y, 0, CENTER);
    }
    /**
     * Set footer for each page. The "#page" in display string will be replaced with current page number,
     * and "#total" will be replaced with number of total pages. The header will be printed in the middle upper of the page.
     * @param display the string to print
     * @param fontName font name
     * @param fontStyle the style constant for the Font. The style argument is an integer bitmask that may be PLAIN, or a bitwise union of BOLD and/or ITALIC (for example, ITALIC or BOLD|ITALIC). If the style argument does not conform to one of the expected integer bitmasks then the style is set to PLAIN.
     * @param fontSize font size in inches.
     * @param x the <i>x</i> coordinate to draw.
     * @param y the <i>y</i> coordinate to draw.
     * @param w the width(height) of the head to draw.
     * @param align alignment of the head.
     * @see         EasyPrint#LEFT
     * @see         EasyPrint#RIGHT
     * @see         EasyPrint#BOTH
     * @see         EasyPrint#CENTER
     * @see         EasyPrint#SPREAD
     * @see         EasyPrint#VERTICAL
     * @see         EasyPrint#VBOTH
     * @see         EasyPrint#VSPREAD
     * @see         EasyPrint#VCENTER
     */
    public void setFooter(String display, String fontName, int fontStyle, double fontSize, double x, double y, double w, int align) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETFOOTER, new Object[]{display, fontName, new Integer(fontStyle), new Double(fontSize), new Double(x), new Double(y), new Double(w), new Integer(align)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETFOOTER, new Object[]{display, fontName, new Integer(fontStyle), new Double(fontSize), new Double(x), new Double(y), new Double(w), new Integer(align)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Close current page and start next.
     */
    public void newPage() {
        // If PageListener call this method, infinite recursion will occur. So ignore it
        if (inProcess) return;
        commands.add(new PrintCommand(PrintCommand.NEWPAGE, new Object[]{},inProcess));
    }
    /**
     * Set Line Width. Default is 1.1
     * @param width The line width, default is 1.1
     */
    public void setLineWidth(double width) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETLINEWIDTH, new Object[]{new Double(width)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETLINEWIDTH, new Object[]{new Double(width)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * @param id Table's id
     * @param caption Table's caption
     * @param startx left margin relative to current column
     * @param column Titles for each column. Set this to null for a customized header.
     * @param width Widths for each column in inches.
     * @param wrap wrap mode for each columm, true to wrap false to truncate.
     * @param align Align style for each column.
     * @see         EasyPrint#LEFT
     * @see         EasyPrint#RIGHT
     * @see         EasyPrint#BOTH
     * @see         EasyPrint#CENTER
     * @see         EasyPrint#SPREAD
     */
    public void addTable(String id, String caption, double startx, String[] column, double[] width, boolean[] wrap, int[] align) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.ADDTABLE, new Object[]{id, caption, new Double(startx), column, width,wrap, align},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.ADDTABLE, new Object[]{id, caption, new Double(startx), column, width,wrap, align},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Create a Table whose id is "defaultTable"
     * @param startx left margin relative to current column
     * @param setHeader ex: "column1|column2|colum3|column4"
     * @param setWidth ex: "^1.0|<0.7|>0.8~|2.0", ^ for CENTER, < for LEFT, > for RIGHT, ~ for no wrap
     */
    public void addTable(double startx, String setHeader, String setWidth) {
        addTable("defaultTable", startx, setHeader, setWidth);
    }
    /**
     * @param id Table's id
     * @param startx left margin relative to current column
     * @param setHeader ex: "column1|column2|colum3|column4"
     * @param setWidth ex: "^1.0|<0.7|>0.8~|2.0", ^ for CENTER, < for LEFT, > for RIGHT, ~ for no wrap
     */
    public void addTable(String id, double startx, String setHeader, String setWidth) {
        StringTokenizer st = new StringTokenizer(setHeader, "|");
        Vector<String> head = new Vector<String>();
        while (st.hasMoreTokens()) {
            head.add(st.nextToken());
        }
        String[] column = new String[head.size()];
        for (int i = 0; i < head.size(); i++)
            column[i] = head.get(i);
        st = new StringTokenizer(setWidth,"|");
        Vector<String> setting = new Vector<String>();
        while (st.hasMoreTokens()) {
            setting.add(st.nextToken());
        }
        double[] width = new double[setting.size()];
        boolean[] wrap = new boolean[setting.size()];
        int[] align = new int[setting.size()];
        for (int i = 0; i < setting.size(); i++) {
            String s = setting.get(i);
            align[i] = LEFT;
            wrap[i] = true;
            if (s.length() == 0)
                continue;
            char lead = s.charAt(0);
            if (lead == '^' || lead == '<' || lead == '>') {
                if (lead == '^')
                    align[i] = CENTER;
                else if (lead == '<')
                    align[i] = LEFT;
                else if (lead == '>')
                    align[i] = RIGHT;
                s = s.substring(1, s.length());
            }
            if (s.length() == 0) continue;
            char tail = s.charAt(s.length()-1);
            if (tail == '~' || tail == ';') {
                if (tail == '~')
                    wrap[i] = false;
                s = s.substring(0, s.length() - 1);
            }
            if (s.length() == 0) continue;
            width[i] = Double.parseDouble(s);
        }
        addTable(id, "", startx, column, width, wrap, align);
    }
    /**
     * Close table and let drawParagraph to start after this table's last row.
     * @param id Table's id
     */
    public void closeTable(String id) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.CLOSETABLE, new Object[]{id},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.CLOSETABLE, new Object[]{id},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Close defaultTable and let drawParagraph to start after this table's last row.
     */
    public void closeTable() {
        closeTable("defaultTable");
    }
    /**
     * Add a cell that compose a customized table head.
     * @param id Table's id
     * @param title string to display in the cell
     * @param x row coordiate from up left corner for this cell.
     * @param y column coordiate from up left corner for this cell.
     * @param w how may columns the cell will occupy.
     * @param h how may rows the cell will occupy.
     * @param align an integer specify the alignmemt mode
     * @see         EasyPrint#LEFT
     * @see         EasyPrint#RIGHT
     * @see         EasyPrint#BOTH
     * @see         EasyPrint#CENTER
     * @see         EasyPrint#SPREAD
     */
    public void addTableHeadCell(String id, String title, int x, int y, int w, int h, int align) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.ADDTABLEHEADCELL, new Object[]{id, title, new Integer(x),new Integer(y),new Integer(w),new Integer(h),new Integer(align)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.ADDTABLEHEADCELL, new Object[]{id, title, new Integer(x),new Integer(y),new Integer(w),new Integer(h),new Integer(align)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Add a cell that compose a customized row.
     * @param id Table's id
     * @param title string to display in the cell
     * @param x row coordiate from up left corner for this cell.
     * @param y column coordiate from up left corner for this cell.
     * @param w how may columns the cell will occupy.
     * @param h how may rows the cell will occupy.
     * @param align an integer specify the alignmemt mode
     * @see         EasyPrint#LEFT
     * @see         EasyPrint#RIGHT
     * @see         EasyPrint#BOTH
     * @see         EasyPrint#CENTER
     * @see         EasyPrint#SPREAD
     */
    public void addRowCell(String id, String title, int x, int y, int w, int h, int align) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.ADDROWCELL, new Object[]{id, title, new Integer(x),new Integer(y),new Integer(w),new Integer(h),new Integer(align)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.ADDROWCELL, new Object[]{id, title, new Integer(x),new Integer(y),new Integer(w),new Integer(h),new Integer(align)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Close a row and draw all queued cells on output device.
     * @param id Table's id
     */
    public void closeRow(String id) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.CLOSEROW, new Object[]{id},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.CLOSEROW, new Object[]{id},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Indicate a table's border should be drawn or not.
     * @param id Table's id
     * @param on true to print border, false not to print
     */
    public void setTableBorder(String id, boolean on) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETTABLEBORDER, new Object[]{id, new Boolean(on)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETTABLEBORDER, new Object[]{id, new Boolean(on)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Set columns for the current page.
     * @param p array of (x,y,width,height) indicates each column's (x,y) and (width, height) in inches.
     */
    public void setPageColumn(double[][] p) { // array of (x,y,width,height)
        Double[][] tmp = new Double[p.length][6];
        for (int i = 0; i < p.length; i++) {
            tmp[i][0] = new Double(p[i][0]); // startx
            tmp[i][1] = new Double(p[i][1]); // starty
            tmp[i][2] = new Double(p[i][2]); // width
            tmp[i][3] = new Double(p[i][3]); // height
            tmp[i][4] = new Double(p[i][0]); // current x
            tmp[i][5] = new Double(p[i][1]); // current y
        }
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.SETCOLUMN, new Object[]{tmp},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.SETCOLUMN, new Object[]{tmp},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Print a string at the first available column and forward cursor to proper position. If no column is available in current page, a new page will be created.
     * @param s string to be printed
     */
    public void drawParagraph(String s, int style) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.DRAWPARAGRAPH, new Object[]{s,new Integer(style)},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.DRAWPARAGRAPH, new Object[]{s,new Integer(style)},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Print a string at the first available column and forward cursor to proper position. If no column is available in current page, a new page will be created.
     * @param s string to be printed
     */
    public void drawParagraph(String s) {
        drawParagraph(s, BOTH);
    }
    /**
     * Print a row in a table
     * @param id Table's id
     * @param data row data. If data.length > columns of the table, then ArrayOutOfBoundException will be thrown.
     */
    public void addRow(String id, String[] data) {
        if (!inProcess)
            commands.add(new PrintCommand(PrintCommand.ADDROW, new Object[]{id,data},inProcess));
        else {
            batchSize++;
            commands.insertElementAt(new PrintCommand(PrintCommand.ADDROW, new Object[]{id,data},inProcess),commandAt+batchSize);
        }
    }
    /**
     * Print a row in a table whose id is "defaultTable"
     * @param data row data. Default column seperator is | , row seperator is ; or new line
     */
    public void addRow(String data) {
        addRow("defaultTable", data);
    }
    /**
     * Print a row in a table
     * @param id Table's id
     * @param data row data. Default column seperator is | , row seperator is ; or new line
     */
    public void addRow(String id, String data) {
        StringTokenizer st = new StringTokenizer(data, ";\n");
        while (st.hasMoreTokens()) {
            StringTokenizer line = new StringTokenizer(st.nextToken(), "|");
            Vector<String> tt = new Vector<String>();
            while (line.hasMoreTokens()) {
                tt.add(line.nextToken());
            }
            String[] tmp = new String[tt.size()];
            tt.copyInto(tmp);
            if (!inProcess) {
                commands.add(new PrintCommand(PrintCommand.ADDROW, new Object[]{id,tmp},inProcess));
            } else {
                batchSize++;
                commands.insertElementAt(new PrintCommand(PrintCommand.ADDROW, new Object[]{id,data},inProcess),commandAt+batchSize);
            }
        }
    }
    /**
     * View pages on screen.
     */
    public void preview() {
        currentPage = 1;
        g = null;
        mode = COUNTPAGE;
        processCommand();
        totalPages = currentPage;
        currentPage = 1;
        mode = PREVIEW;
        processCommand();
        pc.repaint();
    }
    /**
     * Zoom in pages on screen.
     * @param dots The dots per inches. Default is 72. You may set a larger number for dots to have a detailed view.
     */
    public void zoom(int dots) {
        this.dots = dots;
        pages.setSize(0);
        System.gc();
        currentPage = 1;
        g = null;
        mode = PREVIEW;
        processCommand();
        totalPages = currentPage;
        pc.repaint();
    }
    /**
     * Show a Print Job dialog to print a page.
     */
    public void print(int pageNum) {
        print(new int[] {pageNum});
    }
    /**
     * Show a Print Job dialog to print a list of pages.
     */
    public void print(int[] pageNum) {
        dots = 72;
        currentPage = 1;
        g = null;
        mode = COUNTPAGE;
        processCommand();
        totalPages = currentPage;
        forbid = new boolean[totalPages+1];
        for (int i = 0; i < forbid.length; i++)
            forbid[i] = true;
        for (int i = 0; i < pageNum.length; i++)
            if (pageNum[i] >= 0 && pageNum[i] < totalPages)
                forbid[pageNum[i]] = false;
        mode = PRINT;
        currentPage = 1;
        g = null;
        processCommand();
        if (pjob != null) {
            if (g != null)
                g.dispose();
            pjob.end();
            pjob = null;
        }
        forbid = null;
    }
    public void print(boolean[] f) {
        dots = 72;
        currentPage = 1;
        g = null;
        mode = COUNTPAGE;
        processCommand();
        totalPages = currentPage;
        currentPage = 1;
        g = null;
        mode = PRINT;
        forbid = f;
        processCommand();
        if (pjob != null) {
            if (g != null)
                g.dispose();
            pjob.end();
            pjob = null;
        }
        forbid = null;
    }
    /**
     * Show a Print Job dialog for print.
     */
    public void print() {
        print(false);
    }
    /**
     * Try to print out pages.
     * @param background false to invoke a print job dialog, true for background mode print.
     */
    public void print(boolean background) {
        this.background = background;
        dots = 72;
        currentPage = 1;
        g = null;
        mode = COUNTPAGE;
        processCommand();
        totalPages = currentPage;
        currentPage = 1;
        g = null;
        mode = PRINT;
        processCommand();
        if (pjob != null) {
            if (g != null)
                g.dispose();
            pjob.end();
            pjob = null;
        }
    }
    /**
     * Try to save commands to a File.
     * @param f destination File to save commands
     * @see EasyPrint#load
     */
    public void save(File f) {
        try {
            save(new ObjectOutputStream(new FileOutputStream(f)));
        } catch(Exception err) {
        }
    }
    /**
     * Try to save commands to an ObjectOutputStream.
     * @param oos destination for commands
     * @see EasyPrint#load
     */
    public void save(ObjectOutputStream oos) {
        try {
            mode = COUNTPAGE;
            processCommand();
            oos.writeObject(commands);
        } catch(Exception err) {
        }
    }
    /**
     * Load commands from an ObjectInputStream.
     * @param ois source of commmands
     */
    public void load(ObjectInputStream ois) {
        try {
            commands = (Vector<PrintCommand>)(ois.readObject());
            loaded = true;
        } catch(Exception err) {
        }
    }
    /**
     * Load commands from a File.
     * @param f source of commmands
     */
    public void load(File f) {
        try {
            load(new ObjectInputStream(new FileInputStream(f)));
        } catch(Exception err) {
        }
    }
    /**
     * Draw grid for each page. This feature maybe usefull in development.
     * @param on true to draw grid, false not to draw.
     */
    public void setGridOn(boolean on) {
        gridOn = on;
    }
    /**
     * Set the row gap in a table. A row's height will be lineWith + rowGap
     * @param tid Table's id
     * @param gap true to draw grid, false not to draw. Default is 0.4
     */
    public void setRowGap(String tid, double gap) {
        if (!inProcess)
            commandAt = commands.size()-1;
        commands.insertElementAt(new PrintCommand(PrintCommand.SETROWGAP, new Object[]{tid, new Double(gap)},inProcess),commandAt+1);
    }
    public void draw(String s, int x, int y, int w, int style) {
        if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) return;
        if (s == null) return;
        while (s.endsWith("\n") || s.endsWith("\t") || s.endsWith(" ")) {
            s = s.substring(0,s.length()-1);
        }
        if (s.length() == 0)
            return;
        FontMetrics f = g.getFontMetrics();
        char[] chars = s.toCharArray();
        int start = 0;
        int gap, totalHeight = 0, fontHeight, ascent;
        int totalPix = 0;
        for (int k = 0; k < chars.length; k++) {
            totalPix += f.charWidth(chars[k]);
        }
        fontHeight = f.getHeight();
        totalHeight = fontHeight*chars.length;
        ascent = f.getAscent();
        switch(style) {
        case VERTICAL:
            for (int i = 0; i < chars.length; i++) {
                g.drawChars(chars, i, 1, x, y+ascent);
                y += fontHeight;
            }
            break;
        case VSPREAD:
            if (chars.length>0) {
                double gg = (double)(w-totalHeight)/(chars.length);
                double at = y + gg/2;
                for (int i = 0; i < chars.length; i++) {
                    g.drawChars(chars, i, 1, x, (int)at+ascent);
                    at += fontHeight+gg;
                }
            }
            break;
        case VBOTH:
            if (chars.length>1) {
                double gg = (double)(w-totalHeight)/(chars.length-1);
                double at = y;
                for (int i = 0; i < chars.length; i++) {
                    g.drawChars(chars, i, 1, x, (int)at+ascent);
                    at += fontHeight+gg;
                }
                break;
            }
        case VCENTER:
            y += (w-totalHeight)/2;
            for (int i = 0; i < chars.length; i++) {
                g.drawChars(chars, i, 1, x, y+ascent);
                y += fontHeight;
            }
            break;
        case RIGHT:
            g.drawString(s, x+w-totalPix, y);
            break;
        case SPREAD:
            if (chars.length>0) {
                double gg = (double)(w-totalPix)/(chars.length);
                double at = x + gg/2;
                for (int i = 0; i < chars.length; i++) {
                    g.drawChars(chars, i, 1, (int)at, y);
                    at += f.charWidth(chars[i]) + gg;
                }
            }
            break;
        case BOTH:
            if (chars.length>1) {
                // check if all english and has more than one blank in words
                int k = 0;
                // skip leading blanks
                while (k < chars.length && chars[k]<=' ')
                    k++;
                int blanks = 0;
                boolean allEng = true;
                for (;k < chars.length; k++) {
                    if (chars[k] > 255) {
                        allEng = false;
                        break;
                    }
                    if (chars[k] <= ' ') {
                        blanks++;
                        while (k < chars.length && chars[k] <= ' ')
                            k++;
                    }
                }
                if (allEng == true && blanks > 0) {
                    totalPix = 0;
                    // leading blanks keeps untouched.
                    for (k = 0; k < chars.length && chars[k] <= ' '; k++) {
                        totalPix += f.charWidth(' ');
                    }
                    // calculate total pixals the string will occupied
                    // continuous blanks will be counted one space
                    for (;k < chars.length;) {
                        if (chars[k] <= ' ') {
                            totalPix += f.charWidth(' ');
                            for (k++; k < chars.length && chars[k] <= ' '; k++)
                                ;
                        } else {
                            totalPix += f.charWidth(chars[k]);
                            k++;
                        }
                    }
                    double gg = (double)(w-totalPix)/blanks;
                    double at = x;
                    // keep all leading blanks
                    for (k = 0; k < chars.length && chars[k] <= ' '; k++) {
                        at += f.charWidth(' ');
                    }
                    // print out string
                    for (; k < chars.length;) {
                        if (chars[k] <= ' ') {
                            at += f.charWidth(' ') + gg;
                            for (k++; k < chars.length && chars[k] <= ' '; k++)
                                ;
                        } else {
                            g.drawChars(chars, k, 1, (int)at, y);
                            at += f.charWidth(chars[k]);
                            k++;
                        }
                    }
                } else {
                    double gg = (double)(w-totalPix)/(chars.length-1);
                    double at = x;
                    for (int i = 0; i < chars.length; i++) {
                        g.drawChars(chars, i, 1, (int)(at+0.5), y);
                        at += f.charWidth(chars[i])+gg;
                    }
                }
                break;
            }
        case CENTER:
            g.drawString(s, x+(w-totalPix)/2, y);
            break;
        case LEFT:
        default:
            g.drawString(s, x, y);
            break;
        }
    }
    String[] vsplit(String s, int width) {
        Vector<String> rel = new Vector<String>();
        FontMetrics f = g.getFontMetrics();
        char[] chars = s.toCharArray();
        int start = 0;
        int fontHeight = f.getHeight();
        int totalHeight = 0;
        for (int k = 0; k < chars.length; k++) {
            if (chars[k] == '\n') {
                rel.add(s.substring(start, k));
                start = k + 1;
                totalHeight = 0;
            } else if ((totalHeight += fontHeight) > width) { // one line overflow
                int tail = k - 1;
                while (tail > start && chars[tail] == ' ') tail--;
                rel.add(s.substring(start, tail + 1));
                start = k;
                totalHeight = 0;
            }
        }
        if (start < s.length())
            rel.add(s.substring(start, s.length()));
        String[] tt = new String[rel.size()];
        rel.copyInto(tt);
        return tt;
    }
    public String[] split(String s, int width) {
        Vector<String> rel = new Vector<String>();
        FontMetrics f = g.getFontMetrics();
        char[] chars = s.toCharArray();
        int start = 0;
        int totalPix = 0;
        for (int k = 0; k < chars.length; k++) {
            int perPix = f.charWidth(chars[k]);
            if (chars[k] == '\n') {
                rel.add(s.substring(start, k+1));
                start = k + 1;
                totalPix = 0;
            } else if ((totalPix+perPix) > width) { // one line overflow
                // check the split point
                int i = k; // the first point of next line
                // find the first english letter
                while (i > start && !(chars[i]<=' ' || chars[i]>255))
                    i--;
                if (start == i)
                    i = k - 1;
                int tail = i - 1;
                // delete tailing blanks
                while (tail > start && chars[tail] <= ' ') tail--;
                // delte leading blanks
                while (i < chars.length && chars[i] <= ' ') i++;
                rel.add(s.substring(start, tail + 1));
                start = k = i;
                totalPix = 0;
            } else
                totalPix += perPix;
        }
        if (start < s.length())
            rel.add(s.substring(start, s.length()));
        String[] tt = new String[rel.size()];
        rel.copyInto(tt);
        return tt;
    }
    // split into at most 2 lines. First String fit in widht, remaining chars are stored in second String
    public String[] splitOne(String s, int width) {
        FontMetrics f = g.getFontMetrics();
        char[] chars = s.toCharArray();
        String[] tt = new String[2];
        int start = 0;
        int totalPix = 0;
        for (int k = 0; k < chars.length; k++) {
            int perPix = f.charWidth(chars[k]);
            if (chars[k] == '\n') {
                tt[0] = s.substring(start, k+1);
                tt[1] = s.substring(k+1, s.length());
                return tt;
            } else if ((totalPix+perPix) > width) { // one line overflow
                // check the split point
                int i = k; // the first point of next line
                // find the first english letter
                while (i > start && !(chars[i]<=' ' || chars[i]>255))
                    i--;
                if (start == i)
                    i = k - 1;
                int tail = i - 1;
                while (tail > start && chars[tail] <= ' ') tail--;
                // delte leading blanks
                while (i < chars.length && chars[i] <= ' ') i++;
                tt[0] = s.substring(start, tail + 1);
                tt[1] = s.substring(i, s.length());
                return tt;
            } else
                totalPix += perPix;
        }
        tt[0] = s;
        tt[1] = null;
        return tt;
    }
    public String truncate(String s, int width) {
        StringBuilder sb = new StringBuilder();
        FontMetrics f = g.getFontMetrics();
        char[] chars = s.toCharArray();
        int start = 0;
        int totalWidth = 0;
        for (int k = 0; k < chars.length; k++) {
            if ((totalWidth += f.charWidth(chars[k])) >= width)
                break;
            sb.append(chars[k]);
        }
        return sb.toString();
    }
    public String vtruncate(String s, int width) {
        StringBuilder sb = new StringBuilder();
        FontMetrics f = g.getFontMetrics();
        int fontHeight = f.getHeight();
        char[] chars = s.toCharArray();
        int start = 0;
        int totalHeight = 0;
        for (int k = 0; k < chars.length; k++) {
            if ((totalHeight += fontHeight) >= width)
                break;
            sb.append(chars[k]);
        }
        return sb.toString();
    }
    private Vector<PrintCommand> commands = new Vector<PrintCommand>();
    private Vector<TablePos> tables;
    private Font font = null;
    private int[][] col = null;
    private PrintJob pjob = null;
    private String media = "A4";
    private String ori = "portrait";
    private String printer = "";
    private int copy = 1;
    private int mode = PREVIEW;
    private Graphics g = null;
    public String jobTitle = "Print";
    public int currentPage = 1;
    public int totalPages = 0;
    private PageListener pl;
    private int dots = 72;
    private String footer = "#page / #total";
    private int footerX, footerY, footerW;
    private int footerAlign;
    private Font footerFont;
    private String header = "";
    private int headerX, headerY, headerW;
    private int headerAlign;
    private Font headerFont;
    private boolean inProcess, loaded, background;
    private int commandAt = 0;
    private double lineWidth = 1.1;
    private boolean gridOn;
    private int inCol;
    private int batchSize;
    private boolean[] forbid;
    void processCommand() {
        String pre, tableid;
        int j, curx, cury;
        FontMetrics f;
        double fontHeight, rowHeight;
        TablePos tp;
        int[] xpoints, ypoints;
        double[] xp, yp;
        int ascent;
        inProcess = true;
        // reset all object variables, because processCommand() will be executed more than once.
        tables = new Vector<TablePos>();
        col = null;
        copy = 1;
        font = null;
        // set default column
        Dimension d = getPreferredSize();
        col = new int[1][6];
        // all margins are 0.5 inches
        col[0][0] = col[0][4] = (int)(0.5*dots);
        col[0][1] = col[0][5] = (int)(0.5*dots);
        col[0][2] = d.width - dots;
        col[0][3] = d.height - dots;
        inCol = 0;
        // set default footer
        footer = "#page / #total";
        footerX = d.width/2;
        footerY = d.height - (int)(0.5*dots);
        footerW = 0;
        footerAlign = CENTER;
        // remove commands generated from method pageCreated() in previous stage.
        // This ensures mutiple calls to preview() or print() will work correctly
        if (!loaded) {
            for (int i = 0; i < commands.size(); i++) {
                PrintCommand com = commands.get(i);
                if (com.genByPageEvent) {
                    commands.remove(i);
                    i--;
                }
            }
        }
        // set to -1 to ensure commands created by pageListner in firt page will be processed first.
        commandAt = -1;
        batchSize = 0;
        g = null;
        for (commandAt = 0; commandAt < commands.size(); commandAt++) {
            try {
                batchSize = 0;
                PrintCommand com = commands.get(commandAt);
                switch(com.opcode) {
                case PrintCommand.CLEARRECT:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.clearRect((int)(((Double)(com.param[0])).doubleValue()*dots), (int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots));
                    break;
                case PrintCommand.CLIPRECT:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.clipRect((int)(((Double)(com.param[0])).doubleValue()*dots), (int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots));
                    break;
                case PrintCommand.COPYAREA:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.copyArea((int)(((Double)(com.param[0])).doubleValue()*dots), (int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots),(int)(((Double)(com.param[4])).doubleValue()*dots),(int)(((Double)(com.param[5])).doubleValue()*dots));
                    break;
                case PrintCommand.DRAW3DRECT:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.draw3DRect((int)(((Double)(com.param[0])).doubleValue()*dots), (int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots),((Boolean)(com.param[4])).booleanValue());
                    break;
                case PrintCommand.DRAWARC:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.drawArc((int)(((Double)(com.param[0])).doubleValue()*dots), (int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots),((Integer)(com.param[4])).intValue(),((Integer)(com.param[5])).intValue());
                    break;
                case PrintCommand.DRAWIMAGE:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    Image img = new ReadyImage((byte[])(com.param[0]),(int)(((Double)(com.param[3])).doubleValue()*dots),(int)(((Double)(com.param[4])).doubleValue()*dots)).getImage();
                    g.drawImage(img, (int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),null);
                    break;
                case PrintCommand.DRAWOVAL:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.drawOval((int)(((Double)(com.param[0])).doubleValue()*dots), (int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots));
                    break;
                case PrintCommand.DRAWPOLYGON:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    xp = (double[])(com.param[0]);
                    yp = (double[])(com.param[1]);
                    xpoints = new int[xp.length];
                    ypoints = new int[yp.length];
                    for (int k = 0; k < xp.length; k++)
                        xpoints[k] = (int)(xp[k] * dots);
                    for (int k = 0; k < yp.length; k++)
                        ypoints[k] = (int)(yp[k] * dots);
                    g.drawPolygon(xpoints, ypoints,((Integer)(com.param[2])).intValue());
                    break;
                case PrintCommand.DRAWPOLYLINE:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    xp = (double[])(com.param[0]);
                    yp = (double[])(com.param[1]);
                    xpoints = new int[xp.length];
                    ypoints = new int[yp.length];
                    for (int k = 0; k < xp.length; k++)
                        xpoints[k] = (int)(xp[k] * dots);
                    for (int k = 0; k < yp.length; k++)
                        ypoints[k] = (int)(yp[k] * dots);
                    g.drawPolyline(xpoints, ypoints,((Integer)(com.param[2])).intValue());
                    break;
                case PrintCommand.DRAWRECT:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.drawRect((int)(((Double)(com.param[0])).doubleValue()*dots),(int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots));
                    break;
                case PrintCommand.DRAWROUNDRECT:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.drawRoundRect((int)(((Double)(com.param[0])).doubleValue()*dots),(int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots),(int)(((Double)(com.param[4])).doubleValue()*dots),(int)(((Double)(com.param[5])).doubleValue()*dots));
                    break;
                case PrintCommand.FILL3DRECT:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.fill3DRect((int)(((Double)(com.param[0])).doubleValue()*dots),(int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots),((Boolean)(com.param[4])).booleanValue());
                    break;
                case PrintCommand.FILLARC:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.fillArc((int)(((Double)(com.param[0])).doubleValue()*dots),(int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots),((Integer)(com.param[4])).intValue(),((Integer)(com.param[5])).intValue());
                    break;
                case PrintCommand.FILLOVAL:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.fillOval((int)(((Double)(com.param[0])).doubleValue()*dots),(int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots));
                    break;
                case PrintCommand.FILLPOLYGON:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    xp = (double[])(com.param[0]);
                    yp = (double[])(com.param[1]);
                    xpoints = new int[xp.length];
                    ypoints = new int[yp.length];
                    for (int k = 0; k < xp.length; k++)
                        xpoints[k] = (int)(xp[k] * dots);
                    for (int k = 0; k < yp.length; k++)
                        ypoints[k] = (int)(yp[k] * dots);
                    g.fillPolygon(xpoints, ypoints,((Integer)(com.param[2])).intValue());
                    break;
                case PrintCommand.FILLRECT:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.fillRect((int)(((Double)(com.param[0])).doubleValue()*dots),(int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots));
                    break;
                case PrintCommand.FILLROUNDRECT:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    g.fillRoundRect((int)(((Double)(com.param[0])).doubleValue()*dots),(int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots),(int)(((Double)(com.param[4])).doubleValue()*dots),(int)(((Double)(com.param[5])).doubleValue()*dots));
                    break;
                case PrintCommand.SETCLIP:
                    if (g == null) getPrintGraphic();
                    g.setClip((int)(((Double)(com.param[0])).doubleValue()*dots),(int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots));
                    break;
                case PrintCommand.SETCOLOR:
                    if (g == null) getPrintGraphic();
                    g.setColor((Color)(com.param[0]));
                    break;
                case PrintCommand.SETPAINTMODE:
                    if (g == null) getPrintGraphic();
                    g.setPaintMode();
                    break;
                case PrintCommand.SETXORMODE:
                    if (g == null) getPrintGraphic();
                    g.setXORMode((Color)(com.param[0]));
                    break;
                case PrintCommand.SETMEDIA:
                    pre = (String)(com.param[0]);
                    if (!media.equals(pre)) { // change media, so begin another print job
                        if (pjob != null) {
                            if (g != null)
                               g.dispose();
                            pjob.end();
                            pjob = null;
                            g = null;
                        }
                    }
                    media = pre;
                    break;
                case PrintCommand.SETORI:
                    pre = (String)(com.param[0]);
                    if (!ori.equals(pre)) { // change orientation, so begin another print job
                        if (pjob != null) {
                            if (g != null)
                                g.dispose();
                            pjob.end();
                            pjob = null;
                            g = null;
                        }
                    }
                    ori = pre;
                    break;
                case PrintCommand.SETCOPY:
                    int c = ((Integer)(com.param[0])).intValue();
                    copy = c;
                    break;
                case PrintCommand.SETPRINTER:
                    pre = (String)(com.param[0]);
                    if (!printer.equals(pre)) { // change printer, so begin another print job
                        if (pjob != null) {
                            if (g != null)
                                g.dispose();
                            pjob.end();
                            pjob = null;
                            g = null;
                        }
                    }
                    printer = pre;
                    break;
                case PrintCommand.SETJOBTITLE:
                    jobTitle = (String)(com.param[0]);
                    break;
                case PrintCommand.DRAWSTRING:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    draw((String)(com.param[0]), (int)(((Double)(com.param[1])).doubleValue()*dots),(int)(((Double)(com.param[2])).doubleValue()*dots),(int)(((Double)(com.param[3])).doubleValue()*dots),((Integer)(com.param[4])).intValue());
                    break;
                case PrintCommand.NEWPAGE:
                    nextPage();
                    break;
                case PrintCommand.SETFONT:
                    if (g == null) getPrintGraphic();
                    font = new Font((String)(com.param[0]), ((Integer)(com.param[1])).intValue(), (int)(((Double)(com.param[2])).doubleValue()*dots+0.5));
                    g.setFont(font);
                    break;
                case PrintCommand.SETCOLUMN:
                    Double tt[][] = (Double[][])(com.param[0]);
                    col = new int[tt.length][6];
                    for (int k = 0; k < tt.length; k++) {
                        col[k][0] = (int)(tt[k][0].doubleValue()*dots); // startx
                        col[k][1] = (int)(tt[k][1].doubleValue()*dots); // starty
                        col[k][2] = (int)(tt[k][2].doubleValue()*dots); // width
                        col[k][3] = (int)(tt[k][3].doubleValue()*dots); // height
                        col[k][4] = (int)(tt[k][4].doubleValue()*dots); // currentx
                        col[k][5] = (int)(tt[k][5].doubleValue()*dots); // currenty
                    }
                    break;
                case PrintCommand.DRAWIMAGEINPARAGRAPH:
                    if (g == null) getPrintGraphic();
                    String cap = (String)(com.param[1]); // the string to draw
                    int align = ((Integer)(com.param[6])).intValue();
                    boolean forward = ((Boolean)(com.param[7])).booleanValue();
                    f = g.getFontMetrics();
                    fontHeight = f.getHeight();
                    int atx = (int)(((Double)(com.param[2])).doubleValue()*dots);
                    int aty = (int)(((Double)(com.param[3])).doubleValue()*dots);
                    int needW = (int)(((Double)(com.param[4])).doubleValue()*dots);
                    int needH = (int)(((Double)(com.param[5])).doubleValue()*dots);
                    int imH = needH;
                    if (cap != null && cap.length() != 0)
                        needH += (int)(fontHeight*lineWidth);
                    // try to find which column to draw
                    for (j = 0; j < col.length; j++) {
                        // currentx < starx+widht and currenty < starty + height
                        if (col[j][4] < col[j][0]+col[j][2] && col[j][5]+needH+aty < col[j][1]+col[j][3])
                            break;
                        // mark full
                        col[j][4] = col[j][0] + col[j][2];
                        col[j][5] = col[j][1] + col[j][3];
                    }
                    if (j == col.length) { // all columns are full
                        nextPage();
                        j = 0;
                    }
                    // calculate the draw position
                    cury = col[j][5] + (int)(fontHeight*(lineWidth-1))+aty;
                    if (align == CENTER) {
                        curx = col[j][0]+(col[j][2]-needW)/2;
                    } else if (align == RIGHT) {
                        curx = col[j][0]+col[j][2]-needW-atx;
                    } else {
                        curx = col[j][0] + atx;
                    }
                    Image im = new ReadyImage((byte[])(com.param[0]),needW, imH).getImage();
                    if (!(mode == COUNTPAGE || forbid!=null && forbid[currentPage]))
                        g.drawImage(im, curx, cury, needW, imH, null);
                    cury += imH;
                    if (cap != null && cap.length()>0) {
                        draw(cap, col[j][0]+atx, cury+f.getAscent(), col[j][2], align);
                        cury += (int)(fontHeight*lineWidth);
                    }
                    col[j][4] = col[j][0];
                    if (forward)
                        col[j][5] = cury;
                    break;
                case PrintCommand.DRAWPARAGRAPH:
                    if (g == null) getPrintGraphic();
                    String s = (String)(com.param[0]); // the string to draw
                    int style = ((Integer)(com.param[1])).intValue();
                    f = g.getFontMetrics();
                    ascent = f.getAscent();
                    fontHeight = (int)(ascent * lineWidth);
                    // try to find which column to draw
                    for (j = 0; j < col.length; j++) {
                        // currentx < starx+widht and currenty < starty + height
                        if (col[j][4] < col[j][0]+col[j][2] && col[j][5]+ascent < col[j][1]+col[j][3])
                            break;
                        // mark full
                        col[j][4] = col[j][0] + col[j][2];
                        col[j][5] = col[j][1] + col[j][3];
                    }
                    if (j == col.length) { // all columns are full
                        nextPage();
                        j = 0;
                    }
                    inCol = j;
                    if (s==null || s.length()==0) {
                        if (col[j][5] != col[j][1] + col[j][3])
                            col[j][5] += fontHeight;
                        break;
                    }
                    curx = col[j][4];
                    cury = col[j][5];
                    char[] chars = s.toCharArray();
                    for (;;) {
                        String[] s1 = splitOne(s, col[j][2]);
                        if (s1[1] == null) { // last line
                            if (style == BOTH)
                                draw(s1[0], curx, cury+ascent, col[j][2], LEFT);
                            else
                                draw(s1[0], curx, cury+ascent, col[j][2], style);
                            cury += fontHeight;
                            curx = col[j][0];
                            break;
                        } else if (s1[0].endsWith("\n")) {
                            if (style == BOTH)
                                draw(s1[0], curx, cury+ascent, col[j][2], LEFT);
                            else
                                draw(s1[0].substring(0,s1[0].length()-1), curx, cury+ascent, col[j][2], style);
                            cury += fontHeight;
                            curx = col[j][0];
                        } else {
                            draw(s1[0], curx, cury+ascent, col[j][2], style);
                            cury += fontHeight;
                            curx = col[j][0];
                        }
                        s = s1[1];
                        if (cury +ascent >= col[j][1]+col[j][3]) {
                            // mark column j is full
                            col[j][4] = col[j][0] + col[j][2];
                            col[j][5] = col[j][1] + col[j][3];
                            j++;
                            if (j >= col.length) { // to nextpage
                                nextPage();
                                j = 0;
                            }
                            curx = col[j][0];
                            cury = col[j][1];
                            inCol = j;
                        }
                    }
                    col[j][4] = curx;
                    col[j][5] = cury;
                    break;
                case PrintCommand.DRAWLINE:
                    if (g == null) getPrintGraphic();
                    if (mode == COUNTPAGE || forbid!=null && forbid[currentPage]) continue;
                    double x1 = ((Double)(com.param[0])).doubleValue();
                    double y1 = ((Double)(com.param[1])).doubleValue();
                    double x2 = ((Double)(com.param[2])).doubleValue();
                    double y2 = ((Double)(com.param[3])).doubleValue();
                    g.drawLine((int)(x1*dots),(int)(y1*dots),(int)(x2*dots),(int)(y2*dots));
                    break;
                case PrintCommand.ADDTABLE:
                    tp = new TablePos();
                    tp.id = (String)(com.param[0]);
                    tp.caption = (String)(com.param[1]);
                    tp.startx = (int)(((Double)(com.param[2])).doubleValue()*dots);
                    tp.title = (String[])(com.param[3]);
                    double[] www = (double[])(com.param[4]);
                    tp.width = new int[www.length];
                    tp.wrap = (boolean[])(com.param[5]);
                    tp.align = (int[])(com.param[6]);
                    // fill in missing parameters
                    int maxDim = 0;
                    if (tp.title != null && tp.title.length > maxDim)
                        maxDim = tp.title.length;
                    if (tp.width != null && tp.width.length > maxDim)
                        maxDim = tp.width.length;
                    for (int z = 0; z < tp.width.length; z++)
                        tp.width[z] = (int)(www[z]*dots);
                    if (tp.wrap == null || tp.wrap.length < maxDim) {
                        boolean[] tb = new boolean[maxDim];
                        if (tp.wrap != null) {
                            for (int z = 0; z < tp.wrap.length; z++)
                                tb[z] = tp.wrap[z];
                        }
                        tp.wrap = tb;
                    }
                    if (tp.align == null || tp.align.length < maxDim) {
                        int[] tb = new int[maxDim];
                        if (tp.align != null) {
                            for (int z = 0; z < tp.align.length; z++)
                                tb[z] = tp.align[z];
                        }
                        tp.align = tb;
                    }
                    tables.add(tp);
                    // try to find which column to draw the table
                    for (j = 0; j < col.length; j++) {
                        if (col[j][4] < col[j][0]+col[j][2] && col[j][5] < col[j][1]+col[j][3])
                            break;
                    }
                    if (j == col.length) { // all columns are full
                        nextPage();
                        j = 0;
                    }
                    tp.cury = col[j][5];
                    tp.inCol = j;
                    break;
                case PrintCommand.ADDROW:
                    if (g == null) getPrintGraphic();
                    // check if need to next column or next page
                    tableid = (String)(com.param[0]);
                    String[] data = (String[])(com.param[1]);
                    tp = null;
                    for (int k = 0; k < tables.size(); k++) {
                        if (tables.get(k).id.equals(tableid)) {
                            tp = tables.get(k);
                            break;
                        }
                    }
                    if (tp == null) { // no such table
                        continue;
                    }
                    tp.addRow(data);
                    break;
                case PrintCommand.CLOSETABLE:
                    tableid = (String)(com.param[0]);
                    tp = null;
                    for (int k = 0; k < tables.size(); k++) {
                        if (tables.get(k).id.equals(tableid)) {
                            tp = tables.get(k);
                            tables.remove(k);
                            break;
                        }
                    }
                    if (tp == null) { // no such table
                        continue;
                    }
                    if (tp.cury > col[tp.inCol][5]) {
                        col[tp.inCol][5] = tp.cury;
                    }
                    break;
                case PrintCommand.CLOSEROW:
                    if (g == null) getPrintGraphic();
                    tableid = (String)(com.param[0]);
                    tp = null;
                    for (int k = 0; k < tables.size(); k++) {
                        if (tables.get(k).id.equals(tableid)) {
                            tp = tables.get(k);
                            break;
                        }
                    }
                    if (tp == null) { // no such table
                        continue;
                    }
                    tp.closeRow();
                    break;
                case PrintCommand.ADDTABLEHEADCELL:
                    tableid = (String)(com.param[0]);
                    tp = null;
                    for (int k = 0; k < tables.size(); k++) {
                        if (tables.get(k).id.equals(tableid)) {
                            tp = tables.get(k);
                            break;
                        }
                    }
                    if (tp != null) { // found target table
                        String title = (String)(com.param[1]);
                        int x = ((Integer)(com.param[2])).intValue();
                        int y = ((Integer)(com.param[3])).intValue();
                        int w = ((Integer)(com.param[4])).intValue();
                        int h = ((Integer)(com.param[5])).intValue();
                        int a = ((Integer)(com.param[6])).intValue();
                        tp.addHeadCell(title, x, y, w, h, a);
                    }
                    break;
                case PrintCommand.ADDROWCELL:
                    tableid = (String)(com.param[0]);
                    tp = null;
                    for (int k = 0; k < tables.size(); k++) {
                        if (tables.get(k).id.equals(tableid)) {
                            tp = tables.get(k);
                            break;
                        }
                    }
                    if (tp != null) { // found target table
                        String title = (String)(com.param[1]);
                        int x = ((Integer)(com.param[2])).intValue();
                        int y = ((Integer)(com.param[3])).intValue();
                        int w = ((Integer)(com.param[4])).intValue();
                        int h = ((Integer)(com.param[5])).intValue();
                        int a = ((Integer)(com.param[6])).intValue();
                        tp.addRowCell(title, x, y, w, h, a);
                    }
                    break;
                case PrintCommand.SETTABLEBORDER:
                    tableid = (String)(com.param[0]);
                    tp = null;
                    for (int k = 0; k < tables.size(); k++) {
                        if (tables.get(k).id.equals(tableid)) {
                            tp = tables.get(k);
                            break;
                        }
                    }
                    if (tp != null) { // no such table
                        tp.borderOn = ((Boolean)(com.param[1])).booleanValue();
                    }
                    break;
                case PrintCommand.SETHEADER:
                    header = (String)(com.param[0]);
                    headerFont = new Font((String)(com.param[1]),((Integer)(com.param[2])).intValue(), (int)(((Double)(com.param[3])).doubleValue()*dots));
                    headerX = (int)(((Double)(com.param[4])).doubleValue()*dots);
                    headerY = (int)(((Double)(com.param[5])).doubleValue()*dots);
                    headerW = (int)(((Double)(com.param[6])).doubleValue()*dots);
                    headerAlign = ((Integer)(com.param[7])).intValue();
                    break;
                case PrintCommand.SETFOOTER:
                    footer = (String)(com.param[0]);
                    footerFont = new Font((String)(com.param[1]),((Integer)(com.param[2])).intValue(), (int)(((Double)(com.param[3])).doubleValue()*dots));
                    footerX = (int)(((Double)(com.param[4])).doubleValue()*dots);
                    footerY = (int)(((Double)(com.param[5])).doubleValue()*dots);
                    footerW = (int)(((Double)(com.param[6])).doubleValue()*dots);
                    footerAlign = ((Integer)(com.param[7])).intValue();
                    break;
                case PrintCommand.SETLINEWIDTH:
                    lineWidth = ((Double)(com.param[0])).doubleValue();
                    break;
                case PrintCommand.SETROWGAP:
                    tableid = (String)(com.param[0]);
                    tp = null;
                    for (int k = 0; k < tables.size(); k++) {
                        if (tables.get(k).id.equals(tableid)) {
                            tp = tables.get(k);
                            break;
                        }
                    }
                    if (tp != null) { // no such table
                        tp.rowGap = ((Double)(com.param[1])).doubleValue();
                    }
                    break;
                case PrintCommand.SETXY:
                    int setx = (int)(((Double)(com.param[0])).doubleValue()*dots);
                    int sety = (int)(((Double)(com.param[1])).doubleValue()*dots);
                    for (j = 0; j < col.length; j++) {
                        // currentx < starx+widht and currenty < starty + height
                        if (setx >= col[j][0] && setx <= col[j][0]+col[j][2] && sety >= col[j][1] && sety <= col[j][1]+col[j][3])
                            break;
                    }
                    if (j == col.length) { // no column in such posion, choose 0
                        j = 0;
                    }
                    inCol = j;
                    col[j][4] = setx;
                    col[j][5] = sety;
                }
            } catch(Exception err) {
                err.printStackTrace();
                break;
            }
        }
        if (g != null)
            g.drawString(" ",100,100); // work around the bug that last command not printed
        inProcess = false;
    }
    private void nextPage() {
        try {
            if (mode == PREVIEW) {
                Dimension d = getPreferredSize();
                Image tmp = pc.createImage(d.width, d.height);
                pages.add(tmp);
            } else if (mode == PRINT) {
                if (g != null) {
                    g.drawString(" ",100,100);
                    g.dispose();
                }
            } else if (mode == COUNTPAGE) {
            }
            for (int k = 0; k < col.length; k++) {
                // reset all current(x,y)
                col[k][4] = col[k][0];
                col[k][5] = col[k][1];
            }
            for (int k = 0; k < tables.size(); k++) {
                TablePos ttt = tables.get(k);
                ttt.cury = col[0][1]; // set to first column's stary
                ttt.inCol = 0;
                ttt.needHead = true;
            }
            inCol = 0;
            currentPage++;
            getPrintGraphic();
        } catch(Exception err) {}
    }
    /**
     * A EasyPrint created by constructor EasyPrint() will call this method when a new page is created.
     * Override this method in a subclass for a customized new page behavior.
     */
    protected void pageCreated(EasyPrint pb) {
    }
    // This method will be called wheneven a new page is created.
    private void getPrintGraphic() {
        if (mode == PREVIEW) {
            if (pages.size() == 0) {
                Dimension d = getPreferredSize();
                Image tmp = pc.createImage(d.width, d.height);
                pages.add(tmp);
            }
            g = new MyGraphics(pages.get(pages.size()-1).getGraphics());
            if (font != null) {
                g.setFont(font);
            }
        } else if (mode == PRINT) {
            // pjob.getGraphics() will create a new printer page, so check if need to print out
            if (forbid == null || !forbid[currentPage]) {
                if (pjob == null) {
                    pjob = getPrintJob(background);
                }
                if (pjob == null) {
                    g = new BufferedImage(1000,1000,BufferedImage.TYPE_3BYTE_BGR).getGraphics();
                } else g = pjob.getGraphics();
            } else
                g = new BufferedImage(1000,1000,BufferedImage.TYPE_3BYTE_BGR).getGraphics();
            if (g == null)
                return;
            g = new MyGraphics(g);
            if (font != null) {
                g.setFont(font);
            }
        } else if (mode == COUNTPAGE) {
            if (g == null)
                g = new BufferedImage(1000,1000,BufferedImage.TYPE_3BYTE_BGR).getGraphics();
            // g will not be disposed, so we don't need to create a new one or set font
            return; // no need to draw header and footer in COUNTPAGE mode
        }
        if (forbid != null && forbid[currentPage]) return;
        // draw Header
        if (header != null && !header.equals("")) {
            String f = header.replace("#page", Integer.toString(currentPage));
            f = f.replace("#total", Integer.toString(totalPages));
            if (headerFont == null)
                headerFont = font;
            if (headerFont != null)
                g.setFont(headerFont);
            FontMetrics fm = g.getFontMetrics();
            int ascent = fm.getAscent();
            draw(f, headerX, headerY+ascent, headerW, headerAlign);
            if (font != null) { // restore original font
                g.setFont(font);
            }
        }
        // draw Footer
        if (footer != null && !footer.equals("")) {
            String f = footer.replace("#page", Integer.toString(currentPage));
            f = f.replace("#total", Integer.toString(totalPages));
            if (footerFont == null)
                footerFont = font;
            if (footerFont != null)
                g.setFont(footerFont);
            draw(f, footerX, footerY, footerW, footerAlign);
            if (font != null) { // restore original font
                g.setFont(font);
            }
        }
        // draw grid
        if (gridOn && mode != COUNTPAGE && (forbid==null || !forbid[currentPage])) {
            Color c = g.getColor();
            g.setFont(new Font("monospaced", Font.PLAIN, 10));
            g.setColor(Color.RED);
            Dimension d = getPreferredSize();
            for (int i = 1; i*dots < d.width; i++) {
                g.drawString(i+"",i*dots, 20);
                g.drawLine(i*dots, 20, i*dots, d.height-10);
            }
            for (int i = 1; i*dots < d.height; i++) {
                g.drawString(i+"", 10, i*dots);
                g.drawLine(20, i*dots, d.width-10, i*dots);
            }
            g.setColor(c);
            g.setFont(font);
        }
        // handle page event
        if (pl == null)
            pageCreated(this);
        else
            pl.pageCreated(this);
    }
    private PrintJob getPrintJob(boolean background) {
        PageAttributes pa = new PageAttributes();
        pa.setMedia(mediaMap.get(media));
        if (ori.equalsIgnoreCase("portrait")) {
            pa.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
        } else if (ori.equalsIgnoreCase("landscape")) {
            pa.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
        }
        JobAttributes jAttr = new JobAttributes();
        jAttr.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES);
        jAttr.setCopies(copy);
        if (printer!=null) {
            jAttr.setPrinter(printer);
        }
        JFrame f = new JFrame(jobTitle);
        if (!background) {
            //f.setVisible(true);
            PrintJob tmp = Toolkit.getDefaultToolkit().getPrintJob(f, jobTitle, jAttr, pa);
            f.dispose();
            return tmp;
        } else {
            jAttr.setDialog(JobAttributes.DialogType.NONE);
            return Toolkit.getDefaultToolkit().getPrintJob(f, jobTitle, jAttr, pa);
        }
    }
    public Dimension getPreferredSize() {
        double[] d = getSize();
        return new Dimension((int)(d[0]*dots),(int)(d[1]*dots));
    }
    private static Hashtable<String,PageAttributes.MediaType> mediaMap;
    static {
        mediaMap = new Hashtable<String, PageAttributes.MediaType>();
        mediaMap.put("A0", PageAttributes.MediaType.A0);
        mediaMap.put("A1", PageAttributes.MediaType.A1);
        mediaMap.put("A2", PageAttributes.MediaType.A2);
        mediaMap.put("A3", PageAttributes.MediaType.A3);
        mediaMap.put("A4", PageAttributes.MediaType.A4);
        mediaMap.put("A5", PageAttributes.MediaType.A5);
        mediaMap.put("A6", PageAttributes.MediaType.A6);
        mediaMap.put("A7", PageAttributes.MediaType.A7);
        mediaMap.put("A8", PageAttributes.MediaType.A8);
        mediaMap.put("A9", PageAttributes.MediaType.A9);
        mediaMap.put("A10", PageAttributes.MediaType.A10);
        mediaMap.put("B0", PageAttributes.MediaType.B0);
        mediaMap.put("B1", PageAttributes.MediaType.B1);
        mediaMap.put("B2", PageAttributes.MediaType.B2);
        mediaMap.put("B3", PageAttributes.MediaType.B3);
        mediaMap.put("B4", PageAttributes.MediaType.B4);
        mediaMap.put("B5", PageAttributes.MediaType.B5);
        mediaMap.put("B6", PageAttributes.MediaType.B6);
        mediaMap.put("B7", PageAttributes.MediaType.B7);
        mediaMap.put("B8", PageAttributes.MediaType.B8);
        mediaMap.put("B9", PageAttributes.MediaType.B9);
        mediaMap.put("B10", PageAttributes.MediaType.B10);
        mediaMap.put("letter", PageAttributes.MediaType.LETTER);
    }
    private double[] getSize() {
        double w = 8.27;
        double h = 11.69;
        if (media.equals("A4")) {
            w = 8.27;
            h = 11.69;
        } else if (media.equals("B4")) {
            w = 9.84;
            h = 13.9;
        } else if (media.equals("A3")) {
            w = 11.69;
            h = 16.54;
        } else if (media.equals("B3")) {
            w = 13.90;
            h = 19.68; // B3 直印
        } else if (media.equals("A5")) {
            w = 5.83;
            h = 8.27;
        } else if (media.equals("B5")) {
            w = 6.93;
            h = 9.84;
        } else if (media.equals("A2")) {
            w = 16.54;
            h = 23.39;
        } else if (media.equals("B2")) {
            w = 19.68;
            h = 27.83;
        } else if (media.equals("A1")) {
            w = 23.38;
            h = 33.11;
        } else if (media.equals("B1")) {
            w = 27.83;
            h = 39.37;
        } else if (media.equals("A0")) {
            w = 33.11;
            h = 46.81;
        } else if (media.equals("B0")) {
            w = 39.37;
            h = 46.81;
        } else if (media.equals("LETTER")) {
            w = 8.5;
            h = 11;
        }
        if (ori.equals("portrait"))
            return new double[] { w, h};
        return new double[] {h, w};
    }
    // inner class for table rendering
    class TablePos {
        boolean needHead = true, borderOn = true;
        int cury;
        int startx;
        int inCol;
        String id, caption;
        String[] title;
        int[] align;
        int[] width;
        boolean[] wrap;
        double rowGap = 0.4;
        Vector<Cell> headCells = new Vector<Cell>();
        Vector<Cell> rowCells = new Vector<Cell>();
        Font headFont;
        class Cell {
            int x, y, w, h, a;
            String content;
            Cell(int x, int y, int w, int h, int a, String c) {
                this.x = x;
                this.y = y;
                this.w = w;
                this.h = h;
                this.a = a;
                this.content = c;
            }
        }
        void addTitleCell(int x, int y, int w, int h, int a, String content) {
            headCells.add(new Cell(x, y, w, h, a, content));
        }
        void addRow(String[] data) {
            // is current column availabe?
            int needHeight = 0;
            if (needHead)
                needHeight += drawHead(true);
            needHeight += drawRow(data, true);
            if (cury+needHeight > col[inCol][1]+col[inCol][3]) { // cross boundary
                col[inCol][4] = col[inCol][0] + col[inCol][2];
                col[inCol][5] = col[inCol][1] + col[inCol][3];
                inCol++; // to next column
                if (inCol != col.length)
                    cury = col[inCol][1];
            }
            if (inCol == col.length) { // all columns are full
                nextPage();
            }
            if (needHead) {
                drawHead(false);
                needHead = false;
            }
            drawRow(data, false);
        }
        private int drawRow(String[] data, boolean countOnly) {
            FontMetrics f = g.getFontMetrics();
            int ascent = f.getAscent();
            int fontHeight = (int)(ascent * lineWidth);
            int gap = (f.charWidth(' ')+1)/2;
            int atx = col[inCol][0]+startx;
            int maxLine = 1;
            for (int k = 0; k < data.length; k++) {
                String myData = data[k];
                if (myData.length() == 0) continue;
                boolean myWrap = wrap[k];
                if (myData.endsWith("~")) {
                    myWrap = false;
                    myData = myData.substring(0, myData.length() - 1);
                }
                if (myData.length() == 0) continue;
                int myAlign = align[k];
                char lead = myData.charAt(0);
                if (lead == '^' || lead == '<' || lead == '>') {
                    if (lead == '^')
                        myAlign = CENTER;
                    else if (lead == '<')
                        myAlign = LEFT;
                    else if (lead == '>')
                        myAlign = RIGHT;
                    myData = myData.substring(1, myData.length());
                }
                if (myWrap) {
                    String[] tmp = split(myData, width[k]-2*gap);
                    if (maxLine < tmp.length)
                        maxLine = tmp.length;
                    if (!countOnly) {
                        for (int l = 0; l < tmp.length; l++) {
                            draw(tmp[l], atx+gap+1, cury+fontHeight*l+ascent, width[k]-2*gap, myAlign);
                        }
                    }
                } else { // truncate mode
                    if (!countOnly) {
                        draw(truncate(myData, width[k]-2*gap), atx+gap+1, cury+(int)((lineWidth-1)/2*ascent)+ascent, width[k]-2*gap, myAlign);
                    }
                }
                atx += width[k];
            }
            if (countOnly)
                return maxLine*fontHeight+2*gap;
            // draw border
            if (borderOn && mode != COUNTPAGE && (forbid==null || !forbid[currentPage])) {
                atx = col[inCol][0]+startx;
                for (int k = 0; k < data.length; k++) {
                    g.drawRect(atx, cury-(int)((lineWidth-1)/2*ascent), width[k], (maxLine*fontHeight)+2*gap);
                    atx += width[k];
                }
            }
            cury += maxLine*fontHeight+2*gap;
            if (cury > col[inCol][5])
                col[inCol][5] = cury;
            return  maxLine*fontHeight+2*gap;
        }
        void addHeadCell(String s, int x, int y, int w, int h, int align) {
            headCells.add(new Cell(x, y, w, h, align, s));
            headFont = g.getFont();
        }
        void addRowCell(String s, int x, int y, int w, int h, int align) {
            rowCells.add(new Cell(x, y, w, h, align, s));
        }
        void closeRow() {
            int needHeight = drawCells(rowCells, true, false);
            if (needHead)
                needHeight += drawHead(true);
            // is current column availabe?
            if (cury+needHeight > col[inCol][1]+col[inCol][3]) { // cross boundary
                col[inCol][5] = cury+needHeight;
                inCol++; // to next column
                if (inCol != col.length)
                    cury = col[inCol][1];
            }
            if (inCol == col.length) { // all columns are full
                nextPage();
            }
            if (needHead) {
                drawHead();
                needHead = false;
            }
            drawCells(rowCells);
            rowCells.clear();
            if (cury > col[inCol][5])
                col[inCol][5] = cury;
        }
        int drawHead() {
            return drawHead(false);
        }
        int drawHead(boolean countOnly) {
            Font contentFont = g.getFont();
            if (headFont != null) {
                g.setFont(headFont);
            }
            FontMetrics f = g.getFontMetrics();
            int gap = (f.charWidth(' ')+1)/2;
            int ascent = f.getAscent();
            int fontHeight = (int)(ascent * lineWidth);
            int rowHeight = (int)(ascent * (lineWidth+rowGap));
            int headHeight = 0;
            // draw Caption
            if (caption != null && !caption.equals("")) {
                if (!countOnly) {
                    if (mode != COUNTPAGE && (forbid==null || !forbid[currentPage]))
                        g.drawString(caption, col[inCol][4]+startx, cury+fontHeight);
                    cury += rowHeight;
                }
                headHeight += rowHeight;
            }
            // draw Titles and lines
            int atx = col[inCol][0]+startx;
            if (title != null && title.length>0) {
                int maxLine = 1;
                // draw title
                // find max lines
                for (int k = 0; k < title.length; k++) {
                    String[] tmp = split(title[k], width[k]-2*gap);
                    if (tmp.length > maxLine)
                        maxLine = tmp.length;
                }
                if (countOnly) {
                    g.setFont(contentFont);
                    return headHeight + (maxLine*fontHeight+2*gap);
                }
                for (int k = 0; k < title.length; k++) {
                    String[] tmp = split(title[k], width[k]-2*gap);
                    int topgap = (int)(((maxLine-tmp.length)*fontHeight+1)/2.0);
                    for (int l = 0; l < tmp.length; l++) {
                        int alignType = LEFT;
                        if (align != null && align.length >= k)
                            alignType = align[k];
                        draw(tmp[l], atx+gap+1, cury+fontHeight*l+topgap+1+ascent, width[k]-2*gap, alignType);
                    }
                    atx += width[k];
                }
                // draw lines
                if (borderOn && mode != COUNTPAGE && (forbid==null || !forbid[currentPage])) {
                    atx = col[inCol][0]+startx;
                    for (int k = 0; k < title.length; k++) {
                        g.drawRect(atx, cury-(int)((lineWidth-1)/2*ascent), width[k], maxLine*fontHeight+2*gap);
                        atx += width[k];
                    }
                }
                cury += (maxLine*fontHeight+2*gap);
                g.setFont(contentFont);
                return headHeight + (maxLine*fontHeight+2*gap);
            } else { // customized head
                g.setFont(contentFont);
                return drawCells(headCells, countOnly, true);
            }
        }
        private int drawCells(Vector<Cell> cells) {
            return drawCells(cells, false, false);
        }
        private int drawCells(Vector<Cell> cells, boolean countOnly, boolean inHead) {
            Font contentFont = g.getFont();
            if (inHead && headFont != null)
                g.setFont(headFont);
            FontMetrics f = g.getFontMetrics();
            int gap = (f.charWidth(' ')+1)/2;
            int ascent = f.getAscent();
            int fontHeight = (int)(ascent * lineWidth);
            int rowHeight = (int)(ascent * (lineWidth+rowGap));
            int atx = col[inCol][0]+startx;
            // how many rows?
            int maxRow = 0;
            for (int i = 0; i < cells.size(); i++) {
                Cell tt = cells.get(i);
                if (tt.y + tt.h > maxRow)
                    maxRow = tt.y + tt.h;
            }
            int[] rh = new int[maxRow];
            int[] rlines = new int[maxRow];
            // find each row's height
            for (int i = 0; i < cells.size(); i++) {
                Cell tt = cells.get(i);
                if (tt.content != null && tt.content.length() > 0 && tt.h == 1) {
                    int w = width[tt.x];
                    for (int j = tt.x+1; j < tt.x+tt.w; j++)
                        w += width[j]*dots;
                    String[] tmp = split(tt.content, w-2*gap);
                    if (tmp.length > rlines[tt.y])
                        rlines[tt.y] = tmp.length;
                }
            }
            for (int j = 0; j < rlines.length; j++) {
                rh[j] = (rlines[j]*fontHeight + 2*gap);
            }
            int headHeight = 0;
            for (int j = 0; j < rh.length; j++)
                headHeight += rh[j];
            if (countOnly) {
                g.setFont(contentFont);
                return headHeight;
            }
            // draw each cell
            for (int i = 0; i < cells.size(); i++) {
                Cell tt = cells.get(i);
                atx = col[inCol][0]+startx;
                for (int j = 0; j < tt.x; j++)
                   atx += width[j];
                int w = width[tt.x];
                for (int j = tt.x+1; j < tt.x+tt.w; j++)
                    w += width[j];
                int h = 0;
                for (int j = tt.y; j < tt.y+tt.h; j++)
                    h += rh[j];
                int aty = cury;
                for (int j = 0; j < tt.y; j++)
                    aty += rh[j];
                if (tt.content != null && tt.content.length() > 0) {
                    String[] tmp = split(tt.content, w-2*gap);
                    int topgap = (int)((h-tmp.length*fontHeight-gap)/2);
                    for (int l = 0; l < tmp.length; l++)
                        draw(tmp[l], atx+gap+1, aty+l*fontHeight+topgap+ascent, w-2*gap, tt.a);
                }
                // draw rectangle
                if (borderOn && mode != COUNTPAGE && (forbid==null || !forbid[currentPage]))
                    g.drawRect(atx, aty-(int)((lineWidth-1)/2*ascent), w, (int)(h));
            }
            // forward tablepos.
            cury += headHeight;
            g.setFont(contentFont);
            return headHeight;
        }
    }
    private int viewingPage;
    private Vector<Image> pages = new Vector<Image>();
    private JComponent pc = new LocalPaint();
    /**
     * Get the page number being viewed. Page number starts from one.
     */
    public int getViewingPage() {
        return viewingPage+1;
    }
    /**
     * Get the total page number of this print job
     */
    public int getTotalPage() {
        return totalPages;
    }
    /**
     * Render previous page.
     */
    public void viewPreviousPage() {
        viewingPage--;
        if (viewingPage < 0) viewingPage = 0;
        pc.repaint();
    }
    /**
     * Render next page.
     */
    public void viewNextPage() {
        viewingPage++;
        if (viewingPage >= pages.size())
            viewingPage = pages.size() - 1;
        pc.repaint();
    }
    /**
     * Get the JComponent where EasyPrint will paint.
     */
    public JComponent getPrintCanvas() {
        return pc;
    }
    class LocalPaint extends JComponent {
        public void paintComponent(Graphics g) {
            if (pages.size() <= viewingPage)
                viewingPage = pages.size()-1;
            if (viewingPage <0 || viewingPage >= pages.size()) return;
            g.drawImage(pages.get(viewingPage),0,0,null);
        }
        public Dimension getPreferredSize() {
            if (pages.size() <= viewingPage)
                return EasyPrint.this.getPreferredSize();
            Image bi = pages.get(viewingPage);
            return new Dimension(bi.getWidth(null), bi.getHeight(null));
        }
    }
}
// make sure an image is ready and scaled to the specified width and height
class ReadyImage implements ImageObserver {
    int width, height;
    Image img;
    boolean done;
    public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        if ((infoflags&(ImageObserver.ALLBITS|ImageObserver.ERROR|ImageObserver.ABORT)) != 0) { // the image has been loaded completely or an error occurs
            done = true;
            notifyAll(); // 叫醒在等待的
            return false;
        }
        return true;
    }
    public ReadyImage(byte[] buf, int w, int h) {
        img = Toolkit.getDefaultToolkit().createImage(buf);
        img.getWidth(this);
        img.getHeight(this);
        ensureReady();
        if (w != width || h != height) {
            done = false;
            img = img.getScaledInstance(w,h,Image.SCALE_DEFAULT);
            img.getWidth(this);
            img.getHeight(this);
            ensureReady();
        }
    }
    private synchronized void ensureReady() {
        try {
            while (!done) {
                wait();
            }
        } catch(InterruptedException epp) {}
    }
    public Image getImage() {
        return img;
    }
}
class PrintCommand implements Serializable {
    public static final int SETMEDIA = 0;
    public static final int SETORI = 1;
    public static final int SETCOPY = 2;
    public static final int SETPRINTER = 3;
    public static final int SETJOBTITLE = 4;
    public static final int DRAWSTRING = 5;
    public static final int NEWPAGE = 6;
    public static final int SETFONT = 7;
    public static final int SETCOLUMN = 8;
    public static final int DRAWPARAGRAPH = 9;
    public static final int DRAWLINE = 10;
    public static final int ADDTABLE = 11;
    public static final int ADDROW = 12;
    public static final int CLOSETABLE = 13;
    public static final int ADDTABLEHEADCELL = 14;
    public static final int SETTABLEBORDER = 15;
    public static final int CLEARRECT = 16;
    public static final int CLIPRECT = 17;
    public static final int COPYAREA = 18;
    public static final int DRAW3DRECT = 19;
    public static final int DRAWARC = 20;
    public static final int DRAWIMAGE = 21;
    public static final int DRAWOVAL = 22;
    public static final int DRAWPOLYGON = 23;
    public static final int DRAWPOLYLINE = 24;
    public static final int DRAWRECT = 25;
    public static final int DRAWROUNDRECT = 26;
    public static final int FILL3DRECT = 27;
    public static final int FILLARC = 28;
    public static final int FILLOVAL = 29;
    public static final int FILLPOLYGON = 30;
    public static final int FILLRECT = 31;
    public static final int FILLROUNDRECT = 32;
    public static final int SETCLIP = 33;
    public static final int SETCOLOR = 34;
    public static final int SETPAINTMODE = 35;
    public static final int SETXORMODE = 36;
    public static final int SETHEADER = 37;
    public static final int SETFOOTER = 38;
    public static final int ADDROWCELL = 39;
    public static final int CLOSEROW = 40;
    public static final int SETLINEWIDTH = 41;
    public static final int SETROWGAP = 42;
    public static final int DRAWIMAGEINPARAGRAPH = 43;
    public static final int SETXY = 44;
    int opcode;
    Object[] param;
    boolean genByPageEvent;
    public PrintCommand(int opcode, Object[] param, boolean in) {
        this.opcode = opcode;
        this.param = param;
        genByPageEvent= in;
    }
}
class MyGraphics extends DebugGraphics {
    Graphics g;
    public MyGraphics(Graphics g) {
        super(g);
        this.g = g;
    }
    public void drawString(String s, int x, int y) {
        Font f = g.getFont();
        int size = f.getSize();
        if (f.canDisplayUpTo(s) == -1) {
            g.drawString(s, x, y);
            return;
        }
        int space = 0;
        // some font can't be displayed
        for (int i = 0; i < s.length(); i++) {
            String single = s.substring(i, i+1);
            if (f.canDisplayUpTo(single) != -1) { // Chage font to monospaced to print out unprintable char in current font
                Font second = new Font("monospaced", Font.PLAIN, size);
                g.setFont(second);
            }
            g.drawString(single, (int)(x+(i-0.5*space)*size), y);
            if (single.equals(" ")) space++;
            g.setFont(f); //
        }
    }
}
/*
class BufferedGraphics extends MyGraphics {
    class GraphicsCommand {
        int commandType;
        int x, y, width, height, dx, dy;
        int startAngle, arcAngle, offset, length;
        int dx1, dx2, dy1, dy2, sx1, sx2, sy1, sy2;
        int x1, y1, x2, y2;
        boolean raised;
        Image img;
        Color color;
        Font font;
        byte[] bdata;
        char[] cdata;
        ImageObserver observer
    }
    public static final int CLEARRECT = 1;
    public static final int CLIPRECT = 2;
    public static final int COPYAREA = 3;
    public static final int CREATE0 = 4;
    public static final int CREATE4 = 5;
    public static final int DISPOSE = 6;
    public static final int DRAW3DRECT = 7;
    public static final int DRAWARC = 8;
    public static final int DRAWBYTES = 9;
    public static final int DRAWIMAGE = 10;
    private Vector<GraphicsCommand> buf = new Vector<GraphicsCommand>();
    public void clearRect(int x, int y, int width, int height) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = CLEARRECT;
        c.x = x;
        c.y = y;
        c.width = width;
        c.height = height;
        buf.add(c);
    }
    public void clipRect(int x, int y, int width, int height) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = CLIPRECT;
        c.x = x;
        c.y = y;
        c.width = width;
        c.height = height;
        buf.add(c);
    }
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = COPYAREA;
        c.x = x;
        c.y = y;
        c.width = width;
        c.height = height;
        c.dx = dx;
        c.dy = dy;
        buf.add(c);
    }
    public Graphics create() {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = CREATE0;
        buf.add(c);
        return this;
    }
    public Graphics create(int x, int y, int width, int height) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = CREATE4;
        c.x = x;
        c.y = y;
        c.width = width;
        c.height = height;
        buf.add(c);
        return this;
    }
    public void dispose() {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DISPOSE;
        buf.add(c);
    }
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAW3DRECT;
        c.x = x;
        c.y = y;
        c.width = width;
        c.height = height;
        c.raised = raised;
        buf.add(c);
    }
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAWARC;
        c.x = x;
        c.y = y;
        c.width = width;
        c.height = height;
        c.startAngle = startAngle;
        c.arcAngle = arcAngle;
        buf.add(c);
    }
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAWBYTES;
        c.bdata = data;
        c.offset = offset;
        c.length = length;
        c.x = x;
        c.y = y;
        buf.add(c);
    }
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAWCHARS;
        c.cdata = data;
        c.offset = offset;
        c.length = length;
        c.x = x;
        c.y = y;
        buf.add(c);
    }
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAWIMAGE;
        c.colog = bgcolor;
        c.observer = observer;
        c.x = x;
        c.y = y;
        buf.add(c);
        return true;
    }
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAWIMAGE;
        c.observer = observer;
        c.x = x;
        c.y = y;
        buf.add(c);
        return true;
    }
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAWIMAGE;
        c.colog = bgcolor;
        c.observer = observer;
        c.x = x;
        c.y = y;
        c.width = width;
        c.height = height;
        buf.add(c);
        return true;
    }
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAWIMAGE;
        c.observer = observer;
        c.x = x;
        c.y = y;
        c.width = width;
        c.height = height;
        buf.add(c);
        return true;
    }
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        GraphicsCommand c = new GraphicsCommand();
        c.commandType = DRAWIMAGE;
        c.observer = observer;
        c.dx1 = dx1;
        c.dx2 = dx2;
        c.dy1 = dy1;
        c.dy2 = dy2;
        c.sx1 = sx1;
        c.sx2 = sx2;
        c.sy1 = sy1;
        c.sy2 = sy2;
        c.width = width;
        c.height = height;
        c.color = bgcolor;
        buf.add(c);
        return true;
    }
abstract  boolean 	drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
          Draws as much of the specified area of the specified image as is currently available, scaling it on the fly to fit inside the specified area of the destination drawable surface.
abstract  void 	drawLine(int x1, int y1, int x2, int y2)
          Draws a line, using the current color, between the points (x1, y1) and (x2, y2) in this graphics context's coordinate system.
abstract  void 	drawOval(int x, int y, int width, int height)
          Draws the outline of an oval.
abstract  void 	drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
          Draws a closed polygon defined by arrays of x and y coordinates.
 void 	drawPolygon(Polygon p)
          Draws the outline of a polygon defined by the specified Polygon object.
abstract  void 	drawPolyline(int[] xPoints, int[] yPoints, int nPoints)
          Draws a sequence of connected lines defined by arrays of x and y coordinates.
 void 	drawRect(int x, int y, int width, int height)
          Draws the outline of the specified rectangle.
abstract  void 	drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
          Draws an outlined round-cornered rectangle using this graphics context's current color.
abstract  void 	drawString(AttributedCharacterIterator iterator, int x, int y)
          Renders the text of the specified iterator applying its attributes in accordance with the specification of the TextAttribute class.
abstract  void 	drawString(String str, int x, int y)
          Draws the text given by the specified string, using this graphics context's current font and color.
 void 	fill3DRect(int x, int y, int width, int height, boolean raised)
          Paints a 3-D highlighted rectangle filled with the current color.
abstract  void 	fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
          Fills a circular or elliptical arc covering the specified rectangle.
abstract  void 	fillOval(int x, int y, int width, int height)
          Fills an oval bounded by the specified rectangle with the current color.
abstract  void 	fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
          Fills a closed polygon defined by arrays of x and y coordinates.
 void 	fillPolygon(Polygon p)
          Fills the polygon defined by the specified Polygon object with the graphics context's current color.
abstract  void 	fillRect(int x, int y, int width, int height)
          Fills the specified rectangle.
abstract  void 	fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
          Fills the specified rounded corner rectangle with the current color.
 void 	finalize()
          Disposes of this graphics context once it is no longer referenced.
abstract  Shape 	getClip()
          Gets the current clipping area.
abstract  Rectangle 	getClipBounds()
          Returns the bounding rectangle of the current clipping area.
 Rectangle 	getClipBounds(Rectangle r)
          Returns the bounding rectangle of the current clipping area.
 Rectangle 	getClipRect()
          Deprecated. As of JDK version 1.1, replaced by getClipBounds().
abstract  Color 	getColor()
          Gets this graphics context's current color.
abstract  Font 	getFont()
          Gets the current font.
 FontMetrics 	getFontMetrics()
          Gets the font metrics of the current font.
abstract  FontMetrics 	getFontMetrics(Font f)
          Gets the font metrics for the specified font.
 boolean 	hitClip(int x, int y, int width, int height)
          Returns true if the specified rectangular area might intersect the current clipping area.
abstract  void 	setClip(int x, int y, int width, int height)
          Sets the current clip to the rectangle specified by the given coordinates.
abstract  void 	setClip(Shape clip)
          Sets the current clipping area to an arbitrary clip shape.
abstract  void 	setColor(Color c)
          Sets this graphics context's current color to the specified color.
abstract  void 	setFont(Font font)
          Sets this graphics context's font to the specified font.
abstract  void 	setPaintMode()
          Sets the paint mode of this graphics context to overwrite the destination with this graphics context's current color.
abstract  void 	setXORMode(Color c1)
          Sets the paint mode of this graphics context to alternate between this graphics context's current color and the new specified color.
 String 	toString()
          Returns a String object representing this Graphics object's value.
abstract  void 	translate(int x, int y)
          Translates the origin of the graphics context to the point (x, y) in the current coordinate system.

}
*/
