package com.jackrockz.root.tickets

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.commons.RxBaseActivity
import com.jackrockz.root.MainActivity
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.activity_ticket_detail.*

class TicketDetailActivity : RxBaseActivity() {
    val ticket = MyApplication.instance.currentTicket
    var isFromPayment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_detail)

        isFromPayment = intent.getBooleanExtra(GlobalConstants.PREFS_ISFROMPAYMENT, false)
        if (isFromPayment) {
            Utils.showAlertDialog(this, "Thank You!", getString(R.string.string_thank_you))
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        drawQrCode()
        Init()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isFromPayment) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(GlobalConstants.PREFS_ISFROMPAYMENT, true)
            startActivity(intent)
            finish()
            return
        }
        super.onBackPressed()
    }

    fun drawQrCode() {
        val writer = QRCodeWriter()

        val bitMatrix = writer.encode(ticket.checkin_url, BarcodeFormat.QR_CODE, 256, 256)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE);
            }
        }
        imgQrCode.setImageBitmap(bmp);
    }

    fun Init() {
        txtName.text = String.format("%s %s", ticket.first_name, ticket.last_name)
        txtQuantity.text = String.format("%dx", ticket.quantity)
        txtTitle.text = ticket.event.title
        txtLocation.text = ticket.event.venue.name
        txtDate.text = Utils.getStringFromTwoDates(ticket.event.start_date, ticket.event.end_date)
    }
}
