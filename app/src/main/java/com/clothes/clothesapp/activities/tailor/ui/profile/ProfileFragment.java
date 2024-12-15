package com.clothes.clothesapp.activities.tailor.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clothes.clothesapp.R;
import com.clothes.clothesapp.activities.tailor.ui.works.GalleryActivity;
import com.clothes.clothesapp.callback.GalleryCallback;
import com.clothes.clothesapp.callback.ImageCallback;
import com.clothes.clothesapp.callback.TailorCallback;
import com.clothes.clothesapp.controller.ComplaintsController;
import com.clothes.clothesapp.controller.GalleryController;
import com.clothes.clothesapp.controller.TailorController;
import com.clothes.clothesapp.databinding.FragmentNotificationsBinding;
import com.clothes.clothesapp.model.Gallery;
import com.clothes.clothesapp.model.Tailor;
import com.clothes.clothesapp.utils.LoadingHelper;
import com.clothes.clothesapp.utils.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    EditText name,email,password,phone,address;
    Button updateProfile;
    CircleImageView profileImage;

    private LoadingHelper loadingHelper;
    String imageURL = "";
    public static final int PICK_IMAGE = 1;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        phone = root.findViewById(R.id.phone);
        address = root.findViewById(R.id.address);
        profileImage = root.findViewById(R.id.profile_image);
        updateProfile = root.findViewById(R.id.update_profile);
        loadingHelper = new LoadingHelper(getActivity());

        name.setText(SharedData.currentTailor.getName());
        email.setText(SharedData.currentTailor.getEmail());
        password.setText(SharedData.currentTailor.getPassword());
        phone.setText(SharedData.currentTailor.getPhone());
        address.setText(SharedData.currentTailor.getAddress());
        imageURL = SharedData.currentTailor.getAvatar();

        Picasso.get()
                .load(imageURL)
                .error(R.drawable.avatar)
                .placeholder(R.drawable.avatar)
                .into(profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if(checkReadPermission()){
                        pickImage();
                    }
                }else{
                    pickImage();
                }
            }
        });


        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedData.currentTailor.setName(name.getText().toString());
                SharedData.currentTailor.setAddress(address.getText().toString());
                SharedData.currentTailor.setAvatar(imageURL);
                SharedData.currentTailor.setEmail(email.getText().toString());
                SharedData.currentTailor.setPassword(password.getText().toString());
                SharedData.currentTailor.setPhone(phone.getText().toString());

                new TailorController().Save(SharedData.currentTailor, new TailorCallback() {
                    @Override
                    public void onSuccess(ArrayList<Tailor> tailors) {
                        Toast.makeText(getActivity(), getString(R.string.account_updated_successfully), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });
            }
        });

        return root;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkReadPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            int permissionWriteExternal = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_MEDIA_IMAGES);
            if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{ Manifest.permission.READ_MEDIA_IMAGES},2);
                return false;
            }else{
                return true;
            }
        }else{
            int permissionWriteExternal = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},2);
                return false;
            }else{
                return true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImage();
            }
        }
    }

    private void pickImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }


            loadingHelper.showLoading(getString(R.string.uploading_image));
            new ComplaintsController().uploadImage(data.getData(), new ImageCallback() {

                @Override
                public void onSuccess(String URL) {
                    imageURL = URL;
                    loadingHelper.dismissLoading();
                    Picasso.get().load(URL).placeholder(R.drawable.avatar).error(R.drawable.avatar).into(profileImage);

                }

                @Override
                public void onFail(String msg) {
                    loadingHelper.dismissLoading();
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }


            });
        }
    }


}