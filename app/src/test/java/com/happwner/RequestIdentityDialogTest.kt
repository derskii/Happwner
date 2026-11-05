package com.happwner

import android.content.DialogInterface
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28, 35])
class RequestIdentityDialogTest {
    private lateinit var controller: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before fun setup() {
        PrefsManager.getSafePrefs(RuntimeEnvironment.getApplication()).edit().clear().commit()
        controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        activity = controller.get()
    }

    @After fun teardown() { controller.pause().stop().destroy() }

    private fun open(): AlertDialog {
        activity.findViewById<View>(R.id.btnRequestDevice).performClick()
        shadowOf(Looper.getMainLooper()).idle()
        return ShadowDialog.getLatestDialog() as AlertDialog
    }

    private fun select(dialog: AlertDialog, spinner: Int, index: Int) {
        dialog.findViewById<Spinner>(spinner)!!.setSelection(index)
        shadowOf(Looper.getMainLooper()).idle()
    }

    @Test fun `Apple iPhone 11 Happ and generated HWID apply and survive reopening`() {
        val dialog = open()
        select(dialog, R.id.identityPlatform, DeviceProfiles.groups.indexOf("apple"))
        select(dialog, R.id.identityDevice, DeviceProfiles.inGroup("apple").indexOfFirst { it.id == "iphone11" })
        select(dialog, R.id.identityClient, 2) // Keep current, system default, Happ.
        dialog.findViewById<View>(R.id.identityGenerateHwid)!!.performClick()
        val hwid = dialog.findViewById<EditText>(R.id.identityHwid)!!.text.toString()
        assertTrue(hwid.matches(Regex("[0-9A-F-]{36}")))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
        val prefs = PrefsManager.getSafePrefs(activity)
        assertEquals("iphone11", prefs.getString("request_device", null))
        assertEquals(hwid, prefs.getString("custom_hwid", null))
        assertEquals("Happ/4.10.1", prefs.getString("custom_user_agent", null))
        assertEquals(hwid, open().findViewById<EditText>(R.id.identityHwid)!!.text.toString())
    }

    @Test fun `Windows model and OS version can be edited`() {
        val dialog = open()
        select(dialog, R.id.identityPlatform, DeviceProfiles.groups.indexOf("windows"))
        dialog.findViewById<EditText>(R.id.identityModel)!!.setText("Dell XPS 13")
        dialog.findViewById<EditText>(R.id.identityOsVersion)!!.setText("10")
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
        val prefs = PrefsManager.getSafePrefs(activity)
        assertEquals("Windows", DeviceProfiles.find(prefs.getString("request_device", null)).os)
        assertEquals("10", prefs.getString("identity_version_windows", null))
        assertEquals("Dell XPS 13", prefs.getString("identity_model_windows", null))
    }

    @Test fun `cancel leaves the active identity unchanged`() {
        val dialog = open()
        select(dialog, R.id.identityPlatform, DeviceProfiles.groups.indexOf("apple"))
        dialog.findViewById<View>(R.id.identityGenerateHwid)!!.performClick()
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick()
        assertFalse(PrefsManager.getSafePrefs(activity).contains("request_device"))
        assertEquals("", activity.findViewById<EditText>(R.id.inputHwid).text.toString())
    }
}
