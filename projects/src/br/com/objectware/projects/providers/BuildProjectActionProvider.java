/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.objectware.projects.providers;

import br.com.objectware.assembler.Assembler;
import br.com.objectware.assembler.Assemblers;
import br.com.objectware.commons.i18n.I18N;
import br.com.objectware.commons.utils.Default;
import br.com.objectware.commons.utils.FileUtil;
import br.com.objectware.commons.utils.TimerUtil;
import br.com.objectware.domain.ProjectProperties;
import br.com.objectware.domain.enums.BuildSequence;
import br.com.objectware.projects.actions.ScriptType;
import br.com.objectware.projects.actions.ShellScriptExecuterService;
import br.com.objectware.projects.domain.BuildOptions;
import br.com.objectware.projects.domain.OpCodeProject;
import java.awt.Color;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import nl.grauw.glass.AssemblyException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.windows.IOColorLines;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Provides the basic actions for OpCode projects.
 * 
 * @author Luciano M. Christofoletti
 * @since 24/Apr/2015
 * 
 * Useful docs:
 *     https://platform.netbeans.org/tutorials/nbm-projecttypeant.html
 *     http://www.rgagnon.com/javadetails/java-0014.html (script execution from java)
 *     http://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html
 */
public class BuildProjectActionProvider implements ActionProvider {
    
    // available actions for build and run OpCode files and projects
    private static final String[] SUPPORTED_ACTIONS = new String[] {
        ActionProvider.COMMAND_BUILD,
        ActionProvider.COMMAND_RUN,
        ActionProvider.COMMAND_REBUILD
    };
    
    /** The project data associated to the node representation */
    private final OpCodeProject project;
    
    /** The current selected projects */
    private final org.openide.util.Lookup.Result<Project> activeProjects;
    
    /** The current selected file */
    private final org.openide.util.Lookup.Result<FileObject> selectedFile;
    
    /**
     * Build action provider. Stores the project reference.
     * @param project the target project
     */
    public BuildProjectActionProvider(OpCodeProject project) {
        
        this.project = project;
        
        // setup the lookup listeners
        this.activeProjects = Utilities.actionsGlobalContext().lookupResult(Project.class);
        this.selectedFile = Utilities.actionsGlobalContext().lookupResult(FileObject.class);
    }   
    
    @Override
    public String[] getSupportedActions() {
        return BuildProjectActionProvider.SUPPORTED_ACTIONS;
    }
    
    @Override
    public void invokeAction(String string, Lookup lookup) throws IllegalArgumentException {

//        Collection<? extends BuildProjectAction> builds = Lookup.getDefault().lookupAll(BuildProjectAction.class);
//        if(!builds.isEmpty()) {
////            System.out.println("building project: " + this.project);
////        } else {
//            BuildProjectAction builder = (BuildProjectAction) builds.toArray()[0];
//            builder.build(project);
//        }
        
        switch (string) {
            
            case ActionProvider.COMMAND_BUILD:
                
                // there is a project node selected -> build the entire project
                if (!this.activeProjects.allInstances().isEmpty()) {
                    
                    // build the entire project
                    BuildSequence buildSequence = this.project.getProperties().getBuildSequence();
                    boolean runBothScripts = buildSequence.equals(BuildSequence.FIRST_EMBEDDED_THEN_SCRIPT);
                    
                    // verify if the embedded compiler must be executed
                    if(runBothScripts || buildSequence.equals(BuildSequence.ONLY_EMBEDDED)) {
                        this.buildProject();
                    }   
                    
                    // verify if the external build script must be executed
                    if(runBothScripts || buildSequence.equals(BuildSequence.ONLY_BUILD_SCRIPT)) {
                        
                        ShellScriptExecuterService buildScript = new ShellScriptExecuterService(
                            Default.getBuildScriptFileName(),
                            this.project
                        );
                        buildScript.setCleanOutput(!runBothScripts); // clean the output if running only build script
                        buildScript.runShellScript(ScriptType.BUILD);
                    }   
                    
                } else if (!this.selectedFile.allInstances().isEmpty()) {
                    
                    // build the single file selected in the project tree
                    Object[] fileObjectInstances = this.selectedFile.allInstances().toArray();
                    FileObject sourceFile = (FileObject) fileObjectInstances[0];
                    if (FileUtil.isSourceFile(sourceFile)) {
                        this.buildFile(sourceFile);
                    }   
                }   
                
                break;
                
            case ActionProvider.COMMAND_REBUILD:
                
                ShellScriptExecuterService cleanScript = new ShellScriptExecuterService(
                    Default.getCleanScriptFileName(),
                    this.project
                );
                cleanScript.runShellScript(ScriptType.CLEAN);
                
                break;
                
            case ActionProvider.COMMAND_RUN:
                
                ShellScriptExecuterService runScript = new ShellScriptExecuterService(
                    Default.getRunScriptFileName(),
                    this.project
                );
                runScript.runShellScript(ScriptType.EXECUTE);
                
                break;
        }

//        if (string.equals(ActionProvider.COMMAND_DELETE)) {
//            DefaultProjectOperations.performDefaultDeleteOperation(this.project);
//        } if (string.equals(ActionProvider.COMMAND_COPY)) {
//            DefaultProjectOperations.performDefaultCopyOperation(this.project);
//        } //Here we find the Ant script and call the target we need!
    }
    
