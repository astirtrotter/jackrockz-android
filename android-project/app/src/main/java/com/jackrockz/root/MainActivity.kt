package com.jackrockz.root

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.commons.RxBaseActivity
import com.jackrockz.onboarding.WelcomeActivity
import com.jackrockz.root.ambassador.AmbassadorActivity
import com.jackrockz.root.events.EventsFragment
import com.jackrockz.root.support.SupportFragment
import com.jackrockz.root.tickets.MyTicketsFragment
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class MainActivity : RxBaseActivity() {
    @JvmField var navItemIndex = 0
    @JvmField var CURRENT_TAG = "hot events"
    var mHandler: Handler = Handler()

    var activityTitles = arrayOf<String>()

    var isFromPayment = false

    var onBackPressedListener: OnBackPressedListener? = null
    interface OnBackPressedListener {
        fun doBack()
    }
    var callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        isFromPayment = intent.getBooleanExtra(GlobalConstants.PREFS_ISFROMPAYMENT, false)

        mHandler = Handler()
        activityTitles = resources.getStringArray(R.array.nav_item_activity_titles)

        setUpNavigationView()

        if (isFromPayment) {
            toolbar.imgLogo.visibility = View.GONE
            navItemIndex = 2
            CURRENT_TAG = "my tickets"
            loadHomeFragment()
        } else if (savedInstanceState == null) {
            navItemIndex = 0
            CURRENT_TAG = "hot events"
            loadHomeFragment()
        }

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
            }

            override fun onError(p0: FacebookException?) {
                Utils.showToast(this@MainActivity, "Unfortunately facebook login failed.")
            }

            override fun onSuccess(p0: LoginResult?) {
                Utils.showLoading(this@MainActivity)
                ProcessToken(p0!!.accessToken.token)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        nav_view.menu.findItem(R.id.nav_code).isVisible = MyApplication.instance.currentUser.ambassador == null
        nav_view.menu.findItem(R.id.nav_login).isVisible = Utils.loadData(GlobalConstants.PREFS_IS_TAKE_LOOK) == "true"
        nav_view.menu.findItem(R.id.nav_logout).isVisible = !nav_view.menu.findItem(R.id.nav_login).isVisible
    }

    fun getHomeFragment(): Fragment = when (navItemIndex) {
        0 -> EventsFragment(true)
        1 -> EventsFragment()
        2 -> MyTicketsFragment()
        3 -> SupportFragment()
        else -> EventsFragment(true)
    }

    fun loadHomeFragment() {
        selectNavMenu()
        setToolbarTitle()

        if (supportFragmentManager.findFragmentByTag(CURRENT_TAG) != null) {
            drawer_layout.closeDrawers()
            return
        }

        val mPendingRunnable = Runnable {
            val fragment = getHomeFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(
                    R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_popup_enter, R.anim.abc_popup_exit)
            ft.replace(R.id.activity_base_content, fragment, CURRENT_TAG)
            ft.commitAllowingStateLoss()
        }
        mHandler.post(mPendingRunnable)
        drawer_layout.closeDrawers()
    }

    fun setUpNavigationView() {
        var string = ""
        if (MyApplication.instance.currentUser.first_name != null)
            string = string + MyApplication.instance.currentUser.first_name + " "

        if (MyApplication.instance.currentUser.last_name != null)
            string += MyApplication.instance.currentUser.last_name

        (nav_view.getHeaderView(0).findViewById(R.id.txtName) as TextView).text = string
        nav_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_hot_events -> {
                    navItemIndex = 0
                    toolbar.imgLogo.visibility = View.VISIBLE
                    CURRENT_TAG = "hot events"
                }
                R.id.nav_events -> {
                    navItemIndex = 1
                    toolbar.imgLogo.visibility = View.VISIBLE
                    CURRENT_TAG = "events"
                }
                R.id.nav_mytickets -> {
                    navItemIndex = 2
                    toolbar.imgLogo.visibility = View.GONE
                    CURRENT_TAG = "my tickets"
                }
                R.id.nav_support -> {
                    toolbar.imgLogo.visibility = View.GONE
                    navItemIndex = 3
                    CURRENT_TAG = "support"
                }
                R.id.nav_code -> {
                    drawer_layout.closeDrawer(Gravity.LEFT)
                    startActivity(Intent(this, AmbassadorActivity::class.java))
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_logout -> {
                    LoginManager.getInstance().logOut()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_login -> {
                    drawer_layout.closeDrawers()
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
                    return@setNavigationItemSelectedListener true
                }
            }

            menuItem.isChecked = !menuItem.isChecked

            loadHomeFragment()
            true
        }

        val actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
            }
        }

        drawer_layout.setDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()
    }

    fun setToolbarTitle() {
        supportActionBar!!.title = activityTitles[navItemIndex]
    }

    fun selectNavMenu() {
        nav_view.menu.getItem(navItemIndex).isChecked = true
    }

    override fun onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener!!.doBack()
            return
        }

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
            return
        }
        
        super.onBackPressed()
    }

    fun ProcessToken(token: String) {
        val subscription = apiManager.putMe(token = token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { user ->
                            Utils.saveObject(GlobalConstants.PREFS_USER, user)
                            Utils.saveData(GlobalConstants.PREFS_IS_TAKE_LOOK, "false")
                            nav_view.menu.findItem(R.id.nav_login).isVisible = Utils.loadData(GlobalConstants.PREFS_IS_TAKE_LOOK) == "true"
                            nav_view.menu.findItem(R.id.nav_logout).isVisible = !nav_view.menu.findItem(R.id.nav_login).isVisible

                            Utils.hideLoading()
                            setUpNavigationView()

                            navItemIndex = 0
                            CURRENT_TAG = "hot events"
                            loadHomeFragment()
                        },
                        { e ->
                            Utils.hideLoading()
                            Utils.showToast(this, "Network connection error.")
                        }
                )
        subscriptions.add(subscription)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
