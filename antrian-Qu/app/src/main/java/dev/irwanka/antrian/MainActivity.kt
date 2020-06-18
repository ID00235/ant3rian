package dev.irwanka.antrian

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.res.Resources
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Build
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random
import android.content.ComponentName
import android.os.IBinder
import android.content.ServiceConnection





abstract class AppCompatActivity

class MainActivity : AppCompatActivity() , ServicePanggilan.Callbacks {

    lateinit var btnCalling: Button

    var servicePanggilan : ServicePanggilan? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnCalling = findViewById(R.id.btn_calling)

        btnCalling.setOnClickListener {
            // If the service is not running then start it
            val serviceClass = ServicePanggilan::class.java
            val intentService = Intent(applicationContext, serviceClass)
            if (!isServiceRunning(serviceClass)) {
                // Start the service
                startService(intentService)
                bindService(intentService, mConnection,Context.BIND_AUTO_CREATE); //Binding to the service!
            } else {
                toast("Service already running.")
            }
        }

    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }

    override fun updateClient(number: Int) {
        textNumber.setText("Nomor Panggilan: " + number)
        /**
        val cek = servicePanggilan?.getStatusPanggilan();
        if(cek==1){
            toast("Oke Panggilan Random aja")
        }
        **/
    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            Toast.makeText(this@MainActivity, "Call Service Connected", Toast.LENGTH_SHORT)
                .show()
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ServicePanggilan.LocalBinder
            servicePanggilan = binder.serviceInstance
            servicePanggilan?.registerClient(this@MainActivity) //Activity register in the service as client for callabcks!

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Toast.makeText(this@MainActivity, "onServiceDisconnected called", Toast.LENGTH_SHORT)
                .show()
            toast("Call Service disconnected")
        }
    }


}

fun Context.toast(message:String){
    Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
}
