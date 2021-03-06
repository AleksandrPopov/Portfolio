package com.alexanderpopov.car.nameplate.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.alexanderpopov.car.nameplate.R;
import com.alexanderpopov.car.nameplate.adapters.MainActivityRecyclerViewAdapter;
import com.alexanderpopov.car.nameplate.resources.ParseColorResource;
import com.alexanderpopov.car.nameplate.adapters.AllColorListRecyclerViewAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AllColorListActivity extends AppCompatActivity {

    private ArrayList<ParseColorResource> mArray;
    private AllColorListRecyclerViewAdapter mAdapter;
    private String mUrl;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_list);

        mArray = new ArrayList<>();

        RecyclerView mRecyclerView = findViewById(R.id.colorListRecyclerView);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new AllColorListRecyclerViewAdapter(this, mArray);
        mRecyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        mUrl = getString(intent.getIntExtra(MainActivityRecyclerViewAdapter.CAR_COLOR_TABLE, 1));

        if(Objects.equals(mUrl, "LAND ROVER")){
            mUrl = "Landrover";
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(intent.getIntExtra((MainActivityRecyclerViewAdapter.CAR_NAME_TEXT), 1));

        allColorListParse();
    }
    public void allColorListParse() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            ProgressBar progressBar = findViewById(R.id.progressBar);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Document doc = Jsoup.connect(mUrl).get();
                    Element table = doc.select("table").first();
                    Elements elements = table.select("tr");
                    for (int i = 1; i < elements.size(); i++) {
                        Element row = elements.get(i);
                        Elements colorName = row.select("td:nth-child(2)");
                        Elements colorCode = row.select("td:nth-child(3)");
                        String color = row.select("td:nth-child(1)").attr("style").substring(17, 24);
                            ParseColorResource data = new ParseColorResource(color, colorName.text(), colorCode.text());
                            mArray.add(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        };
        task.execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}

