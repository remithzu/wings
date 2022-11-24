package com.rmtz.wings.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rmtz.wings.R
import com.rmtz.wings.commons.Utils.getDate
import com.rmtz.wings.data.*
import com.rmtz.wings.databinding.FragmentCheckoutBinding
import com.rmtz.wings.databinding.ItemCartBinding

class CheckoutFragment : Fragment() {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: Adapter
    private lateinit var database: AppDatabase
    private var tr = arrayListOf<DbTransactionDetail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = binding.materialToolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_Cart_to_Product)
        }
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "Wings Database"
        ).allowMainThreadQueries().build()
        val idTransaction = database.TransactionDao().getTransactionLatestId()+1
        var idDetTrans = database.TransactionDao().getDetailTransLatestId()
        val user = database.TempDao().getTemp()[0]
        adapter = Adapter(database)
        binding.rvListCart.adapter = adapter
        adapter.getAction = object: Adapter.Action {
            override fun onActionAdd(dbTemp: DbProduct) {
                try {
                    val id = database.TransactionDao().getTempLastId()+1
                    database.TransactionDao().insertTemp(DbTransactionTemp(
                        id = id,
                        transactionId = idTransaction,
                        code = "DT$idDetTrans",
                        number = "DT$idDetTrans",
                        productId = dbTemp.id,
                        productCode = dbTemp.code,
                        price = dbTemp.price,
                        quantity = 1,
                        unit = dbTemp.unit,
                        subTotal = dbTemp.price - dbTemp.discount,
                        currency = dbTemp.currency
                    ))
                    calculate()
                } catch (e: Exception) {
                    Log.e("wLog", "E:: $e")
                    Snackbar.make(binding.root, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
                    calculate()
                }
            }

            override fun onActionRemove(dbTemp: DbProduct) {
                try {
                    val t = database.TransactionDao().getLastItem(dbTemp.id)
                    database.TransactionDao().deleteTemp(t)
                    calculate()
                } catch (e: Exception) {
                    Snackbar.make(binding.root, "Error while processing to DB", Snackbar.LENGTH_LONG).show()
                    calculate()
                }
            }
        }
        binding.btnSubmit.setOnClickListener {
            try {
                var total: Long = 0
                tr.forEach {
                    total += it.subTotal
                    idDetTrans += 1
                    database.TransactionDao().insertDetailTrans(
                        DbTransactionDetail(
                            id = idDetTrans,
                            transactionId = idTransaction,
                            code = "DT$idDetTrans",
                            number = "DT$idDetTrans",
                            productId = it.productId,
                            productCode = it.productCode,
                            price = it.price,
                            quantity = it.quantity,
                            unit = it.unit,
                            subTotal = it.subTotal,
                            currency = it.currency
                        )
                    )
                }
                database.TransactionDao().insertTransaction(
                    DbTransaction(
                        id = idTransaction,
                        code = "T$idTransaction",
                        number = "T$idTransaction",
                        user = user.name,
                        total = total,
                        date = requireContext().getDate()
                    )
                )

                database.TransactionDao().deleteTempAll()
                calculate()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Error while processing to DB", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_Cart_to_Product)
        }
        calculate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun calculate() {
        tr = arrayListOf()
        val idTransaction = database.TransactionDao().getTransactionLatestId()+1
        var idDetTrans = database.TransactionDao().getDetailTransLatestId()
        val dbDtlT = arrayListOf<DbTransactionDetail>()
        val tempTr = database.TransactionDao().getListTemp()
        val tempTrBy = tempTr.groupBy {
            it.productId
        }
        tempTrBy.forEach { t ->
            var productId = 0L
            var productCode = ""
            var price = 0L
            var quantity = 0
            var unit = ""
            var subTotal = 0L
            var currency = ""
            idDetTrans += 1
            t.value.forEach {
                productId = it.productId
                productCode = it.productCode
                price = it.price
                quantity += it.quantity
                unit = it.unit
                subTotal = it.subTotal
                currency = it.currency
            }
            dbDtlT.add(DbTransactionDetail(
                idDetTrans,
                transactionId = idTransaction,
                code = "DT$idDetTrans",
                number = "DT$idDetTrans",
                productId = productId,
                productCode = productCode,
                price = price,
                quantity = quantity,
                unit = unit,
                subTotal = subTotal * quantity,
                currency = currency
            ))
        }
        adapter.list = dbDtlT
        adapter.notifyDataSetChanged()

        var total: Long = 0
        if (dbDtlT.size!=0) {
            dbDtlT.forEach {
                total += it.subTotal
            }
        } else {
            total = 0
        }
        binding.tvTotalValue.text = String.format(getString(R.string.rp_1), total)
        tr = dbDtlT

        binding.btnSubmit.isEnabled = dbDtlT.size!=0
    }

    class ViewHolder(val bindItem: ItemCartBinding): RecyclerView.ViewHolder(bindItem.root)
    class Adapter(val db: AppDatabase): RecyclerView.Adapter<ViewHolder>() {
        var list = listOf<DbTransactionDetail>()
        var getAction: Action? = null

        interface Action {
            fun onActionAdd(dbTemp: DbProduct)
            fun onActionRemove(dbTemp: DbProduct)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemCartBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val m = list[position]
            val product = db.ProductDao().getProduct(m.productId)[0]
            val temp = db.TransactionDao().getProduct(m.productId)
            var subTotal:Long = 0
            temp.forEach{
                subTotal += it.subTotal
            }
            holder.bindItem.ivIcon
            holder.bindItem.tvProductName.text = product.name
            holder.bindItem.tvUnit.text = product.unit
            holder.bindItem.tvSubTotal.text = String.format(holder.itemView.context.getString(R.string.rp_1), subTotal)
            holder.bindItem.tieQuantity.setText("${m.quantity}")
            holder.bindItem.btnAdd.setOnClickListener{
                getAction?.onActionAdd(product)
            }
            holder.bindItem.btnMin.setOnClickListener{
                getAction?.onActionRemove(product)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}