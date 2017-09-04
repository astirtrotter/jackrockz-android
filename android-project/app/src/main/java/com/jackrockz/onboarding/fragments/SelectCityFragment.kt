package com.jackrockz.onboarding.fragments

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jackrockz.R
import com.jackrockz.api.CityModel
import com.jackrockz.commons.RxBaseFragment
import com.jackrockz.onboarding.WelcomeActivity
import com.jackrockz.onboarding.adapter.CityAdapter
import com.jackrockz.utils.GlobalConstants
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.fragment_select_city.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SelectCityFragment : RxBaseFragment(), View.OnClickListener {
    var listItems = ArrayList<CityModel>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_select_city, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar.visibility = View.VISIBLE
        val subscription = apiManager.getCities().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { aryCities ->
                            try {
                                progressBar.visibility = View.GONE

                                listItems = aryCities as ArrayList<CityModel>
                                recycler_view.adapter = CityAdapter(this, listItems)
                            } catch (ignored: Exception) {

                            }
                        },
                        { e ->
                            try {
                                progressBar.visibility = View.GONE
                                Utils.showToast(activity, e.message ?: "Network connection error.")
                            } catch (ignored: Exception) {
                            }
                        }
                )
        subscriptions.add(subscription)

        recycler_view.apply {
            setHasFixedSize(true)
            val linearLayout = LinearLayoutManager(context)
            linearLayout.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayout
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = CityAdapter(this@SelectCityFragment, listItems)
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.itemCity) {
            Utils.showLoading(activity)

            val city = v.tag as CityModel

            val subscription = apiManager.putMe(city = city.id.toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { user ->
                                Utils.saveObject(GlobalConstants.PREFS_USER, user)

                                Utils.hideLoading()
                                (activity as WelcomeActivity).changeFragment(ArrivalDateFragment())
                            },
                            { e ->
                                Utils.hideLoading()
                                Utils.showToast(activity, e.message ?: "Network connection error.")
                            }
                    )
            subscriptions.add(subscription)
        }
    }
}
