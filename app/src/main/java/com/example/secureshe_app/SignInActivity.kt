package com.example.secureshe_app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.secureshe_app.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth=FirebaseAuth.getInstance()

        binding.textViewRegister.setOnClickListener{
            val intent=Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener{
            val email=binding.editTextEmail.text.toString()
            val pass=binding.editTextPassword.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()){

                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intent = Intent(this,HomeActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }

            }else{
                Toast.makeText(this,"Empty Fields are not allowed", Toast.LENGTH_SHORT).show()

            }
        }

    }
}
