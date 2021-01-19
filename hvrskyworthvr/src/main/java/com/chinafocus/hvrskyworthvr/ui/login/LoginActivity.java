package com.chinafocus.hvrskyworthvr.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.blankj.utilcode.util.BarUtils;
import com.chinafocus.hvrskyworthvr.R;
import com.chinafocus.hvrskyworthvr.ui.main.MainActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText etAccount;
    private AppCompatEditText etConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        BarUtils.setStatusBarLightMode(this, true);

        etAccount = findViewById(R.id.et_account_first);
        etConfirm = findViewById(R.id.et_account_confirm);
        findViewById(R.id.bt_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        login();
    }

    private void login() {
        Editable account = etAccount.getText();
        Editable confirm = etConfirm.getText();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, R.string.login_account_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.equals(account, confirm)) {
            saveAccount(Objects.requireNonNull(account).toString());
            startMainActivity();
        } else {
            Toast.makeText(this, R.string.login_account_must_equals, Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void saveAccount(String s) {
//        SPUtils.getInstance().put(Constants.ACCOUNT_NAME, s);
    }
}