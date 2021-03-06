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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tools.DistanceTools;
import models.Distance;
import models.SmallAds;

public final class LbcReader {
	private static String SMALL_ADS_SITE = "leboncoin";
	private static String ROOT_URL = "http://www.leboncoin.fr/ventes_immobilieres/offres/midi_pyrenees";
	private int dpt;
	
	private static final String PAGE = "o=";
	private static final String IMMO = "ret=3";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.FRANCE);
	
	public LbcReader(int dpt) {
		this.dpt = dpt;
	}
	
	public List<SmallAds> getAds(int minSurface, int maxPrice) {
		List<SmallAds> res = new LinkedList<SmallAds>();

		int page = 1;
		try {
			while(hasYesterDayAds(res, page, minSurface, maxPrice)) {
				page++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	private boolean hasYesterDayAds(List<SmallAds> res, int page, int minSurface, int maxPrice) throws MalformedURLException, IOException {
		boolean isMoreYesterday = true;
		String url = ROOT_URL+"/"+getDpt(this.dpt)+"/?"+IMMO+"&"+PAGE+page;
		
		Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-15", url);
		Elements list = doc.getElementsByClass("list-lbc");
		Element list_lbc = list.get(0);
		Elements allLinks = list_lbc.getElementsByTag("a");
		
		Pattern pattern = Pattern.compile("([\\d ]+)");
		
		for(Element link : allLinks) {
			String href = link.attr("href");
			Element date = link.child(0).child(0).child(0);
			String dateText = date.text();
			// On cherche uniquement les annonces publiées la veille (Hier).
			// Donc on teste si la date ne vaut plus 'hier'. Mais si il y a eu
			// des annonces aujourd'hui, il faut ne part en tenir compte.
			// Normalement, il faudrait echappé les Aujourd'hui.
			
			// Les annonces du jour ne nous interessent pas. On passe donc à
			// l'élément suivant jusqu'à tomber sur une annonce de la veille.
			if(dateText.equals("Aujourd'hui")) {
				continue;
			}
			
			if(!dateText.equalsIgnoreCase("Hier")) {
				isMoreYesterday = false;
				break;
			}
			
			Date adsDate = parseDate(dateText);
			String location = link.child(0).child(2).child(2).text().trim().replaceAll("[ \t\r\n]", "").replaceAll("/", ", ");
			location = location.replaceAll("[\\d]", "").trim();
			// Supprime le nom du département de la destination pour éviter les doublons en base.
			int indexOf = location.indexOf(",");
			if(indexOf > 0) {
				location = location.substring(0, indexOf);
			}
			
			Element titleElt = link.getElementsByClass("title").get(0);
			Matcher matcher = pattern.matcher(titleElt.text());
			double surface = -1;
			if(matcher.find()) {
				String trim = matcher.group(1).replaceAll(" ", "").trim();
				if(!trim.equals("")) {
					surface = Double.parseDouble(trim);
				}
			}
			
			if(surface != -1 && surface < minSurface) {
				continue;
			}
			
			Elements priceElt = link.getElementsByClass("price");
			matcher = pattern.matcher(priceElt.text());
			double price = 0;
			if(matcher.find()) {
				String trim = matcher.group(1).replaceAll(" ", "").trim();
				if(!trim.equals("")) {
					price = Double.parseDouble(trim);
				}
			}
			
			if(price > maxPrice) {
				continue;
			}
			
			
//			Distance d = new Distance();
//			d.setOrigin("Impasse Alice Guy, Toulouse");
//			d.setDestination(location);
//			d.setDistance(new Double(0));
//			d.setDuration(new Double(0));
//			d.setAllowed(true);
			
			Distance d = DistanceTools.getDistance("Impasse Alice Guy Toulouse", location);
			
			SmallAds ads = new SmallAds();
			ads.setSmallAdsSite(SMALL_ADS_SITE);
			ads.setUrl(href);
			ads.setDate(adsDate);
			ads.setDistance(d);
			ads.setSurface(surface);
			ads.setPrice(price);
			
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
	
	private String getDpt(int dpt) {
		switch (this.dpt) {
		case 31:
			return "haute_garonne";
		case 32:
			return "gers";
		default:
			return "";
		}
		
		
	}
}
