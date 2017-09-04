package com.jackrockz.root.events

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.api.EventModel
import com.jackrockz.api.GalleryModel
import com.jackrockz.api.VisitorModel
import com.jackrockz.commons.RxBaseActivity
import com.jackrockz.commons.extensions.loadImg
import com.jackrockz.root.ambassador.AmbassadorActivity
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.description.*
import kotlinx.android.synthetic.main.image_slider.*
import kotlinx.android.synthetic.main.location.*
import kotlinx.android.synthetic.main.review_layout.*
import kotlinx.android.synthetic.main.who_layout.*
import java.util.*

class EventDetailActivity : RxBaseActivity(), View.OnClickListener, OnMapReadyCallback {

    lateinit var event: EventModel

    var timer: Timer? = null
    val PERIOD_MS: Long = 4000 // time in milliseconds between successive task executions.
    val handler = Handler()
    var Update = Runnable {  }
    var previousState = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        event = MyApplication.instance.currentEvent

        supportActionBar!!.title = event.venue.name
        supportActionBar!!.subtitle = Utils.getStringFromTwoDates(event.start_date, event.end_date)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        txtRegularPrice.paintFlags = txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        btnGet.setOnClickListener(this)

        txtTitle.text = event.title
        txtSubtitle.text = event.supplementary_subtitle
        txtViewsCount.text = event.views_count.toString() + "x"
        txtExpCount.text = event.experience_count.toString() + "x"
        txtGuestCount.text = event.guestlist_count.toString() + "x"
        txtPrice.text = event.price
        txtRegularPrice.text = event.regular_price
        txtDescription.text = event.description

        if (event.visitors != null) {
            layout_who.visibility = View.VISIBLE
            who_recycler_view.apply {
                setHasFixedSize(true)
                val linearLayout = LinearLayoutManager(context)
                linearLayout.orientation = LinearLayoutManager.HORIZONTAL
                layoutManager = linearLayout
                adapter = WhoAdapter(this@EventDetailActivity, event.visitors as ArrayList<VisitorModel>)
            }
        }

        if (event.reviews.isNotEmpty()) {
            val review = event.reviews.last()
            layout_review.visibility = View.VISIBLE
            review_txtName.text = review.name
            review_txtDescription.text = review.body
            review_rating.rating = review.rating.toFloat()
            review_imgView.loadImg(review.image.medium)
        }

        val galleryList = ArrayList<GalleryModel>()

        if (event.image != null) {
            galleryList.add(GalleryModel(event.image))
        }
        if (event.gallery?.items != null) {
            galleryList.addAll(event.gallery!!.items!!)
        }
        if (galleryList.size > 0) {
            viewpager.adapter = ImagePagerAdapter(this, galleryList)
            indicator.setViewPager(viewpager)

            if (galleryList.size > 1) {
                Update = Runnable {
                    viewpager.setCurrentItem(if (viewpager.currentItem + 1 == viewpager.adapter.count) 0 else viewpager.currentItem + 1, true)
                }

                viewpager.setOnPageChangeListener(object: ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    }

                    override fun onPageSelected(position: Int) {
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                            timer?.cancel()
                            timer?.purge()
                        }
                        if (previousState == ViewPager.SCROLL_STATE_DRAGGING && state == ViewPager.SCROLL_STATE_SETTLING) {
                            timer = Timer() // This will create a new Thread
                            timer?.schedule(object : TimerTask() { // task to be scheduled
                                override fun run() {
                                    handler.post(Update)
                                }
                            }, PERIOD_MS, PERIOD_MS)
                        }
                        previousState = state;
                    }

                })
            }
        }

        txtVenueName.text = event.venue.name
        txtVenueCity.text = event.venue.city

        if (event.note != null) {
            txtVenueNote.text = event.note
        } else {
            txtVenueNote.visibility = View.GONE
        }
        if (event.venue.address != null) {
            txtVenueAddress.text = event.venue.address
        } else {
            txtVenueAddress.visibility = View.GONE
        }

        initMap()
    }

    override fun onResume() {
        super.onResume()

        if (viewpager.adapter.count > 1) {
            timer = Timer() // This will create a new Thread
            timer?.schedule(object : TimerTask() { // task to be scheduled
                override fun run() {
                    handler.post(Update)
                }
            }, PERIOD_MS, PERIOD_MS)
        }

        if (MyApplication.instance.currentUser.ambassador == null) {
            btnGet.text = "Unlock"
        } else if (event.is_sold_out) {
            btnGet.text = "Sold Out"
            btnGet.isClickable = false
        } else {
            btnGet.text = "Get on guestlist"
        }
    }

    override fun onPause() {
        super.onPause()

        timer?.cancel()
        timer?.purge()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnGet -> {
                if (btnGet.text == "Get on guestlist") {
                    startActivity(Intent(this, EventPaymentActivity::class.java))
                    this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                } else if (btnGet.text == "Sold Out") {

                } else {
                    startActivity(Intent(this, AmbassadorActivity::class.java))
                }
            }
            R.id.imgWho -> {
                val visitor = v.tag as VisitorModel
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(visitor.link)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
    }

    fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this);
    }

    override fun onMapReady(p0: GoogleMap) {
        if (event.venue.latitude == null || event.venue.longitude == null)
            return

        val position = LatLng(event.venue.latitude!!, event.venue.longitude!!)

        val cameraPosition = CameraPosition.Builder()
                .target(position)
                .zoom(10f)
                .build()
        p0.apply {
            addMarker(MarkerOptions().position(position).title(event.venue.name))
            animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

    }
}
