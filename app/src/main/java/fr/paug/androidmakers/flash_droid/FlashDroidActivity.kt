package fr.paug.androidmakers.flash_droid

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.BaseAdapter
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.google.ar.core.ArCoreApk
import fr.paug.androidmakers.R
import fr.paug.androidmakers.ui.activity.BaseActivity

class FlashDroidActivity : AppCompatActivity() {
    val FLASH_DROID_PREFERENCES = "FLASH_DROID_PREFERENCES"
    val KEY_FIRST_LAUNCH = "KEY_FIRST_LAUCNH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences(FLASH_DROID_PREFERENCES, Context.MODE_PRIVATE)

        val availability = ArCoreApk.getInstance().checkAvailability(this)

        return if (!availability.isSupported || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            setNougatNeededFragment()
        } else if (true || preferences.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
            setOnboarding()
        } else {
            setHunt()
        }
    }

    private fun setNougatNeededFragment() {
        val frameLayout = FrameLayout(this)
        frameLayout.id = R.id.flash_droid_framelayout

        supportFragmentManager.beginTransaction()
                .add(frameLayout.id, NougatNeededFragment() )
        setContentView(frameLayout)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setOnboarding() {
        val onboardingView = OnboardingView(context = this)
        onboardingView.setOnFinishedCallback {
            setHunt()
        }
        setContentView(onboardingView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setHunt() {
        val frameLayout = FrameLayout(this)
        frameLayout.id = R.id.flash_droid_framelayout

        supportFragmentManager.beginTransaction()
                .add(frameLayout.id, HuntFragment() )
        setContentView(frameLayout)
    }
}