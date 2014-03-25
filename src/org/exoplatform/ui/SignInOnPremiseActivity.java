package org.exoplatform.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.exoplatform.R;
import org.exoplatform.ui.login.LoginProxy;
import org.exoplatform.utils.*;

public class SignInOnPremiseActivity extends Activity implements LoginProxy.ProxyListener {

  private Button   mLoginBtn;

  private EditText mUrlTxt;

  private EditText mUserTxt;

  private EditText mPassTxt;

  private TextView mAlertTxt;

  private LoginProxy mLoginProxy;

  private static final String TAG = "eXo____SignInOnPremiseActivity____";


  public void onCreate(Bundle savedInstanceState) {
    if (!WelcomeActivity.mIsTablet) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    super.onCreate(savedInstanceState);

    setContentView(R.layout.onpremise);

    mLoginBtn = (Button) findViewById(R.id.onpremise_login_btn);
    mLoginBtn.setEnabled(false);

    mUrlTxt   = (EditText) findViewById(R.id.onpremise_url_edit_txt);
    mUserTxt  = (EditText) findViewById(R.id.onpremise_user_edit_txt);
    mPassTxt  = (EditText) findViewById(R.id.onpremise_pass_edit_txt);

    mUrlTxt.addTextChangedListener(onAnyInputChanged());
    mUserTxt.addTextChangedListener(onAnyInputChanged());
    mPassTxt.addTextChangedListener(onAnyInputChanged());

    AssetUtils.setTypeFace(
        AssetUtils.getCustomTypeface(AssetUtils.ROBOTO_REGULAR), mUrlTxt);
    AssetUtils.setTypeFace(
        AssetUtils.getCustomTypeface(AssetUtils.ROBOTO_REGULAR), mUserTxt);
    AssetUtils.setTypeFace(
        AssetUtils.getCustomTypeface(AssetUtils.ROBOTO_REGULAR), mPassTxt);
  }

  @Override
  protected void onResume() {
    super.onResume();
    SettingUtils.setDefaultLanguage(this);
    onChangeLanguage();
  }

  private void onChangeLanguage() {
    Resources resources = getResources();
    ((TextView) findViewById(R.id.onpremise_title_txt)).setText(resources.getString(R.string.OnPremise));
    ((TextView) findViewById(R.id.onpremise_url_txt)).setText(resources.getString(R.string.EnterUrl));
    ((TextView) findViewById(R.id.onpremise_login_txt)).setText(resources.getString(R.string.EnterLogin));
    ((EditText) findViewById(R.id.onpremise_url_edit_txt)).setHint(resources.getString(R.string.YourIntranetHint));
    ((EditText) findViewById(R.id.onpremise_user_edit_txt)).setHint(resources.getString(R.string.UserNameHint));
    ((EditText) findViewById(R.id.onpremise_pass_edit_txt)).setHint(resources.getString(R.string.PasswordHint));
    ((Button)   findViewById(R.id.onpremise_login_btn)).setText(resources.getString(R.string.LogIn));
  }

  public View.OnClickListener onClickLogIn() {

    return new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String url      = mUrlTxt.getText().toString();
        String user     = mUserTxt.getText().toString();
        String pass     = mPassTxt.getText().toString();

        if (!url.startsWith(ExoConnectionUtils.HTTP)) url = ExoConnectionUtils.HTTP + url;

        /** check url */
        if (!ExoConnectionUtils.validateUrl(url) || ExoConnectionUtils.urlHasWrongTenant(url)) {
          InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
          if (inputMethodManager!= null) inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

          Toast.makeText(SignInOnPremiseActivity.this, R.string.ServerInvalid, Toast.LENGTH_SHORT).show();
          return ;
        }

        makeRequestSigningIn(url, user, pass);
      }
    };
  }


  private void makeRequestSigningIn(String url, String user, String pass) {
    Log.d(TAG, "sign in: " + user + " - url: " + url);

    Bundle loginData = new Bundle();
    loginData.putString(LoginProxy.USERNAME, user);
    loginData.putString(LoginProxy.PASSWORD, pass);
    loginData.putString(LoginProxy.DOMAIN,   url);

    mLoginProxy = new LoginProxy(this, LoginProxy.WITH_USERNAME, loginData);
    mLoginProxy.setListener(this);
    mLoginProxy.performLogin();
  }

  @Override
  public void onLoginFinished(boolean result) {
    if (!result) return ;
    Intent next = new Intent(this, HomeActivity.class);
    //next.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(next);
  }

  private TextWatcher onAnyInputChanged() {
    return new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        String url   = mUrlTxt.getText().toString();
        String user  = mUserTxt.getText().toString();
        String pass  = mPassTxt.getText().toString();

        /** check password and email is inputted */
        if (url.isEmpty() || user.isEmpty() || pass.isEmpty()) {
          mLoginBtn.setEnabled(false);
          return ;
        }

        mLoginBtn.setEnabled(true);
        mLoginBtn.setOnClickListener(onClickLogIn());
      }

      @Override
      public void afterTextChanged(Editable editable) { }
    };
  }
}