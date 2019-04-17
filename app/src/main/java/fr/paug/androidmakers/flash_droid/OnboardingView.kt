package fr.paug.androidmakers.flash_droid

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout

class OnboardingView (context: Context): CoordinatorLayout(context) {

    var onFinished: (() -> Unit)? = null

    fun setOnFinishedCallback(onFinished: () -> Unit) {
        this.onFinished = onFinished
    }

}