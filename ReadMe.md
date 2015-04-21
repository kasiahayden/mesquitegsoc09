#readme.txt

# Introduction #

This is the same readme.txt that is found in the Install zip file located in the Downloads tab.

# ReadMe #

Setup Instructions for MesquiteNexmlViewer
Downloads and source code available at: http://code.google.com/p/mesquitegsoc09/



---(1)---

If you have not already done so, install the most recent version of Mesquite. This may require installing Java if your computer does not already have Java loaded. To install Mesquite, follow the instructions here: http://mesquiteproject.org/mesquite/download/download.html





---(2)---
Unzip the zip files nexml.zip and mesquitenexmlviewer.zip and org.zip. Once they are unzipped they should be named "nexml", "mesquitenexmlviewer" and "org", respectively.





---(3a)---
Put the unzipped file named "org" inside of your "Mesquite\_Folder" folder. (If "org" already exists, then merge the two files. That is, take the contents from the org file you downloaded and put them into the already existing org file.)



> On OSX, your file hierarchy might look like this:

> Applications < mesquite\_folder < org



> On Windows, your file hierarchy might look like this:

> C:\Program Files\Mesquite\_Folder\org



---(3b)---
Inside the "Mesquite\_Folder" folder, you should see a folder named "mesquite". In this "mesquite" folder, put the files named "nexml" and "mesquitenexmlviewer."



> On OSX, your file hierarchy might look like this:

> Applications < mesquite\_folder < mesquite < nexml

> Applications < mesquite\_folder < mesquite < mesquitenexmlviewer



> On Windows, your file hierarchy might look like this:

> C:\Program Files\Mesquite\_Folder\mesquite\nexml

> C:\Program Files\Mesquite\_Folder\mesquite\mesquitenexmlviewer




As you can see, "nexml" and "mesquitenexmlviewer" should be in the same folder, that is the "mesquite" folder.


---(3c)---
Inside the "Mesquite\_Folder" folder, you should see a folder named "jars". In this "jars" folder, put the file named "grappa1\_2".





---(4c)---
Run Mesquite and check to see that the modules loaded. On the startup screen you should be able to scroll through the text and see output that will tell you the modules were successfully loaded.



It will look something like:



> Additional modules loaded from [path to Mesquite\_Folder](Your.md)

> nexml



> Additional modules loaded from [path to Mesquite\_Folder](Your.md)

> mesquitenexmlviewer



If you are not able to find this in the log you can still try the next step.





---(5)---
Open a NeXML file in Mesquite. A pop-up box will appear and Mesquite will give you the option to choose your interpreter. Scroll down until you see NeXML and select it. Immediately after this Mesquite will prompt you to save the imported file in its new format. Mesquite requires you to save before you can view your imported file, so save the file in the location of your choice.

After you save, the matrix file will either automatically open or you will have to click "Show Matrix".



--- Phenex tool ---

You can view any cell's annotations by clicking on the Phenex button in the toolbar and selecting a cell. (Run your cursor over the tools to see a tool description in the annotations panel. The Phenex button's description is something like "Displays Phenex-generated NeXML annotations".) Annotations will show in the footnotes box directly beneath the matrix.


--- Graphviz tool ---

You can view a Graphviz-generated dot graph of a cell's annotations by clicking on the Graphviz button in the toolbar and selecting a cell. (The Graphviz button's description is "Displays dot graph of NeXML annotations.") The dot graph will appear in a separate pop-up window.

See screenshots that highlight the Phenex button and the Graphviz button in the screenshots folder in this same (Installation) directory.