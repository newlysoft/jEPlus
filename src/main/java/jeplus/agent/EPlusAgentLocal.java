/***************************************************************************
 *   jEPlus - EnergyPlus shell for parametric studies                      *
 *   Copyright (C) 2010  Yi Zhang <yi@jeplus.org>                          *
 *                                                                         *
 *   This program is free software: you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 *                                                                         *
 ***************************************************************************/
package jeplus.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jeplus.*;
import jeplus.data.ExecutionOptions;
import jeplus.gui.JFrameAgentLocalMonitor;
import jeplus.gui.JPanel_LocalControllerOptions;

/**
 * Local simulation manager to replace the run() functions in EPlusBatch and 
 * EPlusTask. This agent supports multiple threads, which is configurable on
 * the fly.
 * @todo AgentLocal to be implemented
 * @author Yi Zhang
 * @version 0.5b
 * @since 0.5b
 */
public class EPlusAgentLocal extends EPlusAgent {
    
    /**
     * Construct with Exec settings
     * @param settings Reference to an existing Exec settings instance
     */
    public EPlusAgentLocal (ExecutionOptions settings) {
        super("Local batch simulation controller", settings);
        this.QueueCapacity = 10000;
        this.attachDefaultCollector();
        SettingsPanel = new jeplus.gui.JPanel_EPlusSettings (JEPlusConfig.getDefaultInstance());
        OptionsPanel = new JPanel_LocalControllerOptions (Settings);
    }
    
    @Override
    public void showAgentMonitorGUI (boolean show, boolean reset) {
        if (show) {
            if (MonitorGUI == null) {
                MonitorGUI = new JFrameAgentLocalMonitor (this);
                MonitorGUI.setTitle("Simulation Agent Local");
                MonitorGUI.setSize(600, 530);
                MonitorGUI.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            }
            if (reset) {
                MonitorGUI.reset();
            }
            MonitorGUI.pack();
            if (! MonitorGUI.isVisible()) {
                MonitorGUI.setVisible(true);
            }
            if (! MonitorGUI.isActive()) {
                MonitorGUI.requestFocus();
            }
        }else {
            if (MonitorGUI != null && MonitorGUI.isVisible()) {
                MonitorGUI.setVisible(false);
            }
        }
    }
    
    /**
     * Start the agent in a separate thread
     */
    @Override
    public void run() {

        // Notify Owner
        this.getJobOwner().setSimulationRunning(true);
        
        // show monitor GUI
        this.State = AgentState.RUNNING;
        if (this.getJobOwner().getGUI() != null || (MonitorGUI != null && MonitorGUI.isVisible())) {
            showAgentMonitorGUI(true, true);
        }

        if (Processors == null) {
            Processors = new ArrayList<> ();
        }
        // Clear all lists before run
        //if (FinishedJobs.size() > 0) FinishedJobs.removeAllElements();
        purgeAllLists();
        // Timing
        StartTime = new Date();
        StopAgent = false;

        while ((! StopAgent) && JobQueue.size() > 0)  {
            // Fill processor threads
            while ((! StopAgent) && (State != AgentState.PAUSED) && Processors.size() < Settings.getNumThreads() && JobQueue.size() > 0) {
                EPlusTask job = JobQueue.remove(0);
                if (job != null) {
                    this.RunningJobs.add(job);
                    Processors.add(job);
                    job.start();
                    // GUI update
                    writeLog("Job " + job.getJobID() + " started. " + JobQueue.size() + " more to go!");
                    job.setExecuted(true);
                }else {
                    break;
                }
                try { Thread.sleep(Settings.getDelay()); } catch (Exception ex) {}
            }
            // Check if any of the processors have finished
            for (int i=0; i<Processors.size(); i++) {
                Thread proc = Processors.get(i);
                if (! proc.isAlive()) {
                    Processors.remove(proc);
                    RunningJobs.remove((EPlusTask)proc);
                    FinishedJobs.add((EPlusTask)proc);
                    i --;
                }
            }
            try { Thread.sleep(Settings.getDelay()); } catch (Exception ex) {}
        }
        // if manually stopped
        if (StopAgent) {
            writeLog("Local agent received a STOP signal. ");
            if (Processors.size() > 0) {
                int n = JOptionPane.showConfirmDialog(
                    this.getGUIPanel(),
                    "There are still " + Processors.size() + " E+ simulations running. Do you want to wait till they finish?",
                    "Stop signal received",
                    JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    // Wait for the last few jobs to finish
                    while (Processors.size() > 0) {
                        // Check if any of the processors have finished
                        for (int i=0; i<Processors.size(); i++) {
                            Thread proc = Processors.get(i);
                            if (! proc.isAlive()) {
                                Processors.remove(proc);
                                RunningJobs.remove((EPlusTask)proc);
                                FinishedJobs.add((EPlusTask)proc);
                                i --;
                            }
                        }
                        try { Thread.sleep(Settings.getDelay()); } catch (Exception ex) {}
                    }
                }else {
                    Processors.clear();
                }
            }
            State = AgentState.CANCELLED;
            StopAgent = false;
        }else {
            // GUI update
            writeLog("Nearly there ...");
            // Wait for the last few jobs to finish
            while (Processors.size() > 0) {
                // Check if any of the processors have finished
                for (int i=0; i<Processors.size(); i++) {
                    Thread proc = Processors.get(i);
                    if (! proc.isAlive()) {
                        Processors.remove(proc);
                        RunningJobs.remove((EPlusTask)proc);
                        FinishedJobs.add((EPlusTask)proc);
                        i --;
                    }
                }
                try { Thread.sleep(Settings.getDelay()); } catch (Exception ex) {}
            }
            State = AgentState.FINISHED;
        }
        writeLog(this.getStatus());
        writeLog("Local agent stopped. ");

        // Start collecting results
        writeLog("Collecting results ...");
        runResultCollection (true);

        // Done
        writeLog("All done!");
        // write start time
        Date EndTime = new Date ();
        writeLog("Simulation finished at: " + DateFormat.format(EndTime));
        writeLog("Total execution time = " + ((EndTime.getTime() - StartTime.getTime())/1000) + " seconds.\n");

        // Notify Owner
        this.getJobOwner().setSimulationRunning(false);
    }

