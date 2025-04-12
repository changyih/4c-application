package com.example.olderperson.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.olderperson.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 联系人数据仓库
 */
class ContactRepository(private val context: Context) {
    private val TAG = "ContactRepository"
    
    /**
     * 获取所有联系人列表
     */
    val contacts: Flow<List<FamilyContact>> = context.dataStore.data
        .catch { exception ->
            Log.e(TAG, "Error reading contacts: ", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            val contactsJson = preferences[ContactHelper.CONTACTS_KEY] ?: ""
            if (contactsJson.isEmpty()) {
                // 如果没有存储的联系人数据，返回默认联系人
                ContactHelper.defaultContacts
            } else {
                // 否则从JSON解析联系人列表
                ContactHelper.contactsFromJson(contactsJson)
            }
        }
    
    /**
     * 保存联系人列表
     */
    suspend fun saveContacts(contacts: List<FamilyContact>) {
        try {
            context.dataStore.edit { preferences ->
                val contactsJson = ContactHelper.contactsToJson(contacts)
                preferences[ContactHelper.CONTACTS_KEY] = contactsJson
            }
            Log.d(TAG, "保存联系人成功，数量: ${contacts.size}")
        } catch (e: Exception) {
            Log.e(TAG, "保存联系人失败: ${e.message}", e)
        }
    }
    
    /**
     * 添加联系人
     */
    suspend fun addContact(contact: FamilyContact) {
        try {
            val currentContacts = getCurrentContacts()
            // 检查是否有重复ID
            if (currentContacts.any { it.id == contact.id }) {
                Log.w(TAG, "联系人ID已存在: ${contact.id}")
                return
            }
            
            val newContacts = currentContacts + contact
            saveContacts(newContacts)
            Log.d(TAG, "添加联系人成功: ${contact.name}")
        } catch (e: Exception) {
            Log.e(TAG, "添加联系人失败: ${e.message}", e)
        }
    }
    
    /**
     * 删除联系人
     */
    suspend fun deleteContact(contactId: String) {
        try {
            val currentContacts = getCurrentContacts()
            val newContacts = currentContacts.filter { it.id != contactId }
            
            if (newContacts.size == currentContacts.size) {
                Log.w(TAG, "未找到要删除的联系人: $contactId")
                return
            }
            
            saveContacts(newContacts)
            Log.d(TAG, "删除联系人成功，ID: $contactId")
        } catch (e: Exception) {
            Log.e(TAG, "删除联系人失败: ${e.message}", e)
        }
    }
    
    /**
     * 更新联系人
     */
    suspend fun updateContact(contact: FamilyContact) {
        try {
            val currentContacts = getCurrentContacts()
            val newContacts = currentContacts.map { 
                if (it.id == contact.id) contact else it 
            }
            
            saveContacts(newContacts)
            Log.d(TAG, "更新联系人成功: ${contact.name}")
        } catch (e: Exception) {
            Log.e(TAG, "更新联系人失败: ${e.message}", e)
        }
    }
    
    /**
     * 获取当前联系人列表
     */
    private suspend fun getCurrentContacts(): List<FamilyContact> {
        return try {
            val preferences = context.dataStore.data.first()
            val contactsJson = preferences[ContactHelper.CONTACTS_KEY] ?: ""
            
            if (contactsJson.isEmpty()) {
                ContactHelper.defaultContacts
            } else {
                ContactHelper.contactsFromJson(contactsJson)
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取当前联系人失败: ${e.message}", e)
            ContactHelper.defaultContacts
        }
    }
} 