package com.example.tiroberita.ui.fragments.redactions.sindonews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiroberita.R;
import com.example.tiroberita.databinding.FragmentHomeSindonewsBinding;
import com.example.tiroberita.model.DataModel;
import com.example.tiroberita.model.FirebaseResponseModel;
import com.example.tiroberita.model.PostModel;
import com.example.tiroberita.model.ResponseModel;
import com.example.tiroberita.model.SaveModel;
import com.example.tiroberita.ui.ItemClickListener;
import com.example.tiroberita.ui.adapters.NewsAdapter;
import com.example.tiroberita.ui.fragments.redactions.antara.HomeAntaraFragment;
import com.example.tiroberita.ui.fragments.redactions.cnbc.HomeCnbcFragment;
import com.example.tiroberita.ui.fragments.redactions.cnn.HomeCnnFragment;
import com.example.tiroberita.ui.fragments.redactions.jpnn.HomeJpnnFragment;
import com.example.tiroberita.ui.fragments.redactions.kumparan.HomeKumparanFragment;
import com.example.tiroberita.ui.fragments.redactions.okezone.HomeOkezoneFragment;
import com.example.tiroberita.ui.fragments.redactions.republika.HomeRepublikaFragment;
import com.example.tiroberita.ui.fragments.redactions.suara.HomeSuaraFragment;
import com.example.tiroberita.ui.fragments.redactions.tempo.HomeTempoFragment;
import com.example.tiroberita.ui.fragments.redactions.tribun.HomeTribunNewsFragment;
import com.example.tiroberita.util.constans.Constans;
import com.example.tiroberita.viewmodel.post.PostViewModel;
import com.example.tiroberita.viewmodel.sindonews.SindonewsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;

@AndroidEntryPoint
public class HomeSindoNewsFragment extends Fragment implements ItemClickListener {

    private FragmentHomeSindonewsBinding binding;
    private SharedPreferences sharedPreferences;
    private SindonewsViewModel sindonewsViewModel;
    private NewsAdapter newsAdapter;
    private List<PostModel> postModelList;
    private PostViewModel postViewModel;
    private DataModel dataModel;
    private String newsType = "terbaru";
    private LinearLayoutManager linearLayoutManager;
    private BottomSheetBehavior bottomSheetShare, bottomSheetWebView, bottomSheetMediaBerita;
    private String thumbnail, postTitle, postDesc, postDate, postUrl, userId, created_at, username, redactionName;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeSindonewsBinding.inflate(inflater, container, false);

        init();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listenerTabLayout();
        listener();
        listenerMediaBeritaPicker();
        getData("terbaru");
        greetings();
        setUpBottomSheetShare();
        setUpBottomSheetWebView();
        setUpBottomSheetMediaBerita();
        hideFab();

        binding.tvUsername.setText(sharedPreferences.getString(Constans.USERNAME, "-"));

    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences(Constans.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sindonewsViewModel = new ViewModelProvider(this).get(SindonewsViewModel.class);
        userId = sharedPreferences.getString(Constans.USER_ID, null);
        username = sharedPreferences.getString(Constans.USERNAME, null);
        redactionName = "Sindonews.com";
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

    }