    /**
     * Set state of the agent. Only Running and Paused are accepted
     * @param State 
     */
    @Override
    public void setState(AgentState State) {
        if (State == AgentState.RUNNING || State == AgentState.PAUSED) {
            this.State = State;
        }
    }

    @Override
    public int getExecutionType() {
        return ExecutionOptions.INTERNAL_CONTROLLER;
    }

    /**
     * Check the local EnergyPlus installation settings.
     * @return True if everything is in place
     */
    @Override
    public boolean checkAgentSettings() {
        String bindir = JEPlusConfig.getDefaultInstance().getResolvedEPlusBinDir();
        String expandobjects = JEPlusConfig.getDefaultInstance().getResolvedExpandObjects();
        String epmacro = JEPlusConfig.getDefaultInstance().getResolvedEPMacro();
        String exe = JEPlusConfig.getDefaultInstance().getResolvedEPlusEXEC();
        String idd = EPlusConfig.getEPDefIDD();
        String readvars = JEPlusConfig.getDefaultInstance().getResolvedReadVars();

        boolean success = true;
        boolean ok = new File(exe).exists();
        if (! ok) {
            try {
                this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Error: " + exe + " is not accessible.");
            }catch (Exception ex) {
                System.err.println("[" + this.AgentID + "]: Settings error: " + exe + " is not accessible.");
            }
        }
        success &= ok;
        ok = new File(epmacro).exists();
        if (! ok) { 
            try {
                this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Error: " + epmacro + " is not accessible.");
            }catch (Exception ex) {
                System.err.println("[" + this.AgentID + "]: Settings error: " + epmacro + " is not accessible.");
            }
        }
        success &= ok;
        ok = new File(bindir + idd).exists();
        if (! ok) { 
            try {
                this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Error: " + bindir + idd + " is not accessible.");
            }catch (Exception ex) {
                System.err.println("[" + this.AgentID + "]: Settings error: " + bindir + idd + " is not accessible.");
            }
        }
        success &= ok;
        ok = new File(readvars).exists();
        if (! ok) { 
            try {
                this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Error: " + readvars + " is not accessible.");
            }catch (Exception ex) {
                System.err.println("[" + this.AgentID + "]: Settings error: " + readvars + " is not accessible.");
            }
        }
        success &= ok;
        ok = new File(expandobjects).exists();
        if (! ok) { 
            try {
                this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Error: " + expandobjects + " is not accessible.");
            }catch (Exception ex) {
                System.err.println("[" + this.AgentID + "]: Settings error: " + expandobjects + " is not accessible.");
            }
        }
        success &= ok;
        // Check if E+ version is for the current project
        String EpVer = JEPlusConfig.getDefaultInstance().getEPlusVersion();
        String IdfVer = this.getJobOwner().getProject().getEPlusModelVersion();
        ok = IdfVer != null && EpVer.startsWith(IdfVer);
        if (! ok) { 
            try {
                this.JobOwner.getBatchInfo().addValidationError("[" + this.AgentID + "]: Warning: E+ version (" + 
                        EpVer + ") is different than the project's (" +
                        IdfVer + ").");
            }catch (Exception ex) {
                System.err.println("[" + this.AgentID + "]: Version checking error. ");
            }
        }
        success &= ok;
        try {
            this.JobOwner.getBatchInfo().setValidationSuccessful(success);
        }catch (Exception ex) {}
        return success;
    }
}
