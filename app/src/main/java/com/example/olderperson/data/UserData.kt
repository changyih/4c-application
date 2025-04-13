package com.example.olderperson.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 用户角色
enum class UserRole {
    ELDER, // 老年人角色 - 使用呵护模式
    FAMILY // 家人角色 - 使用关爱模式
}

// 用户数据模型
data class User(
    val id: String,
    val phoneNumber: String,
    val password: String,
    val name: String,
    val role: UserRole,
    val location: String,
    val avatar: String? = null,
    val relation: String? = null, // 与老人的关系，如"儿子"等
    val healthPlans: Int = 0,
    val serviceOrders: Int = 0,
    val devices: Int = 0
)

// 用户数据存储
val Context.userDataStore by preferencesDataStore(name = "user_prefs")
private val USER_ID_KEY = stringPreferencesKey("current_user_id")
private val REMEMBER_LOGIN_KEY = booleanPreferencesKey("remember_login")

object UserManager {
    // 预设账号数据
    private val users = mutableListOf(
        // 父亲李长青 - 呵护模式用户
        User(
            id = "user1",
            phoneNumber = "13800000001",
            password = "123456",
            name = "李长青",
            role = UserRole.ELDER,
            location = "吉林 长春",
            healthPlans = 3,
            serviceOrders = 1,
            devices = 3
        ),
        
        // 儿子李明远 - 关爱模式用户

        User(
            id = "user2",
            phoneNumber = "13800000002",
            password = "123456",
            name = "李明远",
            role = UserRole.FAMILY,
            location = "四川 成都",
            relation = "儿子"
        )
    )
    
    // 当前登录用户ID
    private var currentUserId: String? = null
    
    // 是否记住登录状态
    private var rememberLogin: Boolean = false
    
    // 获取用户列表
    fun getUsers(): List<User> = users.toList()
    
    // 添加用户
    fun addUser(user: User) {
        users.add(user)
    }
    
    // 通过电话和密码验证用户
    fun authenticateUser(phoneNumber: String, password: String): User? {
        return users.find { it.phoneNumber == phoneNumber && it.password == password }
    }
    
    // 通过ID获取用户
    fun getUserById(userId: String): User? {
        return users.find { it.id == userId }
    }
    
    // 设置当前用户ID
    fun setCurrentUserId(userId: String) {
        currentUserId = userId
    }
    
    // 获取当前用户
    fun getCurrentUser(): User? {
        return currentUserId?.let { getUserById(it) }
    }
    
    // 设置是否记住登录状态
    fun setRememberLogin(remember: Boolean) {
        rememberLogin = remember
    }
    
    // 获取是否记住登录状态
    fun getRememberLogin(): Boolean {
        return rememberLogin
    }
    
    // 保存登录状态
    suspend fun saveLoginState(context: Context, userId: String, remember: Boolean) {
        context.userDataStore.edit { preferences ->
            if (remember) {
                preferences[USER_ID_KEY] = userId
            } else {
                preferences.remove(USER_ID_KEY)
            }
            preferences[REMEMBER_LOGIN_KEY] = remember
        }
        setCurrentUserId(userId)
        setRememberLogin(remember)
    }
    
    // 从DataStore加载用户ID
    suspend fun loadLoginState(context: Context): Pair<String?, Boolean> {
        val data = context.userDataStore.data.first()
        val remember = data[REMEMBER_LOGIN_KEY] ?: false
        
        // 只有在记住登录状态时才读取用户ID
        val userId = if (remember) data[USER_ID_KEY] else null
        
        userId?.let { setCurrentUserId(it) }
        setRememberLogin(remember)
        
        return Pair(userId, remember)
    }
    
    // 清除当前用户登录状态
    suspend fun clearCurrentUser(context: Context) {
        context.userDataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences[REMEMBER_LOGIN_KEY] = false
        }
        currentUserId = null
        rememberLogin = false
    }
} 