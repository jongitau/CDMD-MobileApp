package com.cdms.android.event

import com.cdms.android.network.RetrofitException


class ErrorEvent {

    var retrofitException: RetrofitException? = null
    var message: String? = null
    var throwable: Throwable? = null

    constructor(retrofitException: RetrofitException) {
        this.retrofitException = retrofitException
    }

    constructor(message: String) {
        this.message = message
    }

    constructor(throwable: Throwable){
        this.throwable = throwable
    }
}

