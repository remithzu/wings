package com.rmtz.wings.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rmtz.wings.R
import com.rmtz.wings.commons.Utils.md5
import com.rmtz.wings.data.AppDatabase
import com.rmtz.wings.data.DbTemp
import com.rmtz.wings.data.DbUser
import com.rmtz.wings.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isLogin = true
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "Wings Database"
        ).allowMainThreadQueries().build()
        val user = database.UserDao().getListUser()
        if (user.isEmpty()) {
            isLogin = false
            binding.textView.text = getString(R.string.register)
            binding.tilFullName.visibility = View.VISIBLE
            binding.tilRePassword.visibility = View.VISIBLE
            binding.btnSubmit.text = getString(R.string.register)
        } else {
            isLogin = true
            binding.textView.text = getString(R.string.login)
            binding.tilFullName.visibility = View.GONE
            binding.tilRePassword.visibility = View.GONE
            binding.tilRePassword.error = null
            binding.tilRePassword.isErrorEnabled = false
            binding.btnSubmit.text = getString(R.string.login)
        }
        initListener()
        validate()
    }

    private fun initListener() {
        binding.apply {
            btnSubmit.setOnClickListener {
                if (isLogin) {
                    try {
                        val login = database.UserDao().getLogin(tieUsername.text.toString(), tiePassword.text.toString().md5())
                        database.TempDao().insertTemp(DbTemp(
                            id = 1,
                            name = login.name,
                            username = login.username
                        ))
                        findNavController().navigate(R.id.action_login_to_product)
                    } catch (e: Exception) {
                        Snackbar.make(btnSubmit, "Wrong Username of Password!", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    try {
                        database.UserDao().insertUser(
                            DbUser(
                                id = 0,
                                name = tieFullName.text.toString(),
                                username = tieUsername.text.toString(),
                                password = tiePassword.text.toString().md5()
                            ))

                        database.TempDao().insertTemp(DbTemp(
                            id = 1,
                            name = tieFullName.text.toString(),
                            username = tieUsername.text.toString()
                        ))
                        findNavController().navigate(R.id.action_login_to_product)
                    } catch (e: Exception) {
                        Snackbar.make(btnSubmit, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            tieFullName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tieUsername.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tiePassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tieRePassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    p0.let {
                        val value = p0.toString()
                        if (binding.tiePassword.text.toString() != value) {
                            binding.tilRePassword.isErrorEnabled = true
                            binding.tilRePassword.error = getString(R.string.password_error_not_match)
                        } else {
                            binding.tilRePassword.isErrorEnabled = false
                            binding.tilRePassword.error = null
                        }
                    }
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private fun validate(): Boolean {
        val isMatch = binding.tiePassword.text.toString() == binding.tieRePassword.text.toString()
        Log.e("wLog", "isMatch:: $isMatch")
        Log.e("wLog", "isLogin:: $isLogin")
        val result = if (isLogin) {
            !binding.tieUsername.text.isNullOrBlank() &&
                    !binding.tiePassword.text.isNullOrBlank()
        } else {
            !binding.tieFullName.text.isNullOrBlank() &&
                    !binding.tieUsername.text.isNullOrBlank() &&
                    !binding.tiePassword.text.isNullOrBlank() &&
                    isMatch
        }
        binding.btnSubmit.isEnabled = result
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}