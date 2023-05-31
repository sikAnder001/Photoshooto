package com.photoshooto.domain.usecase.base

import com.photoshooto.domain.model.ApiError

interface UseCaseResponse<Type> {

    fun onSuccess(result: Type)

    fun onError(apiError: ApiError?)
}

