package com.example.clickify.AdminDashBoard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.clickify.API_Manager.ApiManager;
import com.example.clickify.Login.LoginScreen;
import com.example.clickify.OverLayService.OverlayActivity;
import com.example.clickify.OverLayService.OverlayPointerService;
import com.example.clickify.OverLayService.OverlayService;
import com.example.clickify.R;
import com.example.clickify.SessionManager.SessionManager;
import com.example.clickify.User;
import com.example.clickify.UtilityClass;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminDashBoard extends AppCompatActivity implements UserListAdapter.OnItemClickListener {

    RecyclerView userRecycler;

    List<User> userList;
    List<User> filterUserList;
    UserListAdapter userListAdapter;
    ApiManager api;
    ProgressBar pb;
    View rootView;
    SwipeRefreshLayout refresher;
    ImageView ic_logout, ic_clicker;
    SearchView userSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        hooksFind();
        getDataFromDataBase();
        refreshListner();
        onClickerListner();
    }

    private void onClickerListner() {
        ic_logout.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashBoard.this, LoginScreen.class));
            finish();
            stopService(new Intent(AdminDashBoard.this, OverlayService.class));
            stopService(new Intent(AdminDashBoard.this, OverlayPointerService.class));
            new SessionManager(AdminDashBoard.this).logoutUser();


        });
        ic_clicker.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashBoard.this, OverlayActivity.class);
            intent.putExtra("intent_text", "admin"); // Add extra text
            startActivity(intent);
            finish();
        });

        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void refreshListner() {
        refresher.setOnRefreshListener(() -> {
            if(UtilityClass.isInternetAvailable(AdminDashBoard.this)) {

                refresher.setRefreshing(true);
                // Refresh your data here
                new Handler().postDelayed(this::getDataFromDataBase, 3000);
            }else
            {
                Snackbar.make(rootView,"Please Check Your Internet Connection!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setListToRecycler() {
        userListAdapter = new UserListAdapter(this, filterUserList, this);
        userRecycler.setAdapter(userListAdapter);
    }

    private void getDataFromDataBase() {
        pb.setVisibility(View.VISIBLE);
        userList.clear();
        filterUserList.clear();
        api.fetchAllUsers(new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if (jsonArray != null) {
                    jsonManager(jsonArray);
                } else {
                    Snackbar.make(rootView, "Please Wait for Server Response!", Snackbar.LENGTH_LONG).show();
                }
                Log.w("jarvis", jsonArray.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pb.setVisibility(View.GONE);
                Log.w("jarvis", "volley error " + volleyError.toString());
            }
        });
    }

    private void jsonManager(JSONArray dataArray) {
        try {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject js = dataArray.getJSONObject(i);
                int id = js.getInt("id");
                String email = js.getString("user_email");
                String isRegistered = js.getString("isRegistered");
                userList.add(new User(id, email, isRegistered));
            }
            filterUserList.addAll(userList);
            refresher.setRefreshing(false);
            pb.setVisibility(View.GONE);
            setListToRecycler();
        } catch (JSONException e) {
            Log.e("jarvis", e.toString());
            pb.setVisibility(View.GONE);
        }
    }

    private void hooksFind() {
        userRecycler = findViewById(R.id.recyclerView);
        pb = findViewById(R.id.progressBarAdmin);
        refresher = findViewById(R.id.swipeRefreshLayout);
        ic_clicker = findViewById(R.id.ic_clicker);
        ic_logout = findViewById(R.id.ic_logout);
        userSearchView = findViewById(R.id.searchView);
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
        rootView = findViewById(android.R.id.content);
        userList = new ArrayList<>();
        filterUserList = new ArrayList<>();
        api = new ApiManager(this);
    }

    @Override
    public void onItemClick(User user, int position) {
        if (user != null) {
            if (user.getIsRegistered().equals("1")) {
                pb.setVisibility(View.VISIBLE);

                deleteUser(user.getId(), position);
            } else if (user.getIsRegistered().equals("0")) {
                pb.setVisibility(View.VISIBLE);
                approveUser(user.getId(), position);
            }
        }
    }

    private void approveUser(int id, int position) {
        api.approveUser(id, true, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pb.setVisibility(View.GONE);
                userList.get(position).setIsRegistered("1");
                userListAdapter.notifyItemChanged(position);
                Snackbar.make(rootView, "Approved Successfully!", Snackbar.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pb.setVisibility(View.GONE);
                Snackbar.make(rootView, "Server Error!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteUser(int id, int position) {
        api.deleteUser(id, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                userList.remove(position);
                filterUserList.remove(position);
                userListAdapter.notifyItemRemoved(position);
                userListAdapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
                Snackbar.make(rootView, "Deleted Successfully!", Snackbar.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("jarvis", Objects.requireNonNull(volleyError.getMessage()));
                pb.setVisibility(View.GONE);
                Snackbar.make(rootView, "Server Error !", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void filter(String text) {
        List<User> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            text = text.toLowerCase();
            for (User item : userList) {
                if (item.getUserEmail().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
        }
        userListAdapter.updateList(filteredList);
    }
}
