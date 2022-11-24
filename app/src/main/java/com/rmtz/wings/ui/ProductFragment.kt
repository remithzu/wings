package com.rmtz.wings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rmtz.wings.MainActivity
import com.rmtz.wings.R
import com.rmtz.wings.data.AppDatabase
import com.rmtz.wings.data.DbProduct
import com.rmtz.wings.data.DbTransactionTemp
import com.rmtz.wings.databinding.FragmentProductBinding
import com.rmtz.wings.databinding.ItemProductBinding

class ProductFragment : Fragment() {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: Adapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "Wings Database"
        ).allowMainThreadQueries().build()
        val toolbar: Toolbar = binding.materialToolbar
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).finish()
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_transaction_history -> {
                    findNavController().navigate(R.id.action_Product_to_Transaction)
                    true
                }
                R.id.action_logout -> {
                    val temp = database.TempDao().getTemp()[0]
                    database.TempDao().deleteTemp(temp)
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    true
                }
                else -> {
                    true
                }
            }
        }

        adapter = Adapter()
        binding.rvListProduct.adapter = adapter

        val product = database.ProductDao().getListProduct()
        Log.e("wLog", "product:: $product")
        val id = database.ProductDao().getLatestId() + 1
        if (product.isEmpty()) {
            try {
                database.ProductDao().insertProduct(
                    DbProduct(
                        id = id,
                        code = "SKP01",
                        name = "Soklin Pewangi",
                        price = 15000,
                        currency = "IDR",
                        discount = 1500,
                        dimension = "170mm x 150mm",
                        unit = "PCS",
                    )
                )
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
            }
        }
        adapter.getAction = object: Adapter.Action {
            override fun onActionAdd(product: DbProduct) {
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
                            productId = product.id,
                            productCode = product.code,
                            price = product.price,
                            quantity = 1,
                            unit = product.unit,
                            subTotal = product.price - product.discount,
                            currency = product.currency
                        ))
                    findNavController().navigate(R.id.action_product_to_cart)
                } catch (e: Exception) {
                    Snackbar.make(binding.root, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onActionGet(product: DbProduct) {
                val direction = ProductFragmentDirections.actionProductToDetailProduct(product.id.toString())
                findNavController().navigate(direction)
            }
        }
        adapter.list = product
        adapter.notifyDataSetChanged()
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_product_to_add_product)
        }
        binding.btnCart.setOnClickListener {
            findNavController().navigate(R.id.action_product_to_cart)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ViewHolder(val bindItem: ItemProductBinding): RecyclerView.ViewHolder(bindItem.root)
    class Adapter: RecyclerView.Adapter<ViewHolder>() {
        var list = listOf<DbProduct>()
        var getAction: Action? = null

        interface Action {
            fun onActionAdd(product: DbProduct)
            fun onActionGet(product: DbProduct)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemProductBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val m = list[position]
            holder.bindItem.ivIcon
            holder.bindItem.tvProductName.text = m.name
            holder.bindItem.tvPrice.text = String.format(holder.itemView.context.getString(R.string.rp_1), m.price)
            if (m.discount!=0L) {
                val disc = m.price - m.discount
                holder.bindItem.tvPriceDisc.visibility = View.VISIBLE
                holder.bindItem.tvPriceDisc.text = String.format(holder.itemView.context.getString(R.string.rp_1), disc)
                holder.bindItem.tvPrice.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.pomegranate))
            } else {
                holder.bindItem.tvPriceDisc.visibility = View.GONE
            }
            holder.bindItem.btnAdd.setOnClickListener {
                getAction?.onActionAdd(m)
            }

            holder.itemView.setOnClickListener {
                getAction?.onActionGet(m)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}

