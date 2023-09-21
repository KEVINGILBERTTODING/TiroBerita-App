package com.example.tiroberita.data.repository.app;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tiroberita.model.AppModel;
import com.example.tiroberita.model.FirebaseResponseModel;
import com.example.tiroberita.util.constans.Constans;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

public class AppRepository {

    @Inject
    public AppRepository () {

    }
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public LiveData<FirebaseResponseModel<AppModel>> updateCheck() {
        MutableLiveData<FirebaseResponseModel<AppModel>> responseModelMutableLiveData = new MutableLiveData<>();
        Query query = databaseReference.child(Constans.APP);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AppModel appModel = snapshot.getValue(AppModel.class);
                responseModelMutableLiveData.setValue(new FirebaseResponseModel<>(true, Constans.RESPONSE_SUCCESS, appModel));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                responseModelMutableLiveData.setValue(new FirebaseResponseModel<>(false, Constans.ERR_MESSAGE, null));
            }
        });

        return  responseModelMutableLiveData;

    }


}
