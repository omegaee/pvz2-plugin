package cn.omegaee.pvz2plugin;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    protected TextView tip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        initView();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new ConfigModule())
                .commit();

    }

    private void initView(){
        tip = findViewById(R.id.tip);
        if(tip != null){
            String html = "本软件完全免费 --- " +
                    "<a href=\"https://github.com/omegaee/pvz2-plugin\">官网 - github</a>";
            Spanned spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
            tip.setMovementMethod(LinkMovementMethod.getInstance());
            tip.setText(spanned);
        }

    }
}
