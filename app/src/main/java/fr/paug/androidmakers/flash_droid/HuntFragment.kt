package fr.paug.androidmakers.flash_droid

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.rendering.ViewSizer
import com.google.ar.sceneform.ux.ArFragment
import fr.paug.androidmakers.R
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.N)
class HuntFragment : ArFragment() {

    private val trackedImages = mutableSetOf<Int>()
    private var amHugeBanner: ViewRenderable? = null

    override fun onResume() {
        super.onResume()
        arSceneView.scene.addOnUpdateListener(this::onUpdateFrame);
    }

    override fun onPause() {
        super.onPause()
        arSceneView.scene.removeOnUpdateListener(this::onUpdateFrame)
    }

    private fun onUpdateFrame(frameTime: FrameTime) {
        val frame = arSceneView.arFrame

        // If there is no frame, renderable not loaded, or ARCore is not tracking yet, just return.
        if (frame == null
                || frame.camera.trackingState != TrackingState.TRACKING
                || amHugeBanner == null) {
            return
        }

        val updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
        for (augmentedImage in updatedAugmentedImages) {
            when (augmentedImage.trackingState!!) {
                TrackingState.PAUSED -> {
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    Log.d("HuntFragment", "Detected Image " + augmentedImage.getIndex())
                }

                TrackingState.TRACKING -> {
                    // Create a new anchor for newly found images.
                    if (!trackedImages.contains(augmentedImage.index)) {
                        val base = AnchorNode()
                        base.anchor = augmentedImage.createAnchor(augmentedImage.centerPose)

                        val node = Node()
                        node.setParent(base)
                        node.localPosition = Vector3(0f, 0f, augmentedImage.extentZ/2)
                        node.localRotation = Quaternion.axisAngle(Vector3(1.0f, 0f, 0f), -90f)
                        node.localScale = Vector3(augmentedImage.extentX, augmentedImage.extentZ, 0f)

                        node.renderable = amHugeBanner

                        trackedImages.add(augmentedImage.index)
                        arSceneView.scene.addChild(base)
                    }
                }

                TrackingState.STOPPED -> trackedImages.remove(augmentedImage.index)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val frameLayout = FrameLayout(container!!.context)
        val view = super.onCreateView(inflater, frameLayout, savedInstanceState)

        // Turn off the plane discovery since we're only looking for images
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false

        val view2 = ImageView(context)
        view2.setImageResource(R.drawable.am)
        ViewRenderable.builder().setView(context, view2).build().handle { renderable, throwable ->
            if (throwable != null) {
                Log.e("HuntFragment", "Ooops")
            } else {
                val size = Vector3(1.0f, 1.0f, 1.0f)
                renderable.sizer = ViewSizer {
                    size
                }
                this.amHugeBanner = renderable
            }
        }

        frameLayout.addView(view)
        return frameLayout
    }

    override fun getSessionConfiguration(session: Session): Config {
        val config = super.getSessionConfiguration(session)
        if (!setupAugmentedImageDatabase(config, session)) {
            Log.e("HuntFragment", "Could not setup augmented image database")
        }
        return config
    }

    private fun setupAugmentedImageDatabase(config: Config, session: Session): Boolean {
        val assetManager = if (context != null) context!!.assets else null
        if (assetManager == null) {
            Log.e("ArAmFragment", "Context is null, cannot intitialize image database.")
            return false
        }

        try {
            context!!.assets.open("images.imgdb").use {
                config.augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, it)
            }
        } catch (e: IOException) {
            Log.e("HuntFragment", "IO exception loading augmented image database.", e)
            return false
        }

        return true
    }
}