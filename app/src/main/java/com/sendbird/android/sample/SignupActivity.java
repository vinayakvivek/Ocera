package com.sendbird.android.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sendbird.android.SendBird;

public class SignupActivity extends Activity {

	private enum State {DISCONNECTED, CONNECTING, CONNECTED}

	/**
	 * To test push notifications with your own appId, you should replace google-services.json with yours.
	 * Also you need to set Server API Token and Sender ID in SendBird dashboard.
	 * Please carefully read "Push notifications" section in SendBird Android documentation
	 */
	private static final String appId = "B6EC64C5-19B9-449D-AAFD-3415FC8C3599"; /* Sample SendBird Application */


	private EditText inputEmail, inputPassword, inputNick;
	private Button btnLogIn, btnSignUp;
	private FirebaseAuth auth;

	String EMAIL = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		SendBird.init(appId, this);

		auth = FirebaseAuth.getInstance();

		btnSignUp = (Button) findViewById(R.id.signUpButton);
		btnLogIn = (Button) findViewById(R.id.loginButton);
		inputEmail = (EditText) findViewById(R.id.emailText);
		inputPassword = (EditText) findViewById(R.id.passwordText);
		inputNick = (EditText) findViewById(R.id.nickText);

		btnLogIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
			}
		});

		btnSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String email = inputEmail.getText().toString().trim();
				String password = inputPassword.getText().toString().trim();
				final String nick = inputNick.getText().toString().trim();

				if (TextUtils.isEmpty(email)) {
					Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (TextUtils.isEmpty(nick)) {
					Toast.makeText(getApplicationContext(), "Enter nick name!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (password.length() < 6) {
					Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
					return;
				}

				//create user
				auth.createUserWithEmailAndPassword(email, password)
						.addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
							@Override
							public void onComplete(@NonNull Task<AuthResult> task) {
								// If sign in fails, display a message to the user. If sign in succeeds
								// the auth state listener will be notified and logic to handle the
								// signed in user can be handled in the listener.
								if (!task.isSuccessful()) {
									Toast.makeText(SignupActivity.this, "Authentication failed",
											Toast.LENGTH_SHORT).show();
								} else {
									EMAIL = email;
									goToMain();
								}
							}
						});

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		FirebaseUser user = auth.getCurrentUser();
		if (user != null) {
			// User is signed in
			Log.d("AppInfo", "onAuthStateChanged:signed_in:" + user.getUid());
			EMAIL = user.getEmail();
			goToMain();
		} else {
			// User is signed out
			Log.d("AppInfo", "onAuthStateChanged:signed_out");
		}
	}

	public void goToMain() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("email", EMAIL);
		startActivity(intent);
	}
}
