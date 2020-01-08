package com.getitfoodie.rider


import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment() {

    internal lateinit var listener: DatePickerDialog.OnDateSetListener


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = targetFragment as HistoryFragment
    }

    override
    fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return  DatePickerDialog(activity!!, listener, year, month, day)
    }
}
