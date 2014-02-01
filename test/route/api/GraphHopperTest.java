package route.api;

import models.Distance;
import static org.fest.assertions.Assertions.*;
import org.junit.Test;

public class GraphHopperTest {
	@Test
	public void readJSONTes() {
		Distance distance = new Distance();
		distance.setAllowed(true);
		distance.setDestination("L'Isle en Dodon");
		distance.setOrigin("impasse alice guy toulouse");
		distance.setDistance(new Double(0));
		distance.setDuration(new Double(0));
		
		GraphHopperAPI.setDistance(distance);
		
		assertThat(distance.getDistance()).isGreaterThan(0);
		assertThat(distance.getDuration()).isGreaterThan(0);
	}
}
