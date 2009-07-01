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

	int cCurrent; //column. Columns start at 0 and from the left-most column.
	int tCurrent; //row. Rows start at 0 and from the top row.
	String cellExplanation;
	//String upcomingCellExplanation = "";

	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		addMenuItem("Display NeXML Tree", MesquiteModule.makeCommand("displayNeXMLTree",  this));
		logln("startJob in NexmlViewer ran.");
		logln("NexmlViewer's employer is " + getEmployer());
		//logln("NexmlViewer's window is " + getModuleWindow());
		
		//.setExplanation("Setting text by calling setExplanation");
		//MesquiteWindow w = getModuleWindow();
		return true;
	}
	 
   	 //From DataWindowAssistant
	/*.................................................................................................................*/
   	/** method called by data window to inform assistant that data have changed*/
   	 public void setTableAndData(MesquiteTable table, CharacterData data){
		this.table = table;
		this.data = data;
		//logln("Data name is " + data.getName());
		//this.containerOfModule().setExplanation("Setting explanation from NexmlViewer"); //Can't tell if this does anything
		resetContainingMenuBar();
		//logln("NexmlViewer's setTableAndData method runs");
	}
   	 
   	 //From DataWindowAssistant
 	//called by data editor when selection changed
 	public void tableSelectionChanged(){
 	}
   	public void colorsLegendGoAway(){
   	}
   	
  //From DataWindowAssistant
   	public boolean hasDisplayModifications(){
   		return false;
   	}
   	public String getDisplayModString(int ic, int it){
   		
   		return null;
   	}
   	public String getCellString(int ic, int it){
   		
   		return null;
   	}
  	public String getCellExplanation(int ic, int it){
  		//logln("getCellExplanation runs."); //Runs with mouse-over
  		//cellExplanation = upcomingCellExplanation;
  		//upcomingCellExplanation = this.containerOfModule().getExplanation();
  		cellExplanation = this.containerOfModule().getExplanation();
  		//logln("getExplanation: " + this.containerOfModule().getExplanation());
  		return null;
	}
  	public void focusInCell(int ic, int it){
  		//logln("focusInCell runs."); //Runs with mouse-over
  		cCurrent = ic; //column
  		tCurrent = it; //row
  		//logln("Cell selected is (column/row): " + cCurrent + ", " + tCurrent);
	}
   	 
  	
  	//From AlterData
  	
  	//EmployeeNeed - initialized to result of call to registerEmployeeNeeded
	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
		//EmployeeNeed e2 = registerEmployeeNeed(DataAlterer.class, getName() + " needs a particular method to alter data in the Character Matrix Editor.",
		//"These options are available in the Alter/Transform submenu of the Matrix menu of the Character Matrix Editor");
		//e2.setPriority(2);
	}
	/*.................................................................................................................*/
	/** returns whether this module is requesting to appear as a primary choice */
   	public boolean requestPrimaryChoice(){
   		return true;  
   	}
   	//default false, override when releasing
	/*.................................................................................................................*/
   	 public boolean isPrerelease(){
   	 	return true;
   	 }
  	
	/*.................................................................................................................*/
   	 public boolean isSubstantive(){
   	 	return false;
   	 }
	/*.................................................................................................................*/
    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {
    		 if (checker.compare(this.getClass(), "Displays NeXML annotations tree", "[name of module]", commandName, "displayNeXMLTree")) {
    	   	 		if (table!=null && data !=null){
    		    	 	logln("doCommand in NexmlViewer ran. Later this will cause NeXML tree to pop up."); 
    		    	 	//logln("getAnnotation gives: " + this.containerOfModule().getAnnotation()); //pulls from top window
    		    	 	logln("getExplanation gives: " + this.containerOfModule().getExplanation()); //returns the command used :(
    		    	 	//this.containerOfModule().setExplanation("Setting explanation from NexmlViewer");  //sets bottom window
    		    	 	//this.containerOfModule().setAnnotation(this.containerOfModule().getAnnotation() + "   1Setting footnote/annotation from NexmlViewer", "2Setting explanation from NexmlViewer"); 
    		    	 	//logln("Is a cell selected? T/F: " + table.anyCellSelected());
    		    	 	//setExplanation(table.getCellExplanation(table.cellAnnotated.getColumn(), table.cellAnnotated.getRow()));
    		    	 	logln("Cell selected is: " + cCurrent + ", " + tCurrent);
    		    	 	logln("Column selected is: " + table.getColumnNameText(cCurrent));
    		    	 	logln("Taxon select is: " + table.getRowNameText(tCurrent));
    		    	 	this.containerOfModule().setAnnotation(this.containerOfModule().getAnnotation() + "   1Setting footnote/annotation from NexmlViewer: Column: " + table.getColumnNameText(cCurrent) + "Row: " + table.getRowNameText(tCurrent), cellExplanation);
    		    	 	//this.containerOfModule().getExplanation();
    	   	 		}
    		 }
    	     else
    	    	 	return  super.doCommand(commandName, arguments, checker);
    		return null; 
   	 }
    	 /*
    	  Command Processing and menu control
     - doCommand(String commandName, String arguments, CommandChecker)
       - structure is long if/elseif, testing commandname using checker.compare
          - arguments to checker.compare:
             - module's class
             - command explanation
             - argument pattern
             - commandName argument
             - name of command to test
             - returns boolean comparison of commandName and testcommand
           - in startup, doCommand is called with a CommandChecker that harvests its arguments and 
              always returns false - this allows documentation pages to be constructed automatically
       - final else should call super.doCommand() 
       - MesquiteThread.isScripting() - test to check if safe to query user
    	  */
    	 
    	 
    	 
    //Necessary for a module	 
	/*.................................................................................................................*/
    public String getName() {
		return "Nexml Viewer";
   	 }
	/*.................................................................................................................*/
 	/** returns an explanation of what the module does.*/
 	public String getExplanation() {
 		return "Displays Phenex-genereated NeXML annotations.";
   	 }
   	 
}
