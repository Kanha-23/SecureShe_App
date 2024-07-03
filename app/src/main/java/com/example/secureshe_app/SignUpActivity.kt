package com.example.secureshe_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.secureshe_app.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth=FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener{
            val email=binding.editTextEmail.text.toString()
            val pass=binding.editTextPassword.text.toString()
            val confirmPass=binding.editTextConPassword.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                if(pass==confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                       if(it.isSuccessful){
                            val intent = Intent(this,SignInActivity::class.java)
                            startActivity(intent)
                       }else{
                           Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()

                       }
                    }
                }else{
                    Toast.makeText(this,"password is not matching",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Empty Fields are not allowed",Toast.LENGTH_SHORT).show()

            }
        }
    }
}
