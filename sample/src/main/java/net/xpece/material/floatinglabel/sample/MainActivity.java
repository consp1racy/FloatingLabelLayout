package net.xpece.material.floatinglabel.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;

import net.xpece.material.floatinglabel.FloatingHelperView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Eugen on 18. 3. 2015.
 */
public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.et_password)
    EditText mPassword;

    @InjectView(R.id.helper_password)
    FloatingHelperView mHelperPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.check_password)
    public void onCheckPassword() {
        mHelperPassword.showError();
    }
}
