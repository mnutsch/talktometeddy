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
 
public class RegisterActivity extends Activity {
	 	Button btnRegister;
	 	TextView btnLinkToLogin;
	    EditText inputFullName;
	    EditText inputEmail;
	    EditText inputPassword;
	    TextView registerErrorMsg;
	     
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
	        setContentView(R.layout.register);
	 
	        // Importing all assets like buttons, text fields
	        inputFullName = (EditText) findViewById(R.id.reg_fullname);
	        inputEmail = (EditText) findViewById(R.id.reg_email);
	        inputPassword = (EditText) findViewById(R.id.reg_password);
	        btnRegister = (Button) findViewById(R.id.btnRegister);
	        btnLinkToLogin = (TextView) findViewById(R.id.link_to_login);
	        context = this;
	         
	        // Register Button Click event
	        btnRegister.setOnClickListener(new View.OnClickListener() {  
	            public void onClick(View view) {
	            	InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                mgr.hideSoftInputFromWindow(inputPassword.getWindowToken(), 0);
	                mgr.hideSoftInputFromWindow(inputEmail.getWindowToken(), 0);

	                new Thread(new Runnable() {
	            		 public void run() {
	            			 String name = inputFullName.getText().toString();
	     	                String email = inputEmail.getText().toString();
	     	                String password = inputPassword.getText().toString();
	     	                UserFunctions userFunction = new UserFunctions();
	     	                JSONObject json = userFunction.registerUser(name, email, password);
	     	                 
	     	                // check for login response
	     	                try {
	     	                    if (json.getString(KEY_SUCCESS) != null) {
	     	                        String res = json.getString(KEY_SUCCESS); 
	     	                        if(Integer.parseInt(res) == 1){
	     	                            // user successfully registred
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
	     	                            // Close Registration Screen
	     	                            finish();
	     	                        }else{
	     	                            // Error in registration
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
	 
	        // Link to Login Screen
	        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
	 
	            public void onClick(View view) {
	                Intent i = new Intent(getApplicationContext(),
	                        LoginActivity.class);
	                startActivity(i);
	                // Close Registration View
	                finish();
	            }
	        });
	    }
	}