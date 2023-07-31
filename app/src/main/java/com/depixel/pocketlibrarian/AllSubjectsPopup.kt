package com.depixel.pocketlibrarian

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_all_subjects_popup.*


class AllSubjectsPopup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)


//        val alpha = 100 //between 0-255
//        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
//        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
//        colorAnimation.duration = 500 // milliseconds
//        colorAnimation.addUpdateListener { animator ->
//            popup_background.setBackgroundColor(animator.animatedValue as Int)
//        }
//        colorAnimation.start()

//        moreSubjectsCard.alpha = 0f
//        moreSubjectsCard.animate().alpha(1f).setDuration(500).setInterpolator(
//            DecelerateInterpolator()
//        ).start()

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS)

        setContentView(R.layout.activity_all_subjects_popup)

        all_items_popup_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val gson = GsonBuilder().create()

        val moreSubjectsJSON = getJsonDataFromAsset(applicationContext,"allSubjects.json")
        val moreSubjects = gson.fromJson(moreSubjectsJSON, MoreSubjects::class.java)

        runOnUiThread {
            all_items_popup_recycler.adapter = MoreSubjectsAdapter(moreSubjects)
        }
    }
}