package com.wanglu.jobanalyse.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.webkit.CookieSyncManager.createInstance
import android.webkit.CookieSyncManager.getInstance
import com.wanglu.jobanalyse.R
import kotlinx.android.synthetic.main.activity_common_web.*
import kotlinx.android.synthetic.main.view_toolbar.*
import java.util.*

/**
 * Created by WangLu on 2018/4/20.
 */

class CommonWebActivity : BaseActivity() {



    private var url: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_web)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar!!.post { toolbar!!.title = intent.getStringExtra("title") }

        toolbar!!.setNavigationOnClickListener { finish() }

        url = intent.getStringExtra("url")

        //创建CookieSyncManager
        createInstance(this)
        //得到CookieManager
        val cookieManager = CookieManager.getInstance()
        //得到向URL中添加的Cookie的值
        val cookieString = "user_trace_token=20170710165243-ce36faaf-9bfd-46c2-8ede-71e7ba6bef58; LGUID=20170710165244-23f430d3-654d-11e7-a26a-525400f775ce; _ga=GA1.2.112752877.1499676764; _ga=GA1.3.112752877.1499676764; index_location_city=%E5%8C%97%E4%BA%AC; Hm_lvt_4233e74dff0ae5bd0a3d81c6ccf756e6=1522738058,1523430832,1523667660,1524012821; JSESSIONID=ABAAABAAAFDABFG6B31FD79B235D3DED625EAE03F750F26; _gid=GA1.2.50299978.1524211588; LGSID=20180420160628-baac41b8-4471-11e8-b8c5-5254005c3644; _gat=1; Hm_lpvt_4233e74dff0ae5bd0a3d81c6ccf756e6=1524213521; LGRID=20180420163841-3a567ad8-4476-11e8-b8c6-5254005c3644"
        //使用cookieManager..setCookie()向URL中添加Cookie
        cookieManager.setCookie(url, cookieString)
        getInstance().sync()

        val header = HashMap<String, String>()
        header.put("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Mobile Safari/537.36")
        header.put("Host", "m.lagou.com")

        web_view!!.loadUrl(url, header)
        val settings = web_view!!.settings
        settings.javaScriptEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW   // 设置https可以加载http资源

        web_view!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100)
                    loading_progress!!.visibility = View.GONE
                else
                    loading_progress!!.visibility = View.VISIBLE
                loading_progress!!.progress = newProgress
            }
        }

        web_view!!.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                web_view!!.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)


                view.loadUrl("javascript:var a = document.getElementById('push_bottom').style.display='none';var b = document.getElementById('header').style.display='none';var c = document.getElementsByClassName('fix_btn_group')[0].style.display='none';var d = document.getElementsByClassName('collicon activeable')[0].style.display='none';" +
                        " $('#content .detail .items .item').eq(0).children('.icon').css({" +
                        "background: 'url(http://fairyever.qiniudn.com/wl-lg-img-hack.png) no-repeat -66px -48px'," +
                        "backgroundSize: '250px 250px'" +
                        "});" +
                        "$('#content .detail .items .item').eq(1).children('.icon').css({" +
                        "background: 'url(http://fairyever.qiniudn.com/wl-lg-img-hack.png) no-repeat -79px -49px'," +
                        "backgroundSize: '250px 250px'" +
                        "});" +
                        "$('#content .detail .items .item').eq(2).children('.icon').css({" +
                        "background: 'url(http://fairyever.qiniudn.com/wl-lg-img-hack.png) no-repeat -93px -49px'," +
                        "backgroundSize: '250px 250px'" +
                        "});" +
                        "$('#content .detail .items .item').eq(3).children('.icon').css({" +
                        "background: 'url(http://fairyever.qiniudn.com/wl-lg-img-hack.png) no-repeat -110.5px -49.5px'," +
                        "backgroundSize: '250px 250px'" +
                        "});" +
                        "$('#content .detail .items .item').eq(4).children('.icon').css({" +
                        "background: 'url(http://fairyever.qiniudn.com/wl-lg-img-hack.png) no-repeat -127.5px -49px'," +
                        "backgroundSize: '250px 250px'" +
                        "});" +
                        "$('#content > div.company.activeable > div > span').css({" +
                        "background: 'url(http://fairyever.qiniudn.com/wl-lg-img-hack.png) no-repeat -222.5px -3.5px'," +
                        "    backgroundSize: '250px 250px'" +
                        "});" +
                        "$('#content > div.positioneval > ul > li > div.zan-wrap > span > i').css({" +
                        "background: 'url(http://fairyever.qiniudn.com/wl-lg-img-hack.png) no-repeat -43.5px -218px'," +
                        "    backgroundSize: '250px 250px'" +
                        "});" +
                        "$('#content > div.company.activeable > div > div > h2').css({" +
                        "color: '#e74c3c'" +
                        "});" +
                        "$('#content > div.positiondesc > header').css({" +
                        "    color: '#e74c3c'," +
                        "    backgroundColor: '#FFC6C0'" +
                        "});" +
                        "$('#content > div.positioneval > div').css({" +
                        "    color: '#e74c3c'," +
                        "    backgroundColor: '#FFC6C0'" +
                        "});" +
                        "$('#content > div.positioneval > ul > li > div.zan-wrap > span > span').css({" +
                        "    color: '#e74c3c'," +
                        "});" +
                        "$('#content > div.positioneval > ul > li > div.zan-wrap > span > span > span').css({" +
                        "    color: '#e74c3c'," +
                        "}); " +
                        "")

                if (web_view!!.visibility == View.GONE)
                    web_view!!.visibility = View.VISIBLE
            }
        }
    }


    //点击返回上一页面
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && web_view!!.canGoBack()) {
            web_view!!.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}
