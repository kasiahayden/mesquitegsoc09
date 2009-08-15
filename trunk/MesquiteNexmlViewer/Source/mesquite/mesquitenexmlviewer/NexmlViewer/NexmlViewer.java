package mesquite.mesquitenexmlviewer.NexmlViewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;
import mesquite.lib.table.*;
import mesquite.mesquitenexmlviewer.lib.URIMap;
import mesquite.categ.lib.CategoricalData;
import mesquite.categ.lib.CategoricalState;
import att.grappa.*;
import att.grappa.Parser;


/** 
 * The main class for MesquiteNexmlViewer, where NeXML annotations are generated by pulling data from 
 * the hashmaps in URIMap. Also automatically generates Graphviz dot graphs based on the NeXML annotations.
 * @author Kasia Hayden
 * @version 2009-08-15
 * */

public class NexmlViewer extends DataWindowAssistantI {
	MesquiteTable table;
	CharacterData data;
	
	TableTool nexmlTool;
	TableTool dotTool;

	int cCurrent;
	int tCurrent;
	String cellExplanation;
	StringBuffer esb = new StringBuffer(100);
	
	NameReference notesNameRef = NameReference.getNameReference("notes");
	
	URIMap UriMap = null;
	
	GrappaPanel grappaPanel;
	public DemoFrame  frame  = null;
	
