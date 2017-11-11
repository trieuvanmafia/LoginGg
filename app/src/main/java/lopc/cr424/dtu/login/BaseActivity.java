package lopc.cr424.dtu.login;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Admin on 11/9/2017.
 */

public class BaseActivity extends AppCompatActivity{
    private ProgressDialog mProgressDialog;
    public void showProgressDialog(){
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }
    public void hideProgressDialog(){
        if (mProgressDialog !=null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }
    @Override
    public  void onDestroy(){
        super.onDestroy();
        hideProgressDialog();
    }


}
