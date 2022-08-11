package com.cbi.app.trs.data.entities

data class UserData(var user_token: UserToken? = null, var user_profile: UserProfile? = null) {
    data class UserToken(val userID: Int, var jwt: String, val refresh_token: String, val expire_date: Long)
    data class UserProfile(var user_avatar: String? = null, var first_name: String? = null, var last_name: String? = null, var email: String? = null, var dob: Long? = null, var user_plan: String? = null,
                           val plan_platform: Int? = null,
                           var plan_register_date: Long? = null, var plan_expire_date: Long? = null, var user_settings: UserSetting? = null, var user_rank: UserRank? = null,
                           var cancel_subscription_link: String? = null, var pending_cancel: Int? = null) {

        fun isFreeUser(): Boolean {
            if ((user_plan.isNullOrEmpty() || user_plan == "Freemium")) return true
            if (user_plan == "TRS Comp" || user_plan == "VMC") return false
            if (plan_expire_date == null || plan_expire_date!! + 259200 < (System.currentTimeMillis() / 1000)) return true //extend 3 days

            return false
        }

        fun isMonthlyUser(): Boolean {
            return !user_plan.isNullOrEmpty() && user_plan == "Mobile Monthly Plan"
        }

        fun isYearlyUser(): Boolean {
            return !user_plan.isNullOrEmpty() && user_plan == "Mobile Annual Plan"
        }

        fun isTrialUser(): Boolean {
            return !user_plan.isNullOrEmpty() && user_plan == "Mobile IAP Free Trial"
        }
    }

}