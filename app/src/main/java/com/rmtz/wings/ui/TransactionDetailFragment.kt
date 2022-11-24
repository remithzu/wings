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
import com.rmtz.wings.R
import com.rmtz.wings.data.AppDatabase
import com.rmtz.wings.data.DbTransaction
import com.rmtz.wings.data.DbTransactionDetail
import com.rmtz.wings.databinding.FragmentTransactionDetailBinding
import com.rmtz.wings.databinding.ItemTransactionBinding
import com.rmtz.wings.databinding.ItemTransactionDetailBinding

class TransactionDetailFragment : Fragment() {
    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = binding.materialToolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_Transaction_Detail_to_Transaction)
        }
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "Wings Database"
        ).allowMainThreadQueries().build()
        val idTransaction = arguments?.getString("id_transaction")?.toLong()?: 0
        val tr = database.TransactionDao().getTransaction(idTransaction)[0]
        val trDl = database.TransactionDao().getDetailTrans(idTransaction)
        Log.e("wLog", "trDl:: $trDl")
        adapter = Adapter(database)
        binding.rvListTransaction.adapter = adapter
        adapter.list = trDl
        adapter.notifyDataSetChanged()
        binding.tvDate.text = tr.date
        binding.tvDocumentCode.text = tr.code
        binding.tvDocumentNumber.text = tr.number
        binding.tvName.text = tr.user
        binding.tvTotal.text = String.format(getString(R.string.rp_1), tr.total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ViewHolder(val bindItem: ItemTransactionDetailBinding): RecyclerView.ViewHolder(bindItem.root)
    class Adapter(val db: AppDatabase): RecyclerView.Adapter<ViewHolder>() {
        var list = listOf<DbTransactionDetail>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemTransactionDetailBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val m = list[position]
            val product = db.ProductDao().getProduct(m.productId)[0]
            holder.bindItem.tvCode.text = product.code
            holder.bindItem.tvProductName.text = product.name
            holder.bindItem.tvQuantity.text = "${m.quantity}"
            holder.bindItem.tvPrice.text = String.format(holder.itemView.context.getString(R.string.rp_1), product.price)
            holder.bindItem.tvTotal.text = String.format(holder.itemView.context.getString(R.string.rp_1), m.subTotal)
        }

        override fun getItemCount(): Int = list.size
    }
}