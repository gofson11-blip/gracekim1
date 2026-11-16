package com.gracekim.saju.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.gracekim.saju.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object Ads {
    @Volatile
    private var initialized = false

    /** 앱 시작 시 한 번 호출. 메인 스레드를 막지 않도록 백그라운드에서 초기화합니다. */
    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        val appContext = context.applicationContext
        Thread { MobileAds.initialize(appContext) {} }.start()
    }
}

/** 화면 하단에 붙는 적응형 배너 광고 */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val widthDp = LocalConfiguration.current.screenWidthDp
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        factory = { ctx ->
            AdView(ctx).apply {
                adUnitId = BuildConfig.ADMOB_BANNER_ID
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, widthDp))
                loadAd(AdRequest.Builder().build())
            }
        },
        onRelease = { it.destroy() },
    )
}

/** 보상형 광고: 끝까지 보면 [show] 의 onReward 가 호출됩니다. */
class RewardedAdController(private val context: Context) {
    private var ad: RewardedAd? = null
    private var loading = false

    fun load() {
        if (ad != null || loading) return
        loading = true
        RewardedAd.load(
            context,
            BuildConfig.ADMOB_REWARDED_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(loaded: RewardedAd) {
                    ad = loaded
                    loading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    ad = null
                    loading = false
                }
            },
        )
    }

    /**
     * 광고를 보여 줍니다. 광고가 준비되지 않았거나(인터넷 없음 등) 표시에 실패하면
     * 사용자가 막히지 않도록 바로 보상을 줍니다.
     */
    fun show(onReward: () -> Unit) {
        val activity = context.findActivity()
        val current = ad
        if (current == null || activity == null) {
            onReward()
            load()
            return
        }
        ad = null
        var rewarded = false
        current.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                load()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                if (!rewarded) {
                    rewarded = true
                    onReward()
                }
                load()
            }
        }
        current.show(activity) {
            if (!rewarded) {
                rewarded = true
                onReward()
            }
        }
    }
}

@Composable
fun rememberRewardedAd(): RewardedAdController {
    val context = LocalContext.current
    val controller = remember(context) { RewardedAdController(context) }
    DisposableEffect(controller) {
        controller.load()
        onDispose { }
    }
    return controller
}

private fun Context.findActivity(): Activity? {
    var c: Context? = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}
