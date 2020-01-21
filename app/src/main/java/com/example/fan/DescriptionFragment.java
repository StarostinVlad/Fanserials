package com.example.fan;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;


public class DescriptionFragment extends Fragment {
    private static final Map<Character, String> letters = new HashMap<Character, String>();

    static {
        letters.put('А', "A");
        letters.put('Б', "B");
        letters.put('В', "V");
        letters.put('Г', "G");
        letters.put('Д', "D");
        letters.put('Е', "E");
        letters.put('Ё', "E");
        letters.put('Ж', "Zh");
        letters.put('З', "Z");
        letters.put('И', "I");
        letters.put('Й', "I");
        letters.put('К', "K");
        letters.put('Л', "L");
        letters.put('М', "M");
        letters.put('Н', "N");
        letters.put('О', "O");
        letters.put('П', "P");
        letters.put('Р', "R");
        letters.put('С', "S");
        letters.put('Т', "T");
        letters.put('У', "U");
        letters.put('Ф', "F");
        letters.put('Х', "Kh");
        letters.put('Ц', "C");
        letters.put('Ч', "Ch");
        letters.put('Ш', "Sh");
        letters.put('Щ', "Sch");
        letters.put('Ъ', "");
        letters.put('Ы', "Y");
        letters.put('Ь', "");
        letters.put('Э', "E");
        letters.put('Ю', "Yu");
        letters.put('Я', "Ya");
        letters.put('а', "a");
        letters.put('б', "b");
        letters.put('в', "v");
        letters.put('г', "g");
        letters.put('д', "d");
        letters.put('е', "e");
        letters.put('ё', "e");
        letters.put('ж', "zh");
        letters.put('з', "z");
        letters.put('и', "i");
        letters.put('й', "i");
        letters.put('к', "k");
        letters.put('л', "l");
        letters.put('м', "m");
        letters.put('н', "n");
        letters.put('о', "o");
        letters.put('п', "p");
        letters.put('р', "r");
        letters.put('с', "s");
        letters.put('т', "t");
        letters.put('у', "u");
        letters.put('ф', "f");
        letters.put('х', "h");
        letters.put('ц', "c");
        letters.put('ч', "ch");
        letters.put('ш', "sh");
        letters.put('щ', "sch");
        letters.put('ъ', "");
        letters.put('ы', "y");
        letters.put('ь', "");
        letters.put('э', "e");
        letters.put('ю', "yu");
        letters.put('я', "ya");
    }

    public ArrayAdapter<String> uu;
    protected String uri;
    TextView text;
    ProgressBar pr;


    Button subscribe;

    Spinner sp;
    String topic = "";
    String title = "";
    private SharedPreferences sPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.d("sa","onAnnoCreateView fragment");
        sPref = getContext().getSharedPreferences("SUBSCRIBES", MODE_PRIVATE);
        View v = inflater.inflate(R.layout.description_fragment, container, false);
        text = (TextView) v.findViewById(R.id.textView3);
        text.setText("Описание текущей серии");
        sp = (Spinner) v.findViewById(R.id.spinner);
        subscribe = v.findViewById(R.id.subscribe_button);
        title = Video.currentSeria.getName();
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

//                Toast.makeText(getContext(),translit(title),
//                        Toast.LENGTH_SHORT).show();
                topic = translit(title + " " + sp.getSelectedItem().toString());
                if (!sPref.getBoolean(topic, true) || !sPref.contains(topic)) {
                    FirebaseMessaging.getInstance()
                            .subscribeToTopic(topic)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Вы подписались на: " +
                                            title + " " + sp.getSelectedItem().toString();
                                    if (!task.isSuccessful()) {
                                        msg = getString(R.string.msg_subscribe_failed);
                                    } else {
                                        subscribe.setText("Отписаться");
                                        sPref.edit().putBoolean(topic, true).apply();
                                    }
                                    Toast.makeText(getContext(), msg,
                                            Toast.LENGTH_SHORT).show();

                                    subscribe.setEnabled(true);
                                }
                            });
                } else {
                    FirebaseMessaging.getInstance()
                            .unsubscribeFromTopic(topic)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Подписка на " +
                                            title + " " + sp.getSelectedItem().toString() + " отменена";
                                    if (!task.isSuccessful()) {
                                        msg = getString(R.string.msg_subscribe_failed);
                                    } else {
                                        subscribe.setText("Подписаться");
                                        sPref.edit().remove(topic).apply();
//                                        sPref.edit().putBoolean(topic, false).apply();
                                    }
                                    Toast.makeText(getContext(), msg,
                                            Toast.LENGTH_SHORT).show();

                                    subscribe.setEnabled(true);
                                }
                            });
                }
            }
        });
        //Log.d("sa","onAnnoCreateView end fragment");
        return v;
    }

    void fill(final boolean rest, final ArrayList<CurrentSeriaInfo> currentSerias, String descript) {

        text.setText(descript);
        ArrayList<String> titleList = new ArrayList<>();
        for (CurrentSeriaInfo item : currentSerias)
            titleList.add(item.Title);
        uu = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, titleList);
        sp.setAdapter(uu);
        //pr.setVisibility(View.INVISIBLE);

        if (!currentSerias.isEmpty())
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View itemSelected, int selectedItemPosition, long selectedId) {
                    uri = currentSerias.get(selectedItemPosition).Url;
                    Log.d("currentSeria", "url:" + uri);

                    topic = translit(title + " " + currentSerias.get(selectedItemPosition).Title);
                    Log.d("subscribe", "topic:" + topic);

                    Video.curUri = uri;
                    VideoFragment.starting = false;
                    VideoFragment.seturi(uri, 0);
                    if (sPref.contains(topic)) {
                        Log.d("subscribe", "on: " + sPref.getBoolean(topic, false));
                        if (sPref.getBoolean(topic, false)) {
                            subscribe.setText("Отписаться");
                        } else {
                            subscribe.setText("Подписаться");
                        }
                    } else {
                        subscribe.setText("Подписаться");
                    }

                }

                public void onNothingSelected(AdapterView<?> parent) {
                    text.setText("nothing");
                }
            });
//            else for (String vid : vidUri) {
//                //Log.d("tmp","vid="+vid );
//            }
    }

    String translit(String input) {
        input = input.replace(" ", "_");
        StringBuilder output = new StringBuilder();
        for (char ch : input.toCharArray()) {
//            Log.d("translit", "char: "+ch+" / "+letters.get(ch));
            if (letters.containsKey(ch))
                output.append(letters.get(ch));
            else
                output.append(ch);
        }
        return output.toString();
    }
   /* public void prVisible(boolean visible){
        if(visible)pr.setVisibility(View.VISIBLE);
        else pr.setVisibility(View.INVISIBLE);
    }*/
}
