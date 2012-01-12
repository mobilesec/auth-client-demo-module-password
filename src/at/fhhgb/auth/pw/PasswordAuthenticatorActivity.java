package at.fhhgb.auth.pw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.fhhgb.auth.lib.intent.IntentIntegrator.Extras;
import at.fhhgb.auth.lib.util.UIUtils;
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
        
        // this just for testing
        if (userUri == null) {
        	finish();
			return;
		}
		if (!checkUserStillExists()) {
			UIUtils.showErrorDialog(
					this,
					"User deleted",
					"The requested user does not exist anymore, authentication will not be possible!",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			return;
		}
        checkFeaturesExist();
    }

	private boolean checkUserStillExists() {
		Cursor c = managedQuery(userUri, null, null, null, null);
		return c.getCount() > 0;
	}

	private void checkFeaturesExist() {
		Uri featuresUri = Feature.buildFeaturesForSubjectUri(ContentUris.parseId(userUri));
		Cursor c = managedQuery(featuresUri, null, null, null, null);
		if (c.getCount() == 0) {
			askToCreatePassword();
		} else {
			setupUi();
		}
	}

	private void askToCreatePassword() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("No password");
		builder.setMessage("No password for this user yet, do you want to create one now?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_INSERT, Feature.CONTENT_URI);
				intent.putExtra(Extras.EXTRA_USER_ID, ContentUris.parseId(userUri));
				
				startActivity(intent);
				finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		builder.show();
	}

	/**
	 * Loads user info from the content resolver and sets up button listener.
	 */
	private void setupUi() {
		TextView claimedName = (TextView) findViewById(R.id.txt_claim);
		Button okBtn = (Button) findViewById(R.id.btn_ok);
		editPw = (EditText) findViewById(R.id.edit_password);
		
		okBtn.setOnClickListener(this);
		
		fillNameFields(claimedName);
	}

	/**
	 * @param claimedName
	 */
	private void fillNameFields(TextView claimedName) {
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
			hideKeypad();
			checkResults();
			break;
		default:
			break;
		}
	}

	private void hideKeypad() {
		IBinder windowToken = editPw.getWindowToken();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(windowToken, 0);
	}

	private void checkResults() {
		if (checkPassword()) {
			returnSuccess();
		} else {
			returnFailure();
		}
	}

	private void returnFailure() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Extras.EXTRA_RESULT, false);
		resultIntent.putExtra(Extras.EXTRA_RESULT_CONFIDENCE, 0.0d);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	private void returnSuccess() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Extras.EXTRA_RESULT, true);
		resultIntent.putExtra(Extras.EXTRA_RESULT_CONFIDENCE, 1.0d);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	private boolean checkPassword() {
		String enteredPassword = editPw.getText().toString();
		// query the provider for saved passwords for this user
		int userId = Integer.parseInt(userUri.getLastPathSegment());
		Uri uri = Feature.buildFeaturesForSubjectUri(userId);
		
		Cursor c = managedQuery(uri, null, null, null, null);  
		if (!c.moveToFirst()) {
			Toast.makeText(this, "No corresponding user found", Toast.LENGTH_SHORT).show();
		} 
		
		String savedPassword = c.getString(c.getColumnIndexOrThrow(Feature.REPRESENTATION));
		
		return enteredPassword.equals(savedPassword);
	}
}