package net.xpece.material.floatinglabel.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import net.xpece.material.floatinglabel.FloatingHelperView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Eugen on 18. 3. 2015.
 */
public class MainActivity extends ActionBarActivity {
    private static final String[] DATA_SET = {
        "January", "Feburuary", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    @InjectView(R.id.et_password)
    EditText mPassword;

    @InjectView(R.id.helper_password)
    FloatingHelperView mHelperPassword;

    @InjectView(R.id.sp_month)
    Spinner mSpinnerMonth;

    @InjectView(R.id.helper_month)
    FloatingHelperView mHelperMonth;

    private ArrayAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DATA_SET){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setPadding(0,0,0,0);
                return v;
            }
        };
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerMonth.setAdapter(mAdapter);
        mSpinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHelperMonth.showDefault();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mHelperMonth.showDefault();
            }
        });
    }

    @OnClick(R.id.check_password)
    public void onCheckPassword() {
        mHelperPassword.showError();
    }

    @OnClick(R.id.check_month)
    public void onCheckMonth() {
        mHelperMonth.showError();
    }

}
