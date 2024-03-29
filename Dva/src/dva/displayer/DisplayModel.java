/*
 * DisplayModel.java
 *
 * Created on May 7, 2007, 5:14 PM
 *
 */

package dva.displayer;

import dva.acuitytest.AcuityTestConvergenceException;
import dva.acuitytest.AcuityTestDivergenceException;
import dva.acuitytest.AcuityTestManager;
import dva.acuitytest.AcuityTestMaxStepException;
import dva.util.DvaLogger;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Observable;

/**
 *
 * @author J-Chris
 */
public class DisplayModel extends Observable implements ComponentListener {

    /**
     * Creates a new instance of DisplayModel
     */
    public DisplayModel() {
        setDefault();
    }
    
    public Element getCurrentDisplayedElement(){
        return currentElement; 
    }
    
    public void updateX(int x){
        currentElement.setX(x); 
        
        this.x = x; 
        
        setChanged(); 
        notifyObservers();
    }
    
    public void updateY(int y){
        currentElement.setY(y); 
        
        this.y = y; 
        
        setChanged(); 
        notifyObservers();
    }
    
    public void update(int x, int y){
        currentElement.setX(x); 
        currentElement.setY(y); 
        
        this.x = x; 
        this.y = y; 
        
        setChanged(); 
        notifyObservers();
    }
    
    public void update(Element element, int size){
        //keep current position
        element.setX(currentElement.getX());
        element.setY(currentElement.getY());
        
        this.currentElement = element; 
        
        setChanged(); 
        notifyObservers();
    }
    
    public void enableCallibration(){
        currentElement = new Optotype(true);
    }
    
    public boolean setupAcuityTest(File patientdir) {
        try {
            AcuityTestManager.reset(); 
            AcuityTestManager.setNextAcuityTest(patientdir);
            setMessageToDisplay(resourceBundle.getString("message.displayer.patientready"));
            return true; 
            
        } catch (Exception e){
            DvaLogger.error(DisplayModel.class, e); 
            return false; 
        }
    }
    
    public State getState(){
        return currentState; 
    }
    
    public Element notifyOperatorEvent(OperatorEvent operatorEvent) {
        try{
            //if no acuitytest is available, exit
            if (AcuityTestManager.getCurrentAcuityTest() == null) return null;

            DvaLogger.debug(DisplayModel.class, "state:"+currentState); 

            if (currentState == State.INIT){ 

                if (operatorEvent == OperatorEvent.NEXT_OPTOTYPE){
                    //save time
                    this.savedTime = System.currentTimeMillis(); 

                    //disable message
                    disableMessage(); 
                    disableImage();

                    //display next character
                    currentElement = AcuityTestManager.getCurrentAcuityTest().getNext();

                    //set new state
                    this.currentState = State.TESTING; 
                }

            } else if (currentState == State.TESTING){

                //compute answertime
                answerTime = savedTime - System.currentTimeMillis(); 

                //save patient response
                if (operatorEvent != OperatorEvent.NEXT_OPTOTYPE){
                    //should be an 'OPTOTYPE_' event
                    this.patientAnswerStr = operatorEvent.toString(); 
                    this.patientAnswer = patientAnswerStr.equals(currentElement.toString()); 
                }

                //save patient answer
                AcuityTestManager.getCurrentAcuityTest().saveAnswer(answerTime, this.currentElement, patientAnswer, patientAnswerStr);

                //update status
                AcuityTestManager.updateStatus(); 

                if (AcuityTestManager.getStatus() == AcuityTestManager.Status.TEST_RUNNING) {
                    //if there is a pause between each character
                    if (pauseBetween){

                        //display ready message
                        setMessageToDisplay(resourceBundle.getString("message.displayer.patientready"));

                        //set new state
                        this.currentState = State.PAUSE; 

                    } else {

                        disableMessage(); 
                        disableImage();

                        //display next character
                        currentElement = AcuityTestManager.getCurrentAcuityTest().getNext();
                    } 
                } else if (AcuityTestManager.getStatus() == AcuityTestManager.Status.TEST_DONE){
                    //update displayer
                    setMessageToDisplay(resourceBundle.getString("message.displayer.patientready"));
                }

            } else if (currentState == State.PAUSE){

                if (operatorEvent == OperatorEvent.NEXT_OPTOTYPE){
                    disableMessage(); 
                    disableImage();

                    //display next character
                    currentElement = AcuityTestManager.getCurrentAcuityTest().getNext();

                    //set new state
                    this.currentState = State.TESTING; 
                }
            }

            //notify ModelView
            setChanged(); 
            notifyObservers(DisplayModel.EventType.OPERATOR_EVENT);

            DvaLogger.debug(DisplayModel.class, "currentState:"+currentState); 
            
            return this.currentElement; 
            
        } catch (AcuityTestConvergenceException atcex){
            AcuityTestManager.getCurrentAcuityTest().toFile();
            setMessageToDisplay(atcex.getMessage());
            DvaLogger.error(DisplayModel.class, atcex.getMessage()); 
            
        } catch (AcuityTestDivergenceException atdex){
            AcuityTestManager.getCurrentAcuityTest().toFile();
            setMessageToDisplay(atdex.getMessage());
            DvaLogger.error(DisplayModel.class, atdex.getMessage()); 
            
        } catch (AcuityTestMaxStepException atmsex) {
            AcuityTestManager.getCurrentAcuityTest().toFile();
            setMessageToDisplay(atmsex.getMessage());
            DvaLogger.error(DisplayModel.class, atmsex.getMessage()); 
            
        } finally {
            return null; 
        }
        
        
    }
    
