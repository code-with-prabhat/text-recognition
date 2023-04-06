package com.textrecogination;

import android.content.Intent; 
import android.graphics.Bitmap; 
import android.os.Bundle; 
import android.provider.MediaStore; 
import android.view.View; 
import android.widget.Button; 
import android.widget.ImageView; 
import android.widget.TextView; 
import android.net.Uri;
import android.widget.Toast; 
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull; 
import androidx.appcompat.app.AppCompatActivity; 
import com.google.android.gms.tasks.OnFailureListener; 
import com.google.android.gms.tasks.OnSuccessListener; 
import com.google.firebase.ml.vision.FirebaseVision; 
import com.google.firebase.ml.vision.common.FirebaseVisionImage; 
import com.google.firebase.ml.vision.text.FirebaseVisionText; 
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector; 
import java.util.List; 

  

public class MainActivity extends AppCompatActivity {

    private ImageView img; 
    private TextView textview; 
    private Button snapBtn; 
    private Button detectBtn, storeImage; 
    
    int io = 101;

    private Bitmap imageBitmap; 
    static final int REQUEST_IMAGE_CAPTURE = 1; 

      @Override
      protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main); 
        img = (ImageView) findViewById(R.id.image); 
        textview = (TextView) findViewById(R.id.text);
        snapBtn = (Button) findViewById(R.id.snapbtn); 
        detectBtn = (Button) findViewById(R.id.detectbtn);
        storeImage = (Button) findViewById(R.id.storeimage);    
    



         storeImage.setOnClickListener(new View.OnClickListener() { 
            @Override
            public void onClick(View v) { 
                  Intent i = new Intent();
                  i.setType("image/*");
                  i.setAction(Intent.ACTION_GET_CONTENT);
                  startActivityForResult(Intent.createChooser(i,"select picture"),io);
            } 
        }); 


        detectBtn.setOnClickListener(new View.OnClickListener() { 
            @Override
            public void onClick(View v) { 
                detectTxt(); 
            } 
        }); 
            
            
            
            
        snapBtn.setOnClickListener(new View.OnClickListener() { 
            @Override
            public void onClick(View v) { 
                dispatchTakePictureIntent(); 
            } 
        }); 
    } 



    private void dispatchTakePictureIntent() { 
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) { 
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE); 

        } 

    } 




   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        super.onActivityResult(requestCode, resultCode, data); 
            
         if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { 
            Bundle extras = data.getExtras(); 
            imageBitmap = (Bitmap) extras.get("data"); 
            img.setImageBitmap(imageBitmap); 

        }    
            
            
        if(resultCode == RESULT_OK){
                      if(requestCode == io){
                          Uri imageuri = data.getData();
                              if(imageuri != null ){
                                  try{
                                     img.setImageURI(imageuri);
                                      
                                     imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageuri);  
                                  }catch(Exception e){
                                       
                                  }
                              }
                      }
        }
                  
    } 
    
    

  

    private void detectTxt() { 
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap); 
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector(); 
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() { 
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processTxt(firebaseVisionText); 

            } 

        }).addOnFailureListener(new OnFailureListener() { 
            @Override
            public void onFailure(@NonNull Exception e) { 
                Toast.makeText(MainActivity.this, "Fail to detect the text from image..", Toast.LENGTH_SHORT).show(); 

            } 

        }); 

    } 

  
  
  

    private void processTxt(FirebaseVisionText text) { 
        List<FirebaseVisionText.Block> blocks = text.getBlocks(); 
        if (blocks.size() == 0) { 
            Toast.makeText(MainActivity.this, "No Text ", Toast.LENGTH_LONG).show(); 
        } 

        for (FirebaseVisionText.Block block : text.getBlocks()) { 
            String txt = block.getText(); 
            textview.setText(txt); 

        } 

    } 
}