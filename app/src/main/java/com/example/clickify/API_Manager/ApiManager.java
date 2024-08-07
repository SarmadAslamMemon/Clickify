package com.example.clickify.API_Manager;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiManager {
    private static final String BASE_URL = "https://roughlyandriodapp.000webhostapp.com/Clickify/";
    private RequestQueue requestQueue;

    public ApiManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void sendRequest(String url, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {

        Log.i("jarvis", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, listener, errorListener);
        requestQueue.add(request);
    }


    // Add a user


    public void addUser(String userEmail, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        String url = BASE_URL + "add_user.php";

        Log.i("jarvis", url);
        Map<String, String> params = new HashMap<>();
        params.put("email", userEmail);  // Only email param is needed

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle the response
                    Log.i("jarvis", "Response: " + response);
                    listener.onResponse(response);
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Log.e("jarvis", "Error: " + error.getMessage());
                        errorListener.onErrorResponse(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        requestQueue.add(request);
    }




    // Delete a user
    public void deleteUser(int id, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        String url = BASE_URL + "delete_user.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        Log.w("jarvis","add user url :"+url);
        requestQueue.add(request);
    }

    // Approve a request
    public void approveUser(int userId, boolean isRegistered, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        String url = BASE_URL + "approve_user.php";

        Log.w("jarvis","check "+url);
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(userId));
        params.put("isRegistered", String.valueOf(isRegistered));

        StringRequest request = new StringRequest(Request.Method.POST, url,
                listener,
                errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        requestQueue.add(request);
    }

    // Fetch all users
    public void fetchAllUsers(final Response.Listener<JSONArray> listener, final Response.ErrorListener errorListener) {
        String url = BASE_URL + "fetch_users.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, null, listener, errorListener);
        requestQueue.add(request);
    }
}
