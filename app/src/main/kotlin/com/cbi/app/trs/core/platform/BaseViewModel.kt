
package com.cbi.app.trs.core.platform

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cbi.app.trs.core.exception.Failure

/**
 * Base ViewModel class with default Failure handling.
 * @see ViewModel
 * @see Failure
 */
abstract class BaseViewModel : ViewModel() {

    var failureData: MutableLiveData<Failure> = MutableLiveData()

    protected fun handleFailure(failure: Failure?) {
        this.failureData.value = failure
    }
}