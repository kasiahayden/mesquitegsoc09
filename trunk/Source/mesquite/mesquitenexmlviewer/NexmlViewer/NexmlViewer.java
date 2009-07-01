/*
 *  
 */
 package mesquite.mesquitenexmlviewer.NexmlViewer;

    import java.util.*;
    import java.awt.*;
    import java.awt.image.*;

    import mesquite.lib.*;
    import mesquite.lib.characters.*;
    import mesquite.lib.duties.*;
    import mesquite.lib.table.*;
    import mesquite.charMatrices.BasicDataWindowMaker.*;

    /* ======================================================================== */
    public class NexmlViewer extends DataWindowAssistantI {
            MesquiteTable table;
            CharacterData data;

            /*.................................................................................................................*/
            public boolean startJob(String arguments, Object condition, boolean hiredByName) {
                    addMenuItem("Display NeXML Tree", MesquiteModule.makeCommand("doNothingAtTheMoment",  this));
                    logln("startJob in NexmlViewer ran.");
                    logln("NexmlViewer's employer is " + getEmployer());
                    //logln("NexmlViewer's window is " + getModuleWindow());
                   
                    //.setExplanation("Setting text by calling setExplanation");
                    //MesquiteWindow w = getModuleWindow();
                    return true;
            }
            /*.................................................................................................................*/
            /** returns whether this module is requesting to appear as a primary choice */
            public boolean requestPrimaryChoice(){
                    return true; 
            }
            /*.................................................................................................................*/
             public boolean isPrerelease(){
                    return false;
             }
            /*.................................................................................................................*/
            public void setTableAndData(MesquiteTable table, CharacterData data){
                    this.table = table;
                    this.data = data;
                    //logln("Data name is " + data.getName());
                    this.containerOfModule().setExplanation("Setting explanation from NexmlViewer");
                    //this.containerOfModule().setAppendKasia(true);
                    resetContainingMenuBar();
                    //logln("NexmlViewer's setTableAndData method runs");
            }
           
            /*.................................................................................................................*/
             public boolean isSubstantive(){
                    return false;
             }
            /*.................................................................................................................*/
             public Object doCommand(String commandName, String arguments, CommandChecker checker) {
                    
            return null;
           
             }
            /*.................................................................................................................*/
             public String getName() {
                    return "Nexml Viewer";
             }
            /*.................................................................................................................*/
            /** returns an explanation of what the module does.*/
            public String getExplanation() {
                    return "Displays Phenex-genereated NeXML annotations." ;
             }
            
    }
