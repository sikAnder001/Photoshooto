package com.photoshooto.data.di

import com.photoshooto.data.repository.PostsRepositoryImp
import com.photoshooto.data.source.remote.ApiService
import com.photoshooto.domain.repository.HomeApiService
import com.photoshooto.domain.repository.PostsRepository
import com.photoshooto.domain.usecase.cart.CartDetailsUseCase
import com.photoshooto.domain.usecase.landing_screen.LandingScreenUseCase
import com.photoshooto.domain.usecase.login.UserLoginUseCase
import com.photoshooto.domain.usecase.manage_address.ManageAddressUseCase
import com.photoshooto.domain.usecase.manage_admin.ManageUsersUseCase
import com.photoshooto.domain.usecase.manage_request.ManageRequestUseCase
import com.photoshooto.domain.usecase.manage_users.ManageUsersCase
import com.photoshooto.domain.usecase.notificationApi.NotificationRequestUseCase
import com.photoshooto.domain.usecase.orders.OrderDetailUseCase
import com.photoshooto.domain.usecase.orders.OrdersStatusUseCase
import com.photoshooto.domain.usecase.orders.OrdersUseCase
import com.photoshooto.domain.usecase.payment_success.PaymentSuccessUseCase
import com.photoshooto.domain.usecase.product_details.ProductDetailsUseCase
import com.photoshooto.domain.usecase.profile.GetProfileByIDUseCase
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.CreateEventUseCase
import com.photoshooto.domain.usecase.qrCodeSetup.myQrCode.MyQrUseCase
import com.photoshooto.domain.usecase.qr_generation.QrGenerationUseCase
import com.photoshooto.domain.usecase.reset_pwd.ResetPwdUseCase
import com.photoshooto.domain.usecase.upload_file.UploadFileUseCase
import com.photoshooto.domain.usecase.verify_otp.VerifyOtpUseCase
import com.photoshooto.ui.notification.GetNotificationUseCase
import com.photoshooto.util.BASE_URL
import com.photoshooto.util.DOMAIN
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


private const val TIME_OUT = 60L

val NetworkModule = module {

    single { createService(get()) }

    single { createRetrofit(get(), BASE_URL) }

    single { createOkHttpClient() }

    single { createWebService<HomeApiService>(get(), DOMAIN) }

    single { MoshiConverterFactory.create() }

    single { Moshi.Builder().build() }

}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor).build()
}

/*fun enableTls12OnPreLollipop(client: OkHttpClient.Builder): OkHttpClient.Builder? {
    if (Build.VERSION.SDK_INT < 22) {
        try {

            val sc: SSLContext = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            client.sslSocketFactory(
                Tls12SocketFactory(sc.getSocketFactory()),
                trustAllCerts.get(0) as X509TrustManager
            )
            val cs: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build()
            val csslv3: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.SSL_3_0)
                .build()
            val specs: MutableList<ConnectionSpec> = ArrayList()
            specs.add(cs)
            specs.add(csslv3)
            specs.add(ConnectionSpec.COMPATIBLE_TLS)
            specs.add(ConnectionSpec.CLEARTEXT)
            client.connectionSpecs(specs)
        } catch (exc: Exception) {
            Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc)
        }
    }
    return client
}*/

fun createRetrofit(okHttpClient: OkHttpClient, url: String): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create()).build()
}

fun createService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}

fun createPostRepository(apiService: ApiService): PostsRepository {
    return PostsRepositoryImp(apiService)
}


//fun createSendOtpMobileUseCase(postsRepository: PostsRepository): SendOtpMobileUseCase {
//    return SendOtpMobileUseCase(postsRepository)
//}
//
//fun createSignupUseCase(postsRepository: PostsRepository): SignupUseCase {
//    return SignupUseCase(postsRepository)
//}

fun createNotificationRequestUseCase(postsRepository: PostsRepository): NotificationRequestUseCase {
    return NotificationRequestUseCase(postsRepository)
}

fun createUserLoginUseCase(postsRepository: PostsRepository): UserLoginUseCase {
    return UserLoginUseCase(postsRepository)
}

//fun createLogoutUseCase(postsRepository: PostsRepository): LogoutUseCase {
//    return LogoutUseCase(postsRepository)
//}
fun createVerifyOtpUseCase(postsRepository: PostsRepository): VerifyOtpUseCase {
    return VerifyOtpUseCase(postsRepository)
}

fun createResetPwdUseCase(postsRepository: PostsRepository): ResetPwdUseCase {
    return ResetPwdUseCase(postsRepository)
}

//fun createGetUserProfileUseCase(postsRepository: PostsRepository): GetUserProfileUseCase {
//    return GetUserProfileUseCase(postsRepository)
//}

fun createGetProfileByIDUseCase(postsRepository: PostsRepository): GetProfileByIDUseCase {
    return GetProfileByIDUseCase(postsRepository)
}


fun createUploadFileUseCase(postsRepository: PostsRepository): UploadFileUseCase {
    return UploadFileUseCase(postsRepository)
}

//fun createUpdateProfileUseCase(postsRepository: PostsRepository): UpdateProfileUseCase {
//    return UpdateProfileUseCase(postsRepository)
//}

fun createProductDetailsUseCase(postsRepository: PostsRepository): ProductDetailsUseCase {
    return ProductDetailsUseCase(postsRepository)
}

fun createCartDetailsUseCase(postsRepository: PostsRepository): CartDetailsUseCase {
    return CartDetailsUseCase(postsRepository)
}


fun createOrdersUseCase(postsRepository: PostsRepository): OrdersUseCase {
    return OrdersUseCase(postsRepository)
}

fun createOrderDetailUseCase(postsRepository: PostsRepository): OrderDetailUseCase {
    return OrderDetailUseCase(postsRepository)
}

fun createEventUseCase(postsRepository: PostsRepository): CreateEventUseCase {
    return CreateEventUseCase(postsRepository)
}

fun myQRUseCase(postsRepository: PostsRepository): MyQrUseCase {
    return MyQrUseCase(postsRepository)
}

fun createOrdersStatusUseCase(postsRepository: PostsRepository): OrdersStatusUseCase {
    return OrdersStatusUseCase(postsRepository)
}

fun createQrGenerationUseCase(postsRepository: PostsRepository): QrGenerationUseCase {
    return QrGenerationUseCase(postsRepository)
}

fun createManageAddressUseCase(postsRepository: PostsRepository): ManageAddressUseCase {
    return ManageAddressUseCase(postsRepository)
}

fun createManageUsersUseCase(postsRepository: PostsRepository): ManageUsersUseCase {
    return ManageUsersUseCase(postsRepository)
}

fun createManageRequestUseCase(postsRepository: PostsRepository): ManageRequestUseCase {
    return ManageRequestUseCase(postsRepository)
}

fun createManageUsersCase(postsRepository: PostsRepository): ManageUsersCase {
    return ManageUsersCase(postsRepository)
}

fun createGetNotificationUseCaseUseCase(postsRepository: PostsRepository): GetNotificationUseCase {
    return GetNotificationUseCase(postsRepository)
}

fun createGetLandingScreenUseCaseUseCase(postsRepository: PostsRepository): LandingScreenUseCase {
    return LandingScreenUseCase(postsRepository)
}

fun createPaymentSuccessUseCase(postsRepository: PostsRepository): PaymentSuccessUseCase {
    return PaymentSuccessUseCase(postsRepository)
}


inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}