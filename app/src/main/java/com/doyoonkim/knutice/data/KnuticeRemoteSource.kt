package com.doyoonkim.knutice.data

import android.util.Log
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.NoticesPerPage
import com.doyoonkim.knutice.model.TokenInfo
import com.doyoonkim.knutice.model.TopThreeNotices
import com.doyoonkim.knutice.model.ValidateTokenResult
import com.example.knutice.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject


class KnuticeRemoteSource @Inject constructor() {

    private val noticeService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_ROOT)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val fcmService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_ROOT_FCM)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    suspend fun getTopThreeNotice(): TopThreeNotices {
        Log.d("KnuticeRemoteSource", "Start retrofit service")
        return noticeService.create(KnuticeService::class.java).run {
            this.getTopThreeNotice()
        }
    }


    suspend fun getNoticeListPerPage(category: NoticeCategory, lastNttId: Int): NoticesPerPage {
        Log.d("KnuticeRemoteSource", "Start retrofit service")
        return noticeService.create(KnuticeService::class.java).run {
            if (lastNttId == 0) {
                this.getFirstPageOfNotice(category)
            } else {
                this.getNoticeListPerPage(category, lastNttId)
            }
        }
    }

    suspend fun getFullNoticeContent(url: String): Deferred<String> =
        CoroutineScope(Dispatchers.IO).async {
            Jsoup.connect(url)
                .get()
                .getElementsByClass("bbs-view-content bbs-view-content-skin05")
                .text() ?: "Unable to receive full notice content"
        }

    fun validateToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            fcmService.create(KnuticeService::class.java).validateToken(
                TokenInfo(deviceToken = token)
            ).run {
                if (this.result?.resultCode == 200) Log.d("KnuticeServer", "Token saved.")
                else Log.d("KnuticeServer", "Failed to save token")
            }
        }
    }

}

interface KnuticeService {

    @GET("/open-api/notice")
    suspend fun getTopThreeNotice(): TopThreeNotices

    @GET("/open-api/notice/list")
    suspend fun getNoticeListPerPage(
        @Query("noticeName") category: NoticeCategory,
        @Query("nttId") lastNttId: Int
    ): NoticesPerPage

    @GET("/open-api/notice/list")
    suspend fun getFirstPageOfNotice(
        @Query("noticeName") category: NoticeCategory
    ): NoticesPerPage

    @Headers("Content-Type: application/json")
    @POST("/fcm/sendDeviceToken")
    suspend fun validateToken(
        @Body requestBody: TokenInfo
    ): ValidateTokenResult

}