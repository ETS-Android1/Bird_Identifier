package com.example.bird_identifier;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bird_identifier.ml.TfModel;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    File output;
    Button btnLoadImage;
//    ImageView btnCaptureImage;
    TextView tvResult;
    ImageView ivAddImage;
//    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> mGetContent;

//    public void setStatusBarColor(View statusBar,int color){
//            statusBar.setBackgroundColor(color);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ivAddImage=findViewById(R.id.iv_add_image);
        tvResult=findViewById(R.id.result_here);
        btnLoadImage=findViewById(R.id.bt_load_image);
//        btnCaptureImage=findViewById(R.id.btn_camera);
        getWindow().setStatusBarColor(getResources().getColor(R.color.status));

        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
               Bitmap b=null;
               if(result==null){
                   startActivity(new Intent(getApplicationContext(),MainActivity.class));
               }
               try{
                   b=UriToBitmap(result);
               }catch(IOException e){
                   e.printStackTrace();
                }

               ivAddImage.setImageBitmap(b);
               outputGenerator(b);
            }
        });

        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        tvResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q="+tvResult.getText().toString()));
                startActivity(it);
            }
        });

//        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File dir=
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//
//                output=new File(dir, "CameraContentDemo.jpeg");
//                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
//
//                startActivityForResult(i, 2);
//            }
//        });

    }

    private void outputGenerator(Bitmap b) {
        try {
            TfModel model = TfModel.newInstance(MainActivity.this);

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(b);

            // Runs model inference and gets result.
            TfModel.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            int index=0;
            float max=probability.get(0).getScore();
            for(int i=0;i<probability.size();i++){
                if(max<probability.get(i).getScore()){
                    max=probability.get(i).getScore();
                    index=i;
                }
            }

            Category output=probability.get(index);
            tvResult.setText(output.getLabel());
            Log.d("birds", ""+output.getLabel());

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private Bitmap UriToBitmap(Uri result) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(),result);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==1){
//            Uri u=data.getData();
//            ivAddImage.setImageURI(u);
//        }
////        else if(requestCode==2){
////            if (resultCode == RESULT_OK) {
////                Intent i=new Intent(Intent.ACTION_VIEW);
////
////                i.setDataAndType(Uri.fromFile(output), "image/jpeg");
////                startActivity(i);
//////                finish();
////            }
////        }
//    }
}