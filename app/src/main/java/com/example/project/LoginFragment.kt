package com.example.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.project.databinding.FragmentLoginBinding
import com.example.project.retrofit.AuthRequest
import com.example.project.retrofit.MainApi
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by activityViewModels() // надо разобраться что это такое
    private lateinit var mainApi : MainApi

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRetrofit()

        binding.apply {

            buttonSecond.setOnClickListener {
                auth(
                    AuthRequest(
                        userName.text.toString(),
                        password.text.toString()
                    )
                )
            }

            buttonFirst.setOnClickListener {
                findNavController().navigate(R.id.action_LoginFragment_to_ProductsFragment)
            }
        }
    }

    private fun initRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mainApi = retrofit.create(MainApi::class.java)
    }

    private fun auth(authRequest: AuthRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainApi.auth(authRequest)
            val message = response.errorBody()?.string()?.let { JSONObject(it).getString("message") }
            requireActivity().runOnUiThread {
                Snackbar.make(requireView(), message.toString(), Snackbar.LENGTH_SHORT).show()
                val user = response.body()
                if (user != null) {
                    Picasso.get().load(user.image).into(binding.imageView)
                    binding.firstName.text = user.firstName
                    binding.lastName.text = user.lastName
                    binding.buttonFirst.visibility = View.VISIBLE
                    viewModel.token.value = user.token
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}