package com.uca.app2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private val am = listOf(
        AM_Class("WFAN", 93.1f, R.drawable.wfan),
        AM_Class("KABC", 95.7f, R.drawable.kabc),
        AM_Class("KARN", 96.6f, R.drawable.karnam),
        AM_Class("WBAP", 97.6f, R.drawable.wbap),
        AM_Class("WLS", 98.7f, R.drawable.wls)
    )

    private val fm = listOf(
        FM_Class("WXYT", 97.1f, R.drawable.wxyt),
        FM_Class("KURB", 98.5f, R.drawable.kurb),
        FM_Class("WKIM", 98.9f, R.drawable.wkim),
        FM_Class("WWTN", 99.7f, R.drawable.wwtn),
        FM_Class("KDXE", 101.9f, R.drawable.kdxe),
        FM_Class("KARN", 102.9f, R.drawable.karnfm),
        FM_Class("KLAL", 107.7f, R.drawable.klal)
    )

    // AM or FM? AM by default.
    private var channelCurrent: String = "am"
    // Hold the current frequency channel
    private var frequencyCurrent: Float = 0f
    // Hold which Radio_Class.
    private lateinit var current: Radio_Class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ErrorChecking()) {
            setContentView(R.layout.activity_main)
            initializeLogic()
        } else {
            setContentView(R.layout.no_internet)
        }
    }

    private fun initializeLogic() {
        findViewById<Button>(R.id.play).setOnClickListener {
            // Variable that holds the current radio, this will be used to change the URL. XXXX.mp3.
            val radio_name_URL = if (current is FM_Class) {
                current.name.lowercase() + channelCurrent
            } else {
                current.name.lowercase() + channelCurrent
            }
            val intent = Intent(this, WebviewActivity::class.java).putExtra(
                "EXTRA_URL",
                "http://playerservices.streamtheworld.com/api/livestream-redirect/$radio_name_URL.mp3"
            )
            startActivity(intent)
        }

        findViewById<SeekBar>(R.id.seekbar).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val frequency: Float = 88f + (p1 - 20f) / 10f
                frequencyCurrent = frequency
                checkStatus()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        findViewById<SwitchCompat>(R.id.channel).setOnCheckedChangeListener { button, b ->
            channelCurrent = if (b) {
                "fm"
            } else {
                "am"
            }
            button.text = channelCurrent.uppercase()
            checkStatus()
        }

    }

    // Check if we are ONLINE
    private fun ErrorChecking(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    // Find what channel corresponds with the current frequency
    private fun checkStatus() {
        val radio = if (channelCurrent == "fm") {
            findFM(frequencyCurrent)
        } else {
            findAM(frequencyCurrent)
        }
        if (radio != null) {
            current = radio
            findViewById<ImageView>(R.id.image).setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    radio.image
                )
            )
            if (radio.name == "KARN") {
                findViewById<TextView>(R.id.info).text = radio.name + channelCurrent.uppercase()
            } else {
                findViewById<TextView>(R.id.info).text = radio.name
            }

        } else {
            findViewById<ImageView>(R.id.image).setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.launch
                )
            )
            findViewById<TextView>(R.id.info).text = "Now Playing"
        }
    }



    private fun findAM(frequency: Float): AM_Class? {
        return am.firstOrNull { it.frequency < frequency + 0.2f && it.frequency > frequency - 0.2f }
    }

    private fun findFM(frequency: Float): FM_Class? {
        return fm.firstOrNull { it.frequency < frequency + 0.3f && it.frequency > frequency - 0.3f }
    }
}