/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeplus.gui;

import java.awt.Color;
import java.awt.Window;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import jeplus.EPlusConfig;
import jeplus.JEPlusConfig;
import jeplus.JEPlusFrameMain;
import jeplus.TRNSYSConfig;
import jeplus.util.PythonTools;

/**
 *
 * @author Yi
 */
public class JPanelProgConfiguration extends javax.swing.JPanel {

    protected String title = "External Executables Configuration";
    protected final JFileChooser fc = new JFileChooser("./");
    protected JEPlusConfig Config = JEPlusConfig.getDefaultInstance();
    protected String ConfigFile = null;

    protected Window HostWindow = null;
    
    /**
     * Creates new form JPanelProgConfiguration
     * @param host
     */
    public JPanelProgConfiguration(Window host) {
        initComponents();
        HostWindow = host;
    }

    public String getConfigFile() {
        return ConfigFile;
    }

    /** 
     * Set an alternative configuration to this panel
     * @param configfile 
     */
    public void setConfigFile(String configfile) {
        ConfigFile = configfile;
        Config = JEPlusConfig.getNewInstance(ConfigFile);
        initSettings();
        checkSettings();
    }

    /** 
     * Set an alternative configuration to this panel
     * @param config
     */
    public void setConfig(JEPlusConfig config) {
        Config = config;
        ConfigFile = config.getCurrentConfigFile();
        initSettings();
        checkSettings();
    }

    /**
     * initialise display from data records
     */
    public final void initSettings () {
        // Config = JEPlusConfig.getNewInstance(ConfigFile);
        txtEPlusBinDir.setText(Config.getEPlusBinDir());
        txtTrnsysBinDir.setText(Config.getTRNSYSBinDir());
        txtTrnsysEXE.setText(Config.getTRNSYSEXEC());
        this.txtPython2Exe.setText(Config.getPython2EXE() == null ? "Select Python 2 exe..." : Config.getPython2EXE());
        this.txtPython3Exe.setText(Config.getPython3EXE() == null ? "Select Python 3 exe..." : Config.getPython3EXE());
    }

