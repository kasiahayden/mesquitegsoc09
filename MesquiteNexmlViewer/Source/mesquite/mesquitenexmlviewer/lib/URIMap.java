package mesquite.mesquitenexmlviewer.lib;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import mesquite.lib.FileElement;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteMessage;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteThread;

/** 
 * URIMap is where all of the URIs are stored after being fetched from online obo files. 
 * @author Kasia Hayden
 * @version 2009-08-15
 * */

public class URIMap extends FileElement {
	public org.w3c.dom.Document domDoc = null;
	public static Map<String, String> URIMap = new HashMap<String, String>();
	public Map<Collection<String>, Map<String, String>> masterMap = new HashMap<Collection<String>, Map<String, String>>();
	public Map<String, String> otuHM = new HashMap<String, String>();
	public Map<String, String> charHM = new HashMap<String, String>();
	public Map<String, Map<String, String>> stateHM = new HashMap<String, Map<String, String>>();
	public boolean internetFail = false;
	
	/*.................................................................................................................*/
	public URIMap(org.w3c.dom.Document domDoc){
		this.domDoc = domDoc;
	}
	/*.................................................................................................................*/
	/** Populates the hashmap of otu elements.*/
	public void FillOtuHM(){
		List<Element> otusElements = getChildrenByTagName(domDoc.getDocumentElement(), "otus"); //otus is the tag name
		for (Element thisElement : otusElements) {//each otus in the tree (there's one)
			List<Element> OtuElements = getChildrenByTagName(thisElement, "otu");
			for (Element thisE : OtuElements) {//each otu element in the tree
				//get id for otu element, which corresponds to a row
				String otuId = thisE.getAttribute("id").trim();
				String otuLabel = thisE.getAttribute("label").trim();
				otuHM.put(otuId, otuLabel);
			}
		}
	}
	/*.................................................................................................................*/		
	/** Populates the hashmap of characters elements.*/
	public void FillCharHM(){
		List<Element> charactersElements = getChildrenByTagName(domDoc.getDocumentElement(), "characters"); //characters is the tag name
		for (Element thisElement : charactersElements) {//each characters element in the tree (there's one)
			List<Element> formatElements = getChildrenByTagName(thisElement, "format");
			for (Element thisE : formatElements) {//only one format tag in formatElements
				List<Element> charElements = getChildrenByTagName(thisE, "char");
				for (Element charElement : charElements){//for each char tag in the list of char tags
					String charId = charElement.getAttribute("id").trim();
					String charLabel = charElement.getAttribute("label").trim();
					charHM.put(charId, charLabel);
				}
			}
		}
	}
	/*.................................................................................................................*/
	/** Populates the hashmap of state elements, which fill the URIMap hashmap.*/
	public void FillStateHM(){
		String stateId = null;
		String descriptionLabel = null;
		String bearerTyperef = null;
		String holdsTyperef = null;
		String qualityTyperef = null;
		String relatedETyperef = null;
		String qualifierRelLabel = null;
		List<Element> charactersElements = getChildrenByTagName(domDoc.getDocumentElement(), "characters"); //characters is the tag name
		for (Element thisElement : charactersElements) {//each characters element in the tree (there's one)
			List<Element> formatElements = getChildrenByTagName(thisElement, "format");
			for (Element thisE : formatElements) {//only one format tag in formatElements
				List<Element> statesElements = getChildrenByTagName(thisE, "states");//more than one states element
				for (Element statesElement : statesElements) {
					List<Element> stateElements = getChildrenByTagName(statesElement, "state");
					for (Element stateElement : stateElements){
						stateId = stateElement.getAttribute("id").trim();
						descriptionLabel = stateElement.getAttribute("label").trim();
						List<Element> metaElements = getChildrenByTagName(stateElement, "meta");
						for (Element metaElement : metaElements){//only one
							List<Element> phenotypeElements = getChildrenByTagName(metaElement, "phenotype");
							for (Element phenotypeElement : phenotypeElements){
								List<Element> phenoCharElements = getChildrenByTagName(phenotypeElement, "phenotype_character");
								for (Element phenoCharElement : phenoCharElements){
									List<Element> bearerElements = getChildrenByTagName(phenoCharElement, "bearer");
									for (Element bearerElement : bearerElements){//only one bearer element in list
										List<Element> bearTyperefElement = getChildrenByTagName(bearerElement, "typeref");
										for (Element bearTyperefEl : bearTyperefElement){
											bearerTyperef = bearTyperefEl.getAttribute("about").trim();	
											if (!URIMap.containsKey(bearerTyperef)){
												URIMap.put(bearerTyperef.trim(), null);
											}
											//check for qualifier tag
											List<Element> qualifierTag = getChildrenByTagName(bearTyperefEl, "qualifier");
											if (qualifierTag!=null){
												for (Element qualifierTagElement : qualifierTag){
													qualifierRelLabel = qualifierTagElement.getAttribute("relation").trim();
													List<Element> holdsIRT = getChildrenByTagName(qualifierTagElement, "holds_in_relation_to");
													for (Element holdsIRTElement : holdsIRT){//only one
														List<Element> holdsTyperefElement = getChildrenByTagName(holdsIRTElement, "typeref");
														for (Element holdsTyperefEl : holdsTyperefElement){
															holdsTyperef = holdsTyperefEl.getAttribute("about").trim();
															if (!URIMap.containsKey(holdsTyperef)){
																URIMap.put(holdsTyperef.trim(), null);
															}
														}
													}
												}
											}
										}
									}
									List<Element> qualityElements = getChildrenByTagName(phenoCharElement, "quality");
									for (Element qualityElement : qualityElements){//only one quality element in list
										List<Element> qualTyperefElement = getChildrenByTagName(qualityElement, "typeref");
										for (Element qualTyperefEl : qualTyperefElement){
											qualityTyperef = qualTyperefEl.getAttribute("about").trim();
											if (!URIMap.containsKey(qualityTyperef)){
												URIMap.put(qualityTyperef.trim(), null);;
											}
										}							
										//check for related_entity tag
										List<Element> relatedETag = getChildrenByTagName(qualityElement, "related_entity");
										if (relatedETag!=null){
											for (Element relETag : relatedETag){//only one
												List<Element> relETyperefElement = getChildrenByTagName(relETag, "typeref");
												for (Element relETyperefEl : relETyperefElement){//only one
													relatedETyperef = relETyperefEl.getAttribute("about").trim();
													if (!URIMap.containsKey(relatedETyperef)){
														URIMap.put(relatedETyperef.trim(), null);
													}
												}
											}
										}
									}
								}
							}
						}
						
						/*
						int startQual = qualifierRelLabel.indexOf(":");
						qualifierRelLabel = qualifierRelLabel.substring(startQual);
						qualifierRelLabel = qualifierRelLabel.replaceAll("_", " ");
						*/
						
						Map<String, String> values = new HashMap<String, String>();
						values.put("bearer", bearerTyperef);
						values.put("holds", holdsTyperef);
						values.put("quality", qualityTyperef);
						values.put("related", relatedETyperef);
						values.put("description", descriptionLabel);
						values.put("qualifier", qualifierRelLabel);
						
						stateHM.put(stateId, values);

						bearerTyperef = null;
						holdsTyperef = null;
						qualityTyperef = null;
						relatedETyperef = null;
						descriptionLabel = null;
					}
				}	
			}
		}
		fillOntIds();
	}
	/*.................................................................................................................*/
	/** Connects to Sourceforge and pulls specific obo files.
	 *@see fillOntIds(String path) 
	 **/
	public void fillOntIds(){
		String taoPath = "http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/anatomy/gross_anatomy/animal_gross_anatomy/fish/teleost_anatomy.obo";
		String ttoPath = "http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/taxonomy/teleost_taxonomy.obo";
		String bspoPath = "http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/anatomy/caro/spatial.obo";
		String patoPath = "http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/quality.obo";
		
		//logln("Loading TAO ontology...");
		fillOntIds(taoPath);
		
		//logln("Loading TTO ontology...");
		fillOntIds(ttoPath);
		
		//logln("Loading BSPO ontology...");
		fillOntIds(bspoPath);
		
		//logln("Loading PATO ontology...");
		fillOntIds(patoPath);
		
		if (internetFail == true){
			MesquiteModule.mesquiteTrunk.discreetAlert( MesquiteThread.isScripting()," Mesquite could not connect to the internt and will default to displaying numeric URIs instead of text annotations.");
		}	
	}
	/*.................................................................................................................*/
	/** Takes path to obo files on Sourceforge and parses for ids and labels of URIs.
	 *@param Path to an obo file on Sourceforge 
	 *@see fillOntIds()
	 **/
	public void fillOntIds(String path){
		Pattern idPattern = Pattern.compile("^id:");
		Pattern namePattern = Pattern.compile("^name:");
		String idMatch = null;
		String nameMatch = null;
		Matcher matcher; //String to search
		
		DataInputStream stream;
		StringBuffer sBb= new StringBuffer(100);
		StringBuffer s= new StringBuffer(100);
		MesquiteInteger remnant = new MesquiteInteger(-1);
		URL url = null; 
		try {
			url = new URL(path);
		}
		catch (MalformedURLException e) {MesquiteModule.mesquiteTrunk.discreetAlert( MesquiteThread.isScripting(),"Bad URL for ontology files. Mesquite will default to displaying URIs as annotations.");}

		try {
			URLConnection urlConnection = url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			stream = new DataInputStream(inputStream);
			String newS = " ";
			
			while (newS != null) {
				newS = MesquiteFile.readLine(stream, sBb, remnant);
				if (newS!=null) {
					matcher = idPattern.matcher(newS);
					if (matcher.find() == true){
						idMatch = newS;
						int startStr = "id:".length();
						idMatch = idMatch.substring(startStr, idMatch.length()).trim();
					}
					matcher = namePattern.matcher(newS);
					if (matcher.find() == true){
						 nameMatch = newS;
						int startStr = "name:".length();
						nameMatch = nameMatch.substring(startStr, nameMatch.length()).trim();
					}
					
					if ((idMatch!=null && idMatch.length()!=0 && idMatch.trim().length()!=0) && (nameMatch!=null && nameMatch.length()!=0 && nameMatch.trim().length()!=0)){
						if (startsWithURI(idMatch) == true){
							if (URIMap.containsKey(idMatch)){
								URIMap.put(idMatch, nameMatch);
							}
						}
						else {
							idMatch = null;
							nameMatch = null;
						}
					}
				}
			}
		}
		catch( IOException e ) {
			MesquiteMessage.warnProgrammer("IO Exception found (6a) : " + path + "   " + e.getMessage());
			internetFail = true; 
		}	
	}
	/*.................................................................................................................*/
	/** Helper method for parsing obo files from Sourceforge, 
	 * looks for lines beginning with the types of URIs we are looking for.
	 *@param Line of input parsed from obo file pulled from Sourceforge 
	 *fillOntIds(String path)
	 **/
	public boolean startsWithURI(String input){
		Pattern TAOPattern = Pattern.compile("^TAO");
		Pattern PATOPattern = Pattern.compile("^PATO");
		Pattern BSPOPattern = Pattern.compile("^BSPO");
		boolean taoBool = false;
		boolean patoBool = false;
		boolean bspoBool = false;
		Matcher matcher;
		
		matcher = TAOPattern.matcher(input);
		if (matcher.find() == true){
			taoBool = true;
		}
		matcher = PATOPattern.matcher(input);
		if (matcher.find() == true){
			patoBool = true;
		}
		matcher = BSPOPattern.matcher(input);
		if (matcher.find() == true){
			bspoBool = true;
		}
		
		if (taoBool == true || patoBool == true || bspoBool == true){
			return true;
		}
		
		return false;
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
	
	
}
