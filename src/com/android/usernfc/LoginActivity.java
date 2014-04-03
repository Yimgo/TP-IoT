package com.android.usernfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private TextView registerScreen;
	private Button login;
	private EditText etEmail;
	private EditText etPwd;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);
 
        registerScreen = (TextView) findViewById(R.id.link_to_register);
        login = (Button) findViewById(R.id.btnLogin);
 
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
        
        // Listening to login button
        login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO : check credentials
				Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
				startActivity(homeIntent);
			}
		});
    }
}
