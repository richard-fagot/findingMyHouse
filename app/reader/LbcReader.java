package reader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.Distance;
import models.SmallAds;

public final class LbcReader {
	private static String ROOT_URL = "http://www.leboncoin.fr/ventes_immobilieres/offres/midi_pyrenees";
	private String dpt;
	
	private static final String PAGE = "o=";
	private static final String IMMO = "ret=3";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.FRANCE);
	
	public LbcReader(String dpt) {
		this.dpt = dpt;
	}
	
	public List<SmallAds> getAds() {
		List<SmallAds> res = new LinkedList<SmallAds>();

		int page = 1;
		try {
			while(hasYesterDayAds(res, page)) {
				page++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	private boolean hasYesterDayAds(List<SmallAds> res, int page) throws MalformedURLException, IOException {
		boolean isMoreYesterday = true;
		String url = ROOT_URL+"/"+dpt+"/?"+IMMO+"&"+PAGE+page;
		
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-15", url);
		Elements list = doc.getElementsByClass("list-lbc");
		Element list_lbc = list.get(0);
		Elements allLinks = list_lbc.getElementsByTag("a");
		
		for(Element link : allLinks) {
			String href = link.attr("href");
			Element date = link.child(0).child(0).child(0);
			String dateText = date.text();
			if(!dateText.equalsIgnoreCase("Hiers") && !dateText.equalsIgnoreCase("Aujourd'hui")) {
				isMoreYesterday = false;
				break;
			}
			Date adsDate = parseDate(dateText);
			String location = link.child(0).child(2).child(2).text().trim().replaceAll("[ \t\r\n]", "").replaceAll("/", ", ");
			
			Distance d = new Distance();
			d.setOrigin("Impasse Alice Guy, Toulouse");
			d.setDestination(location);

			SmallAds ads = new SmallAds();
			ads.setUrl(href);
			ads.setDate(adsDate);
			ads.setDistance(d);
			
			res.add(ads);
		}
		
		return isMoreYesterday;
	}
	
	private Date parseDate(String text) {
		if(text.equalsIgnoreCase("hier")) {
			Calendar yesterday = Calendar.getInstance();
			yesterday.add(Calendar.DAY_OF_MONTH, -1);
			return yesterday.getTime();
		}
		
		if(text.equalsIgnoreCase("Aujourd'hui")) {
			Calendar rightNow = Calendar.getInstance();
			return rightNow.getTime();
		}
		
		try {
			return sdf.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
