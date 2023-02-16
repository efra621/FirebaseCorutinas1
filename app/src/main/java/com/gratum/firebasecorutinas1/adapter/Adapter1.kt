package com.gratum.firebasecorutinas1.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gratum.firebasecorutinas1.R
import com.gratum.firebasecorutinas1.databinding.ItemMemberBinding
import com.gratum.firebasecorutinas1.model.MemberModel
import com.squareup.picasso.Picasso

//class UserAdapter(val userList: List<Usuario>): RecyclerView.Adapter<UserAdapter.UserViewHolder>()
class Adapter1(private val onLoadMore: () -> Unit) :
    RecyclerView.Adapter<Adapter1.MemberViewHolder>() {

    private val list = mutableListOf<MemberModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return MemberViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.render(list[position])

        //when scroll to bottom, load more data
        if (position == list.size - 1) {
            onLoadMore()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    //View holder encargado de pintar el item del recyclerView
    class MemberViewHolder(val item_view: View) : RecyclerView.ViewHolder(item_view) {

        private val memberModel = null

        val binding = ItemMemberBinding.bind(item_view)

        fun render(memberModel: MemberModel) {

            binding.textView.text = memberModel.id
            binding.textView2.text = memberModel.email
            binding.textView3.text = memberModel.nickname

//            Picasso.get()
//                .load(memberModel.profileImageUrl)
//                .into(binding.imageView3)
/*
            item_view.setOnClickListener {
                Toast.makeText(
                    item_view.context,
                    "Has seleccionado a ${userList.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

 */

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reload(list: List<MemberModel>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun loadMore(list: List<MemberModel>){
        this.list.addAll(list)
        notifyItemRangeChanged(this.list.size - list.size+1, list.size)
    }
}

