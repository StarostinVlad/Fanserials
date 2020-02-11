package com.example.fan.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.fan.R;
import com.example.fan.utils.Utils;

import org.apache.commons.lang.StringUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static android.content.Context.MODE_PRIVATE;


public class AuthorizationFragment extends Fragment {

    WebView vkAuthView;
    String cookie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plus_one, container, false);
        vkAuthView = view.findViewById(R.id.vk_auth_form);
        Utils utils = new Utils();
        String domain = utils.getDomainFromPreference(getContext());
        vkAuthView.loadUrl("http://oauth.vk.com/authorize?client_id=6031373&redirect_uri=" + domain + "%2Flogin%2F%3Fprovider%3Dvk&scope=offline%2Cwall%2Cemail&response_type=code");
        vkAuthView.getSettings().setJavaScriptEnabled(true);
        vkAuthView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                cookie = CookieManager.getInstance().getCookie(String.valueOf(request.getUrl()));
                Log.d("Auth", "cookie: " + cookie);
                if (StringUtils.contains(String.valueOf(request.getUrl()), "profile")) {

                    vkAuthView.clearCache(true);
//                    Utils.setCookie(cookie);
                    vkAuthView.destroy();


                    SharedPreferences sPref = getContext().getSharedPreferences("URL", MODE_PRIVATE);
                    sPref.edit().putString("Cookie", cookie).apply();
                    sPref.edit().putBoolean("Auth", true).apply();

                    Bundle args = new Bundle();
                    args.putString("Href", "http://fanserials.network/profile/");
                    args.putString("Title", "Лента");
                    MainFragment bFragment = new MainFragment();
                    bFragment.setArguments(args);

                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_act_id, bFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } else {
                    view.loadUrl(String.valueOf(request.getUrl()));
                }

                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        return view;
    }

}