    /**
     * Build the given source file using the embedded compiler.
     * @param sourceFile 
     */
    private void buildFile(FileObject sourceFile) {
        
        String windowCaption = ScriptType.BUILD.getOutputWindowCaption();
        InputOutput inputOutput = IOProvider.getDefault().getIO(windowCaption, false);
        inputOutput.select();
        OutputWriter outputWriter = inputOutput.getOut();
        
        // get information about the project's path, source and target files
        FileObject projectDirectory = this.project.getProjectDirectory();
        String sourceFilePath = FileUtil.getRelativePath(projectDirectory.getPath(), sourceFile.getPath());
        String targetFilePath = FileUtil.getPath(Default.BUILD_FOLDER, sourceFile.getName(), Default.BIN_FILE_EXTENSION);
        
        try {
            
            // clean up the build output
            outputWriter.reset();
            
            // "generate" the build options (for use on the build project method)
            StringBuilder singleFileOptions = new StringBuilder();
            singleFileOptions.append(sourceFilePath).append(' ');
            singleFileOptions.append(targetFilePath).append(' ');
            
            BuildOptions buildOptions = BuildOptions.parse(singleFileOptions.toString());
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            
            // output status
            outputWriter.println(I18N.getString("start.build.file.process",
                sourceFile.getNameExt(),
                currentTime.toString())
            );  
            outputWriter.println();
            
            outputWriter.println(I18N.getString("project.path", 
                FileUtil.normalizeAndTranslatePath(projectDirectory.getPath())));
            outputWriter.println(I18N.getString("source.file.arg", buildOptions.getMainSource().get()));
            outputWriter.println(I18N.getString("target.file.arg", buildOptions.getTargetFile().get()));
            outputWriter.flush();
            
            // assembly the given file using the "build project" method :P
            long startTime = System.currentTimeMillis();
            this.buildProjectUsingEmbeddedCompiler(projectDirectory, buildOptions);
            long totalTime = System.currentTimeMillis() - startTime;
            
            // show total script execution time
            outputWriter.println();
            colorOutput(inputOutput, I18N.getString("build.successful"), Color.BLUE);
            outputWriter.println();
            outputWriter.println(I18N.getString("total.time", TimerUtil.getIntervalString(totalTime)));
            outputWriter.println("Ok");
            
        } catch (IOException | NullPointerException | InvalidParameterException ioException) {
            
            // this exception may occur when the "outputWriter.reset()" is executed
            outputWriter.println();
            colorOutput(inputOutput, ioException.getLocalizedMessage(), Color.RED);
        }
        // this exception may occur when the "buildProjectUsingEmbeddedCompiler()" is executed
         catch (AssemblyException asmException) {
            
            // assembly compilation error occured...
            outputWriter.println();
            colorOutput(inputOutput, I18N.getString("compiler.error"), Color.RED);
            outputWriter.println();
            colorOutput(inputOutput, asmException.getMessage(), Color.BLUE);
            
        } finally {
            outputWriter.close();
        }
    }
    