    public void setPauseBetween(boolean pauseBetween){
        this.pauseBetween = pauseBetween; 
    }
    
    public BufferedImage getImage(){
        return bimg; 
    }
    
    public void disableImage(){
        this.image = false; 
        this.bimg = null; 
    }
    
     public void displayImage(BufferedImage bimg){
        this.bimg = bimg; 
        this.image = true; 
        
        //notify ModelView
        setChanged(); 
        notifyObservers(DisplayModel.EventType.DISPLAY_IMAGE);
    }
    
    public void setMessageToDisplay(String messageToDisplay){
        this.messageToDisplay = messageToDisplay; 
        this.message = true; 
        
        //notify ModelView
        setChanged(); 
        notifyObservers(DisplayModel.EventType.DISPLAY_MESSAGE);
    }
    
    public void disableMessage(){
        this.message = false; 
    }
    
    public String getMessageToDisplay(){
        return messageToDisplay; 
    }
    
    public Font getMessageFont(){
        return messageFont; 
    }
    
    public void setMessageFont(Font messageFont){
        this.messageFont = messageFont; 
    }
    
    public Color getMessageColor(){
        return this.messageColor; 
    }
    
    public void setMessageColor(Color messageColor){
        this.messageColor = messageColor; 
    }
    
    public boolean isMessage(){
        return this.message; 
    }
    
    public boolean isImage(){
        return this.image; 
    }
    
    public int getX(){
        return x; 
    }
    
    public int getY(){
        return y; 
    }
    
    public void setDefault(){
        x = resourceBundle.getInt("config.displayer.defaultX"); 
        y = resourceBundle.getInt("config.displayer.defaultY"); 
    }
    
    public double getScaleCorrectionFactor() {
        return scaleCorrectionFactor;
    }

    public void setScaleCorrectionFactor(double scaleCorrectionFactor) {
        this.scaleCorrectionFactor = scaleCorrectionFactor;
        
        //notify ModelView
        setChanged(); 
        notifyObservers(DisplayModel.EventType.SCALING);
    }
    
    public void componentHidden(ComponentEvent e){
        //do nothing
    }
    public void componentMoved(ComponentEvent e){
        //do nothing
    }
    
    public void componentResized(ComponentEvent e){
        DvaLogger.debug(DisplayView.class, "Window resized");
        //notify ModelView
        setChanged(); 
        notifyObservers(DisplayModel.EventType.RESIZED_WINDOW);
        
    }
    public void componentShown(ComponentEvent e) {
        //do nothing
    }
   
    //state machine attributes
    public enum OperatorEvent {NEXT_OPTOTYPE, OPTOTYPE_DONTKNOW, OPTOTYPE_C, OPTOTYPE_D, OPTOTYPE_H, OPTOTYPE_K, OPTOTYPE_N, OPTOTYPE_O, OPTOTYPE_R, OPTOTYPE_S, OPTOTYPE_V, OPTOTYPE_Z }; 
    public enum State { INIT, TESTING, PAUSE, END }; 
    public enum EventType { DISPLAY_MESSAGE, DISPLAY_IMAGE, OPERATOR_EVENT, SCALING, RESIZED_WINDOW}; 
    boolean pauseBetween = false; 
    private State currentState = State.INIT; 
    
    //patient asnwer specific attribute
    private long savedTime; 
    private long answerTime; 
    private boolean patientAnswer = true; 
    private String patientAnswerStr = ""; 
    
    //Graphics attribute
    private int x; 
    private int y; 
    private Element currentElement = new Optotype(true); 
    private double scaleCorrectionFactor = 1; 
    
    //image attribute
    private BufferedImage bimg = null; 
    private boolean image = false; 
    
    //message attributes
    private String messageToDisplay = ""; 
    private boolean message = false; 
    private Font messageFont =  new Font("Tahoma", Font.PLAIN, 40);
    private Color messageColor = Color.BLACK; 
    
    //resources
    private dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N; 
    
}