	public Map<String, String> nodeHM = new HashMap<String, String>();
	
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {
			addMenuItem("Display NeXML", MesquiteModule.makeCommand(
					"displayNeXML", this));
			if (containerOfModule() instanceof MesquiteWindow) {
				nexmlTool = new TableTool(this, "DisplayNeXML", getPath(), "nexml.gif", 1,1,"Displays Phenex-generated NeXML annotations", "Displays Phenex-generated NeXML annotations in the footnote box.", MesquiteModule.makeCommand("displayNeXML", this), null, null);
				nexmlTool.setWorksOnColumnNames(true);
				nexmlTool.setWorksOnRowNames(true);
				((MesquiteWindow)containerOfModule()).addTool(nexmlTool);
				nexmlTool.setPopUpOwner(this);
				setUseMenubar(false); //menu available by touching button
			}
			
			addMenuItem("Display dot graph", MesquiteModule.makeCommand(
					"displayDotGraph", this));
			if (containerOfModule() instanceof MesquiteWindow) {
				dotTool = new TableTool(this, "displayDotGraph", getPath(), "dot.gif", 1,1,"Displays dot graph of NeXML annotations", "Displays dot graphs of Phenex-generated NeXML annotations.", MesquiteModule.makeCommand("displayDotGraph", this), null, null);
				dotTool.setWorksOnColumnNames(true);
				dotTool.setWorksOnRowNames(true);
				((MesquiteWindow)containerOfModule()).addTool(dotTool);
				dotTool.setPopUpOwner(this);
				setUseMenubar(false); //menu available by touching button
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
	public void tableSelectionChanged() {
	}
	/*.................................................................................................................*/
	public void colorsLegendGoAway() {
	}
	/*.................................................................................................................*/
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
		if (checker.compare(this.getClass(),
				"Displays Phenex-generated NeXML annotations",
				"[name of module]", commandName, "displayNeXML")) {
			if (table != null && data != null && hasURIMapElement() == true) {
				this.containerOfModule().setAnnotation(getFootnoteAnnotation(),
						copyCellExplanation(cCurrent, tCurrent));
			}
		}
		else if (checker.compare(this.getClass(),
				"Displays dot graph of NeXML annotations", "[name of module]",
				commandName, "displayDotGraph")) {
			
			String dotPath = null;
			try {
				dotPath = this.getProject().getHomeDirectoryName();
				dotPath += "dot_temp.dot";
				BufferedWriter out = new BufferedWriter(new FileWriter(dotPath));
				out.write(getDotGraphElements());
				
				getDotGraphElements();
				out.close();
			} catch (IOException e) {
				logln("Problem writing dot file.");
			}
			
			Graph newGraph = null;
			try {
				FileInputStream input = new FileInputStream(dotPath);
				Parser graphParser = new Parser(input, System.err);
				graphParser.parse();
				newGraph = graphParser.getGraph();
			} catch (Exception e) {
				logln("Grappa failed.");
			}
			
			String columnName = table.getColumnNameText(cCurrent);
			String rowName = table.getRowNameText(tCurrent);
			String graphTitle = rowName.trim() + "  |  " + columnName.trim();
			frame = new DemoFrame(newGraph, graphTitle);

		} else{
			return super.doCommand(commandName, arguments, checker);
		}

		return null;
	}
	/*.................................................................................................................*/
	/** Gets the correct URI labels from URIMap for the given cell clicked. */
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
			String descriptionTemp = tempValHM.get("description"); //Doesn't need to be fished from URIMap.URIMap- is in masterMap
			
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
						+ "    Quality: " +		 qualityTemp + " (" + descriptionTemp + ")");
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
	/** Generates a Graphviz dot graph based on the Phenex-generated NeXML annotations corresponding with the cell clicked. */
	public String getDotGraphElements(){
		String graph = null;
		
		String columnName = table.getColumnNameText(cCurrent);
		String rowName = table.getRowNameText(tCurrent);
	
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
			
			String taxonNode = rowName;
			
			int nodeCount = 0;
			
			if (bearerTemp != null){
				++nodeCount;		
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, bearerTemp);
			}
			if (holdsTemp != null){
				++nodeCount;
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, holdsTemp);
			}
			if (qualityTemp != null){
				++nodeCount;
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, qualityTemp);
			}
			if (relatedTemp != null){
				++nodeCount;
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, relatedTemp);
			}
			if (descriptionTemp != null){
				++nodeCount;
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, descriptionTemp);
			}
			if (nodeHM.size() == nodeCount){
			}
			if (nodeHM.size() != 0){
				double xFromLeft = 0.0;
				double yFromBottom = 0.0;
				double nodeInterval = 100.0;
				String shape = "ellipse";
				double width = 1.0;
				double height = 0.50;
				
				graph = "graph G {";
				
				//Root node
				width = (taxonNode.length() * (.125));
				yFromBottom += nodeCount * nodeInterval;
				graph += "\"" + taxonNode + "\"" + " [shape=" + shape + ", pos=\"" + xFromLeft + "," + yFromBottom + "\", width=\"" + width + "\", height=\"" + height + "\"]; ";	
				
				for (int nodeNum = 1; nodeNum <= nodeCount; nodeNum++){
					String nodeKey = "" + nodeNum;
					width = (nodeHM.get(nodeKey).length() * (.125));
					yFromBottom -= nodeInterval;
					graph += "n" + nodeNum + " [shape=" + shape + ", pos=\"" + xFromLeft + "," + yFromBottom + "\", width=\"" + width + "\", height=\"" + height + "\" " + "label=\"" + nodeHM.get(nodeKey) + "\"]; ";
				}
				
				//Link nodes
				double lineTop = nodeCount * nodeInterval;
				double lineBottom = 0;
				double halfNodeHeight = (height/2) * 75;
				graph += "\"" + taxonNode + "\"";
				for (int nodeNum = 1; nodeNum <= nodeCount; nodeNum++){
					lineTop -= halfNodeHeight;
					lineBottom = lineTop - nodeInterval + (2 * halfNodeHeight);
					graph += " -- ";
					graph += "n" + nodeNum + " [pos=\"" + xFromLeft + "," + lineTop + " " + xFromLeft + "," + lineBottom + "\"]; ";
					graph += "n" + nodeNum + " ";
					lineTop = lineBottom - halfNodeHeight;
				}
				graph += ";}";	
				
			}
		}
		return graph;
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
	/* ======================================================================== */
	/** Class responsible for displaying Graphviz dot graph, through the Grappa Java graph drawing package.*/
	class DemoFrame extends JFrame{
		GrappaPanel gp;
		Graph graph = null;
		JPanel panel = null;

		public DemoFrame(Graph graph, String graphTitle) {
			super(graphTitle);
			this.graph = graph;

			setSize(600, 400);
			setLocation(100, 100);

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent wev) {
					Window w = wev.getWindow();
					w.setVisible(false);
					w.dispose();
				}
			});

			JScrollPane jsp = new JScrollPane();

			gp = new GrappaPanel(graph);
			gp.addGrappaListener(new GrappaAdapter());
			gp.setScaleToFit(true);

			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;

			panel = new JPanel();
			panel.setLayout(gbl);

			getContentPane().add("Center", jsp);
			getContentPane().add("West", panel);

			setVisible(true);
			jsp.setViewportView(gp);
		}
	}
}

