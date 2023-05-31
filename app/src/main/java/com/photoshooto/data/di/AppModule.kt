package com.photoshooto.data.di

//import com.photoshooto.domain.usecase.profile.UpdateProfileViewModel
import com.photoshooto.domain.usecase.cart.CartDetailsViewModel
import com.photoshooto.domain.usecase.coupons.CouponsViewModel
import com.photoshooto.domain.usecase.landing_screen.LandingScreenViewModel
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.manage_address.ManageAddressViewModel
import com.photoshooto.domain.usecase.manage_admin.ManageAdminViewModel
import com.photoshooto.domain.usecase.manage_request.NewRequestViewModel
import com.photoshooto.domain.usecase.manage_users.ManageUsersViewModel
import com.photoshooto.domain.usecase.notificationApi.NotificationViewModel
import com.photoshooto.domain.usecase.orders.OrdersStatusViewModel
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.domain.usecase.payment_success.PaymentSuccessViewModel
import com.photoshooto.domain.usecase.product_details.ProductDetailsViewModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.domain.usecase.qr_event_order.EventOrderHistoryDetailsUseCase
import com.photoshooto.domain.usecase.qr_event_order.QREventOrderHistoryViewModel
import com.photoshooto.domain.usecase.qr_generation.QrGenerationViewModel
import com.photoshooto.domain.usecase.reset_pwd.ResetPwdViewModel
import com.photoshooto.domain.usecase.upload_file.UploadFileViewModel
import com.photoshooto.domain.usecase.verify_otp.VerifyOtpViewModel
import com.photoshooto.ui.notification.GetNotificationViewModel
import com.photoshooto.ui.qrcodesetup.createEvent.CreateEventViewModel
import com.photoshooto.ui.qrcodesetup.myQrCodes.MyQrViewModel
import com.photoshooto.ui.qrorderhistory.OngoingEventViewModel
import com.photoshooto.ui.qrorderhistory.QREventHistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {

    single { createPostRepository(get()) }

//    viewModel { SignUpViewModel(get()) }
//    single { createSendOtpMobileUseCase(get()) }
//
//    viewModel { SignupVerifyOtpViewModel(get()) }
//    single { createSignupUseCase(get()) }

    //notification api call
    viewModel { NotificationViewModel(get()) }
    single { createNotificationRequestUseCase(get()) }

    //login api call
    single { createUserLoginUseCase(get()) }
    viewModel { UserLoginViewModel(get()) }

//    single { createLogoutUseCase(get()) }

    //verifyOtp api call
    viewModel { VerifyOtpViewModel(get()) }
    single { createVerifyOtpUseCase(get()) }

    //ResetPwd api call
    viewModel { ResetPwdViewModel(get()) }
    single { createResetPwdUseCase(get()) }

    //get User Profile api call
    viewModel { GetUserProfileViewModel(get(), get(), get()) }
//    single { createGetUserProfileUseCase(get()) }

    single { createGetProfileByIDUseCase(get()) }

    //upload Profile image api call
    viewModel { UploadFileViewModel(get()) }
    single { createUploadFileUseCase(get()) }

    //update Profile api call
//    viewModel { UpdateProfileViewModel(get()) }
//    single { createUpdateProfileUseCase(get()) }

    //product details api call
    viewModel { ProductDetailsViewModel(get(), get()) }
    single { createProductDetailsUseCase(get()) }
    //single { createUploadFileUseCase(get()) }


    //order details api call
    viewModel { OrdersViewModel(get(), get()) }
    single { createOrdersUseCase(get()) }
    single { createOrderDetailUseCase(get()) }

    //order details api call
    viewModel { OrdersStatusViewModel(get()) }
    single { createOrdersStatusUseCase(get()) }

    //cart details api call
    viewModel { CartDetailsViewModel(get()) }
    single { createCartDetailsUseCase(get()) }

    //coupons api call
    viewModel { CouponsViewModel() }

    // manage admin api call
    viewModel { ManageAdminViewModel(get()) }
    single { createManageUsersUseCase(get()) }

    // coupons api call

    viewModel { CreateEventViewModel(get()) }
    single { createEventUseCase(get()) }

    viewModel { MyQrViewModel(get()) }
    single { myQRUseCase(get()) }

    viewModel { NewRequestViewModel(get()) }
    single { createManageRequestUseCase(get()) }

    //standee details api call
    viewModel { QrGenerationViewModel(get(), get(), get(), get()) }
    single { createQrGenerationUseCase(get()) }

    //address api call
    viewModel { ManageAddressViewModel(get()) }
    single { createManageAddressUseCase(get()) }

    viewModel { ManageUsersViewModel(get()) }
    single { createManageUsersCase(get()) }

    //notification
    viewModel { GetNotificationViewModel(get()) }
    single { createGetNotificationUseCaseUseCase(get()) }


    //QR EVENT ORDER HISTORY
    viewModel { QREventOrderHistoryViewModel(get()) }
    single { EventOrderHistoryDetailsUseCase(get()) }

    //QR EVENT ORDER HISTORY
    viewModel { OngoingEventViewModel(get()) }
    viewModel { QREventHistoryViewModel(get()) }
    //single { EventOrderHistoryDetailsUseCase(get()) }

    viewModel { LandingScreenViewModel(get()) }
    single { createGetLandingScreenUseCaseUseCase(get()) }

    viewModel { PaymentSuccessViewModel(get()) }
    single { createPaymentSuccessUseCase(get()) }

}