    /**
     * Build the current project.
     * There are two build options: using the embedded compiler or executing an external compiler
     * using the build script provided 
     * @param project 
     */
    private void buildProject() {
        
        // get the I/O window
        String windowCaption = ScriptType.BUILD.getOutputWindowCaption();
        InputOutput inputOutput = IOProvider.getDefault().getIO(windowCaption, false);
        inputOutput.select();
        OutputWriter outputWriter = inputOutput.getOut();
        
        // parse the build options
        ProjectProperties projectProperties = this.project.getProperties();
        BuildOptions buildOptions = BuildOptions.parse(projectProperties.getBuildOptions());
        
        try {
            // clean up the build output
            outputWriter.reset();
            
            Optional<String> mainSource = buildOptions.getMainSource();
            Optional<String> targetFile = buildOptions.getTargetFile();
            if(!mainSource.isPresent() || !targetFile.isPresent()) {
                outputWriter.println(I18N.getString("missing.build.parameters"));
                return;
            }   
            
            // get the project name and base folders
            FileObject projectDirectory = this.project.getProjectDirectory();
            
            String projectName = projectProperties.getName();
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            
            // output status
            outputWriter.append(I18N.getString("start.process.message", I18N.getString("build"), projectName));
            outputWriter.append(" ").append(I18N.getString("at")).append(" ").println(currentTime.toString());
            outputWriter.println(I18N.getString("build.using.embedded.compiler"));
            outputWriter.println();
            
            outputWriter.println(I18N.getString("project.path", 
                FileUtil.normalizeAndTranslatePath(projectDirectory.getPath())));
            outputWriter.println(I18N.getString("main.source.file.arg", mainSource.get()));
            outputWriter.println(I18N.getString("target.file.arg", targetFile.get()));
            outputWriter.flush();
            
            // compile the assembly project
            long startTime = System.currentTimeMillis();
            this.buildProjectUsingEmbeddedCompiler(projectDirectory, buildOptions);
            long totalTime = System.currentTimeMillis() - startTime;
            
            // show total script execution time
            outputWriter.println();
            colorOutput(inputOutput, I18N.getString("build.successful"), Color.BLUE);
            
            outputWriter.println();
            outputWriter.println(I18N.getString("total.time", TimerUtil.getIntervalString(totalTime)));
            outputWriter.println("Ok");
            outputWriter.println();
            
        } catch (IOException | NullPointerException | InvalidParameterException ioexception) {
            
            // this exception may occur when the "outputWriter.reset()" is executed
            outputWriter.println();
            colorOutput(inputOutput, ioexception.getLocalizedMessage(), Color.RED);
        }
        // this exception may occur when the "buildProjectUsingEmbeddedCompiler()" is executed
         catch (AssemblyException asmException) {
            
            // assembly error occured...
            outputWriter.println();
            colorOutput(inputOutput, I18N.getString("compiler.error"), Color.RED);
            outputWriter.println();
            colorOutput(inputOutput, asmException.getMessage(), Color.BLUE);
            
        } finally {
            outputWriter.close();
        }
    }
    /**
     * 
     * @param projectDir
     * @param buildOptions
     * @throws InvalidParameterException 
     */
    private void buildProjectUsingEmbeddedCompiler(FileObject projectDirectory, BuildOptions buildOptions)
            throws InvalidParameterException {
        
        // get the build directory
        String buildPath = projectDirectory.getFileObject(Default.BUILD_FOLDER).getPath();
        String mainSource = buildOptions.getMainSource().get();
        
        // get the source file object
        String mainSourceTranslated = FileUtil.translatePath(mainSource);
        
//        // hack to avoid problems with projects created on different platforms
//        if((Default.isUnix() || Default.isMac()) && mainSourceTranslated.contains(Default.WIN_FS)) {
//            mainSourceTranslated = FileUtil.translateWinFSToUnixFS(mainSource);
//        }
        
        // verify if the main source file is available
        FileObject fileObject = projectDirectory.getFileObject(mainSourceTranslated);
        if(fileObject == null) {
            throw new InvalidParameterException(
                I18N.getString("build.error.source.not.found", mainSource)
            );
        }
        
        // get the main source file name and extension
        String mainSourcePath = fileObject.getName();
        String mainSourceNameExt = fileObject.getPath();
        
        // set the target file name
        String targetFileNameExt = FileUtil.getPath(buildPath, Default.BUILD_FILE_NAME);
        if(buildOptions.getTargetFile() != null) {
            targetFileNameExt = FileUtil.getPath(projectDirectory.getPath(), buildOptions.getTargetFile().get());
        }
        
//        // hack to avoid problems with projects created on different platforms
//        if((Default.isUnix() || Default.isMac()) && mainSourceTranslated.contains(Default.WIN_FS)) {
//            targetFileNameExt = FileUtil.translateWinFSToUnixFS(targetFileNameExt);
//        }
        
        // fill up the argument to build the main project file
        List<String> arguments = new ArrayList<>();
        arguments.add("-I");
        arguments.add(projectDirectory.getPath());
        arguments.add(mainSourceNameExt);
        arguments.add(targetFileNameExt);
        
        Optional<String> option = buildOptions.getOption(0);
        if(option.isPresent() && option.get().equalsIgnoreCase("-symbols")) {
             arguments.add(FileUtil.getPath(buildPath, mainSourcePath, Default.SYM_FILE_EXTENSION));
        }
        
        // compile the main source file (and it's dependencies)
        try {
            Assembler assembler = Assemblers.GLASS.newInstance();
            assembler.parseArguments(arguments);
            assembler.assemble();
        } catch (InstantiationException | IllegalAccessException exception) {
            Exceptions.printStackTrace(exception);
        }   
        
    }
    
