package com.example.autorenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import com.google.android.material.textfield.TextInputLayout;
import okhttp3.*;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddCarActivity extends AppCompatActivity{
    public int nextID=0;
    public ImageButton[] arrayImageButton = new ImageButton[10];
    Uri[] arrayImageButtonURI = new Uri[10];
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        Context context = getApplicationContext();
        if(!gv.isLogInStatus()){
            CharSequence ToastTextMsg = "You are not logged in, Please login first!";
            Toast toast = Toast.makeText(context, ToastTextMsg, Toast.LENGTH_SHORT);
            toast.show();
            Intent i = new Intent(AddCarActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        Button BtnCancel = findViewById(R.id.btn_cancel);
        BtnCancel.setOnClickListener(view -> finish());

        //region DropDown Items handler

        TextInputLayout textInputLayout_duration_unit;
        AutoCompleteTextView AutoCompleteTextView_duration_unit;

        TextInputLayout textInputLayout_age_unit;
        AutoCompleteTextView AutoCompleteTextView_age_unit;

        TextInputLayout textInputLayout_trnas;
        AutoCompleteTextView autoCompleteTextView_Trans;

        TextInputLayout textInputLayout_available;
        AutoCompleteTextView autoCompleteTextView_available;

        textInputLayout_duration_unit = findViewById(R.id.input_duration_unit_drop);
        AutoCompleteTextView_duration_unit = findViewById(R.id.input_duration_unit_items);
        String[] optionDurationUnit = {"Years", "Months", "Days"};
        ArrayAdapter<String> itemAdapterDurationUnit = new ArrayAdapter(AddCarActivity.this, R.layout.items_list, optionDurationUnit);
        AutoCompleteTextView_duration_unit.setAdapter(itemAdapterDurationUnit);

        textInputLayout_available = findViewById(R.id.input_available_drop);
        autoCompleteTextView_available = findViewById(R.id.input_available_items);
        String[] optionAvailable = {"Available", "Rented"};
        ArrayAdapter<String> itemAdapterAvailable = new ArrayAdapter(AddCarActivity.this, R.layout.items_list, optionAvailable);
        autoCompleteTextView_available.setAdapter(itemAdapterAvailable);

        textInputLayout_age_unit = findViewById(R.id.input_age_unit_drop);
        AutoCompleteTextView_age_unit = findViewById(R.id.input_age_unit_items);
        String[] optionAgeUnit = {"Years", "Months"};
        ArrayAdapter<String> itemAdapterAgeUnit = new ArrayAdapter(AddCarActivity.this, R.layout.items_list, optionAgeUnit);
        AutoCompleteTextView_age_unit.setAdapter(itemAdapterAgeUnit);

        textInputLayout_trnas = findViewById(R.id.input_trans_drop);
        autoCompleteTextView_Trans = findViewById(R.id.input_trans_items);
        String[] optionTrans = {"Auto", "Manual"};
        ArrayAdapter<String> itemAdapterTrans = new ArrayAdapter(AddCarActivity.this, R.layout.items_list, optionTrans);
        autoCompleteTextView_Trans.setAdapter(itemAdapterTrans);
        //endregion

        imageButtonInit();
        Button postBtn = findViewById(R.id.btn_post);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] inputStringHandler = new String[20];

                //region Init element holder
                TextInputLayout priceTextLayout = findViewById(R.id.input_price);
                EditText inputPriceText = findViewById(R.id.input_price_text);
                String inputPrice = inputPriceText.getText().toString();

                TextInputLayout brandTextLayout = findViewById(R.id.input_brand);
                EditText brandText = findViewById(R.id.input_brand_text);
                String inputBrand = brandText.getText().toString();

                TextInputLayout modelTextLayout = findViewById(R.id.input_model);
                EditText modelText = findViewById(R.id.input_model_text);
                String inputModel = modelText.getText().toString();

                TextInputLayout MaxDurTextLayout = findViewById(R.id.Max_duration_of_rent);
                EditText MaxDurText = findViewById(R.id.Max_duration_of_rent_text);
                String inputMaxDur = MaxDurText.getText().toString();

                TextInputLayout durUnitTextLayout = findViewById(R.id.input_duration_unit_drop);
                EditText durUnitText = findViewById(R.id.input_duration_unit_items);
                String inputDurUnit = durUnitText.getText().toString();

                TextInputLayout locationTextLayout = findViewById(R.id.input_location);
                EditText locationText = findViewById(R.id.input_location_text);
                String inputLocation = locationText.getText().toString();

                TextInputLayout availableTextLayout = findViewById(R.id.input_available_drop);
                EditText inputAvailableText = findViewById(R.id.input_available_items);
                String inputAvailable = inputAvailableText.getText().toString();


                EditText inputDescText = findViewById(R.id.input_description_text);
                String inputDesc = inputDescText.getText().toString();

                EditText inputColorText = findViewById(R.id.input_color_text);
                String inputColor = inputColorText.getText().toString();

                EditText inputNumOfSeatText = findViewById(R.id.input_numOfSeat_text);
                String inputNumOfSeat = inputNumOfSeatText.getText().toString();

                EditText inputTypeOfGasText = findViewById(R.id.input_typeOfGas_text);
                String inputTypeOfGas = inputTypeOfGasText.getText().toString();

                EditText inputTypeOfVehicleText = findViewById(R.id.input_typeOfVehicle_text);
                String inputTypeOfVehicle = inputTypeOfVehicleText.getText().toString();

                EditText inputVehicleMileageText = findViewById(R.id.input_vehicleMileage_text);
                String inputVehicleMileage = inputVehicleMileageText.getText().toString();

                EditText inputAgeOfVehicleText = findViewById(R.id.input_ageOfVehicle_text);
                String inputAgeOfVehicle = inputAgeOfVehicleText.getText().toString();

                EditText inputAgeUnitItemsText = findViewById(R.id.input_age_unit_items);
                String inputAgeUnit = inputAgeUnitItemsText.getText().toString();

                EditText inputTransItemsText = findViewById(R.id.input_trans_items);
                String inputTrans = inputTransItemsText.getText().toString();

                EditText inputDisOfEngineText = findViewById(R.id.input_displacementOfEngine_text);
                String inputDisOfEngine = inputDisOfEngineText.getText().toString();

                //endregion

                boolean inputErrorFlag = false;

                //region Input validate
                if (inputPrice.isEmpty()) {
                    priceTextLayout.setErrorEnabled(true);
                    priceTextLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    brandTextLayout.setErrorEnabled(false);
                    inputStringHandler[4] = inputPrice;
                }

                if (inputBrand.isEmpty()) {
                    brandTextLayout.setErrorEnabled(true);
                    brandTextLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    brandTextLayout.setErrorEnabled(false);
                    inputStringHandler[2] = inputBrand;
                }
                if (inputModel.isEmpty()) {
                    modelTextLayout.setErrorEnabled(true);
                    modelTextLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    modelTextLayout.setErrorEnabled(false);
                    inputStringHandler[3] = inputModel;
                }
                if (inputMaxDur.isEmpty()) {
                    MaxDurTextLayout.setErrorEnabled(true);
                    MaxDurTextLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    MaxDurTextLayout.setErrorEnabled(false);
                    inputStringHandler[7] = inputMaxDur;
                }
                if (!(inputDurUnit.equals("Years") || inputDurUnit.equals("Months") || inputDurUnit.equals("Days"))) {
                    durUnitTextLayout.setErrorEnabled(true);
                    durUnitTextLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    durUnitTextLayout.setErrorEnabled(false);
                    inputStringHandler[8] = inputDurUnit;
                }
                if (inputLocation.isEmpty()) {
                    locationTextLayout.setErrorEnabled(true);
                    locationTextLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    locationTextLayout.setErrorEnabled(false);
                    inputStringHandler[6] = inputLocation;
                }

                if (!(inputAvailable.equals("Rented") || inputAvailable.equals("Available"))) {
                    availableTextLayout.setErrorEnabled(true);
                    availableTextLayout.setError("Required");
                    inputErrorFlag = true;
                } else {
                    availableTextLayout.setErrorEnabled(false);
                    if (inputAvailable.equals("Rented")) {
                        inputStringHandler[19] = "0";
                    } else {
                        inputStringHandler[19] = "1";
                    }
                }

                if (!(inputAgeOfVehicle.isEmpty())) {
                    if (!(inputAgeUnit.equals("Years") || inputAgeUnit.equals("Months"))) {
                        textInputLayout_age_unit.setErrorEnabled(true);
                        textInputLayout_age_unit.setError("Required");
                        inputErrorFlag = true;
                    } else {
                        textInputLayout_age_unit.setErrorEnabled(false);
                        inputStringHandler[15] = inputAgeOfVehicle;
                        inputStringHandler[16] = inputAgeUnit;
                    }
                }

                if ((inputTrans.equals("Auto") || inputTrans.equals("Manual"))) inputStringHandler[17] = inputTrans;

                if (!(inputDesc.isEmpty())) inputStringHandler[9] = inputDesc;

                if (!(inputNumOfSeat.isEmpty())) inputStringHandler[10] = inputNumOfSeat;

                if (!(inputColor.isEmpty())) inputStringHandler[11] = inputColor;

                if (!(inputTypeOfGas.isEmpty())) inputStringHandler[12] = inputTypeOfGas;

                if (!(inputTypeOfVehicle.isEmpty())) inputStringHandler[13] = inputTypeOfVehicle;

                if (!(inputDisOfEngine.isEmpty())) inputStringHandler[18] = inputDisOfEngine;

                if (!(inputVehicleMileage.isEmpty())) inputStringHandler[14] = inputVehicleMileage;
                //endregion

                CharSequence ToastTextMsg = "Successfully Added!";
                int duration = Toast.LENGTH_SHORT;

                if (!inputErrorFlag) {
                    inputStringHandler[1] = gv.getLogInUsrID();
                    StringBuilder sqlStatement = new StringBuilder();
                    for (int i = 1; i < 20; i++) {
                        if (inputStringHandler[i] == null) {
                            sqlStatement.append("NULL, ");
                            continue;
                        }
                        sqlStatement.append("'").append(inputStringHandler[i]).append("', ");
                    }

                    int SQLLineUpdate = postResult(sqlStatement.substring(0, sqlStatement.length() - 2));       //Uploading to SQL
                    if (SQLLineUpdate == 1) {
                        Toast toast = Toast.makeText(context, ToastTextMsg, duration);
                        toast.show();

                        for(int i=0; i<10; i++){
                            if(arrayImageButtonURI[i]!=null){
                                try {
                                    uploadImage(arrayImageButtonURI[i], String.valueOf(nextID));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                      finish();
                    } else {
                        ToastTextMsg = "Fail!";
                        Toast toast = Toast.makeText(context, ToastTextMsg, duration);
                        toast.show();
                    }
                } else {
                    ToastTextMsg = "Input has error, please check!";
                    Toast toast = Toast.makeText(context, ToastTextMsg, duration);
                    toast.show();
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageButton[] arrayImageButton = new ImageButton[10];
        for (int i = 0; i < 10; i++) {
            String idName = "addPhoto" + i;
            int resID = getResources().getIdentifier(idName, "id", getPackageName());
            arrayImageButton[i] = findViewById(resID);
        }

        if (requestCode == 3) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                for (int i = 0; i < 10; i++) {
                    if (arrayImageButton[i].getTag().toString().equals("noPhoto")) {
                        arrayImageButton[i].setImageURI(selectedImage);
                        arrayImageButtonURI[i] = selectedImage;
                        arrayImageButton[i].setTag("hasPhoto");
                        break;
                    }
                }
            }
        }
        if (requestCode == 104) {
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                for (int i = 0; i < 10; i++) {
                    if (arrayImageButton[i].getTag().toString().equals("noPhoto")) {
                        arrayImageButton[i].setImageURI(Uri.fromFile(f));
                        arrayImageButtonURI[i] = Uri.fromFile(f);
                        arrayImageButton[i].setTag("hasPhoto");
                        break;
                    }
                }
            }
        }

    }

    public void imageButtonInit() {
        for (int i = 0; i < 10; i++) {
            String idName = "addPhoto" + i;
            int resID = getResources().getIdentifier(idName, "id", getPackageName());
            arrayImageButton[i] = findViewById(resID);
            arrayImageButton[i].setOnClickListener(view -> popUpImageClick(view));
        }
    }

    private File createImageFile() throws IOException { /*from: https://developer.android.com/training/camera/photobasics*/
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() { /*from: https://developer.android.com/training/camera/photobasics*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 104);
            }
        }
    }

    public void popUpImageClick(View view) {
        ImageButton currentImageBtn = findViewById(view.getId());
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(AddCarActivity.this)
                .setTitle("Please choose an action")
                .setView(rootLayout);

        AlertDialog popUp = builder.create();

        Button galleryBtn = new Button(this);
        galleryBtn.setText("Choose From Gallery");
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ImageSelectIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(ImageSelectIntent, 3);
                popUp.dismiss();
            }
        });

        Button cameraBtn = new Button(this);
        cameraBtn.setText("Take a picture");
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                popUp.dismiss();
            }
        });

        Button deleteBtn = new Button(this);
        deleteBtn.setText("Delete");
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_addphoto));
                String tempIDtoProcess = getResources().getResourceEntryName(currentImageBtn.getId());
                tempIDtoProcess = tempIDtoProcess.substring(8);
                arrayImageButtonURI[Integer.parseInt(tempIDtoProcess)] = null;
                currentImageBtn.setTag("noPhoto");
                popUp.dismiss();
            }
        });
        if ((view.getTag().toString().equals("noPhoto"))){
            rootLayout.addView(galleryBtn);
            rootLayout.addView(cameraBtn);
        }
        if (!(view.getTag().toString().equals("noPhoto"))) rootLayout.addView(deleteBtn);
        popUp.show();
    }

    public int postResult(String sqlStatement) {
        SQLHandler s = new SQLHandler();
        try {
            ResultSet resultMaxCarID = s.sqlSelect("SELECT MAX(Car_id) FROM CAR");
            resultMaxCarID.first();
            nextID = Integer.parseInt(resultMaxCarID.getString(1));
            nextID++;
            return s.sqlUpdate("INSERT INTO CAR VALUES ("+ "'"+ nextID +"', " + sqlStatement + ");");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("Error", String.valueOf(e));
        }
        return -1;
    }

    public static String getPathFromInputStreamUri(Context context, Uri uri, String fileName) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File file = createTemporalFileFrom(context, inputStream, fileName);
                filePath = file.getPath();

            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }
            }
        }

        return filePath;
    }

    private static File createTemporalFileFrom(Context context, InputStream inputStream, String fileName)
            throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            targetFile = new File(context.getCacheDir(), fileName);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;

    }


    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final OkHttpClient client = new OkHttpClient();

    public void uploadImage(Uri filePath, String carID) throws Exception {
        String filePathConverted = getPathFromInputStreamUri(getApplicationContext(), filePath,"temp_pic");;
        File file = new File(filePathConverted);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("carID", carID)
                .addFormDataPart("file", "something.png", RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();

        Request request = new Request.Builder()
                .url("http://note.dyndns.org/autoRenter/upload.php")
                .post(requestBody)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println(response.body().string());
        }
    }

}