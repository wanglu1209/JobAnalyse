package com.wanglu.jobanalyse.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import butterknife.ButterKnife
import com.wanglu.jobanalyse.R
import com.wanglu.jobanalyse.adapter.MainJobAdapter
import com.wanglu.jobanalyse.model.MainJob
import com.wanglu.jobanalyse.network.CustomRequestCallback
import com.wanglu.jobanalyse.network.RetrofitFactory
import kotlinx.android.synthetic.main.activity_search_job.*
import kotlinx.android.synthetic.main.view_toolbar.*

/**
 * Created by WangLu on 2018/4/20.
 */

class SearchJobActivity : BaseActivity() {


    private lateinit var mSelectedCity: String
    private lateinit var mSelectedJob: String
    private lateinit var mJobAdapter: MainJobAdapter

    private var mJobs: List<MainJob>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_job)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.post { toolbar.title = "搜索" }

        toolbar.setNavigationOnClickListener { finish() }


        mSelectedCity = intent.getStringExtra("selected_city")
        mSelectedJob = intent.getStringExtra("selected_job")

        mJobAdapter = MainJobAdapter()
        mJobAdapter.emptyView = LayoutInflater.from(this).inflate(R.layout.view_empty, null)
        rv_search_list.layoutManager = LinearLayoutManager(this)
        rv_search_list.adapter = mJobAdapter

        mJobAdapter.setOnItemClickListener { _, _, position ->
            val toWebIntent = Intent(this, CommonWebActivity::class.java)
            toWebIntent.putExtra("title", mJobs!![position].companyName + " - " + mJobs!![position].positionName)
            toWebIntent.putExtra("url", "https://m.lagou.com/jobs/" + mJobs!![position].positionId + ".html")
            startActivity(toWebIntent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.queryHint = "请输入公司名"
        searchView.isIconified = false // 默认展开
        searchView.maxWidth = 2500   // 设置长度

        val searchAutoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
        searchAutoComplete.setHintTextColor(resources.getColor(android.R.color.white))//设置提示文字颜色
        searchAutoComplete.setTextColor(resources.getColor(android.R.color.white))//设置内容文字颜色

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchCompany(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }


    private fun searchCompany(text: String) {
        RetrofitFactory
                .requestApi
                .searchCompany(mSelectedCity, mSelectedJob, text)
                .enqueue(object : CustomRequestCallback<List<MainJob>> {
                    override fun onParse(data: List<MainJob>) {
                        mJobs = data
                        mJobAdapter.setNewData(data)
                    }
                })
    }


}
