package com.video.downloading.app.downloader.online.app.actvities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.video.downloading.app.downloader.online.app.R
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*

class TermsAndConditionsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        card.visibility = View.GONE
        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                card.visibility = View.VISIBLE
            } else {
                card.visibility = View.GONE
            }
        }

        accept.setOnClickListener {
            if (checkBox.isChecked) {

                SharedPrefUtils.saveData(this, "isTerms", true)
                SharedPrefUtils.saveData(this, "isFirst", true)
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finishAffinity()
            }
        }
    }

}
