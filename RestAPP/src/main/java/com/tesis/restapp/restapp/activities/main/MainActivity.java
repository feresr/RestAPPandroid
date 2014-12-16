package com.tesis.restapp.restapp.activities.main;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.tesis.restapp.restapp.R;
import com.tesis.restapp.restapp.api.ApiClient;
import com.tesis.restapp.restapp.api.RestAppApiInterface;
import com.tesis.restapp.restapp.database.DatabaseHandler;
import com.tesis.restapp.restapp.models.Order;
import com.tesis.restapp.restapp.models.Table;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements MainHandler {
    private static final String KEY_SELECTED_ORDER = "SELECTED_ORDER";
    private static final String KEY_DIALOG_SHOWING = "DIALOG_SHOWING";
    private ProgressDialog pDialog;

    private RestAppApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pDialog = new ProgressDialog(this);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        if (savedInstanceState == null) {
            DashboardFragment dashboardFragment = new DashboardFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, dashboardFragment)
                    .commit();

            new SyncDB(this).execute();

        } else {
            if (savedInstanceState.getBoolean(KEY_DIALOG_SHOWING)) {
                pDialog.setMessage("Actualizando BD....");
                pDialog.show();
            }
        }
    }

    @Override
    public void onOrderSelected(Order order) {
        Intent i = new Intent(this, OrderActivity.class);
        i.putExtra("ORDER_ID", order.getId());
        startActivity(i);
    }

    @Override
    public void onNewOrderSelected() {
        TablesFragment tablesFragment = new TablesFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.addToBackStack(null)
                .replace(R.id.container, tablesFragment)
                .commit();
    }

    public void onTableOccupied() {
        Toast.makeText(this, "Alguien ya ocupó esta mesa", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTableSelected(final Table table) {

        pDialog.setMessage("Creando orden...");
        pDialog.show();
        final DatabaseHandler db = new DatabaseHandler(this);

        apiInterface = ApiClient.getRestAppApiClient(this);
        apiInterface.newOrder(table.getId(), new Callback<com.tesis.restapp.restapp.database.Response>() {
            @Override
            public void success(com.tesis.restapp.restapp.database.Response apiResponse, Response response) {
                if (apiResponse.wasSuccessful()) {
                    Order order = new Order();
                    order.setTable(table);
                    order.setId(apiResponse.getId());
                    db.addOrder(order);
                    onOrderSelected(order);
                } else {
                    onTableOccupied();
                }
                pDialog.dismiss();
                db.close();
            }

            @Override
            public void failure(RetrofitError error) {
                pDialog.dismiss();
                //SERVER ERROR

                db.close();
            }
        });

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_DIALOG_SHOWING, pDialog.isShowing());
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {

        final DatabaseHandler db = new DatabaseHandler(this);
        db.close();
        super.onPause();
    }

    private class SyncDB extends AsyncTask<Void, Void, RetrofitError> {

        Context context;

        public SyncDB(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Actualizando BD....");
            pDialog.show();

        }

        @Override
        protected RetrofitError doInBackground(Void... params) {


            apiInterface = ApiClient.getRestAppApiClient(getApplicationContext());
            final DatabaseHandler db = new DatabaseHandler(context);
            try {
                db.addCategories(apiInterface.retrieveCategories());
                db.addItems(apiInterface.retrieveItems());
                db.addTables(apiInterface.retrieveTables());
                db.addOrderItems(apiInterface.retrieveOrderItems());
            } catch (RetrofitError e) {
                return e;
            }
            return null;
        }


        @Override
        protected void onPostExecute(RetrofitError error) {
            pDialog.dismiss();
            if (error != null) {
                Log.d("SAD", error.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Error");
                builder.setMessage("There was a server error");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.create().show();
            }
            super.onPostExecute(error);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}


