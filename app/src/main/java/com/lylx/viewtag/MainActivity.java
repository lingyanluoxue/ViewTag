package com.lylx.viewtag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lylx.viewtag_annotation.BindView;
import com.lylx.viewtag_annotation.OnClick;
import com.lylx.viewtag_api.ViewTag;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.text_view1)
    public TextView textView1;
    @BindView(R.id.text_view2)
    public TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewTag.inject(this);
        textView1.setText("text1");
        textView2.setText("text2");
    }

    @OnClick({R.id.text_view1, R.id.text_view2})
    public void onViewClick(View view) {
        switch (view.getId()){
            case R.id.text_view1:
                Toast.makeText(this, "click_textView1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.text_view2:
                Toast.makeText(this, "click_textView2", Toast.LENGTH_SHORT).show();
                break;
        }

    }

}
