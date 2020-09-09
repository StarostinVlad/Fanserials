package com.starostinvlad.fan.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.api.retro.NetworkService;
import com.starostinvlad.fan.api.retro.Token;
import com.starostinvlad.fan.utils.SharedPref;
import com.starostinvlad.fan.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang.StringUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationFragment extends Fragment {

    EditText email;
    EditText pass;
    Button vkAuthBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_fragment, container, false);
        vkAuthBtn = view.findViewById(R.id.vk_auth_btn);
        email = view.findViewById(R.id.email_field);
        pass = view.findViewById(R.id.pass_field);
        vkAuthBtn.setOnClickListener(view12 -> {
            VKAuthorizationFragment vkAuthFragment = new VKAuthorizationFragment();
            getFragmentManager()
                    .beginTransaction().addToBackStack(null)
                    .replace(R.id.main_act_id, vkAuthFragment).commit();
        });

        Button login = view.findViewById(R.id.login_btn);
        login.setOnClickListener(view1 -> {
            if (StringUtils.isEmpty(email.getText().toString())) {
                Toast.makeText(getContext(), "Поле email не полжно быть пустым", Toast.LENGTH_LONG).show();
            } else if (StringUtils.isEmpty(pass.getText().toString())) {
                Toast.makeText(getContext(), "Поле пароль не должно быть пустым", Toast.LENGTH_LONG).show();
            } else {

                Utils.login(email.getText().toString(), pass.getText().toString());

                NetworkService.getInstance()
                        .getSerials()
                        .getToken(email.getText().toString(), pass.getText().toString())
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                Log.d("retrofit", "res: " + response);
                                String res = null;
                                try {
                                    if (response.isSuccessful()) {
                                        res = response.body().string(); // do something with that
                                        String cookie = response.headers().get("Set-Cookie");
                                        Log.d("retrofit", "res: " + cookie);
                                        assert res != null;
                                        GsonBuilder builder = new GsonBuilder();
                                        Gson gson = builder.create();
                                        Token token = gson.fromJson(res, Token.class);
                                        Log.d("retrofit", "token: " + Utils.TOKEN);
                                        SharedPref.write(SharedPref.TOKEN, token.getToken());
                                        SharedPref.write(SharedPref.AUTH, true);
                                        Bundle args = new Bundle();
                                        args.putBoolean("PROFILE", true);
                                        MainFragment bFragment = new MainFragment();
                                        bFragment.setArguments(args);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.main_act_id, bFragment)
                                                .commit();
                                    } else {
                                        res = response.errorBody().string(); // do something with that
                                        if (res.contains("Bad password")) {
                                            Toast.makeText(getContext(), "Неверный пароль!", Toast.LENGTH_LONG).show();
                                        }else if(res.contains("User not found")){
                                            Toast.makeText(getContext(), "Пользователь не найден!", Toast.LENGTH_LONG).show();
                                        }
                                        else if(res.contains("Bad email format")){
                                            Toast.makeText(getContext(), "Неправильный формат email!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
            }
        });

        return view;
    }
}
