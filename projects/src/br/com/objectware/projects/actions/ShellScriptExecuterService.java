/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.actions;

import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.FileUtil;
import br.com.objectware.commons.utils.TimerUtil;
import br.com.objectware.domain.ProjectProperties;
import br.com.objectware.projects.domain.BuildOptions;
import br.com.objectware.projects.domain.OpCodeProject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.Optional;
import org.openide.filesystems.FileObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Runs a shell script using the given project properties (project dir, main source and target name)
 * 
 * @author Luciano M. Christofoletti <luciano@objectware.net>
 * @since 23/Jun/2015
 */
public class ShellScriptExecuterService extends Thread {
    
    /** The script file to be executed */
    private final String scriptFileName;
    
    /** The project data associated to the node representation */
    private final OpCodeProject project;
    
    /** Clean the output window before running the process? */
    private boolean cleanOutput = true;
    
    /** The script type (defines the output text when running the script) */
    private ScriptType scriptType = ScriptType.UNDEFINED;
    
    /**
     * Shell script executer service. Run the given script file name for the given project.
     * @param scriptFileName 
     * @param project 
     */
    public ShellScriptExecuterService(String scriptFileName, OpCodeProject project) {
        this.scriptFileName = scriptFileName;
        this.project = project;
    }   
    
    /**
     * Run the curent shell script.
     * @param scriptType type of script (showed in the header of output panel)
     */
    public void runShellScript(ScriptType scriptType) {
        
        // set the script type
        this.scriptType = scriptType;
        
        // output window selection
        InputOutput inputOutput = this.getIO(scriptType);
        OutputWriter outputWriter = inputOutput.getOut();
        
        // get the script parameters
        FileObject projectDir = this.project.getProjectDirectory();
        String actionDescription = scriptType.getActionDescription();
        
        try {
            // prepare the output writer and the I/O window
            if(this.cleanOutput) {
                outputWriter.reset();
            }
            inputOutput.select();
            
            // verify if the script file is available
            File buildScriptFile = new File(FileUtil.getPath(projectDir.getPath(), this.scriptFileName));
            if(!buildScriptFile.exists()) {
                outputWriter.println(I18N.getString("script.file.not.found", this.scriptFileName));
                outputWriter.println(I18N.getString("please.configure.script", actionDescription));
                return;
            }   
            
            // get the project name and base folders
            ProjectProperties projectProperties = this.project.getProperties();
            String projectName = projectProperties.getName();
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            
            outputWriter.append(I18N.getString("start.process.message", actionDescription, projectName));
            outputWriter.append(" ").append(I18N.getString("at")).append(" ").println(currentTime.toString());
            outputWriter.println();
            outputWriter.println(I18N.getString("project.path", 
                    FileUtil.normalizePath(this.project.getProjectDirectory().getPath())
                )
            );
            
            outputWriter.flush();
            TimerUtil.sleep(300);
            
            // do the hard job!
            this.start();
            
        } catch (IOException | NullPointerException | InvalidParameterException exception) {
            // TODO: improve the exception handler!
            outputWriter.println(exception.getMessage());
        }   
    }
    
    @Override
    public void run() {
        
        // the input/output window must be available
        InputOutput inputOutput = this.getIO(this.scriptType);
        OutputWriter outputWriter = inputOutput.getOut();
        outputWriter.println(I18N.getString("running.script", this.scriptFileName));
        
        // the start time of current process
        long startTime = System.currentTimeMillis();
        
        // execute the external script
        this.executeProcess(this.project.getProjectDirectory(), this.scriptFileName);
        
        // evaluate total execution time
        long totalTime = System.currentTimeMillis() - startTime;
        
        // print the "end of script" message
        outputWriter.append(I18N.getString("done")).println(".");
        outputWriter.println(I18N.getString("total.time", TimerUtil.getIntervalString(totalTime)));
        outputWriter.println("Ok");
    }   
    
    /**
     * Execute a system process (may be a shell script).
     * 
     * @param projectPath
     * @param scriptFileName
     */
    public void executeProcess(FileObject projectPath, String scriptFileName) {
        
        // parse the build options
        OutputWriter outputWriter = this.getIO(this.scriptType).getOut();
        ProjectProperties projectProperties = this.project.getProperties();
        BuildOptions buildOptions = BuildOptions.parse(projectProperties.getBuildOptions());
        Process process = null;
        
        // the script to be executed and the
        String scriptNormalizedPath = FileUtil.getNormalizedPathInQuotes(projectPath.getPath(), scriptFileName);
        String projectNormalizedPath = FileUtil.normalizePath(projectPath.getPath());
        
        // set the MAIN_SOURCE path (if available)
        Optional<String> mainSource = buildOptions.getMainSource();
        String mainSourceNormalizedPath = Default.EMPTY_STRING;
        if(mainSource.isPresent() && FileUtil.isValidPath(mainSource.get())) {
            mainSourceNormalizedPath = FileUtil.getNormalizedPath(projectPath.getPath(), mainSource.get());
        }   
        
        // set the TARGET_FILE path (if available)
        Optional<String> targetFile = buildOptions.getTargetFile();
        String targetFileNormalizedPath = Default.EMPTY_STRING;
        if(targetFile.isPresent() && FileUtil.isValidPath(targetFile.get())) {
            targetFileNormalizedPath = FileUtil.getNormalizedPath(projectPath.getPath(), targetFile.get());
        }   
        
        String environment[] = {
            "PROJECT_DIR="+projectNormalizedPath,
            "PROJECT_PATH="+projectNormalizedPath,
            "MAIN_SOURCE="+mainSourceNormalizedPath,
            "TARGET_FILE="+targetFileNormalizedPath
        };
        
        try {
            process = Runtime.getRuntime().exec(
                    scriptNormalizedPath, // the script
                    environment, // env vars
                    new File(projectPath.getPath()) // the start directory
            );
            
            // execute the current command
            try (InputStreamReader isr = new InputStreamReader(process.getInputStream());
                BufferedReader br = new BufferedReader(isr)) {
                
                String output;
                while ((output = br.readLine()) != null) {
                    outputWriter.println(output);
                    outputWriter.flush();
                }
            }
            
            TimerUtil.sleep(100);
            outputWriter.println(I18N.getString("return.value", process.exitValue()));
            
            //process.waitFor();
            
        } catch (IOException exception) {
            outputWriter.println(exception.getLocalizedMessage());
        } finally {
            if(process != null) {
                process.destroy();
            }
        }
    }
    
    /**
     * 
     * @param cleanOutput 
     */
    public void setCleanOutput(boolean cleanOutput) {
        this.cleanOutput = cleanOutput;
    }
    
    /**
     * Get the I/O window reference.
     * @param scriptType the script type (build/clean/execute)
     * @return 
     */
    public InputOutput getIO(ScriptType scriptType) {
        
        String windowCaption = I18N.getString("default.output.caption");
        if(scriptType != null) {
            windowCaption = scriptType.getOutputWindowCaption();
        }
        
        return IOProvider.getDefault().getIO(windowCaption, false);
    }
}
