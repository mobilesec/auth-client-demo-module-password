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

import android.content.ContentValues;
import at.fhhgb.auth.lib.intent.IntentIntegrator.AuthModes;
import at.fhhgb.auth.provider.AuthDb.Mode;

/**
 * Some content value definitions for initializing database.
 * @author thomaskaiser
 *
 */
public final class ContentValueDefaults {

	public static final String MODE_TYPE = AuthModes.PASSWORD;
	public static final String UNIQUE_NAME = "at.fhhgb.auth.pw.PasswordAuthenticator";
	public static final String DISPLAY_NAME = "Simple Password Authentication";
	
	public static ContentValues DEFAULT_CONTENT_VALUES;
	
	static {
		DEFAULT_CONTENT_VALUES = new ContentValues();
		DEFAULT_CONTENT_VALUES.put(Mode.NAME, UNIQUE_NAME);
		DEFAULT_CONTENT_VALUES.put(Mode.TYPE, MODE_TYPE);
		DEFAULT_CONTENT_VALUES.put(Mode.DISPLAY_NAME, DISPLAY_NAME);
		DEFAULT_CONTENT_VALUES.put(Mode.PACKAGE_NAME, "at.fhhgb.auth.pw");
		DEFAULT_CONTENT_VALUES.put(Mode.CLASS_NAME, ".PasswordAuthenticatorActivity");
	}
}
