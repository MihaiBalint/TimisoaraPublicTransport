package ro.mihai.tpt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import ro.mihai.tpt.utils.CityActivity;
import ro.mihai.tpt.utils.CityNotLoadedException;

/**
 * Created by Mihai Balint on 8/19/16.
 */
public class MessageList extends CityActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected final void onCreateCityActivity(Bundle savedInstanceState) throws CityNotLoadedException {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_list);

        // Assign fields
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

        // Set click listeners
        mSignInButton.setOnClickListener(new OnSignIn());

        mFirebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnSignIn());
            } else {
                // Google Sign In failed
                Log.e("MessageList", "Google Sign In failed.");
            }
        }
    }

    private class OnSignIn implements View.OnClickListener, OnCompleteListener<AuthResult> {

        public void onClick(View v) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        public void onComplete(@NonNull Task<AuthResult> task) {
            // If sign in fails, display a message to the user.
            if (!task.isSuccessful()) {
                Toast.makeText(MessageList.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(MessageList.this, MessageList.class));
                finish();
            }
        }
    }
}
