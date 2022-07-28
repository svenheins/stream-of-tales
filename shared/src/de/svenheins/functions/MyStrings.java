package de.svenheins.functions;


import java.awt.Polygon;

import de.svenheins.objects.Space;

public class MyStrings {

	public static String Space2String(Space space) {
		Polygon p;
		String returnString ="";
		for (int i = space.getPolygon().size()-1; i >= 0 ; i--) {
			p = space.getPolygon().get(i);
			returnString +="\t<path"+"\n\t\t"+"d="+"\""+"M ";
			for (int j = 0; j < p.xpoints.length; j++ ) {
				returnString += ""+ (p.xpoints[j]-space.getPolyX())+".0,"+ (p.ypoints[j]-space.getPolyY()) +".0 ";
			}
			
			returnString += "z\""+"\n"+"\t id=\"path2985\" \n \t style=\"fill:none;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\"/> \n />"+"\n\n";
	
		}
		
		return returnString;
	}
	
	public static String Space2SVG(Space space) {
		String pre = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> \n <!-- Created with Inkscape (http://www.inkscape.org/) --> \n \n <svg \n \t xmlns:dc=\"http://purl.org/dc/elements/1.1/\" \n xmlns:cc=\"http://creativecommons.org/ns#\" \n xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n xmlns:svg=\"http://www.w3.org/2000/svg\" \n xmlns=\"http://www.w3.org/2000/svg\" \n   version=\"1.1\" \n \t   width=\"210mm\" \n \t   height=\"297mm\" \n \t   id=\"svg2\"> \n \t <defs \n \t     id=\"defs4\" /> \n \t <metadata \n \t    id=\"metadata7\"> \n \t   <rdf:RDF> \n \t     <cc:Work \n \t       rdf:about=\"\">				\n \t    <dc:format>image/svg+xml</dc:format> \n \t   <dc:type \n \t rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" /> \n \t <dc:title></dc:title> \n \t      </cc:Work> \n \t    </rdf:RDF>	\n \t  </metadata>";
		//		String pre = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?> \n <!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\"\n\"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\">";
		String svgString =pre+ "\n\t<g \n \t \t id=\"layer1\">\n"+ Space2String(space)+"\t</g>\n\n</svg>";
		return svgString;
	}
}
