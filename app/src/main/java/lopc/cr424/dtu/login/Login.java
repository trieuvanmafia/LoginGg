package lopc.cr424.dtu.login;
import java.lang.*;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.concurrent.Executor;

public class Login extends BaseActivity implements
    GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener{

    private static final String TAG ="Google Activity";
    private static final int RC_SIGN_IN =9001;

    //Start declare auth
    private FirebaseAuth mAuth;
    //End declare_auth

    //Start declare auth listener
    private  FirebaseAuth.AuthStateListener mAuthListener;
    //End declare auth listener
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    Button BtnMain;
    EditText user,password;
    TextView signup,forgotpass;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //views
        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        //config signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity*/, this/*On ConnetionFailedListener*/)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //initialize_auth
        mAuth = FirebaseAuth.getInstance();

        //auth state listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged:sign_out");
                }
                //Start Exclude
                updateUI(user);
            }
        };

        @Override
        public void onStart(){
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        public void onStop(){
            super.onStop();
            if (mAuthListener != null){
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }
        //Start onactivityresult
        @Override
        public void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if(requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    //Google Sign In was successful, authenticate with firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    //Google Sign in fail, update UI appropriately
                    //START EXCLUDE
                    updateUI(null);
                    //END EXCLUDE
                }
            }
        };
        //auth with google
        private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
            Log.d(TAG,"firebaseAuthWithGoogle: "+ acct.getId());
            //Star_exclude silent
            showProgressDialog();
            //End
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
            mAuth.signInWithCredential(credential).addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG,"signInWithCredential:onComplete"+task.isSuccessful());
                            if(!task.isSuccessful()){
                                Log.v(TAG,"signInWithCredential",task.getException());
                                Toast.makeText(Login.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                            }
                            //Start exclude
                            hideProgressDialog();
                            //End
                        }

                    });

        }

        //Signin
        private void signIn(){
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent,RC_SIGN_IN);
        }
        private void signOut(){
            mAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            updateUI(null);
                        }
                    }
            );
        }
        private void revokeAccess(){
            mAuth.signOut();
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            updateUI(null);
                        }
                    }
            );
        }
        private void updateUI (FirebaseUser user){
            if (user != null){
                mStatusTextView.setText("Google User : {user.getEmail()}");
                mDetailTextView.setText("Firebase User : {user.getUid()}");
            } else {
                mStatusTextView.setText("Sign Out");
                mDetailTextView.setText(null);
                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            }

        }
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
            Log.d(TAG,"onConnectionFailed ",connectionResult);
            Toast.makeText(this,"Google Play Services error",Toast.LENGTH_SHORT).show();
        }

        @Override
                public void onClick(View v){
            switch (v.getId()){
                case R.id.button:
                    signIn();
                    break;
                case R.id.sign_out_button:
                    signOut();
                    break;
                case R.id.disconnect_button:
                    revokeAccess();
                    break;
            }
        }

//        signup.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent chuyen = new Intent(Login.this, DangKi.class);
//            startActivity(chuyen);
//        }
//    });

    }
