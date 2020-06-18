package dev.irwanka.antrian

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import java.util.*
import android.os.Bundle
import android.app.Activity
import android.content.res.Resources
import android.media.MediaPlayer
import dev.irwanka.antrian.ServicePanggilan.LocalBinder

class ServicePanggilan : Service() {

    private val mBinder = LocalBinder()
    private var mediaPlayer: MediaPlayer?=null;
    val arrayListAudio = ArrayList<Int>();
    var currentSound:Int = 0;
    var onCalling:Boolean = false;

    var activity: Callbacks? = null

    override fun onBind(intent: Intent): IBinder? {
        return mBinder;
    }

    var handler = Handler()
    private lateinit var mRunnableCalling:Runnable



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Send a notification that service is started
        toast("Service started.")
        onCalling=false;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.end);
        mRunnableCalling = Runnable {
            // Do something here
            cekPanggilanAntrian()
        }

        handler.postDelayed(
            mRunnableCalling, // Runnable
            1000 // Delay in milliseconds
        )
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        toast("Service destroyed.")
        mediaPlayer?.release()
        handler.removeCallbacks(mRunnableCalling)
    }

    // Custom method to do a task`
    private fun cekPanggilanAntrian() {
        val rand = Random()
        val status = 1 + rand.nextInt(5);
        activity?.updateClient(status);
        //ada panggilan antrian
        if(status==5){
            if (!mediaPlayer?.isPlaying()!!){
                val number = 1 + rand.nextInt(300)
                val loket = 1+ rand.nextInt(4)
                toast("Panggilan Number : $number Loket: $loket" )
                activity?.updateClient(number);
                callingAntrian(number, loket)
            }
        }else{
            if (!mediaPlayer?.isPlaying()!!){
                handler.postDelayed(
                    mRunnableCalling, // Runnable
                    500 // Delay in milliseconds
                )
            }
        }
    }

    //Here Activity register to the service as Callbacks client
    fun registerClient(activity: Activity) {
        this.activity = activity as Callbacks
    }

    fun getStatusPanggilan():Int{
        val rand = Random()
        val status = rand.nextInt(2);
        return status;
    }

    fun callingAntrian(no_antrian:Int, no_loket:Int){

        onCalling=true;
        currentSound = 0;
        arrayListAudio.clear();
        Toast.makeText(this.applicationContext,"Panggil Antrian " + no_antrian + " Loket " + no_loket,
            Toast.LENGTH_LONG).show()

        var nomor_antrian:Int = no_antrian;
        var nomor_loket:Int = no_loket;
        var nomor_antrian_terbilang:String = Terbilang.terbilang(nomor_antrian);
        var nomor_loket_terbilang:String = Terbilang.terbilang(nomor_loket);
        var string_terbilang_antrian = nomor_antrian_terbilang.split(" ").toTypedArray();
        var string_terbilang_loket = nomor_loket_terbilang.split(" ").toTypedArray();

        arrayListAudio.add(R.raw.start);
        arrayListAudio.add(R.raw.antrian);
        for (nomor in string_terbilang_antrian) {
            val res: Resources = applicationContext.getResources()
            val soundId: Int = res.getIdentifier(nomor, "raw", applicationContext.getPackageName())
            arrayListAudio.add(soundId);
        }

        arrayListAudio.add(R.raw.loket);
        for (nomor in string_terbilang_loket) {
            val res: Resources = applicationContext.getResources()
            val soundId: Int = res.getIdentifier(nomor, "raw", applicationContext.getPackageName())
            arrayListAudio.add(soundId);
        }
        arrayListAudio.add(R.raw.end);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), arrayListAudio.get(currentSound) );
        mediaPlayer?.start();
        onCompleteAudio();
    }


    fun onCompleteAudio(){
        mediaPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            currentSound++;
            if (currentSound < arrayListAudio.size) {
                mediaPlayer =
                    MediaPlayer.create(getApplicationContext(), arrayListAudio.get(currentSound));
                mediaPlayer?.start();
                onCompleteAudio();
                onCalling = true;
            } else {
                onCalling = false;
                arrayListAudio.clear();
                currentSound = 0;
                handler.postDelayed(
                    mRunnableCalling, // Runnable
                    500 // Delay in milliseconds
                )
            }
        });
    }

    //returns the instance of the service
    inner class LocalBinder : Binder() {
        val serviceInstance: ServicePanggilan
            get() = this@ServicePanggilan
    }

    interface Callbacks {
        fun updateClient(data: Int)
    }

}
