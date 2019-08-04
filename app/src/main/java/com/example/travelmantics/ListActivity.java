package com.example.travelmantics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        } else {
            insertMenu.setVisible(false);
        }
        return true;
    }

//     to make the list_activity_menu do a work using Selection method


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
//                getting the AuthUI instance
                AuthUI.getInstance()
                        .signOut(this)
//                        checking for a successful logout
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(Task<Void> task) {
                                // this perform the action of going to the login page using the attachListener
                                FirebaseUtil.attachListener();
                            }
                        });
//                using the detachListener
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReferenc("traveldeals", this);
        RecyclerView rvDeals = findViewById(R.id.rvDeals);
        final DealAdaptor adaptor = new DealAdaptor();
        rvDeals.setAdapter(adaptor);
        LinearLayoutManager dealLayoutManger = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvDeals.setLayoutManager(dealLayoutManger);
        FirebaseUtil.attachListener();
    }

    public void showMenu() {
//        this help to know that content of the menu have changed
        invalidateOptionsMenu();
    }
}
