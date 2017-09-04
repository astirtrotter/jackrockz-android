package com.jackrockz.root.tickets

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jackrockz.MyApplication
import com.jackrockz.R
import com.jackrockz.api.TicketModel
import com.jackrockz.commons.RxBaseFragment
import com.jackrockz.utils.Utils
import kotlinx.android.synthetic.main.fragment_my_tickets.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MyTicketsFragment : RxBaseFragment(), View.OnClickListener {
    var listItems = ArrayList<TicketModel>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_my_tickets, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null)
            return

        val subscription = apiManager.getTickets().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { aryTickets ->
                            try {
                                progressBar.visibility = View.GONE

                                listItems = aryTickets as ArrayList<TicketModel>
                                recycler_view.adapter = TicketsAdapter(this, listItems)
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
//            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = TicketsAdapter(this@MyTicketsFragment, listItems)
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.itemTicket -> {
                MyApplication.instance.currentTicket = v.tag as TicketModel
                startActivity(Intent(activity, TicketDetailActivity::class.java))
            }
        }
    }

}
