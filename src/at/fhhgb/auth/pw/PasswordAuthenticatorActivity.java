package at.fhhgb.auth.pw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.fhhgb.auth.provider.AuthDb.Feature;
import at.fhhgb.auth.provider.AuthDb.Subject;

public class PasswordAuthenticatorActivity extends Activity implements OnClickListener {
    private Uri userUri;
	private EditText editPw;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        userUri = getIntent().getData();
        setupUi();
    }

	/**
	 * Loads user info from the content resolver and sets up button listener.
	 */
	private void setupUi() {
		TextView claimedName = (TextView) findViewById(R.id.txt_claim);
		Button okBtn = (Button) findViewById(R.id.btn_ok);
		editPw = (EditText) findViewById(R.id.edit_password);
		
		okBtn.setOnClickListener(this);
		
		Cursor cursor = managedQuery(userUri, null, null, null, null);
		if (cursor.moveToFirst()) {
			String firstName = cursor.getString(cursor.getColumnIndex(Subject.FIRST_NAME));
			String lastName = cursor.getString(cursor.getColumnIndex(Subject.LAST_NAME));
			
			claimedName.setText(lastName + ", " + firstName);
		} else {
			claimedName.setText("Error: Could not load user!");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			checkPassword();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 */
	private void checkPassword() {
		String enteredPassword = editPw.getText().toString();
		// query the provider for saved passwords for this user
		int userId = Integer.parseInt(userUri.getLastPathSegment());
		Uri uri = Feature.buildFeaturesForSubjectUri(userId);
		
		Cursor c = managedQuery(uri, null, null, null, null);  
		if (!c.moveToFirst()) {
			Toast.makeText(this, "No corresponding user found", Toast.LENGTH_SHORT).show();
		} 
		
		// TODO continue here with no existing user features
		String savedPassword = c.getString(c.getColumnIndexOrThrow(Feature.REPRESENTATION));
		
		String title;
		String message;
		if (enteredPassword.equals(savedPassword)) {
			title = "Password OK";
			message = "You entered the correct password";
		} else {
			title = "Password WRONG";
			message = "You entered the wrong password";
		}
		
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}
}