package com.example.muszakicikkwebshop;

import static java.util.Objects.isNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();

    private NotificationHandler mNotificationHandler;
    private JobScheduler jobScheduler;

    private int RowLengthNumber = 2;
    private boolean viewRow = true;
    private int ProductNumberLimit = 10;

    private FirebaseUser mUser;



    private FirebaseFirestore mFirestore;
    private int kosarCount = 0;
    private TextView TextViewCount;
    private FrameLayout frameLayoutGreenDot;
    private RecyclerView csereElemNezet;

    private ArrayList<ProductItemModell> mProductsData;
    private ProductItemModellAdapter mProductAdapter;
    private CollectionReference mProducts;

    private MenuItem menuLogOut;
    private MenuItem menuLogIn;
    private MenuItem menuRegister;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // alapból anonym belépés van

        mUser = FirebaseAuth.getInstance().getCurrentUser();




        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        csereElemNezet = findViewById(R.id.revyclerView);

        csereElemNezet.setLayoutManager(new GridLayoutManager(this, RowLengthNumber));

        mFirestore = FirebaseFirestore.getInstance();
        mProducts = mFirestore.collection("Items");

        mProductsData = new ArrayList<>();
        mProductAdapter = new ProductItemModellAdapter(this, mProductsData);
        csereElemNezet.setAdapter(mProductAdapter);


        mProducts = mFirestore.collection("Items");

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userEmail", "");
        editor.apply();
        editor.clear();
        editor.apply();
        queryData();
        mNotificationHandler = new NotificationHandler(this);

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        setJobScheduler();

    }
    // Data upload
    private void dataUpload(String name, String info, String price, Float starN, String imageR){
        mProducts.add(new ProductItemModell(
                name,
                info,
                price,
                starN,
                imageR
        ));
    }


    private void logingIn(){
        Intent intent =new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userEmail", "");
        editor.apply();
        editor.clear();
        editor.apply();
        finish();
        FirebaseAuth.getInstance().signOut();
    }
    private void logOut(){
        //preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userEmail", "");
        editor.apply();
        editor.clear();
        editor.apply();
        finish();
        FirebaseAuth.getInstance().signOut();
        startActivity(getIntent());
    }

    private void registering(){
        Intent intent =new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void queryBestProduct(){
        // FireStore lekérdezés
        mProductsData.clear();

        mProducts.whereGreaterThanOrEqualTo("StarNumber", 4).limit(ProductNumberLimit).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        ProductItemModell productItemModell = document.toObject(ProductItemModell.class);
                        mProductsData.add(productItemModell);
                    }
                    mProductAdapter.notifyDataSetChanged();
        });
    }
    private void queryData(){
        // FireStore lekérdezés

        mProductsData.clear();
        mProducts.orderBy("Name").limit(ProductNumberLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                ProductItemModell productItemModell = document.toObject(ProductItemModell.class);
                // add hozzá a mutatott listához
                mProductsData.add(productItemModell);
            }

            mProductAdapter.notifyDataSetChanged();
        });
    }



    @Override
    protected void onStart() {
        super.onStart();


        String userEmail = preferences.getString("userEmail", "NINCS ITT");
        Log.d(LOG_TAG, "In onStart: "+userEmail);
        Log.d(LOG_TAG, "onStart");
    }


    @Override
    protected void onResume(){
        super.onResume();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null){
            Log.d(LOG_TAG, "Authenticated user");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user");
        }
        // TODO
        // kosárba gomb enable/disable
        // @+id/Kosarba
        Log.d(LOG_TAG, "onResume");
        String userEmail = preferences.getString("userEmail", "NINCS ITT");

        Log.d(LOG_TAG, "Itt ez: "+userEmail);

        if(userEmail.contains("@") && !isNull(menuLogOut)){
            menuLogOut.setEnabled(true);
            menuLogIn.setEnabled(false);
            menuRegister.setEnabled(false);
        }
        // TODO:
        // CRUDE (szálak 7.)
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main_manu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        menuLogOut = menu.findItem(R.id.log_out);
        menuLogIn = menu.findItem(R.id.log_in);
        menuRegister = menu.findItem(R.id.registering);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mProductAdapter.getFilter().filter(s);
                return false;
            }
        });
        // A neten szt írták hogy "onCreateOptionsMenu" az "onResume" előtt fut le
        // de logolás mutatja hogy ez nem igaz
        onResume();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.log_out:
                Log.d(LOG_TAG, "Logout clicked");
                logOut();
                return true;
            case R.id.log_in:
                Log.d(LOG_TAG, "Logingin in");
                logingIn();
                return true;
            case R.id.allProducts:
                Log.d(LOG_TAG, "All products");
                queryData();
                return true;
            case R.id.bestProducts:
                Log.d(LOG_TAG, "Best products");
                queryBestProduct();
                return true;
            case R.id.registering:
                Log.d(LOG_TAG, "Regisztering");
                registering();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        frameLayoutGreenDot = (FrameLayout) rootView.findViewById(R.id.view_alert_green_circle);
        TextViewCount = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon() {
        kosarCount++;
        if(0 < kosarCount){
            TextViewCount.setText(String.valueOf(kosarCount));
        } else{ TextViewCount.setText("");}
        frameLayoutGreenDot.setVisibility((kosarCount > 0) ? View.VISIBLE : View.GONE);
        mNotificationHandler.send("Item in cart");
    }

    private void setJobScheduler(){
        int networkType = JobInfo.NETWORK_TYPE_UNMETERED;
        int hardDeadLine = 5000;

        ComponentName name = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, name).setRequiredNetworkType(networkType)
                .setRequiresCharging(true)
                .setOverrideDeadline(hardDeadLine);
        jobScheduler.schedule(builder.build());
        //jobScheduler.cancel(0);
    }

}