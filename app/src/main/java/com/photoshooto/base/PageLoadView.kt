package  com.photoshooto.base

interface PageLoadView {
    abstract val PrefKeys: Any

    fun showProgress()
    fun dismissProgress()
}