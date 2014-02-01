package tools;

import route.api.GraphHopperAPI;
import models.Distance;

public final class DistanceTools {
	public static Distance getDistance(String origin, String destination) {
		Distance distance = Distance.find.where().ilike("destination", destination).findUnique();
		if(distance == null) {
			distance = new Distance();
			distance.setAllowed(true);
			distance.setDestination(destination);
			distance.setOrigin(origin);
			
			GraphHopperAPI.setDistance(distance);
			distance.save();
		}
		
		return distance;
	}
}
