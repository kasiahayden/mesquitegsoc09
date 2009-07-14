package mesquite.mesquitenexmlviewer.OboManager;

import java.util.*;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import mesquite.lib.*;
import mesquite.lib.duties.*;

import mesquite.mesquitenexmlviewer.lib.*;

import mesquite.nexml.InterpretNEXML.*;

public class OboManager extends FileInit {
	public org.w3c.dom.Document domDoc = null;
	public URIMap uriMap;
	Properties properties = null;

	/*.................................................................................................................*/
	public Class getDutyClass() {
		return OboManager.class;
	}
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition,
			boolean hiredByName) {

		return true;
	}
	/*.................................................................................................................*/
	public boolean isSubstantive() {
		return true;
	}
	/*.................................................................................................................*/
	public boolean isPrerelease() {
		return false;
	}
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments,
			CommandChecker checker) {

		return null;
	}
	/*.................................................................................................................*/
	/**
	 * A method called immediately after the file has been read in or completely
	 * set up (if a new file).
	 */
	public void fileReadIn(MesquiteFile f) {
		 //logln("OboManager's fileReadIn method ran");
		if (f == null || f.getProject() == null)
			return;

		//properties = new Properties();
		properties = ObjectConverter.getPredicateHandlerMapping();
		String path = (String)(properties.get("path"));
		//logln("---------path from properties: " + path);
		
		FileInputStream fs = null;
		try {
			//fs = new FileInputStream((String)(properties.get("path")));
			fs = new FileInputStream(path);
			domDoc = parse(fs);
		} catch (Exception e) {
			e.printStackTrace();
			logln("Hopefully this line doesn't print.");
		}
		
		uriMap = new URIMap(domDoc);
		if (uriMap!=null) {
			uriMap.setName(this.toString());//Obo Manager and a unique id
			uriMap.addToFile( f, getProject(), findElementManager(URIMap.class));
			//uriMap.addToFile( getProject().getHomeFile(), getProject(), findElementManager(URIMap.class)); 
			getProject().addFileElement(uriMap);
		}
		
		//logln("-----OboManager----");
		//logln("This file is: " + f.toString());
		//logln("uriMap belongs to: " + uriMap.getFileName());
		//logln("getProject().getHomeFile(): " + getProject().getHomeFile().toString());
		//logln("getProject(): " + getProject().toString());
		//getProject().getHomeFile(): File "Vari_new.xml.nex" in directory "/home/kasia/workspace/Mesquite Project/Mesquite_Folder/", id 0
		//getProject(): mesquite.lib.MesquiteProject@1ee2c5
		
		/*
		//Temporary- to get it to skip everything below
		if (f.getPath() != "/home/kasia/workspace/Mesquite Project/Mesquite_Folder/Vari_new.xml.nex"){
			return;
		}
		*/

		uriMap.FillOtuHM();
		uriMap.FillCharHM();
		logln("Loading ontologies...");
		uriMap.FillStateHM();
		
		
		String tempCharName = null;
		String tempRowName = null;
		String tempStateId = null;
		
		int rowNumber = 1;
		List<Element> charactersElements = getChildrenByTagName(domDoc.getDocumentElement(), "characters");
		for (Element thisElement : charactersElements) {//each characters element in the tree (there's one)
			List<Element> matrixElements = getChildrenByTagName(thisElement, "matrix"); 
			for (Element thisE : matrixElements) {//each matrix element- only one
				List<Element> rowElements = getChildrenByTagName(thisE, "row"); //list of row elements
				for (Element rowElement : rowElements) { //for each row in the list of rows
					tempRowName = rowElement.getAttribute("otu").trim();
					tempRowName = uriMap.otuHM.get(tempRowName);
					//***:Later on should create method to go back and make entries for the otu rows that weren't called
					List<Element> cellElements = getChildrenByTagName(rowElement, "cell");
					for (Element cellElement : cellElements){ //for cell in the list of cells 
						tempCharName = cellElement.getAttribute("char").trim();
						tempCharName = uriMap.charHM.get(tempCharName);
						tempStateId = cellElement.getAttribute("state").trim();
						
						Collection<String> keyCoord = new ArrayList<String>();
						keyCoord.add(tempCharName);
						keyCoord.add(tempRowName);
						
						Map<String, String> valCell = uriMap.stateHM.get(tempStateId);
                       
						/*
						String bTest = valCell.get("bearer");
                        String hTest = valCell.get("holds");
                        String qTest = valCell.get("quality");
                        String rTest = valCell.get("related");
                        //logln("Result of calling masterMap: Bearer: " + bTest + " Quality: " + qTest);

                                        if (hTest!= null || rTest!=null){
                                                logln("Column: " + tempCharName + " Row: " + tempRowName);
                                                logln("Bearer: " + bTest + "  Holds in Relation to: " + hTest + "  Quality: " + qTest + "  Related Entity: " + rTest);
                                        }
	    		    	*/
                                        
	    		    	uriMap.masterMap.put(keyCoord, valCell);
						
						tempCharName = null;
						tempStateId = null;
					}
					tempRowName = null;
					++rowNumber;
				}
			}
		}
	}
	
	/*.................................................................................................................*/
	public static org.w3c.dom.Document parse(InputStream inputStream)
			throws ParserConfigurationException, SAXException, IOException {
		return getDocumentBuilder().parse(inputStream);
	}
	/*.................................................................................................................*/
	private static DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		return documentBuilder;
	}
	/*.................................................................................................................*/
	public List<Element> getChildrenByTagName(Element element, String tagName) {
		List<Element> result = new ArrayList<Element>();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			String localName = children.item(i).getNodeName();
			if (null != localName && localName.equals(tagName)) {
				result.add((Element) children.item(i));
			}
		}
		return result;
	}
	/*.................................................................................................................*/
	public String getName() {
		return "Obo Manager";
	}
	/*.................................................................................................................*/
	/** returns an explanation of what the module does. */
	public String getExplanation() {
		return "Manages .obo ontology files when importing a NeXML file.";
	}
}