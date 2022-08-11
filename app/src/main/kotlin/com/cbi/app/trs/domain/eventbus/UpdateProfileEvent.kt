package com.cbi.app.trs.domain.eventbus

import com.cbi.app.trs.domain.usecases.user.PostUserProfile

class UpdateProfileEvent(val data: PostUserProfile.Param)