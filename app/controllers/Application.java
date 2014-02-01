package controllers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Distance;
import models.SmallAds;
import play.Logger;
import play.Logger.ALogger;
import play.Routes;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import reader.LbcReader;
import reader.SeLogerReader;
import views.html.*;

import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller {
	private static boolean hasRetrieved = false;
	private static ALogger logger = Logger.of(Application.class);
	
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
			if(distance.getDuration() == 0) {
				String destination = distance.getDestination();
				unknownDest.add(destination);
			}
		}
		
		return ok(Json.toJson(unknownDest));
	}
	
	public static Result getAds() {
		int[] dpts = {31,32};
		int maxPrice = 65000;
		int minSurface = 2000;
		int maxDuration = 40*60;
		
		List<SmallAds> allAds = new LinkedList<>();
		
		for(int dpt : dpts) {
			LbcReader lbcr = new LbcReader(dpt);
			allAds.addAll(lbcr.getAds(minSurface, maxPrice));
			
			SeLogerReader slr = new SeLogerReader(dpt);
			allAds.addAll(slr.getAds(minSurface, maxPrice));
		}
		
		List<SmallAds> finalAds = new LinkedList<>();
		for(SmallAds ads : allAds) {
			Distance distance = ads.getDistance();
			if(distance == null) {
				finalAds.add(ads);
			} else {
				Double duration = distance.getDuration();
				if(duration == null || duration <= maxDuration) {
					finalAds.add(ads);
				}
			}
		}
		
		return ok(ads.render(finalAds));
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
				if(!destData.get("status").asText().equals("NOT_FOUND")) {
					String asText = dests.get(i).asText();
					
					String jsonCity = asText.substring(0, asText.indexOf(","));//+", Gers";
					Matcher matcher = compile.matcher(jsonCity);
					if(matcher.find()) {
						String city = matcher.group(1).trim();
						// Sometimes, the city returned by google after the
						// dataMatrix is different from the city recorded in the
						// database from the reader.
						Distance distance = Distance.find.where().ilike("destination", city+"%").findUnique();
						if(distance != null) {
							distance.setDuration(destData.get("duration").get("value").asDouble());
							distance.setDistance(destData.get("distance").get("value").asDouble());
							distance.save();
						} 
					}
				}
			}
		}
		return getSuccessfullAds();
	}
	
	public static Result getSuccessfullAds() {
		double maxDuration = 40*60; // more then 40 minutes is rejected
		double minDuration = 20*60;
		double minSurface = 2000; // less than 2000m² is rejected
		double maxPrice = 65000;
		
		List<SmallAds> res = new LinkedList<SmallAds>();
		List<SmallAds> all = SmallAds.find.all();
		for(SmallAds ads : all) {
			Distance distance = ads.getDistance();

			Double duration = distance.getDuration();
			if((duration <= maxDuration) && (duration >= minDuration)) {
				distance.setAllowed(true);
			} else {
				distance.setAllowed(false);
			}
			distance.save();
			
			double surface = ads.getSurface();
			if(distance.isAllowed() && ((surface < 0 ) || ((surface >= minSurface) && (ads.getPrice() <= maxPrice)))) {
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
		
		int[] dpts = {31, 32};
		
		for(int dpt : dpts) {
			List<SmallAds> adsList = new LinkedList<>();
			
			LbcReader reader = new LbcReader(dpt);
			// the reader returns non persisted ads
			adsList = reader.getAds(2000, 65000);
			logger.info("Found in lbc : "+adsList.size());
			
			SeLogerReader slr = new SeLogerReader(dpt);
			List<SmallAds> slrAds = slr.getAds(2000, 65000);
			logger.info("Found in seloger : " + slrAds.size());
			
			adsList.addAll(slrAds);
			
			for(SmallAds ads : adsList) {
				// The distance is not persisted when returned from the reader
				// and isAllowed return true (set by the reader).
				Distance distance = ads.getDistance();
				String location = distance.getDestination();
				// Check if this destination already exists in the database. If
				// yes, we swap both distances.
				Distance existingDistance = Distance.find.where().ieq("destination", location).findUnique();
				if(existingDistance != null) {
					distance = existingDistance;
					ads.setDistance(existingDistance);
				} else {
					distance.save();
				}
				
				// At this step, the distance is the one returned by the reader,
				// returning isAllowed:true as we have not computed the
				// distance. Or true or false if it has been retrieved from the
				// database. The ads is selected for the rest of the process
				// only if the distance is allowed.
				if(distance.isAllowed() || (distance.getDistance() != null && distance.getDistance().equals(0))) {
					ads.save();
				} else {
					continue;
				}
				
				System.out.println(location);
			}
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
