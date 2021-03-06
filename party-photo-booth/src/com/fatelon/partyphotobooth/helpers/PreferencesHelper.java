/*
 * This file is part of Flying PhotoBooth.
 * 
 * Flying PhotoBooth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flying PhotoBooth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flying PhotoBooth.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fatelon.partyphotobooth.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.Date;

/**
 * Helper for storing and retrieving user preferences.
 *
 * @author Benedict Lau
 */
public class PreferencesHelper {

    /**
     * Photo booth modes.
     */
    public enum PhotoBoothMode {

        /**
         * Self-serve mode uses front-facing camera and count down.
         */
        SELF_SERVE,

        /**
         * Automatic mode uses the front-facing camera and auto-triggers count down after capturing
         * the first frame. Discard is disabled in this mode.
         */
        AUTOMATIC,

        /**
         * Photographer mode uses back-facing camera and no count down.
         */
        PHOTOGRAPHER;
    }

    /**
     * Photo booth themes.
     */
    public enum PhotoBoothTheme {

        /**
         * Blue stripes.
         */
        STRIPES_BLUE,

        /**
         * Pink stripes.
         */
        STRIPES_PINK,

        /**
         * Orange stripes.
         */
        STRIPES_ORANGE,

        /**
         * Green stripes.
         */
        STRIPES_GREEN,

        /**
         * Minimalist theme.
         */
        MINIMALIST,

        /**
         * Vintage theme.
         */
        VINTAGE,

        /**
         * Carbon theme.
         */
        CARBON
    }

    /**
     * Photo strip templates.
     */
    public enum PhotoStripTemplate {

        /**
         * Single photo.
         */
        SINGLE(PhotoStripArrangement.VERTICAL, 1),

        /**
         * Vertical arrangement of 2 photos.
         */
        VERTICAL_2(PhotoStripArrangement.VERTICAL, 2),

        /**
         * Horizontal arrangement of 2 photos.
         */
        HORIZONTAL_2(PhotoStripArrangement.HORIZONTAL, 2),

        /**
         * Vertical arrangement of 3 photos.
         */
        VERTICAL_3(PhotoStripArrangement.VERTICAL, 3),

        /**
         * Horizontal arrangement of 3 photos.
         */
        HORIZONTAL_3(PhotoStripArrangement.HORIZONTAL, 3),

        /**
         * Vertical arrangement of 4 photos.
         */
        VERTICAL_4(PhotoStripArrangement.VERTICAL, 4),

        /**
         * Horizontal arrangement of 4 photos.
         */
        HORIZONTAL_4(PhotoStripArrangement.HORIZONTAL, 4),

        /**
         * Box arrangement of 4 photos.
         */
        BOX_4(PhotoStripArrangement.BOX, 4);

        /**
         * The arrangement of the photo strip.
         */
        private PhotoStripArrangement mArrangement;

        /**
         * The number of photos in the photo strip.
         */
        private int mNumPhotos;

        /**
         * Constructor.
         *
         * @param arrangement the arrangement of the photo strip.
         * @param numPhotos   the number of photos in the photo strip.
         */
        private PhotoStripTemplate(PhotoStripArrangement arrangement, int numPhotos) {
            mArrangement = arrangement;
            mNumPhotos = numPhotos;
        }

        /**
         * Gets the arrangement of the photo strip.
         *
         * @return the {@link PhotoStripArrangement}.
         */
        public PhotoStripArrangement getArrangement() {
            return mArrangement;
        }

        /**
         * Gets the number of photos in the photo strip.
         *
         * @return the stored number of photos.
         */
        public int getNumPhotos() {
            return mNumPhotos;
        }
    }

    /**
     * Photo strip arrangements.
     */
    public enum PhotoStripArrangement {

        /**
         * Vertical arrangement of photos.
         */
        VERTICAL,

        /**
         * Horizontal arrangement of photos.
         */
        HORIZONTAL,

        /**
         * Box arrangement of photos.
         */
        BOX
    }

    /**
     * Preference value to hide the event date.
     */
    public static final long EVENT_DATE_HIDDEN = -1L;

    /**
     * Key for the photo booth mode record.
     */
    private static final String KEY_PHOTO_BOOTH_MODE = "photoBoothMode";

    /**
     * Key for the photo booth theme record.
     */
    private static final String KEY_PHOTO_BOOTH_THEME = "photoBoothTheme";

    /**
     * Key for the photo strip template record.
     */
    private static final String KEY_PHOTO_STRIP_TEMPLATE = "photoStripTemplate";

    /**
     * Key for the first line of the event title record.
     */
    private static final String KEY_EVENT_LINE_ONE = "eventLineOne";

    /**
     * Key for the second line of the event title record.
     */
    private static final String KEY_EVENT_LINE_TWO = "eventLineTwo";

