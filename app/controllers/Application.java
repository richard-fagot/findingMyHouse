package controllers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Distance;
import models.SmallAds;
import play.Routes;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import reader.LbcReader;
import views.html.index;

import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller {
	private static boolean hasRetrieved = false;
	
	public static Result index() {
		return ok(index.render());
	}

	public static Result getElligibleAds() {
		return ok(Json.toJson(retrieveElligibleAds()));
	}

	public static Result getUnknownDest() {
		List<SmallAds> all = retrieveElligibleAds();
		Set<String> unknownDest = new HashSet<String>();
		
		for(SmallAds ads : all) {
			Distance distance = ads.getDistance();
			if(distance.getDuration() == null) {
				String destination = distance.getDestination();
				unknownDest.add(destination);
			}
		}
		
		return ok(Json.toJson(unknownDest));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result saveDistance() {
//		{
//			"rows":[
//				{"elements":[
//					{"distance":{"text":"78,4 km","value":78407},
//					 "duration":{"text":"1 heure 10 min","value":4200},
//					 "status":"OK"},
//					{"distance":{"text":"135 km","value":135237},
//					 "duration":{"text":"1 heure 56 min","value":6982},
//					 "status":"OK"}
//				]}
//			],
//			"originAddresses":["Impasse Alice Guy, 31300 Toulouse, France"],
//			"destinationAddresses":["Saint-Clar, France","Galiax, France"]
//		}
		Pattern compile = Pattern.compile("(\\D+)");
		JsonNode json = request().body().asJson();
		JsonNode dests = json.get("destinationAddresses");
		for(JsonNode row : json.get("rows")) {
			JsonNode destsData = row.get("elements");
			for(int i = 0 ; i < dests.size() ; i++) {
				JsonNode destData = destsData.get(i);
				String asText = dests.get(i).asText();
				String jsonCity = asText.substring(0, asText.indexOf(","));//+", Gers";
				Matcher matcher = compile.matcher(jsonCity);
				if(matcher.find()) {
					String city = matcher.group(1).trim();
					Distance distance = Distance.find.where().ilike("destination", city+"%").findUnique();
					distance.setDuration(destData.get("duration").get("value").asDouble());
					distance.setDistance(destData.get("distance").get("value").asDouble());
					distance.save();
				}
			}
		}
		return getSuccessfullAds();
	}
	
	public static Result getSuccessfullAds() {
		double admissibleDuration = 40*60; // more then 40 minutes is rejected
		double minSurface = 2000; // less than 2000m² is rejected
		
		List<SmallAds> res = new LinkedList<SmallAds>();
		List<SmallAds> all = SmallAds.find.all();
		for(SmallAds ads : all) {
			Distance distance = ads.getDistance();

			Double duration = distance.getDuration();
			if((duration <= admissibleDuration)) {
				distance.setAllowed(true);
				distance.save();
			}
			
			double surface = ads.getSurface();
			if(distance.isAllowed() && (surface >= minSurface)) {
				ads.setAccepted(true);
				ads.save();
			}
			
			if(ads.isAccepted()) {
				res.add(ads);
			}
		}
		return ok(Json.toJson(res));
	}
	
	private static List<SmallAds> retrieveElligibleAds() {
		List<SmallAds> all = SmallAds.find.all();
		for(SmallAds ads : all) {
			ads.delete();
		}
		String dpt = "gers";
		
		LbcReader reader = new LbcReader(dpt);
		List<SmallAds> adsList = reader.getAds();
		
		for(SmallAds ads : adsList) {
			Distance distance = ads.getDistance();
			String location = distance.getDestination();
			Distance existingDistance = Distance.find.where().ieq("destination", location).findUnique();
			if(existingDistance != null) {
				if(existingDistance.isAllowed()) {
					ads.setDistance(existingDistance);
				} else {
					continue;
				}
			} else {
				distance.save();
			}
			
			ads.save();
			
			System.out.println(location);
		}
		hasRetrieved = true;
		if(!hasRetrieved) {
		}
		
		return SmallAds.find.all();
	}
	
	/**
	 * Allow to access routes from javascript. Usefull to pass javascript
	 * parameters to routes.
	 * 
	 * https://franzgranlund.wordpress.com/2012/03/29/play-framework-2-0-javascriptrouter-in-java/
	 * 
	 * @return
	 */
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
          Routes.javascriptRouter("jsRoutes",
            // Routes
            controllers.routes.javascript.Application.getSuccessfullAds()
          )
        );
      }
}
