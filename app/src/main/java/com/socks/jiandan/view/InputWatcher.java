package com.socks.jiandan.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.afollestad.materialdialogs.internal.MDButton;
import com.socks.jiandan.utils.TextUtil;

/**
 * Created by zhaokaiqiang on 15/12/24.
 */
public class InputWatcher implements TextWatcher {

    private EditText editName;
    private EditText editEmail;
    MDButton buttonAction;

    public InputWatcher(EditText name, EditText email, MDButton button) {
        editName = name;
        editEmail = email;
        buttonAction = button;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        buttonAction.setEnabled(TextUtil.isEmail(editEmail.getText().toString().trim()
        ) && !TextUtil.isNull(editName.getText().toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}