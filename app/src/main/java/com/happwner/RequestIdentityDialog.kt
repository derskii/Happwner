package com.happwner

import android.content.SharedPreferences
import android.os.Build
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object RequestIdentityDialog {
    data class Selection(val profile: String, val model: String, val version: String, val hwid: String, val ua: String)

    fun show(activity: AppCompatActivity, prefs: SharedPreferences, currentHwid: String, currentUa: String,
             agents: List<String>, onApply: (Selection) -> Unit) {
        val view = activity.layoutInflater.inflate(R.layout.dialog_request_identity, null)
        val platform = view.findViewById<Spinner>(R.id.identityPlatform)
        val device = view.findViewById<Spinner>(R.id.identityDevice)
        val client = view.findViewById<Spinner>(R.id.identityClient)
        val model = view.findViewById<EditText>(R.id.identityModel)
        val version = view.findViewById<EditText>(R.id.identityOsVersion)
        val hwid = view.findViewById<EditText>(R.id.identityHwid)
        val selected = DeviceProfiles.find(prefs.getString("request_device", "android"))
        val currentGroup = DeviceProfiles.groups.indexOf(selected.group)
        var profiles = DeviceProfiles.inGroup(selected.group)
        var activeId: String? = null

        fun adapter(labels: List<String>) = ArrayAdapter(activity, android.R.layout.simple_spinner_item, labels).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        fun listener(action: (Int) -> Unit) = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) = action(position)
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        fun fillProfile(index: Int) {
            val p = profiles.getOrNull(index) ?: return
            if (activeId == p.id) return
            activeId = p.id
            model.setText(prefs.getString("identity_model_${p.id}", if (p.id == "android") Build.MODEL else p.model))
            version.setText(prefs.getString("identity_version_${p.id}", if (p.id == "android") Build.VERSION.RELEASE else p.version))
            hwid.setText(if (p.id == selected.id) currentHwid else prefs.getString("identity_hwid_${p.id}", ""))
        }
        fun fillDevices(groupIndex: Int) {
            profiles = DeviceProfiles.inGroup(DeviceProfiles.groups[groupIndex])
            activeId = null
            device.adapter = adapter(profiles.map {
                val label = if (it.id == "android") activity.getString(R.string.identity_current_device, Build.MODEL) else it.model
                "$label · ${it.os} ${it.version}".trim()
            })
            val index = profiles.indexOfFirst { it.id == selected.id }.coerceAtLeast(0)
            device.setSelection(index)
            fillProfile(index)
        }

        platform.adapter = adapter(activity.resources.getStringArray(R.array.identity_platforms).toList())
        platform.setSelection(currentGroup)
        fillDevices(currentGroup)
        device.onItemSelectedListener = listener(::fillProfile)
        platform.onItemSelectedListener = listener { index ->
            if (profiles.firstOrNull()?.group != DeviceProfiles.groups[index]) fillDevices(index)
        }
        val uaValues = listOf(currentUa) + agents
        client.adapter = adapter(listOf(activity.getString(R.string.identity_keep_ua, currentUa.ifBlank { "—" })) +
            agents.map { it.ifEmpty { activity.getString(R.string.ua_system_default) } })
        view.findViewById<View>(R.id.identityGenerateHwid).setOnClickListener {
            val generate = { hwid.setText(HwidGenerator.generate(activeId ?: "android")) }
            if (hwid.text.isNotBlank()) {
                MaterialAlertDialogBuilder(activity).setTitle(R.string.hwid_replace_title)
                    .setMessage(R.string.hwid_replace_message)
                    .setPositiveButton(R.string.hwid_generate) { _, _ -> generate() }
                    .setNegativeButton(android.R.string.cancel, null).show()
            } else generate()
        }
        val dialog = MaterialAlertDialogBuilder(activity).setTitle(R.string.device_select_title)
            .setView(view).setPositiveButton(R.string.identity_apply, null)
            .setNegativeButton(android.R.string.cancel, null).create()
        dialog.setOnShowListener {
            dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if (model.text.isBlank() || version.text.isBlank()) {
                    if (model.text.isBlank()) model.error = activity.getString(R.string.identity_required)
                    if (version.text.isBlank()) version.error = activity.getString(R.string.identity_required)
                } else {
                    onApply(Selection(activeId ?: "android", model.text.toString().trim(), version.text.toString().trim(),
                        hwid.text.toString().trim(), uaValues[client.selectedItemPosition]))
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
}
