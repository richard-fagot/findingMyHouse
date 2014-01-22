package controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Distance;
import models.SmallAds;
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
		JsonNode json = request().body().asJson();
		
		return ok();
	}
	
	private static List<SmallAds> retrieveElligibleAds() {
		if(!hasRetrieved) {
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
		}
		
		return SmallAds.find.all();
	}
}
