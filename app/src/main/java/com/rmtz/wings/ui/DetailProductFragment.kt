package com.rmtz.wings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rmtz.wings.R
import com.rmtz.wings.data.AppDatabase
import com.rmtz.wings.data.DbProduct
import com.rmtz.wings.data.DbTransactionTemp
import com.rmtz.wings.databinding.FragmentDetailProductBinding

class DetailProductFragment : Fragment() {
    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private var product : DbProduct? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = binding.materialToolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_detail_product_to_product)
        }
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "Wings Database"
        ).allowMainThreadQueries().build()
        try {
            product = database.ProductDao().getProduct(arguments?.getString("id_product")?.toLong()?: 0)[0]
            binding.tvProductName.text = product?.name
            binding.tvPrice.text = String.format(getString(R.string.rp_1), product?.price)
            binding.tvDimension.text = product?.dimension
            binding.tvPriceUnit.text = product?.unit
        }catch (e: Exception) {
            Snackbar.make(binding.root, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
        }
        binding.btnSubmit.setOnClickListener {
            val idTransaction = database.TransactionDao().getTransactionLatestId()+1
            val idTemp = database.TransactionDao().getTempLastId()+1
            val idDetTrans = database.TransactionDao().getDetailTransLatestId()+1
            try {
                database.TransactionDao().insertTemp(
                    DbTransactionTemp(
                        id = idTemp,
                        transactionId = idTransaction,
                        code = "DT$idDetTrans",
                        number = "DT$idDetTrans",
                        productId = product?.id!!,
                        productCode = product?.code!!,
                        price = product?.price!!,
                        quantity = 1,
                        unit = product?.unit!!,
                        subTotal = product?.price!! - product?.discount!!,
                        currency = product?.currency!!
                    )
                )
                findNavController().navigate(R.id.action_detail_product_to_cart)
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}