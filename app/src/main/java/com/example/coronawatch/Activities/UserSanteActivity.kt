package com.example.coronawatch.Activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.coronawatch.R
import kotlinx.android.synthetic.main.activity_user_sante.*

class UserSanteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sante)

        newDialog(hearLayout, heartRateTextView)
        newDialog(bloodLayout, bloodRateTextView)
        newDialog(heightLayout, heightTextView)
        newDialog(weightLayout, weightTextView)
        newDialog(temperatureLayout, temperatureTextView)
        newDialog(ageLayout, ageTextView)

    }

    fun getData(){

    }

    fun saveData(){

    }

    fun initChart(){

    }

    fun newDialog(layout : RelativeLayout, valueTextView : TextView){
        layout.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.sante_dialog_layout)

            val saveBtn = dialog.findViewById<Button>(R.id.saveBtn)

            saveBtn.setOnClickListener {
                val newV = dialog.findViewById<EditText>(R.id.editText).text.toString()
                if (newV == ""){
                    Toast.makeText(this, "الرجاء ملء المعطيات", Toast.LENGTH_SHORT).show()
                }else{
                    valueTextView.text = newV
                    dialog.cancel()
                }
            }
            dialog.show()
        }
    }


}
