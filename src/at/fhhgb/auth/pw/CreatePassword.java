/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.fhhgb.auth.pw;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import at.fhhgb.auth.lib.intent.IntentIntegrator.Extras;
import at.fhhgb.auth.provider.AuthDb.Feature;

/**
 * Lets the user create a password, if none exists yet. 
 * No constraints except the passwords have to be equal.
 * @author thomaskaiser
 *
 */
public class CreatePassword extends Activity {
	
	private static final String TAG = "PWAuth";
	private long userId;
	private EditText editPw1;
	private EditText editPw2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_password);
		
		userId = getIntent().getLongExtra(Extras.EXTRA_USER_ID, -1);
		editPw1 = (EditText) findViewById(R.id.edit_new_pw_1);
		editPw2 = (EditText) findViewById(R.id.edit_new_pw_2);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			createPassword();
			break;

		default:
			break;
		}
	}

	private void createPassword() {
		String pw1 = editPw1.getText().toString();
		String pw2 = editPw2.getText().toString();
		
		if (TextUtils.isEmpty(pw1) || TextUtils.isEmpty(pw2)) {
			Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (!pw1.equals(pw2)) {
			Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		insertFeature(pw1);
	}

	private void insertFeature(String password) {
		long modeId = ((PWApplication)getApplication()).getModeId();
		
		ContentValues cv = new ContentValues();
		cv.put(Feature.SUBJECT_ID, userId);
		cv.put(Feature.MODE_ID, modeId);
		cv.put(Feature.REPRESENTATION, password);
		Uri insert = getContentResolver().insert(Feature.CONTENT_URI, cv);
		Log.d(TAG, "Inserted password to URI: " + insert);
		
		if (ContentUris.parseId(insert) != -1) {
			finish();
		} else {
			Toast.makeText(this, "Could not save password!", Toast.LENGTH_LONG).show();
		}
	}

}
