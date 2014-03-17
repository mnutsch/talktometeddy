package com.talkingteddy;

import org.json.JSONException;
import org.json.JSONObject;

import Library.DatabaseHandler;
import Library.UserFunctions;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
public class LoginActivity extends Activity {
	
	Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;
    TextView registerScreen;
    TextView skipLoginRegiserScreen;
    
	// JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_time";
    
    private static Context context;
	
    
    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Helper.showToast(toast, context);
            }
        });
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // setting default screen to login.xml
        setContentView(R.layout.login);
        
        registerScreen = (TextView) findViewById(R.id.link_to_register);
        // Importing all assets like buttons, text fields
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
        context = this;
        
        // Login button
        
     // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(inputPassword.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(inputEmail.getWindowToken(), 0);
                
            	 new Thread(new Runnable() {
            		 public void run() {
            			 
            			 String email = inputEmail.getText().toString();
                         String password = inputPassword.getText().toString();
                         UserFunctions userFunction = new UserFunctions();
                         JSONObject json = userFunction.loginUser(email, password);
          
                         // check for login response
                         try {
                             if (json.getString(KEY_SUCCESS) != null) {
                                 String res = json.getString(KEY_SUCCESS); 
                                 if(Integer.parseInt(res) == 1){
                                     // user successfully logged in
                                     // Store user details in SQLite Database
                                	 DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                                     JSONObject json_user = json.getJSONObject("user");
                                      
                                     // Clear all previous data in database
                                     userFunction.logoutUser(getApplicationContext());
                                     db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                        
                                      
                                     // Launch Dashboard Screen
                                     Intent dashboard = new Intent(getApplicationContext(), TalkingTeddyActivity.class);
                                      
                                     // Close all views before launching Dashboard
                                     dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                     startActivity(dashboard);
                                      
                                     // Close Login Screen
                                     finish();
                                 }else{
                                     // Error in login
                                	 showToast(json.get(KEY_ERROR_MSG).toString());

                                	 
                                 }
                             }
                         } catch (JSONException e) {
                             e.printStackTrace();
                     }
            	 }
            }).start();
         }
        });
 
        
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}