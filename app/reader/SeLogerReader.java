package reader;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import models.Distance;
import models.SmallAds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tools.DistanceTools;

public final class SeLogerReader {
	private static String SMALL_ADS_SITE = "Se loger";
	// idtt=2&cp=32&idtypebien=4&fraicheur=2&surf_terrainmin=2000&pxmax=65000
	private static String ROOT_URL = "http://ws.seloger.com/search.xml?idtt=2";
	private int dpt;
	
	public SeLogerReader(int dpt) {
		this.dpt = dpt;
	}
	
	public List<SmallAds> getAds(int minSurface, int maxPrice) {
		List<SmallAds> res = new LinkedList<SmallAds>();

		String url = ROOT_URL+"&fraicheur=2&cp="+this.dpt+"&idtypebien=4&surf_terrainmin="+minSurface+"&pxmax="+maxPrice;
		
		try {
			Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
			Elements list = doc.getElementsByTag("annonce");
			
			for(Element ads : list) {
				String location = "";
				String href = "#";
				double surface = -1;
				double price = 0;
				
				Elements locationElt = ads.getElementsByTag("ville");
				if(locationElt.size() > 0) {
					location = locationElt.get(0).text().replaceAll("[\\d]", "").trim();
				}
				
				Elements hrefElt = ads.getElementsByTag("permaLien");
				if(hrefElt.size() > 0) {
					href = hrefElt.get(0).text();
				}
				
				Elements surfaceElt = ads.getElementsByTag("surface");
				if(surfaceElt.size() > 0) {
					surface = Double.parseDouble(surfaceElt.get(0).text());
				}
				
				if(surface != -1 && surface < minSurface) {
					continue;
				}
				
				Elements priceElt = ads.getElementsByTag("prix");
				if(priceElt.size() > 0) {
					price = Double.parseDouble(priceElt.get(0).text());
				}
				
				if(price > maxPrice) {
					continue;
				}
				
//				Distance d = new Distance();
//				d.setOrigin("Impasse Alice Guy, Toulouse");
//				d.setDestination(location);
//				d.setDistance(new Double(0));
//				d.setDuration(new Double(0));
//				d.setAllowed(true);
				
				Distance d = DistanceTools.getDistance("Impasse Alice Guy Toulouse", location);
				
				SmallAds smallAds = new SmallAds();
				smallAds.setSmallAdsSite(SMALL_ADS_SITE);
				smallAds.setUrl(href);
				//smallAds.setDate(adsDate);
				smallAds.setDistance(d);
				smallAds.setSurface(surface);
				smallAds.setPrice(price);
				
				res.add(smallAds);
			}
			System.out.println(list.size());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

}
