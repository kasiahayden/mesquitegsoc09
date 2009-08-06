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
import java.io.FileInputStream;
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
	
	TableTool xmlTool;

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
			Graph newGraph = null;
			try {
				  FileInputStream input = new FileInputStream(dotFile);
				  Parser graphParser = new Parser(input, System.err);
				  graphParser.parse();
				  newGraph = graphParser.getGraph();
				  logln("Grappa working ----------------");
			}
			catch(Exception e) {
				logln("Grappa unsuccessful.");
			}


			pictureWindow = new PictureWindow(this);
			setModuleWindow(pictureWindow);
			String pic = "/home/kasia/Pictures/img_3148.jpg";
			//pictureWindow.setPath(pic);
			//Component graphComponent = (Component)newGraph;
			//pictureWindow.graphics[0].add(comp)
			//GrappaPanel grappaPanel = new GrappaPanel();
			pictureWindow.addToWindow(grappaPanel);
			pictureWindow.setCurrentObject(newGraph);
			
			frame = new DemoFrame(newGraph);
			//frame.show();
			
			
				 		resetContainingMenuBar();
				 		resetAllWindowsMenus();
						pictureWindow.setVisible(true);


		 		setModuleWindow(pictureWindow);
		 		resetContainingMenuBar();
		 		resetAllWindowsMenus();
			
			//Keep:
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

		frame.show();
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
			jsp.getViewport().setBackingStoreEnabled(true);

			gp = new GrappaPanel(graph);
			gp.addGrappaListener(new GrappaAdapter());
			gp.setScaleToFit(false);

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

