package fr.paug.androidmakers.flash_droid

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import fr.paug.androidmakers.R

class OnboardingView (context: Context): ConstraintLayout(context) {

    var onFinished: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.onboarding_view, this, true)
    }

    fun setOnFinishedCallback(onFinished: () -> Unit) {
        this.onFinished = onFinished
    }

}