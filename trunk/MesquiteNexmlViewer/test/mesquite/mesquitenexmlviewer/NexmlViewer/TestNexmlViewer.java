package mesquite.mesquitenexmlviewer.NexmlViewer;


import junit.framework.Assert;
import org.junit.Test;

public class TestNexmlViewer{
	
	@Test //include this after every method I want tested (rather than methods called)
	public void testNexmlViewer(){

		URIMap UriMap = null;
		
		String columnName = "column ";
		String rowName = "row ";
	
		Collection<String> tempArray = new ArrayList<String>();
		tempArray.add(columnName.trim());
		tempArray.add(rowName.trim());
		
		Collection<String> key = new ArrayList<String>();
		key.add("column");
		key.add("row");
		
		Map<String, String> value = new HashMap<String, String>();
		value.put("bearer", "1bearer");
		value.put("holds", "2holds");
		value.put("quality", "3quality");
		value.put("related", "4related");
		value.put("description", "descriptionLabel"); //Doesn't need to be fished from URIMap.URIMap- is in masterMap
		
		UriMap.masterMap.put(key, value);
		
		UriMap.URIMap.put("1bearer", "bearerLabel");
		UriMap.URIMap.put("2holds", "holdsLabel");
		UriMap.URIMap.put("3quality", "qualityLabel");
		UriMap.URIMap.put("4related", "relatedLabel");
		
		Assert.assertEquals("UriMap.masterMap.containsKey(tempArray) should be true", UriMap.masterMap.containsKey(tempArray),
				true;
		
		if (UriMap.masterMap.containsKey(tempArray)) {
			Map<String, String> tempValHM = UriMap.masterMap.get(tempArray);
			String bearerTemp = tempValHM.get("bearer");
			String holdsTemp = tempValHM.get("holds");
			String qualityTemp = tempValHM.get("quality");
			String relatedTemp = tempValHM.get("related");
			String descriptionTemp = tempValHM.get("description"); //Doesn't need to be fished from URIMap.URIMap- is in masterMap
			
			Assert.assertEquals("URIMap.URIMap.containsKey(bearerTemp) should be true", URIMap.URIMap.containsKey(bearerTemp),
					true;
			Assert.assertEquals("URIMap.URIMap.containsKey(holdsTemp) should be true", URIMap.URIMap.containsKey(holdsTemp),
					true;
			Assert.assertEquals("URIMap.URIMap.containsKey(qualityTemp) should be true", URIMap.URIMap.containsKey(qualityTemp),
					true;
			Assert.assertEquals("URIMap.URIMap.containsKey(relatedTemp) should be true", URIMap.URIMap.containsKey(relatedTemp),
					true;
			
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

			Assert.assertEquals("bearerTemp should equal \"bearerLabel\"", bearerTemp, "bearerLabel";
			Assert.assertEquals("holdsTemp should equal \"holdsLabel\"", holdsTemp, "holdsLabel";
			Assert.assertEquals("qualityTemp should equal \"qualityLabel\"", qualityTemp, "qualityLabel";
			Assert.assertEquals("relatedTemp should equal \"relatedLabel\"", relatedTemp, "relatedLabel";
			Assert.assertEquals("descriptionTemp should equal \"descriptionLabel\"", descriptionTemp, "descriptionLabel";
			
			
		}
	}
}


/*
Examples from Rutger's code:

TestMatrix:
OTU chimp = mammals.createOTU();
		chimp.setLabel("chimp");
		Assert.assertEquals("chimp.getLabel() should be \"chimp\"", "chimp",
				chimp.getLabel());

TestAnnotation:
	    /**
	     * Set/Get a URI
	     */
/*
	    try {
			otu.addAnnotationValue("cdao:hasURI", ns, new URI("http://www.nexml.org"));
			Set<Object> annos = otu.getRelValues("cdao:hasURI");
			Object relValue = annos.iterator().next();
			Assert.assertEquals(((URI)relValue).toString(),"http://www.nexml.org");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	    
TestFileParse:
try {
			doc = DocumentFactory.parse(file);
		} catch (SAXException e) {
			Assert.assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
		
TestSerializable:
public void MakeNeXML () {
		Document nexmlDocument = null;
		try {
			nexmlDocument = DocumentFactory.createDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertNotNull("document != null", nexmlDocument);
		Assert.assertEquals("label is null", null, nexmlDocument.getLabel());
		Assert.assertEquals("id is null", null, nexmlDocument.getId());
		Assert.assertNotNull("xml output != null", nexmlDocument.getXmlString());
	}
	


*/