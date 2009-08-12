package mesquite.mesquitenexmlviewer.NexmlViewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JComponent;
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


/* ======================================================================== */
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
	
	PictureWindow pictureWindow;
	String pathToPicture;
	
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
					
			
			/*
			resetContainingMenuBar();
			resetAllWindowsMenus();
			pictureWindow.setVisible(true);


			setModuleWindow(pictureWindow);
			resetContainingMenuBar();
			resetAllWindowsMenus();
			*/
			
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
		if (checker.compare(this.getClass(),
				"Displays Phenex-generated NeXML annotations",
				"[name of module]", commandName, "displayNeXML")) {
			if (table != null && data != null && hasURIMapElement() == true) {
				this.containerOfModule().setAnnotation(getFootnoteAnnotation(),
						copyCellExplanation(cCurrent, tCurrent));
			}
		}
		/*
		 * pictureWindow = new Pic//getDotGraphElements();tureWindow(this);
		 * setModuleWindow(pictureWindow); String pic =
		 * "/home/kasia/Pictures/img_3148.jpg"; pictureWindow.setPath(pic);
						
		 * //pictureWindow.addToWindow(grappaPanel);
		 * //pictureWindow.setCurrentObject(newGraph);
		 */

		else if (checker.compare(this.getClass(),
				"Displays dot graph of NeXML annotations", "[name of module]",
				commandName, "displayDotGraph")) {
			
			String dotPath = null;
			try {
				dotPath = this.getProject().getHomeDirectoryName();
				// logln("Path is: " + thisPath);
				dotPath += "dot_temp.dot";
				BufferedWriter out = new BufferedWriter(new FileWriter(dotPath));
				//out.write("graph G {n0 [shape=ellipse, pos=\"536,112\", width=\"0.75\", height=\"0.50\"];n1 [shape=ellipse, pos=\"614,112\", width=\"0.92\", height=\"0.50\"]; n2 [shape=diamond, style=filled, color=lightgrey, pos=\"383,112\", width=\"0.89\", height=\"0.67\"];n3 [shape=diamond, style=filled, color=lightgrey, pos=\"462,112\", width=\"0.81\", height=\"0.67\"];n0 -- n1 -- n2 -- n3;}");
				//out.write("graph G {n0 [shape=ellipse, pos=\"240,112\", width=\"0.75\", height=\"0.50\"];n1 [shape=ellipse, pos=\"0,112\", width=\"0.92\", height=\"0.50\"]; n2 [shape=diamond, style=filled, color=lightgrey, pos=\"80,112\", width=\"0.89\", height=\"0.67\"];n3 [shape=diamond, style=filled, color=lightgrey, pos=\"160,112\", width=\"0.81\", height=\"0.67\"];n0 -- n1 -- n2 -- n3;}");
				//logln("Static graph is: graph G {n0 [shape=ellipse, pos=\"240,112\", width=\"0.75\", height=\"0.50\"];n1 [shape=ellipse, pos=\"0,112\", width=\"0.92\", height=\"0.50\"]; n2 [shape=diamond, style=filled, color=lightgrey, pos=\"80,112\", width=\"0.89\", height=\"0.67\"];n3 [shape=diamond, style=filled, color=lightgrey, pos=\"160,112\", width=\"0.81\", height=\"0.67\"];n0 -- n1 -- n2 -- n3;}");
				out.write(getDotGraphElements());
				
				getDotGraphElements();
				out.close();
			} catch (IOException e) {
				logln("Problem writing dot file.");
			}

			// String dotFile =
			// "/home/kasia/workspace/Mesquite Project/Mesquite_Folder/dot_temp.dot";
			// String dotFile =
			// "/home/kasia/projects/zgrviewer/data/graphs/example.dot";
			// String dotFile =
			// "/home/kasia/projects/zgrviewer/data/graphs/ERTemp.dot";
			// String dotFile = "/opt/jdk1.6.0_13/grappa/DEMO/cluster.dot";
			// String dotFile = "/opt/jdk1.6.0_13/grappa/DEMO/ER.dot";
			// String dotFile =
			// "graph G {n0 [shape=ellipse, pos=\"536,112\", width=\"0.75\", height=\"0.50\"];n1 [shape=ellipse, pos=\"614,112\", width=\"0.92\", height=\"0.50\"]; n2 [shape=diamond, style=filled, color=lightgrey, pos=\"383,112\", width=\"0.89\", height=\"0.67\"];n3 [shape=diamond, style=filled, color=lightgrey, pos=\"462,112\", width=\"0.81\", height=\"0.67\"];n0 -- n1 -- n2 -- n3;}";

			Graph newGraph = null;
			try {
				FileInputStream input = new FileInputStream(dotPath);
				Parser graphParser = new Parser(input, System.err);
				graphParser.parse();
				newGraph = graphParser.getGraph();
				// logln("Grappa working ----------------");
			} catch (Exception e) {
				logln("Grappa failed.");
			}
			
			frame = new DemoFrame(newGraph);

		} else{
			return super.doCommand(commandName, arguments, checker);
		}
		/* Kasia: Need command for displaying annotations
		pictureWindow = new PictureWindow(this);
		setModuleWindow(pictureWindow);
		String pic = "/home/kasia/Pictures/img_3148.jpg";
		pictureWindow.setPath(pic);
		*/
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
			//boolean descriptionPresent = false;
			
			if (bearerTemp != null){
				++nodeCount;		
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, bearerTemp);
				logln("bearerTemp: " + bearerTemp);
			}
			if (holdsTemp != null){
				++nodeCount;
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, holdsTemp);
				logln("holdsTemp: " + holdsTemp);
			}
			if (qualityTemp != null){
				++nodeCount;
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, qualityTemp);
				logln("qualityTemp: " + qualityTemp);
			}
			if (relatedTemp != null){
				++nodeCount;
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, relatedTemp);
				logln("relatedTemp: " + relatedTemp);
			}
			if (descriptionTemp != null){
				++nodeCount;
				String intNodeCount = "" + nodeCount;
				nodeHM.put(intNodeCount, descriptionTemp);
				logln("descriptionTemp: " + descriptionTemp);
			}
			
			logln("nodeCount = " + nodeCount);
			
			
			if (nodeHM.size() == nodeCount){
				logln("nodeHM == nodeCount ---------");
			}
			else {
				logln("nodeHM != nodeCount");
				logln("nodeHM.size = " + nodeHM.size() + "and nodeCount = " + nodeCount);
			}
			///*
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
				logln("width: " + width + " for " + taxonNode + "\nxFromLeft: " + xFromLeft);
				yFromBottom += nodeCount * nodeInterval;
				graph += "\"" + taxonNode + "\"" + " [shape=" + shape + ", pos=\"" + xFromLeft + "," + yFromBottom + "\", width=\"" + width + "\", height=\"" + height + "\"]; ";
				//xFromLeft = (width/2.0) * 20;		
				
				for (int nodeNum = 1; nodeNum <= nodeCount; nodeNum++){
					String nodeKey = "" + nodeNum;
					width = (nodeHM.get(nodeKey).length() * (.125));
					logln("width: " + width + " for " + nodeHM.get(nodeKey) + "\nxFromLeft: " + xFromLeft);
					yFromBottom -= nodeInterval;
					//xFromLeft += (taxonNode.length() * (.125) * 50.0);
					//xFromLeft += (width/2.0) * 20;
					graph += "n" + nodeNum + " [shape=" + shape + ", pos=\"" + xFromLeft + "," + yFromBottom + "\", width=\"" + width + "\", height=\"" + height + "\" " + "label=\"" + nodeHM.get(nodeKey) + "\"]; ";
					//xFromLeft += (width/2.0) * 20;
				}
				/*
				
				
				for (int nodeNum = 1; nodeNum <= nodeCount; nodeNum++){
					String nodeKey = "" + nodeNum;
					double labelLength = nodeHM.get(nodeKey).length();
					width = (labelLength * (.125));
					logln("width: " + width + " for " + nodeHM.get(nodeKey));
					if (nodeNum > 1){
						xFromLeft += (width/2);
					}
					graph += "\"" + nodeHM.get(nodeKey) + "\"" + " [shape=" + shape + ", pos=\"" + xFromLeft + "," + yFromBottom + "\", width=\"" + width + "\", height=\"" + height + "\"]; ";
					xFromLeft += (80 + (width/2));
				}
				*/
				
				/*
				//Root node
				yFromBottom = 112;
				xFromLeft = (xFromLeft - 80)/2; //centering
				graph += "\"" + taxonNode + "\"" + " [shape=" + shape + ", pos=\"" + xFromLeft + "," + yFromBottom + "\"," + "label=\"" + taxonNode + "\"]; ";
				*/
				
				
				
				/*
				//Link nodes
				for (int nodeNum = 1; nodeNum <= nodeCount; nodeNum++){
					String nodeKey = "" + nodeNum;
					String nodeName = "\"" + nodeHM.get(nodeKey) + "\"";
					graph += "\"" + taxonNode + "\"" + " -- " + "n" + nodeNum + "[headclip=true];";	// tailclip=false];";
				}
				graph += "}";
				*/
				
				/*
				//Link nodes
				for (int nodeNum = 1; nodeNum <= nodeCount; nodeNum++){
					String nodeKey = "" + nodeNum;
					String nodeName = "\"" + nodeHM.get(nodeKey) + "\"";
					graph += "\"" + taxonNode + "\"" + " -- " + nodeName + ";";
				}
				graph += "}";
				
				
				*/
				
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
			
			//[pos="551,188 565,171 587,145 600,129"];
			
			logln("Generated graph from most recent click is:\n" + graph);
			
			
			/*
			 * Generated graph from most recent click is:
graph G {n2 [shape=ellipse, pos="0.0,112.0", width="0.92", height="0.5"]; n2 [shape=ellipse, pos="80.0,112.0", width="0.92", height="0.5"]; n2 -- n2 -- ;}
			 */
			
					
			//*/
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
	public class PictureWindow extends MesquiteWindow implements Commandable  {
		String path;
		Image image;
		ImagePanel imagePanel;
		MediaTracker mt = null;
			boolean errored = true;
			int count =0;
		
		public PictureWindow (NexmlViewer nexmlViewer){
			super(nexmlViewer, true); //infobar
	      		setWindowSize(64,64);
	      		setMinimalMenus(true);
			//getGraphicsArea().setLayout(new BorderLayout());
			addToWindow(imagePanel = new ImagePanel(this));
			//addToWindow("ghlgj");
			imagePanel.setSize(64, 64);
			setLocation(0,0);
			imagePanel.setVisible(true);
			resetTitle();
	      		setWindowSize(64,64);
		}
		/*.................................................................................................................*/
		/** When called the window will determine its own title.  MesquiteWindows need
		to be self-titling so that when things change (names of files, tree blocks, etc.)
		they can reset their titles properly*/
		public void resetTitle(){
			setTitle("Picture: " + path); //TODO: what tree?
		}
		public void checkSize(){
			
		}
		public boolean setPath(String path){
			this.path = path;
			image = MesquiteImage.getImage(path);
			if (MesquiteImage.waitForImageToLoad(image, this.getOuterContentsArea())){
				imagePanel.setImage(image);
				imagePanel.repaint();
				setResizable(true);
		      	if (image!=null) {
		      		setWindowSize(image.getWidth(imagePanel),image.getHeight(imagePanel));
		      		imagePanel.setSize(image.getWidth(imagePanel),image.getHeight(imagePanel));
		      	}
				setResizable(false);
				resetTitle();
				return true;
			}
			return false;
		}
	}
	/* ======================================================================== */
	/** The Panel containing the Mesquite logo on the startup window */
	class ImagePanel extends Panel {
		Image pic;
		PictureWindow pw;
		public ImagePanel (PictureWindow pw) {
			setBackground(Color.white);
			this.pw = pw;
		}
		/*.................................................................................................................*/
		public void paint(Graphics g) {
		   	if (MesquiteWindow.checkDoomed(this))
		   		return;
				g.drawImage(pic,0,0,(ImageObserver)this);
			MesquiteWindow.uncheckDoomed(this);
		}
		public void setImage(Image i){
			pic = i;
		}
	
	}
	
	/* ======================================================================== */
	
	void doDemo(InputStream input) {
		Parser program = new Parser(input, System.err);
		try {
			// program.debug_parse(4);
			program.parse();
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
			ex.printStackTrace(System.err);
			System.exit(1);
		}
		Graph graph = null;

		graph = program.getGraph();

		System.err.println("The graph contains "
				+ graph.countOfElements(Grappa.NODE | Grappa.EDGE
						| Grappa.SUBGRAPH) + " elements.");

		graph.setEditable(true);
		// graph.setMenuable(true);
		//graph.setErrorWriter(new PrintWriter(System.err, true));
		// graph.printGraph(new PrintWriter(System.out));

		System.err.println("bbox="
				+ graph.getBoundingBox().getBounds().toString());

		frame = new DemoFrame(graph);

		//frame.show();
	}
	class DemoFrame extends JFrame implements ActionListener {
		GrappaPanel gp;
		Graph graph = null;

		JButton layout = null;
		JButton printer = null;
		JButton draw = null;
		JButton quit = null;
		JPanel panel = null;

		public DemoFrame(Graph graph) {
			super("DemoFrame");
			this.graph = graph;

			setSize(600, 400);
			setLocation(100, 100);

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent wev) {
					Window w = wev.getWindow();
					w.setVisible(false);
					w.dispose();
					System.exit(0);
				}
			});

			JScrollPane jsp = new JScrollPane();
			//jsp.getViewport().setBackingStoreEnabled(true);

			gp = new GrappaPanel(graph);
			gp.addGrappaListener(new GrappaAdapter());
			gp.setScaleToFit(true);//Kasia originally false

			java.awt.Rectangle bbox = graph.getBoundingBox().getBounds();

			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTHWEST;

			panel = new JPanel();
			panel.setLayout(gbl);

			draw = new JButton("Draw");
			gbl.setConstraints(draw, gbc);
			panel.add(draw);
			draw.addActionListener(this);

			layout = new JButton("Layout");
			gbl.setConstraints(layout, gbc);
			panel.add(layout);
			layout.addActionListener(this);

			printer = new JButton("Print");
			gbl.setConstraints(printer, gbc);
			panel.add(printer);
			printer.addActionListener(this);

			quit = new JButton("Quit");
			gbl.setConstraints(quit, gbc);
			panel.add(quit);
			quit.addActionListener(this);

			getContentPane().add("Center", jsp);
			getContentPane().add("West", panel);

			setVisible(true);
			jsp.setViewportView(gp);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JButton) {
				JButton tgt = (JButton) e.getSource();
				if (tgt == draw) {
					graph.repaint();
				} else if (tgt == quit) {
					System.exit(0);
				} else if (tgt == printer) {
					graph.printGraph(System.out);
					System.out.flush();
				} else if (tgt == layout) {
					Object connector = null;
					try {
						connector = Runtime.getRuntime().exec("/opt/jdk1.6.0_13/grappa/DEMO/formatDemo");
					} catch (Exception ex) {
						System.err
								.println("Exception while setting up Process: "
										+ ex.getMessage()
										+ "\nTrying URLConnection...");
						connector = null;
					}
					if (connector == null) {
						try {
							connector = (new URL(
									"http://www.research.att.com/~john/cgi-bin/format-graph"))
									.openConnection();
							URLConnection urlConn = (URLConnection) connector;
							urlConn.setDoInput(true);
							urlConn.setDoOutput(true);
							urlConn.setUseCaches(false);
							urlConn.setRequestProperty("Content-Type",
									"application/x-www-form-urlencoded");
						} catch (Exception ex) {
							System.err
									.println("Exception while setting up URLConnection: "
											+ ex.getMessage()
											+ "\nLayout not performed.");
							connector = null;
						}
					}
					if (connector != null) {
						if (!GrappaSupport.filterGraph(graph, connector)) {
							System.err
									.println("ERROR: somewhere in filterGraph");
						}
						if (connector instanceof Process) {
							try {
								int code = ((Process) connector).waitFor();
								if (code != 0) {
									System.err
											.println("WARNING: proc exit code is: "
													+ code);
								}
							} catch (InterruptedException ex) {
								System.err
										.println("Exception while closing down proc: "
												+ ex.getMessage());
								ex.printStackTrace(System.err);
							}
						}
						connector = null;
					}
					graph.repaint();
				}
			}
		}


	}
	
   
}

