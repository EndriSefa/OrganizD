package com.example.organizd

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.organizd.databinding.FragmentTimerBinding
import java.util.*


class TimerFragment : Fragment(R.layout.fragment_timer) {

    lateinit var binding: FragmentTimerBinding
    lateinit var dataHelper: DataHelper

    private val timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_timer, container, false)

        val mNotificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        binding = FragmentTimerBinding.inflate(layoutInflater)
        val view = binding.root

        dataHelper = DataHelper(requireActivity()) // Non posso inserire nelle parentesi (applicationContext) poiché mi trovo in un fragment.


        binding.startButton.setOnClickListener{ startStopAction() }
        binding.resetButton.setOnClickListener{ resetAction() }

        if(dataHelper.timerCounting())
        {
            startTimer()
        }
        else
        {
            stopTimer()
            if(dataHelper.startTime() != null && dataHelper.stopTime() != null)
            {
                val time = Date().time - calcRestartTime().time
                binding.timeTV.text = timeStringFromLong(time)
            }
        }


        // Aggiornamento costante della vista
        timer.scheduleAtFixedRate(TimeTask(), 0, 500) // Ritardo nell'aggiornamento pari a zero e controlleremo ogni mezzo secondo.


        // Info toast
        binding.infoIcon.setOnClickListener{
            Toast.makeText(this@TimerFragment.requireActivity(), "La funzione total focus permette di silenziare qualunque notifica per migliorare la tua concentrazione.", Toast.LENGTH_LONG).show()
        }


        
        // Prova attivazione modalità non disturbare.

        binding.switchNotDisturb.setOnCheckedChangeListener{ buttonView, isChecked ->

            if(isChecked)
            {
                if (!mNotificationManager.isNotificationPolicyAccessGranted) {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                    startActivity(intent)
                }
            }

        }



        return view
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        //val info = view.findViewById<ImageView>(R.id.infoIcon)

        //binding = FragmentTimerBinding.inflate(layoutInflater)
        //setContentView(binding.root)



    }

    private inner class TimeTask: TimerTask()
    {
        override fun run()
        {
            //println(LocalDateTime.now())
            if(dataHelper.timerCounting()) // Se il timer è partito
            {
                //val tempo: DataHelper(context)
                val time = Date().time - dataHelper.startTime()!!.time // Allora per sapere dove passa calcolo l'ora attuale (Date().time) meno l'ora d'inizio (dataHelpre.startTime()!!.time)
                binding.timeTV.text = timeStringFromLong(time)
            }
        }
    }

    private fun resetAction() {

        dataHelper.setStopTime(null)
        dataHelper.setStartTime(null)
        stopTimer()
        binding.timeTV.text = timeStringFromLong(0)

    }

    private fun stopTimer()
    {
        dataHelper.setTimerCounting(false)
        binding.startButton.text = getString(R.string.start)
    }

    private fun startTimer()
    {
        dataHelper.setTimerCounting(true)
        binding.startButton.text = getString(R.string.stop)
    }

    private fun startStopAction()
    {
        if(dataHelper.timerCounting())
        {
            dataHelper.setStopTime(Date())
            stopTimer()
        }
        else
        {
            if(dataHelper.stopTime() != null)
            {
                dataHelper.setStartTime(calcRestartTime())
                dataHelper.setStopTime(null)
            }
            else
            {
                dataHelper.setStartTime(Date())
            }
            startTimer()
        }
    }

    private fun calcRestartTime(): Date
    {
        val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff) // (Ora di inizio - ora di fine) aggiugilo all'ora corrente.
    }

    private fun timeStringFromLong(ms: Long): String
    {

        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        return makeTimeString(hours, minutes, seconds)

    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String
    {

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)

    }

}


