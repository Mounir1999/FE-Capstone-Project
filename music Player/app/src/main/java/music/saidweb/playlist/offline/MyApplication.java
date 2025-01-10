package music.saidweb.playlist.offline;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.MobileAds;
import org.json.JSONException;
import org.json.JSONObject;

public class MyApplication extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        MobileAds.initialize(this, initializationStatus -> {

        });
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener()
        {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
            }
        } );
        getData();
        AppOpenManager appOpenManager = new AppOpenManager(this);

    }

    public void getData() {
        Volley.newRequestQueue(this).add(new StringRequest(0, Constants.jsonUrl, new Response.Listener<String>() {
            public void onResponse(String str) {
                try {
                    JSONObject jSONObjectRoot = new JSONObject(str);
                    JSONObject jsonAds = jSONObjectRoot.getJSONObject("Ads");
                    Constants.ad_banner = jsonAds.getString("ad_banner");
                    Constants.ad_inter = jsonAds.getString("ad_inter");
                    Constants.ad_native = jsonAds.getString("ad_native");
                    Constants.back_up = jsonAds.getString("back_up");
                    Constants.ad_status = jsonAds.getString("ad_status");
                    Constants.app_open = jsonAds.getString("openapp");
                    Constants.ad_interval = jsonAds.getInt("ad_interval");
                    //----------------------- AdMob -----------------------//
                    JSONObject jsonAdMob = jsonAds.getJSONObject("AdMob");
                    Constants.adMobBannerId = jsonAdMob.getString("banner");
                    Constants.adMobInterstitialId = jsonAdMob.getString("interstitial");
                    Constants.adMobNativeId = jsonAdMob.getString("native");
                    Constants.admobAppOpen = jsonAdMob.getString("appopen");
                    //----------------------- AdMob -----------------------//

                    //----------------------- Google Ads Manager -----------------------//
                    JSONObject jsonGamanager = jsonAds.getJSONObject("GAM");
                    Constants.gAdMangerBannerId = jsonGamanager.getString("banner");
                    Constants.gAdMangerInterstitialId = jsonGamanager.getString("interstitial");
                    Constants.gAdMangerNativeId = jsonGamanager.getString("native");
                    Constants.gOpenApp = jsonGamanager.getString("appopen");
                    //----------------------- Google Ads Manager -----------------------//

                    //----------------------- FAN -----------------------//
                    JSONObject jsonFan = jsonAds.getJSONObject("FAN");
                    Constants.fanBannerId = jsonFan.getString("banner");
                    Constants.fanInterstitialId = jsonFan.getString("interstitial");
                    Constants.fanNativeId = jsonFan.getString("native");
                    //----------------------- FAN -----------------------//

                    //----------------------- ironSource -----------------------//
                    JSONObject jsonIron = jsonAds.getJSONObject("ironSource");
                    Constants.ironAppKey = jsonIron.getString("app_key");
                    //----------------------- ironSource -----------------------//

                    //----------------------- MAX -----------------------//
                    JSONObject jsonMax = jsonAds.getJSONObject("MAX");
                    Constants.maxBannerId = jsonMax.getString("banner");
                    Constants.maxInterstitialId = jsonMax.getString("interstitial");
                    Constants.maxNativeId = jsonMax.getString("native");
                    Constants.maxAppOpen = jsonMax.getString("appopen");
                    //----------------------- MAX -----------------------//

                    //----------------------- Applovin_Discovery -----------------------//
                    JSONObject jsonApplovin = jsonAds.getJSONObject("Applovin");
                    Constants.Applovinbanner = jsonApplovin.getString("banner");
                    Constants.ApplovinInter = jsonApplovin.getString("interstitial");
                    //----------------------- StartApp -----------------------//

                    //----------------------- StartApp -----------------------//
                    JSONObject jsonStartApp = jsonAds.getJSONObject("StartApp");
                    Constants.StartAppId = jsonStartApp.getString("app_id");
                    //----------------------- StartApp -----------------------//
                    Constants.cnx = 1;
                } catch (JSONException e) {
                    Log.e("Catch", e.toString());
                    Constants.cnx = 2;
                }

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Constants.cnx = 2;
            }
        }));
    }
}