    /**
     * Key for the event logo uri record.
     */
    private static final String KEY_EVENT_LOGO_URI = "eventLogoUri";

    /**
     * Key for the event date record.
     */
    private static final String KEY_EVENT_DATE = "eventDate";

    /**
     * Key for whether enabled share services are shown in a notice screen.
     */
    private static final String KEY_NOTICE_ENABLED = "noticeEnabled";

    /**
     * The default preferences for the event title.
     */
    private static final String DEFAULT_EVENT_TITLE_PREFERENCE = "";

    /**
     * The default preferences for the event logo uri.
     */
    private static final String DEFAULT_EVENT_LOGO_URI_PREFERENCE = "";

    /**
     * Key for whether enabled posting mail messages.
     */
    private static final String KEY_MAIL_ENABLED = "keyMailEnabled";

    /**
     * Key default enabled posting mail messages.
     */
    private static final Boolean DEFAULT_KEY_MAIL_ENABLED = false;

    /**
     * The default preferences for the subject line of the mail settings fragment.
     */
    private static final String DEFAULT_SUBJECT_IN_MAIL_SETTINGS = "";

    /**
     * The default preferences for the message line of the mail settings fragment.
     */
    private static final String DEFAULT_MESSAGE_IN_MAIL_SETTINGS = "";

    /**
     * The default preferences for the email line of the mail settings fragment.
     */
    private static final String DEFAULT_EMAIL_IN_MAIL_SETTINGS = "";

    /**
     * The default preferences for the password line of the mail settings fragment.
     */
    private static final String DEFAULT_EMAIL_PASSWORD_IN_MAIL_SETTINGS = "";

    /**
     * The default preferences for the email line in the conformation fragment.
     */
    private static final String DEFAULT_EMAIL_IN_CONFORMATION = "";

    /**
     * Key for the subject line of the mail settings fragment.
     */
    private static final String KEY_SUBJECT_LINE_IN_MAIL_SETTINGS = "subjectLineMailSettings";

    /**
     * Key for the message line of the mail settings fragment.
     */
    private static final String KEY_MESSAGE_LINE_IN_MAIL_SETTINGS = "messageLineMailSettings";

    /**
     * Key for the message line of the mail settings fragment.
     */
    private static final String KEY_EMAIL_LINE_IN_MAIL_SETTINGS = "emailLineMailSettings";

    /**
     * Key for the password line of the mail settings fragment.
     */
    private static final String KEY_EMAIL_PASSWORD_LINE_IN_MAIL_SETTINGS = "passwordLineInMailSettings";

    /**
     * Key for the message line on the conformation fragment.
     */
    private static final String KEY_EMAIL_LINE_IN_CONFORMATION = "emailLineMailConformation";

    //
    // Public methods.
    //

    /**
     * Stores the photo booth mode preference.
     *
     * @param context the {@link Context}.
     * @param mode    one of {@link PhotoBoothMode}. Must not be null.
     */
    public void storePhotoBoothMode(Context context, PhotoBoothMode mode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        preferences.edit().putString(KEY_PHOTO_BOOTH_MODE, mode.name()).apply();
    }

    /**
     * Reads the photo booth mode preference.
     *
     * @param context the {@link Context}.
     * @return the stored {@link PhotoBoothMode}.
     */
    public PhotoBoothMode getPhotoBoothMode(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String mode = preferences.getString(KEY_PHOTO_BOOTH_MODE, PhotoBoothMode.SELF_SERVE.name());
        return PhotoBoothMode.valueOf(mode);
    }

