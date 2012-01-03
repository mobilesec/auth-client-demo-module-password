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

import android.app.Application;
import android.database.Cursor;
import at.fhhgb.auth.provider.AuthDb.Mode;

/**
 * @author thomaskaiser
 *
 */
public class PWApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		initAuthMethod();
	}

	private void initAuthMethod() {
		if (!pwAuthMethodExists()) {
			createAuthMethod();
		}
	}

	private void createAuthMethod() {
		getContentResolver().insert(Mode.CONTENT_URI, Defs.DEFAULT_CONTENT_VALUES);
	}

	private boolean pwAuthMethodExists() {
		String[] projection = {
				Mode.NAME
		};
		String selection = Mode.NAME + " = ?";
		String[] selectionArgs = {
				Defs.UNIQUE_NAME
		};
		Cursor c = getContentResolver().query(Mode.CONTENT_URI, projection, selection, selectionArgs, null);
		
		if (!c.moveToFirst()) 
			return false;
		
		String existingName = c.getString(c.getColumnIndexOrThrow(Mode.NAME));
		
		return Defs.UNIQUE_NAME.equals(existingName);
	}

}
