package com.blackviking.hosh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackviking.hosh.ImageViewers.BlurImage;
import com.blackviking.hosh.Model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.rohitarya.picasso.facedetection.transformation.FaceCenterCrop;
import com.rohitarya.picasso.facedetection.transformation.core.PicassoFaceDetector;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OtherUserProfile extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton messageUserFab, followUserFab;
    private String userId, currentUid;
    private ImageView userProfileImage, coverPhoto;
    private TextView username, status, online, gender, followersCount, location, interest, dateJoined, bio;
    private RecyclerView userGalleryRecycler;
    private LinearLayoutManager layoutManager;
    private Button viewFollowers;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference userRef;
    private CoordinatorLayout rootLayout;
    private UserModel currentUser;
    private int BLUR_PRECENTAGE = 50;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*---   FONT MANAGEMENT   ---*/
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Wigrum-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_other_user_profile);


        /*---   TOOLBAR   ---*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*---   IMAGE FACE DETECTION   ---*/
        PicassoFaceDetector.initialize(this);


        /*---   LOCAL   ---*/
        Paper.init(this);


        /*---   FIREBASE   ---*/
        if (mAuth.getCurrentUser() != null)
            currentUid = mAuth.getCurrentUser().getUid();
        userRef = db.getReference("Users");


        /*---   INTENT DATA   ---*/
        userId = getIntent().getStringExtra("UserId");


        /*---   WIDGETS   ---*/
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        messageUserFab = (FloatingActionButton)findViewById(R.id.messageUser);
        followUserFab = (FloatingActionButton)findViewById(R.id.followUser);
        coverPhoto = (ImageView)findViewById(R.id.userProfilePictureBlur);
        userProfileImage = (ImageView)findViewById(R.id.userProfilePicture);
        rootLayout = (CoordinatorLayout)findViewById(R.id.otherUserProfileRootLayout);
        username = (TextView)findViewById(R.id.userUsername);
        status = (TextView)findViewById(R.id.userStatus);
        online = (TextView)findViewById(R.id.userOnlineStatus);
        gender = (TextView)findViewById(R.id.userGender);
        followersCount = (TextView)findViewById(R.id.userFollowers);
        location = (TextView)findViewById(R.id.userLocation);
        interest = (TextView)findViewById(R.id.userInterest);
        dateJoined = (TextView)findViewById(R.id.userDateJoined);
        bio = (TextView)findViewById(R.id.userBio);
        userGalleryRecycler = (RecyclerView)findViewById(R.id.userGalleryRecycler);
        viewFollowers = (Button)findViewById(R.id.viewUserFollowers);


        /*---   TOOLBAR   ---*/
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);


        /*---   RECYCLER CONTROLLER   ---*/
        userGalleryRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        userGalleryRecycler.setLayoutManager(layoutManager);


        loadUserProfile(userId);

    }

    private void loadUserProfile(final String userId) {

        final SimpleArcDialog mDialog = new SimpleArcDialog(this);
        mDialog.setConfiguration(new ArcConfiguration(this));

        ArcConfiguration configuration = new ArcConfiguration(this);
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.COMPLETE_ARC);
        configuration.setText("Fetching Details . . .");

        mDialog.setConfiguration(configuration);

        mDialog.show();


        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDialog.dismiss();
                currentUser = dataSnapshot.getValue(UserModel.class);

                /*---   USER NAME   ---*/
                collapsingToolbarLayout.setTitle("@"+currentUser.getUserName());
                username.setText("@"+currentUser.getUserName());


                /*---   IMAGE   ---*/
                if (!currentUser.getProfilePictureThumb().equals("")){

                    /*---   PROFILE IMAGE   ---*/
                    Picasso.with(getBaseContext())
                            .load(currentUser.getProfilePictureThumb())
                            .placeholder(R.drawable.ic_loading_animation)
                            .transform(new FaceCenterCrop(400, 400))
                            .into(userProfileImage);


                    /*---   BLUR COVER   ---*/
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            coverPhoto.setImageBitmap(BlurImage.fastblur(bitmap, 1f,
                                    BLUR_PRECENTAGE));
                        }
                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            coverPhoto.setImageResource(R.drawable.empty_profile);
                        }
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };


                    /*---   COVER PHOTO   ---*/
                    Picasso.with(getBaseContext())
                            .load(currentUser.getProfilePictureThumb())
                            .placeholder(R.drawable.ic_loading_animation)
                            .centerCrop()
                            .into(target);



                }


                /*---   FABs   ---*/
                messageUserFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(rootLayout, "Messaging Under Dev !", Snackbar.LENGTH_LONG).show();
                    }
                });

                userRef.child(currentUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("Following").child(userId).exists()){

                            followUserFab.setImageResource(R.drawable.ic_unfollow_user);

                            userRef.child(currentUid).child("Following").child(userId).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Snackbar.make(rootLayout, "You have un followed @"+currentUser.getUserName(), Snackbar.LENGTH_LONG).show();
                                        }
                                    });

                        } else {

                            followUserFab.setImageResource(R.drawable.ic_follow_user);

                            userRef.child(currentUid).child("Following").child(userId).setValue("Following")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Snackbar.make(rootLayout, "You are now following @"+currentUser.getUserName(), Snackbar.LENGTH_LONG).show();
                                        }
                                    });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                /*---   ONLINE, BIO, GENDER, LOCATION, INTEREST, DATE JOINED   ---*/
                online.setText(currentUser.getOnlineState());
                bio.setText(currentUser.getBio());
                gender.setText(currentUser.getSex());
                location.setText(currentUser.getLocation());
                interest.setText(currentUser.getLookingFor());
                dateJoined.setText(currentUser.getDateJoined());

                /*---   BIO   ---*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
