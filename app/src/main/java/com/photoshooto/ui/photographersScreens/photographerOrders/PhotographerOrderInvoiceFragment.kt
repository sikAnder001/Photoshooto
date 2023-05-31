package com.photoshooto.ui.photographersScreens.photographerOrders

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.databinding.FragmentPhotographerOrderInvoiceBinding
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.util.Status
import com.photoshooto.util.onToast
import com.photoshooto.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotographerOrderInvoiceFragment : Fragment() {
    private val viewModel: OrdersViewModel by viewModel()

    private lateinit var binding: FragmentPhotographerOrderInvoiceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerOrderInvoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (requireArguments().containsKey("OrderID")) {
            val orderID = requireArguments().get("OrderID").toString()
            viewModel.getInvoice(orderID)
            with(viewModel) {
                invoiceLiveData.observe(requireActivity()) { response ->
                    if (response != null) {
                        when (response.status) {
                            Status.SUCCESS -> {
                                onToast(response.data?.message!!, requireActivity())
                                val mainUrl =
                                    "https://photoshooto.com${response.data.data?.invoicePdf}"
                                Log.i("pdfFile", mainUrl)
                                /*CoroutineScope(Dispatchers.IO).launch {
                                    var inputStream: InputStream? = null
                                    try {
                                        val url = URL(mainUrl)
                                        val urlConnection: HttpURLConnection =
                                            url.openConnection() as HttpsURLConnection
                                        if (urlConnection.responseCode == 200) {
                                            inputStream =
                                                BufferedInputStream(urlConnection.inputStream)
                                        }
                                        withContext(Dispatchers.Main) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                binding.progressBar.visibility = View.GONE
                                                binding.idPDFView.fromStream(inputStream).load()
                                            }, 5000)
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }*/


                                val builder = CustomTabsIntent.Builder()
                                builder.setToolbarColor(Color.parseColor("#054871"))
                                val customTabsIntent = builder.build()
                                customTabsIntent.launchUrl(requireActivity(), Uri.parse(mainUrl))

                                binding.webView.settings.javaScriptEnabled = true
                                binding.webView.webViewClient = WebViewClient()
                                binding.webView.loadUrl(
                                    "http://docs.google.com/gview?embedded=true&url=$mainUrl"
                                )
                                binding.progressBar.visibility = View.GONE

                                binding.imgDownloadInvoice.setOnClickListener {
                                    downloadPDF(mainUrl)
                                }
                                binding.imgShare.setOnClickListener {

                                    sharePDF(mainUrl)
                                }

                            }
                            Status.LOADING -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            Status.ERROR -> {
                                binding.progressBar.visibility = View.GONE
                                response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun downloadPDF(mainUrl: String) {
        val request = DownloadManager.Request(Uri.parse(mainUrl))
        val title =
            URLUtil.guessFileName(mainUrl, null, null)
        request.setTitle(title)
        request.setDescription("Downloading File Please wait...")
        val cookie = CookieManager.getInstance().getCookie(mainUrl)
        request.addRequestHeader("cookie", cookie)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
        val downloadManager: DownloadManager =
            requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        onToast("Downloading Started", requireActivity())
    }

    private fun sharePDF(mainURL: String) {
        /*       val sendIntent: Intent = Intent().apply {
                   setDataAndType(Uri.parse(mainURL), "application/pdf")
                   action = Intent.ACTION_VIEW
               }
               val shareIntent = Intent.createChooser(sendIntent, null)
               startActivity(shareIntent)*/
        //startActivity ( Intent (Intent.ACTION_VIEW).setDataAndType(Uri.parse(mainURL), "application/pdf"));
    }
}