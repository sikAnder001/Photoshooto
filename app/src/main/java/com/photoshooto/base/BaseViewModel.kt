package  com.photoshooto.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.photoshooto.domain.exception.MyException
import com.photoshooto.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {
    var pageLoadView: PageLoadView? = null
        private set

    fun setupView(pageLoadView: PageLoadView) {
        this.pageLoadView = pageLoadView
    }

    val exceptionLiveData: MutableLiveData<Exception> = MutableLiveData()

    var user: User? = null

    var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun <T> postValue(either: Either<MyException, T>, successLiveData: MutableLiveData<T>) {
        either.either({
            exceptionLiveData.postValue(it)
        }, {
            successLiveData.postValue(it)
        })
    }
}