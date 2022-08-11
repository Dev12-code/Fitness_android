package com.cbi.app.trs.features.fragments.setting.user_setting

import android.os.Bundle
import android.view.View
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.close
import com.cbi.app.trs.core.extension.failure
import com.cbi.app.trs.core.extension.observe
import com.cbi.app.trs.core.extension.viewModel
import com.cbi.app.trs.core.platform.DarkBaseFragment
import com.cbi.app.trs.core.platform.OnItemClickListener
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.usecases.user.PostUserProfile
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.fragments.search.SearchTagView
import com.cbi.app.trs.features.viewmodel.UserProfileViewModel
import com.google.android.flexbox.FlexDirection
import kotlinx.android.synthetic.main.fragment_user_setting.*

class SettingUserFragment : DarkBaseFragment(), OnItemClickListener {
    override fun layoutId() = R.layout.fragment_user_setting
    private var userProfile: UserData.UserProfile? = null

    val equipment_tags: ArrayList<SystemData.Equipment> = ArrayList()

    lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        userProfileViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(systemEquipment, ::onReceiveSystemEquipment)
            observe(userProfile, ::onReceiveUserProfile)
            observe(updateUserProfile, ::onReceiveUpdateProfile)
        }
    }

    override fun onReloadData() {
        userProfileViewModel.loadSystemEquipment()
    }

    private fun onReceiveUpdateProfile(baseEntities: BaseEntities?) {
        hideProgress()
        if (baseEntities == null) return
        if (baseEntities.isSuccess) close()
    }

    private fun onReceiveUserProfile(userProfile: UserData.UserProfile?) {
        hideProgress()
        if (userProfile == null) return
        this.userProfile = userProfile
        loadUserEquipment()
    }

    private fun loadUserEquipment() {
        for (childIndex in 0 until equipment_tags_flexbox.childCount) {
            val childTag = equipment_tags_flexbox.getChildAt(childIndex).tag as SystemData.Equipment
            if (childTag.equipment_title.contains("Lacrosse Ball") || childTag.equipment_title.contains("Foam Roller")) {
                //always checked 2 equipment above
                (equipment_tags_flexbox.getChildAt(childIndex) as SearchTagView).setCheck(true, isEnabled = false)
            } else {
                (equipment_tags_flexbox.getChildAt(childIndex) as SearchTagView).setCheck(false, isEnabled = true)
            }
            userProfile?.user_settings?.my_equipments?.let {
                for (userTag in it) {
                    if (childTag.equipment_title.contains("Lacrosse Ball") || childTag.equipment_title.contains("Foam Roller")) {
                        //always checked 2 equipment above
                        (equipment_tags_flexbox.getChildAt(childIndex) as SearchTagView).setCheck(true, isEnabled = false)
                        continue
                    }
                    if (childTag.equipment_id == userTag.equipment_id) {
                        (equipment_tags_flexbox.getChildAt(childIndex) as SearchTagView).setCheck(true, isEnabled = true)
                    }
                }
            }
        }
    }

    private fun onReceiveSystemEquipment(list: List<SystemData.Equipment>?) {
        equipment_tags.clear()
        if (list != null) {
            equipment_tags.addAll(list)
            loadEquipment()
        }
    }

    private fun loadEquipment() {
        equipment_tags_flexbox.flexDirection = FlexDirection.ROW
        equipment_tags_flexbox.removeAllViews()
        for (tag in equipment_tags) {
            equipment_tags_flexbox.addView(activity?.let {
                SearchTagView(it).apply {
                    setText(tag.equipment_title)
                    this.tag = tag
                    onItemClickListener = this@SettingUserFragment
                }
            })
        }

        showProgress()
        userProfileViewModel.getUserProfile(userID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (equipment_tags.isEmpty()) {
            showProgress()
            userProfileViewModel.loadSystemEquipment()
        } else {
            loadEquipment()
        }
    }

    private fun initView() {
        clear_all.setOnClickListener {
            //show popup
            DialogAlert()
                    .setTitle(getString(R.string.message))
                    .setMessage(getString(R.string.message_equipment_popup_clear_all))
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .onPositive {
                        for (tag in 0 until equipment_tags_flexbox.childCount) {
                            if (!(equipment_tags_flexbox.getChildAt(tag) as SearchTagView).getEnable()) {
                                continue
                            }
                            (equipment_tags_flexbox.getChildAt(tag) as SearchTagView).setCheck(false)
                        }
                    }
                    .show(requireContext())
        }

        back_btn.setOnClickListener {
            pop(activity)
        }
        save_btn.setOnClickListener { save() }
    }

    private fun save() {
        val result = ArrayList<SystemData.Equipment>()
        for (childIndex in 0 until equipment_tags_flexbox.childCount) {
            val childView = equipment_tags_flexbox.getChildAt(childIndex) as SearchTagView
            if (childView.isChecked) {
                result.add(childView.tag as SystemData.Equipment)
            }
        }
        this.userProfile?.user_settings?.my_equipments = result
        showProgress()
        userProfileViewModel.updateUserProfile(Pair(userID, PostUserProfile.Param(this.userProfile)))
//        userProfileViewModel.updateUserProfile(
//                Pair(userID, PostUserProfile.Param(
//                        UserData.UserProfile(user_settings = UserSetting(my_equipments = result))
//                )
//                )
//        )
    }

    override fun onItemClick(item: Any?, position: Int) {
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }
}
