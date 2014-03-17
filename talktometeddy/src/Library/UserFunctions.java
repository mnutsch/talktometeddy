package Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.content.Context;
 
public class UserFunctions {
     
    private JSONParser jsonParser;
     
    // Testing in localhost using wamp or xampp 
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String loginURL = "https://fierce-ridge-5371.herokuapp.com/api/users";
    private static String registerURL = "https://fierce-ridge-5371.herokuapp.com/api/users";
    private static String activityURL = "https://fierce-ridge-5371.herokuapp.com/api/activity";
    
    private static String login_tag = "login";
    private static String register_tag = "register";
     
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
     
    /**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = null;
		try {
			json = jsonParser.getJSONFromUrl(loginURL, params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
     
    /**
     * function make Login Request
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("username", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
         
        // getting JSON Object
        JSONObject json = null;
		try {
			json = jsonParser.getJSONFromUrl(registerURL, params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // return json
        return json;
    }
    
    public JSONObject trackUsage(Context context, String reco, String resposne)
    { 	 // getting JSON Object
        JSONObject json = null;
        
    	if (isUserLoggedIn(context))
    	{
    		DatabaseHandler db = new DatabaseHandler(context);
    		HashMap<String, String>  userDetails = db.getUserDetails();
    		
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("reco", reco));
	        params.add(new BasicNameValuePair("response", resposne));
	        params.add(new BasicNameValuePair("uid", userDetails.get("uid")));
	        
			try {
				json = jsonParser.getJSONFromUrl(activityURL, params);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        // return json
        return json;
    }
     
    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }
     
    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
     
}