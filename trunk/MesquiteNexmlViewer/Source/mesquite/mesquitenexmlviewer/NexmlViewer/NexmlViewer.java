package mesquite.mesquitenexmlviewer.NexmlViewer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;
import mesquite.lib.table.*;
import mesquite.mesquitenexmlviewer.lib.URIMap;
import mesquite.categ.lib.CategoricalData;
import mesquite.categ.lib.CategoricalState;
import att.grappa.*;
import att.grappa.Parser;


/* ======================================================================== */
public class NexmlViewer extends DataWindowAssistantI {
	MesquiteTable table;
	CharacterData data;
	
	TableTool xmlTool;

	int cCurrent;
	int tCurrent;
	String cellExplanation;
	StringBuffer esb = new StringBuffer(100);
	
	NameReference notesNameRef = NameReference.getNameReference("notes");
	
	URIMap UriMap = null;
	
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
			addMenuItem("Display NeXML", MesquiteModule.makeCommand(
					"displayNeXMLTree", this));
			if (containerOfModule() instanceof MesquiteWindow) {
				xmlTool = new TableTool(this, "DisplayNeXML", getPath(), "nexml.gif", 1,1,"Display Phenex-generated NeXML", "Displays Phenex-generated NeXML annotations in the footnote box.", MesquiteModule.makeCommand("displayNeXMLTree", this), null, null);
				xmlTool.setWorksOnColumnNames(true);
				xmlTool.setWorksOnRowNames(true);
				((MesquiteWindow)containerOfModule()).addTool(xmlTool);
				xmlTool.setPopUpOwner(this);
				setUseMenubar(false); //menu available by touching button

			}

			
			String dotFile = "/home/kasia/projects/zgrviewer/data/graphs/example.dot";
			try {
				  FileInputStream input = new FileInputStream(dotFile);
				  Parser graphParser = new Parser(input, System.err);
				  graphParser.parse();
				  Graph newGraph = graphParser.getGraph();
				  logln("Grappa working ----------------");
			}
			catch(Exception e) {
				logln("Grappa unsuccessful.");
			}
			
			
		return true;
	}

	/*.................................................................................................................*/
	/** method called by data window to inform assistant that data have changed */
	public void setTableAndData(MesquiteTable table, CharacterData data) {
		this.table = table;
		this.data = data;
		resetContainingMenuBar();
	}
	/*.................................................................................................................*/
	// From DataWindowAssistant
	// called by data editor when selection changed
	public void tableSelectionChanged() {
	}
	/*.................................................................................................................*/
	public void colorsLegendGoAway() {
	}
	/*.................................................................................................................*/
	// From DataWindowAssistant
	public boolean hasDisplayModifications() {
		return false;
	}
	/*.................................................................................................................*/
	public String getDisplayModString(int ic, int it) {

		return null;
	}
	/*.................................................................................................................*/
	public String getCellString(int ic, int it) {

		return null;
	}
	/*.................................................................................................................*/
	public String getCellExplanation(int ic, int it) {
		return null;
	}
	/*.................................................................................................................*/
	public String copyCellExplanation(int cCurrent, int tCurrent) {
		String s = "Kasia:BasicDataWindowMaker [";
		if (tCurrent >= 0 && tCurrent < data.getNumTaxa())
			s += "t." + (tCurrent + 1);
		if (cCurrent >= 0 && cCurrent < data.getNumChars())
			s += " c." + (cCurrent + 1);
		if (tCurrent >= 0 && tCurrent < data.getNumTaxa() && cCurrent >= 0
				&& cCurrent < data.getNumChars()) {
			esb.setLength(0);
			data.statesIntoStringBuffer(cCurrent, tCurrent, esb, false);
			s += " s." + esb;
		}
		s += "] ";
		if (cCurrent >= 0 && cCurrent < data.getNumChars())
			s += data.getCharacterName(cCurrent)
					+ statesExplanation(cCurrent, tCurrent);
		if (tCurrent >= 0 && tCurrent < data.getNumTaxa() && cCurrent >= 0)
			s += " [in taxon \"" + data.getTaxa().getTaxonName(tCurrent)
					+ "\"]";

		if (this == null || this.containerOfModule() == null)
			return null;

		AttachedNotesVector anv = null;
		if (cCurrent < 0)
			anv = (AttachedNotesVector) data.getTaxa().getAssociatedObject(
					notesNameRef, tCurrent);
		else if (tCurrent < 0)
			anv = (AttachedNotesVector) data.getAssociatedObject(notesNameRef,
					cCurrent);
		else
			anv = (AttachedNotesVector) data.getCellObject(notesNameRef,
					cCurrent, tCurrent);

		if (anv != null && anv.getNumNotes() > 0) {
			s += "\n-----------------";
			s += "\nAnnotations:";
			for (int i = 0; i < anv.getNumNotes(); i++) {
				AttachedNote note = anv.getAttachedNote(i);
				String c = note.getComment();
				if (!StringUtil.blank(c)) {
					s += "\n" + (c.replace('\n', ' ')).replace('\r', ' ');
					if (!StringUtil.blank(note.getAuthorName()))
						s += "  (author: " + note.getAuthorName() + ")";
				}
			}
			s += "\n-----------------";
		}
		return s;
	}
	/*.................................................................................................................*/
	public void focusInCell(int ic, int it) {
		cCurrent = ic; // column
		tCurrent = it; // row
	}
	/*.................................................................................................................*/
	/** returns whether this module is requesting to appear as a primary choice */
	public boolean requestPrimaryChoice() {
		return true;
	}
	/*.................................................................................................................*/
	public boolean isPrerelease() {
		return true;
	}
	/*.................................................................................................................*/
	public boolean isSubstantive() {
		return false;
	}
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments,
			CommandChecker checker) {
		if (checker.compare(this.getClass(), "Displays NeXML annotations",
				"[name of module]", commandName, "displayNeXMLTree")) {
			if (table != null && data != null && hasURIMapElement() == true) {
				this.containerOfModule().setAnnotation(getFootnoteAnnotation(),
						copyCellExplanation(cCurrent, tCurrent));
			}
		} else
			return super.doCommand(commandName, arguments, checker);
		return null;
	}
	/*.................................................................................................................*/
	public String getFootnoteAnnotation() {
		String sAnnot = "";
		sAnnot += this.containerOfModule().getAnnotation();
		if (sAnnot != null && sAnnot.length() != 0
				&& sAnnot.trim().length() != 0) {
			sAnnot += "\n";
		}
		String columnName = table.getColumnNameText(cCurrent);
		String rowName = table.getRowNameText(tCurrent);
		sAnnot += "Taxon: " + rowName;
		//sAnnot += "Column: " + columnName + "    Row: " + rowName + " ";

		Collection<String> tempArray = new ArrayList<String>();
		tempArray.add(columnName.trim());
		tempArray.add(rowName.trim());

		ListableVector vectors = data.getFile().getFileElements();
		if (vectors !=null && vectors.size()>0) {
	   		for (int i = 0; i<vectors.size(); i++){
	   			if (vectors.elementAt(i) instanceof URIMap){
	   				UriMap = (URIMap)vectors.elementAt(i);
	   			}
	   		}		
	   	}
		

		if (UriMap.masterMap.containsKey(tempArray)) {
			Map<String, String> tempValHM = UriMap.masterMap.get(tempArray);
			String bearerTemp = tempValHM.get("bearer");
			String holdsTemp = tempValHM.get("holds");
			String qualityTemp = tempValHM.get("quality");
			String relatedTemp = tempValHM.get("related");
			String descriptionTemp = tempValHM.get("description");
			
			if (URIMap.URIMap.containsKey(bearerTemp)) {
				bearerTemp = URIMap.URIMap.get(bearerTemp);
			}
			if (URIMap.URIMap.containsKey(holdsTemp)) {
				holdsTemp = URIMap.URIMap.get(holdsTemp);
			}
			if (URIMap.URIMap.containsKey(qualityTemp)) {
				qualityTemp = URIMap.URIMap.get(qualityTemp);
			}
			if (URIMap.URIMap.containsKey(relatedTemp)) {
				relatedTemp = URIMap.URIMap.get(relatedTemp);
			}
			
			
			if (holdsTemp == null && relatedTemp == null) {
				sAnnot += ("  Bearer: " + bearerTemp + "  Quality: " + qualityTemp  + " (" + descriptionTemp + ")");
			} 
			else {
				sAnnot += ("  (No annotation found.)");
			}
			/*
			else if (holdsTemp != null && relatedTemp == null) {
				sAnnot += ("    Bearer: " + bearerTemp
						+ "    Holds in Relation to: " + holdsTemp
						+ "    Quality: " + qualityTemp + " (" + descriptionTemp + ")");
			} else if (holdsTemp == null && relatedTemp != null) {
				sAnnot += ("    Bearer: " + bearerTemp + "    Quality: "
						+ qualityTemp + " (" + descriptionTemp + ")" + "    Related Entity: " + relatedTemp);
			} else { // (holdsTemp!=null && relatedTemp!=null)
				sAnnot += ("    Bearer: " + bearerTemp
						+ "    Holds in Relation to: " + holdsTemp
						+ "    Quality: " + qualityTemp 
						+ " (" + descriptionTemp + ")"
						+ "    Related Entity: " + relatedTemp);
			}
			*/
			
			
			/*//New lines between each
			if (holdsTemp == null && relatedTemp == null) {
				sAnnot += ("\nBearer: " + bearerTemp + "\nQuality: " + qualityTemp  + " (" + descriptionTemp + ")");
			} else if (holdsTemp != null && relatedTemp == null) {
				sAnnot += ("\nBearer: " + bearerTemp
						+ "\nHolds in Relation to: " + holdsTemp
						+ "\nQuality: " + qualityTemp + " (" + descriptionTemp + ")");
			} else if (holdsTemp == null && relatedTemp != null) {
				sAnnot += ("\nBearer: " + bearerTemp + "\nQuality: "
						+ qualityTemp + " (" + descriptionTemp + ")" + "\nRelated Entity: " + relatedTemp);
			} else { // (holdsTemp!=null && relatedTemp!=null)
				sAnnot += ("\nBearer: " + bearerTemp
						+ "\nHolds in Relation to: " + holdsTemp
						+ "\nQuality: " + qualityTemp 
						+ " (" + descriptionTemp + ")"
						+ "\nRelated Entity: " + relatedTemp);
			}
			*/
		}
			
		
		return sAnnot;
	}
	/*.................................................................................................................*/
	public boolean hasURIMapElement(){
		ListableVector vectors = data.getFile().getFileElements();
		if (vectors !=null && vectors.size()>0) {
	   		for (int i = 0; i<vectors.size(); i++){
	   			if (vectors.elementAt(i) instanceof URIMap){
	   				return true;
	   			}
	   		}		
	   	}
		return false;
	}
	/*.................................................................................................................*/
	public String getName() {
		return "Nexml Viewer";
	}
	/*.................................................................................................................*/
	/** returns an explanation of what the module does. */
	public String getExplanation() {
		return "Displays Phenex-genereated NeXML annotations.";
	}
	/*.................................................................................................................*/
	private String statesExplanation(int column, int row) {
		String s = "";
		if (data.getClass() == CategoricalData.class) {
			s += ":  ";
			long state = ((CategoricalData) data).getState(column, row);
			CategoricalData cData = (CategoricalData) data;
			for (int i = 0; i <= CategoricalState.maxCategoricalState; i++)
				if (cData.hasStateName(column, i)) {
					s += "(" + cData.getSymbol(i) + ")";
					if (CategoricalState.isElement(state, i))
						s += "*";
					s += " " + cData.getStateName(column, i);
					s += "; ";
				}
		}
		return s;
	}
}