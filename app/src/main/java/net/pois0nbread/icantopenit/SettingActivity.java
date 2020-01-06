package net.pois0nbread.icantopenit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.InputType;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <pre>
 *     author : Pois0nBread
 *     e-mail : pois0nbreads@gmail.com
 *     time   : 2019/01/06
 *     desc   : AppSettingActivity
 *     version: 1.0
 * </pre>
 */

public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private SharedPreferences mSharedPreferences;

    private Switch settingSwitch1, settingSwitch2, settingSwitch3;

    private FingerprintManager mFingerprintManager;
    private boolean Finger = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSharedPreferences = getSharedPreferences("user_setting", Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= 23) {
            mFingerprintManager = getSystemService(FingerprintManager.class);
            if (!mFingerprintManager.isHardwareDetected()) Finger = false;
            if (!mFingerprintManager.hasEnrolledFingerprints()) Finger = false;
        } else {
            Finger = false;
        }
        bindview();
        setState();
    }

    private void bindview() {
        settingSwitch1 = (Switch) findViewById(R.id.setting_switch1);
        settingSwitch2 = (Switch) findViewById(R.id.setting_switch2);
        settingSwitch3 = (Switch) findViewById(R.id.setting_switch3);

        settingSwitch1.setOnCheckedChangeListener(this);
        settingSwitch2.setOnCheckedChangeListener(this);
        settingSwitch3.setOnCheckedChangeListener(this);
    }

    private void setState(){
        settingSwitch3.setChecked(mSharedPreferences.getBoolean("isHide", false));
        if (mSharedPreferences.getBoolean("pass", false)) {
            settingSwitch1.setChecked(true);
            settingSwitch2.setEnabled(true);
        } else {
            settingSwitch1.setChecked(false);
            settingSwitch2.setEnabled(false);
        }
        if (!Finger) {
            settingSwitch2.setEnabled(false);
            settingSwitch2.setText("你的手机不支持或没有录入指纹 XD");
            return;
        }
        if (mSharedPreferences.getBoolean("finger", false)) {
            settingSwitch2.setChecked(true);
        } else {
            settingSwitch2.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (!compoundButton.isPressed()) return;
        switch (compoundButton.getId()) {
            case R.id.setting_switch1:
                if (b) {
                    final EditText mEditText = new EditText(this);
                    mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    new AlertDialog
                            .Builder(this)
                            .setTitle("设置密码")
                            .setView(mEditText)
                            .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (mEditText.getText().toString().equals("")) {
                                        Toast.makeText(SettingActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mSharedPreferences.edit().putString("pwd", mEditText.getText().toString()).apply();
                                        mSharedPreferences.edit().putBoolean("pass", true).apply();
                                        dialogInterface.dismiss();
                                        setState();
                                    }
                                }
                            })
                            .create()
                            .show();
                } else {
                    mSharedPreferences.edit().putString("pwd", "").apply();
                    mSharedPreferences.edit().putBoolean("pass", false).apply();
                }
                break;
            case R.id.setting_switch2:
                if (b) {
                    final TextView textView = new TextView(SettingActivity.this);
                    textView.setText("请验证你的指纹");
                    final CancellationSignal cancellationSignal = new CancellationSignal();
                    final AlertDialog alertDialog = new AlertDialog
                            .Builder(this)
                            .setTitle("设置指纹")
                            .setView(textView)
                            .setCancelable(false)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    cancellationSignal.cancel();
                                    setState();
                                    return;
                                }
                            })
                            .create();
                    alertDialog.show();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        AuthenticationCallback authenticationCallback = new AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                Toast.makeText(SettingActivity.this, errString, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                                //指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。
                                textView.setText("指纹验证失败，请重试");
                            }

                            @Override
                            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                                //指纹密码验证成功
                                Toast.makeText(SettingActivity.this, "指纹密码验证成功", Toast.LENGTH_SHORT).show();
                                mSharedPreferences.edit().putBoolean("finger", true).apply();
                                setState();
                                alertDialog.dismiss();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                //指纹验证失败，指纹识别失败，可再验，错误原因为：该指纹不是系统录入的指纹。
                                textView.setText("指纹验证失败，请重试");
                            }
                        };
                        mFingerprintManager.authenticate(null, cancellationSignal, 0, authenticationCallback, null);
                    }
                } else {
                    mSharedPreferences.edit().putBoolean("finger", false).apply();
                }
                break;
            case R.id.setting_switch3:
                if (b) {
                    getPackageManager().setComponentEnabledSetting(new ComponentName(SettingActivity.this, "net.pois0nbread.icantopenit.MainAlias"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                } else {
                    getPackageManager().setComponentEnabledSetting(new ComponentName(SettingActivity.this, "net.pois0nbread.icantopenit.MainAlias"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                }
                mSharedPreferences.edit().putBoolean("isHide", b).apply();
                Toast.makeText(SettingActivity.this, "操作成功！", Toast.LENGTH_SHORT).show();
                break;
        }
        setState();
    }
}
