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
    private val fm = listOf(
        FM("WXYT", 97.1f, R.drawable.wxyt),
        FM("KURB", 98.5f, R.drawable.kurb),
        FM("WKIM", 98.9f, R.drawable.wkim),
        FM("WWTN", 99.7f, R.drawable.wwtn),
        FM("KDXE", 101.9f, R.drawable.kdxe),
        FM("KARN", 102.9f, R.drawable.karnfm),
        FM("KLAL", 107.7f, R.drawable.klal)
    )

    private val am = listOf(
        AM("WFAN", 93.1f, R.drawable.wfan),
        AM("KABC", 95.7f, R.drawable.kabc),
        AM("KARN", 96.6f, R.drawable.karnam),
        AM("WBAP", 97.6f, R.drawable.wbap),
        AM("WLS", 98.7f, R.drawable.wls)
    )

    private lateinit var current: Radio
    private var currentChannel: String = "am"
    private var currentFrequency: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isOnline()) {
            setContentView(R.layout.activity_main)
            initializeLogic()
        } else {
            setContentView(R.layout.no_internet)
        }
    }

    private fun initializeLogic() {
        findViewById<Button>(R.id.play).setOnClickListener {
            val radio = if (current is FM) {
                current.name.lowercase() + currentChannel
            } else {
                current.name.lowercase() + currentChannel
            }

            val intent = Intent(this, WebviewActivity::class.java).putExtra(
                "EXTRA_URL",
                "http://playerservices.streamtheworld.com/api/livestream-redirect/$radio.mp3"
            )
            startActivity(intent)
        }

        findViewById<SeekBar>(R.id.seekbar).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val frequency: Float = 88f + (p1 - 20f) / 10f
                currentFrequency = frequency
                checkStatus()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        findViewById<SwitchCompat>(R.id.channel).setOnCheckedChangeListener { button, b ->
            currentChannel = if (b) {
                "fm"
            } else {
                "am"
            }
            button.text = currentChannel.uppercase()
            checkStatus()
        }

    }

    private fun findFM(frequency: Float): FM? {
        return fm.firstOrNull { it.frequency < frequency + 0.3f && it.frequency > frequency - 0.3f }
    }

    private fun findAM(frequency: Float): AM? {
        return am.firstOrNull { it.frequency < frequency + 0.2f && it.frequency > frequency - 0.2f }
    }

    private fun checkStatus() {
        val radio = if (currentChannel == "fm") {
            findFM(currentFrequency)
        } else {
            findAM(currentFrequency)
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
                findViewById<TextView>(R.id.info).text = radio.name + currentChannel.uppercase()
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

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}