package com.akil.learnlog;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    TextView emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailEditText=findViewById(R.id.email_id_text);
        passwordEditText=findViewById(R.id.password_id_text);
        confirmPasswordEditText=findViewById(R.id.passcheck_id_text);
        createAccountBtn=findViewById(R.id.create_account_text_view_btn);
        progressBar=findViewById(R.id.progressbtn);
        loginBtnTextView=findViewById(R.id.login_text);
        createAccountBtn.setOnClickListener(v-> createAccount());
        loginBtnTextView.setOnClickListener(v-> finish());
    }
    void createAccount(){
        String email=emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmpassword= confirmPasswordEditText.getText().toString();
        boolean isValidated=validateData(email,password,confirmpassword);
        if(!isValidated){
            return;
        }
        createAccountInFirebase(email,password);
    }
    void createAccountInFirebase(String email,String password){
        changeInProgress(true);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            //account created
                            Utility.showToast(CreateAccountActivity.this,"Account created succesfully and plz Verify the mail-ID");

                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else{
                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());

                        }
                    }
                }
        );

    }
    void changeInProgress(boolean inProgress){
       if(inProgress){
           progressBar.setVisibility(View.VISIBLE);
           createAccountBtn.setVisibility(View.GONE);
       }
       else{
           progressBar.setVisibility(View.GONE);
           createAccountBtn.setVisibility(View.VISIBLE);
       }
    }
    boolean validateData(String email,String password,String confirmPassword){
       if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
           emailEditText.setError("Invalid e-mail !");
           return false;
       }
       if(password.length()<6){
           passwordEditText.setError("!nvalid");
           return false;
       }
       if(!password.equals(confirmPassword)){
           confirmPasswordEditText.setError("password doesn't match");
           return false;
       }
       return true;
    }
}