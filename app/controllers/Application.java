package controllers;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Distance;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
public class Application extends Controller {

	public static Result index() {
		String urlRoot = "http://www.leboncoin.fr/ventes_immobilieres/offres/midi_pyrenees";
		String dpt = "gers";
		String type = "ret=3";

		String url = urlRoot + "/" + dpt + "/?" + type;

		try {
			Document doc = Jsoup.parse(new URL(url).openStream(), "iso-8859-15", url);
			Elements list = doc.getElementsByClass("list-lbc");
			Element list_lbc = list.get(0);
			Elements allLinks = list_lbc.getElementsByTag("a");
			
			for(Element link : allLinks) {
				Element date = link.child(0).child(0).child(0);
				System.out.println(date.text().trim());
				if(date.text().trim().equalsIgnoreCase("Hier")) {
					String location = link.child(0).child(2).child(2).text().trim().replaceAll("[ \t\r\n]", "").replaceAll("/", ", ");
					System.out.println(location);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return ok(index.render());
	}

}
