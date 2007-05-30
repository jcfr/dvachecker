/*
 * ScreenMapper.java
 *
 * Created on May 29, 2007, 5:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dva.util;

import dva.displayer.Displayer;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

/**
 *
 * @author J-Chris
 */
public class ScreenMapper {
    
    private static double visualAcuityCharts[] = {0.1, 0.13, 0.17, 0.2, 0.25, 0.33, 0.4, 0.5, 0.67, 0.8, 1, 1.25, 1.67, 2};
    
    private double horizontalRes = 1280; 
    private double verticalRes = 800; 
    private float diagonalLength = 12.1f; 
    private float patientDistance = 6f; 
    private double screen_width = 0; 
    private double pixel_width = 0; 
    private double characterResolution = 1400;
    
    /*
     *
     */
    public static double getVA(int step){
        return visualAcuityCharts[step]; 
    }
    
    private GraphicsDevice outputGraphicsDevice = null; 
    
    static private ScreenMapper instance = null; 
    
    private static dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N;
            
    /** prevente from instanciation */
    private ScreenMapper() {
        screen_width = getScreenWidth(diagonalLength, horizontalRes / verticalRes, Units.INCHES); 
        pixel_width = getPixelWidth(screen_width, horizontalRes, Units.MM); 
    }
    
    /**
     *
     */
    public static ScreenMapper getInstance(){
        if (instance==null){
            instance = new ScreenMapper(); 
        }
        return instance; 
    }
    
    /**
     *
     */
    public enum Units {MM, M, INCHES}
    
    /**
     *
     */
    public double inches2mm(double inches) {
        return inches * 25.4; 
    }
    
    /**
     *
     */
    public double getScreenWidth(double diagonalLength, double aspectRatio, Units unit){
        if (unit == Units.INCHES) {
            diagonalLength = inches2mm( diagonalLength );
        }
        double w = Math.sqrt( Math.pow( diagonalLength, 2) / ( 1 + (1 / Math.pow(aspectRatio, 2))) ); 
        return w; 
    }
    
    /**
     *
     */
    public double getScreenHeight(double diagonalLength, double aspectRatio, Units unit){
        if (unit == Units.INCHES) {
            diagonalLength = inches2mm( diagonalLength );
        }
        double w = Math.sqrt( Math.pow( diagonalLength, 2) / ( 1 + Math.pow(aspectRatio, 2)) ); 
        return w; 
    }
    
    /**
     *
     */
    public double getPixelWidth(double screenWidth, double horizontalRes, Units unit){
        if (unit == Units.INCHES) {
            screenWidth = inches2mm( screenWidth );
        }
        return screenWidth / horizontalRes; 
    }
    
    /**
     *
     */
    public double getPixelHeight(double screenHeight, double verticalRes, Units unit){
        if (unit == Units.INCHES) {
            screenHeight = inches2mm( screenHeight );
        }
        return screenHeight / verticalRes; 
    }
    
    /**
     *
     */
    public double va2size(double va, double patientDistance, Units unit){
        //convert to MM
        if (unit == Units.M){
            patientDistance = patientDistance * 1000; 
        }
        double size = 2 * (patientDistance * Math.tan( Math.toRadians( 1 / (va * 60)) / 2 ));
        return size * 5;
    }
    
    /**
     *
     */
    public void setDisplayerOptions(double _horizontalRes, double _verticalRes, float _diagonalLength, float _patientDistance){
        horizontalRes = _horizontalRes;
        verticalRes = _verticalRes;
        diagonalLength = _diagonalLength;
        patientDistance = _patientDistance;
        DvaLogger.debug(ScreenMapper.class, "horizontalRes:"+horizontalRes+", verticalRes:"+verticalRes+", diagonalLength:"+diagonalLength+", patientDistance:"+patientDistance);
        
        characterResolution = resourceBundle.getDouble("config.character.resolution");
        double aspect_ratio = horizontalRes / verticalRes;
        DvaLogger.debug(ScreenMapper.class, "aspect_ratio:"+aspect_ratio);
        screen_width = getScreenWidth(diagonalLength, aspect_ratio, Units.INCHES);
        DvaLogger.debug(ScreenMapper.class, "screen_width:"+screen_width); 
        DvaLogger.debug(ScreenMapper.class, "width(pixel):" + millimeterAsPixel(screen_width, Displayer.getInstance()) );
        DvaLogger.debug(ScreenMapper.class, "height(pixel):" + millimeterAsPixel(getScreenHeight(diagonalLength, aspect_ratio, Units.INCHES), Displayer.getInstance()) );
        pixel_width = getPixelWidth(screen_width, horizontalRes, Units.MM); 
        DvaLogger.debug(ScreenMapper.class, "pixel_width:"+pixel_width); 
        
    }
    
