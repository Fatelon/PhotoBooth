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
package com.groundupworks.partyphotobooth.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.groundupworks.partyphotobooth.MyApplication;
import com.groundupworks.partyphotobooth.R;
import com.groundupworks.partyphotobooth.SendMail;
import com.groundupworks.partyphotobooth.helpers.PreferencesHelper;
import com.groundupworks.partyphotobooth.helpers.TextHelper;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Confirmation screen for photo strip submission.
 *
 * @author Benedict Lau
 */
public class ConfirmationFragment extends Fragment {

    /**
     * The name of the auto-submission timer.
     */
    private static final String AUTO_SUBMISSION_TIMER_NAME = "submissionTimer";

    /**
     * The short timeout for auto-submission to trigger in milliseconds.
     */
    private static final long AUTO_SUBMISSION_TIMEOUT_SHORT = 150000L;

    /**
     * The long timeout for auto-submission to trigger in milliseconds.
     */
    private static final long AUTO_SUBMISSION_TIMEOUT_LONG = 600000L;

    /**
     * Timer for scheduling auto-submission of photo strip.
     */
    private Timer mSubmissionTimer = null;

    /**
     * The selected auto-submission timeout.
     */
    private long mAutoSubmissionTimeout = AUTO_SUBMISSION_TIMEOUT_LONG;

    /**
     * Callbacks for this fragment.
     */
    private WeakReference<ConfirmationFragment.ICallbacks> mCallbacks = null;

    private PreferencesHelper mPreferencesHelper = new PreferencesHelper();

    //
    // Views.
    //

    private TextView mMessage;

    private Button mSubmit;

    private Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = new WeakReference<ConfirmationFragment.ICallbacks>((ConfirmationFragment.ICallbacks) activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = container.getContext();
        /*
         * Inflate views from XML.
         */
        View view = inflater.inflate(R.layout.fragment_confirmation, container, false);
        mMessage = (TextView) view.findViewById(R.id.confirmation_message);
        mSubmit = (Button) view.findViewById(R.id.confirmation_button_submit);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get from preference.
        PreferencesHelper preferencesHelper = new PreferencesHelper();
        PreferencesHelper.PhotoBoothMode mode = preferencesHelper.getPhotoBoothMode(getActivity());



        String message = getString(R.string.confirmation__message, getString(R.string.app_name));
        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);

        // Set submission mode.
        if (PreferencesHelper.PhotoBoothMode.AUTOMATIC.equals(mode)) {
            mAutoSubmissionTimeout = AUTO_SUBMISSION_TIMEOUT_SHORT;
        } else {
            mSubmit.setVisibility(View.VISIBLE);
            mSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Disable to prevent multiple clicks.
                    v.setEnabled(false);

                    // Call to client.
                    ICallbacks callbacks = getCallbacks();
                    if (callbacks != null) {
                        callbacks.onSubmit();

                        // Clear weak reference to prevent possibility of duplicate calls.
                        mCallbacks.clear();
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Schedule auto-submission of the fragment.
        mSubmissionTimer = new Timer(AUTO_SUBMISSION_TIMER_NAME);
        mSubmissionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Post submission request to ui thread.
                final Activity activity = getActivity();
                if (activity != null && !activity.isFinishing()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                            public void run() {

                            // Call to client.
                            ICallbacks callbacks = getCallbacks();
                            if (callbacks != null) {
                                callbacks.onSubmit();

                                // Clear weak reference to prevent possibility of duplicate calls.
                                mCallbacks.clear();
                            }
                        }
                    });
                }
            }
        }, mAutoSubmissionTimeout);
    }

    @Override
    public void onPause() {
        // Cancel timer for auto-submission.
        if (mSubmissionTimer != null) {
            mSubmissionTimer.cancel();
            mSubmissionTimer = null;
        }

        super.onPause();
    }

    //
    // Private methods.
    //

    /**
     * Gets the callbacks for this fragment.
     *
     * @return the callbacks; or null if not set.
     */
    private ConfirmationFragment.ICallbacks getCallbacks() {
        ConfirmationFragment.ICallbacks callbacks = null;
        if (mCallbacks != null) {
            callbacks = mCallbacks.get();
        }
        return callbacks;
    }

    //
    // Public methods.
    //

    /**
     * Creates a new {@link ConfirmationFragment} instance.
     *
     * @return the new {@link ConfirmationFragment} instance.
     */
    public static ConfirmationFragment newInstance() {
        return new ConfirmationFragment();
    }

    //
    // Interfaces.
    //

    /**
     * Callbacks for this fragment.
     */
    public interface ICallbacks {

        /**
         * The submit button is clicked.
         */
        public void onSubmit();
    }
}
