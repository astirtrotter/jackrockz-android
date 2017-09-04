package com.jackrockz.root.events

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.api.EventModel
import com.jackrockz.api.PaymentModel
import com.jackrockz.api.PaymentsModel
import com.jackrockz.api.TicketModel
import com.jackrockz.commons.RxBaseActivity
import com.jackrockz.root.ambassador.AmbassadorActivity
import com.jackrockz.root.tickets.TicketDetailActivity
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.activity_event_payment.*
import org.json.JSONObject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class EventPaymentActivity : RxBaseActivity(), View.OnClickListener {
    val event = MyApplication.instance.currentEvent

    var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_payment)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val url = intent.data
        if (url != null) {
            OnPaymentSucceed(url)
        }

        txtTitle.text = event.title + (" - " + event.subtitle).takeIf { event.subtitle != null }
        txtDate.text = Utils.convertStringToDate(event.start_date, "MM/dd/yy")
        txtVenue.text = event.venue.name + ", " + event.venue.city
        txtEmail.hint = MyApplication.instance.currentUser.email

        txtQuantity.setOnClickListener(this)
        btnPurchase.setOnClickListener(this)

        CalcPrices()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.txtQuantity -> OnQuantity()
            R.id.btnPurchase -> OnPurchase()
        }
    }

    fun OnQuantity() {
        val d = Dialog(this)
        d.setTitle("Select Quantity")
        d.setContentView(R.layout.dialog_number_picker)

        val np = d.findViewById(R.id.numberPicker1) as NumberPicker
        val button1 = d.findViewById(R.id.button1)
        val button2 = d.findViewById(R.id.button2)

        np.maxValue = 10
        np.minValue = 1
        np.value = quantity
        np.wrapSelectorWheel = false
        button1.setOnClickListener {
            quantity = np.value
            CalcPrices()
            d.dismiss()
        }
        button2.setOnClickListener{
            d.dismiss()
        }
        d.show()
    }

    fun CalcPrices() {
        txtTotalPrice.text = "€" + String.format("%.2f", event.raw_price * quantity)
        txtPayDoor.text = "€" + String.format("%.2f", (event.raw_price - event.raw_prepayment_price) * quantity)
        txtPayNow.text = "€" + String.format("%.2f", event.raw_prepayment_price * quantity)
        txtQuantity.setText(quantity.toString() + "x")

        listOf(txtTotalPrice, txtPayDoor, txtPayNow).forEach { it.text = it.text.toString().replace(".", ",") }
    }

    fun OnPurchase() {
        if (txtPhone.text.isEmpty()) {
            Utils.showAlertDialog(this, "Oops...", resources.getString(R.string.alert_phone))
            return
        }

        Utils.showLoading(this)
        val subscription = apiManager.postPayment(event.id.toString(), event.id.toString(), "jackrockz://payments/{token}", quantity)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { obj ->
                            Utils.hideLoading()

                            if (obj is PaymentModel) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(obj.payment_url)
                                startActivity(intent)
                            } else if (obj is TicketModel) {
                                Utils.hideLoading()
                                MyApplication.instance.currentTicket = obj

                                val intent = Intent(this, TicketDetailActivity::class.java)
                                intent.putExtra(GlobalConstants.PREFS_ISFROMPAYMENT, true)
                                startActivity(intent)
                                finish()
                            }
                        },
                        { e ->
                            Utils.hideLoading()
                            Utils.showToast(this, "Failed payment.")
                        }
                )
        subscriptions.add(subscription)
    }

    fun OnPaymentSucceed(url: Uri) {
        Utils.showLoading(this)

        val segments = url.path.split("/")
        val paymentToken = segments[segments.size - 1]

        val subscription = apiManager.getTicketToken(paymentToken).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { ticketToken ->
                            val subscription = apiManager.getTicket(ticketToken).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe (
                                            { ticket ->
                                                Utils.hideLoading()
                                                MyApplication.instance.currentTicket = ticket

                                                val intent = Intent(this, TicketDetailActivity::class.java)
                                                intent.putExtra(GlobalConstants.PREFS_ISFROMPAYMENT, true)
                                                startActivity(intent)
                                                finish()
                                            },
                                            { e ->
                                                Utils.hideLoading()
                                                Utils.showToast(this, "Failed payment.")
                                            }
                                    )
                            subscriptions.add(subscription)
                        },
                        { e ->
                            Utils.hideLoading()
                            Utils.showToast(this, "Failed payment.")
                        }
                )
        subscriptions.add(subscription)
    }
}