    @Override
    public synchronized boolean isActionEnabled(String command, Lookup lookup) throws IllegalArgumentException {
        
        // flag that identify if there are selected projects
        boolean isProject = !this.activeProjects.allInstances().isEmpty();
        boolean isSourceFile = false;
        
        // source files are not folders and must have a valid source file extension
        Collection<? extends FileObject> fileInstances = this.selectedFile.allInstances();
        if(!fileInstances.isEmpty()) {
            java.util.Optional<?> fileObject = fileInstances.stream().findFirst();
            isSourceFile = FileUtil.isSourceFile((FileObject) fileObject.get());
        }   
        
        switch (command) {
            case ActionProvider.COMMAND_DELETE:
                return true;
            case ActionProvider.COMMAND_COPY:
                return true;
            case ActionProvider.COMMAND_RUN:
                return isProject;
            case ActionProvider.COMMAND_BUILD:
                return (isProject || isSourceFile);
            case ActionProvider.COMMAND_CLEAN:
                return isProject;
            case ActionProvider.COMMAND_REBUILD:
                return isProject;
            default:
                return false;
        }
        
    }
    
    /**
     * Output text to the Output window with the given color.
     * @param inputOutput
     * @param text
     * @param color 
     */
    private void colorOutput(InputOutput inputOutput, String text, Color color) {
        try {
            IOColorLines.println(inputOutput, text, color);
        } catch (IOException ioException) {
            inputOutput.getOut().println(text);
        }   
    }   
    
} 