    /**
     * check validity of directory and command/file names
     */
    public final void checkSettings () {
        boolean errors = false;
        StringBuilder buf = new StringBuilder ("<html>");
        File dir = new File (Config.getEPlusBinDir());
        buf.append("<p><em>EnergyPlus:</em></p>");
        if (! (dir.exists() && dir.isDirectory())) {
            txtEPlusBinDir.setForeground(Color.red);
            buf.append("<p>EnergyPlus binary folder ").append(dir.getAbsolutePath()).append(" does not exist!</p>");
        }else {
            txtEPlusBinDir.setForeground(Color.black);
            buf.append("<p>");
            File f = new File(Config.getEPlusBinDir() + EPlusConfig.getEPDefIDD());
            if (! f.exists()) {
                buf.append(f.getAbsolutePath());
                errors = true;
            }else {
                buf.append("Found EnergyPlus IDD version ").append(Config.getEPlusVersion()).append("</p><p>");
            }
            f = new File(Config.getEPlusEXEC());
            if (! f.exists()) {
                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
                errors = true;
            }
            f = new File(Config.getEPlusEPMacro());
            if (! f.exists()) {
                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
                errors = true;
            }
            f = new File(Config.getEPlusExpandObjects());
            if (! f.exists()) {
                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
                errors = true;
            }
            f = new File(Config.getEPlusReadVars());
            if (! f.exists()) {
                buf.append(!errors ? "" : ", ").append(f.getAbsolutePath());
                errors = true;
            }
            buf.append(!errors ? "" : " are missing!").append("</p>");
        }
        
        dir = new File (txtTrnsysBinDir.getText());
        buf.append("<p><em>TRNSYS:</em></p>");
        if (! (dir.exists() && dir.isDirectory())) {
            txtTrnsysBinDir.setForeground(Color.red);
            buf.append("<p>TRNSYS folder ").append(dir.getAbsolutePath()).append(" does not exist!</p>");
        } else {
            txtTrnsysBinDir.setForeground(Color.black);
        }
        File f = new File(txtTrnsysEXE.getText());
        if (! f.exists()) {
            txtTrnsysEXE.setForeground(Color.red);
            buf.append("<p>TRNSYS Executable ").append(f.getAbsolutePath()).append(" is missing!</p>");
        } else {
            txtTrnsysEXE.setForeground(Color.black);
            buf.append("<p>Found TRNSYS ").append("</p>");
        }
        buf.append("<p></p>");
        
        buf.append("<p><em>Python2:</em></p>");
        f = new File(txtPython2Exe.getText());
        if (! f.exists()) {
            txtPython2Exe.setForeground(Color.red);
            buf.append("<p>Python 2 Executable ").append(f.getAbsolutePath()).append(" is missing!</p>");
        } else {
            // Get python version with "python -V"
            String ver = PythonTools.getPythonVersion(Config, "python2");
            if (ver.startsWith("Error:")) {
                txtPython2Exe.setForeground(Color.red);
                buf.append("<p>").append(ver).append("</p>");
            }else {
                txtPython2Exe.setForeground(Color.black);
                buf.append("<p>Found ").append(ver).append("</p>");
            }
        }
        buf.append("<p></p>");

        buf.append("<p><em>Python3:</em></p>");
        f = new File(txtPython3Exe.getText());
        if (! f.exists()) {
            txtPython3Exe.setForeground(Color.red);
            buf.append("<p>Python 3 Executable ").append(f.getAbsolutePath()).append(" is missing!</p>");
        } else {
            // Get python version with "python -V"
            String ver = PythonTools.getPythonVersion(Config, "python3");
            if (ver.startsWith("Error:")) {
                txtPython3Exe.setForeground(Color.red);
                buf.append("<p>").append(ver).append("</p>");
            }else {
                txtPython3Exe.setForeground(Color.black);
                buf.append("<p>Found ").append(ver).append("</p>");
            }
        }

        buf.append("<p></p></html>");
        lblInformation.setText(buf.toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cmdSelectEPlusDir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cmdEnergyPlusDetails = new javax.swing.JButton();
        txtEPlusBinDir = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtTrnsysBinDir = new javax.swing.JTextField();
        cmdSelectTrnsysDir = new javax.swing.JButton();
        cmdSelectTRNexe = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtTrnsysEXE = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        txtPython3Exe = new javax.swing.JTextField();
        cmdSelectPython3Exe = new javax.swing.JButton();
        cmdSelectPython2Exe = new javax.swing.JButton();
        txtPython2Exe = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cmdSave = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lblInformation = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EnergyPlus"));

        cmdSelectEPlusDir.setText("...");
        cmdSelectEPlusDir.setToolTipText("Select the folder where EnergyPlus.exe and Energy+.idd are located");
        cmdSelectEPlusDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectEPlusDirActionPerformed(evt);
            }
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Energy+ Diretory:");

        cmdEnergyPlusDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jeplus/images/tool.png"))); // NOI18N
        cmdEnergyPlusDetails.setToolTipText("Check and specify individual E+ tools");
        cmdEnergyPlusDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEnergyPlusDetailsActionPerformed(evt);
            }
        });

        txtEPlusBinDir.setText("C:/EnergyPlusV2-2-0/");
        txtEPlusBinDir.setToolTipText("This is the directory where 'Energy+.idd' is located");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtEPlusBinDir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdSelectEPlusDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdEnergyPlusDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdSelectEPlusDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmdEnergyPlusDetails)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtEPlusBinDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("TRNSYS"));

        txtTrnsysBinDir.setText("C:/Program Files/Trnsys16_1/");
        txtTrnsysBinDir.setToolTipText("This is the directory where the folders 'Exe and UserLib' are located");

        cmdSelectTrnsysDir.setText("...");
        cmdSelectTrnsysDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectTrnsysDirActionPerformed(evt);
            }
        });

        cmdSelectTRNexe.setText("...");
        cmdSelectTRNexe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectTRNexeActionPerformed(evt);
            }
        });

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("TRNSYS Executable:");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("TRNSYS Diretory:");

        txtTrnsysEXE.setText("C:/Program Files/Trnsys16_1/Exe/TRNExe.exe");
        txtTrnsysEXE.setToolTipText("The command may vary within different projects of TRNSYS. Edit this field if necessary. If the executable is located in a different location, please specify the relative diretory to the TRNSYS binary directory above.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTrnsysBinDir, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTrnsysEXE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmdSelectTRNexe, 0, 1, Short.MAX_VALUE)
                    .addComponent(cmdSelectTrnsysDir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtTrnsysBinDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectTrnsysDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTrnsysEXE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cmdSelectTRNexe))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Python"));

        txtPython3Exe.setText("Select Python3 executable...");

        cmdSelectPython3Exe.setText("...");
        cmdSelectPython3Exe.setToolTipText("Select the root working directory");
        cmdSelectPython3Exe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectPython3ExeActionPerformed(evt);
            }
        });

        cmdSelectPython2Exe.setText("...");
        cmdSelectPython2Exe.setToolTipText("Select the root working directory");
        cmdSelectPython2Exe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectPython2ExeActionPerformed(evt);
            }
        });

        txtPython2Exe.setText("Select Python2 executable...");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Python 2 Executable: ");
        jLabel1.setMaximumSize(new java.awt.Dimension(116, 14));
        jLabel1.setMinimumSize(new java.awt.Dimension(116, 14));
        jLabel1.setPreferredSize(new java.awt.Dimension(116, 14));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Python 3 Executable:");
        jLabel3.setMaximumSize(new java.awt.Dimension(116, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(116, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(116, 14));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPython2Exe)
                    .addComponent(txtPython3Exe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdSelectPython2Exe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectPython3Exe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPython2Exe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectPython2Exe)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPython3Exe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSelectPython3Exe)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cmdSave.setText("Save Configuration and Close");
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });
        jPanel3.add(cmdSave);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lblInformation.setBackground(new java.awt.Color(204, 204, 204));
        lblInformation.setText("jLabel2");
        lblInformation.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInformation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInformation, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelectEPlusDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectEPlusDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(Config.getEPlusBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            Config.setEPlusBinDir(bindir);
            Config.setEPlusEPMacro(bindir + EPlusConfig.getDefEPlusEPMacro());
            Config.setEPlusExpandObjects(bindir + EPlusConfig.getDefEPlusExpandObjects());
            Config.setEPlusEXEC(bindir + EPlusConfig.getDefEPlusEXEC());
            Config.setEPlusReadVars(bindir + EPlusConfig.getDefEPlusReadVars());
            initSettings();
            checkSettings();
            Config.saveToFile(Config.getCurrentConfigFile());
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }//GEN-LAST:event_cmdSelectEPlusDirActionPerformed

    private void cmdEnergyPlusDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEnergyPlusDetailsActionPerformed
        JDialog dialog = new JDialog (JEPlusFrameMain.getCurrentMainGUI(), "Set EnergyPlus binaries", true);
        dialog.getContentPane().add(new JPanel_EPlusSettingsDetailed (dialog, Config));
        // Add dialog closing listener
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                initSettings();
                checkSettings();
            }
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                initSettings();
                checkSettings();
            }
        });
        dialog.setSize(500, 260);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_cmdEnergyPlusDetailsActionPerformed

    private void cmdSelectTrnsysDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectTrnsysDirActionPerformed
        // Select a directory to open
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = file.getAbsolutePath();
            String bindir = fn + File.separator;
            Config.setTRNYSBinDir(bindir);
            Config.setTRNSYSEXEC(new File (bindir + TRNSYSConfig.getDefTRNSYSEXEC()).getAbsolutePath());
            initSettings();
            checkSettings();
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }//GEN-LAST:event_cmdSelectTrnsysDirActionPerformed

    private void cmdSelectTRNexeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectTRNexeActionPerformed
        // Select a file to open
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(new File(Config.getTRNSYSBinDir()));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String name = file.getPath();
            txtTrnsysEXE.setText(name);
            txtTrnsysEXE.setForeground(Color.black);
            Config.setTRNSYSEXEC(name);
        }
        fc.resetChoosableFileFilters();
        checkSettings ();
    }//GEN-LAST:event_cmdSelectTRNexeActionPerformed

    private void cmdSelectPython3ExeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectPython3ExeActionPerformed
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.ALL));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(new File("./"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            txtPython3Exe.setText(file.getAbsolutePath());
            Config.setPython3EXE(file.getAbsolutePath());
        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFiles(null);
        checkSettings ();
    }//GEN-LAST:event_cmdSelectPython3ExeActionPerformed

    private void cmdSelectPython2ExeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectPython2ExeActionPerformed
        // Select a file to open
        fc.setFileFilter(EPlusConfig.getFileFilter(EPlusConfig.ALL));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(new File(""));
        fc.setCurrentDirectory(new File("./"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            txtPython2Exe.setText(file.getAbsolutePath());
            Config.setPython2EXE(file.getAbsolutePath());
        }
        fc.resetChoosableFileFilters();
        fc.setSelectedFiles(null);
        checkSettings ();
    }//GEN-LAST:event_cmdSelectPython2ExeActionPerformed

    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
        Config.saveToFile(ConfigFile);
        HostWindow.dispose();
    }//GEN-LAST:event_cmdSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdEnergyPlusDetails;
    private javax.swing.JButton cmdSave;
    private javax.swing.JButton cmdSelectEPlusDir;
    private javax.swing.JButton cmdSelectPython2Exe;
    private javax.swing.JButton cmdSelectPython3Exe;
    private javax.swing.JButton cmdSelectTRNexe;
    private javax.swing.JButton cmdSelectTrnsysDir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblInformation;
    private javax.swing.JTextField txtEPlusBinDir;
    private javax.swing.JTextField txtPython2Exe;
    private javax.swing.JTextField txtPython3Exe;
    private javax.swing.JTextField txtTrnsysBinDir;
    private javax.swing.JTextField txtTrnsysEXE;
    // End of variables declaration//GEN-END:variables
}