
package mesquite.mesquitenexmlviewer.OboManager;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.io.*;

import org.nexml.model.Annotation;
import org.nexml.model.Document;
import org.nexml.model.OTU;
import org.nexml.model.OTUs;

import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;
import mesquite.nexml.InterpretNEXML.*;


public class OboManager extends FileInit {
	String fileContentAsString = null;
	/*
	int numBlocks =0;
	MesquiteMenuItemSpec addAuthMMI;
	String noAuthorWarningNew = "The Author for this account and machine has not yet been set, but you are requesting an AUTHORS block." +
	" You should now go to the Set Author... menu item in the Defaults submenu of the File menu to set an author name." + 
	" For the code, please indicate a short code unique in your collaborative group.  If you do not set an author name, the AUTHORS block might not be written";
	String noAuthorWarning = "The Author for this account and machine has not yet been set, but this file contains an AUTHORS block." +
	" If you are going to edit and save this file, you are strongly urged to go to the Set Author... menu item in the Defaults submenu of the File menu to set an author name." + 
	" For the code, please indicate a short code unique in your collaborative group.";
	*/
	public Class getDutyClass(){
		return OboManager.class;
	}
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		/*
		addAuthMMI = getFileCoordinator().addMenuItem(MesquiteTrunk.editMenu, "Add AUTHORS Block to File", new MesquiteCommand("addAuthorsBlock", this));
		addAuthMMI.setEnabled(false);
		MesquiteTrunk.resetMenuItemEnabling();
//		getFileCoordinator().addMenuItem(MesquiteTrunk.editMenu, "Add Last Author to Matrix Names", new MesquiteCommand("addAuthorNameToMatrices", this));
		*/
		return true;
	}
	/*.................................................................................................................*/
	public boolean isSubstantive(){
		return true;
	}
	public boolean isPrerelease(){
		return false;
	}
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		/*
		if (checker.compare(this.getClass(), "Adds name of author of scripting file to data matrix names", null, commandName, "addAuthorNameToMatrices")) {
			MesquiteFile f=  checker.getFile();
			if (f!= null &&f.getPreviousSaver() != null && f != getProject().getHomeFile()){
				int numMatrices = getProject().getNumberCharMatrices(f);
				for (int i= 0; i< numMatrices; i++) {
					CharacterData data = getProject().getCharacterMatrix(f, i);
					if (data.getLastModifiedAuthor() != null) {
						data.setName(data.getName() + " (from " + data.getLastModifiedAuthor() + ")");
						MesquiteWindow.resetAllTitles();
					}	
				}
			}
		}
		else 	if (checker.compare(this.getClass(), "Adds an authors block to the file", null, commandName, "addAuthorsBlock")) {
			MesquiteFile f=  checker.getFile();
			if (f == null)
				f = getProject().getHomeFile();
			AuthorsBlock ab = new AuthorsBlock(f, this);
			numBlocks++;
			addNEXUSBlock(ab);
			addAuthMMI.setEnabled(false);
			MesquiteTrunk.resetMenuItemEnabling();
			if (MesquiteModule.author.hasDefaultSettings() && !MesquiteThread.isScripting())
				discreetAlert( noAuthorWarningNew);
			if (MesquiteModule.author != null && !MesquiteModule.author.hasDefaultSettings()){

				if (f.getProject().numAuthors() == 0) {
					ListableVector authors = f.getProject().getAuthors();
					Author a = new Author();
					a.setName(MesquiteModule.author.getName());
					a.setCode(MesquiteModule.author.getCode());
					a.setCurrent(true);
					authors.addElement(a, false);
				}
			}
		}
		else
			return  super.doCommand(commandName, arguments, checker);
			*/
		return null;
	}
	/*.................................................................................................................*/
	/** A method called immediately after the file has been read in or completely set up (if a new file).*/
	public void fileReadIn(MesquiteFile f) {
		logln("OboManager's fileReadIn method ran");
		
		if (f== null || f.getProject() == null)
			return;
			
		String fileContent = f.getFileContentsAsString(100); //Reads imported NeXML files after they've been saved as .nex files :(
		logln("*****File content is: ******" + fileContent);
		
		//f.getFileContentsAsString(relativePath)
		
		ObjectConverter objectConverter = new ObjectConverter(this);
		Document xmlDocument = objectConverter.createDocumentFromProject(getProject());
		List<OTUs> xmlOTUsList = xmlDocument.getOTUsList();
		for (OTUs xmlOTUs : xmlOTUsList) {
			for ( OTU xmlOTU : xmlOTUs.getAllOTUs() ) {
				Set<Annotation> allAnnotations = xmlOTU.getAllAnnotations();
				for ( Annotation annotation : allAnnotations ) { //Then pulled apart this way, 
					String ann = annotation.toString(); //and read as strings with .toString
					logln("OboManager ann: "+ ann);
				}
			}
		}
		
		
		//getProject().
		//ObjectConverter.createDocumentFromProject(getProject());
		
		
		//logln("getAnnotation from OboManager: " + getProject().//.getCharacterMatrices().getAnnotation());
		
		
		//logln("Location: " + thisFile.getDirectoryName() + thisFile.getFileName());
		
		
		
		
		
		//1) Look for OBO ontology references in the URI's, 
		//2) read what it can and stash them in a special subclass of FileElement. 

		//Then in the annotation viewer, when you find a reference such as TAO:0001348, 
		//you can look up the term using one of the findNearest methods to find the FileElement in the 
		//project and use the method you write to lookup the natural language name (stratum marginale).
		
		
		/*
		 * based on the URI, we know there is a file out there (an obo file in
		 * this case) that has natural language names that correspond to these
		 * URI's (e.g., TAO:0000239 -> mesocoracoid bone).
		 * So what we want to do is read this file and build a hashmap to use
		 * when and if these annotations are displayed
		 */
		 
		/*
		 * /** Returns the contents of the file, local or remote.  The parameter "maxCharacters"
	sets an upper limit on how many characters are read (if <0, then all characters read in)
	public String getFileContentsAsString(int maxCharacters) {
		StringBuffer sb = new StringBuffer(100);
		StringBuffer line = new StringBuffer(100);
		openReading();
		while (readLine(line) && (maxCharacters<0 ||  sb.length() <maxCharacters)){
			sb.append(line.toString());
			sb.append(StringUtil.lineSeparator);			
		}
		closeReading();
		return sb.toString();
	}
		 */
		
		
		
		/*	
			
		NexusBlock[] bs = getProject().getNexusBlocks(AuthorsBlock.class, f); 
		addAuthMMI.setEnabled((bs == null || bs.length ==0) && !Author.addAuthorBlockByDefault);
		if ((bs == null || bs.length ==0) && Author.addAuthorBlockByDefault){
			AuthorsBlock ab = new AuthorsBlock(f, this);
			numBlocks++;
			addNEXUSBlock(ab);
		}
		MesquiteTrunk.resetMenuItemEnabling();
		if (MesquiteModule.author != null && !MesquiteModule.author.hasDefaultSettings()){

			if (f.getProject().numAuthors() == 0) {
				ListableVector authors = f.getProject().getAuthors();
				Author a = new Author();
				a.setName(MesquiteModule.author.getName());
				a.setCode(MesquiteModule.author.getCode());
				a.setCurrent(true);
				authors.addElement(a, false);
			}
		}
		*/
	}
	/*.................................................................................................................*/
	/*Author findAuthor(Author author){
		
		if (author == null)
			return null;
		if (getProject().numAuthors()>0){
			ListableVector v = getProject().getAuthors();
			for (int i = 0; i< v.size(); i++){
				Author a = (Author)v.elementAt(i);
				if (a.getCode()!= null && a.getCode().equals(author.getCode()) && a.getName()!= null && a.getName().equals(author.getName()))
					return a;
			}
		}
		
		return null;
	}
	/*.................................................................................................................*/
	//public NexusBlockTest getNexusBlockTest(){ return new AuthorsBlockTest();}
	/*.................................................................................................................*/
	/*public NexusBlock readNexusBlock(MesquiteFile file, String name, FileBlock block, StringBuffer blockComments, String fileReadingArguments){

		String commandString;
		NexusBlock b=new AuthorsBlock(file, this);
		ListableVector v = getProject().getAuthors();
		MesquiteString comment = new MesquiteString();
		boolean found = false;
		for (int ia = 0; ia< v.size(); ia++){
			Author au = (Author)v.elementAt(ia);
			if (au == MesquiteModule.author) {
				found = true;
			}
		}
		if (!found)
			v.addElement(MesquiteModule.author, false);
		if (MesquiteModule.author.hasDefaultSettings() && !MesquiteThread.isScripting())
			discreetAlert( noAuthorWarning);
		while (!StringUtil.blank(commandString = block.getNextFileCommand(comment))) {
			String commandName = parser.getFirstToken(commandString);

			if (commandName.equalsIgnoreCase("AUTHOR")) {
				String token = null;

				Author a = new Author();
				while (!StringUtil.blank(token = parser.getNextToken())){
					if ("NAME".equalsIgnoreCase(token)){
						parser.getNextToken(); //=
						a.setName(parser.getNextToken());

					}
					else if ("CODE".equalsIgnoreCase(token)){
						parser.getNextToken(); //=
						a.setCode(parser.getNextToken());
					}
					else if ("LASTSAVER".equalsIgnoreCase(token)){
						if (!a.hasDefaultSettings())
							file.setPreviousSaver(a);
					}
				}
				if (findAuthor(a) == null) //not found; add
					v.addElement(a, false);

			}
		}
		return b;
	}

	/*.................................................................................................................*/
	public String getName() {
		return "Obo Manager";
	}

	/*.................................................................................................................*/
	/** returns an explanation of what the module does.*/
	public String getExplanation() {
		return "Manages .obo ontology files when importing a NeXML file." ;
	}
}