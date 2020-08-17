package com.starostinvlad.fan.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.api.retro.NetworkService;
import com.starostinvlad.fan.api.retro.Token;
import com.starostinvlad.fan.utils.SharedPref;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.starostinvlad.fan.utils.Utils.DOMAIN;


public class VKAuthorizationFragment extends Fragment {

    WebView vkAuthView;
    String cookie;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vk_auth_fragment, container, false);

        progressBar = view.findViewById(R.id.auth_progress);

        progressBar.setVisibility(View.VISIBLE);

        vkAuthView = view.findViewById(R.id.vk_auth_form);
//        String url = "https://oauth.vk.com/authorize?client_id=6031373&scope=offline,email&redirect_uri=http://oauth.vk.com/blank.html&display=touch&response_type=code";
//        String url = "http://oauth.vk.com/authorize?client_id=6031373&redirect_uri=" + RemoteConfig.read(SharedPref.DOMAIN).replace("://", "%3A%2F%2F") + "%2Flogin%2F%3Fprovider%3Dvk&scope=offline%2Cwall%2Cemail&response_type=code";
        String url = "http://oauth.vk.com/authorize?client_id=6031373&redirect_uri=" + DOMAIN.replace("://", "%3A%2F%2F") + "%2Flogin%2F%3Fprovider%3Dvk&scope=offline%2Cwall%2Cemail&response_type=code";
        Log.d("Auth", "domain: " + url);
        vkAuthView.loadUrl(url);
        vkAuthView.getSettings().setJavaScriptEnabled(true);
        vkAuthView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final WebResourceRequest request) {
                String headers = "";
                headers = request.getUrl().toString();

//                Log.d("Auth", "cookie: " + cookie + "  ///  " + headers);

                if (headers.contains("profile")) {
                    cookie = CookieManager.getInstance().getCookie(String.valueOf(request.getUrl()));
                    view.loadUrl("https://oauth.vk.com/authorize?client_id=6031373&scope=offline,email&redirect_uri=http://oauth.vk.com/blank.html&display=touch&response_type=code");
                } else if (headers.contains("#code")) {
                    final String data = headers.split("=")[1];
                    NetworkService.getInstance()
                            .getSerials()
                            .getToken(data)
                            .enqueue(new Callback<Token>() {
                                @Override
                                public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                                    Token token = response.body();
                                    assert token != null;
//                                    Log.d("retrofit", "token: " + Utils.token);
                                    SharedPref.write(SharedPref.TOKEN, token.getToken());
                                }

                                @Override
                                public void onFailure(Call<Token> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });

                    vkAuthView.clearCache(true);
//                    Utils.setCookie(cookie);
                    vkAuthView.destroy();


                    SharedPref.write(SharedPref.COOKIE, cookie);
                    SharedPref.write(SharedPref.AUTH, true);

                    Bundle args = new Bundle();
                    args.putBoolean("PROFILE", true);
                    MainFragment bFragment = new MainFragment();
                    bFragment.setArguments(args);

                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();

                    FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    fragmentManager.popBackStack();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_act_id, bFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } else {
                    view.loadUrl(String.valueOf(request.getUrl()));
                }
                progressBar.setVisibility(View.GONE);
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        return view;
    }

}
