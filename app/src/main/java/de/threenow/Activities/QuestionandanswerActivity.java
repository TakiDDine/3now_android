package de.threenow.Activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.threenow.Adapters.qandaAdapter;
import de.threenow.Helper.LocaleManager;
import de.threenow.Helper.SharedHelper;
import de.threenow.R;


public class QuestionandanswerActivity extends AppCompatActivity {
    ExpandableListView expandableListView;
    List<String> listqa;
    HashMap<String, List<String>> listItem;
    qandaAdapter adapter;

    @Override
    protected void attachBaseContext(Context base) {
        if (SharedHelper.getKey(base, "lang") != null)
            super.attachBaseContext(LocaleManager.setNewLocale(base, SharedHelper.getKey(base, "lang")));
        else
            super.attachBaseContext(LocaleManager.setNewLocale(base, "de"));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionandanswer);

        setTitle(getString(R.string.questions_and_answers));

        expandableListView = findViewById(R.id.questionandanswer_Listview);
        listqa = new ArrayList<>();
        listItem = new HashMap<>();
        adapter = new qandaAdapter(this, listqa, listItem);
        expandableListView.setAdapter(adapter);
        initListData();
    }

    private void initListData() {
        listqa.add(getString(R.string.question1));
        listqa.add(getString(R.string.question2));
        listqa.add(getString(R.string.question3));
        listqa.add(getString(R.string.question4));
        listqa.add(getString(R.string.question5));
        listqa.add(getString(R.string.question6));
        listqa.add(getString(R.string.question7));
        listqa.add(getString(R.string.question8));
        listqa.add(getString(R.string.question9));
        listqa.add(getString(R.string.question10));
        listqa.add(getString(R.string.question11));
        listqa.add(getString(R.string.question12));
        listqa.add(getString(R.string.question13));
        listqa.add(getString(R.string.question14));
        listqa.add(getString(R.string.question15));
        listqa.add(getString(R.string.question16));
        listqa.add(getString(R.string.question17));

        String[] array;

        List<String> list1 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question1);
        for (String item : array) {
            list1.add(item);
        }

        List<String> list2 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question2);
        for (String item : array) {
            list2.add(item);
        }

        List<String> list3 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question3);
        for (String item : array) {
            list3.add(item);
        }

        List<String> list4 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question4);
        for (String item : array) {
            list4.add(item);
        }

        List<String> list5 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question5);
        for (String item : array) {
            list5.add(item);
        }

        List<String> list6 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question6);
        for (String item : array) {
            list6.add(item);
        }

        List<String> list7 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question7);
        for (String item : array) {
            list7.add(item);
        }

        List<String> list8 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question8);
        for (String item : array) {
            list8.add(item);
        }

        List<String> list9 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question9);
        for (String item : array) {
            list9.add(item);
        }

        List<String> list10 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question10);
        for (String item : array) {
            list10.add(item);
        }

        List<String> list11 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question11);
        for (String item : array) {
            list11.add(item);
        }

        List<String> list12 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question12);
        for (String item : array) {
            list12.add(item);
        }

        List<String> list13 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question13);
        for (String item : array) {
            list13.add(item);
        }

        List<String> list14 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question14);
        for (String item : array) {
            list14.add(item);
        }

        List<String> list15 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question15);
        for (String item : array) {
            list15.add(item);
        }

        List<String> list16 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question16);
        for (String item : array) {
            list16.add(item);
        }

        List<String> list17 = new ArrayList<>();
        array = getResources().getStringArray(R.array.question17);
        for (String item : array) {
            list17.add(item);
        }

        listItem.put(listqa.get(0), list1);
        listItem.put(listqa.get(1), list2);
        listItem.put(listqa.get(2), list3);
        listItem.put(listqa.get(3), list4);
        listItem.put(listqa.get(4), list5);
        listItem.put(listqa.get(5), list6);
        listItem.put(listqa.get(6), list7);
        listItem.put(listqa.get(7), list8);
        listItem.put(listqa.get(8), list9);
        listItem.put(listqa.get(9), list10);
        listItem.put(listqa.get(10), list11);
        listItem.put(listqa.get(11), list12);
        listItem.put(listqa.get(12), list13);
        listItem.put(listqa.get(13), list14);
        listItem.put(listqa.get(14), list15);
        listItem.put(listqa.get(15), list16);
        listItem.put(listqa.get(16), list17);

        adapter.notifyDataSetChanged();
    }
}
