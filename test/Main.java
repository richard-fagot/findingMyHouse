import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

	public static void main(String[] args) {
		String t = "Terrain 800 m²";
		Pattern compile = Pattern.compile("([\\d ]+)");
//		Pattern compile = Pattern.compile("\\b ([\\d ]+)m");
		Matcher matcher = compile.matcher(t);
		if(matcher.find()) {
			System.out.println("'"+matcher.group(1).trim()+"'");
		}

	}

}
