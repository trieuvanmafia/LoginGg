package lopc.cr424.dtu.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Admin on 11/11/2017.
 */

public class Login extends Activity{
    Button btnLogin;
    EditText user,password;
    TextView SignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlogin);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,DangKi.class);
                startActivity(intent);
            }
        });

    }
    private void Anhxa(){
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        user = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        SignUp = (TextView) findViewById(R.id.txtSignUp);
    }
}

