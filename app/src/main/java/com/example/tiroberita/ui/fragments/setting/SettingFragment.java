package com.example.tiroberita.ui.fragments.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiroberita.databinding.FragmentSettingBinding;
import com.example.tiroberita.model.AppModel;
import com.example.tiroberita.model.FirebaseResponseModel;
import com.example.tiroberita.ui.activities.onboard.OnBoardActivitiy;
import com.example.tiroberita.util.constans.Constans;
import com.example.tiroberita.viewmodel.app.AppViewModel;
import com.example.tiroberita.viewmodel.auth.AuthViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;

@AndroidEntryPoint
public class SettingFragment extends Fragment {


    private FragmentSettingBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AppViewModel appViewModel;
    private AuthViewModel authViewModel;
    private String username, userId, postUrl;
    private BottomSheetBehavior bottomSheetBehavior, bottomSheetCheckUpdate, bottomSheetUpdateUsername;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        init();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener();
        setUpBottomSheetMenu();
        setUpBottomSheetCheckUpdate();
        setUpBottomSheetUpdateUsername();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetCheckUpdate.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetUpdateUsername.setState(BottomSheetBehavior.STATE_HIDDEN);

        binding.tvUsername.setText("Hai, " + username);

    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences(Constans.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        userId = sharedPreferences.getString(Constans.USER_ID, "0");
        username = sharedPreferences.getString(Constans.USERNAME, "none");
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);


    }

    private void checkUpdate() {
        appViewModel.checkUpdate().observe(getViewLifecycleOwner(), new Observer<FirebaseResponseModel<AppModel>>() {
            @Override
            public void onChanged(FirebaseResponseModel<AppModel> appModelFirebaseResponseModel) {
                if (appModelFirebaseResponseModel.getSuccess() == true && appModelFirebaseResponseModel.getData() != null) {
                  if (!appModelFirebaseResponseModel.getData().getVersion().contains(Constans.APP_VER)) {
                      showBottomSheetCheckUpdate();
                      binding.tvDescUpdate.setText(appModelFirebaseResponseModel.getData().getTitle());
                      postUrl = appModelFirebaseResponseModel.getData().getUrl();
                  }else {
                      showToast(Constans.TOAST_NORMAL, "Anda telah menggunakan versi terbaru");
                  }
                }else {
                    showToast(Constans.TOAST_ERROR, Constans.ERR_MESSAGE);
                }
            }
        });
    }

    private void listener() {
        binding.btnLogout.setOnClickListener(view -> {
           showBottomSheetMenu();
        });

        binding.vOverlay.setOnClickListener(view -> {
            hideBottomSheet();
        });
        binding.vOverlay2.setOnClickListener(view -> {
            hideBottomSheetCheckUpdate();
        });

        binding.vOverlay3.setOnClickListener(view -> {
            hideBottomSheetUpdateUsername();
        });

        binding.cvUpdateApp.setOnClickListener(view -> {
            hideBottomSheet();
            checkUpdate();
        });

        binding.btnUpdate.setOnClickListener(view -> {
            directPost(postUrl);
        });

        binding.cvUpdateUsername.setOnClickListener(view -> {
            hideBottomSheet();
            showBottomSheetUpdateUsername();
        });

        binding.btnSimpan.setOnClickListener(view -> {
            updateUsername();
        });
    }


    private void updateUsername() {
        if (binding.etUsername.getText().toString().isEmpty()) {
            showToast(Constans.TOAST_ERROR, "Username tidak boleh kosong");

        }else if (userId == null){
            showToast(Constans.TOAST_ERROR, Constans.ERR_MESSAGE);
        }else {
            authViewModel.updateUsername(binding.etUsername.getText().toString(), userId).observe(getViewLifecycleOwner(), new Observer<FirebaseResponseModel>() {
                @Override
                public void onChanged(FirebaseResponseModel firebaseResponseModel) {
                    if (firebaseResponseModel.getSuccess() == true) {
                        showToast(Constans.TOAST_NORMAL, firebaseResponseModel.getMessage());
                        editSharedPref(Constans.USERNAME, binding.etUsername.getText().toString());
                        binding.tvUsername.setText(binding.etUsername.getText().toString());
                        hideBottomSheetUpdateUsername();

                    }else {
                        showToast(Constans.TOAST_ERROR, firebaseResponseModel.getMessage());
                    }
                }
            });
        }

    }

    private void deleteSharedPref() {
        editor.clear();
        editor.apply();
        startActivity(new Intent(getActivity(), OnBoardActivitiy.class));
        getActivity().finish();
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

    private void directPost(String postUrl) {
        // buka browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
        startActivity(browserIntent);
    }



    private void setUpBottomSheetMenu() {

        bottomSheetBehavior = BottomSheetBehavior.from(binding.rlBottomSheetMenu);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setHideable(true);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideBottomSheet();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    private void setUpBottomSheetCheckUpdate() {

        bottomSheetCheckUpdate = BottomSheetBehavior.from(binding.rlBottomSheetUpdate);
        bottomSheetCheckUpdate.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetCheckUpdate.setHideable(true);

        bottomSheetCheckUpdate.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideBottomSheetCheckUpdate();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }


    private void setUpBottomSheetUpdateUsername() {

        bottomSheetUpdateUsername = BottomSheetBehavior.from(binding.rlBottomSheetUpdateUsername);
        bottomSheetUpdateUsername.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetUpdateUsername.setHideable(true);

        bottomSheetUpdateUsername.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideBottomSheetUpdateUsername();


                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }


    private void showBottomSheetCheckUpdate() {
        bottomSheetCheckUpdate.setState(BottomSheetBehavior.STATE_COLLAPSED);
        binding.vOverlay2.setVisibility(View.VISIBLE);
    }

    private void showBottomSheetUpdateUsername() {
        bottomSheetUpdateUsername.setState(BottomSheetBehavior.STATE_COLLAPSED);
        binding.vOverlay3.setVisibility(View.VISIBLE);
        binding.etUsername.setText(username);
    }

    private void hideBottomSheetUpdateUsername() {
        bottomSheetUpdateUsername.setState(BottomSheetBehavior.STATE_HIDDEN);
        binding.vOverlay3.setVisibility(View.GONE);
        binding.etUsername.setText("");
    }

    private void showBottomSheetMenu() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        binding.vOverlay.setVisibility(View.VISIBLE);

    }

    private void hideBottomSheet() {
        binding.vOverlay.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    private void hideBottomSheetCheckUpdate() {
        binding.vOverlay2.setVisibility(View.GONE);
        bottomSheetCheckUpdate.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    private void editSharedPref(String name, String value) {
        editor.putString(name, value);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}