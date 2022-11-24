package com.rmtz.wings.ui

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.rmtz.wings.databinding.FragmentTransactionBinding
import com.rmtz.wings.databinding.ItemTransactionBinding

class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = binding.materialToolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_Transaction_to_Product)
        }
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "Wings Database"
        ).allowMainThreadQueries().build()
        val tr = database.TransactionDao().getListTransaction()
        adapter = Adapter()
        adapter.getAction = object: Adapter.Action{
            override fun onAction(db: DbTransaction) {
                val directions = TransactionFragmentDirections.actionTransactionToTransactionDetail(db.id.toString())
                findNavController().navigate(directions)
            }
        }
        binding.rvListTransaction.adapter = adapter
        adapter.list = tr
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ViewHolder(val bindItem: ItemTransactionBinding): RecyclerView.ViewHolder(bindItem.root)
    class Adapter: RecyclerView.Adapter<ViewHolder>() {
        var list = listOf<DbTransaction>()
        var getAction: Action? = null

        interface Action {
            fun onAction(db: DbTransaction)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemTransactionBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val m = list[position]
            holder.bindItem.tvDate.text = m.date
            holder.bindItem.tvCode.text = m.code
            holder.bindItem.tvTotal.text = String.format(holder.itemView.context.getString(R.string.rp_1), m.total)
            holder.itemView.setOnClickListener {
                getAction?.onAction(m)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}