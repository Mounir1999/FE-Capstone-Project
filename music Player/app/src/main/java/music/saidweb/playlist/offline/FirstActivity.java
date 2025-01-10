package music.saidweb.playlist.offline;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.applovin.mediation.ads.MaxAppOpenAd;


public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_more_apps,btn_rate,btn_privacy,btn_skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //ADS
        AdsManager.getInstance().init(this, Constants.ad_banner);
        AdsManager.getInstance().init(this, Constants.ad_inter);
        AdsManager.getInstance().init(this, Constants.ad_native);
        AdsManager.getInstance().loadNativeAd(this, Constants.ad_native);
        AdsManager.getInstance().loadInterstitialAd(this, Constants.ad_inter);

        btn_more_apps =findViewById(R.id.btn_more_apps);
        btn_rate =findViewById(R.id.btn_rate);
        btn_privacy =findViewById(R.id.btn_privacy);
        btn_skip =findViewById(R.id.btn_skip);


        btn_more_apps.setOnClickListener(this);
        btn_rate.setOnClickListener(this);
        btn_privacy.setOnClickListener(this);
        btn_skip.setOnClickListener(this);

        if (Constants.cnx == 1){
            AppOpenManager.appOpenAd = new MaxAppOpenAd(Constants.app_open, MyApplication.context);
            AppOpenManager.appOpenAd.setListener(AppOpenManager.maxthis);
            AppOpenManager.appOpenAd.loadAd();
        }
        
    }






    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.btn_more_apps:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Your_developer_name")));
                break;

            case R.id.btn_rate:
                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=music.saidweb.playlist.offline")));
                break;

            case R.id.btn_privacy:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://saidplus-app.blogspot.com/p/privacy-policy.html")));
                break;

            case R.id.btn_skip:
                startActivity(new Intent(FirstActivity.this,MainActivity.class));
                AdsManager.getInstance().showInterstitialAd();
                finish();
                break;


        }

    }

}