    /**
     *
     */
    public double getRatio(int chartLevel, Component c){
        DvaLogger.debug(ScreenMapper.class, "chartLevel:"+chartLevel+", index:"+(visualAcuityCharts.length - chartLevel));
        //we will assume the scaling factor is the same on vertical and horizontal axis
        double va = visualAcuityCharts[visualAcuityCharts.length - chartLevel]; 
        DvaLogger.debug(ScreenMapper.class, "va:"+va); 
        double character_width = va2size(va, patientDistance, Units.M); 
        DvaLogger.debug(ScreenMapper.class, "character_width:"+character_width); 
        double numberOfPixel = millimeterAsPixel(character_width, c);//character_width / pixel_width;
        DvaLogger.debug(ScreenMapper.class, "numberOfPixel:"+numberOfPixel); 
        //double ratio =  numberOfPixel / (characterResolution * 14);
        double ratio =  numberOfPixel / characterResolution;
        DvaLogger.debug(ScreenMapper.class, "ratio:"+ratio); 
        return ratio; 
    }
    
    /**
     * Converts Inches and returns pixels using the specified resolution.
     * 
     * @param in         the Inches
     * @param component  the component that provides the graphics object
     * @return the given Inches as pixels
     */
    public int inchAsPixel(double in, Component component) {
        return inchAsPixel(in, getScreenResolution(component));
    }
    

    /**
     * Converts Millimeters and returns pixels using the resolution of the
     * given component's graphics object.
     * 
     * @param mm            Millimeters
     * @param component    the component that provides the graphics object
     * @return the given Millimeters as pixels
     */
    public int millimeterAsPixel(double mm, Component component) {
        return millimeterAsPixel(mm, getScreenResolution(component));
    }
    

    /**
     * Converts Centimeters and returns pixels using the resolution of the
     * given component's graphics object.
     * 
     * @param cm            Centimeters
     * @param component    the component that provides the graphics object
     * @return the given Centimeters as pixels
     */
    public int centimeterAsPixel(double cm, Component component) {
        return centimeterAsPixel(cm, getScreenResolution(component));
    }
    
    /**
     * Converts Inches and returns pixels using the specified resolution.
     * 
     * @param in    the Inches
     * @param dpi   the resolution
     * @return the given Inches as pixels
     */
    protected final int inchAsPixel(double in, int dpi) {
        return (int) Math.round(dpi * in);
    }
    

    /**
     * Converts Millimeters and returns pixels using the specified resolution.
     * 
     * @param mm    Millimeters
     * @param dpi   the resolution
     * @return the given Millimeters as pixels
     */
    protected final int millimeterAsPixel(double mm, int dpi) {
        return (int) Math.round(dpi * mm * 10 / 254);
    }

    
    /**
     * Converts Centimeters and returns pixels using the specified resolution.
     * 
     * @param cm    Centimeters
     * @param dpi   the resolution
     * @return the given Centimeters as pixels
     */
    protected final int centimeterAsPixel(double cm, int dpi) {
        return (int) Math.round(dpi * cm * 100 / 254);
    }
    
    /**
     * Returns the components screen resolution or the default screen
     * resolution if the component is null or has no toolkit assigned yet.
     * 
     * @param c the component to ask for a toolkit
     * @return the component's screen resolution
     */
    protected int getScreenResolution(Component c) {
        if (c == null)
            return getDefaultScreenResolution();
            
        Toolkit toolkit = c.getToolkit();
        return toolkit != null
            ? toolkit.getScreenResolution()
            : getDefaultScreenResolution();
    }
    
    
    private static int defaultScreenResolution = -1;
    
    
    /**
     * Computes and returns the default resolution.
     * 
     * @return the default screen resolution
     */
    protected int getDefaultScreenResolution() {
        if (defaultScreenResolution == -1) {
            defaultScreenResolution = 
                Toolkit.getDefaultToolkit().getScreenResolution();
        }
        return defaultScreenResolution;
    }
    
    /**
     *
     */
    public GraphicsDevice getOutputGraphicsDevice(){
        return outputGraphicsDevice; 
    }
    
    /**
     *
     */
    public void detectOutputScreen(){
        //get graphic env.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        //get screen devices
        GraphicsDevice gs[] = ge.getScreenDevices();
        
        DvaLogger.debug("There are " + gs.length + " screens available");
        
        if (gs.length == 1){
            DvaLogger.info(ScreenMapper.class, "One output device available"); 
            outputGraphicsDevice = gs[0]; 
            
        } else if (gs.length == 2){
            DvaLogger.info(ScreenMapper.class, "Two output devices available - Device 2 selected"); 
            //assume the desired output device is the second on
            outputGraphicsDevice = gs[1]; 
            
        } else {
            //more than 2 available output device - ask operator
            // TO BE IMPLEMENTED
            
            //by default the second one is selected
            outputGraphicsDevice = gs[1]; 
            DvaLogger.info(ScreenMapper.class, "More than two output devices available - Device 2 selected");
        }
    }
    
}
