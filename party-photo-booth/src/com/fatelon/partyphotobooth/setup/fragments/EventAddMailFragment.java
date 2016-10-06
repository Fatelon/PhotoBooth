package com.fatelon.partyphotobooth.setup.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fatelon.partyphotobooth.helpers.PreferencesHelper;
import com.groundupworks.partyphotobooth.R;
import com.fatelon.partyphotobooth.helpers.TextHelper;

/**
 * Ui for setting up the mail information.
 *
 * Created by Fatelon on 10.09.2016.
 *
 * @author Sergey Petrov
 */

public class EventAddMailFragment extends Fragment {

    private PreferencesHelper mPreferencesHelper = new PreferencesHelper();

    private EditText mSubject;

    private EditText mMessage;

    private EditText mEmail;

    private EditText mEmailPassword;

    private Button mOk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
         * Inflate views from XML.
         */
        View view = inflater.inflate(R.layout.fragment_add_mail_info, container, false);
        mSubject = (EditText) view.findViewById(R.id.setup_subject_line);
        mMessage = (EditText) view.findViewById(R.id.setup_message_line);
        mEmail = (EditText) view.findViewById(R.id.setup_email_line);
        mEmailPassword = (EditText) view.findViewById(R.id.setup_email_password_line);
        mOk = (Button) view.findViewById(R.id.setup_add_mail_info_button_ok);

        mEmail.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean consumed = false;
                if ((keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()==0 || keyCode == KeyEvent.KEYCODE_BACK && event.getAction()==0)
                        && !TextHelper.isValidEmail(mEmail.getText().toString())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("E-mail is not valid");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
//                    consumed = true;
                }
                return consumed;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context appContext = getActivity().getApplicationContext();
        String subjectLine = mPreferencesHelper.getMailSettingsSubject(appContext);
        String messageLine = mPreferencesHelper.getMailSettingsMessage(appContext);
        String emailLine = mPreferencesHelper.getMailSettingsEmail(appContext);
        String passwordLine = mPreferencesHelper.getMailPasswordSettingsEmail(appContext);

        if (TextHelper.isValid(subjectLine)) {
            mSubject.setText(subjectLine);
//            mSubject.setNextFocusDownId(R.id.setup_event_info_line_two);
        }

        if (TextHelper.isValid(messageLine)) {
            mMessage.setText(messageLine);
//            mMessage.setNextFocusDownId(R.id.setup_event_info_line_two);
        }

        if (TextHelper.isValid(emailLine)) {
            mEmail.setText(emailLine);
//            mMessage.setNextFocusDownId(R.id.setup_event_info_line_two);
        }

        if (TextHelper.isValid(passwordLine)) {
            mEmailPassword.setText(passwordLine);
//            mMessage.setNextFocusDownId(R.id.setup_event_info_line_two);
        }

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onPause() {
        Context appContext = getActivity().getApplicationContext();

        // Store subject.
        String subjectLineString = null;
        Editable subjectLine = mSubject.getText();
        if (subjectLine != null && subjectLine.length() > 0) {
            subjectLineString = subjectLine.toString();
        }
        mPreferencesHelper.storeMailSettingsSubject(appContext, subjectLineString);

        // Store message.
        String messageLineString = null;
        Editable messageLine = mMessage.getText();
        if (messageLine != null && messageLine.length() > 0) {
            messageLineString = messageLine.toString();
        }
        mPreferencesHelper.storeMailSettingsMessage(appContext, messageLineString);

        // Store email.
        String emailLineString = null;
        Editable emailLine = mEmail.getText();
        if (emailLine != null && emailLine.length() > 0 && TextHelper.isValidEmail(emailLine.toString())) {
            emailLineString = emailLine.toString();
        }
        mPreferencesHelper.storeMailSettingsEmail(appContext, emailLineString);

        // Store email password.
        String passwordLineString = null;
        Editable passwordLine = mEmailPassword.getText();
        if (passwordLine != null && passwordLine.length() > 0) {
            passwordLineString = passwordLine.toString();
        }
        mPreferencesHelper.storeMailPasswordSettingsEmail(appContext, passwordLineString);

        super.onPause();
    }

    /**
     * Creates a new {@link EventAddMailFragment} instance.
     *
     * @return the new {@link EventAddMailFragment} instance.
     */
    public static EventAddMailFragment newInstance() {
        return new EventAddMailFragment();
    }

}
