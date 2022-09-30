package com.example.roomexample

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomexample.databinding.DialogUpdateBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_add.setOnClickListener(this@MainActivity)
        val employeeDao  = (application as EmployeeApp).db.employeeDao()
        lifecycleScope.launch{
            employeeDao.getEmployees().collect{
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, employeeDao)
            }
        }

    }


    override fun onClick(view: View?) {
        val employeeDao  = (application as EmployeeApp).db.employeeDao()
        when(view!!.id){
            R.id.btn_add -> {
                addRecord(employeeDao)
            }
        }
    }

    fun addRecord(employeeDao: EmployeeDao){
        val name = etName.text.toString()
        val email = etEmail.text.toString()

        if(name.isNotEmpty() && email.isNotEmpty()){
            //CONTEXT
            lifecycleScope.launch{
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext, "Record Saved", Toast.LENGTH_LONG).show()
                etEmail.setText("")
                etName.setText("")
            }
        }else{
            Toast.makeText(applicationContext, "Name and Email must be provided", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListOfDataIntoRecyclerView(employees: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao){
            if(employees.isNotEmpty()){
                val itemAdapter = ItemAdapter(employees,{
                    updatedId ->
                    updateRecordDialog(updatedId , employeeDao)
                },{
                    deleteId ->
                    deleteRecordDialog(deleteId, employeeDao)
                })
                rvItemsList.layoutManager = LinearLayoutManager(this);
                rvItemsList.adapter = itemAdapter
                rvItemsList.visibility = View.VISIBLE
                list_title_no_records.visibility = View.GONE
            }else{
                rvItemsList.visibility = View.GONE
                list_title_no_records.visibility = View.VISIBLE
            }
    }

    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao){
        val updateDialog = Dialog(this@MainActivity, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch{
            employeeDao.getEmployeeById(id).collect{
                if(it != null){
                    binding.etName.setText(it.name)
                    binding.etEmail.setText(it.email)
                }
            }
        }

        binding.tvUpdate.setOnClickListener{
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty()){
                lifecycleScope.launch{
                    employeeDao.update(EmployeeEntity(id = id, name = name, email = email))
                    Toast.makeText(this@MainActivity, "Employee updated", Toast.LENGTH_LONG).show()
                    updateDialog.dismiss()
                }
            }else{
                Toast.makeText(this@MainActivity, "Name or Email can't be blank", Toast.LENGTH_LONG).show()
            }
        }

        binding.tvCancel.setOnClickListener{
            updateDialog.dismiss()
        }

        updateDialog.show()
    }

    private fun deleteRecordDialog(id: Int, employeeDao: EmployeeDao){
        var builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Delete Record")
        builder.setPositiveButton("Yes"){
            dialog, _ ->
            lifecycleScope.launch{
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(this@MainActivity, "Record deleted", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }.setNegativeButton("Cancel"){
                dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}