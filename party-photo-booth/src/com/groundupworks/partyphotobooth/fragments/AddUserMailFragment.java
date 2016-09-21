package com.groundupworks.partyphotobooth.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.groundupworks.partyphotobooth.R;
import com.groundupworks.partyphotobooth.helpers.PreferencesHelper;

import java.lang.ref.WeakReference;

/**
 * Created by User on 14.09.2016.
 */
public class AddUserMailFragment extends Fragment {

    private Context mContext;

    private WeakReference<AddUserMailFragment.ICallbacks> mCallbacks = null;

    private PreferencesHelper mPreferencesHelper = new PreferencesHelper();


    private EditText etMail;
    private Button btnOK;
    private Button btnCancle;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = new WeakReference<AddUserMailFragment.ICallbacks>((AddUserMailFragment.ICallbacks) activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = container.getContext();

        View view = inflater.inflate(R.layout.add_user_mail_fragment, container, false);
        etMail = (EditText) view.findViewById(R.id.setup_email_line);
        btnOK = (Button) view.findViewById(R.id.add_user_mail_button_ok);
        btnCancle = (Button) view.findViewById(R.id.add_user_mail_button_cancel);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ICallbacks callbacks = getCallbacks();
                if (callbacks != null) {
                    callbacks.userMailAddressAnswer(etMail.getText().toString());
                }
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ICallbacks callbacks = getCallbacks();
                if (callbacks != null) {
                    callbacks.userMailAddressAnswer("cancel");
                }
            }
        });
        etMail.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                return handled;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static AddUserMailFragment newInstance() {
        AddUserMailFragment fragment = new AddUserMailFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private AddUserMailFragment.ICallbacks getCallbacks() {
        AddUserMailFragment.ICallbacks callbacks = null;
        if (mCallbacks != null) {
            callbacks = mCallbacks.get();
        }
        return callbacks;
    }

    public interface ICallbacks {
        public void userMailAddressAnswer(String userAnswer);
    }
}
