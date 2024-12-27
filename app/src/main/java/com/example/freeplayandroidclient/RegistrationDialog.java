package com.example.freeplayandroidclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class RegistrationDialog extends DialogFragment implements View.OnClickListener {
    private Button cancelButton, signupButton;
    private EditText userName, userEmail, userPassword;
    @Override
    public void onClick(View view) {
        if (view.getId() == cancelButton.getId()) {
            listener.onDialogNegativeClick(this);
        } else if (view.getId() == signupButton.getId()) {
            listener.onDialogPositiveClick(this);
        }
    }

    public interface RegistrationDialogListener {
        public void onDialogPositiveClick(RegistrationDialog dialog);
        public void onDialogNegativeClick(RegistrationDialog dialog);
    }
    RegistrationDialogListener listener;


    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (RegistrationDialogListener) context;
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.registration, null);

        cancelButton = (Button) view.findViewById(R.id.cancel);
        signupButton = (Button) view.findViewById(R.id.signup);
        userName = (EditText) view.findViewById(R.id.userName);
        userEmail = (EditText) view.findViewById(R.id.userEmail);
        userPassword = (EditText) view.findViewById(R.id.userPassword);

        cancelButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
        userName.setOnClickListener(this);
        userEmail.setOnClickListener(this);
        userPassword.setOnClickListener(this);

        builder.setView(view);

        return builder.create();
    }

    public String getUserName() {
        return userName.getText().toString();
    }
    public String getUserEmail() {
        return userEmail.getText().toString();
    }
    public String getUserPassword() {
        return userPassword.getText().toString();
    }

}
