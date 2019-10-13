package ezviz.ezopensdk.ezvizapi;

import android.content.Intent;
import android.widget.Toast;
import com.ezviz.opensdk.auth.EZAuthHandleActivity;
import com.videogo.OptionActivity;

/**
 * TODO description
 *
 * @author dingwei3
 * @date 2018/1/15
 */

public class EzvizEntryActivity extends EZAuthHandleActivity{

    public void onAuthSuccess() {
        Intent toIntent = new Intent(this, OptionActivity.class);
        toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toIntent);
        Toast.makeText(this,"onAuthSuccess",Toast.LENGTH_LONG).show();
    }

    public void onAuthFail(int errorCode) {
        Toast.makeText(this,"Auth fail",Toast.LENGTH_LONG).show();
    }
}
