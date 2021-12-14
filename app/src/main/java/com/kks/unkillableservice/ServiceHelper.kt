package com.kks.unkillableservice

import android.content.Context
import android.content.Intent

/**
 * Created by kaungkhantsoe at 14/12/2021
 */

fun getServiceIntent(context: Context) =
    Intent(context, MyService::class.java)

