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

public class LoginActivity extends Activity {

	private EditText inputEmail, inputPassword;
	private Button btnLogIn, btnSignUp;
	private FirebaseAuth auth;

	String EMAIL = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		auth = FirebaseAuth.getInstance();
		FirebaseUser user = auth.getCurrentUser();
		if (user != null) {
			// User is signed in
			Log.d("AppInfo", "onAuthStateChanged:signed_in:" + user.getUid());
			EMAIL = auth.getCurrentUser().getEmail();
			goToMain();
		} else {
			// User is signed out
			Log.d("AppInfo", "onAuthStateChanged:signed_out");
		}

		btnSignUp = (Button) findViewById(R.id.signUpButton);
		btnLogIn = (Button) findViewById(R.id.loginButton);
		inputEmail = (EditText) findViewById(R.id.emailText);
		inputPassword = (EditText) findViewById(R.id.passwordText);

		btnSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), SignupActivity.class));
			}
		});

		btnLogIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String email = inputEmail.getText().toString();
				final String password = inputPassword.getText().toString();

				if (TextUtils.isEmpty(email)) {
					Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
					return;
				}

				//authenticate user
				auth.signInWithEmailAndPassword(email, password)
						.addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
							@Override
							public void onComplete(@NonNull Task<AuthResult> task) {
								// If sign in fails, display a message to the user. If sign in succeeds
								// the auth state listener will be notified and logic to handle the
								// signed in user can be handled in the l\\
								if (!task.isSuccessful()) {
									// there was an error
									if (password.length() < 6) {
										inputPassword.setError("Password length should be >= 6");
									} else {
										Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
									}
								} else {
									EMAIL = email;
									goToMain();
								}
							}
						});

			}
		});
	}

	public void goToMain() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("email", EMAIL);
		startActivity(intent);
	}
}
