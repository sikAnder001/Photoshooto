package com.photoshooto.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.photoshooto.R
import com.photoshooto.domain.exception.MyException
import com.photoshooto.domain.model.CustomProgressDialog
import com.photoshooto.ui.job.utility.showAppDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        var dialogShowing = false
    }

    lateinit var toolbar1: Toolbar
    private var needToShowBackButton: Boolean? = false

    lateinit var job: Job
    private var progress: CustomProgressDialog? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    abstract fun getBaseViewModel(): BaseViewModel?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        job = Job()

        progress = CustomProgressDialog(this)

        attachBaseObserver()
    }

    private fun setStatusBarColor() {
        this.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = resources.getColor(getStatusBarColorId())
        }
    }

    open fun getStatusBarColorId(): Int {
        return R.color.white
    }

    private fun attachBaseObserver() {
        getBaseViewModel()?.exceptionLiveData?.observe(this, Observer {
            hideProgress()
            it?.apply {
                when (this) {
                    is MyException.InternetConnectionException -> {
                        showAppDialog(
                            getString(R.string.app_name),
                            getString(R.string.exception_error_network),
                            getString(R.string.ok), { dialog, _ ->
                                dialog.dismiss()
                                finish()
                            }
                        )
                    }
                    is MyException.JsonException -> showErrorDialog(getString(R.string.exception_error_unparceble))
                    is MyException.ServerNotAvailableException -> showErrorDialog(getString(R.string.exception_error_server))
                    is MyException.DatabaseException -> showErrorDialog(getString(R.string.exception_error_database))
                    is MyException.NetworkCallCancelException -> {
                    }
                    else -> {
                        var message = it.message
                        if (message?.isEmpty() == true) {
                            message = it.localizedMessage
                        }
                        showErrorDialog("Unknown error : $message")
                    }
                }
            }
        })
    }

    private fun showErrorDialog(message: String) {
        showAppDialog(
            getString(R.string.app_name),
            message,
            getString(R.string.ok), DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun showProgress() {
        if (!this.isFinishing) {
            progress?.show()
        }
    }

    fun hideProgress() {
        if (!this.isFinishing && progress?.isShowing == true) {
            progress?.dismiss()
        }
    }
}