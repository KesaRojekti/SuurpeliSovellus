package fi.suurpelisovellus.suurpeli.suurpelifirebase

import android.app.Dialog
import android.app.DialogFragment
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import org.jetbrains.anko.share

class SettingsDialogFragment : DialogFragment() {

    private var checkedItems = booleanArrayOf(true, false, false, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //track if you made any changes
        var changes: Boolean = false
        //read preferences
        readPreferences()
        //build the dialog
        val diaBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
                .setTitle(getString(R.string.settings_text))
                //set up a multiple choice list (can contain just 1 item)
                .setMultiChoiceItems(R.array.settings_dialog_options, checkedItems) { _, which, isChecked ->
                    if(isChecked){
                        if(which == 0){
                            checkedItems[0] = true
                            changes = true
                        }
                    }
                    //check if unchecked
                    else {
                        if(which == 0){
                            checkedItems[0] = false
                            changes = true
                        }
                    }
                }

                //ok button, dismisses the dialog and writes changes to preferences
                .setPositiveButton("OK") { _, _ ->
                    //only write changes if you made any
                    if(changes){
                        writePreferences()
                        //reload MainActivity so the changes you made take effect
                        val reload: Intent = Intent(activity?.baseContext, MainActivity::class.java)
                        reload.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(reload)
                    }
                }

        return diaBuilder.create()
    }

    //write changes into preferences
    fun writePreferences(){
        val sharedPref = activity?.getSharedPreferences(getString(R.string.settings_notifications_key), Context.MODE_PRIVATE)
        val sharedKey = getString(R.string.settings_notifications_key)
        val editor = sharedPref?.edit()
        editor?.putBoolean(sharedKey, checkedItems[0])
        editor?.apply()


        //debug
        /*
        val def = false
        val stance = sharedPref?.getBoolean(sharedKey, def)
        Log.d("stance is", "" + stance)
        */
    }

    //read preferences and replace default values in the array
    fun readPreferences(){
        val sharedPref = activity?.getSharedPreferences(getString(R.string.settings_notifications_key), Context.MODE_PRIVATE)
        val sharedKey = getString(R.string.settings_notifications_key)
        val pref = sharedPref?.getBoolean(sharedKey, true)
        checkedItems[0] = pref!!
    }
}