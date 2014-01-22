package controllers;

import java.net.URL;
import java.util.Date;
import java.util.List;

import models.Distance;
import models.LastCall;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
public class Application extends Controller {

	public static Result index() {
		String urlRoot = "http://www.leboncoin.fr/ventes_immobilieres/offres/midi_pyrenees";
		String dpt = "gers";
		String type = "ret=3";

		String url = urlRoot + "/" + dpt + "/?" + type;

//		List<LastCall> lastCalls = LastCall.find.all();
//		LastCall lastCall;
//		if(lastCalls.isEmpty()) {
//			lastCall = new LastCall();
//			lastCall.setDate(new Date());
//			lastCall.save();
//		} else {
//			lastCall = lastCalls.get(0);
//		}
//		
//		Date now = new Date();
//		
//		if(now.equals(lastCall.getDate()))
		
		try {
			Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-15", url);
			Elements list = doc.getElementsByClass("list-lbc");
			Element list_lbc = list.get(0);
			Elements allLinks = list_lbc.getElementsByTag("a");
			
			for(Element link : allLinks) {
				Element date = link.child(0).child(0).child(0);
				if(date.text().trim().equalsIgnoreCase("Hier")) {
					String location = link.child(0).child(2).child(2).text().trim().replaceAll("[ \t\r\n]", "").replaceAll("/", ", ");
					System.out.println(location);
					List<Distance> distances = Distance.find.where().ieq("destination", location).findList();
					if(distances.isEmpty()) {
						
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return ok(index.render());
	}

}
