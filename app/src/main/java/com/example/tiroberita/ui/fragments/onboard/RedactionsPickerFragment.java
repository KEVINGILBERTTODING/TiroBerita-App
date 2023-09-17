package com.example.tiroberita.ui.fragments.onboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import java.text.SimpleDateFormat;
import android.view.ViewGroup;

import com.example.tiroberita.R;
import com.example.tiroberita.databinding.FragmentRedactionsPickerBinding;
import com.example.tiroberita.model.UserModel;
import com.example.tiroberita.ui.activities.main.MainActivity;
import com.example.tiroberita.util.Constans;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class RedactionsPickerFragment extends Fragment {
    private FragmentRedactionsPickerBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;
    private String redactionFavorite, username, userId, created_at;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRedactionsPickerBinding.inflate(inflater, container, false);
        init();
        setUpBottomSheet();
        validateUserInfo();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener();
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences(Constans.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        redactionFavorite = sharedPreferences.getString(Constans.REDACTION_FAVORIT, null);
        database = FirebaseDatabase.getInstance();
        username = sharedPreferences.getString(Constans.USERNAME, null);
        myRef = database.getReference();
    }

    private void setUpBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.rlBottomSheet);
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

    private void listener() {
        binding.cvCnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            showBottomSheet();
            addRedactionPicker(getContext().getString(R.string.cnn), getContext().getString(R.string.cnn_short_desc), getContext().getDrawable(R.drawable.logo_cnn));

            }
        });

        binding.cvTribbun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.tribun), getContext().getString(R.string.tribun_short_desc), getContext().getDrawable(R.drawable.logo_tribun));

            }
        });

        binding.cvAntara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.antara), getContext().getString(R.string.antara_short_desc), getContext().getDrawable(R.drawable.logo_antara));

            }
        });
        binding.cvOkeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.okezone), getContext().getString(R.string.okezone_short_desc), getContext().getDrawable(R.drawable.logo_okezone));

            }
        });

        binding.cvCnbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.cnbc), getContext().getString(R.string.cnbc_short_desc), getContext().getDrawable(R.drawable.logo_cnbc));

            }
        });
        binding.cvKumparan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.kumparan), getContext().getString(R.string.kumparan_short_desc), getContext().getDrawable(R.drawable.logo_kumparan));

            }
        });

        binding.cvMerdeka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.merdeka), getContext().getString(R.string.merdeka_short_desc), getContext().getDrawable(R.drawable.logo_merdeka));

            }
        });

        binding.cvSindoNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.sindonews), getContext().getString(R.string.sindonews_short_desc), getContext().getDrawable(R.drawable.logo_sindonews));

            }
        });
        binding.cvTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.tempo), getContext().getString(R.string.tempo_short_desc), getContext().getDrawable(R.drawable.tempo));

            }
        });

        binding.cvSuara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.suara), getContext().getString(R.string.suara_short_desc), getContext().getDrawable(R.drawable.logo_suara));

            }
        });

        binding.cvRepublika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.republika), getContext().getString(R.string.republika_short_desc), getContext().getDrawable(R.drawable.logo_republika));

            }
        });

        binding.cvJpnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
                addRedactionPicker(getContext().getString(R.string.jpnn), getContext().getString(R.string.jpnn_short_desc), getContext().getDrawable(R.drawable.logo_jpnn));

            }
        });


        binding.vOverlay.setOnClickListener(view -> {
           hideBottomSheet();
        });

        binding.btnSimpan.setOnClickListener(view -> {
            saveUserInfo();
        });

    }



    private void showBottomSheet() {

        binding.vOverlay.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    private void addRedactionPicker(String redactionName, String redactionDesc, Drawable drawable) {

        binding.tvRedaction.setText(redactionName);
        binding.tvDesc.setText(redactionDesc);
        binding.ivRedactionLogo.setImageDrawable(drawable);

    }

    private void hideBottomSheet() {
        binding.vOverlay.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    private void validateUserInfo() {
        if (sharedPreferences.getString(Constans.REDACTION_FAVORIT, null) != null) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    private void saveUserInfo() {

        userId = UUID.randomUUID().toString().substring(0, 6);
        created_at = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if (binding.tvRedaction.getText().toString().isEmpty()) {
            showToast(Constans.TOAST_NORMAL, "Anda belum memilih media");
        }else if (username == null) {
            showToast(Constans.TOAST_ERROR, "Terjadi kesalahan");
        }else if(userId == null) {
            showToast(Constans.TOAST_ERROR, "Terjadi kesalahan");
        }else {
            UserModel userModel = new UserModel(userId, username, binding.tvRedaction.getText().toString(), created_at);
            myRef.child("user").push().setValue(userModel);

            // save sharedpref
            editor.putString(Constans.REDACTION_FAVORIT, binding.tvRedaction.getText().toString());
            editor.putString(Constans.USER_ID, userId);
            editor.putString(Constans.JOIN_DATE, created_at);
            editor.apply();

            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
            showToast(Constans.TOAST_NORMAL, "Selamat membaca...");
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}