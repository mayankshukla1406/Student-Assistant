package com.example.studentasisstant.FirebaseCloudMessaging

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.studentasisstant.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.json.JSONException
import org.json.JSONObject

class FirebaseNotification(context: Context):ContextWrapper(context) {
    fun sendFCMPush(token:String,title : String,message : String) {
        val Legacy_SERVER_KEY: String = ""

        val msg = message
        val title = title
        val token: String = token
        var obj: JSONObject? = null
        var objData: JSONObject? = null
        var dataobjData: JSONObject? = null
        try {
            obj = JSONObject()
            objData = JSONObject()
            objData.put("message", msg)
            objData.put("title", title)
            objData.put("sound", "default")
            objData.put("icon", R.drawable.ic_launcher_background) //   icon_name image must be there in drawable
            objData.put("tag", token)
            objData.put("priority", "high")
            dataobjData = JSONObject()
            dataobjData.put("message", msg)
            dataobjData.put("title", title)
            obj.put("to", token)
            //obj.put("priority", "high");
            obj.put("notification", objData)
            obj.put("data", dataobjData)
            Log.e("object>", obj.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsObjRequest: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                Response.Listener { response ->
                    Log.e("Success", response.toString() + "")
                    Log.e("token", token)
                }
                ,
                Response.ErrorListener { error -> Log.e("Error", error.toString() + "") }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"] = "key=$Legacy_SERVER_KEY"
                    params["Content-Type"] = "application/json"
                    Log.d("params",params.toString())
                    return params
                }
            }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        val socketTimeout = 1000 * 60 // 60 seconds
        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsObjRequest.retryPolicy = policy
        requestQueue.add(jsObjRequest)
    }
    fun sendToAll(title : String,message : String)
    {
        FirebaseFirestore.getInstance().collection("Token").get()
            .addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                // after getting the data we are calling on success method
                // and inside this method we are checking if the received
                // query snapshot is empty or not.
                if (!queryDocumentSnapshots.isEmpty) {
                    // if the snapshot is not empty we are
                    // hiding our progress bar and adding
                    // our data in a list.
                    val list = queryDocumentSnapshots.documents
                    for (d in list) {
                        val userToken = d.get("token").toString()
                        sendFCMPush(userToken, title, message)
                    }
                }
            })
    }
}
