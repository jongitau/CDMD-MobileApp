//package com.cdms.android.service
//
//import android.os.Handler
//import android.os.Looper
//import android.os.Message
//import android.util.Log
//import com.cdms.android.Global
//import com.lvrenyang.io.BTPrinting
//
//class WorkService : Thread() {
//    private val bt = BTPrinting()
//    var workHandler: Handler? = null
//    private val mLooper: Looper? = null
//    var targetHandler: Handler? = null
//    private val TAG = "WorkThread"
//
//    fun addHandler(handler: Handler?) {
//        if (!targetHandler.contains(handler)) {
//            targetsHandler.add(handler)
//        }
//    }
//
//    fun disconnectBt() {
//        try {
//            bt.Close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun connectBt(BTAddress: String?) {
//        if (null != workHandler && null != mLooper) {
//            val msg: Message = workHandler!!
//                .obtainMessage(Global.MSG_WORKTHREAD_HANDLER_CONNECTBT)
//            msg.obj = BTAddress
//            workHandler!!.sendMessage(msg)
//        } else {
//            if (null == workHandler) Log.v(
//                TAG,
//                "workHandler is null pointer"
//            )
//            if (null == mLooper) Log.v(
//                TAG,
//                "mLooper is null pointer"
//            )
//
//            // 回馈给UI
//            val msg: Message? = targetHandler
//                ?.obtainMessage(Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT)
//            msg?.arg1 = 0
//            if (msg != null) {
//                targetHandler?.sendMessage(msg)
//            }
//        }
//    }
//
//    fun isConnected(): Boolean {
//        return bt.IsOpened()
//    }
//
//}