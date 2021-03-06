package edu.bluejack19_1.BloodFOR.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import edu.bluejack19_1.BloodFOR.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

import edu.bluejack19_1.BloodFOR.InsertDataActivity;
import edu.bluejack19_1.BloodFOR.LoginActivity;
import edu.bluejack19_1.BloodFOR.MainActivity;
import edu.bluejack19_1.BloodFOR.Model.User;
import edu.bluejack19_1.BloodFOR.PasswordActivity;

public class ProfileFragment extends Fragment {
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    public static ImageView profilePic;
    private EditText email,firstName,lastName;
    private FirebaseAuth auth;
    private FirebaseDatabase getDatabase;
    private DatabaseReference getReference;
    private String GetUserID, role, profPic;
    private String email2;
    private static String downloadURL;
    private String password;
    private RadioButton maleProfile, femaleProfile;
    private Button redeemBtn, logoutBtn, changeProfilePictureBtn, saveBtn, changePassword, uploadBtn;
    private static StorageReference storageReference;
    private static Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private FirebaseStorage storage;
    private StorageReference ref;
    private RadioButton a,b,o,ab;
    public static View view2;
    private TextView point;
    private int checkUpload;
    static FragmentActivity activity;
    public static ProgressBar pbar;
    private String pointupdate;
    private FirebaseUser user;

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        view2 = view;
        profPic = "";
        auth = FirebaseAuth.getInstance();
        activity = (FragmentActivity)view.getContext();
        changeProfilePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(view);
            }
        });
//        Log.d("password",LoginActivity.pass);
        Log.d("passwordanda",loginPreferences.getString("password",""));
        user = FirebaseAuth.getInstance().getCurrentUser();
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(view);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PasswordActivity.class);
                String email = MainActivity.email;
                String uid = MainActivity.uid;
                Boolean cek = MainActivity.cekGoogle;
                i.putExtra("email", loginPreferences.getString("email",""));
                i.putExtra("uid",uid);
                i.putExtra("cek",cek);
//                password = LoginActivity.pass;


                i.putExtra("password",loginPreferences.getString("password",""));
                Objects.requireNonNull(getActivity()).finish();
                startActivity(i);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!profPic.equals("") && profPic != null)
                    profPic = downloadURL;
                final String emailTxt = email.getText().toString();
                final String firstNameTxt = firstName.getText().toString();
                String lastNameTxt = lastName.getText().toString();
                String gender = "";
                if(maleProfile.isChecked()){
                    gender = "Male";
                }else{
                    gender = "Female";
                }
                String bloodType = "";
                if(a.isChecked()) bloodType = "A";
                else if(b.isChecked()) bloodType = "B";
                else if(ab.isChecked()) bloodType = "AB";
                else if (o.isChecked()) bloodType = "O";
                else bloodType = "-";

                final User saveData = new User( profPic, firstNameTxt, lastNameTxt, emailTxt, gender, bloodType, role,point.getText().toString());
                getReference.child("User").child(GetUserID).setValue(saveData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"Success Update Profile",Toast.LENGTH_LONG).show();
                    }
                });
                auth.signInWithEmailAndPassword(loginPreferences.getString("email",""),loginPreferences.getString("password","")).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        user.updateEmail(emailTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Update Email Success", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "Update Email Failed", Toast.LENGTH_LONG).show();
                                }
                            }

                        });
                        loginPrefsEditor.putString("email",emailTxt);
                    }

                });
