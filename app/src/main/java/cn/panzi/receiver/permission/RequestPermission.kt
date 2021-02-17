package cn.panzi.receiver.permission

import android.app.Activity


interface RequestPermission {


    fun request(activity: Activity, requestCallback: RequestCallback, vararg permission: String)

}