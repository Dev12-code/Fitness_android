package com.cbi.app.trs.features.fragments.setting.notification

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.core.platform.OnLoadmoreListener
import com.cbi.app.trs.data.entities.NotificationData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.notification.NotificationEntity
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.notification.MarkNotification
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject

class NotificationFragment : DarkBaseFragment(), OnItemClickListener, OnLoadmoreListener {
    override fun layoutId() = R.layout.fragment_notification

    private val pagingParam: PagingParam = PagingParam()
    private var markPosition: Int = -1
    lateinit var notificationViewModel: NotificationViewModel
    private var currentPage = 1
    private var totalPage = 0

    @Inject
    lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        notificationViewModel = viewModel(viewModelFactory) {
            observe(notificationData, ::onReceiveNotification)
            observe(markNotificationData, ::onReceiveMarkNotification)
            failure(failureData, ::handleFailure)
        }
    }

    private fun onReceiveMarkNotification(data: BaseEntities?) {
        hideProgress()
        if (data == null) return
        if (markPosition != -1) {
            if (data.isSuccess) {
                notificationAdapter.collection[markPosition].notification_is_readed = true
                notificationAdapter.notifyDataSetChanged()
                markPosition = -1
            }
        }
    }

    private fun onReceiveNotification(data: NotificationEntity.Data?) {
        hideProgress()
        if (data == null) return
        if (currentPage == 1) notificationAdapter.collection.clear()
        notificationAdapter.collection.addAll(data.data)
        notificationAdapter.notifyDataSetChanged()
        currentPage = data.page
        totalPage = data.max_page
        notificationAdapter.onLoadmoreListener = this

        if (notificationAdapter.collection.isEmpty()) {
            empty_text_2.visibility = View.VISIBLE
        } else {
            empty_text_2.visibility = View.GONE
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
    }

    private fun loadData() {
        showProgress()
        notificationViewModel.getNotification(userDataCache.get()?.user_token?.userID, pagingParam)
    }

    override fun onReloadData() {
        currentPage = 1
        totalPage = 0
        pagingParam.page = 1
        notificationAdapter.collection = ArrayList()
        loadData()
    }

    private fun initView() {
        notification_recylerview.layoutManager = LinearLayoutManager(activity)
        notification_recylerview.adapter = notificationAdapter.apply {
            onItemClickListener = this@NotificationFragment
            onLoadmoreListener = this@NotificationFragment
        }
        back_btn.setOnClickListener { pop(activity) }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }

    override fun onItemClick(item: Any?, position: Int) {
        markPosition = position
        showProgress()
        if (item is NotificationData && !item.notification_is_readed) {
            notificationViewModel.markNotification(Pair(userDataCache.get()?.user_token?.userID, MarkNotification.Params(item.notification_id)))
        }
    }

    override fun onLoadMore() {
        if (currentPage >= totalPage) {
            notificationAdapter.onLoadmoreListener = null
            return
        }
        pagingParam.page++
        currentPage++
        loadData()
    }
}