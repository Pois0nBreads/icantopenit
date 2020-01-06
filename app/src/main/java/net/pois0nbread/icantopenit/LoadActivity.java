package net.pois0nbread.icantopenit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**
 * <pre>
 *     author : Pois0nBread
 *     e-mail : pois0nbreads@gmail.com
 *     time   : 2019/01/06
 *     desc   : AppLoadActivity
 *     version: 1.0
 * </pre>
 */

public class LoadActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    final private CancellationSignal cancellationSignal = new CancellationSignal();

    private FingerprintManager mFingerprintManager;

    private TextView mTextView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        mSharedPreferences = getSharedPreferences("user_setting", Context.MODE_PRIVATE);
        if (!mSharedPreferences.getBoolean("pass", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        bindview();

        if (!mSharedPreferences.getBoolean("finger", false)) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            mFingerprintManager = getSystemService(FingerprintManager.class);
            if (!mFingerprintManager.isHardwareDetected() || !mFingerprintManager.hasEnrolledFingerprints()) {
                mTextView.setText("你的手机不支持或没有录入指纹 XD");
            }

            mTextView.setText("请验证你的指纹");

            FingerprintManager.AuthenticationCallback authenticationCallback = new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    mTextView.setText(errString);
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    //指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。
                    mTextView.setText("指纹验证失败，请重试");
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    //指纹密码验证成功
                    Toast.makeText(LoadActivity.this, "指纹密码验证成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoadActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onAuthenticationFailed() {
                    //指纹验证失败，指纹识别失败，可再验，错误原因为：该指纹不是系统录入的指纹。
                    mTextView.setText("指纹验证失败，请重试");
                }
            };
            mFingerprintManager.authenticate(null, cancellationSignal, 0, authenticationCallback, null);
        } else {
            mTextView.setText("你的手机不支持或没有录入指纹 XD");
        }
    }

    private void bindview() {
        mTextView = (TextView) findViewById(R.id.load_textView);
        mEditText = (EditText) findViewById(R.id.load_editText);
        findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditText.getText().toString().equals(mSharedPreferences.getString("pwd", ""))) {
                    startActivity(new Intent(LoadActivity.this, MainActivity.class));
                    cancellationSignal.cancel();
                    finish();
                } else {
                    Toast.makeText(LoadActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    if (mEditText.getText().toString().equals(mSharedPreferences.getString("pwd", ""))) {
                        startActivity(new Intent(LoadActivity.this, MainActivity.class));
                        cancellationSignal.cancel();
                        finish();
                    } else {
                        Toast.makeText(LoadActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }
}
