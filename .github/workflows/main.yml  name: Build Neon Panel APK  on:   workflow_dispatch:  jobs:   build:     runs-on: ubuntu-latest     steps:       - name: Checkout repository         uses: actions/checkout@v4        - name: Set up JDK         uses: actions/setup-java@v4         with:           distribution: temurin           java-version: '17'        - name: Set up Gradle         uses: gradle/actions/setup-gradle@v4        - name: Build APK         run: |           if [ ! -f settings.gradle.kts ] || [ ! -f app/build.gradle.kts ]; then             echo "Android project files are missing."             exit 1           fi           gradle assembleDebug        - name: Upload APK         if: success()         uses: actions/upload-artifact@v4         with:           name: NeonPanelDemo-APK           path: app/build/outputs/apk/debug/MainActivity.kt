package com.example.neonpaneldemo

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.security.SecureRandom

class MainActivity : Activity() {
    private val bg = Color.rgb(13, 15, 22)
    private val card = Color.rgb(25, 28, 39)
    private val red = Color.rgb(255, 65, 85)
    private val white = Color.rgb(245, 246, 250)
    private val muted = Color.rgb(165, 171, 190)
    private val validDemoKeys = mutableSetOf<String>()
    private lateinit var root: LinearLayout
    private lateinit var keyInput: EditText
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLogin()
    }

    private fun rounded(color: Int, radius: Float = 18f): GradientDrawable =
        GradientDrawable().apply { setColor(color); cornerRadius = radius }

    private fun label(text: String, size: Float, color: Int = white, bold: Boolean = false): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
            gravity = Gravity.CENTER
        }

    private fun button(text: String, action: () -> Unit): Button =
        Button(this).apply {
            this.text = text
            textSize = 14f
            isAllCaps = false
            setTextColor(white)
            background = rounded(red, 14f)
            setPadding(12, 8, 12, 8)
            setOnClickListener { action() }
        }

    private fun column(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(22, 22, 22, 24)
        background = rounded(card, 24f)
    }

    private fun setup(content: View) {
        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
            isFillViewport = true
            addView(content)
        }
        setContentView(scroll)
    }

    private fun showLogin() {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 36, 24, 36)
            setBackgroundColor(bg)
        }
        page.addView(label("NEON PANEL", 30f, red, true), lp(-1, -2, 0, 0, 0, 8))
        page.addView(label("PREMIUM PANEL • DEMO", 13f, muted, true), lp(-1, -2, 0, 0, 0, 26))
        val box = column()
        box.addView(label("Demo Key Login", 22f, white, true), lp(-1, -2, 0, 4, 0, 14))
        keyInput = EditText(this).apply {
            hint = "Enter a generated demo key"
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 15f
            setSingleLine(true)
            background = rounded(bg, 12f)
            setPadding(16, 14, 16, 14)
        }
        box.addView(keyInput, lp(-1, -2, 0, 8, 0, 10))
        box.addView(button("LOGIN", ::login), lp(-1, -2, 0, 8, 0, 8))
        box.addView(button("GENERATE TEST KEY", {
            val key = generateKey()
            validDemoKeys.add(key)
            keyInput.setText(key)
            Toast.makeText(this, "Demo key created on this device", Toast.LENGTH_SHORT).show()
        }), lp(-1, -2, 0, 8, 0, 8))
        box.addView(label("Demo only • No VPN • No real premium service", 12f, muted), lp(-1, -2, 0, 16, 0, 8))
        page.addView(box, lp(-1, -2))
        setup(page)
    }

    private fun login() {
        val entered = keyInput.text.toString().trim().uppercase()
        if (entered.isNotEmpty() && validDemoKeys.contains(entered)) {
            showPanel()
        } else {
            Toast.makeText(this, "Invalid demo key. Generate a key first on this device.", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateKey(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val random = SecureRandom()
        val raw = (1..12).map { chars[random.nextInt(chars.length)] }.joinToString("")
        return "DEMO-${raw.chunked(4).joinToString("-")}"
    }

    private fun showPanel() {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 30, 22, 28)
            setBackgroundColor(bg)
        }
        page.addView(label("NEON PANEL", 29f, red, true), lp(-1, -2, 0, 0, 0, 4))
        page.addView(label("ORIGINAL DEMO INTERFACE", 12f, muted, true), lp(-1, -2, 0, 0, 0, 22))
        val box = column()
        box.addView(label("Premium Panel", 24f, white, true), lp(-1, -2, 0, 8, 0, 4))
        box.addView(label("Status: DEMO ACCESS", 14f, Color.rgb(98, 230, 160), true), lp(-1, -2, 0, 0, 0, 20))
        status = label("Choose a demo action below.", 14f, muted)
        box.addView(status, lp(-1, -2, 0, 0, 0, 14))
        box.addView(button("FEATURE A • Toggle", {
            status.text = "Feature A: " + if (status.text.toString().contains("ON")) "OFF" else "ON"
        }), lp(-1, -2, 0, 6, 0, 6))
        box.addView(button("FEATURE B • Run Demo", {
            status.text = "Feature B ran successfully (local demo)."
        }), lp(-1, -2, 0, 6, 0, 6))
        box.addView(button("GENERATE ANOTHER TEST KEY", {
            val key = generateKey()
            validDemoKeys.add(key)
            status.text = "New local demo key:\n$key"
        }), lp(-1, -2, 0, 6, 0, 6))
        box.addView(button("LOG OUT", ::showLogin), lp(-1, -2, 0, 6, 0, 6))
        box.addView(label("All controls are local demo features. No VPN or external server is used.", 12f, muted), lp(-1, -2, 0, 18, 0, 6))
        page.addView(box, lp(-1, -2))
        setup(page)
    }

    private fun lp(width: Int, height: Int, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(width, height).apply { setMargins(left, top, right, bottom) }
}
