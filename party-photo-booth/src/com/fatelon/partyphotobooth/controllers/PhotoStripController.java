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
package com.fatelon.partyphotobooth.controllers;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Message;
import android.util.SparseArray;

import com.fatelon.partyphotobooth.MailObject;
import com.fatelon.partyphotobooth.MyApplication;
import com.fatelon.partyphotobooth.arrangements.TitledBoxArrangement;
import com.fatelon.partyphotobooth.arrangements.TitledHorizontalArrangement;
import com.fatelon.partyphotobooth.arrangements.TitledVerticalArrangement;
import com.fatelon.partyphotobooth.fragments.PhotoStripFragment;
import com.fatelon.partyphotobooth.helpers.PreferencesHelper;
import com.fatelon.partyphotobooth.helpers.TextHelper;
import com.fatelon.partyphotobooth.themes.Theme;
import com.fatelon.lib.photobooth.framework.BaseController;
import com.fatelon.lib.photobooth.helpers.ImageHelper;
import com.fatelon.lib.photobooth.helpers.ImageHelper.Arrangement;
import com.fatelon.partyphotobooth.MyBDHelper;
import com.groundupworks.partyphotobooth.R;
import com.fatelon.partyphotobooth.SendMailService;
import com.fatelon.partyphotobooth.arrangements.BaseTitleHeader;
import com.groundupworks.wings.Wings;
import com.groundupworks.wings.dropbox.DropboxEndpoint;
import com.groundupworks.wings.facebook.FacebookEndpoint;
import com.groundupworks.wings.gcp.GoogleCloudPrintEndpoint;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class PhotoStripController extends BaseController {

    //
    // Controller events. The ui should be notified of these events.
    //

    public static final int ERROR_JPEG_DATA = -1;

    public static final int ERROR_PHOTO_MISSING = -2;

    public static final int ERROR_PHOTO_STRIP_SUBMIT = -3;

    public static final int THUMB_BITMAP_READY = 0;

    public static final int FRAME_REMOVED = 1;

    public static final int PHOTO_STRIP_READY = 2;

    public static final int PHOTO_STRIP_SUBMITTED = 3;

    //
    // Message bundle keys.
    //

    public static final String MESSAGE_BUNDLE_KEY_FACEBOOK_SHARED = "facebookShared";

    public static final String MESSAGE_BUNDLE_KEY_DROPBOX_SHARED = "dropboxShared";

    public static final String MESSAGE_BUNDLE_KEY_GCP_SHARED = "gcpShared";

    /**
     * The {@link Application} {@link Context}.
     */
    private Context mContext;

    /**
     * The {@link PreferencesHelper}.
     */
    private PreferencesHelper mPreferencesHelper;

    /**
     * The first line of the event title.
     */
    private String mLineOne = null;

    /**
     * The second line of the event title.
     */
    private String mLineTwo = null;

    /**
     * The date of the event.
     */
    private String mDate = null;

    /**
     * The event logo.
     */
    private Bitmap mLogo = null;

    /**
     * The photo strip arrangement.
     */
    private PreferencesHelper.PhotoStripArrangement mArrangementPref;

    /**
     * The theme.
     */
    private Theme mTheme;

    /**
     * The total number of frames to capture.
     */
    private int mFramesTotalPref;

    /**
     * Pixel size of frame thumbnails.
     */
    private int mThumbSize;

    /**
     * List storing the frame bitmaps used to construct the photo strip.
     */
    private List<Bitmap> mFramesList;

    /**
     * Map storing the mapping between unique keys used to identify each frame bitmap and the bitmaps themselves.
     */
    private SparseArray<Bitmap> mFramesMap;

    /**
     * The unique key for the current frame.
     */
    private int mFramesCurrKey;

    /**
     * Constructor.
     */
    public PhotoStripController() {
        mContext = MyApplication.getContext();

        // Set params from preferences.
        mPreferencesHelper = new PreferencesHelper();
        mLineOne = mPreferencesHelper.getEventLineOne(mContext);
        mLineTwo = mPreferencesHelper.getEventLineTwo(mContext);

        long date = mPreferencesHelper.getEventDate(mContext);
        if (date != PreferencesHelper.EVENT_DATE_HIDDEN) {
            mDate = TextHelper.getDateString(mContext, date);
        }

        if (TextHelper.isValid(mPreferencesHelper.getEventLogoUri(mContext))) {
            mLogo = MyApplication.getBitmapCache().tryGet(BaseTitleHeader.EVENT_LOGO_CACHE_KEY);
        }

        PreferencesHelper.PhotoStripTemplate template = mPreferencesHelper.getPhotoStripTemplate(mContext);
        mArrangementPref = template.getArrangement();

        mTheme = Theme.from(mContext, mPreferencesHelper.getPhotoBoothTheme(mContext));

        // Set params for frame management.
        mFramesTotalPref = template.getNumPhotos();
        mFramesList = new LinkedList<Bitmap>();
        mFramesMap = new SparseArray<Bitmap>(mFramesTotalPref);
        mFramesCurrKey = 0;

        // Set params from resources.
        Resources res = mContext.getResources();
        mThumbSize = res.getDimensionPixelSize(R.dimen.photo_thumb_size);
    }

    @Override
    protected void handleEvent(Message msg) {
        switch (msg.what) {
            case PhotoStripFragment.JPEG_DATA_READY:
                Bundle bundle = msg.getData();
                byte[] jpegData = bundle.getByteArray(PhotoStripFragment.MESSAGE_BUNDLE_KEY_JPEG_DATA);
                float rotation = bundle.getFloat(PhotoStripFragment.MESSAGE_BUNDLE_KEY_ROTATION);
                boolean reflection = bundle.getBoolean(PhotoStripFragment.MESSAGE_BUNDLE_KEY_REFLECTION);
                processJpegData(jpegData, rotation, reflection);
                break;
            case PhotoStripFragment.FRAME_REMOVAL:
                processFrameRemoval(msg.arg1);
                break;
            case PhotoStripFragment.PHOTO_STRIP_SUBMIT:
                processPhotoStripSubmission();
                break;
            default:
                break;
        }
    }

    //
    // Private methods.
    //

    /**
     * Reports an error event to ui.
     *
     * @param error the error.
     */
    private void reportError(int error) {
        Message uiMsg = Message.obtain();
        uiMsg.what = error;
        sendUiUpdate(uiMsg);
    }

    /**
     * Processes Jpeg data and notifies ui.
     *
     * @param jpegData   byte array of Jpeg data.
     * @param rotation   clockwise rotation applied to image in degrees.
     * @param reflection horizontal reflection applied to image.
     */
    private void processJpegData(byte[] jpegData, float rotation, boolean reflection) {
        Bitmap frame = ImageHelper.createImage(jpegData, rotation, reflection, mTheme.getFilter());
        if (frame != null) {
            // Create thumbnail bitmap.
            Bitmap thumb = Bitmap.createScaledBitmap(frame, mThumbSize, mThumbSize, true);
            if (thumb != null) {
                // Store frame bitmap.
                int key = storeFrame(frame);

                // Notify ui.
                Message uiMsg = Message.obtain();
                if (isPhotoStripComplete()) {
                    // The last thumbnail bitmap is ready. The photo strip is complete.
                    uiMsg.what = PHOTO_STRIP_READY;
                } else {
                    // A thumbnail bitmap is ready. The photo strip still needs more frames.
                    uiMsg.what = THUMB_BITMAP_READY;
                }
                uiMsg.arg1 = key;
                uiMsg.obj = thumb;
                sendUiUpdate(uiMsg);
            } else {
                // An error has occurred.
                reportError(ERROR_JPEG_DATA);
            }
        } else {
            // An error has occurred.
            reportError(ERROR_JPEG_DATA);
        }
    }

    /**
     * Processes a frame removal request and notifies ui.
     *
     * @param key the key of the frame to remove.
     */
    private void processFrameRemoval(int key) {
        // Remove frame.
        Bitmap frame = mFramesMap.get(key);
        mFramesList.remove(frame);

        // Remove mapping.
        mFramesMap.delete(key);

        // Notify ui.
        Message uiMsg = Message.obtain();
        uiMsg.what = FRAME_REMOVED;
        sendUiUpdate(uiMsg);
    }

    /**
     * Processes a photo strip submission request and notifies ui.
     */
    private void processPhotoStripSubmission() {
        /*
         * Confirm frame count.
         */
        if (mFramesList.size() < mFramesTotalPref) {
            reportError(ERROR_PHOTO_MISSING);
            return;
        }

        /*
         * Create photo strip.
         */
        // Select arrangement.
        Arrangement arrangement = null;
        if (PreferencesHelper.PhotoStripArrangement.HORIZONTAL.equals(mArrangementPref)) {
            arrangement = new TitledHorizontalArrangement(mLineOne, mLineTwo, mDate, mLogo, mTheme.getFont());
        } else if (PreferencesHelper.PhotoStripArrangement.BOX.equals(mArrangementPref)) {
            arrangement = new TitledBoxArrangement(mLineOne, mLineTwo, mDate, mLogo, mTheme.getFont());
        } else {
            arrangement = new TitledVerticalArrangement(mLineOne, mLineTwo, mDate, mLogo, mTheme.getFont());
        }

        // Create photo strip as a single bitmap.
        Bitmap[] bitmaps = new Bitmap[mFramesTotalPref];
        int i = 0;
        for (Bitmap frame : mFramesList) {
            bitmaps[i++] = frame;
        }
        Bitmap photoStrip = ImageHelper.createPhotoStrip(bitmaps, arrangement);

        /////////////////

        photoStrip = addBottomSign("POWERED BY USIE BOOTH", photoStrip, mTheme.getFont());

        /////////////////

        // Reset frame management params.
        mFramesList.clear();
        mFramesMap.clear();
        mFramesCurrKey = 0;

        /*
         * Save photo strip bitmap as Jpeg.
         */
        Context context = MyApplication.getContext();
        try {
            String imageDirectory = ImageHelper.getCapturedImageDirectory(context
                    .getString(R.string.image_helper__image_folder_name));
            if (imageDirectory != null) {
                String imageName = ImageHelper.generateCapturedImageName(context
                        .getString(R.string.image_helper__image_filename_prefix));
                File file = new File(imageDirectory, imageName);
                final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

                // Convert to Jpeg and writes to file.
                boolean isSuccessful = ImageHelper.writeJpeg(photoStrip, outputStream);
                outputStream.flush();
                outputStream.close();



                ///// Attention!!!!! shit code!
                try {
                    boolean isEmailEnabled = mPreferencesHelper.getMailEnabled(context);
                    if (isEmailEnabled) {
                        context.stopService(new Intent(context, SendMailService.class));
                        MailObject mailObject = new MailObject();
                        mailObject.setHostAddress(mPreferencesHelper.getMailSettingsEmail(context));
                        mailObject.setHostPassword(mPreferencesHelper.getMailPasswordSettingsEmail(context));
                        mailObject.setUserAddress(mPreferencesHelper.getUserMail(context));
                        mailObject.setFilePath(file.getPath());
                        mailObject.setSubject(mPreferencesHelper.getMailSettingsSubject(context));
                        mailObject.setMessage(mPreferencesHelper.getMailSettingsMessage(context));

                        MyBDHelper myBDHelper = MyBDHelper.getInstance(context);
                        myBDHelper.addMailObject(mailObject);
                        context.startService(new Intent(context, SendMailService.class));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                /////////////////////////


                if (isSuccessful) {
                    String jpegPath = file.getPath();
                    // Request adding Jpeg to Android Gallery.
                    MediaScannerConnection.scanFile(context, new String[]{jpegPath},
                            new String[]{ImageHelper.JPEG_MIME_TYPE}, null);

                    // Share to Facebook.
                    boolean facebookShared = false;
                    if (Wings.getEndpoint(FacebookEndpoint.class).isLinked()) {
                        facebookShared = Wings.share(jpegPath, FacebookEndpoint.class);
                    }

                    // Share to Dropbox.
                    boolean dropboxShared = false;
                    if (Wings.getEndpoint(DropboxEndpoint.class).isLinked()) {
                        dropboxShared = Wings.share(jpegPath, DropboxEndpoint.class);
                    }

                    // Share to Google Cloud Print.
                    boolean gcpShared = false;
                    if (Wings.getEndpoint(GoogleCloudPrintEndpoint.class).isLinked()) {
                        gcpShared = Wings.share(jpegPath, GoogleCloudPrintEndpoint.class);
                    }

                    // Notify ui the Jpeg is saved and shared to linked services.
                    Message uiMsg = Message.obtain();
                    uiMsg.what = PHOTO_STRIP_SUBMITTED;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(MESSAGE_BUNDLE_KEY_FACEBOOK_SHARED, facebookShared);
                    bundle.putBoolean(MESSAGE_BUNDLE_KEY_DROPBOX_SHARED, dropboxShared);
                    bundle.putBoolean(MESSAGE_BUNDLE_KEY_GCP_SHARED, gcpShared);
                    uiMsg.setData(bundle);
                    sendUiUpdate(uiMsg);
                } else {
                    reportError(ERROR_PHOTO_STRIP_SUBMIT);
                }
            } else {
                // Invalid external storage state or failed directory creation.
                reportError(ERROR_PHOTO_STRIP_SUBMIT);
            }
        } catch (FileNotFoundException e) {
            reportError(ERROR_PHOTO_STRIP_SUBMIT);
        } catch (IOException e) {
            reportError(ERROR_PHOTO_STRIP_SUBMIT);
        }

        /*
         * Recycle photo strip bitmap.
         */
        if (photoStrip != null) {
            photoStrip.recycle();
            photoStrip = null;
        }
    }

    public static Bitmap addBottomSign(String bottomSignTextLine, Bitmap mainBitmap, Typeface font){
        Bitmap returnBitmap = null;
        int returnBitmapWidth = mainBitmap.getWidth();
        int returnBitmapHeight = mainBitmap.getHeight();
        returnBitmap = Bitmap.createBitmap(returnBitmapWidth, returnBitmapHeight, ImageHelper.BITMAP_CONFIG);
        if (returnBitmap != null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(font);
            paint.setShadowLayer(0f, 0f, 0f, Color.WHITE);
            paint.setColor(Color.GRAY);
            float optimalTextSize = TextHelper.getFittedTextSize(bottomSignTextLine, returnBitmapWidth, 50, paint);
//            optimalTextSize *= 2 / 3;
            paint.setTextSize(optimalTextSize);
            Canvas canvas = new Canvas(returnBitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mainBitmap, 0, 0, null);

            canvas.drawText(bottomSignTextLine, returnBitmapWidth / 2, mainBitmap.getHeight() - 50, paint);
        }

        return returnBitmap;
    }

    /**
     * Stores frame bitmap in next available slot in frames map.
     *
     * @param frame the bitmap to store. Must not be null.
     * @return the key of the stored frame.
     */
    private int storeFrame(Bitmap frame) {
        int key = mFramesCurrKey;

        // Add frame to list and map.
        mFramesList.add(frame);
        mFramesMap.put(key, frame);

        // Increment frame key.
        mFramesCurrKey++;

        return key;
    }

    /**
     * Checks whether we have all the frames needed to construct a photo strip.
     *
     * @return true if we have enough frames; false otherwise.
     */
    private boolean isPhotoStripComplete() {
        return mFramesList.size() == mFramesTotalPref;
    }
}
