package mesquite.mesquitenexmlviewer.aNexmlViewerIntro;

import mesquite.lib.duties.PackageIntro;

public class aNexmlViewerIntro extends PackageIntro {
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
 		return true;
  	 }
  	 public Class getDutyClass(){
  	 	return aNexmlViewerIntro.class;
  	 }
	/*.................................................................................................................*/
    	 public String getExplanation() {
		return "Introduces NexmlViewer.";
   	 }
   
	/*.................................................................................................................*/
    	 public String getName() {
		return "NexmlViewer Introduction";
   	 }
	/*.................................................................................................................*/
	/** Returns the name of the package of modules (e.g., "Basic Mesquite Package", "Rhetenor")*/
 	public String getPackageName(){
 		return "NexmlViewer";
 	}
	/*.................................................................................................................*/
	/** Returns information about a package of modules*/
 	public String getPackageCitation(){
 		return "NexmlViewer";
 	}
	/** Returns whether there is a splash banner*/
	public boolean hasSplash(){
 		return true; 
	}
	
	public boolean getHideable(){
		return false;
	}

}
