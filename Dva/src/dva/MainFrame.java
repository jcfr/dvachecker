/*
 * MainFrame.java
 *
 * Created on May 7, 2007, 5:52 AM
 */

package dva;

import dva.actions.CallibrationAction;
import dva.acuitytest.AcuityTestManager;
import dva.acuitytest.AcuityTestException;
import dva.displayer.DisplayModel;
import dva.displayer.Displayer;
import dva.displayer.Element;
import dva.util.DvaLogger;
import dva.util.GUIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 *
 * @author  J-Chris
 */
public class MainFrame extends javax.swing.JFrame implements Observer {
    
    
    /**
     * @param o
     * @param object
     */
    public void update(Observable o, Object object){

        DisplayModel.EventType eventType= (DisplayModel.EventType)object; 
        
        if (!callibrating && eventType==DisplayModel.EventType.OPERATOR_EVENT ){
            AcuityTestManager.Status status = AcuityTestManager.getStatus();

            if ( status == AcuityTestManager.Status.TEST_RUNNING || status == AcuityTestManager.Status.INIT){
                if (displayer.getDisplayModel().getState() == DisplayModel.State.PAUSE){
                    //jLabelClickArea.setText(resourceBundle.getString("message.clickarea.continue")); 
                    
                    //enable the next button
                    this.jButtonDisplayNextOptotype.setEnabled(true); 

                } else if (displayer.getDisplayModel().getState() == DisplayModel.State.TESTING){
                    //jLabelClickArea.setText(resourceBundle.getString("message.clickarea.waitanswer")); 
                    //enable the next button
                    this.jButtonDisplayNextOptotype.setEnabled(false); 
                }

            } else if ( status == AcuityTestManager.Status.TEST_FAILED ){
                DvaLogger.debug(MainFrame.class, "TEST_FAILED");
                String[] options = {"Continue", "Abort"}; 
                int n = JOptionPane.showOptionDialog(this,
                        resourceBundle.getString("message.acuitytest."+AcuityTestManager.getAcuityTest().getTestName()+".failed"),
                        "Test Failure",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (n == JOptionPane.YES_OPTION){
                    //abort experiment
                    DvaLogger.debug(MainFrame.class, "Abort");
                } else {
                    //continue experiment
                    DvaLogger.debug(MainFrame.class, "Continue");
                }

            }  else if ( status == AcuityTestManager.Status.TEST_DONE ){
                DvaLogger.debug(MainFrame.class, "TEST_DONE");

                //update click area
                //jLabelClickArea.setText(resourceBundle.getString("message.clickarea.continue")); 

                String finishedTestName = AcuityTestManager.getAcuityTestName().toUpperCase();

                JOptionPane.showMessageDialog(this, resourceBundle.getString("message.acuitytest.finished", finishedTestName));

                AcuityTestManager.setNextAcuityTest(); 

                JOptionPane.showMessageDialog(this, AcuityTestManager.getAcuityTest().getOperatorInstruction() ); 

            }  else if ( status == AcuityTestManager.Status.ALL_TEST_DONE ){
                DvaLogger.debug(MainFrame.class, "ALL_TEST_DONE");

            }
        }
    }
    
    // specific methods
    /**
     * Return the current patient invloved in the experiment
     */
    Patient getCurrentPatient(){
        return patient; 
    }
    
    /**
     * Coninent method to update the Patient data label
     */
    void updateJLabelPatientData(Patient patient){
        DvaLogger.debug("Updated patient data: \"" + patient.toString() + "\"");
        this.jLabelPatientName.setText( patient.getLastname() + " " + patient.getFirstname() );
        this.jLabelPatientSex.setText( patient.getSex() ); 
        this.jLabelPatientAge.setText( patient.getAge() );
        this.jTextAreaPatientComment.setText(patient.getComment()); 
    }
    
    
    // Specific actions and listerners
    
    /**
     * New Experiment action
     */
    public class NewExperimentAction extends AbstractAction {
        
        public NewExperimentAction(String text, String icon, String desc) {
            super(text, GUIUtils.createNavigationIcon(icon));
            putValue(SHORT_DESCRIPTION, desc);
        }
        
        public void actionPerformed(ActionEvent e) {
            
            //propose a speeds set
            int speeds[] = AcuityTestManager.proposeSpeedSet(); 
            //jLabelDialogPatientSpeedsSetValue.setText( AcuityTestManager.speedsSetToString(speeds) ); 
            
            //show new patient dialog
            GUIUtils.showDialog(jDialogPatientData, true, e);
        }
    }

    /**
     * MouseListener allowing to distinguish between Left and right click of the user
     */
    public class OperatorClickMouseListener implements MouseListener {
        
        public void mouseClicked(MouseEvent e){
            
            if (!enableClickArea) return; 
            
            //DvaLogger.debug(MainFrame.class, "MouseClicked:" + e.getButton()+", ModelState:"+displayer.getDisplayModel().getState()); 

            //try {
                if (e.getButton() == MouseEvent.BUTTON1){
                    //left button on a right-handed mouse

                    //Element element = displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.LEFT_CLICK);
                    
                    //update character position and orientation label
                    //jLabelCharacter.setText(element.toString()); 
                    //jLabelOrientation.setText(element.getOrientation().toString()); 
                    

                } else if (e.getButton() == MouseEvent.BUTTON3) { 
                    //right button on a right-handed mouse

                    //Element element = displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.RIGHT_CLICK);

                    //update character position and orientation label
                    //jLabelCharacter.setText(element.toString()); 
                    //jLabelOrientation.setText(element.getOrientation().toString());
                }
                
            //} catch (AcuityTestException atmsex){
            //    DvaLogger.error(MainFrame.class, atmsex); 
            //}

        }
        
        public void mousePressed(MouseEvent e){
            if (!enableClickArea) return; 
            
            DvaLogger.debug("MousePressed:" + e.getButton()+", ModelState:"+displayer.getDisplayModel().getState()); 
            
            if (displayer.getDisplayModel().getState() == DisplayModel.State.TESTING){
                if (e.getButton() == MouseEvent.BUTTON1){
                    //jPanelClickArea.setBackground(Color.GREEN); 

                } else if (e.getButton() == MouseEvent.BUTTON3){
                    //jPanelClickArea.setBackground(Color.RED); 
                }
            }
   
        }
        
        public void mouseReleased(MouseEvent e){
            if (!enableClickArea) return; 
            //jPanelClickArea.setBackground(Color.DARK_GRAY);
        }
        
        public void mouseEntered(MouseEvent e){
            if (!enableClickArea) return; 
            DvaLogger.debug("Mouse ENTERED validation area");
            
            //jPanelClickArea.setBackground(Color.DARK_GRAY);
        }
        
        public void mouseExited(MouseEvent e) {
            if (!enableClickArea) return; 
            DvaLogger.debug("Mouse EXITED validation area");
            
            //jPanelClickArea.setBackground(jLabelClickAreaBackgroundColor);
        }
    }
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
         
        initComponents();
        
        //init logger
        DvaLogger.initLogger(jTextAreaLog); 
        
        //save jLabelClickArea background color
        //jLabelClickAreaBackgroundColor = jLabelClickArea.getBackground();
        
        //jLabelClickArea.addMouseListener(new OperatorClickMouseListener()); 
        
        //create displayer
        displayer = new Displayer();
        
        displayer.getDisplayModel().addObserver(this); 
        
        AcuityTestManager.proposeSpeedSet();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) { frameSize.height = screenSize.height; }
        if (frameSize.width > screenSize.width) { frameSize.width = screenSize.width; }
        this.setLocation( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jDialogPatientData = new javax.swing.JDialog();
        jPanelDialogPatientData = new javax.swing.JPanel();
        jLabelDialogPatientSex = new javax.swing.JLabel();
        jRadioButtonDialogPatientSexM = new javax.swing.JRadioButton();
        jRadioButtonDialogPatientSexF = new javax.swing.JRadioButton();
        jLabelDialogPatientAge = new javax.swing.JLabel();
        jTextFieldDialogPatientAge = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldDialogPatientLastname = new javax.swing.JTextField();
        jTextFieldDialogPatientFirstname = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaDialogPatientComment = new javax.swing.JTextArea();
        jButtonPatientOk = new javax.swing.JButton();
        jButtonPatientCancel = new javax.swing.JButton();
        buttonGroupDialogPatientSex = new javax.swing.ButtonGroup();
        jDialogAbout = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanelPatientData = new javax.swing.JPanel();
        jLabelPatientSex = new javax.swing.JLabel();
        jLabelPatientName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabelPatientAge = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaPatientComment = new javax.swing.JTextArea();
        jPanelAcuityTest = new javax.swing.JPanel();
        jButtonStartAcuityTest = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabelTreadmillSpeed = new javax.swing.JLabel();
        jLabelPatientSpeedsSetValue = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabelAcuityTestDateTime = new javax.swing.JLabel();
        jPanelResultsValidation = new javax.swing.JPanel();
        jPanelDisplayedCharacter = new javax.swing.JPanel();
        jButtonOptotypeC = new javax.swing.JButton();
        jButtonOptotypeD = new javax.swing.JButton();
        jButtonOptotypeH = new javax.swing.JButton();
        jButtonOptotypeK = new javax.swing.JButton();
        jButtonOptotypeN = new javax.swing.JButton();
        jButtonOptotypeO = new javax.swing.JButton();
        jButtonOptotypeR = new javax.swing.JButton();
        jButtonOptotypeS = new javax.swing.JButton();
        jButtonOptotypeV = new javax.swing.JButton();
        jButtonOptotypeZ = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabelCharacter = new javax.swing.JLabel();
        jButtonDisplayNextOptotype = new javax.swing.JButton();
        jPanelLog = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaLog = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNewExperiment = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemQuit = new javax.swing.JMenuItem();
        jMenuOptions = new javax.swing.JMenu();
        jMenuCallibration = new javax.swing.JMenuItem();
        jCheckBoxMenuItemPauseBetween = new javax.swing.JCheckBoxMenuItem();
        jMenuView = new javax.swing.JMenu();
        jMenuViewDisplayer = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuAbout = new javax.swing.JMenuItem();

        jDialogPatientData.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialogPatientData.setTitle("Enter patient data");
        jDialogPatientData.setModal(true);
        jPanelDialogPatientData.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient data"));
        jLabelDialogPatientSex.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDialogPatientSex.setText("Sex: ");

        buttonGroupDialogPatientSex.add(jRadioButtonDialogPatientSexM);
        jRadioButtonDialogPatientSexM.setSelected(true);
        jRadioButtonDialogPatientSexM.setText("M");
        jRadioButtonDialogPatientSexM.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonDialogPatientSexM.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonDialogPatientSexM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDialogPatientSexMActionPerformed(evt);
            }
        });

        buttonGroupDialogPatientSex.add(jRadioButtonDialogPatientSexF);
        jRadioButtonDialogPatientSexF.setText("F");
        jRadioButtonDialogPatientSexF.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonDialogPatientSexF.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabelDialogPatientAge.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDialogPatientAge.setText("Age: ");

        jTextFieldDialogPatientAge.setText("20");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("LastName: ");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("FirstName: ");

        jLabel12.setText("Comment:");

        jTextAreaDialogPatientComment.setColumns(15);
        jTextAreaDialogPatientComment.setFont(new java.awt.Font("Tahoma", 0, 12));
        jTextAreaDialogPatientComment.setRows(3);
        jScrollPane3.setViewportView(jTextAreaDialogPatientComment);

        org.jdesktop.layout.GroupLayout jPanelDialogPatientDataLayout = new org.jdesktop.layout.GroupLayout(jPanelDialogPatientData);
        jPanelDialogPatientData.setLayout(jPanelDialogPatientDataLayout);
        jPanelDialogPatientDataLayout.setHorizontalGroup(
            jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel12)
                    .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(jLabelDialogPatientSex, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
                    .add(jLabelDialogPatientAge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                        .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextFieldDialogPatientFirstname, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                            .add(jTextFieldDialogPatientLastname, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                        .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelDialogPatientDataLayout.createSequentialGroup()
                                .add(jRadioButtonDialogPatientSexM)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jRadioButtonDialogPatientSexF))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelDialogPatientDataLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                                    .add(jTextFieldDialogPatientAge))))
                        .addContainerGap())))
        );
        jPanelDialogPatientDataLayout.setVerticalGroup(
            jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextFieldDialogPatientLastname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(jTextFieldDialogPatientFirstname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButtonDialogPatientSexM)
                    .add(jRadioButtonDialogPatientSexF)
                    .add(jLabelDialogPatientSex))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelDialogPatientAge)
                    .add(jTextFieldDialogPatientAge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelDialogPatientDataLayout.createSequentialGroup()
                        .add(jLabel12)
                        .addContainerGap(44, Short.MAX_VALUE))
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)))
        );

        jButtonPatientOk.setText("Ok");
        jButtonPatientOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPatientOkActionPerformed(evt);
            }
        });

        jButtonPatientCancel.setText("Cancel");
        jButtonPatientCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPatientCancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jDialogPatientDataLayout = new org.jdesktop.layout.GroupLayout(jDialogPatientData.getContentPane());
        jDialogPatientData.getContentPane().setLayout(jDialogPatientDataLayout);
        jDialogPatientDataLayout.setHorizontalGroup(
            jDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDialogPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap(193, Short.MAX_VALUE)
                .add(jButtonPatientOk)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonPatientCancel)
                .addContainerGap())
        );
        jDialogPatientDataLayout.setVerticalGroup(
            jDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelDialogPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDialogPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonPatientCancel)
                    .add(jButtonPatientOk))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialogAbout.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialogAbout.setTitle("About");
        jDialogAbout.setModal(true);
        jLabel4.setText("Roberto Cardona");

        jLabel5.setText("Jean-Christophe Fillion-Robin");

        org.jdesktop.layout.GroupLayout jDialogAboutLayout = new org.jdesktop.layout.GroupLayout(jDialogAbout.getContentPane());
        jDialogAbout.getContentPane().setLayout(jDialogAboutLayout);
        jDialogAboutLayout.setHorizontalGroup(
            jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogAboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(jLabel5))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialogAboutLayout.setVerticalGroup(
            jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogAboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dynamic Visual Acuity Checker");
        setResizable(false);
        jPanelPatientData.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient data"));
        jLabelPatientSex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel6.setForeground(new java.awt.Color(51, 94, 168));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Name:");

        jLabel7.setForeground(new java.awt.Color(51, 94, 168));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Sex:");

        jLabel9.setForeground(new java.awt.Color(51, 94, 168));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Age:");

        jLabel11.setForeground(new java.awt.Color(51, 94, 168));
        jLabel11.setText("Comment:");

        jTextAreaPatientComment.setColumns(15);
        jTextAreaPatientComment.setEditable(false);
        jTextAreaPatientComment.setFont(new java.awt.Font("Tahoma", 0, 12));
        jTextAreaPatientComment.setRows(3);
        jTextAreaPatientComment.setTabSize(4);
        jScrollPane2.setViewportView(jTextAreaPatientComment);

        org.jdesktop.layout.GroupLayout jPanelPatientDataLayout = new org.jdesktop.layout.GroupLayout(jPanelPatientData);
        jPanelPatientData.setLayout(jPanelPatientDataLayout);
        jPanelPatientDataLayout.setHorizontalGroup(
            jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelPatientDataLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .add(jLabel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelPatientDataLayout.createSequentialGroup()
                        .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabelPatientAge)
                            .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jLabelPatientSex)
                                .add(jLabelPatientName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)))
                        .add(85, 85, 85))
                    .add(jPanelPatientDataLayout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanelPatientDataLayout.setVerticalGroup(
            jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelPatientDataLayout.createSequentialGroup()
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelPatientName)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelPatientSex)
                    .add(jLabel7))
                .add(5, 5, 5)
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelPatientAge)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelPatientDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel11)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelAcuityTest.setBorder(javax.swing.BorderFactory.createTitledBorder("Acuity Test"));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dva/Bundle"); // NOI18N
        jButtonStartAcuityTest.setText(bundle.getString("button.mainframe.startacuitytest")); // NOI18N
        jButtonStartAcuityTest.setEnabled(false);
        jButtonStartAcuityTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartAcuityTestActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(51, 94, 168));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Treadmill speed:");

        jLabel8.setText("km/h");

        jLabelTreadmillSpeed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTreadmillSpeed.setText("0");

        jLabelPatientSpeedsSetValue.setText(" ");

        jLabel13.setForeground(new java.awt.Color(51, 94, 168));
        jLabel13.setText("Date / Time:");

        jLabelAcuityTestDateTime.setText(" ");

        org.jdesktop.layout.GroupLayout jPanelAcuityTestLayout = new org.jdesktop.layout.GroupLayout(jPanelAcuityTest);
        jPanelAcuityTest.setLayout(jPanelAcuityTestLayout);
        jPanelAcuityTestLayout.setHorizontalGroup(
            jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelAcuityTestLayout.createSequentialGroup()
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelAcuityTestLayout.createSequentialGroup()
                        .add(111, 111, 111)
                        .add(jLabelPatientSpeedsSetValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                    .add(jPanelAcuityTestLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jButtonStartAcuityTest, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                            .add(jPanelAcuityTestLayout.createSequentialGroup()
                                .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelTreadmillSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))))
                    .add(jPanelAcuityTestLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabelAcuityTestDateTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelAcuityTestLayout.setVerticalGroup(
            jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelAcuityTestLayout.createSequentialGroup()
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(jLabelAcuityTestDateTime))
                .add(6, 6, 6)
                .add(jButtonStartAcuityTest)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelAcuityTestLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelTreadmillSpeed)
                    .add(jLabel8)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabelPatientSpeedsSetValue)
                .addContainerGap())
        );

        jPanelResultsValidation.setBorder(javax.swing.BorderFactory.createTitledBorder("Operator real-time results validation"));
        jPanelDisplayedCharacter.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient answer"));
        jButtonOptotypeC.setText("C");
        jButtonOptotypeC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeCActionPerformed(evt);
            }
        });

        jButtonOptotypeD.setText("D");
        jButtonOptotypeD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeDActionPerformed(evt);
            }
        });

        jButtonOptotypeH.setText("H");
        jButtonOptotypeH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeHActionPerformed(evt);
            }
        });

        jButtonOptotypeK.setText("K");
        jButtonOptotypeK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeKActionPerformed(evt);
            }
        });

        jButtonOptotypeN.setText("N");
        jButtonOptotypeN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeNActionPerformed(evt);
            }
        });

        jButtonOptotypeO.setText("O");
        jButtonOptotypeO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeOActionPerformed(evt);
            }
        });

        jButtonOptotypeR.setText("R");
        jButtonOptotypeR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeRActionPerformed(evt);
            }
        });

        jButtonOptotypeS.setText("S");
        jButtonOptotypeS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeSActionPerformed(evt);
            }
        });

        jButtonOptotypeV.setText("V");
        jButtonOptotypeV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeVActionPerformed(evt);
            }
        });

        jButtonOptotypeZ.setText("Z");
        jButtonOptotypeZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptotypeZActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelDisplayedCharacterLayout = new org.jdesktop.layout.GroupLayout(jPanelDisplayedCharacter);
        jPanelDisplayedCharacter.setLayout(jPanelDisplayedCharacterLayout);
        jPanelDisplayedCharacterLayout.setHorizontalGroup(
            jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelDisplayedCharacterLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonOptotypeC)
                    .add(jButtonOptotypeO))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelDisplayedCharacterLayout.createSequentialGroup()
                        .add(jButtonOptotypeD)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonOptotypeH)
                        .add(6, 6, 6)
                        .add(jButtonOptotypeK)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonOptotypeN))
                    .add(jPanelDisplayedCharacterLayout.createSequentialGroup()
                        .add(jButtonOptotypeR)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonOptotypeS)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonOptotypeV)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonOptotypeZ)))
                .addContainerGap())
        );
        jPanelDisplayedCharacterLayout.setVerticalGroup(
            jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelDisplayedCharacterLayout.createSequentialGroup()
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonOptotypeC)
                    .add(jButtonOptotypeD)
                    .add(jButtonOptotypeH)
                    .add(jButtonOptotypeK)
                    .add(jButtonOptotypeN))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDisplayedCharacterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonOptotypeO)
                    .add(jButtonOptotypeR)
                    .add(jButtonOptotypeS)
                    .add(jButtonOptotypeV)
                    .add(jButtonOptotypeZ))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel3.setForeground(new java.awt.Color(51, 94, 168));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Character:");

        jLabelCharacter.setFont(new java.awt.Font("Tahoma", 0, 18));
        jLabelCharacter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCharacter.setText("0");

        jButtonDisplayNextOptotype.setText("Next");
        jButtonDisplayNextOptotype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisplayNextOptotypeActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelResultsValidationLayout = new org.jdesktop.layout.GroupLayout(jPanelResultsValidation);
        jPanelResultsValidation.setLayout(jPanelResultsValidationLayout);
        jPanelResultsValidationLayout.setHorizontalGroup(
            jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelResultsValidationLayout.createSequentialGroup()
                .add(jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanelResultsValidationLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanelDisplayedCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanelResultsValidationLayout.createSequentialGroup()
                                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabelCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))))
                    .add(jPanelResultsValidationLayout.createSequentialGroup()
                        .add(115, 115, 115)
                        .add(jButtonDisplayNextOptotype)))
                .addContainerGap())
        );
        jPanelResultsValidationLayout.setVerticalGroup(
            jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelResultsValidationLayout.createSequentialGroup()
                .add(jPanelResultsValidationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabelCharacter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelDisplayedCharacter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonDisplayNextOptotype)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanelLog.setBorder(javax.swing.BorderFactory.createTitledBorder("Log"));
        jTextAreaLog.setColumns(20);
        jTextAreaLog.setEditable(false);
        jTextAreaLog.setLineWrap(true);
        jTextAreaLog.setRows(5);
        jScrollPane1.setViewportView(jTextAreaLog);

        org.jdesktop.layout.GroupLayout jPanelLogLayout = new org.jdesktop.layout.GroupLayout(jPanelLog);
        jPanelLog.setLayout(jPanelLogLayout);
        jPanelLogLayout.setHorizontalGroup(
            jPanelLogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLogLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLogLayout.setVerticalGroup(
            jPanelLogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLogLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                .addContainerGap())
        );

        jMenuFile.setText("File");
        jMenuFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFileActionPerformed(evt);
            }
        });

        jMenuItemNewExperiment.setAction(new NewExperimentAction("New Experiment", "newexp24", "Create a new experiment"));
        jMenuItemNewExperiment.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuFile.add(jMenuItemNewExperiment);

        jMenuFile.add(jSeparator1);

        jMenuItemQuit.setText("Quit");
        jMenuFile.add(jMenuItemQuit);

        jMenuBar1.add(jMenuFile);

        jMenuOptions.setText("Options");
        jMenuCallibration.setAction(new CallibrationAction("Callibrate Displayer", "calibrate24", "Callibrate Displayer"));
        jMenuOptions.add(jMenuCallibration);

        jCheckBoxMenuItemPauseBetween.setText("Set Pauses");
        jCheckBoxMenuItemPauseBetween.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemPauseBetweenActionPerformed(evt);
            }
        });

        jMenuOptions.add(jCheckBoxMenuItemPauseBetween);

        jMenuBar1.add(jMenuOptions);

        jMenuView.setText("View");
        jMenuViewDisplayer.setText("Show/Hide \"DVA Displayer\"");
        jMenuViewDisplayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuViewDisplayerActionPerformed(evt);
            }
        });

        jMenuView.add(jMenuViewDisplayer);

        jMenuBar1.add(jMenuView);

        jMenuHelp.setText("Help");
        jMenuHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuHelpActionPerformed(evt);
            }
        });

        jMenuAbout.setText("About");
        jMenuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAboutActionPerformed(evt);
            }
        });

        jMenuHelp.add(jMenuAbout);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanelLog, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanelAcuityTest, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanelPatientData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelResultsValidation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanelPatientData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanelAcuityTest, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                    .add(jPanelResultsValidation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanelLog, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOptotypeZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeZActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_Z);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeZActionPerformed

    private void jButtonOptotypeVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeVActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_V);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeVActionPerformed

    private void jButtonOptotypeSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeSActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_S);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeSActionPerformed

    private void jButtonOptotypeRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeRActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_R);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeRActionPerformed

    private void jButtonOptotypeOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeOActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_O);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeOActionPerformed

    private void jButtonOptotypeNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeNActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_N);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeNActionPerformed

    private void jButtonOptotypeKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeKActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_K);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeKActionPerformed

    private void jButtonOptotypeHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeHActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_H);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeHActionPerformed

    private void jButtonOptotypeDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeDActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_D);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeDActionPerformed

    private void jButtonOptotypeCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptotypeCActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.OPTOTYPE_C);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonOptotypeCActionPerformed

    private void jButtonDisplayNextOptotypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisplayNextOptotypeActionPerformed
        try{
            displayer.getDisplayModel().notifyOperatorEvent(DisplayModel.OperatorEvent.NEXT_OPTOTYPE);
        } catch (AcuityTestException e){
            DvaLogger.error(MainFrame.class, e); 
        }
    }//GEN-LAST:event_jButtonDisplayNextOptotypeActionPerformed

    private void jCheckBoxMenuItemPauseBetweenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemPauseBetweenActionPerformed
        this.displayer.getDisplayModel().setPauseBetween(jCheckBoxMenuItemPauseBetween.isSelected()); 
    }//GEN-LAST:event_jCheckBoxMenuItemPauseBetweenActionPerformed

    private void jButtonStartAcuityTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartAcuityTestActionPerformed
        //check if displayer is visible
        if (!displayer.isVisible()) displayer.setVisible(true);
        
        //setup acuitytest
        this.displayer.getDisplayModel().setupAcuityTest(); 
        
        //set clickarea message
        //jLabelClickArea.setText(resourceBundle.getString("message.clickarea.continue"));
        
        //enable click area
        enableClickArea(true);
        
        //disable start button
        this.jButtonStartAcuityTest.setEnabled(false); 
        
        //set date and time
        this.jLabelAcuityTestDateTime.setText(AcuityTestManager.getAcuityTest().getStartDateAsString());
        
    }//GEN-LAST:event_jButtonStartAcuityTestActionPerformed

    private void jMenuViewDisplayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuViewDisplayerActionPerformed
        displayer.setVisible(!displayer.isVisible());
    }//GEN-LAST:event_jMenuViewDisplayerActionPerformed

    private void jRadioButtonDialogPatientSexMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDialogPatientSexMActionPerformed
