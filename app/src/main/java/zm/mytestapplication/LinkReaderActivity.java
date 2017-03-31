package zm.mytestapplication;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.hp.linkreadersdk.EasyReadingCallback;
import com.hp.linkreadersdk.EasyReadingFragment;
import com.hp.linkreadersdk.ErrorCode;

public class LinkReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_reader);

        FrameLayout linearLayout = (FrameLayout) findViewById(R.id.linkreader_view);
        if (savedInstanceState == null) {
            EasyReadingFragment easyReadingFragment = EasyReadingFragment.initWithClientID(
                    "d854dx4nmibruwj5r6ityigflc2el136",  // <Client ID>
                    "bHzJK42LtXKh6I1PRM4ugneaIj2QJGgU",  // <Client Secret>
                    new EasyReadingCallback() {
                @Override
                public void onAuthenticationSuccess() {
                    Log.d("Auth", "Authenticated");
                }

                @Override
                public void onAuthenticationError(ErrorCode errorCode) {
                    Log.d("Auth", "ErrorCode " + errorCode.name());
                }
            }, this);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(linearLayout.getId(), easyReadingFragment).commit();
        }
    }
}
