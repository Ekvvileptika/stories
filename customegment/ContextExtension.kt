package az.myaccess.ui.activities.stories.customegment

import android.content.Context

fun Context.dp(valueInDp: Int): Int = (valueInDp * resources.displayMetrics.density).toInt()