// TODO add your handling code here:
        
    }//GEN-LAST:event_jRadioButtonDialogPatientSexMActionPerformed

    private void jMenuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuHelpActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jMenuHelpActionPerformed

    private void jMenuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAboutActionPerformed
        GUIUtils.showDialog(jDialogAbout, true, evt); 
    }//GEN-LAST:event_jMenuAboutActionPerformed

    private void jMenuFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFileActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jMenuFileActionPerformed

    private void jButtonPatientCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPatientCancelActionPerformed
        GUIUtils.showDialog(jDialogPatientData, false, evt); 
    }//GEN-LAST:event_jButtonPatientCancelActionPerformed

    private void jButtonPatientOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPatientOkActionPerformed
        //get new patient data
        String firstname = this.jTextFieldDialogPatientFirstname.getText(); 
        String lastname = this.jTextFieldDialogPatientLastname.getText(); 
        String comment = this.jTextAreaDialogPatientComment.getText(); 
        String sex = GUIUtils.getSelection(buttonGroupDialogPatientSex).getText();
        String age = this.jTextFieldDialogPatientAge.getText(); 
        
        //update patient
        getCurrentPatient().setFirstname(firstname); 
        getCurrentPatient().setLastname(lastname); 
        getCurrentPatient().setComment(comment); 
        getCurrentPatient().setSex(sex); 
        getCurrentPatient().setAge(age); 
        
        //int speeds[] = AcuityTestManager.acceptProposedSpeedsSet(); 
        //jLabelPatientSpeedsSetValue.setText( AcuityTestManager.speedsSetToString(speeds) ); 
        
        
        //enable StartAcuityTest button
        jButtonStartAcuityTest.setEnabled(true); 

        
        //update GUI
        updateJLabelPatientData(getCurrentPatient());
        
        //close dialog
        GUIUtils.showDialog(jDialogPatientData, false, evt); 
    }//GEN-LAST:event_jButtonPatientOkActionPerformed
    
    public void enableClickArea(boolean state){
        this.enableClickArea = state; 
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    
    //Model
    private Patient patient = new Patient();  
    
    //GUI
    private Color jLabelClickAreaBackgroundColor = null; 
    private Displayer displayer = null; 
    private boolean enableClickArea = false; 
    private boolean callibrating = false;
    
    //resources
    private dva.util.MessageResources resourceBundle = new dva.util.MessageResources("dva/Bundle"); // NOI18N; 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupDialogPatientSex;
    private javax.swing.JButton jButtonDisplayNextOptotype;
    private javax.swing.JButton jButtonOptotypeC;
    private javax.swing.JButton jButtonOptotypeD;
    private javax.swing.JButton jButtonOptotypeH;
    private javax.swing.JButton jButtonOptotypeK;
    private javax.swing.JButton jButtonOptotypeN;
    private javax.swing.JButton jButtonOptotypeO;
    private javax.swing.JButton jButtonOptotypeR;
    private javax.swing.JButton jButtonOptotypeS;
    private javax.swing.JButton jButtonOptotypeV;
    private javax.swing.JButton jButtonOptotypeZ;
    private javax.swing.JButton jButtonPatientCancel;
    private javax.swing.JButton jButtonPatientOk;
    private javax.swing.JButton jButtonStartAcuityTest;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemPauseBetween;
    private javax.swing.JDialog jDialogAbout;
    private javax.swing.JDialog jDialogPatientData;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAcuityTestDateTime;
    private javax.swing.JLabel jLabelCharacter;
    private javax.swing.JLabel jLabelDialogPatientAge;
    private javax.swing.JLabel jLabelDialogPatientSex;
    private javax.swing.JLabel jLabelPatientAge;
    private javax.swing.JLabel jLabelPatientName;
    private javax.swing.JLabel jLabelPatientSex;
    private javax.swing.JLabel jLabelPatientSpeedsSetValue;
    private javax.swing.JLabel jLabelTreadmillSpeed;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuCallibration;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemNewExperiment;
    private javax.swing.JMenuItem jMenuItemQuit;
    private javax.swing.JMenu jMenuOptions;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JMenuItem jMenuViewDisplayer;
    private javax.swing.JPanel jPanelAcuityTest;
    private javax.swing.JPanel jPanelDialogPatientData;
    private javax.swing.JPanel jPanelDisplayedCharacter;
    private javax.swing.JPanel jPanelLog;
    private javax.swing.JPanel jPanelPatientData;
    private javax.swing.JPanel jPanelResultsValidation;
    private javax.swing.JRadioButton jRadioButtonDialogPatientSexF;
    private javax.swing.JRadioButton jRadioButtonDialogPatientSexM;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaDialogPatientComment;
    private javax.swing.JTextArea jTextAreaLog;
    private javax.swing.JTextArea jTextAreaPatientComment;
    private javax.swing.JTextField jTextFieldDialogPatientAge;
    private javax.swing.JTextField jTextFieldDialogPatientFirstname;
    private javax.swing.JTextField jTextFieldDialogPatientLastname;
    // End of variables declaration//GEN-END:variables
    
}