    private void getData(String newsType) {
        showShimmer();
        binding.rvNews.setAdapter(null);
        if (newsType.equals("terbaru")){
            sindonewsViewModel.getDataTerbaru().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");

                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("nasional")){
            sindonewsViewModel.getDataNasional().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");


                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("metro")){
            sindonewsViewModel.getDataMetro().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");



                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("sports")){
            sindonewsViewModel.getDataSports().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");


                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        } else if (newsType.equals("ekbis")){
            sindonewsViewModel.getDataEkbis().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");


                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("internasional")){
            sindonewsViewModel.getDataInternational().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");


                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("daerah")){
            sindonewsViewModel.getDataDaerah().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");


                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("tekno")){
            sindonewsViewModel.getDataTekno().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");

                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("sains")){
            sindonewsViewModel.getDataSains().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");

                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("edukasi")){
            sindonewsViewModel.getDataEdukasi().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");

                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("lifestyle")){
            sindonewsViewModel.getDataLifestyle().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");

                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }else if (newsType.equals("kalam")){
            sindonewsViewModel.getDataKalam().observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getSuccess() == true && responseModel.getDataModel() != null) {
                        dataModel = responseModel.getDataModel();
                        postModelList = dataModel.getPostModelList();
                        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        newsAdapter = new NewsAdapter(getContext(), postModelList);
                        binding.rvNews.setAdapter(newsAdapter);
                        binding.rvNews.setLayoutManager(linearLayoutManager);
                        binding.rvNews.setHasFixedSize(true);
                        hideShimmer(false, "");

                        // set on click item
                        newsAdapter.setItemClickListener(HomeSindoNewsFragment.this);

                    }else {
                        hideShimmer(true, responseModel.getMessage());

                    }
                }
            });
        }







    }

    private void showToast(String type, String message) {
        if (type.equals(Constans.TOAST_SUCCESS)){
            Toasty.success(getContext(), message, Toasty.LENGTH_LONG).show();
        }else if (type.equals(Constans.TOAST_NORMAL)){
            Toasty.normal(getContext(), message, Toasty.LENGTH_LONG).show();
        }else {
            Toasty.error(getContext(), message, Toasty.LENGTH_LONG).show();

        }
    }




    private void listenerTabLayout() {
        binding.menuTerbaru.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "terbaru";

            // get data
            getData("terbaru");


            binding.lnTerbaru.setVisibility(View.VISIBLE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);


            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_star));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuNasional.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "nasional";

            // get data
            getData("nasional");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.VISIBLE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuMetro.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "metro";

            // get data
            getData("metro");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.VISIBLE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuSports.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "sports";

            // get data
            getData("sports");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.VISIBLE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuEkonomi.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "ekbis";

            // get data
            getData("ekbis");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.VISIBLE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuInternasional.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "internasional";

            // get data
            getData("internasional");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.VISIBLE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuDaerah.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "daerah";

            // get data
            getData("daerah");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.VISIBLE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));

        });


        binding.menuTeknologi.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "tekno";

            // get data
            getData("tekno");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.VISIBLE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuSains.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "sains";

            // get data
            getData("sains");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.VISIBLE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuEdukasi.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "edukasi";

            // get data
            getData("edukasi");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.VISIBLE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuLifestyle.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.black));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.second_font));

            // set type
            newsType = "lifestyle";

            // get data
            getData("lifestyle");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.VISIBLE);
            binding.lnKalam.setVisibility(View.GONE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah_off));

        });


        binding.menuKalam.setOnClickListener(view -> {
            binding.tvTbTerbaru.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbNasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbMetro.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSports.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEkonomi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbInternasional.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbDaerah.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbTeknologi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbSains.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbEdukasi.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbLifestyle.setTextColor(getContext().getColor(R.color.second_font));
            binding.tvTbKalam.setTextColor(getContext().getColor(R.color.black));

            // set type
            newsType = "kalam";

            // get data
            getData("kalam");

            binding.lnTerbaru.setVisibility(View.GONE);
            binding.lnNasional.setVisibility(View.GONE);
            binding.lnMetro.setVisibility(View.GONE);
            binding.lnSports.setVisibility(View.GONE);
            binding.lnEkonomi.setVisibility(View.GONE);
            binding.lnInternasional.setVisibility(View.GONE);
            binding.lnDaerah.setVisibility(View.GONE);
            binding.lnTeknologi.setVisibility(View.GONE);
            binding.lnSains.setVisibility(View.GONE);
            binding.lnEdukasi.setVisibility(View.GONE);
            binding.lnLifestyle.setVisibility(View.GONE);
            binding.lnKalam.setVisibility(View.VISIBLE);

            binding.ivTerbaru.setImageDrawable(getContext().getDrawable(R.drawable.ic_terbaru_off));
            binding.ivNasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivMetro.setImageDrawable(getContext().getDrawable(R.drawable.ic_city_off));
            binding.ivSports.setImageDrawable(getContext().getDrawable(R.drawable.ic_sport2_off));
            binding.ivEkonomi.setImageDrawable(getContext().getDrawable(R.drawable.ic_ekonomi_off));
            binding.ivInternasional.setImageDrawable(getContext().getDrawable(R.drawable.ic_internasional_off));
            binding.ivDaerah.setImageDrawable(getContext().getDrawable(R.drawable.ic_local_off));
            binding.ivTeknologi.setImageDrawable(getContext().getDrawable(R.drawable.ic_tech_off));
            binding.ivSains.setImageDrawable(getContext().getDrawable(R.drawable.ic_sains_off));
            binding.ivEdukasi.setImageDrawable(getContext().getDrawable(R.drawable.ic_education_off));
            binding.ivLifestyle.setImageDrawable(getContext().getDrawable(R.drawable.ic_life_style_off));
            binding.ivKalam.setImageDrawable(getContext().getDrawable(R.drawable.ic_syariah));
        });


    }



    private void listener() {
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(newsType);
                binding.searchView.setQuery("", false);
                binding.searchView.setIconified(true);
            }
        });

        binding.btnSearch.setOnClickListener(view -> {
            binding.searchView.setVisibility(View.VISIBLE);
            binding.searchView.setIconified(false);
            binding.btnSearch.setVisibility(View.GONE);
            binding.lrHeader.setVisibility(View.GONE);
        });



        binding.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                binding.lrHeader.setVisibility(View.VISIBLE);
                binding.btnSearch.setVisibility(View.VISIBLE);
                binding.searchView.setVisibility(View.GONE);
                binding.lrHeader.setVisibility(View.VISIBLE);


                return false;
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });



        binding.fabMediaPicker.setOnClickListener(view -> {
            showBottomSheetMediaBerita();
            binding.fabMediaPicker.setVisibility(View.GONE);
        });
        binding.vOverlay.setOnClickListener(view -> {
            hideBottomSheetShare();
            hideBottomSheetWebView();
            hideBottomSheetMediaBerita();
        });

        binding.btnShare.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, postUrl);
            sendIntent.setType("text/plain");

            // Hapus preferensi pilihan pengguna (default)
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            // Membuat chooser dialog
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });



        binding.btnSimpan.setOnClickListener(view -> {
            savePost();
        });



        binding.tvHeader.setOnClickListener(view -> {
            setWebView(Constans.URL_SINDONEWS);
            showBottomSheetWebView();
        });


    }

    private void listenerMediaBeritaPicker() {
        binding.mnuSindoNews.setOnClickListener(view -> {
            getData("terbaru");
            hideBottomSheetMediaBerita();
        });

        binding.mnuKumparan.setOnClickListener(view -> {
            moveFragment(new HomeKumparanFragment());
            hideBottomSheetMediaBerita();
        });

        binding.mnuTribun.setOnClickListener(view -> {
            moveFragment(new HomeTribunNewsFragment());
        });

        binding.mnuCnbc.setOnClickListener(view -> {
            moveFragment(new HomeCnbcFragment());
        });

        binding.mnuOkezone.setOnClickListener(view -> {
            moveFragment(new HomeOkezoneFragment());
        });

        binding.mnuCnn.setOnClickListener(view -> {
            moveFragment(new HomeCnnFragment());
        });

        binding.mnuAntara.setOnClickListener(view -> {
            moveFragment(new HomeAntaraFragment());
        });

        binding.mnuTempo.setOnClickListener(view -> {
            moveFragment(new HomeTempoFragment());
        });

        binding.mnuSuara.setOnClickListener(view -> {
            moveFragment(new HomeSuaraFragment());
        });

        binding.mnuRepublika.setOnClickListener(view -> {
            moveFragment(new HomeRepublikaFragment());
        });

        binding.mnuJpnn.setOnClickListener(view -> {
            moveFragment(new HomeJpnnFragment());
        });
    }

    private void greetings() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 12 && hour < 15) {
            binding.tvGreeting.setText("Selamat Siang,");
        } else if (hour >= 15 && hour < 18) {
            binding.tvGreeting.setText("Selamat Sore,");
        } else if (hour >= 18 && hour < 24) {
            binding.tvGreeting.setText("Selamat Malam,");
        } else {
            binding.tvGreeting.setText("Selamat Pagi,");
        }
    }

    private void hideShimmer(Boolean isEmpty, String message) {
        binding.swipeRefresh.setRefreshing(false);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.shimmerLayout.stopShimmer();
                binding.rvNews.setVisibility(View.VISIBLE);
                if (isEmpty == true) {
                    binding.lrEmpty.setVisibility(View.VISIBLE);
                    binding.tvMessage.setText(message);
                }else {
                    binding.lrEmpty.setVisibility(View.GONE);
                }
            }
        }, 2000);


    }

    private void showShimmer() {
        binding.rvNews.setVisibility(View.GONE);
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();
        binding.lrEmpty.setVisibility(View.GONE);
    }

    private void setUpBottomSheetShare () {
        bottomSheetShare = BottomSheetBehavior.from(binding.rlBottomSheetShare);
        bottomSheetShare.setHideable(true);
        bottomSheetShare.setPeekHeight(0);
        bottomSheetShare.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetShare.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideBottomSheetShare();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }


    private void setUpBottomSheetMediaBerita () {
        bottomSheetMediaBerita = BottomSheetBehavior.from(binding.rlBottomSheetMediaPicker);
        bottomSheetMediaBerita.setHideable(true);
        bottomSheetMediaBerita.setPeekHeight(0);
        bottomSheetMediaBerita.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetMediaBerita.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideBottomSheetMediaBerita();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }


    private void setUpBottomSheetWebView () {
        bottomSheetWebView = BottomSheetBehavior.from(binding.RlbottomSheetWebview);
        bottomSheetWebView.setHideable(true);
        bottomSheetWebView.setPeekHeight(0);
        bottomSheetWebView.setState(BottomSheetBehavior.STATE_HIDDEN);


        bottomSheetWebView.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideBottomSheetWebView();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {



            }
        });
    }

    private void showBottomSheetShare() {
        bottomSheetShare.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetShare.setPeekHeight(600);
        binding.vOverlay.setVisibility(View.VISIBLE);
        binding.btnSimpan.setBackground(getContext().getDrawable(R.drawable.ic_save));



    }

    private void hideBottomSheetShare() {
        bottomSheetShare.setState(BottomSheetBehavior.STATE_HIDDEN);
        binding.vOverlay.setVisibility(View.GONE);
        bottomSheetShare.setPeekHeight(0);

    }

    private void hideBottomSheetMediaBerita() {
        bottomSheetMediaBerita.setState(BottomSheetBehavior.STATE_HIDDEN);
        binding.vOverlay.setVisibility(View.GONE);
        bottomSheetMediaBerita.setPeekHeight(0);
        binding.fabMediaPicker.setVisibility(View.VISIBLE);


    }

    private void showBottomSheetMediaBerita() {
        bottomSheetMediaBerita.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetMediaBerita.setPeekHeight(600);
        binding.vOverlay.setVisibility(View.VISIBLE);
        binding.fabMediaPicker.setVisibility(View.GONE);


    }



    private void showBottomSheetWebView() {
        bottomSheetWebView.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetWebView.setPeekHeight(2000);
        bottomSheetWebView.setDraggable(false);
        binding.vOverlay.setVisibility(View.VISIBLE);

    }

    private void hideBottomSheetWebView() {
        bottomSheetWebView.setState(BottomSheetBehavior.STATE_HIDDEN);
        binding.vOverlay.setVisibility(View.GONE);

        // clean webview
        binding.webview.loadUrl("about:blank");
        binding.webview.clearCache(true);
        binding.webview.clearHistory();
        binding.webview.reload();
        binding.webview.clearFormData();
        binding.webview.clearSslPreferences();
        binding.webview.clearMatches();
        binding.webview.clearDisappearingChildren();
        binding.webview.clearAnimation();
        binding.webview.clearFocus();
        binding.webview.destroyDrawingCache();
        binding.webview.removeAllViews();


    }

    private void setWebView(String url) {
        // start shimmer
        shimmerWebView();

        // Aktifkan JavaScript
        WebSettings webSettings = binding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webview.loadUrl(url);

    }

    private void shimmerWebView() {

        binding.webViewShimmer.setVisibility(View.VISIBLE);
        binding.webViewShimmer.startShimmer();
        binding.webview.setVisibility(View.GONE
        );
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.webViewShimmer.setVisibility(View.GONE);
                binding.webViewShimmer.stopShimmer();
                binding.webview.setVisibility(View.VISIBLE);
            }
        }, 3500);
    }

    private void savePost() {
        if (userId == null) {
            showToast(Constans.TOAST_ERROR, Constans.ERR_MESSAGE);
        }else {

            binding.btnSimpan.setBackground(getContext().getDrawable(R.drawable.ic_save_fill));

            created_at = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(new Date());
            String postId = userId + "-" + timeStamp;
            SaveModel saveModel = new SaveModel(postUrl, postTitle, postDesc, postDate, thumbnail, userId, username, created_at, redactionName, postId);


            postViewModel.insertSavePost(saveModel).observe(getViewLifecycleOwner(), new Observer<FirebaseResponseModel>() {
                @Override
                public void onChanged(FirebaseResponseModel firebaseResponseModel) {
                    if (firebaseResponseModel.getSuccess() == true) {
                        showToast(Constans.TOAST_NORMAL, firebaseResponseModel.getMessage());
                        hideBottomSheetShare();
                    }else {
                        showToast(Constans.TOAST_ERROR,Constans.ERR_MESSAGE);
                    }
                }
            });

        }



    }







    @Override
    public void onItemClickListener(String share, Object model) {
        PostModel postModel = (PostModel) model;

        // set value
        postTitle = postModel.getTitle();
        postDesc  = postModel.getDescription();
        postDate = postModel.getPubDate();
        thumbnail = postModel.getThumbnail();
        postUrl = postModel.getLink();


        // set value to bottom sheet
        binding.tvPostTitle.setText(postTitle);


        String date = postDate.substring(0, 10);
        String timeStamp = postDate.substring(11, 19);
        binding.tvPostDate.setText(date + " | " + timeStamp);


        Glide.with(getContext()).load(thumbnail)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .centerCrop()
                .dontAnimate()
                .override(300, 300)
                .into(binding.ivPost);


        // load webview
        setWebView(postUrl);

        // logic listener

        if (share.equals("share")) { // JIKA BUTTON SHARE DI KLIK DI ADAPTER
            showBottomSheetShare();
        }else {
            showBottomSheetWebView();
        }



    }
    private void moveFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameHome, fragment)
                .addToBackStack(null).commit();
    }



    private void hideFab() {

        binding.rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx,
                                   int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    binding.fabMediaPicker.hide();
                } else if (dy < 0) {
                    binding.fabMediaPicker.show();
                }
            }
        });
    }


    private void filter(String querry) {
        ArrayList<PostModel> filteredList = new ArrayList<>();
        for (PostModel item : postModelList) {
            if (item != null && item.getTitle().toLowerCase().contains(querry.toLowerCase())) {
                filteredList.add(item);
            }

            newsAdapter.filter(filteredList);

            if (filteredList.isEmpty()) {
                binding.lrEmpty.setVisibility(View.VISIBLE);
                binding.tvMessage.setText("Berita tidak ditemukan.");
            }else {
                newsAdapter.filter(filteredList);
                binding.lrEmpty.setVisibility(View.GONE);
            }
        }
    }



}