package com.rmtz.wings.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rmtz.wings.R
import com.rmtz.wings.commons.Utils.toNumber
import com.rmtz.wings.data.AppDatabase
import com.rmtz.wings.data.DbProduct
import com.rmtz.wings.databinding.FragmentProductAddBinding

class ProductAddFragment : Fragment() {
    private var _binding: FragmentProductAddBinding? = null
    private val binding get() = _binding!!
    private var isUpdate = false
    private lateinit var database: AppDatabase
    private val id: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = binding.materialToolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_add_product_to_product)
        }

        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "Wings Database"
        ).allowMainThreadQueries().build()
        initListener()
        validate()
    }

    private fun initListener() {
        binding.apply {
            btnSubmit.setOnClickListener {
                if (isUpdate) {
                    try {
                        database.ProductDao().updateProduct(DbProduct(
                            id = id,
                            code = tieProductCode.text.toString(),
                            name = tieProductName.text.toString(),
                            price = tiePrice.text.toString().toNumber(),
                            currency = tieCurrency.text.toString(),
                            discount = tieDiscount.text.toString().toNumber(),
                            dimension = tieDimension.text.toString(),
                            unit = tieUnit.text.toString()
                        ))
                        findNavController().navigate(R.id.action_add_product_to_product)
                    } catch (e: Exception) {
                        Snackbar.make(btnSubmit, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    try {
                        val id = database.ProductDao().getLatestId() + 1
                        database.ProductDao().insertProduct(DbProduct(
                            id = id,
                            code = tieProductCode.text.toString(),
                            name = tieProductName.text.toString(),
                            price = tiePrice.text.toString().toNumber(),
                            currency = tieCurrency.text.toString(),
                            discount = tieDiscount.text.toString().toNumber(),
                            dimension = tieDimension.text.toString(),
                            unit = tieUnit.text.toString()
                        ))
                        findNavController().navigate(R.id.action_add_product_to_product)
                    } catch (e: Exception) {
                        Snackbar.make(btnSubmit, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            tieProductCode.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tieProductName.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tiePrice.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tieCurrency.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tieDiscount.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tieDimension.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            tieUnit.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    validate()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private fun validate(): Boolean {
        val result =!binding.tieProductCode.text.isNullOrBlank() &&
        !binding.tieProductName.text.isNullOrBlank() &&
        !binding.tiePrice.text.isNullOrBlank() &&
        !binding.tieCurrency.text.isNullOrBlank() &&
        !binding.tieDiscount.text.isNullOrBlank() &&
        !binding.tieDimension.text.isNullOrBlank() &&
        !binding.tieUnit.text.isNullOrBlank()
        binding.btnSubmit.isEnabled = result
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}