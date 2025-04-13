# 集成对话界面

为了集成新创建的`FamilyCareScreen`到应用中，请按照以下步骤修改`MainActivity.kt`文件：

## 1. 添加导入语句

在`MainActivity.kt`文件的导入部分添加以下语句：

```kotlin
import com.example.olderperson.ui.screens.FamilyCareScreen
```

## 2. 修改导航部分

找到`MainActivity.kt`中处理导航的部分（大约在第200行附近），将原来的`MessageScreen`替换为`FamilyCareScreen`：

原代码：
```kotlin
NavSection.MESSAGE -> MessageScreen(
    onBackToHome = { currentSection = NavSection.HOME }
)
```

修改为：
```kotlin
NavSection.MESSAGE -> FamilyCareScreen(
    onBackToHome = { currentSection = NavSection.HOME }
)
```

## 3. 修改底部导航栏（可选）

如果需要，可以修改底部导航栏中对应的文本，将"消息"改为"对话"：

找到`BottomNavigationBar`组件，修改相关文本。

## 4. 重新编译应用

完成上述修改后，重新编译应用并运行。现在，当用户点击底部导航栏中的消息图标时，将显示新的对话界面。

## 注意事项

- 新的`FamilyCareScreen`界面专为老人子女设计，更加关注老人的健康状态和日常生活
- 界面中的数据目前是模拟数据，实际应用中需要从后端或本地数据库获取
- 如果遇到任何编译错误，请检查导入语句和组件名称是否正确 