//                getReference.child("User").child(GetUserID).child("point").setValue("100");
                Glide.with(view)
                        .load(saveData.getProfilePicture())
                        .apply(new RequestOptions().override(400, 400))
                        .into(profilePic);
            }
        });



        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ListItemRedeemFragment(), true);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                MainActivity.uid = "";
                MainActivity.cekGoogle = false;
                MainActivity.email = "";
                loginPrefsEditor.remove("email");
                loginPrefsEditor.apply();
                startActivity(i);
                getActivity().finish();

            }
        });
    }

    private boolean loadFragment(Fragment fragment, boolean check) {
        if (fragment != null && check) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment).addToBackStack(null).commit();
            return true;
        }else if (fragment != null && !check) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment).commit();
            return true;
        }
        return false;
    }

    private void chooseImage(final View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        filePath = MainActivity.filePath;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public static void changeImage(View view, Bitmap bitmap){
        Glide.with(view)
                .load(bitmap)
                .apply(new RequestOptions().override(400, 400))
                .into(profilePic);
    }

    public static String uploadImage(Uri path) {
        filePath = path;
        if(filePath != null) {
            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pbar.setVisibility(View.GONE);
                            Toast.makeText(activity,"Update Image Success", Toast.LENGTH_LONG).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadURL = uri.toString();
                                    System.out.println("test download" + downloadURL);
                                }
                            });
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        return downloadURL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    private void init(final View view){
        loginPreferences = getActivity().getSharedPreferences("loginPrefs",getActivity().MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        pbar = view.findViewById(R.id.loading_bar);
        point = view.findViewById(R.id.point);
        profilePic = view.findViewById(R.id.profile_picture);
        email = view.findViewById(R.id.email_profile);
        firstName = view.findViewById(R.id.first_name_profile);
        lastName = view.findViewById(R.id.last_name_profile);
        maleProfile = view.findViewById(R.id.radio_male);
        femaleProfile = view.findViewById(R.id.radio_female);
        changeProfilePictureBtn = view.findViewById(R.id.choose_button);
        changePassword = view.findViewById(R.id.password_button);
        saveBtn = view.findViewById(R.id.save_button);
        redeemBtn = view.findViewById(R.id.redeem_button);
        logoutBtn = view.findViewById(R.id.logout_button);
        uploadBtn = view.findViewById(R.id.upload_button);
        a = view.findViewById(R.id.type_a);
        b = view.findViewById(R.id.type_b);
        ab = view.findViewById(R.id.type_ab);
        o = view.findViewById(R.id.type_o);
        auth = FirebaseAuth.getInstance();
        if(!MainActivity.cekGoogle && !MainActivity.cekFb) {
            FirebaseUser user = auth.getCurrentUser();
            GetUserID = user.getUid();
            email2 = user.getEmail();
        }
        else if(MainActivity.cekFb){
            GetUserID = MainActivity.uid;
            email2 = MainActivity.email;
        }
        else{
            GetUserID = MainActivity.uid;
            email2 = MainActivity.email;
        }
        getDatabase = FirebaseDatabase.getInstance();
        getReference = getDatabase.getReference();
        pbar.setVisibility(view.GONE);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        getReference.child("User").orderByChild("email").equalTo(email2).addChildEventListener(new ChildEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String profilePicUser = dataSnapshot.child("profilePicture").getValue(String.class);
                final String emailUser = dataSnapshot.child("email").getValue(String.class);
                final String firstNameUser = dataSnapshot.child("firstName").getValue(String.class);
                final String lastNameUser = dataSnapshot.child("lastName").getValue(String.class);
                final String genderUser = dataSnapshot.child("gender").getValue(String.class);
                final String bloodTypeUser = dataSnapshot.child("bloodType").getValue(String.class);
                final String points = dataSnapshot.child("point").getValue(String.class);
                Log.d("point",points+"");

                role = dataSnapshot.child("role").getValue(String.class);
                profPic = profilePicUser;
                downloadURL = profilePicUser;
                User user = new User(profilePicUser, firstNameUser, lastNameUser, emailUser, genderUser, bloodTypeUser, role,points);
                email.setText(""+user.getEmail());
                firstName.setText(""+user.getFirstName());
                lastName.setText(""+user.getLastName());
                point.setText(dataSnapshot.child("point").getValue(String.class));
                if(user.getGender().equals("Male")){
                    maleProfile.setChecked(true);
                }else{
                    femaleProfile.setChecked(true);
                }
                if(user.getBloodType().equals("A")) a.setChecked(true);
                else if(user.getBloodType().equals("B")) b.setChecked(true);
                else if(user.getBloodType().equals("AB")) ab.setChecked(true);
                else if(user.getBloodType().equals("O")) o.setChecked(true);
                Glide.with(view)
                        .load(user.getProfilePicture())
                        .apply(new RequestOptions().override(400, 400))
                        .into(profilePic);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}