package de.svenheins.functions;


import java.awt.Polygon;

import de.svenheins.objects.Space;

public class MyStrings {

	public static String Space2String(Space space) {
		Polygon p;
		String returnString ="";
		for (int i = 0; i < space.getPolygon().size(); i++) {
			p = space.getPolygon().get(i);
			returnString +="\t<path"+"\n\t\t"+"d="+"\""+"M ";
			for (int j = 0; j < p.xpoints.length; j++ ) {
				returnString += " "+ p.xpoints[j]+","+p.ypoints[j]+" ";
			}
			
			returnString += "z\""+"\n"+"\t/>"+"\n\n";
	
		}
		
		return returnString;
	}
	
	public static String Space2SVG(Space space) {
		String pre = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?> \n <!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\"\n\"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\">";
		String svgString =pre+ "\n\n"+ "<svg width=\"100%\" height=\"100%\"> \n\t<g>\n"+ Space2String(space)+"\t</g>\n\n</svg>";
		return svgString;
	}
}
