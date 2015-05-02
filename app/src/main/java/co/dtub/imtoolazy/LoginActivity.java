package co.dtub.imtoolazy;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.dtub.imtoolazy.backend.ITLBackend;
import co.dtub.imtoolazy.backend.model.SimpleResponse;

public class LoginActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /* FACEBOOK */
    private CallbackManager callbackManager;

    /* GOOGLE */
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private boolean mIntentInProgress;

    EditText emailField;
    EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Facebook API
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Google API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        // Build UI
        setContentView(R.layout.activity_login);

        // Text Fields
        emailField = (EditText) findViewById(R.id.email);
        passwordField = (EditText) findViewById(R.id.password);

        // Get Buttons
        Button loginButton = (Button) findViewById(R.id.login_button);
        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        LoginButton facebookLogin = (LoginButton) findViewById(R.id.facebook_login);
        SignInButton googleLogin = (SignInButton) findViewById(R.id.google_login);

        // Native Login/Signup Buttons
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNative(v);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpNative(v);
            }
        });

        // Facebook Login Button
        facebookLogin.registerCallback(callbackManager, loginFacebook);

        // Google Login Button
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginGoogle(v);
            }
        });
    }

    /* NATIVE LOGIN AND SIGN UP */

    private void loginNative(View v) {
        final String email = emailField.getText().toString();
        final String password = passwordField.getText().toString();
        BackendApi.sendApiRequest(new BackendApi.Request<SimpleResponse>() {

            @Override
            public SimpleResponse execute(ITLBackend api) throws IOException {
                return api.accountService().loginWithEmail(email, password).execute();
            }

            @Override
            public void onResult(SimpleResponse result) {
                if (result.getSuccess()) {
                    finishLogin(result.getData());
                } else {
                    Toast.makeText(getApplicationContext(), result.getData(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void signUpNative(View v) {

    }

    /* FACEBOOK LOGIN */

    private FacebookCallback<LoginResult> loginFacebook = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            // Get Facebook ID
            final AccessToken token = loginResult.getAccessToken();
            final String facebookId = token.getUserId();

            BackendApi.sendApiRequest(new BackendApi.Request<SimpleResponse>() {
                @Override
                public SimpleResponse execute(ITLBackend api) throws IOException {
                    return api.accountService().loginWithFacebook(facebookId).execute();
                }

                @Override
                public void onResult(SimpleResponse result) {
                    if (result.getSuccess()) {
                        finishLogin(result.getData());
                    } else {
                        GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                String email = null;
                                try {
                                    email = jsonObject.getString("email");
                                } catch (JSONException e) {}

                            }
                        });
                    }
                    // Log out of Facebook
                    LoginManager.getInstance().logOut();
                }
            });
        }
        @Override
        public void onCancel() {}
        @Override
        public void onError(FacebookException e) {
            Toast.makeText(
                    getApplicationContext(),
                    "An unknown error occurred",
                    Toast.LENGTH_SHORT).show();
        }
    };

    /* GOOGLE LOGIN */

    private void loginGoogle(View view) {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;

        // Get Google ID
        final String id = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId();

        BackendApi.sendApiRequest(new BackendApi.Request<SimpleResponse>() {
            @Override
            public SimpleResponse execute(ITLBackend api) throws IOException {
                return api.accountService().loginWithGoogle(id).execute();
            }

            @Override
            public void onResult(SimpleResponse result) {
                if (result.getSuccess()) {
                    finishLogin(result.getData());
                } else {
                    String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                }
                // Log out of Google
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
            }
        });
    }
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            if (mSignInClicked && connectionResult.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void finishLogin(String accountId) {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("accountId", Long.valueOf(accountId));
        editor.apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Facebook Login
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Google Login
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != FragmentActivity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        }
    }
}
