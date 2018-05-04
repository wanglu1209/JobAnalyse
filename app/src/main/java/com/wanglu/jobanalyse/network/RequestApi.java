package com.wanglu.jobanalyse.network;

import com.wanglu.jobanalyse.model.AnalyseModel;
import com.wanglu.jobanalyse.model.BaseModule;
import com.wanglu.jobanalyse.model.JobCategory;
import com.wanglu.jobanalyse.model.MainJob;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by WangLu on 2018/4/18.
 */

public interface RequestApi {

    @GET("/api/getCityJobInfoList/{city}/{category}/{pn}")
    Call<BaseModule<List<MainJob>>> getCityJobInfoList(@Path("city") String city, @Path("category") String category, @Path("pn") int pn);

    @GET("/api/searchCompany/{city}/{category}/{text}")
    Call<BaseModule<List<MainJob>>> searchCompany(@Path("city") String city, @Path("category") String category, @Path("text") String text);

    @GET("/api/getSalaryDistributing/{city}/{category}")
    Call<BaseModule<AnalyseModel>> getSalaryDistributing(@Path("city") String city, @Path("category") String category);

    @GET("/api/getSalaryWeekGraph/{city}/{category}")
    Call<BaseModule<AnalyseModel>> getSalaryWeekGraph(@Path("city") String city, @Path("category") String category);

    @GET("/api/getDistrictJobDistributing/{city}/{category}")
    Call<BaseModule<AnalyseModel>> getDistrictJobDistributing(@Path("city") String city, @Path("category") String category);

    @GET("/api/getDistrictSalaryAvg/{city}/{category}")
    Call<BaseModule<AnalyseModel>> getDistrictSalaryAvg(@Path("city") String city, @Path("category") String category);

    @GET("/api/getSalaryTop10/{city}/{category}")
    Call<BaseModule<List<MainJob>>> getSalaryTop10(@Path("city") String city, @Path("category") String category);

    @GET("/api/getAllJobCategory")
    Call<BaseModule<List<JobCategory>>> getAllJobCategory();
}
