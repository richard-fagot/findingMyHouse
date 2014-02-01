package route.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import models.Distance;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public final class GraphHopperAPI {
	private static String ENCODING = "UTF-8";
	
	public static void setDistance(Distance d) {
		String baseURL = "http://localhost:8989/api/route?instructions=false&vehicle=car&";
		
		String origin = d.getOrigin();
		String destination = d.getDestination();
		
		BufferedReader in = null;
		try {
			String fromPoint = URLEncoder.encode(origin, ENCODING);
			String toPoint = URLEncoder.encode(destination, ENCODING);
			
			String url = baseURL + "point=" + fromPoint + "&point=" + toPoint;
			
			URL graphhopper = new URL(url);
			in = new BufferedReader(new InputStreamReader(graphhopper.openStream()));
			
			String inputLine;
			StringBuilder sb = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}

//		Sample of the JSON returned by GraphHopper 
//			{"route":
//				{"to":[1.34809,43.4787]
//				,"time":1024
//				,"distance":18637.293997248322
//				,"from":[1.3617451,43.5996266]
//				,"coordinates":"spbiG{}hGl@`Av@KbAJXLLp@?T@NN^XLf@QLk@Ec@Bg@dCuHn@e@rKtCzL~EnEhAtBZtEZnBBvACrC_@~Ae@bQoHbX_MnEuCbEmDnJuIrQuMfGsD`BiAfMkHt@y@Za@h@w@NORDX?PAf@UTWRc@HWFi@Ay@j@wBf@oAjDsGt@eBr@gBbBcF`GaVxBgIjDmK|AqEdAoCzCiH~@aCRm@vBoHbGsQb@iBZ}BhHof@d@wCrAgFf@}AbA_CPYLALGLODKD]Eg@H]To@xFqL^Uf@KPBb@XzElCnH|EpIjHhLvK~H|HhGrG`SzT~DhErDtDrJdJfItH|IrHbLfK`NpMbArAlNxObDfD~AvA~@r@tClBzAlAxAtA`AjA|AbD~ApDpAhBlAhA|@j@~@b@hAThANp@@bD_@lBq@fF_Ch[_PpPaIpOyHdFcCfCo@x@En@^Tp@?v@Sn@c@\\oGvCU?MMU?MLGZ@TLTTBVj@\\pB@b@GpEHvD^rEVlAfAlDrArC~DlGXr@XbA`@bA~AbC~B|BxArAbCjBlCnDvFhMjAxEvAdFh@`COFM\\D^PPN?NMdCfH`G|O`EzFbApBd@fAfA~DxCpJfE~IjCxDrAbAhFhDx@RxDj@`[~@`J~DtAP|@`@hF`FfD~DVT~DtAp@Ll@DFAr@iCFGH@TLx@n@"
//				,"bbox":[1.3477422598244755,43.4786703988424,1.4093987958743899,43.59962654387257]
//				}
//			,"info":
//				{"took":0.013912644
//				,"tookGeocoding":3.9068053
//				,"routeFound":true}
//			}
			JSONTokener t = new JSONTokener(sb.toString());
			JSONObject json = new JSONObject(t);
			
			JSONObject route = json.getJSONObject("route");
			int duration = route.getInt("time");
			double distance = route.getDouble("distance");
			JSONArray to = route.getJSONArray("to");
			double lat = to.getDouble(1);
			double lon = to.getDouble(0);
			
			d.setDistance(distance);
			d.setDuration(new Double(duration));
			d.setLat(lat);
			d.setLon(lon);
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
		
		
	}
}