    /**
     * Stores the photo booth theme preference.
     *
     * @param context the {@link Context}.
     * @param theme   one of {@link PhotoBoothTheme}. Must not be null.
     */
    public void storePhotoBoothTheme(Context context, PhotoBoothTheme theme) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        preferences.edit().putString(KEY_PHOTO_BOOTH_THEME, theme.name()).apply();
    }

    /**
     * Reads the photo booth theme preference.
     *
     * @param context the {@link Context}.
     * @return the stored {@link PhotoBoothTheme}.
     */
    public PhotoBoothTheme getPhotoBoothTheme(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String theme = preferences.getString(KEY_PHOTO_BOOTH_THEME, PhotoBoothTheme.STRIPES_BLUE.name());
        return PhotoBoothTheme.valueOf(theme);
    }

    /**
     * Stores the photo strip template preference.
     *
     * @param context  the {@link Context}.
     * @param template one of {@link PhotoStripTemplate}. Must not be null.
     */
    public void storePhotoStripTemplate(Context context, PhotoStripTemplate template) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        preferences.edit().putString(KEY_PHOTO_STRIP_TEMPLATE, template.name()).apply();
    }

    /**
     * Reads the photo strip template preference.
     *
     * @param context the {@link Context}.
     * @return the stored {@link PhotoStripTemplate}.
     */
    public PhotoStripTemplate getPhotoStripTemplate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String template = preferences.getString(KEY_PHOTO_STRIP_TEMPLATE, PhotoStripTemplate.VERTICAL_3.name());
        return PhotoStripTemplate.valueOf(template);
    }

    /**
     * Stores the first line of the event title.
     *
     * @param context      the {@link Context}.
     * @param eventLineOne the first line of the event title; or an empty string. Pass null to clear.
     */
    public void storeEventLineOne(Context context, String eventLineOne) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        if (eventLineOne != null && eventLineOne.length() > 0) {
            editor.putString(KEY_EVENT_LINE_ONE, eventLineOne).apply();
        } else {
            editor.remove(KEY_EVENT_LINE_ONE).apply();
        }
    }

    /**
     * Reads the first line of the event title.
     *
     * @param context the {@link Context}.
     * @return the first line of the event title; or an empty string.
     */
    public String getEventLineOne(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(KEY_EVENT_LINE_ONE, DEFAULT_EVENT_TITLE_PREFERENCE);
    }

    /**
     * Stores the second line of the event title.
     *
     * @param context      the {@link Context}.
     * @param eventLineTwo the second line of the event title; or an empty string. Pass null to clear.
     */
    public void storeEventLineTwo(Context context, String eventLineTwo) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        if (eventLineTwo != null && eventLineTwo.length() > 0) {
            editor.putString(KEY_EVENT_LINE_TWO, eventLineTwo).apply();
        } else {
            editor.remove(KEY_EVENT_LINE_TWO).apply();
        }
    }

    /**
     * Reads the second line of the event title.
     *
     * @param context the {@link Context}.
     * @return the second line of the event title; or an empty string.
     */
    public String getEventLineTwo(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(KEY_EVENT_LINE_TWO, DEFAULT_EVENT_TITLE_PREFERENCE);
    }

    /**
     * Stores the uri to the event logo image.
     *
     * @param context the {@link Context}.
     * @param uri     the uri to the event logo image; or an empty string. Pass null to clear.
     */
    public void storeEventLogoUri(Context context, String uri) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        if (uri != null && uri.length() > 0) {
            editor.putString(KEY_EVENT_LOGO_URI, uri).apply();
        } else {
            editor.remove(KEY_EVENT_LOGO_URI).apply();
        }
    }

    /**
     * Reads the uri to the event logo image.
     *
     * @param context the {@link Context}.
     * @return the uri to the event logo image; or an empty string.
     */
    public String getEventLogoUri(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(KEY_EVENT_LOGO_URI, DEFAULT_EVENT_LOGO_URI_PREFERENCE);
    }

    /**
     * Stores the date of the event in milliseconds since Jan. 1, 1970, midnight GMT.
     *
     * @param context   the {@link Context}.
     * @param eventDate the event date in milliseconds. Pass {@link PreferencesHelper#EVENT_DATE_HIDDEN} to hide event date.
     */
    public void storeEventDate(Context context, long eventDate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        preferences.edit().putLong(KEY_EVENT_DATE, eventDate).apply();
    }

    /**
     * Reads the date of the event in milliseconds since Jan. 1, 1970, midnight GMT.
     *
     * @param context the {@link Context}.
     * @return the event date in milliseconds; or {@link PreferencesHelper#EVENT_DATE_HIDDEN} if hidden. The current
     * date is returned if no record is stored.
     */
    public long getEventDate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getLong(KEY_EVENT_DATE, new Date().getTime());
    }

    /**
     * Stores whether enabled share services are shown in a notice screen.
     *
     * @param context   the {@link Context}.
     * @param isEnabled true to enable; false otherwise.
     */
    public void storeNoticeEnabled(Context context, boolean isEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        preferences.edit().putBoolean(KEY_NOTICE_ENABLED, isEnabled).apply();
    }

    /**
     * Reads whether enabled share services are shown in a notice screen.
     *
     * @param context the {@link Context}.
     * @return true if enabled; false otherwise.
     */
    public boolean getNoticeEnabled(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getBoolean(KEY_NOTICE_ENABLED, false);
    }

    /**
     * Stores whether enabled posting mail messages.
     *
     * @param context the {@link Context}.
     * @param isEnabled true to enable; false otherwise.
     */
    public void storeMailEnabled(Context context, boolean isEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        preferences.edit().putBoolean(KEY_MAIL_ENABLED, isEnabled).apply();
    }

    /**
     * Reads whether enabled posting mail messages.
     *
     * @param context the {@link Context}.
     * @return true if enabled; false otherwise.
     */
    public boolean getMailEnabled(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getBoolean(KEY_MAIL_ENABLED, DEFAULT_KEY_MAIL_ENABLED);
    }

    /**
     * Stores the subject line of the mail settings fragment.
     *
     * @param context      the {@link Context}.
     * @param subjectLine the subject line of the mail settings fragment; or an empty string. Pass null to clear.
     */
    public void storeMailSettingsSubject(Context context, String subjectLine) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        if (subjectLine != null && subjectLine.length() > 0) {
            editor.putString(KEY_SUBJECT_LINE_IN_MAIL_SETTINGS, subjectLine).apply();
        } else {
            editor.remove(KEY_SUBJECT_LINE_IN_MAIL_SETTINGS).apply();
        }
    }

    /**
     * Reads the subject line of the mail settings fragment.
     *
     * @param context the {@link Context}.
     * @return the subject line of the mail settings fragment; or an empty string.
     */
    public String getMailSettingsSubject(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(KEY_SUBJECT_LINE_IN_MAIL_SETTINGS, DEFAULT_SUBJECT_IN_MAIL_SETTINGS);
    }

    /**
     * Stores the subject line of the mail settings fragment.
     *
     * @param context      the {@link Context}.
     * @param messageLine the message line of the mail settings fragment; or an empty string. Pass null to clear.
     */
    public void storeMailSettingsMessage(Context context, String messageLine) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        if (messageLine != null && messageLine.length() > 0) {
            editor.putString(KEY_MESSAGE_LINE_IN_MAIL_SETTINGS, messageLine).apply();
        } else {
            editor.remove(KEY_MESSAGE_LINE_IN_MAIL_SETTINGS).apply();
        }
    }

    /**
     * Reads the subject line of the mail settings fragment.
     *
     * @param context the {@link Context}.
     * @return the message line of the mail settings fragment; or an empty string.
     */
    public String getMailSettingsMessage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(KEY_MESSAGE_LINE_IN_MAIL_SETTINGS, DEFAULT_MESSAGE_IN_MAIL_SETTINGS);
    }

    /**
     * Stores the email line of the mail settings fragment.
     *
     * @param context      the {@link Context}.
     * @param emailLine the email line of the mail settings fragment; or an empty string. Pass null to clear.
     */
    public void storeMailSettingsEmail(Context context, String emailLine) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        if (emailLine != null && emailLine.length() > 0) {
            editor.putString(KEY_EMAIL_LINE_IN_MAIL_SETTINGS, emailLine).apply();
        } else {
            editor.remove(KEY_EMAIL_LINE_IN_MAIL_SETTINGS).apply();
        }
    }

    /**
     * Reads the email line of the mail settings fragment.
     *
     * @param context the {@link Context}.
     * @return the email line of the mail settings fragment; or an empty string.
     */
    public String getMailSettingsEmail(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(KEY_EMAIL_LINE_IN_MAIL_SETTINGS, DEFAULT_EMAIL_IN_MAIL_SETTINGS);
    }

    /**
     * Stores the password line of the mail settings fragment.
     *
     * @param context      the {@link Context}.
     * @param passwordLine the password line of the mail settings fragment; or an empty string. Pass null to clear.
     */
    public void storeMailPasswordSettingsEmail(Context context, String passwordLine) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        if (passwordLine != null && passwordLine.length() > 0) {
            editor.putString(KEY_EMAIL_PASSWORD_LINE_IN_MAIL_SETTINGS, passwordLine).apply();
        } else {
            editor.remove(KEY_EMAIL_PASSWORD_LINE_IN_MAIL_SETTINGS).apply();
        }
    }

    /**
     * Reads the password line of the mail settings fragment.
     *
     * @param context the {@link Context}.
     * @return the password line of the mail settings fragment; or an empty string.
     */
    public String getMailPasswordSettingsEmail(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(KEY_EMAIL_PASSWORD_LINE_IN_MAIL_SETTINGS, DEFAULT_EMAIL_PASSWORD_IN_MAIL_SETTINGS);
    }

    /**
     * Stores the user email line of the confirmation fragment.
     *
     * @param context      the {@link Context}.
     * @param emailLine the user email line of the confirmation fragment; or an empty string. Pass null to clear.
     */
    public void storeUserMail(Context context, String emailLine) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        if (emailLine != null && emailLine.length() > 0) {
            editor.putString(KEY_EMAIL_LINE_IN_CONFORMATION, emailLine).apply();
        } else {
            editor.remove(KEY_EMAIL_LINE_IN_CONFORMATION).apply();
        }
    }

    /**
     * Reads the user email line.
     *
     * @param context the {@link Context}.
     * @return the user email line of the confirmation fragment; or an empty string.
     */
    public String getUserMail(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return preferences.getString(KEY_EMAIL_LINE_IN_CONFORMATION, DEFAULT_EMAIL_IN_CONFORMATION);
    }
}
