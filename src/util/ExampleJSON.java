package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExampleJSON {
	public String fileText;
	
	public static void main(String[] args) {
		ExampleJSON m = new ExampleJSON();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("C:\\Users\\JoJones\\Desktop\\test.json"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			
			m.fileText = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		JSONParser p = new JSONParser();
		
		try {
			JSONObject obj = (JSONObject) p.parse(m.fileText);
			System.out.println(obj);

			System.out.println("Field \"ID\"");
			System.out.println(obj.get("ID"));
			System.out.println();
			
			System.out.println("Testing arrays");
			System.out.println(obj.get("person"));
			System.out.println();
			
			JSONArray test = (JSONArray) obj.get("person");
			System.out.println("Testing more arrays");
			System.out.println(test.get(0));
			System.out.println();
			
			JSONObject obj2 = (JSONObject) test.get(1);
			System.out.println("Last test");
			System.out.println(obj2.get("name"));
		} catch (ParseException pe) {
			System.out.println("pos: " + pe.getPosition());
			System.out.println(pe);
		}
		
		JSONObject testing = new JSONObject();
		
		JSONObject x = new JSONObject();
		x.put("test", 32);
		JSONObject y = new JSONObject();
		y.put("dsaf", "DSKF");
		
		// EXAMPLE FOR PLAYER LIST
//		ArrayList<JSONObject> al = new ArrayList<JSONObject>();
//		for (int i = 0; i < players.size(); i++) {
//			JSONObject temp = new JSONObject();
//			temp.put("ID", i);
//			temp.put("name", players.get(i).getName());
//			...
//		}
		
		Object[] testArray = { x, y };
		
//		testArray.add("hello");
//		testArray.add("goodebye");
		
		testing.put("height", 66);
		testing.put("test", false);
		testing.put("UH", "nope");
		//testing.put("testArray", testArray);
		testing.put("otherArray", testArray);
		
		System.out.println();
		System.out.println("JSON object to send");
		System.out.println(testing);
	}
}
